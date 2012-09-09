/**
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

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppDescriptor;
import org.openmrs.module.appframework.api.AppFrameworkService;
import org.openmrs.ui.framework.page.PageModel;


/**
 * Home page for Mirebalais EMR (shows list of apps)
 * Shows the login view instead if you are not authenticated.
 */
public class HomePageController {
	
	public String controller(PageModel model) {
		// you need to be authenticated or else we show you the login view instead
		if (!Context.isAuthenticated()) {
			return "login";
		}
		
		AppFrameworkService appService = Context.getService(AppFrameworkService.class);
		List<AppDescriptor> apps = appService.getAppsForUser(Context.getAuthenticatedUser());
		model.addAttribute("apps", apps);
		return null;
	}
	
}
