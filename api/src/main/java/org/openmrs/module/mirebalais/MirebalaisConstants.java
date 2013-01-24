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

/**
 *
 */
public class MirebalaisConstants {
	
	public static final String MIREBALAIS_MODULE_ID = "mirebalais";
	
	public static final String MIREBALAIS_LOCATION_UUID = "a084f714-a536-473b-94e6-ec317b152b43";
	
	public static final String RADIOLOGY_ORDERABLE_CONCEPTS_GP = "mirebalais.radiology.orderableConcepts";
	
	public static final String RADIOLOGY_ORDERTYPE_GP = "radiology-order-type";
	
	public static final String ZL_IDENTIFIER_TYPE_UUID = "a541af1e-105c-40bf-b345-ba1fd6a59b85";
	
	public static final String DOSSIER_NUMBER_IDENTIFIER_TYPE_UUID = "e66645eb-03a8-4991-b4ce-e87318e37566";
	
	public static final String LOCAL_ZL_IDENTIFIER_POOL_UUID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
	
	public static final String REMOTE_ZL_IDENTIFIER_SOURCE_UUID = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";
	
	public static final String DOSSIER_NUMBER_ZL_IDENTIFIER_SOURCE_UUID = "cccccccc-cccc-cccc-cccc-cccccccccccc";
	
	public static final String REMOTE_ZL_IDENTIFIER_SOURCE_URL = "http://localhost:8080/mirebalais/module/idgen/exportIdentifiers.form?source=3&comment=Testing+Mirebalais";
	
	public static final int LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE = 500;
	
	public static final int LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE = 1000;
	
	public static String REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME = "testidgen";
	
	public static String REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD = "Testing123";
	
	public static String DEFAULT_MIRTH_MYSQL_USERNAME = "test";
	
	public static String DEFAULT_MIRTH_MYSQL_PASSWORD = "test123";

    public static String DEFAULT_MIRTH_MYSQL_DATABASE = "openmrs";

    public static String DEFAULT_PACS_IP_ADDRESS = "127.0.0.1";

    public static String DEFAULT_PACS_DESTINATION_PORT = "6660";
}
