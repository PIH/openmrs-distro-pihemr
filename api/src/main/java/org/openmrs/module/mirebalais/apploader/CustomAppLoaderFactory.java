package org.openmrs.module.mirebalais.apploader;

import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.AppTemplate;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.factory.AppFrameworkFactory;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.CoreMetadata;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.RadiologyMetadata;
import org.openmrs.module.mirebalaisreports.MirebalaisReportsProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ACTIVE_VISITS_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ADMISSION_FORM_AWAITING_ADMISSION_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ADMISSION_NOTE_VISIT_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ALL_PATIENTS_WITH_IDS_DATA_EXPORT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.APPOINTMENTS_DATA_EXPORT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.APPOINTMENTS_TAB;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.APPOINTMENT_SCHEDULING_HOME_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ARCHIVES_ROOM_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.AWAITING_ADMISSION_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.CHECK_IN_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.CHECK_IN_VISIT_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.CONSULT_ENCOUNTER_TEMPLATE;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.CONSULT_NOTE_VISIT_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.CREATE_RETROSPECTIVE_VISIT_OVERALL_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.CREATE_VISIT_OVERALL_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DAILY_CHECK_INS_OVERVIEW_REPORT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DAILY_CLINICAL_ENCOUNTERS_OVERVIEW_REPORT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DAILY_INPATIENTS_OVERVIEW_REPORT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DAILY_REGISTRATIONS_OVERVIEW_REPORT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DASHBOARD_INCLUDE_FRAGMENTS_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DEATH_CERTIFICATE_HEADER_EXTENSION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DEATH_CERTIFICATE_VISIT_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DEATH_INFO_HEADER_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DEFAULT_ENCOUNTER_TEMPLATE;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DEFAULT_PRINTERS_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DENY_ADMISSION_FORM_AWAITING_ADMISSION_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DISPENSE_MEDICATION_VISIT_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DISPENSING_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ED_CONSULT_NOTE_VISIT_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.FEATURE_TOGGLES_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.INPATIENTS_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.LEGACY_MPI_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.LEGACY_PATIENT_LOOKUP_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.LEGACY_PATIENT_REGISTRATION_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.LEGACY_PATIENT_REGISTRATION_ED_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.LQAS_DATA_EXPORT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.MANAGE_ACCOUNTS_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.MANAGE_PRINTERS_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.MERGE_PATIENTS_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.MERGE_VISITS_OVERALL_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.MONTHLY_INPATIENTS_OVERVIEW_REPORT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.MY_ACCOUNT_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.NON_CODED_DIAGNOSES_DATA_QUALITY_REPORT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.NO_DETAILS_ENCOUNTER_TEMPLATE;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ORDER_CT_VISIT_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ORDER_ULTRASOUND_VISIT_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ORDER_XRAY_VISIT_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.PAPER_RECORD_ACTIONS_INCLUDES;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.PIH_HEADER_EXTENSION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.PRINT_ID_CARD_OVERALL_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.PRINT_PAPER_FORM_LABEL_OVERALL_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.PRINT_WRISTBAND_ACTION_INCLUDES;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.PRINT_WRISTBAND_OVERALL_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.RADIOLOGY_TAB;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REGISTER_TEST_PATIENT_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTING_AD_HOC_ANALYSIS;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTING_DATA_EXPORT_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTING_DATA_QUALITY_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTING_OVERVIEW_REPORTS_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTING_OVERVIEW_REPORTS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTS_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REQUEST_APPOINTMENT_OVERALL_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REQUEST_PAPER_RECORD_OVERALL_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.SCHEDULE_APPOINTMENT_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.SURGICAL_NOTE_VISIT_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.SYSTEM_ADMINISTRATION_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.USERS_AND_PROVIDERS_DATA_EXPORT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.VISIT_ACTIONS_INCLUDES;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.VITALS_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.VITALS_CAPTURE_VISIT_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addFeatureToggleToApp;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addFeatureToggleToExtension;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToHomePage;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToOverallActions;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToSystemAdministrationPage;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.app;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.arrayNode;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.awaitingAdmissionAction;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.dashboardTab;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.dataExport;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.encounterTemplate;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.extension;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.findPatientTemplateApp;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.fragmentExtension;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.header;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.map;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.objectNode;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.overallAction;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.overviewReport;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.registerTemplateForEncounterType;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.simpleHtmlFormLink;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.standardHtmlFormLink;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.visitAction;

@Component
public class CustomAppLoaderFactory implements AppFrameworkFactory {

    private List<AppDescriptor> apps;

    private List<Extension> extensions;

    // TODO have this be set to true on a context refresh?  would also need to trigger another a context refresh
    private Boolean needsRefresh = true;

    @Override
    public List<AppDescriptor> getAppDescriptors() throws IOException {
        if (needsRefresh) {
            loadAppsAndExtensions();
        }
        return apps;
    }

    @Override
    public List<Extension> getExtensions() throws IOException {
        if (needsRefresh) {
            loadAppsAndExtensions();
        }
        return extensions;
    }

    @Override
    public List<AppTemplate> getAppTemplates() throws IOException {
        return null;
    }


    private void loadAppsAndExtensions() {

        apps = new ArrayList<AppDescriptor>();
        extensions = new ArrayList<Extension>();

        configureHeader();
        setupDefaultEncounterTemplates();
        enableVisitManagement();
        enableActiveVisits();
        enableCheckIn();
        enableVitals();
        enableConsult();
        enableADT();
        enableDeathCertificate();
        enableRadiology();
        enableDispensing();
        enableSurgery();
        enableReports();
        enableArchives();
        enableWristbands();
        enableAppointmentScheduling();
        enableSystemAdministration();
        enableMyAccount();

        enableLegacyMPI();
        enableLegacyPatientRegistration();
        registerLacollinePatientRegistrationEncounterTypes();

        needsRefresh = false;
    }

    private void configureHeader(){
        extensions.add(header(PIH_HEADER_EXTENSION, "/ms/uiframework/resource/mirebalais/images/partners_in_health_logo.png"));
    }

    private void setupDefaultEncounterTemplates() {

        extensions.add(encounterTemplate(DEFAULT_ENCOUNTER_TEMPLATE,
                "coreapps",
                "patientdashboard/encountertemplate/defaultEncounterTemplate"));

        extensions.add(encounterTemplate(NO_DETAILS_ENCOUNTER_TEMPLATE,
                "coreapps",
                "patientdashboard/encountertemplate/noDetailsEncounterTemplate"));

    }

    private void enableVisitManagement() {

        extensions.add(overallAction(CREATE_VISIT_OVERALL_ACTION,
                "coreapps.task.startVisit.label",
                "icon-check-in",
                "script",
                "visit.showQuickVisitCreationDialog({{patient.patientId}})",
                "Task: coreapps.createVisit",
                "!visit && !patient.person.dead"));

        extensions.add(overallAction(CREATE_RETROSPECTIVE_VISIT_OVERALL_ACTION,
                "coreapps.task.createRetrospectiveVisit.label",
                "icon-plus",
                "script",
                "visit.showRetrospectiveVisitCreationDialog()",
                "Task: coreapps.createRetrospectiveVisit",
                null));

        extensions.add(overallAction(MERGE_VISITS_OVERALL_ACTION,
                "coreapps.task.mergeVisits.label",
                "icon-link",
                "link",
                "coreapps/mergeVisits.page?patientId={{patient.uuid}}",
                "Task: coreapps.mergeVisits",
                null));

        // this provides the javascript the backs the three overall action buttons
        extensions.add(fragmentExtension(VISIT_ACTIONS_INCLUDES,
                "coreapps",
                "patientdashboard/visitIncludes",
                null,
                DASHBOARD_INCLUDE_FRAGMENTS_EXTENSION_POINT));

    }

    private void enableActiveVisits() {

        apps.add(addToHomePage(app(ACTIVE_VISITS_APP,
                "coreapps.activeVisits.app.label",
                "icon-check-in",
                "coreapps/activeVisits.page?app=mirebalais.activeVisits",
                "App: org.openmrs.module.coreapps.activeVisits",
                objectNode("patientPageUrl", "App: org.openmrs.module.coreapps.activeVisits"))));

    }

    private void enableCheckIn() {

        apps.add(addToHomePage(findPatientTemplateApp(CHECK_IN_APP,
                "mirebalais.app.patientRegistration.checkin.label",
                "icon-paste",
                "App: mirebalais.checkin",
                "/mirebalais/checkin/checkin.page?patientId={{patientId}}",
                null)));

        extensions.add(visitAction(CHECK_IN_VISIT_ACTION,
                "mirebalais.task.checkin.label",
                "icon-check-in",
                "link",
                simpleHtmlFormLink("mirebalais:htmlforms/checkin.xml"),
                "Task: mirebalais.checkinForm",
                null));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.CHECK_IN,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-check-in");
    }

    private void enableVitals() {

        apps.add(addToHomePage(findPatientTemplateApp(VITALS_APP,
                "mirebalais.outpatientVitals.title",
                "icon-vitals",
                "App: mirebalais.outpatientVitals",
                "/mirebalais/outpatientvitals/patient.page?patientId={{patientId}}",
                null)));

        extensions.add(visitAction(VITALS_CAPTURE_VISIT_ACTION,
                "mirebalais.task.vitals.label",
                "icon-vitals",
                "link",
                simpleHtmlFormLink("mirebalais:htmlforms/vitals.xml"),
                "Task: emr.enterClinicalForms",
                "visit != null && visit.active"));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.VITALS,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-vitals");

    }

    private void enableConsult() {

        extensions.add(visitAction(CONSULT_NOTE_VISIT_ACTION,
                "emr.clinic.consult.title",
                "icon-stethoscope",
                "link",
                standardHtmlFormLink("mirebalais:htmlforms/outpatientConsult.xml&returnProvider=emr&returnPage=disposition/dispositionAction"),
                null,
                "(user.get('fn').hasPrivilege('Task: emr.enterClinicalForms') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: emr.retroConsultNote') || (visit != null && (Date.now () - visit.stopDatetimeInMilliseconds)/(1000 * 60 * 60 * 24) <30 &&  user.get('fn').hasPrivilege('Task: emr.retroConsultNoteThisProviderOnly'))"));

        extensions.add(visitAction(ED_CONSULT_NOTE_VISIT_ACTION,
                "emr.ed.consult.title",
                "icon-stethoscope",
                "link",
                standardHtmlFormLink("mirebalais:htmlforms/edNote.xml&returnProvider=emr&returnPage=disposition/dispositionAction"),
                null,
                "(user.get('fn').hasPrivilege('Task: emr.enterClinicalForms') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: emr.retroConsultNote') || (visit != null && (Date.now () - visit.stopDatetimeInMilliseconds)/(1000 * 60 * 60 * 24) <30 &&  user.get('fn').hasPrivilege('Task: emr.retroConsultNoteThisProviderOnly'))"));

        extensions.add(encounterTemplate(CONSULT_ENCOUNTER_TEMPLATE, "mirebalais", "patientdashboard/encountertemplate/consultEncounterTemplate"));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.VITALS,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-stethoscope", null, true, null);
    }

    private void enableADT() {

        apps.add(addToHomePage(app(AWAITING_ADMISSION_APP,
                "coreapps.app.awaitingAdmission.label",
                "icon-list-ul",
                "coreapps/adt/awaitingAdmission.page",
                "App: coreapps.awaitingAdmission",
                objectNode("patientPageUrl", "/coreapps/patientdashboard/patientDashboard.page?patientId={{patientId}}"))));

        apps.add(addToHomePage(app(INPATIENTS_APP,
                "mirebalaisreports.app.inpatients.label",
                "icon-list-ol",
                "mirebalaisreports/inpatientList.page",
                "App: emr.inpatients",
                null)));

        extensions.add(awaitingAdmissionAction(ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
                "mirebalais.task.admit.label",
                "icon-h-sign",
                "link",
                standardHtmlFormLink("mirebalais:htmlforms/admissionNote.xml&returnProvider=coreapps&returnPage=adt/awaitingAdmission&returnLabel=coreapps.app.awaitingAdmission.label"),
                "Task: emr.enterClinicalForms",
                null));

        extensions.add(awaitingAdmissionAction(DENY_ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
                "uicommons.cancel",
                "icon-remove",
                "link",
                standardHtmlFormLink("mirebalais:htmlforms/cancelAdmission.xml&returnProvider=coreapps&returnPage=adt/awaitingAdmission"),
                "Task: emr.enterClinicalForms",
                null));

        extensions.add(visitAction(ADMISSION_NOTE_VISIT_ACTION,
                "mirebalais.task.admit.label",
                "icon-h-sign",
                "link",
                standardHtmlFormLink("mirebalais:htmlforms/admissionNote.xml"),
                null,
                "(user.get('fn').hasPrivilege('Task: emr.enterClinicalForms') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: emr.retroConsultNote') || (visit != null && (Date.now () - visit.stopDatetimeInMilliseconds)/(1000 * 60 * 60 * 24) <30 &&  user.get('fn').hasPrivilege('Task: emr.retroConsultNoteThisProviderOnly'))"));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.ADMISSION,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-signin", null, true, null);

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.CANCEL_ADMISSION,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-ban-circle", true, true, null);

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.TRANSFER,
                findExtensionById(NO_DETAILS_ENCOUNTER_TEMPLATE), "icon-share", null, true, null);

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.EXIT_FROM_CARE,
                findExtensionById(NO_DETAILS_ENCOUNTER_TEMPLATE), "icon-signout", null, true, null);
    }

    private void enableDeathCertificate() {

        extensions.add(visitAction(DEATH_CERTIFICATE_VISIT_ACTION,
                "mirebalais.deathCertificate.enter.label",
                "icon-remove-circle",
                "link",
                simpleHtmlFormLink("mirebalais:htmlforms/deathCertificate.xml"),
                "Task: mirebalais.enterDeathCertificate",
                "!patient.person.dead"
        ));

        extensions.add(fragmentExtension(DEATH_CERTIFICATE_HEADER_EXTENSION,
                "mirebalais",
                "deathcertificate/headerLink",
                "Task: mirebalais.enterDeathCertificate",
                DEATH_INFO_HEADER_EXTENSION_POINT));

        addFeatureToggleToExtension(findExtensionById(DEATH_CERTIFICATE_VISIT_ACTION), "deathNote");
        addFeatureToggleToExtension(findExtensionById(DEATH_CERTIFICATE_HEADER_EXTENSION), "deathNote");
    }

    private void enableRadiology() {

        extensions.add(dashboardTab(RADIOLOGY_TAB,
                "radiologyapp.radiology.label",
                "Task: org.openmrs.module.radiologyapp.tab",
                "radiologyapp",
                "radiologyTab"));

        extensions.add(visitAction(ORDER_XRAY_VISIT_ACTION,
                "radiologyapp.task.order.CR.label",
                "icon-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=CR",
                null,
                "(user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.orderXray') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.retroOrder')"));

        extensions.add(visitAction(ORDER_CT_VISIT_ACTION,
                "radiologyapp.task.order.CT.label",
                "icon-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=Ct",
                null,
                "(user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.orderCT') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.retroOrder')"));

        extensions.add(visitAction(ORDER_ULTRASOUND_VISIT_ACTION,
                "radiologyapp.task.order.US.label",
                "icon-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=US",
                null,
                "(user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.orderUS') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.retroOrder')"));

        registerTemplateForEncounterType(RadiologyMetadata.EncounterTypes.RADIOLOGY_ORDER,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-x-ray");

        registerTemplateForEncounterType(RadiologyMetadata.EncounterTypes.RADIOLOGY_STUDY,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-x-ray");

        registerTemplateForEncounterType(RadiologyMetadata.EncounterTypes.RADIOLOGY_REPORT,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-x-ray");
    }

    private void enableDispensing() {

        // TODO change this to use the coreapps find patient app?
        apps.add(addToHomePage(app(DISPENSING_APP,
                "dispensing.app.label",
                "icon-medicine",
                "dispensing/findPatient.page",
                "App: dispensing.app.dispense",
                null)));

        extensions.add(visitAction(DISPENSE_MEDICATION_VISIT_ACTION,
                "dispensing.app.label",
                "icon-medicine",
                "link",
                standardHtmlFormLink("dispensing:htmlforms/dispensing.xml"),
                "Task: mirebalais.dispensing",
                null));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.MEDICATION_DISPENSED,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-medicine", true, true, "bad21515-fd04-4ff6-bfcd-78456d12f168");

    }

    private void enableSurgery() {

        extensions.add(visitAction(SURGICAL_NOTE_VISIT_ACTION,
                "mirebalais.task.surgicalOperativeNote.label",
                "icon-paste",
                "link",
                standardHtmlFormLink("mirebalais:htmlforms/surgicalPostOpNote.xml"),
                "Task: emr.enterClinicalForms",
                null));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.POST_OPERATIVE_NOTE,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-paste", true, true, "9b135b19-7ebe-4a51-aea2-69a53f9383af");
        }

    private void enableReports() {

        // TODO move all the other reporting stuff in here?
        // TODO do we need to explicitly add the extension points?

        apps.add(addToHomePage(app(REPORTS_APP,
                "reportingui.reportsapp.home.title",
                "icon-list-alt",
                "reportingui/reportsapp/home.page",
                "App: reportingui.reports",
                null)));

        extensions.add(extension(REPORTING_AD_HOC_ANALYSIS,
                "reportingui.adHocAnalysis.label",
                null,
                "link",
                "reportingui/adHocManage.page",
                "App: reportingui.adHocAnalysis",
                null,
                REPORTING_DATA_EXPORT_EXTENSION_POINT,
                9999,
                null));

        extensions.add(overviewReport(DAILY_REGISTRATIONS_OVERVIEW_REPORT,
                "mirebalaisreports.dailyRegistrations.name",
                MirebalaisReportsProperties.DAILY_REGISTRATIONS_REPORT_DEFINITION_UUID,
                "App: reportingui.reports",
                "mirebalaisreports-dailyRegistrationsReport-link"));

        extensions.add(overviewReport(DAILY_CHECK_INS_OVERVIEW_REPORT,
                "mirebalaisreports.dailyCheckInEncounters.name",
                MirebalaisReportsProperties.DAILY_CHECK_INS_REPORT_DEFINITION_UUID,
                "App: reportingui.reports",
                "mirebalaisreports-dailyCheckInsReport-link"));

        extensions.add(overviewReport(DAILY_CLINICAL_ENCOUNTERS_OVERVIEW_REPORT,
                "mirebalaisreports-dailyCheckInsReport-link",
                MirebalaisReportsProperties.DAILY_CLINICAL_ENCOUNTERS_REPORT_DEFINITION_UUID,
                "App: reportingui.reports",
                "mirebalaisreports-dailyClinicalEncountersReport-link"));

        // custom overview report
        extensions.add(extension(DAILY_INPATIENTS_OVERVIEW_REPORT,
                "mirebalaisreports.inpatientStatsDailyReport.name",
                null,
                "link",
                "mirebalaisreports/inpatientStatsDailyReport.page",
                "App: reportingui.reports",
                null,
                REPORTING_OVERVIEW_REPORTS_EXTENSION_POINT,
                REPORTING_OVERVIEW_REPORTS_ORDER.indexOf(DAILY_INPATIENTS_OVERVIEW_REPORT),
                map("linkId", "mirebalaisreports-inpatientDailyReport-link")));

        extensions.add(overviewReport(MONTHLY_INPATIENTS_OVERVIEW_REPORT,
                "org.openmrs.module.reportingui.reports.overview",
                MirebalaisReportsProperties.INPATIENT_STATS_MONTHLY_REPORT_DEFINITION_UUID,
                "App: reportingui.reports",
                "mirebalaisreports-inpatientMonthlyReport-link"));

        extensions.add(extension(NON_CODED_DIAGNOSES_DATA_QUALITY_REPORT,
                "mirebalaisreports.dataQuality.nonCodedDiagnoses",
                null,
                "link",
                "mirebalaisreports/nonCodedDiagnoses.page",
                "App: reportingui.reports",
                null,
                REPORTING_DATA_QUALITY_EXTENSION_POINT,
                0,
                map("linkId", "mirebalaisreports-nonCodedDiagnosesReport-link")));

        extensions.add(dataExport(USERS_AND_PROVIDERS_DATA_EXPORT,
                "mirebalaisreports.userAndProviders.name",
                MirebalaisReportsProperties.USERS_AND_PROVIDERS_REPORT_DEFINITION_UUID,
                "App: mirebalaisreports.dataexports",
                "mirebalaisreports-usersAndProvidersReport-link" ));

        extensions.add(dataExport(LQAS_DATA_EXPORT,
                "mirebalaisreports.dataExports.lqasDiagnoses",
                MirebalaisReportsProperties.LQAS_DIAGNOSES_REPORT_DEFINITION_UUID,
                "App: mirebalaisreports.dataexports",
                "mirebalaisreports-lqasDiagnosesReport-link"));

        extensions.add(dataExport(ALL_PATIENTS_WITH_IDS_DATA_EXPORT,
                "mirebalaisreports.allpatientswithids.name",
                MirebalaisReportsProperties.ALL_PATIENTS_WITH_IDS_REPORT_DEFINITION_UUID,
                "App: mirebalaisreports.dataexports",
                "mirebalaisreports-allPatientsWithIdsReport-link"));

        extensions.add(dataExport(APPOINTMENTS_DATA_EXPORT,
                "mirebalaisreports.appointments.name",
                MirebalaisReportsProperties.APPOINTMENTS_REPORT_DEFINITION_UUID,
                "App: mirebalaisreports.dataexports",
                "mirebalaisreports-appointments-link"));

        addFeatureToggleToExtension(findExtensionById(REPORTING_AD_HOC_ANALYSIS), "reporting_adHocAnalysis");

    }

    private void enableArchives() {

        apps.add(addToHomePage(app(ARCHIVES_ROOM_APP,
                "paperrecord.app.archivesRoom.label",
                "icon-folder-open",
                "paperrecord/archivesRoom.page",
                "App: emr.archivesRoom",
                null)));

        extensions.add(overallAction(REQUEST_PAPER_RECORD_OVERALL_ACTION,
                "paperrecord.task.requestPaperRecord.label",
                "icon-folder-open",
                "script",
                "showRequestChartDialog()",
                "Task: emr.requestPaperRecord",
                null));

        extensions.add(overallAction(PRINT_ID_CARD_OVERALL_ACTION,
                "paperrecord.task.printIdCardLabel.label",
                "icon-print",
                "script",
                "printIdCardLabel()",
                "Task: emr.printLabels",
                null));

        extensions.add(overallAction(PRINT_PAPER_FORM_LABEL_OVERALL_ACTION,
                "paperrecord.task.printPaperFormLabel.label",
                "icon-print",
                "script",
                "printPaperFormLabel()",
                "Task: emr.printLabels",
                null));

        // this provides the javascript the backs the three overall action buttons
        extensions.add(fragmentExtension(PAPER_RECORD_ACTIONS_INCLUDES,
                "paperrecord",
                "patientdashboard/overallActionsIncludes",
                null,
                DASHBOARD_INCLUDE_FRAGMENTS_EXTENSION_POINT));

    }

    public void enableWristbands() {

        extensions.add(overallAction(PRINT_WRISTBAND_OVERALL_ACTION,
                "mirebalais.printWristband",
                "icon-print",
                "script",
                "printWristband()",
                "Task: emr.printLabels",
                null));

        // this provides the javascript the backs the overall action button
        extensions.add(fragmentExtension(PRINT_WRISTBAND_ACTION_INCLUDES,
                "mirebalais",
                "wristband/printWristband",
                null,
                DASHBOARD_INCLUDE_FRAGMENTS_EXTENSION_POINT));

    }

    public void enableAppointmentScheduling() {

        apps.add(addToHomePage(app(APPOINTMENT_SCHEDULING_HOME_APP,
                "appointmentschedulingui.home.title",
                "icon-calendar",
                "/appointmentschedulingui/home.page",
                "App: appointmentschedulingui.home",
                null)));

        apps.add(addToOverallActions(findPatientTemplateApp(SCHEDULE_APPOINTMENT_APP,
                        "appointmentschedulingui.scheduleAppointment.buttonTitle",
                        "icon-calendar",
                        "Task: appointmentschedulingui.bookAppointments",
                        "/appointmentschedulingui/manageAppointments.page?patientId={{patientId}}&breadcrumbOverride={{breadcrumbOverride}}",
                        arrayNode(objectNode("icon", "icon-home", "link", "/index.html"),
                                objectNode("label", "appointmentschedulingui.home.title", "link", "/appointmentschedulingui/home.page"),
                                objectNode("label", "appointmentschedulingui.scheduleAppointment.buttonTitle"))),
                "appointmentschedulingui.scheduleAppointment.title"));

        extensions.add(overallAction(REQUEST_APPOINTMENT_OVERALL_ACTION,
                "appointmentschedulingui.requestAppointment.label",
                "icon-calendar",
                "link",
                "/appointmentschedulingui/requestAppointment.page?patientId={{patient.uuid}}&returnProvider=coreapps&returnPage=patientdashboard/patientDashboard",
                "Task: appointmentschedulingui.requestAppointments",
                null));

        extensions.add(dashboardTab(APPOINTMENTS_TAB,
                "appointmentschedulingui.appointmentsTab.label",
                "App: appointmentschedulingui.viewAppointments",
                "appointmentschedulingui",
                "appointmentsTab"));

        addFeatureToggleToExtension(findExtensionById(APPOINTMENTS_TAB), "requestAppointments");
        addFeatureToggleToExtension(findExtensionById(REQUEST_APPOINTMENT_OVERALL_ACTION), "requestAppointments");

    }

    public void enableSystemAdministration() {

        apps.add(addToHomePage(app(SYSTEM_ADMINISTRATION_APP,
                "coreapps.app.system.administration.label",
                "icon-cogs",
                "coreapps/systemadministration/systemAdministration.page",
                "App: emr.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(MANAGE_ACCOUNTS_APP,
                "emr.task.accountManagement.label",
                "icon-book",
                "emr/account/manageAccounts.page",
                "App: emr.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(MANAGE_PRINTERS_APP,
                "printer.managePrinters",
                "icon-print",
                "printer/managePrinters.page",
                "App: emr.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(DEFAULT_PRINTERS_APP,
                "printer.defaultPrinters",
                "icon-print",
                "printer/defaultPrinters.page",
                "App: emr.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(MERGE_PATIENTS_APP,
                "emr.mergePatients",
                "icon-group",
                "emr/mergePatients.page",
                "App: emr.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(REGISTER_TEST_PATIENT_APP,
                "emr.testPatient.registration",
                "icon-register",
                "mirebalais/patientRegistration/appRouter.page?task=patientRegistration&testPatient=true",
                "App: emr.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(FEATURE_TOGGLES_APP,
                "emr.advancedFeatures",
                "icon-search",
                "mirebalais/toggles.page",
                "App: emr.systemAdministration",
                null)));

        addFeatureToggleToApp(findAppById(REGISTER_TEST_PATIENT_APP), "registerTestPatient");
    }

    public void enableMyAccount() {

        apps.add(addToHomePage(app(MY_ACCOUNT_APP,
                "emr.app.system.administration.myAccount.label",
                "icon-cog",
                "emr/account/myAccount.page",
                null, null)));

        addFeatureToggleToApp(findAppById(MY_ACCOUNT_APP), "myAccountFeature");
    }

    // legacy MPI used in Mirebalais to connect to Lacolline
    public void enableLegacyMPI() {
        apps.add(addToHomePage(app(LEGACY_MPI_APP,
                "mirebalais.mpi.title",
                "icon-zoom-in",
                "mirebalais/mpi/findPatient.page",
                "App: mirebalais.mpi",
                null)));
    }

    public void enableLegacyPatientRegistration() {

        apps.add(addToHomePage(app(LEGACY_PATIENT_REGISTRATION_APP,
                "mirebalais.app.patientRegistration.registration.label",
                "icon-register",
                "mirebalais/patientRegistration/appRouter.page?task=patientRegistration",
                "App: patientregistration.main",
                null)));

        apps.add(addToHomePage(app(LEGACY_PATIENT_REGISTRATION_ED_APP,
                "mirebalais.app.patientRegistration.emergencyCheckin.label",
                "icon-hospital",
                "mirebalais/patientRegistration/appRouter.page?task=edCheckIn",
                "App: patientregistration.main",
                null)));

        apps.add(addToHomePage(app(LEGACY_PATIENT_LOOKUP_APP,
                "mirebalais.app.patientRegistration.patientLookup.label",
                "icon-edit",
                "mirebalais/patientRegistration/appRouter.page?task=patientLookup",
                "App: patientregistration.main",
                null)));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.PATIENT_REGISTRATION,
                findExtensionById(NO_DETAILS_ENCOUNTER_TEMPLATE), "icon-register");

    }

    public void registerLacollinePatientRegistrationEncounterTypes() {
        // TODO: I *believe* these are used in Lacolline, but not 100% sure
        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.PAYMENT,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-money");
        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.PRIMARY_CARE_VISIT,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-calendar");

    }

    public AppDescriptor findAppById(String id) {
        for (AppDescriptor app : apps) {
            if (app.getId().equals(id)) {
                return app;
            }
        }
        throw new IllegalStateException("App Not Found: " + id);
    }

    public Extension findExtensionById(String id) {
        for (Extension extension : extensions) {
            if (extension.getId().equals(id)) {
                return extension;
            }
        }
        throw new IllegalStateException("Extension Not Found: " + id);
    }

    public void setNeedsRefresh(Boolean needsRefresh) {
        this.needsRefresh = needsRefresh;
    }

    // used for mocking
    public void setApps(List<AppDescriptor> apps) {
        this.apps = apps;
    }

    public void setExtensions(List<Extension> extensions) {
        this.extensions = extensions;
    }


}
