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
package org.openmrs.module.mirebalais;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;

public class RuntimeProperties {
	
	public static final String REMOTE_ZL_IDENTIFIER_SOURCE_URL_PROPERTY = "remote_zlidentifier_url";
	
	public static final String REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD_PROPERTY = "remote_zlidentifier_password";
	
	public static final String REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME_PROPERTY = "remote_zlidentifier_username";

    public static final String LACOLLINE_SERVER_URL_PROPERTY = "lacolline_server_url";

    public static final String LACOLLINE_USERNAME_PROPERTY = "lacolline_username";

    public static final String LACOLLINE_PASSWORD_PROPERTY = "lacolline_password";

    private Log log = LogFactory.getLog(getClass());

	public String getRemoteZlIdentifierSourceUsername() {
        String property = Context.getRuntimeProperties().getProperty(REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME_PROPERTY);
        return property != null ? property : MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME;
    }
	
	public String getRemoteZlIdentifierSourcePassword() {
        String property = Context.getRuntimeProperties().getProperty(REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD_PROPERTY);
        return property != null ? property : MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD;
	}
	
	public String getRemoteZlIdentifierSourceUrl() {
        String property = Context.getRuntimeProperties().getProperty(REMOTE_ZL_IDENTIFIER_SOURCE_URL_PROPERTY);
        return property != null ? property : MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_URL;
	}

    public String getLacollineServerUrl() {
        String property = Context.getRuntimeProperties().getProperty(LACOLLINE_SERVER_URL_PROPERTY);
        return property != null ? property : "lacolline-test";  // for MirebalaisHospitalActivatorITTest
    }

    public String getLacollineUsername() {
        String property = Context.getRuntimeProperties().getProperty(LACOLLINE_USERNAME_PROPERTY);
        return property != null ? property : "lacolline-test";
    }

    public String getLacollinePassword() {
        String property = Context.getRuntimeProperties().getProperty(LACOLLINE_PASSWORD_PROPERTY);
        return property != null ? property : "lacolline-test";
    }

}
