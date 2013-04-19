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
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.utils.FeatureToggleProperties;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

/**
 * Home page for Mirebalais EMR (shows list of apps) Shows the login view instead if you are not
 * authenticated.
 */
public class HomePageController {
	
	public static final String MY_ACCOUNT_APP_ID = "emr.myAccount";
	
	public void controller(PageModel model, EmrContext emrContext,
	                       @SpringBean("featureToggles") FeatureToggleProperties featureToggleProperties) {
		
		emrContext.requireAuthentication();
		
		AppFrameworkService appService = Context.getService(AppFrameworkService.class);
		List<AppDescriptor> apps = appService.getAppsForUser(Context.getAuthenticatedUser());
		
		for (int i = 0; i < apps.size(); i++) {
			AppDescriptor appDescriptor = apps.get(i);
			
			if (appDescriptor.getId().equals(MY_ACCOUNT_APP_ID)) {
				if (!featureToggleProperties.isFeatureEnabled("myAccountFeature")) {
					apps.remove(i);
				}
			}
			
		}
		
		model.addAttribute("apps", apps);
	}
	
}
