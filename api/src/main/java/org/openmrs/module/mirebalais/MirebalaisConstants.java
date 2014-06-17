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

    public static final String EXTERNAL_DOSSIER_NUMBER_IDENTIFIER_TYPE_UUID = "9dbea4d4-35a9-4793-959e-952f2a9f5347";
	
	public static final String LOCAL_ZL_IDENTIFIER_POOL_UUID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
	
	public static final String REMOTE_ZL_IDENTIFIER_SOURCE_UUID = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";
	
	public static final String DOSSIER_NUMBER_ZL_IDENTIFIER_SOURCE_UUID = "cccccccc-cccc-cccc-cccc-cccccccccccc";
	
	public static final String REMOTE_ZL_IDENTIFIER_SOURCE_URL = "http://localhost:8080/mirebalais/module/idgen/exportIdentifiers.form?source=3&comment=Testing+Mirebalais";
	
	public static final int LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE = 500;
	
	public static final int LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE = 1000;
	
	public static String REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME = "testidgen";
	
	public static String REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD = "Testing123";

    public static final String TELEPHONE_NUMBER_ATTRIBUTE_TYPE_UUID = "14d4f066-15f5-102d-96e4-000c29c2a5d7";

    public static final String LACOLLINE_LOCATION_UUID = "23e7bb0d-51f9-4d5f-b34b-2fbbfeea1960";

    public static final String CENTRAL_ARCHIVES_LOCATION_UUID = "be50d584-26b2-4371-8768-2b9565742b3b";

    public static final String MPI_REMOTE_SERVER = "lacolline";
    public static final String MPI_SEARCH_RESULTS="mpiSearchResults";
    public static final Integer MPI_CONNECT_TIMEOUT = 60000;

    public static final String TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED = "Mirebalais - Mark Appointments Missed or Completed";

    public static final String TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_DESCRIPTION = "Marks appointments in the past as missed or completed based on Mirebalais business logic";

    public static final Long TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_REPEAT_INTERVAL = new Long(14400); // every four hours

}
