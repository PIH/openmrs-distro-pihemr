package org.openmrs.module.mirebalais.apploader;

import org.openmrs.module.allergyui.AllergyUIConstants;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.pihcore.metadata.core.program.ANCProgram;
import org.openmrs.module.pihcore.metadata.core.program.AsthmaProgram;
import org.openmrs.module.pihcore.metadata.core.program.DiabetesProgram;
import org.openmrs.module.pihcore.metadata.core.program.EpilepsyProgram;
import org.openmrs.module.pihcore.metadata.core.program.HIVProgram;
import org.openmrs.module.pihcore.metadata.core.program.HypertensionProgram;
import org.openmrs.module.pihcore.metadata.core.program.MalnutritionProgram;
import org.openmrs.module.pihcore.metadata.core.program.MentalHealthProgram;
import org.openmrs.module.pihcore.metadata.core.program.NCDProgram;
import org.openmrs.module.pihcore.metadata.core.program.MCHProgram;
import org.openmrs.module.pihcore.metadata.core.program.ZikaProgram;
import org.openmrs.module.pihcore.metadata.core.program.Covid19Program;
import org.openmrs.module.pihcore.metadata.core.program.OncologyProgram;
import org.openmrs.module.registrationapp.RegistrationAppConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        public static final String DISPENSING_SUMMARY = "pih.app.dispensing.summary";
        public static final String HIV_DISPENSING_SUMMARY = "pih.app.hiv.dispensing.summary";
        public static final String HIV_DIAGNOSES_SUMMARY = "pih.app.hiv.diagnoses.summary";
        public static final String VITALS_SUMMARY = "pih.app.vitals.summary";
        public static final String SCHEDULE_APPOINTMENT = "appointmentschedulingui.schedulingAppointmentApp";
        public static final String MY_ACCOUNT = "emr.myAccount";
        public static final String REPORTS = "reportingui.reports";
        public static final String INPATIENTS = "mirebalaisreports.inpatients";
        public static final String ACTIVE_VISITS_LIST = "mirebalaisreports.activeVisitsList";
        public static final String PATIENT_REGISTRATION = "registrationapp.registerPatient";
        public static final String CLINICIAN_DASHBOARD = "pih.app.clinicianDashboard";
        public static final String VISITS_SUMMARY = "coreapps.clinicianfacing.visits";
        public static final String HOME_VISITS_SUMMARY = "coreapps.home.visits";
        public static final String WAITING_FOR_CONSULT = "pih.app.waitingForConsult";
        public static final String CHW_MGMT = "chw.app.mgmt";
        public static final String ED_TRIAGE = "edtriageapp.app.edTriage";
        public static final String ED_TRIAGE_QUEUE = "edtriageapp.app.triageQueue";
        public static final String EPILEPSY_SUMMARY = "pih.app.epilepsy.summary";
        public static final String EPILEPSY_SEIZURES = "pih.app.epilepsy.seizures";
        public static final String TODAYS_VISITS = "pih.app.todaysVisits";
        public static final String PATHOLOGY_TRACKING = "labtracking.app.monitorOrders";
        public static final String LAB_TRACKING = "pih.app.labtracking";
        public static final String LABS = "pih.app.labs.label";
        public static final String ORDER_LABS = "pih.app.labs.ordering";
        public static final String PROGRAMS_LIST = "coreapps.app.programsList";
        public static final String RELATIONSHIPS_REGISTRATION_SUMMARY = "pih.app.relationships.registrationSummary";
        public static final String PROVIDER_RELATIONSHIPS_REGISTRATION_SUMMARY = "pih.app.relationships.providers.registrationSummary";
        public static final String RELATIONSHIPS_CLINICAL_SUMMARY = "pih.app.relationships.clinicalSummary";
        public static final String PROVIDER_RELATIONSHIPS_CLINICAL_SUMMARY = "pih.app.relationships.providers.clinicalSummary";
        public static final String PROGRAM_SUMMARY_LIST = "pih.app.programSummaryList";
        public static final String HIV_SUMMARY = "pih.app.hiv.summary";
        public static final String HIV_NEXT_DISPENSING = "pih.app.hiv.next.dispensing";
        public static final String HIV_ALERTS = "pih.app.hiv.alerts";
        public static final String HIV_CD4_GRAPH = "pih.app.hiv.cd4Graph";
        public static final String HIV_VL_GRAPH = "pih.app.hiv.vlGraph";
        public static final String HIV_OBS_CHART = "pih.app.hiv.obsChart";
        public static final String HIV_CONDITION_LIST = "pih.app.hiv.conditionList";
        public static final String HIV_VISIT_SUMMARY = "pih.app.hiv.visitSummary";
        public static final String HIV_LAST_VITALS = "pih.app.hiv.lastVitals";
        public static final String HIV_DISPENSING = "pih.app.hiv.dispensing";
        public static final String PATIENT_DOCUMENTS = "attachments.app.patientDocuments";
        public static final String CONDITION_LIST = "coreapps.conditionList";
        public static final String ACTIVE_DRUG_ORDERS = "coreapps.activeDrugOrders";
        public static final String BLOOD_PRESSURE_GRAPH = "pih.app.bloodPressure.graph";
        public static final String BLOOD_PRESSURE_OBS_TABLE = "pih.app.bloodPressure.obsTable";
        public static final String ASTHMA_SYMPTOMS_OBS_TABLE = "pih.app.asthma.symptomsObsTable";
        public static final String GLUCOSE_GRAPH = "pih.app.glucoseGraph";
        public static final String HBA1C_GRAPH = "pih.app.hba1cGraph";
        public static final String HEAD_CIRCUMFERENCE_GRAPH = "pih.app.headCircumferenceGraph";
        public static final String BMI_GRAPH = "pih.app.bmiGraph";
        public static final String ABDOMINAL_CIRCUMFERENCE_GRAPH = "pih.app.abdominalCircumferenceGraph";
        public static final String FOOT_EXAM_OBS_TABLE = "pih.app.diabetes.footExamObsTable";
        public static final String URINARY_ALBUMIN_OBS_TABLE = "pih.app.diabetes.urinaryAlbuminObsTable";
        public static final String ALC_TOBAC_USE_SUMMARY = "pih.app.alcTobacUse.summary";
        public static final String CHOLESTEROL_GRAPH = "pih.app.cholesterol.graph";
        public static final String PHQ9_GRAPH = "pih.app.phq9.graph";
        public static final String GAD7_GRAPH = "pih.app.gad7.graph";
        public static final String WHODAS_GRAPH = "pih.app.whodas.graph";
        public static final String ZLDSI_GRAPH = "pih.app.zldsi.graph";
        public static final String SEIZURE_FREQUENCY_GRAPH = "pih.app.seizure.frequency.graph.title";
        public static final String J9_REFERRALS = "pih.app.j9Referrals";
        public static final String COVID_LAB_RESULTS = "pih.app.covidLabResults";
        public static final String ADD_LAB_RESULTS = "pih.app.addLabResults";

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
        public static final String MOST_RECENT_REGISTRATION_EBOLA_SCREENING = "mirebalais.mostRecentRegistrationEbolaScreening";
        public static final String MOST_RECENT_REGISTRATION_INSURANCE = "coreapps.mostRecentRegistrationInsurance";
        public static final String MOST_RECENT_REGISTRATION_CONTACT = "coreapps.mostRecentRegistrationContact";
        public static final String MOST_RECENT_CHECK_IN = "coreapps,mostRecentCheckIn";
        public static final String ALLERGY_SUMMARY = "allergyui.allergySummary";
        public static final String PATHOLOGY_SUMMARY = "labtrackingapp.labSummary";
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
        public static final String LAB_RESULTS_OVERALL_ACTION = "pih.form.labResults";
        public static final String NCD_INITIAL_VISIT_ACTION = "pih.form.ncdAdultInitial";
        public static final String NCD_FOLLOWUP_VISIT_ACTION = "pih.form.ncdAdultFollowup";
        public static final String ECHO_VISIT_ACTION = "pih.form.echoConsult";
        public static final String VACCINATION_VISIT_ACTION = "pih.form.vaccination";

        public static final String COVID19_INITIAL_VISIT_ACTION = "pih.form.covid19Admission";
        public static final String COVID19_FOLLOWUP_VISIT_ACTION = "pih.form.covid19Progress";
        public static final String COVID19_DISCHARGE_VISIT_ACTION = "pih.form.covid19Discharge";

        public static final String HIV_ZL_INITIAL_VISIT_ACTION = "pih.form.hivZLAdultInitial";
        public static final String HIV_ZL_FOLLOWUP_VISIT_ACTION = "pih.form.hivZLAdultFollowup";
        public static final String HIV_ZL_DISPENSING_VISIT_ACTION = "pih.form.hivZLDispensing";

        public static final String OVC_INITIAL_VISIT_ACTION = "pih.form.ovcInitial";
        public static final String OVC_FOLLOWUP_VISIT_ACTION = "pih.form.ovcFollowup";

        public static final String TB_INITIAL_VISIT_ACTION = "pih.form.tbInitial";

        public static final String HIV_ADULT_INITIAL_VISIT_ACTION = "pih.form.hivAdultInitial";
        public static final String HIV_ADULT_FOLLOWUP_VISIT_ACTION = "pih.form.hivAdultFollowup";
        public static final String HIV_PEDS_INITIAL_VISIT_ACTION = "pih.form.hivPedsInitial";
        public static final String HIV_PEDS_FOLLOWUP_VISIT_ACTION = "pih.form.hivPedsFollowup";
        public static final String HIV_ADHERENCE_VISIT_ACTION = "pih.form.hivAdherence";
        public static final String MCH_ANC_INTAKE_VISIT_ACTION = "pih.form.ancIntake";
        public static final String MCH_ANC_FOLLOWUP_VISIT_ACTION = "pih.form.ancFollowup";
        public static final String MCH_PEDS_ACTION = "pih.form.peds";
        public static final String MCH_DELIVERY_VISIT_ACTION = "pih.form.delivery";
        public static final String OB_GYN_VISIT_ACTION = "pih.form.obGyn";
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
        public static final String CHEMO_ORDERING_VISIT_ACTION = "oncology.orderChemo";
        public static final String CHEMO_RECORDING_VISIT_ACTION = "oncology.recordChemo";
        public static final String MEXICO_CONSULT_ACTION = "pih.mexicoConsult";
        public static final String SIERRA_LEONE_OUTPATIENT_INITIAL_VISIT_ACTION = "pih.sierraLeone.outpatient.initial";
        public static final String SIERRA_LEONE_OUTPATIENT_FOLLOWUP_VISIT_ACTION = "pih.sierraLeone.outpatient.followup";

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
        public static final String ORDER_LABS_OVERALL_ACTION = "orderentryowa.orderLabs";
        public static final String VIEW_LABS_OVERALL_ACTION = "labworkflowowa.viewLabs";
        public static final String VIEW_GROWTH_CHART_ACTION = "growthchart.viewChart";
        public static final String MARK_PATIENT_DEAD_OVERALL_ACTION = "coreapps.markPatientDied";

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
            Apps.HIV_DISPENSING,
            Apps.LABS,
            Apps.PATHOLOGY_TRACKING,
            Apps.ADD_LAB_RESULTS,
            Apps.PROGRAM_SUMMARY_LIST,
            Apps.REPORTS,
            Apps.DISPENSING,
            Apps.ED_TRIAGE,
            Apps.ED_TRIAGE_QUEUE,
            Apps.CHW_MGMT,
            Apps.COHORT_BUILDER_APP,
            Apps.J9_REFERRALS,
            Apps.LEGACY_MPI,
            Apps.MY_ACCOUNT,
            Apps.SYSTEM_ADMINISTRATION);

    // The idiosyncratic ordering of these items is due to the fact that the ones used
    // in English and French -speaking places are alphebetized in English and the ones
    // used in Spanish-speaking places are alphebetized in Spanish.
    public static final List<String> PROGRAM_SUMMARY_LIST_APPS_ORDER = Arrays.asList(
            "pih.app." + AsthmaProgram.ASTHMA.uuid() + ".programSummary.dashboard",
            "pih.app." + MalnutritionProgram.MALNUTRITION.uuid() + ".programSummary.dashboard",
            "pih.app." + DiabetesProgram.DIABETES.uuid() + ".programSummary.dashboard",
            "pih.app." + EpilepsyProgram.EPILEPSY.uuid() + ".programSummary.dashboard",
            "pih.app." + HIVProgram.HIV.uuid() + ".programSummary.dashboard",
            "pih.app." + HypertensionProgram.HYPERTENSION.uuid() + ".programSummary.dashboard",
            "pih.app." + ANCProgram.ANC.uuid() + ".programSummary.dashboard",
            "pih.app." + MCHProgram.MCH.uuid() + ".programSummary.dashboard",
            "pih.app." + MentalHealthProgram.MENTAL_HEALTH.uuid() + ".programSummary.dashboard",
            "pih.app." + NCDProgram.NCD.uuid() + ".programSummary.dashboard",
            "pih.app." + OncologyProgram.ONCOLOGY.uuid() + ".programSummary.dashboard",
            "pih.app." + Covid19Program.COVID19.uuid() + ".programSummary.dashboard",
            "pih.app." + ZikaProgram.ZIKA.uuid() + ".programSummary.dashboard"
    );

    public static final List<String> SYSTEM_ADMINISTRATION_APPS_ORDER = Arrays.asList(
            Apps.MANAGE_ACCOUNTS,
            Apps.PRINTER_ADMINISTRATION,
            Apps.MERGE_PATIENTS,
            Apps.FEATURE_TOGGLES);

    public static final List<String> OVERALL_ACTIONS_ORDER = Arrays.asList(
            Extensions.CREATE_VISIT_OVERALL_ACTION,
            Extensions.CREATE_RETROSPECTIVE_VISIT_OVERALL_ACTION,
            Extensions.VIEW_GROWTH_CHART_ACTION,
            Extensions.ORDER_LABS_OVERALL_ACTION,
            Extensions.VIEW_LABS_OVERALL_ACTION,
            Extensions.LAB_RESULTS_OVERALL_ACTION,
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
            Extensions.CHEMO_ORDERING_VISIT_ACTION,
            Extensions.EDIT_PATIENT_DEMOGRAPHICS,
            Extensions.EDIT_PATIENT_CONTACT_INFO,
            Extensions.CHART_SEARCH_OVERALL_ACTION,
            Extensions.MARK_PATIENT_DEAD_OVERALL_ACTION);  // TODO remember to permission chart search in Custom App Loader Factory

    public static final List<String> VISIT_ACTIONS_ORDER = Arrays.asList(
            Extensions.CHECK_IN_VISIT_ACTION,
            Extensions.VITALS_CAPTURE_VISIT_ACTION,
            Extensions.CONSULT_NOTE_VISIT_ACTION,
            Extensions.MEXICO_CONSULT_ACTION,
            Extensions.ADMISSION_NOTE_VISIT_ACTION, // ToDo:  Don't think this is in use from action list
            Extensions.PRIMARY_CARE_ADULT_INITIAL_VISIT_ACTION,
            Extensions.PRIMARY_CARE_ADULT_FOLLOWUP_VISIT_ACTION,
            Extensions.PRIMARY_CARE_PEDS_INITIAL_VISIT_ACTION,
            Extensions.PRIMARY_CARE_PEDS_FOLLOWUP_VISIT_ACTION,
            Extensions.VACCINATION_VISIT_ACTION,
            Extensions.ED_TRIAGE_VISIT_ACTION,
            Extensions.ED_CONSULT_NOTE_VISIT_ACTION,
            Extensions.DISPENSE_MEDICATION_VISIT_ACTION,
            Extensions.SURGICAL_NOTE_VISIT_ACTION,
            Extensions.NCD_INITIAL_VISIT_ACTION,
            Extensions.NCD_FOLLOWUP_VISIT_ACTION,
            Extensions.ECHO_VISIT_ACTION,
            Extensions.MCH_ANC_INTAKE_VISIT_ACTION,
            Extensions.MCH_ANC_FOLLOWUP_VISIT_ACTION,
            Extensions.MCH_DELIVERY_VISIT_ACTION,
            Extensions.OB_GYN_VISIT_ACTION,
            Extensions.MENTAL_HEALTH_VISIT_ACTION,
            Extensions.SOCIO_ECONOMICS_VISIT_ACTION,
            Extensions.VCT_VISIT_ACTION,
            Extensions.HIV_ZL_INITIAL_VISIT_ACTION,
            Extensions.HIV_ZL_FOLLOWUP_VISIT_ACTION,
            Extensions.HIV_ZL_DISPENSING_VISIT_ACTION,
            Extensions.HIV_ADHERENCE_VISIT_ACTION,
            Extensions.ONCOLOGY_CONSULT_NOTE_VISIT_ACTION,
            Extensions.ONCOLOGY_INITIAL_VISIT_ACTION,
            Extensions.CHEMOTHERAPY_VISIT_ACTION,
            Extensions.CHEMO_RECORDING_VISIT_ACTION,
            Extensions.COVID19_INITIAL_VISIT_ACTION,
            Extensions.COVID19_FOLLOWUP_VISIT_ACTION,
            Extensions.COVID19_DISCHARGE_VISIT_ACTION,
            Extensions.ORDER_XRAY_VISIT_ACTION,
            Extensions.ORDER_CT_VISIT_ACTION,
            Extensions.ORDER_ULTRASOUND_VISIT_ACTION,
            Extensions.ORDER_LAB_VISIT_ACTION);

    public static final List<String> HIV_VISIT_ACTIONS_ORDER = Arrays.asList(
            Extensions.HIV_ZL_INITIAL_VISIT_ACTION + ".hiv",
            Extensions.HIV_ZL_FOLLOWUP_VISIT_ACTION + ".hiv",
            Extensions.HIV_ZL_DISPENSING_VISIT_ACTION + ".hiv",
            Extensions.VITALS_CAPTURE_VISIT_ACTION + ".hiv");

    public static final List<String> ONCOLOGY_VISIT_ACTIONS_ORDER = Arrays.asList(
            Extensions.CHEMO_RECORDING_VISIT_ACTION + "oncology"
    );

    public static final List<String> ONCOLOGY_OVERALL_ACTIONS_ORDER = Arrays.asList(
            Extensions.CHEMO_ORDERING_VISIT_ACTION + ".oncology"
    );

    public static final List<String> AWAITING_ADMISSION_ACTIONS_ORDER = Arrays.asList(
            Extensions.ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
            Extensions.DENY_ADMISSION_FORM_AWAITING_ADMISSION_ACTION);

    public static final List<String> CLINICIAN_DASHBOARD_FIRST_COLUMN_ORDER = Arrays.asList(
            Apps.CONDITION_LIST,
            Apps.VISITS_SUMMARY,
            Apps.HOME_VISITS_SUMMARY,
            Apps.COVID_LAB_RESULTS,
            Apps.APPOINTMENT_SCHEDULING_HOME,
            Apps.PROVIDER_RELATIONSHIPS_CLINICAL_SUMMARY,
            Apps.RADIOLOGY_APP,
            Apps.RADIOLOGY_ORDERS_APP,
            Apps.BMI_GRAPH + ExtensionPoints.CLINICIAN_DASHBOARD_FIRST_COLUMN,
            Apps.VITALS_SUMMARY,
            Apps.DISPENSING_SUMMARY
    );

    public static final List<String> CLINICIAN_DASHBOARD_SECOND_COLUMN_ORDER = Arrays.asList(
            Apps.ALLERGY_SUMMARY,
            Apps.MOST_RECENT_VITALS,
            Apps.PATHOLOGY_SUMMARY,
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
            Apps.PROVIDER_RELATIONSHIPS_REGISTRATION_SUMMARY,
            Apps.RELATIONSHIPS_REGISTRATION_SUMMARY,
            Apps.MOST_RECENT_CHECK_IN,
            Apps.ID_CARD_PRINTING_STATUS

    );

    public static final Map<String, List<String>> PROGRAM_DASHBOARD_FIRST_COLUMN_ORDER;

    static {
        Map<String, List<String>> PROGRAM_DASHBOARD_FIRST_COLUMN_ORDER_TEMP = new HashMap<String, List<String>>();

        PROGRAM_DASHBOARD_FIRST_COLUMN_ORDER_TEMP.put(HIVProgram.HIV.uuid(),
                Arrays.asList(
                    // ToDo:  Move Program enrollment is first?
                    "pih.app." + HIVProgram.HIV.uuid() + "patientProgramSummary",
                    "pih.app." + HIVProgram.HIV.uuid() + ".patientProgramHistory",
                    Apps.HIV_SUMMARY,
                    Apps.HIV_VL_GRAPH,
                    Apps.HIV_NEXT_DISPENSING,
                    Apps.HIV_DISPENSING_SUMMARY
                ));

        PROGRAM_DASHBOARD_FIRST_COLUMN_ORDER = Collections.unmodifiableMap(PROGRAM_DASHBOARD_FIRST_COLUMN_ORDER_TEMP);
    }

    public static final Map<String, List<String>> PROGRAM_DASHBOARD_SECOND_COLUMN_ORDER;

    static {
        Map<String, List<String>> PROGRAM_DASHBOARD_SECOND_COLUMN_ORDER_TEMP = new HashMap<String, List<String>>();

        PROGRAM_DASHBOARD_SECOND_COLUMN_ORDER_TEMP.put(HIVProgram.HIV.uuid(),
                Arrays.asList(
                        Apps.HIV_VISIT_SUMMARY,
                        Apps.HIV_DIAGNOSES_SUMMARY,
                        // Adverse events & allergies
                        Apps.BMI_GRAPH,
                        Apps.ALLERGY_SUMMARY
                ));

        PROGRAM_DASHBOARD_SECOND_COLUMN_ORDER = Collections.unmodifiableMap(PROGRAM_DASHBOARD_SECOND_COLUMN_ORDER_TEMP);
    }

}
