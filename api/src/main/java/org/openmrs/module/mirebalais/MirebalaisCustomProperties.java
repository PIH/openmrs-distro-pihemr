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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD;
import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_URL;
import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME;

public class MirebalaisCustomProperties {

    private Properties properties;

    public MirebalaisCustomProperties() {
        properties = new Properties();
        createFile();
    }

    public String getRemoteZlIdentifierSourceUsername() {
        return properties.getProperty("remote_zlidentifier_username", REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME);
    }

    public String getRemoteZlIdentifierSourcePassword() {
        return properties.getProperty("remote_zlidentifier_password", REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD);
    }


    public String getRemoteZlIdentifierSourceUrl() {
        return properties.getProperty("remote_zlidentifier_url", REMOTE_ZL_IDENTIFIER_SOURCE_URL);
    }

    private Properties createFile() {
        String propertiesFile = System.getenv("mirebalais_custom_properties_file");

        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

}
