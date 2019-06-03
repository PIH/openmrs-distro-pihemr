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

    public static final String CHECK_IN_PAYMENT_AMOUNTS_GP = "mirebalais.checkIn.paymentAmounts";
    public static final String CHECK_IN_PAYMENT_AMOUNT_LABELS_GP = "mirebalais.checkIn.paymentAmountLabels";
    public static final String CHECK_IN_PAYMENT_AMOUNT_DEFAULT_GP = "mirebalais.checkIn.paymentAmountDefault";

    public static final String LACOLLINE_LOCATION_UUID = "23e7bb0d-51f9-4d5f-b34b-2fbbfeea1960";

    public static final String MPI_REMOTE_SERVER = "lacolline";
    public static final String MPI_SEARCH_RESULTS="mpiSearchResults";
    public static final Integer MPI_CONNECT_TIMEOUT = 60000;

    public static final String TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED = "Mirebalais - Mark Appointments Missed or Completed";
    public static final String TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_DESCRIPTION = "Marks appointments in the past as missed or completed based on Mirebalais business logic";
    public static final Long TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_REPEAT_INTERVAL = new Long(14400); // every four hours

    public static final String PRIVILEGE_SEARCH_FOR_PATIENTS = "App: coreapps.findPatient";

    public static final String ID_CARD_DATE_FORMAT = "dd/MMM/yyyy";

    public static final String GP_NEXT_RADIOLOGY_ORDER_NUMBER_SEED = "order.nextRadiologyOrderNumberSeed";

    public static final String RADIOLOGY_ORDER_NUMBER_GENERATOR_BEAN_ID = "order.radiologyOrderNumberGenerator";

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
    public static final String CD4_COUNT_UUID = "3ceda710-26fe-102b-80cb-0017a47871b2";
    public static final String VIRAL_LOAD_UUID = "3cd4a882-26fe-102b-80cb-0017a47871b2";
    public static final String VIRAL_LOAD_QUAL_UUID = "1305AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String EPI_SEIZURES_BASELINE = "159517AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String EPI_SEIZURES = "ba2e9e43-5a9d-423f-a33e-c34765785397";

    public static final String MED_DISPENSED_NAME_UUID = "3cd9491e-26fe-102b-80cb-0017a47871b2";
    public static final String MED_DISPENSED_FREQ_UUID = "a15c95ff-236f-488f-a879-f19fc982bbe6";

    public static final String NEXT_RETURN_VISIT_UUID = "CIEL:5096";
}
