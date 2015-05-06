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
import org.openmrs.module.pihcore.identifier.haiti.ConfigureHaitiIdGenerators;

public class RuntimeProperties {

    public static final String LACOLLINE_SERVER_URL_PROPERTY = "lacolline_server_url";
    public static final String LACOLLINE_USERNAME_PROPERTY = "lacolline_username";
    public static final String LACOLLINE_PASSWORD_PROPERTY = "lacolline_password";

    private Log log = LogFactory.getLog(getClass());



    /**
     * @return the prefix which should be used by the local zl identifier generator, if it is enabled.
     * The system will always create zl identifiers of length 6, one of which is a check-digit.  So
     * if this prefix is empty, the first identifier base will be 10000, if the prefix is a single character,
     * the first identifier base will be 1000, if the prefix is two characters, the first identifier base will be 100, etc
     */
    public String getLocalZlIdentifierGeneratorPrefix() {
        String property = Context.getRuntimeProperties().getProperty(ConfigureHaitiIdGenerators.LOCAL_ZL_IDENTIFIER_GENERATOR_PREFIX);
        return property != null ? property : "";
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
