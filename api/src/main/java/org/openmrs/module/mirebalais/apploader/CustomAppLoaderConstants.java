package org.openmrs.module.mirebalais.apploader;

import java.util.Arrays;
import java.util.List;

public class CustomAppLoaderConstants {

    // Apps

    // TODO would ne nice to rename this to "pih.checkin"
    // TODO for checkin would need to fix requestRecord.gsp:69
    public static final String CHECK_IN_APP = "mirebalais.liveCheckin";
    public static final String VITALS_APP = "pih.app.vitals";
    public static final String AWAITING_ADMISSION_APP = "coreapps.app.awaitingAdmission";
    public static final String ACTIVE_VISITS_APP = "pih.app.activeVisits";
    public static final String ARCHIVES_ROOM_APP = "paperrecord.app.archivesRoom";
    public static final String SYSTEM_ADMINISTRATION_APP = "coreapps.app.systemAdministration";
    public static final String APPOINTMENT_SCHEDULING_HOME_APP = "appointmentschedulingui.app";
    public static final String DISPENSING_APP = "dispensing.app";
    public static final String SCHEDULE_APPOINTMENT_APP = "appointmentschedulingui.schedulingAppointmentApp";
    public static final String MY_ACCOUNT_APP = "emr.myAccount";
    public static final String REPORTS_APP = "reportingui.reports";
    public static final String INPATIENTS_APP = "mirebalaisreports.inpatients";

    public static final String MANAGE_ACCOUNTS_APP = "emr.account.manageAccounts";
    public static final String MANAGE_PRINTERS_APP = "printer.managePrinters";
    public static final String DEFAULT_PRINTERS_APP = "printer.defaultPrinters";
    public static final String MERGE_PATIENTS_APP = "emr.mergePatients";
    public static final String REGISTER_TEST_PATIENT_APP = "emr.testPatient.registration";
    public static final String FEATURE_TOGGLES_APP = "pih.featureToggles";

    public static final String LEGACY_MPI_APP = "mirebalais.mpi";
    public static final String LEGACY_PATIENT_REGISTRATION_APP = "mirebalais.patientRegistration.app.registration";
    public static final String LEGACY_PATIENT_REGISTRATION_ED_APP = "mirebalais.patientRegistration.app.registration.ed";
    public static final String LEGACY_PATIENT_LOOKUP_APP = "mirebalais.patientRegistration.app.lookup";


    // Extensions
    public static final String CHECK_IN_VISIT_ACTION = "pih.checkin.visitAction";
    public static final String VITALS_CAPTURE_VISIT_ACTION = "pih.form.vitals";
    public static final String CONSULT_NOTE_VISIT_ACTION = "pih.form.consult";
    public static final String ADMISSION_NOTE_VISIT_ACTION = "pih.form.admission";
    public static final String DISPENSE_MEDICATION_VISIT_ACTION = "dispensing.form";
    public static final String ED_CONSULT_NOTE_VISIT_ACTION = "pih.form.edConsult";
    public static final String SURGICAL_NOTE_VISIT_ACTION = "pih.form.surgicalNote";
    public static final String DEATH_CERTIFICATE_VISIT_ACTION = "pih.haiti.deathCertificate";
    public static final String ORDER_XRAY_VISIT_ACTION = "radiologyapp.orderXray";
    public static final String ORDER_CT_VISIT_ACTION = "radiologyapp.orderCT";
    public static final String ORDER_ULTRASOUND_VISIT_ACTION = "radiologyapp.orderUS";

    public static final String ADMISSION_FORM_AWAITING_ADMISSION_ACTION = "pih.form.admit";
    public static final String DENY_ADMISSION_FORM_AWAITING_ADMISSION_ACTION = "pih.form.deny";

    public static final String REQUEST_PAPER_RECORD_OVERALL_ACTION = "paperrecord.requestPaperRecord";
    public static final String REQUEST_APPOINTMENT_OVERALL_ACTION = "appointmentschedulingui.requestAppointment";
    public static final String PRINT_ID_CARD_OVERALL_ACTION = "paperrecord.printIdCardLabel";
    public static final String PRINT_PAPER_FORM_LABEL_OVERALL_ACTION = "paperrecord.printPaperFormLabel";
    public static final String PRINT_WRISTBAND_OVERALL_ACTION = "pih.wristband.print";
    public static final String CREATE_VISIT_OVERALL_ACTION = "coreapps.createVisit";
    public static final String CREATE_RETROSPECTIVE_VISIT_OVERALL_ACTION = "coreapps.createRetrospectiveVisit";
    public static final String MERGE_VISITS_OVERALL_ACTION = "coreapps.mergeVisits";

    public static final String PAPER_RECORD_ACTIONS_INCLUDES = "paperrecord.patientDashboard.includes";
    public static final String PRINT_WRISTBAND_ACTION_INCLUDES = "pih.wristband.patientDashboard.includes";
    public static final String VISIT_ACTIONS_INCLUDES = "coreapps.patientDashboard.includes";

    public static final String RADIOLOGY_TAB = "radiologyapp.tab";
    public static final String APPOINTMENTS_TAB = "appointmentschedulingui.tab";

    public static final String PIH_HEADER_EXTENSION = "pih.header";

    public static final String DEATH_CERTIFICATE_HEADER_EXTENSION = "pih.header.deathCertificate";

    public static final String REPORTING_AD_HOC_ANALYSIS = "reportingui.dataExports.adHoc";

    // Reports

    public static final String DAILY_REGISTRATIONS_OVERVIEW_REPORT = "mirebalaisreports.overview.dailyRegistrations";
    public static final String DAILY_CHECK_INS_OVERVIEW_REPORT = "mirebalaisreports.overview.dailyCheckIns";
    public static final String DAILY_CLINICAL_ENCOUNTERS_OVERVIEW_REPORT = "mirebalaisreports.overview.dailyClinicalEncounters";
    public static final String DAILY_INPATIENTS_OVERVIEW_REPORT = "mirebalaisreports.overview.inpatientDaily";
    public static final String MONTHLY_INPATIENTS_OVERVIEW_REPORT = "mirebalaisreports.overview.inpatientMonthly";

    public static final String NON_CODED_DIAGNOSES_DATA_QUALITY_REPORT = "mirebalaisreports.dataQuality.nonCodedDiagnoses";

    public static final String USERS_AND_PROVIDERS_DATA_EXPORT = "mirebalaisreports.dataExports.usersAndProviders";
    public static final String LQAS_DATA_EXPORT = "mirebalaisreports.dataExports.lqasDiagnoses";
    public static final String ALL_PATIENTS_WITH_IDS_DATA_EXPORT = "mirebalaisreports.dataExports.allPatientsWithIds";
    public static final String APPOINTMENTS_DATA_EXPORT = "mirebalaisreports.dataExports.appointments";

    // Extension Points
    public static final String OVERALL_ACTIONS_EXTENSION_POINT = "patientDashboard.overallActions";
    public static final String VISIT_ACTIONS_EXTENSION_POINT = "patientDashboard.visitActions";
    public static final String AWAITING_ADMISSION_ACTIONS_EXTENSION_POINT = "coreapps.app.awaitingAdmissionActions";
    public static final String ENCOUNTER_TEMPLATE_EXTENSION_POINT = "org.openmrs.referenceapplication.encounterTemplate";
    public static final String HOME_PAGE_EXTENSION_POINT = "org.openmrs.referenceapplication.homepageLink";
    public static final String HEADER_EXTENSION_POINT = "appui.header.config";
    public static final String DEATH_INFO_HEADER_EXTENSION_POINT = "patientHeader.deathInfo";
    public static final String DASHBOARD_TAB_EXTENSION_POINT = "patientDashboard.tabs";
    public static final String DASHBOARD_INCLUDE_FRAGMENTS_EXTENSION_POINT = "patientDashboard.includeFragments";
    public static final String SYSTEM_ADMINISTRATION_PAGE_EXTENSION_POINT = "systemAdministration.apps";
    public static final String REPORTING_DATA_EXPORT_EXTENSION_POINT = "org.openmrs.module.reportingui.reports.dataexport";
    public static final String REPORTING_OVERVIEW_REPORTS_EXTENSION_POINT = "org.openmrs.module.reportingui.reports.overview";
    public static final String REPORTING_DATA_QUALITY_EXTENSION_POINT = "org.openmrs.module.reportingui.reports.dataquality";


    // Encounter templates
    public static final String DEFAULT_ENCOUNTER_TEMPLATE = "defaultEncounterTemplate";
    public static final String CONSULT_ENCOUNTER_TEMPLATE = "consultEncounterTemplate";
    public static final String NO_DETAILS_ENCOUNTER_TEMPLATE = "noDetailsEncounterTemplate";

    // order of lists define the order apps and extensions appear
    public static final List<String> HOME_PAGE_APPS_ORDER = Arrays.asList(
            CHECK_IN_APP,
            VITALS_APP,
            ACTIVE_VISITS_APP,
            LEGACY_PATIENT_REGISTRATION_APP,
            LEGACY_PATIENT_REGISTRATION_ED_APP,
            LEGACY_PATIENT_LOOKUP_APP,
            DISPENSING_APP,
            APPOINTMENT_SCHEDULING_HOME_APP,
            ARCHIVES_ROOM_APP,
            AWAITING_ADMISSION_APP,
            REPORTS_APP,
            LEGACY_MPI_APP,
            SYSTEM_ADMINISTRATION_APP,
            MY_ACCOUNT_APP);

    public static final List<String> SYSTEM_ADMINISTRATION_APPS_ORDER = Arrays.asList(
            MANAGE_ACCOUNTS_APP,
            MANAGE_PRINTERS_APP,
            DEFAULT_PRINTERS_APP,
            MERGE_PATIENTS_APP,
            REGISTER_TEST_PATIENT_APP,
            FEATURE_TOGGLES_APP);

    public static final List<String> OVERALL_ACTIONS_ORDER = Arrays.asList(
            CREATE_VISIT_OVERALL_ACTION,
            REQUEST_PAPER_RECORD_OVERALL_ACTION,
            REQUEST_APPOINTMENT_OVERALL_ACTION,
            PRINT_PAPER_FORM_LABEL_OVERALL_ACTION,
            PRINT_ID_CARD_OVERALL_ACTION,
            PRINT_WRISTBAND_OVERALL_ACTION,
            SCHEDULE_APPOINTMENT_APP,
            CREATE_RETROSPECTIVE_VISIT_OVERALL_ACTION,
            MERGE_VISITS_OVERALL_ACTION);

    public static final List<String> VISIT_ACTIONS_ORDER = Arrays.asList(
            CONSULT_NOTE_VISIT_ACTION,
            ED_CONSULT_NOTE_VISIT_ACTION,
            ADMISSION_NOTE_VISIT_ACTION,
            VITALS_CAPTURE_VISIT_ACTION,
            ORDER_XRAY_VISIT_ACTION,
            ORDER_CT_VISIT_ACTION,
            ORDER_ULTRASOUND_VISIT_ACTION,
            DISPENSE_MEDICATION_VISIT_ACTION,
            DEATH_CERTIFICATE_VISIT_ACTION,
            CHECK_IN_VISIT_ACTION);

    public static final List<String> AWAITING_ADMISSION_ACTIONS_ORDER = Arrays.asList(
            ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
            DENY_ADMISSION_FORM_AWAITING_ADMISSION_ACTION);

    public static final List<String> REPORTING_OVERVIEW_REPORTS_ORDER = Arrays.asList(
            DAILY_REGISTRATIONS_OVERVIEW_REPORT,
            DAILY_CHECK_INS_OVERVIEW_REPORT,
            DAILY_CLINICAL_ENCOUNTERS_OVERVIEW_REPORT,
            DAILY_INPATIENTS_OVERVIEW_REPORT,
            MONTHLY_INPATIENTS_OVERVIEW_REPORT);

    public static final List<String> REPORTING_DATA_EXPORT_REPORTS_ORDER = Arrays.asList(
            USERS_AND_PROVIDERS_DATA_EXPORT,
            LQAS_DATA_EXPORT,
            ALL_PATIENTS_WITH_IDS_DATA_EXPORT,
            APPOINTMENTS_DATA_EXPORT);

}
