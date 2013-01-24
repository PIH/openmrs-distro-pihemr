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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD;
import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_URL;
import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME;

public class MirebalaisCustomProperties {
	
	public static final String MIREBALAIS_CUSTOM_PROPERTIES_FILE = "MIREBALAIS_CUSTOM_PROPERTIES_FILE";
	
	public static final String REMOTE_ZLIDENTIFIER_URL = "remote_zlidentifier_url";
	
	public static final String REMOTE_ZLIDENTIFIER_PASSWORD = "remote_zlidentifier_password";
	
	public static final String REMOTE_ZLIDENTIFIER_USERNAME = "remote_zlidentifier_username";
	
	public static final String MIRTH_MYSQL_USERNAME = "mirth_mysql_username";
	
	public static final String MIRTH_MYSQL_PASSWORD = "mirth_mysql_password";

    public static final String MIRTH_MYSQL_DATABASE = "mirth_mysql_database";

    public static final String PACS_IP_ADDRESS = "pacs_ip_address";

    public static final String PACS_DESTINATION_PORT= "pacs_destination_port";

	
	private Log log = LogFactory.getLog(getClass());
	
	private Properties properties;
	
	public MirebalaisCustomProperties() {
		properties = new Properties();
		createFile();
	}
	
	public String getRemoteZlIdentifierSourceUsername() {
		return properties.getProperty(REMOTE_ZLIDENTIFIER_USERNAME, REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME);
	}
	
	public String getRemoteZlIdentifierSourcePassword() {
		return properties.getProperty(REMOTE_ZLIDENTIFIER_PASSWORD, REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD);
	}
	
	public String getRemoteZlIdentifierSourceUrl() {
		return properties.getProperty(REMOTE_ZLIDENTIFIER_URL, REMOTE_ZL_IDENTIFIER_SOURCE_URL);
	}
	
	public String getMirthMysqlUsername() {
		return properties.getProperty(MIRTH_MYSQL_USERNAME, MirebalaisConstants.DEFAULT_MIRTH_MYSQL_USERNAME);
	}
	
	public String getMirthMysqlPassword() {
		return properties.getProperty(MIRTH_MYSQL_PASSWORD, MirebalaisConstants.DEFAULT_MIRTH_MYSQL_PASSWORD);
	}

    public String getMirthMysqlDatabase() {
        return properties.getProperty(MIRTH_MYSQL_DATABASE, MirebalaisConstants.DEFAULT_MIRTH_MYSQL_DATABASE);
    }

    public String getPacsIpAddress() {
        return properties.getProperty(PACS_IP_ADDRESS, MirebalaisConstants.DEFAULT_PACS_IP_ADDRESS);
    }

    public String getPacsDestinationPort() {
        return properties.getProperty(PACS_DESTINATION_PORT, MirebalaisConstants.DEFAULT_PACS_DESTINATION_PORT);
    }


	private Properties createFile() {
		String propertiesFile = System.getenv(MIREBALAIS_CUSTOM_PROPERTIES_FILE);
		
		try {
			if (propertiesFile != null) {
				properties.load(new FileInputStream(propertiesFile));
			}
		}
		catch (IOException e) {
			log.error("File mirebalais_custom_properties_file not found. Error: ", e);
		}
		return properties;
	}
	
}
