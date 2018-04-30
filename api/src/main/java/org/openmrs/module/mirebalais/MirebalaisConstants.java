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

    public static final String WEIGHT_CONCEPT_UUID = "3ce93b62-26fe-102b-80cb-0017a47871b2";
    public static final String CD4_COUNT_UUID = "3ceda710-26fe-102b-80cb-0017a47871b2";
    public static final String VIRAL_LOAD_UUID = "3cd4a882-26fe-102b-80cb-0017a47871b2";
    public static final String VIRAL_LOAD_QUAL_UUID = "1305AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
}
