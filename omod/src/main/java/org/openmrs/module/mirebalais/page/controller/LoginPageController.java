/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.mirebalais.page.controller;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.api.db.ContextDAO;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.utils.GeneralUtils;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class LoginPageController {

    //see TRUNK-4536 for details why we need this
    private static final String GET_LOCATIONS = "Get Locations";
     // RA-592: don't use PrivilegeConstants.VIEW_LOCATIONS
    private static final String VIEW_LOCATIONS = "View Locations";

	public String get(
	        PageModel pageModel,
			@SpringBean EmrApiProperties emrApiProperties,
            @SpringBean Config config,
	        @CookieValue(value = EmrConstants.COOKIE_NAME_LAST_SESSION_LOCATION, required = false) String lastSessionLocationId,
	        @SpringBean("locationService") LocationService locationService, EmrContext context, UiUtils ui,
	        PageRequest request) {
		
		if (context.isAuthenticated()) {
			return "redirect:" + ui.pageLink("mirebalais", "home");
		}

        pageModel.put("welcomeMessage", config.getWelcomeMessage());

		// Since the user isn't authenticated, we need to use proxy privileges to get locations via the API
		// TODO consider letting the Anonymous role have the Get Location privilege instead of using proxy privileges
		try {
			Context.addProxyPrivilege(GET_LOCATIONS);
            Context.addProxyPrivilege(VIEW_LOCATIONS);

            List<Location> loginLocations = getLoginLocations(locationService, emrApiProperties);

			pageModel.addAttribute("locations", loginLocations);
			Location lastSessionLocation = null;
			try {
				lastSessionLocation = locationService.getLocation(Integer.valueOf(lastSessionLocationId));
			}
			catch (Exception ex) {
				// pass
			}

            // double-check that the last session location is still a valid login location (and null if not)
            if (lastSessionLocation == null || !(loginLocations.contains(lastSessionLocation))) {
                lastSessionLocation = null;
            }

            pageModel.addAttribute("lastSessionLocation", lastSessionLocation);
		}
		finally {
			Context.removeProxyPrivilege(GET_LOCATIONS);
            Context.removeProxyPrivilege(VIEW_LOCATIONS);
		}
        return null;
	}
	
	public String post(@RequestParam(value = "username", required = false) String username,
	        @RequestParam(value = "password", required = false) String password,
	        @RequestParam(value = "sessionLocation", required = false) Integer sessionLocationId,
	        @SpringBean ContextDAO contextDao, @SpringBean("locationService") LocationService locationService, UiUtils ui,
	        EmrContext context, PageRequest request) {
		
		HttpSession httpSession = request.getRequest().getSession();
		Location sessionLocation = null;
		try {
			// TODO as above, grant this privilege to Anonymous instead of using a proxy privilege
			Context.addProxyPrivilege(PrivilegeConstants.GET_LOCATIONS);
			
			if (sessionLocationId != null) {
				sessionLocation = locationService.getLocation(sessionLocationId);
				if (!sessionLocation.hasTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_LOGIN)) {
					// the UI shouldn't allow this, but protect against it just in case
					httpSession.setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, ui
					        .message("mirebalais.login.error.invalidLocation"));
					return "redirect:" + ui.pageLink("mirebalais", "login");
				}
			}
			if (sessionLocation == null) {
				httpSession.setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, ui
				        .message("mirebalais.login.error.locationRequired"));
				return "redirect:" + ui.pageLink("mirebalais", "login");
			}
			// Set a cookie, so next time someone logs in on this machine, we can default to that same location
			request.setCookieValue(EmrConstants.COOKIE_NAME_LAST_SESSION_LOCATION, sessionLocationId.toString());
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.GET_LOCATIONS);
		}
		
		try {
			context.getUserContext().authenticate(username, password, contextDao);
			context.setSessionLocation(sessionLocation);
			
			// set the locale based on the user's default locale
			Locale userLocale = GeneralUtils.getDefaultLocale(context.getUserContext().getAuthenticatedUser());
			if (userLocale != null) {
				context.getUserContext().setLocale(userLocale);
				
				// these have been taken from the core login servlet, not sure if they are necessary
				request.getResponse().setLocale(userLocale);
				new CookieLocaleResolver().setDefaultLocale(userLocale);
			}
			
			return "redirect:" + ui.pageLink("mirebalais", "home");
		}
		catch (ContextAuthenticationException ex) {
			httpSession.setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, ui
			        .message("mirebalais.login.error.authentication"));
			return "redirect:" + ui.pageLink("mirebalais", "login");
		}
	}

	private List<Location> getLoginLocations(LocationService locationService, EmrApiProperties emrApiProperties) {
		List<Location> locations = locationService.getLocationsByTag(emrApiProperties.getSupportsLoginLocationTag());
		if (locations.size() == 0) {
			locations = locationService.getAllLocations(false);
		}
		return locations;
	}
	
}
