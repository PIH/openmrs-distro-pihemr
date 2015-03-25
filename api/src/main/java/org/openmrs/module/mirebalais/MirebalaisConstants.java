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
	
	public static final String RADIOLOGY_ORDERABLE_CONCEPTS_GP = "mirebalais.radiology.orderableConcepts";
	
	public static final String RADIOLOGY_ORDERTYPE_GP = "radiology-order-type";

    public static final String LOCAL_ZL_IDENTIFIER_GENERATOR_UUID = "52250ca2-d25f-11e4-8dbf-54ee7513a7ff";
	
	public static final String LOCAL_ZL_IDENTIFIER_POOL_UUID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
	
	public static final String REMOTE_ZL_IDENTIFIER_SOURCE_UUID = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";
	
	public static final String UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID = "cccccccc-cccc-cccc-cccc-cccccccccccc";

    public static final String UHM_DOSSIER_NUMBER_PREFIX = "A";

    public static final String CDI_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID = "fac39940-9c35-11e4-bd06-0800200c9a66";

    public static final String CDI_DOSSIER_NUMBER_PREFIX = "CDI";
	
	public static final String REMOTE_ZL_IDENTIFIER_SOURCE_URL = "http://localhost:8080/mirebalais/module/idgen/exportIdentifiers.form?source=3&comment=Testing+Mirebalais";
	
	public static final int LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE = 500;
	
	public static final int LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE = 1000;
	
	public static String REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME = "testidgen";
	
	public static String REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD = "Testing123";

    public static final String LACOLLINE_LOCATION_UUID = "23e7bb0d-51f9-4d5f-b34b-2fbbfeea1960";

    public static final String MPI_REMOTE_SERVER = "lacolline";
    public static final String MPI_SEARCH_RESULTS="mpiSearchResults";
    public static final Integer MPI_CONNECT_TIMEOUT = 60000;

    public static final String TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED = "Mirebalais - Mark Appointments Missed or Completed";

    public static final String TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_DESCRIPTION = "Marks appointments in the past as missed or completed based on Mirebalais business logic";

    public static final Long TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_REPEAT_INTERVAL = new Long(14400); // every four hours

    public static final String PRIVILEGE_SEARCH_FOR_PATIENTS = "App: coreapps.findPatient";

    public static final String HTMLFORMENTRY_CAUSE_OF_DEATH_LIST_TAG_NAME = "causeOfDeathList";

    public static final String PATIENT_DASHBOARD_LINK = "registrationapp/registrationSummary.page";

    public static final String ID_CARD_DATE_FORMAT = "dd/MMM/yyyy";

}
