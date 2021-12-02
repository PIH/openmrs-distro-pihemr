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

public class MirebalaisConstants {

	public static final String MIREBALAIS_MODULE_ID = "mirebalais";

    public static final String LACOLLINE_LOCATION_UUID = "23e7bb0d-51f9-4d5f-b34b-2fbbfeea1960";

    public static final String MPI_REMOTE_SERVER = "lacolline";
    public static final String MPI_SEARCH_RESULTS="mpiSearchResults";
    public static final Integer MPI_CONNECT_TIMEOUT = 60000;

    public static final String TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED = "Mirebalais - Mark Appointments Missed or Completed";
    public static final String TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_DESCRIPTION = "Marks appointments in the past as missed or completed based on Mirebalais business logic";
    public static final Long TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_REPEAT_INTERVAL = new Long(14400); // every four hours

    public static final String GLUCOSE_CONCEPT_UUID = "3cd4e194-26fe-102b-80cb-0017a47871b2";
    public static final String HBA1C_CONCEPT_UUID = "159644AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String ABDOMINAL_CIRCUMFERENCE_CONCEPT_UUID = "163080AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String FOOT_EXAM_CONCEPT_UUID = "18ea04b9-239e-43b8-9508-f57949d60361";
    public static final String URINARY_ALBUMIN_CONCEPT_UUID = "3cd49d88-26fe-102b-80cb-0017a47871b2";
    public static final String ALCOHOL_USE_CONCEPT_UUID = "3cdbde18-26fe-102b-80cb-0017a47871b2";
    public static final String TOBACCO_USE_CONCEPT_UUID = "3ce503e4-26fe-102b-80cb-0017a47871b2";
    public static final String TOTAL_CHOLESTEROL_CONCEPT_UUID = "3cd68c7e-26fe-102b-80cb-0017a47871b2";
    public static final String HDL_CONCEPT_UUID = "3cd68e18-26fe-102b-80cb-0017a47871b2";
    public static final String LDL_CONCEPT_UUID = "3cd68fa8-26fe-102b-80cb-0017a47871b2";
    public static final String SYSTOLIC_BP_CONCEPT_UUID = "3ce934fa-26fe-102b-80cb-0017a47871b2";
    public static final String DIASTOLIC_BP_CONCEPT_UUID = "3ce93694-26fe-102b-80cb-0017a47871b2";
    public static final String WEIGHT_CONCEPT_UUID = "3ce93b62-26fe-102b-80cb-0017a47871b2";
    public static final String HEIGHT_CONCEPT_UUID = "3ce93cf2-26fe-102b-80cb-0017a47871b2";
    public static final String HEAD_CIRC_CONCEPT_UUID = "3ceb96b4-26fe-102b-80cb-0017a47871b2";
    public static final String VIRAL_LOAD_UUID = "3cd4a882-26fe-102b-80cb-0017a47871b2";
    public static final String ASTHMA_DAYTIME_SYMPTOMS_TWICE_WEEKLY = "cc4681ee-95df-4400-9900-23193cdc6592";
    public static final String ASTHMA_DAYTIME_SYMPTOMS_ONCE_WEEKLY = "3672ee1d-ba8e-4748-8ccc-98d70035857b";
    public static final String ASTHMA_MEDS_TWICE_WEEKLY = "f813d9fa-0842-4862-ae08-5ed30a068207";
    public static final String LIMITATION_OF_ACTIVITY = "abab707b-0ca5-43dd-9b6d-57cb2348e8f8";
    public static final String EPI_SEIZURES_BASELINE = "159517AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String EPI_SEIZURES = "ba2e9e43-5a9d-423f-a33e-c34765785397";
    public static final String PHQ9 = "165137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String GAD7 = "8b8769a9-a8cc-4166-ba2a-2e61fb081be7";
    public static final String WHODAS = "163226AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String ZLDSI = "163225AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String SEIZURE_FREQUENCY = "ba2e9e43-5a9d-423f-a33e-c34765785397";
    public static final String SARS_COV2_ANTIBODY_TEST = "165853AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String SARS_COV2_ANTIGEN_TEST = "165852AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String SARS_COV2_RT_PCR_TEST = "165840AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String SARS_COV2_XPERT_TEST = "423edcfa-a5a6-4bc4-a43a-b19644252dc6";
    public static final String MED_DISPENSED_NAME_UUID = "3cd9491e-26fe-102b-80cb-0017a47871b2";
    public static final String HEART_RATE_UUID = "3ce93824-26fe-102b-80cb-0017a47871b2";
    public static final String TEMPERATURE_UUID = "3ce939d2-26fe-102b-80cb-0017a47871b2";
    public static final String RESPIRATORY_RATE_UUID = "3ceb11f8-26fe-102b-80cb-0017a47871b2";
    public static final String ADVERSE_EFFECT_CONCEPT_UUID = "3cd96052-26fe-102b-80cb-0017a47871b2";
    public static final String ADVERSE_EFFECT_DATE_CONCEPT_UUID = "3cd964bc-26fe-102b-80cb-0017a47871b2";
    public static final String CLINICAL_COMMENTS_CONCEPT_UUID = "3cd9d956-26fe-102b-80cb-0017a47871b2";
}
