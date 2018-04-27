package org.openmrs.module.mirebalais.apploader;

import org.openmrs.module.allergyui.AllergyUIConstants;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.registrationapp.RegistrationAppConstants;

import java.util.Arrays;
import java.util.List;

public class CustomAppLoaderConstants {

    public static final class Apps {

        // TODO would be nice to rename this to "pih.checkin"--would need to change CheckInAppWorkflowController in pihcore
        // TODO for checkin would need to fix requestRecord.gsp:69
        public static final String CHECK_IN = "mirebalais.liveCheckin";
        public static final String UHM_VITALS = "pih.uhm.app.vitals";
        public static final String VITALS = "pih.app.vitals";
        public static final String AWAITING_ADMISSION = CoreAppsConstants.AWAITING_ADMISSION;
        public static final String ACTIVE_VISITS = "pih.app.activeVisits";
        public static final String ARCHIVES_ROOM = "paperrecord.app.archivesRoom";
        public static final String SYSTEM_ADMINISTRATION = "coreapps.app.systemAdministration";
        public static final String APPOINTMENT_SCHEDULING_HOME = "appointmentschedulingui.app";
        public static final String DISPENSING = "dispensing.app";
        public static final String SCHEDULE_APPOINTMENT = "appointmentschedulingui.schedulingAppointmentApp";
        public static final String MY_ACCOUNT = "emr.myAccount";
        public static final String REPORTS = "reportingui.reports";
        public static final String INPATIENTS = "mirebalaisreports.inpatients";
        public static final String ACTIVE_VISITS_LIST = "mirebalaisreports.activeVisitsList";
        public static final String PATIENT_REGISTRATION = "registrationapp.registerPatient";
        public static final String CLINICIAN_DASHBOARD = "pih.app.clinicianDashboard";
        public static final String VISITS_SUMMARY = "coreapps.clinicianfacing.visits";
        public static final String WAITING_FOR_CONSULT = "pih.app.waitingForConsult";
        public static final String CHW_MGMT = "chw.app.mgmt";
        public static final String ED_TRIAGE = "edtriageapp.app.edTriage";
        public static final String ED_TRIAGE_QUEUE = "edtriageapp.app.triageQueue";
        public static final String TODAYS_VISITS = "pih.app.todaysVisits";
        public static final String LAB_TRACKING = "labtracking.app.monitorOrders";
        public static final String PROGRAMS_LIST = "coreapps.app.programsList";
        public static final String RELATIONSHIPS_REGISTRATION_SUMMARY = "pih.app.relationships.registration.summary";
        public static final String RELATIONSHIPS_CLINICAL_SUMMARY = "pih.app.relationships.clinical.summary";
        public static final String PROGRAM_SUMMARY_LIST = "pih.app.programSummaryList";
        public static final String ZIKA_PATIENT_PROGRAM_SUMMARY = "pih.app.zika.patientProgramSummary";
        public static final String ZIKA_PATIENT_PROGRAM_HISTORY = "pih.app.zika.patientProgramHistory";
        public static final String ZIKA_PROGRAM_SUMMARY_DASHBOARD = "pih.app.zika.programSummary.dashboard";
        public static final String ZIKA_PROGRAM_STATISTICS = "pih.app.zika.programStatistics";
        public static final String HIV_PATIENT_PROGRAM_SUMMARY = "pih.app.hiv.patientProgramSummary";
        public static final String HIV_PATIENT_PROGRAM_HISTORY = "pih.app.hiv.patientProgramHistory";
        public static final String HIV_PROGRAM_SUMMARY_DASHBOARD = "pih.app.hiv.programSummary.dashboard";
        public static final String HIV_PROGRAM_STATISTICS = "pih.app.hiv.programStatistics";
        public static final String HIV_WEIGHT_GRAPH = "pih.app.hiv.weighGraph";
        public static final String HIV_CD4_GRAPH = "pih.app.hiv.cd4Graph";
        public static final String HIV_OBS_CHART = "pih.app.hiv.obsChart";
        public static final String NCD_PATIENT_PROGRAM_SUMMARY = "pih.app.ncd.patientProgramSummary";
        public static final String NCD_PATIENT_PROGRAM_HISTORY = "pih.app.ncd.patientProgramHistory";
        public static final String NCD_PROGRAM_SUMMARY_DASHBOARD = "pih.app.ncd.programSummary.dashboard";
        public static final String NCD_PROGRAM_STATISTICS = "pih.app.ncd.programStatistics";
        public static final String PATIENT_DOCUMENTS = "attachments.app.patientDocuments";
        public static final String CONDITION_LIST = "coreapps.conditionList";
        public static final String ACTIVE_DRUG_ORDERS = "orderentryui.activeDrugOrders";

        public static final String MANAGE_ACCOUNTS = "emr.account.manageAccounts";
        public static final String PRINTER_ADMINISTRATION = "printer.printerAdministration";
        public static final String MERGE_PATIENTS = "coreapps.mergePatients";
        public static final String FEATURE_TOGGLES = "pih.featureToggles";
        public static final String PATIENT_EXPORT = "pih.exportPatients";
        public static final String PATIENT_IMPORT = "pih.importPatients";

        public static final String ADDITIONAL_IDENTIFIERS = "registrationapp.additionalIdentifiers";
        public static final String MOST_RECENT_VITALS = "coreapps.mostRecentVitals";
        public static final String MOST_RECENT_REGISTRATION = "coreapps.mostRecentRegistration";
        public static final String MOST_RECENT_REGISTRATION_SUMMARY = "coreapps.mostRecentRegistrationSummary";
        public static final String MOST_RECENT_REGISTRATION_SOCIAL = "coreapps.mostRecentRegistrationSocial";
        public static final String MOST_RECENT_REGISTRATION_INSURANCE = "coreapps.mostRecentRegistrationInsurance";
        public static final String MOST_RECENT_REGISTRATION_CONTACT = "coreapps.mostRecentRegistrationContact";
        public static final String MOST_RECENT_CHECK_IN = "coreapps,mostRecentCheckIn";
        public static final String ALLERGY_SUMMARY = "allergyui.allergySummary";
        public static final String LAB_SUMMARY = "labtrackingapp.labSummary";
        public static final String ID_CARD_PRINTING_STATUS = "mirebalais.idCardPrintingStatus";
        public static final String BIOMETRICS_SUMMARY = "registrationapp.biometricsSummary";

        public static final String LEGACY_MPI = "mirebalais.mpi";

        public static final String RADIOLOGY_APP = "radiology.app";
        public static final String RADIOLOGY_ORDERS_APP = "radiology.orders.app";

        public static final String COHORT_BUILDER_APP = "cohortBuilder.app";
    }

    public static final class Extensions {

        public static final String CHECK_IN_VISIT_ACTION = "pih.checkin.visitAction";
        public static final String CHECK_IN_REGISTRATION_ACTION = "pih.checkin.registrationAction";
        public static final String VITALS_CAPTURE_VISIT_ACTION = "pih.form.vitals";
        public static final String CONSULT_NOTE_VISIT_ACTION = "pih.form.consult";
        public static final String ADMISSION_NOTE_VISIT_ACTION = "pih.form.admission";
        public static final String DISPENSE_MEDICATION_VISIT_ACTION = "dispensing.form";
        public static final String ED_CONSULT_NOTE_VISIT_ACTION = "pih.form.edConsult";
        public static final String SURGICAL_NOTE_VISIT_ACTION = "pih.form.surgicalNote";
        public static final String ONCOLOGY_CONSULT_NOTE_VISIT_ACTION = "pih.form.oncologyNote";
        public static final String ONCOLOGY_INITIAL_VISIT_ACTION = "pih.form.oncologyIntake";
        public static final String CHEMOTHERAPY_VISIT_ACTION = "pih.form.chemotherapy";
        public static final String LAB_RESULTS_VISIT_ACTION = "pih.form.labResults";
        public static final String NCD_ADULT_INITIAL_VISIT_ACTION = "pih.form.ncdAdultInitial";
        public static final String NCD_ADULT_FOLLOWUP_VISIT_ACTION = "pih.form.ncdAdultFollowup";

        public static final String HIV_ZL_INITIAL_VISIT_ACTION = "pih.form.hivZLAdultInitial";
        public static final String HIV_ZL_FOLLOWUP_VISIT_ACTION = "pih.form.hivZLAdultFollowup";

        public static final String HIV_ADULT_INITIAL_VISIT_ACTION = "pih.form.hivAdultInitial";
        public static final String HIV_ADULT_FOLLOWUP_VISIT_ACTION = "pih.form.hivAdultFollowup";
        public static final String HIV_PEDS_INITIAL_VISIT_ACTION = "pih.form.hivPedsInitial";
        public static final String HIV_PEDS_FOLLOWUP_VISIT_ACTION = "pih.form.hivPedsFollowup";
        public static final String HIV_ADHERENCE_VISIT_ACTION = "pih.form.hivAdherence";
        public static final String MENTAL_HEALTH_VISIT_ACTION = "pih.form.mentalHealth";
        public static final String VCT_VISIT_ACTION = "pih.form.vct";
        public static final String SOCIO_ECONOMICS_VISIT_ACTION = "pih.form.socioEconomics";
        public static final String ORDER_XRAY_VISIT_ACTION = "radiologyapp.orderXray";
        public static final String ORDER_CT_VISIT_ACTION = "radiologyapp.orderCT";
        public static final String ORDER_ULTRASOUND_VISIT_ACTION = "radiologyapp.orderUS";
        public static final String REGISTRATION_SUMMARY_OVERALL_ACTION = "registrationapp.registrationSummary.link";
        public static final String PRIMARY_CARE_PEDS_INITIAL_VISIT_ACTION = "pih.primaryCare.pedsInitial";
        public static final String PRIMARY_CARE_PEDS_FOLLOWUP_VISIT_ACTION = "pih.primaryCare.pedsFollowup";
        public static final String PRIMARY_CARE_ADULT_INITIAL_VISIT_ACTION = "pih.primaryCare.adultInitial";
        public static final String PRIMARY_CARE_ADULT_FOLLOWUP_VISIT_ACTION = "pih.primaryCare.adultFollowup";
        public static final String ED_TRIAGE_VISIT_ACTION = "edtriageapp.edTriageNote";
        public static final String ORDER_LAB_VISIT_ACTION = "labtrackingapp.orderLab";

        public static final String ADMISSION_FORM_AWAITING_ADMISSION_ACTION = "pih.form.admit";
        public static final String DENY_ADMISSION_FORM_AWAITING_ADMISSION_ACTION = "pih.form.deny";

        public static final String REQUEST_PAPER_RECORD_OVERALL_ACTION = "paperrecord.requestPaperRecord";
        public static final String REQUEST_APPOINTMENT_OVERALL_ACTION = "appointmentschedulingui.requestAppointment";
        public static final String SCHEDULE_APPOINTMENT_OVERALL_ACTION = "appointmentschedulingui.scheduleAppointment";
        public static final String PRINT_ID_CARD_OVERALL_ACTION = "paperrecord.printIdCardLabel";
        public static final String PRINT_PAPER_FORM_LABEL_OVERALL_ACTION = "paperrecord.printPaperFormLabel";
        public static final String PRINT_WRISTBAND_OVERALL_ACTION = "pih.wristband.print";
        public static final String CREATE_VISIT_OVERALL_ACTION = "coreapps.createVisit";
        public static final String CREATE_HIV_VISIT_OVERALL_ACTION = "coreapps.hiv.createVisit";
        public static final String CREATE_RETROSPECTIVE_VISIT_OVERALL_ACTION = "coreapps.createRetrospectiveVisit";
        public static final String MERGE_VISITS_OVERALL_ACTION = "coreapps.mergeVisits";
        public static final String DEATH_CERTIFICATE_OVERALL_ACTION = "pih.haiti.deathCertificate";
        public static final String CHART_SEARCH_OVERALL_ACTION = "chartsearch.overallAction";
        public static final String PATIENT_DOCUMENTS_OVERALL_ACTION = "attachments.patientDocuments.overallAction";

        public static final String PAPER_RECORD_ACTIONS_INCLUDES = "paperrecord.patientDashboard.includes";
        public static final String PRINT_WRISTBAND_ACTION_INCLUDES = "pih.wristband.patientDashboard.includes";
        public static final String VISIT_ACTIONS_INCLUDES = "coreapps.patientDashboard.includes";
        public static final String HIV_DASHBOARD_VISIT_INCLUDES = "pih.hivPatientDashboard.includes";

        public static final String RADIOLOGY_TAB = "radiologyapp.tab";
        public static final String APPOINTMENTS_TAB = "appointmentschedulingui.tab";

        public static final String EDIT_PATIENT_CONTACT_INFO = "registrationapp.editPatientContactInfo";
        public static final String EDIT_PATIENT_DEMOGRAPHICS = "registrationapp.editPatientDemographics";
        public static final String CLINICIAN_FACING_PATIENT_DASHBOARD = "coreapps.clinicianFacingPatientDashboardApp";
        public static final String REGISTER_NEW_PATIENT = "registrationapp.registerNewPatient";
        public static final String MERGE_INTO_ANOTHER_PATIENT = "registrationapp.mergePatient";
        public static final String PRINT_PAPER_FORM_LABEL = "registrationapp.printPaperFormLabel";
        public static final String PRINT_ID_CARD_REGISTRATION_ACTION = "mirebalais.printIdCard";
        public static final String VISITS_DASHBOARD = "coreapps.visitsDashboard";
        public static final String BIOMETRICS_FIND_PATIENT = "biometrics.findPatient";

        public static final String PIH_HEADER_EXTENSION = "pih.header";

        public static final String DEATH_CERTIFICATE_HEADER_EXTENSION = "pih.header.deathCertificate";

        public static final String REPORTING_AD_HOC_ANALYSIS = "reportingui.dataExports.adHoc";

        public static final String CLINICIAN_DASHBOARD_ACTIVE_DRUG_ORDERS = "orderentryui.patientDashboard.activeDrugOrders";

        public static final String ALLERGY_UI_VISIT_NOTE_NEXT_SUPPORT = "allergyui.allergires.visitNoteNextSupport";

        // Reports
        public static final String REGISTRATION_SUMMARY_BY_AGE_REPORT = "mirebalaisreports.overview.registrationsByAge";
        public static final String CHECK_IN_SUMMARY_BY_AGE_REPORT = "mirebalaisreports.overview.checkinsByAge";

        public static final String DAILY_INPATIENTS_OVERVIEW_REPORT = "mirebalaisreports.overview.inpatientDaily";
        public static final String MONTHLY_INPATIENTS_OVERVIEW_REPORT = "mirebalaisreports.overview.inpatientMonthly";


        public static final String NON_CODED_DIAGNOSES_DATA_QUALITY_REPORT = "mirebalaisreports.dataQuality.nonCodedDiagnoses";

        public static final String LQAS_DATA_EXPORT = "mirebalaisreports.dataExports.lqasDiagnoses";
    }

    public static final class ExtensionPoints {
        public static final String OVERALL_ACTIONS = "patientDashboard.overallActions";
        public static final String OVERALL_REGISTRATION_ACTIONS = "registrationSummary.overallActions";
        public static final String VISIT_ACTIONS = "patientDashboard.visitActions";
        public static final String AWAITING_ADMISSION_ACTIONS = "coreapps.app.awaitingAdmissionActions";
        public static final String ENCOUNTER_TEMPLATE = "org.openmrs.referenceapplication.encounterTemplate";
        public static final String HOME_PAGE = "org.openmrs.referenceapplication.homepageLink";
        public static final String PROGRAM_SUMMARY_LIST = Apps.PROGRAM_SUMMARY_LIST + ".apps";
        public static final String DEATH_INFO_HEADER = "patientHeader.deathInfo";
        public static final String DASHBOARD_TAB = "patientDashboard.tabs";
        public static final String DASHBOARD_INCLUDE_FRAGMENTS = "patientDashboard.includeFragments";
        public static final String SYSTEM_ADMINISTRATION_PAGE = "systemAdministration.apps";
        public static final String REPORTING_DATA_EXPORT = "org.openmrs.module.reportingui.reports.dataexport";
        public static final String REPORTING_OVERVIEW_REPORTS = "org.openmrs.module.reportingui.reports.overview";
        public static final String REPORTING_DATA_QUALITY = "org.openmrs.module.reportingui.reports.dataquality";
        public static final String REPORTING_MONITORING = "org.openmrs.module.reportingui.reports.monitoring";
        public static final String PATIENT_HEADER_PATIENT_CONTACT_INFO = "patientHeader.editPatientContactInfo";
        public static final String PATIENT_HEADER_PATIENT_DEMOGRAPHICS = "patientHeader.editPatientDemographics";
        public static final String CLINICIAN_DASHBOARD_FIRST_COLUMN = "patientDashboard.firstColumnFragments";
        public static final String CLINICIAN_DASHBOARD_SECOND_COLUMN = "patientDashboard.secondColumnFragments";
        public static final String REGISTRATION_SUMMARY_CONTENT = "registrationSummary.contentFragments";
        public static final String REGISTRATION_SUMMARY_SECOND_COLUMN_CONTENT = "registrationSummary.secondColumnContentFragments";
        public static final String REGISTRATION_FIND_PATIENT_FRAGMENTS = RegistrationAppConstants.FIND_PATIENT_FRAGMENTS_EXTENSION_POINT;
        public static final String ALLERGIES_PAGE_INCLUDE_PAGE = AllergyUIConstants.ALLERGIES_PAGE_INCLUDE_FRAGMENT_EXTENSION_POINT;
        public static final String PATIENT_SEARCH = "coreapps.patientSearch.extension";
    }

    // TODO are these still used once we switch to the new visit dashboard?
    public static final class EncounterTemplates {
        public static final String DEFAULT = "defaultEncounterTemplate";
        public static final String CONSULT = "consultEncounterTemplate";
        public static final String NO_DETAILS = "noDetailsEncounterTemplate";
        public static final String ED_TRIAGE = "edtriageEncounterTemplate";
    }

    // order of lists define the order apps and extensions appear
    public static final List<String> HOME_PAGE_APPS_ORDER = Arrays.asList(
            Apps.ACTIVE_VISITS,
            Apps.PATIENT_REGISTRATION,
            Apps.CHECK_IN,
            Apps.AWAITING_ADMISSION,
            Apps.UHM_VITALS,
            Apps.VITALS,
            Apps.WAITING_FOR_CONSULT,
            Apps.TODAYS_VISITS,
            Apps.APPOINTMENT_SCHEDULING_HOME,
            Apps.ARCHIVES_ROOM,
            Apps.INPATIENTS,
            Apps.LAB_TRACKING,
            Apps.PROGRAM_SUMMARY_LIST,
            Apps.REPORTS,
            Apps.DISPENSING,
            Apps.ED_TRIAGE,
            Apps.ED_TRIAGE_QUEUE,
            Apps.CHW_MGMT,
            Apps.COHORT_BUILDER_APP,
            Apps.LEGACY_MPI,
            Apps.MY_ACCOUNT,
            Apps.SYSTEM_ADMINISTRATION);

    public static final List<String> PROGRAM_SUMMARY_LIST_APPS_ORDER = Arrays.asList(
            Apps.HIV_PROGRAM_SUMMARY_DASHBOARD
    );

    public static final List<String> SYSTEM_ADMINISTRATION_APPS_ORDER = Arrays.asList(
            Apps.MANAGE_ACCOUNTS,
            Apps.PRINTER_ADMINISTRATION,
            Apps.MERGE_PATIENTS,
            Apps.FEATURE_TOGGLES);

    public static final List<String> OVERALL_ACTIONS_ORDER = Arrays.asList(
            Extensions.CREATE_VISIT_OVERALL_ACTION,
            Extensions.CREATE_RETROSPECTIVE_VISIT_OVERALL_ACTION,
            Extensions.REQUEST_PAPER_RECORD_OVERALL_ACTION,
            Extensions.PRINT_PAPER_FORM_LABEL_OVERALL_ACTION,
            Extensions.PRINT_ID_CARD_OVERALL_ACTION,
            Extensions.PRINT_WRISTBAND_OVERALL_ACTION,
            Extensions.REQUEST_APPOINTMENT_OVERALL_ACTION,
            Extensions.SCHEDULE_APPOINTMENT_OVERALL_ACTION,
            Extensions.MERGE_VISITS_OVERALL_ACTION,
            Extensions.REGISTRATION_SUMMARY_OVERALL_ACTION,
            Extensions.DEATH_CERTIFICATE_OVERALL_ACTION,
            Extensions.PATIENT_DOCUMENTS_OVERALL_ACTION,
            Extensions.EDIT_PATIENT_DEMOGRAPHICS,
            Extensions.EDIT_PATIENT_CONTACT_INFO,
            Extensions.CHART_SEARCH_OVERALL_ACTION);  // TODO remember to permission chart search in Custom App Loader Factory

    public static final List<String> VISIT_ACTIONS_ORDER = Arrays.asList(
            Extensions.CHECK_IN_VISIT_ACTION,
            Extensions.CONSULT_NOTE_VISIT_ACTION,
            Extensions.ADMISSION_NOTE_VISIT_ACTION,
            Extensions.VITALS_CAPTURE_VISIT_ACTION,
            Extensions.ED_TRIAGE_VISIT_ACTION,
            Extensions.PRIMARY_CARE_ADULT_INITIAL_VISIT_ACTION,
            Extensions.PRIMARY_CARE_ADULT_FOLLOWUP_VISIT_ACTION,
            Extensions.PRIMARY_CARE_PEDS_INITIAL_VISIT_ACTION,
            Extensions.PRIMARY_CARE_PEDS_FOLLOWUP_VISIT_ACTION,
            Extensions.ED_CONSULT_NOTE_VISIT_ACTION,
            Extensions.DISPENSE_MEDICATION_VISIT_ACTION,
            Extensions.SURGICAL_NOTE_VISIT_ACTION,
            Extensions.ONCOLOGY_CONSULT_NOTE_VISIT_ACTION,
            Extensions.ONCOLOGY_INITIAL_VISIT_ACTION,
            Extensions.CHEMOTHERAPY_VISIT_ACTION,
            Extensions.LAB_RESULTS_VISIT_ACTION,
            Extensions.NCD_ADULT_INITIAL_VISIT_ACTION,
            Extensions.NCD_ADULT_FOLLOWUP_VISIT_ACTION,
            Extensions.MENTAL_HEALTH_VISIT_ACTION,
            Extensions.VCT_VISIT_ACTION,
            Extensions.SOCIO_ECONOMICS_VISIT_ACTION,
            Extensions.HIV_ZL_INITIAL_VISIT_ACTION,
            Extensions.HIV_ZL_FOLLOWUP_VISIT_ACTION,
            // Extensions.HIV_PEDS_INITIAL_VISIT_ACTION,
            // Extensions.HIV_PEDS_FOLLOWUP_VISIT_ACTION,
            Extensions.HIV_ADULT_INITIAL_VISIT_ACTION,
            Extensions.HIV_ADULT_FOLLOWUP_VISIT_ACTION,
            Extensions.HIV_PEDS_INITIAL_VISIT_ACTION,
            Extensions.HIV_PEDS_FOLLOWUP_VISIT_ACTION,
            Extensions.HIV_ADHERENCE_VISIT_ACTION,
            Extensions.ORDER_XRAY_VISIT_ACTION,
            Extensions.ORDER_CT_VISIT_ACTION,
            Extensions.ORDER_ULTRASOUND_VISIT_ACTION,
            Extensions.ORDER_LAB_VISIT_ACTION);

    public static final List<String> HIV_VISIT_ACTIONS_ORDER = Arrays.asList(
            Extensions.HIV_ZL_INITIAL_VISIT_ACTION + ".hiv",
            Extensions.HIV_ZL_FOLLOWUP_VISIT_ACTION + ".hiv");

    public static final List<String> AWAITING_ADMISSION_ACTIONS_ORDER = Arrays.asList(
            Extensions.ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
            Extensions.DENY_ADMISSION_FORM_AWAITING_ADMISSION_ACTION);

/*    public static final List<String> REPORTING_OVERVIEW_REPORTS_ORDER = Arrays.asList(
            Extensions.DAILY_REGISTRATIONS_OVERVIEW_REPORT,
            Extensions.DAILY_CHECK_INS_OVERVIEW_REPORT,
            Extensions.DAILY_CLINICAL_ENCOUNTERS_OVERVIEW_REPORT,
            Extensions.DAILY_INPATIENTS_OVERVIEW_REPORT,
            Extensions.MONTHLY_INPATIENTS_OVERVIEW_REPORT);*/

/*    public static final List<String> REPORTING_DATA_EXPORT_REPORTS_ORDER = Arrays.asList(
            Extensions.USERS_AND_PROVIDERS_DATA_EXPORT,
            Extensions.LQAS_DATA_EXPORT,
            Extensions.ALL_PATIENTS_WITH_IDS_DATA_EXPORT,
            Extensions.APPOINTMENTS_DATA_EXPORT);*/

    public static final List<String> CLINICIAN_DASHBOARD_FIRST_COLUMN_ORDER = Arrays.asList(
            Apps.CONDITION_LIST,
            Apps.VISITS_SUMMARY,
            Apps.APPOINTMENT_SCHEDULING_HOME,
            Apps.RADIOLOGY_APP,
            Apps.RADIOLOGY_ORDERS_APP
    );

    public static final List<String> CLINICIAN_DASHBOARD_SECOND_COLUMN_ORDER = Arrays.asList(
            Apps.ALLERGY_SUMMARY,
            Apps.MOST_RECENT_VITALS,
            Apps.LAB_SUMMARY,
            Apps.PROGRAMS_LIST,
            Apps.PATIENT_DOCUMENTS,
            Apps.RELATIONSHIPS_CLINICAL_SUMMARY,
            Apps.MOST_RECENT_REGISTRATION,
            Apps.ACTIVE_DRUG_ORDERS);

    public static final List<String> REGISTRATION_SUMMARY_FIRST_COLUMN_ORDER = Arrays.asList(
            Apps.MOST_RECENT_REGISTRATION_SUMMARY,
            Apps.MOST_RECENT_REGISTRATION_INSURANCE,
            Apps.MOST_RECENT_REGISTRATION_SOCIAL
    );

    public static final List<String> REGISTRATION_SUMMARY_SECOND_COLUMN_ORDER = Arrays.asList(
            Apps.ADDITIONAL_IDENTIFIERS,
            Apps.MOST_RECENT_REGISTRATION_CONTACT,
            Apps.BIOMETRICS_SUMMARY,
            Apps.RELATIONSHIPS_REGISTRATION_SUMMARY,
            Apps.MOST_RECENT_CHECK_IN,
            Apps.ID_CARD_PRINTING_STATUS

    );

    public static final List<String> HIV_DASHBOARD_FIRST_COLUMN_ORDER = Arrays.asList(
            Apps.HIV_PATIENT_PROGRAM_SUMMARY,
            Apps.HIV_OBS_CHART
    );

    public static final List<String> HIV_DASHBOARD_SECOND_COLUMN_ORDER = Arrays.asList(
            Apps.HIV_CD4_GRAPH,
            Apps.HIV_WEIGHT_GRAPH,
            Apps.HIV_PATIENT_PROGRAM_HISTORY
    );
}
