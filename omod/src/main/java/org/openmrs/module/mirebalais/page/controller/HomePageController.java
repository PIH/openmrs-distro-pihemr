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

import java.util.Collections;
import java.util.List;

import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

/**
 * Home page for Mirebalais EMR (shows list of apps) Shows the login view instead if you are not
 * authenticated.
 */
public class HomePageController {
	
	public static final String MY_ACCOUNT_EXTENSION_ID = "emr.myAccount";
	
	public static final String HOME_PAGE_EXTENSION_POINT = "org.openmrs.referenceapplication.homepageLink";
	
	public void controller(PageModel model, EmrContext emrContext,
	                       @SpringBean("featureToggles") FeatureToggleProperties featureToggleProperties,
	                       @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService) {
		
		emrContext.requireAuthentication();
		
		List<Extension> extensions = appFrameworkService.getExtensionsForCurrentUser(HOME_PAGE_EXTENSION_POINT);
		
		for (int i = 0; i < extensions.size(); i++) {
			Extension extension = extensions.get(i);

            if (extensionFeatureToggledOff(extension, featureToggleProperties)) {
				extensions.remove(i);
				i--;
			}
			
		}
		
		Collections.sort(extensions);
		model.addAttribute("extensions", extensions);
	}

    private boolean extensionFeatureToggledOff(Extension extension, FeatureToggleProperties featureToggleProperties) {
        if (extension.getExtensionParams() == null) {
            return false;
        }
        String featureToggle = (String) extension.getExtensionParams().get("featureToggle");
        return featureToggle != null && !featureToggleProperties.isFeatureEnabled(featureToggle);
	}
	
}
