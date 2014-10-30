package org.openmrs.module.mirebalais.apploader;

import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.AppTemplate;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.factory.AppFrameworkFactory;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.CoreMetadata;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.RadiologyMetadata;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ADMISSION_FORM_AWAITING_ADMISSION_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ADMISSION_NOTE_VISIT_ACTION;
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
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.LEGACY_MPI_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.LEGACY_PATIENT_LOOKUP_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.LEGACY_PATIENT_REGISTRATION_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.LEGACY_PATIENT_REGISTRATION_ED_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.MANAGE_ACCOUNTS_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.MANAGE_PRINTERS_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.MERGE_PATIENTS_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.MERGE_VISITS_OVERALL_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.MY_ACCOUNT_APP;
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
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REQUEST_APPOINTMENT_OVERALL_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REQUEST_PAPER_RECORD_OVERALL_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.SCHEDULE_APPOINTMENT_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.SURGICAL_NOTE_VISIT_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.SYSTEM_ADMINISTRATION_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.VISIT_ACTIONS_INCLUDES;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.VITALS_APP;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.VITALS_CAPTURE_VISIT_ACTION;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.registerTemplateForEncounterType;

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
        enableCheckIn();
        enableVitals();
        enableConsult();
        enableADT();
        enableDeathCertificate();
        enableRadiology();
        enableDispensing();
        enableSurgery();
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
        extensions.add(CustomAppLoaderUtil.header(PIH_HEADER_EXTENSION, "/ms/uiframework/resource/mirebalais/images/partners_in_health_logo.png"));
    }

    private void setupDefaultEncounterTemplates() {

        extensions.add(CustomAppLoaderUtil.encounterTemplate(DEFAULT_ENCOUNTER_TEMPLATE,
                "coreapps",
                "patientdashboard/encountertemplate/defaultEncounterTemplate"));

        extensions.add(CustomAppLoaderUtil.encounterTemplate(NO_DETAILS_ENCOUNTER_TEMPLATE,
                "coreapps",
                "patientdashboard/encountertemplate/noDetailsEncounterTemplate"));

    }

    private void enableVisitManagement() {

        extensions.add(CustomAppLoaderUtil.overallAction(CREATE_VISIT_OVERALL_ACTION,
                "coreapps.task.startVisit.label",
                "icon-check-in",
                "script",
                "visit.showQuickVisitCreationDialog({{patient.patientId}})",
                "Task: coreapps.createVisit",
                "!visit && !patient.person.dead"));

        extensions.add(CustomAppLoaderUtil.overallAction(CREATE_RETROSPECTIVE_VISIT_OVERALL_ACTION,
                "coreapps.task.createRetrospectiveVisit.label",
                "icon-plus",
                "script",
                "visit.showRetrospectiveVisitCreationDialog()",
                "Task: coreapps.createRetrospectiveVisit",
                null));

        extensions.add(CustomAppLoaderUtil.overallAction(MERGE_VISITS_OVERALL_ACTION,
                "coreapps.task.mergeVisits.label",
                "icon-link",
                "link",
                "coreapps/mergeVisits.page?patientId={{patient.uuid}}",
                "Task: coreapps.mergeVisits",
                null));

        // this provides the javascript the backs the three overall action buttons
        extensions.add(CustomAppLoaderUtil.fragmentExtension(VISIT_ACTIONS_INCLUDES,
                "coreapps",
                "patientdashboard/visitIncludes",
                null,
                DASHBOARD_INCLUDE_FRAGMENTS_EXTENSION_POINT));


    }

    private void enableCheckIn() {

        apps.add(CustomAppLoaderUtil.addToHomePage(CustomAppLoaderUtil.findPatientTemplateApp(CHECK_IN_APP,
                "mirebalais.app.patientRegistration.checkin.label",
                "icon-paste",
                "App: mirebalais.checkin",
                "/mirebalais/checkin/checkin.page?patientId={{patientId}}",
                null)));

        extensions.add(CustomAppLoaderUtil.visitAction(CHECK_IN_VISIT_ACTION,
                "mirebalais.task.checkin.label",
                "icon-check-in",
                "link",
                CustomAppLoaderUtil.simpleHtmlFormLink("mirebalais:htmlforms/checkin.xml"),
                "Task: mirebalais.checkinForm",
                null));

        CustomAppLoaderUtil.registerTemplateForEncounterType(CoreMetadata.EncounterTypes.CHECK_IN,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-check-in");
    }

    private void enableVitals() {

        apps.add(CustomAppLoaderUtil.addToHomePage(CustomAppLoaderUtil.findPatientTemplateApp(VITALS_APP,
                "mirebalais.outpatientVitals.title",
                "icon-vitals",
                "App: mirebalais.outpatientVitals",
                "/mirebalais/outpatientvitals/patient.page?patientId={{patientId}}",
                null)));

        extensions.add(CustomAppLoaderUtil.visitAction(VITALS_CAPTURE_VISIT_ACTION,
                "mirebalais.task.vitals.label",
                "icon-vitals",
                "link",
                CustomAppLoaderUtil.simpleHtmlFormLink("mirebalais:htmlforms/vitals.xml"),
                "Task: emr.enterClinicalForms",
                "visit != null && visit.active"));

        CustomAppLoaderUtil.registerTemplateForEncounterType(CoreMetadata.EncounterTypes.VITALS,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-vitals");

    }

    private void enableConsult() {

        extensions.add(CustomAppLoaderUtil.visitAction(CONSULT_NOTE_VISIT_ACTION,
                "emr.clinic.consult.title",
                "icon-stethoscope",
                "link",
                CustomAppLoaderUtil.standardHtmlFormLink("mirebalais:htmlforms/outpatientConsult.xml&returnProvider=emr&returnPage=disposition/dispositionAction"),
                null,
                "(user.get('fn').hasPrivilege('Task: emr.enterClinicalForms') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: emr.retroConsultNote') || (visit != null && (Date.now () - visit.stopDatetimeInMilliseconds)/(1000 * 60 * 60 * 24) <30 &&  user.get('fn').hasPrivilege('Task: emr.retroConsultNoteThisProviderOnly'))"));

        extensions.add(CustomAppLoaderUtil.visitAction(ED_CONSULT_NOTE_VISIT_ACTION,
                "emr.ed.consult.title",
                "icon-stethoscope",
                "link",
                CustomAppLoaderUtil.standardHtmlFormLink("mirebalais:htmlforms/edNote.xml&returnProvider=emr&returnPage=disposition/dispositionAction"),
                null,
                "(user.get('fn').hasPrivilege('Task: emr.enterClinicalForms') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: emr.retroConsultNote') || (visit != null && (Date.now () - visit.stopDatetimeInMilliseconds)/(1000 * 60 * 60 * 24) <30 &&  user.get('fn').hasPrivilege('Task: emr.retroConsultNoteThisProviderOnly'))"));

        extensions.add(CustomAppLoaderUtil.encounterTemplate(CONSULT_ENCOUNTER_TEMPLATE, "mirebalais", "patientdashboard/encountertemplate/consultEncounterTemplate"));

        CustomAppLoaderUtil.registerTemplateForEncounterType(CoreMetadata.EncounterTypes.VITALS,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-stethoscope", null, true, null);
    }

    private void enableADT() {

        apps.add(CustomAppLoaderUtil.addToHomePage(CustomAppLoaderUtil.app(AWAITING_ADMISSION_APP,
                "coreapps.app.awaitingAdmission.label",
                "icon-list-ul",
                "coreapps/adt/awaitingAdmission.page",
                "App: coreapps.awaitingAdmission",
                CustomAppLoaderUtil.objectNode("patientPageUrl", "/coreapps/patientdashboard/patientDashboard.page?patientId={{patientId}}"))));

        extensions.add(CustomAppLoaderUtil.awaitingAdmissionAction(ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
                "mirebalais.task.admit.label",
                "icon-h-sign",
                "link",
                CustomAppLoaderUtil.standardHtmlFormLink("mirebalais:htmlforms/admissionNote.xml&returnProvider=coreapps&returnPage=adt/awaitingAdmission&returnLabel=coreapps.app.awaitingAdmission.label"),
                "Task: emr.enterClinicalForms",
                null));

        extensions.add(CustomAppLoaderUtil.awaitingAdmissionAction(DENY_ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
                "uicommons.cancel",
                "icon-remove",
                "link",
                CustomAppLoaderUtil.standardHtmlFormLink("mirebalais:htmlforms/cancelAdmission.xml&returnProvider=coreapps&returnPage=adt/awaitingAdmission"),
                "Task: emr.enterClinicalForms",
                null));

        extensions.add(CustomAppLoaderUtil.visitAction(ADMISSION_NOTE_VISIT_ACTION,
                "mirebalais.task.admit.label",
                "icon-h-sign",
                "link",
                CustomAppLoaderUtil.standardHtmlFormLink("mirebalais:htmlforms/admissionNote.xml"),
                null,
                "(user.get('fn').hasPrivilege('Task: emr.enterClinicalForms') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: emr.retroConsultNote') || (visit != null && (Date.now () - visit.stopDatetimeInMilliseconds)/(1000 * 60 * 60 * 24) <30 &&  user.get('fn').hasPrivilege('Task: emr.retroConsultNoteThisProviderOnly'))"));

        CustomAppLoaderUtil.registerTemplateForEncounterType(CoreMetadata.EncounterTypes.ADMISSION,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-signin", null, true, null);

        CustomAppLoaderUtil.registerTemplateForEncounterType(CoreMetadata.EncounterTypes.CANCEL_ADMISSION,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-ban-circle", true, true, null);

        CustomAppLoaderUtil.registerTemplateForEncounterType(CoreMetadata.EncounterTypes.TRANSFER,
                findExtensionById(NO_DETAILS_ENCOUNTER_TEMPLATE), "icon-share", null, true, null);

        CustomAppLoaderUtil.registerTemplateForEncounterType(CoreMetadata.EncounterTypes.EXIT_FROM_CARE,
                findExtensionById(NO_DETAILS_ENCOUNTER_TEMPLATE), "icon-signout", null, true, null);
    }

    private void enableDeathCertificate() {

        extensions.add(CustomAppLoaderUtil.visitAction(DEATH_CERTIFICATE_VISIT_ACTION,
                "mirebalais.deathCertificate.enter.label",
                "icon-remove-circle",
                "link",
                CustomAppLoaderUtil.simpleHtmlFormLink("mirebalais:htmlforms/deathCertificate.xml"),
                "Task: mirebalais.enterDeathCertificate",
                "!patient.person.dead"
        ));

        extensions.add(CustomAppLoaderUtil.fragmentExtension(DEATH_CERTIFICATE_HEADER_EXTENSION,
                "mirebalais",
                "deathcertificate/headerLink",
                "Task: mirebalais.enterDeathCertificate",
                DEATH_INFO_HEADER_EXTENSION_POINT));

        CustomAppLoaderUtil.addFeatureToggleToExtension(findExtensionById(DEATH_CERTIFICATE_VISIT_ACTION), "deathNote");
        CustomAppLoaderUtil.addFeatureToggleToExtension(findExtensionById(DEATH_CERTIFICATE_HEADER_EXTENSION), "deathNote");
    }

    private void enableRadiology() {

        extensions.add(CustomAppLoaderUtil.dashboardTab(RADIOLOGY_TAB,
                "radiologyapp.radiology.label",
                "Task: org.openmrs.module.radiologyapp.tab",
                "radiologyapp",
                "radiologyTab"));

        extensions.add(CustomAppLoaderUtil.visitAction(ORDER_XRAY_VISIT_ACTION,
                "radiologyapp.task.order.CR.label",
                "icon-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=CR",
                null,
                "(user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.orderXray') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.retroOrder')"));

        extensions.add(CustomAppLoaderUtil.visitAction(ORDER_CT_VISIT_ACTION,
                "radiologyapp.task.order.CT.label",
                "icon-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=Ct",
                null,
                "(user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.orderCT') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.retroOrder')"));

        extensions.add(CustomAppLoaderUtil.visitAction(ORDER_ULTRASOUND_VISIT_ACTION,
                "radiologyapp.task.order.US.label",
                "icon-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=US",
                null,
                "(user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.orderUS') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.retroOrder')"));

        CustomAppLoaderUtil.registerTemplateForEncounterType(RadiologyMetadata.EncounterTypes.RADIOLOGY_ORDER,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-x-ray");

        CustomAppLoaderUtil.registerTemplateForEncounterType(RadiologyMetadata.EncounterTypes.RADIOLOGY_STUDY,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-x-ray");

        CustomAppLoaderUtil.registerTemplateForEncounterType(RadiologyMetadata.EncounterTypes.RADIOLOGY_REPORT,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-x-ray");
    }

    private void enableDispensing() {

        // TODO change this to use the coreapps find patient app?
        apps.add(CustomAppLoaderUtil.addToHomePage(CustomAppLoaderUtil.app(DISPENSING_APP,
                "dispensing.app.label",
                "icon-medicine",
                "dispensing/findPatient.page",
                "App: dispensing.app.dispense",
                null)));

        extensions.add(CustomAppLoaderUtil.visitAction(DISPENSE_MEDICATION_VISIT_ACTION,
                "dispensing.app.label",
                "icon-medicine",
                "link",
                CustomAppLoaderUtil.standardHtmlFormLink("dispensing:htmlforms/dispensing.xml"),
                "Task: mirebalais.dispensing",
                null));

        CustomAppLoaderUtil.registerTemplateForEncounterType(CoreMetadata.EncounterTypes.MEDICATION_DISPENSED,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-medicine", true, true, "bad21515-fd04-4ff6-bfcd-78456d12f168");

    }

    private void enableSurgery() {

        extensions.add(CustomAppLoaderUtil.visitAction(SURGICAL_NOTE_VISIT_ACTION,
                "mirebalais.task.surgicalOperativeNote.label",
                "icon-paste",
                "link",
                CustomAppLoaderUtil.standardHtmlFormLink("mirebalais:htmlforms/surgicalPostOpNote.xml"),
                "Task: emr.enterClinicalForms",
                null));

        CustomAppLoaderUtil.registerTemplateForEncounterType(CoreMetadata.EncounterTypes.POST_OPERATIVE_NOTE,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-paste", true, true, "9b135b19-7ebe-4a51-aea2-69a53f9383af");
        }

    private void enableArchives() {

        apps.add(CustomAppLoaderUtil.addToHomePage(CustomAppLoaderUtil.app(ARCHIVES_ROOM_APP,
                "paperrecord.app.archivesRoom.label",
                "icon-folder-open",
                "paperrecord/archivesRoom.page",
                "App: emr.archivesRoom",
                null)));

        extensions.add(CustomAppLoaderUtil.overallAction(REQUEST_PAPER_RECORD_OVERALL_ACTION,
                "paperrecord.task.requestPaperRecord.label",
                "icon-folder-open",
                "script",
                "showRequestChartDialog()",
                "Task: emr.requestPaperRecord",
                null));

        extensions.add(CustomAppLoaderUtil.overallAction(PRINT_ID_CARD_OVERALL_ACTION,
                "paperrecord.task.printIdCardLabel.label",
                "icon-print",
                "script",
                "printIdCardLabel()",
                "Task: emr.printLabels",
                null));

        extensions.add(CustomAppLoaderUtil.overallAction(PRINT_PAPER_FORM_LABEL_OVERALL_ACTION,
                "paperrecord.task.printPaperFormLabel.label",
                "icon-print",
                "script",
                "printPaperFormLabel()",
                "Task: emr.printLabels",
                null));

        // this provides the javascript the backs the three overall action buttons
        extensions.add(CustomAppLoaderUtil.fragmentExtension(PAPER_RECORD_ACTIONS_INCLUDES,
                "paperrecord",
                "patientdashboard/overallActionsIncludes",
                null,
                DASHBOARD_INCLUDE_FRAGMENTS_EXTENSION_POINT));

    }

    public void enableWristbands() {

        extensions.add(CustomAppLoaderUtil.overallAction(PRINT_WRISTBAND_OVERALL_ACTION,
                "mirebalais.printWristband",
                "icon-print",
                "script",
                "printWristband()",
                "Task: emr.printLabels",
                null));

        // this provides the javascript the backs the overall action button
        extensions.add(CustomAppLoaderUtil.fragmentExtension(PRINT_WRISTBAND_ACTION_INCLUDES,
                "mirebalais",
                "wristband/printWristband",
                null,
                DASHBOARD_INCLUDE_FRAGMENTS_EXTENSION_POINT));

    }

    public void enableAppointmentScheduling() {

        apps.add(CustomAppLoaderUtil.addToHomePage(CustomAppLoaderUtil.app(APPOINTMENT_SCHEDULING_HOME_APP,
                "appointmentschedulingui.home.title",
                "icon-calendar",
                "/appointmentschedulingui/home.page",
                "App: appointmentschedulingui.home",
                null)));

        apps.add(CustomAppLoaderUtil.addToOverallActions(CustomAppLoaderUtil.findPatientTemplateApp(SCHEDULE_APPOINTMENT_APP,
                        "appointmentschedulingui.scheduleAppointment.buttonTitle",
                        "icon-calendar",
                        "Task: appointmentschedulingui.bookAppointments",
                        "/appointmentschedulingui/manageAppointments.page?patientId={{patientId}}&breadcrumbOverride={{breadcrumbOverride}}",
                        CustomAppLoaderUtil.arrayNode(CustomAppLoaderUtil.objectNode("icon", "icon-home", "link", "/index.html"),
                                CustomAppLoaderUtil.objectNode("label", "appointmentschedulingui.home.title", "link", "/appointmentschedulingui/home.page"),
                                CustomAppLoaderUtil.objectNode("label", "appointmentschedulingui.scheduleAppointment.buttonTitle"))),
                "appointmentschedulingui.scheduleAppointment.title"));

        extensions.add(CustomAppLoaderUtil.overallAction(REQUEST_APPOINTMENT_OVERALL_ACTION,
                "appointmentschedulingui.requestAppointment.label",
                "icon-calendar",
                "link",
                "/appointmentschedulingui/requestAppointment.page?patientId={{patient.uuid}}&returnProvider=coreapps&returnPage=patientdashboard/patientDashboard",
                "Task: appointmentschedulingui.requestAppointments",
                null));

        extensions.add(CustomAppLoaderUtil.dashboardTab(APPOINTMENTS_TAB,
                "appointmentschedulingui.appointmentsTab.label",
                "App: appointmentschedulingui.viewAppointments",
                "appointmentschedulingui",
                "appointmentsTab"));

        CustomAppLoaderUtil.addFeatureToggleToExtension(findExtensionById(APPOINTMENTS_TAB), "requestAppointments");
        CustomAppLoaderUtil.addFeatureToggleToExtension(findExtensionById(REQUEST_APPOINTMENT_OVERALL_ACTION), "requestAppointments");

    }

    public void enableSystemAdministration() {

        apps.add(CustomAppLoaderUtil.addToHomePage(CustomAppLoaderUtil.app(SYSTEM_ADMINISTRATION_APP,
                "coreapps.app.system.administration.label",
                "icon-cogs",
                "coreapps/systemadministration/systemAdministration.page",
                "App: emr.systemAdministration",
                null)));

        apps.add(CustomAppLoaderUtil.addToSystemAdministrationPage(CustomAppLoaderUtil.app(MANAGE_ACCOUNTS_APP,
                "emr.task.accountManagement.label",
                "icon-book",
                "emr/account/manageAccounts.page",
                "App: emr.systemAdministration",
                null)));

        apps.add(CustomAppLoaderUtil.addToSystemAdministrationPage(CustomAppLoaderUtil.app(MANAGE_PRINTERS_APP,
                "printer.managePrinters",
                "icon-print",
                "printer/managePrinters.page",
                "App: emr.systemAdministration",
                null)));

        apps.add(CustomAppLoaderUtil.addToSystemAdministrationPage(CustomAppLoaderUtil.app(DEFAULT_PRINTERS_APP,
                "printer.defaultPrinters",
                "icon-print",
                "printer/defaultPrinters.page",
                "App: emr.systemAdministration",
                null)));

        apps.add(CustomAppLoaderUtil.addToSystemAdministrationPage(CustomAppLoaderUtil.app(MERGE_PATIENTS_APP,
                "emr.mergePatients",
                "icon-group",
                "emr/mergePatients.page",
                "App: emr.systemAdministration",
                null)));

        apps.add(CustomAppLoaderUtil.addToSystemAdministrationPage(CustomAppLoaderUtil.app(REGISTER_TEST_PATIENT_APP,
                "emr.testPatient.registration",
                "icon-register",
                "mirebalais/patientRegistration/appRouter.page?task=patientRegistration&testPatient=true",
                "App: emr.systemAdministration",
                null)));

        apps.add(CustomAppLoaderUtil.addToSystemAdministrationPage(CustomAppLoaderUtil.app(FEATURE_TOGGLES_APP,
                "emr.advancedFeatures",
                "icon-search",
                "mirebalais/toggles.page",
                "App: emr.systemAdministration",
                null)));

        CustomAppLoaderUtil.addFeatureToggleToApp(findAppById(REGISTER_TEST_PATIENT_APP), "registerTestPatient");
    }

    public void enableMyAccount() {

        apps.add(CustomAppLoaderUtil.addToHomePage(CustomAppLoaderUtil.app(MY_ACCOUNT_APP,
                "emr.app.system.administration.myAccount.label",
                "icon-cog",
                "emr/account/myAccount.page",
                null, null)));

        CustomAppLoaderUtil.addFeatureToggleToApp(findAppById(MY_ACCOUNT_APP), "myAccountFeature");
    }

    // legacy MPI used in Mirebalais to connect to Lacolline
    public void enableLegacyMPI() {
        apps.add(CustomAppLoaderUtil.addToHomePage(CustomAppLoaderUtil.app(LEGACY_MPI_APP,
                "mirebalais.mpi.title",
                "icon-zoom-in",
                "mirebalais/mpi/findPatient.page",
                "App: mirebalais.mpi",
                null)));
    }

    public void enableLegacyPatientRegistration() {

        apps.add(CustomAppLoaderUtil.addToHomePage(CustomAppLoaderUtil.app(LEGACY_PATIENT_REGISTRATION_APP,
                "mirebalais.app.patientRegistration.registration.label",
                "icon-register",
                "mirebalais/patientRegistration/appRouter.page?task=patientRegistration",
                "App: patientregistration.main",
                null)));

        apps.add(CustomAppLoaderUtil.addToHomePage(CustomAppLoaderUtil.app(LEGACY_PATIENT_REGISTRATION_ED_APP,
                "mirebalais.app.patientRegistration.emergencyCheckin.label",
                "icon-hospital",
                "mirebalais/patientRegistration/appRouter.page?task=edCheckIn",
                "App: patientregistration.main",
                null)));

        apps.add(CustomAppLoaderUtil.addToHomePage(CustomAppLoaderUtil.app(LEGACY_PATIENT_LOOKUP_APP,
                "mirebalais.app.patientRegistration.patientLookup.label",
                "icon-edit",
                "mirebalais/patientRegistration/appRouter.page?task=patientLookup",
                "App: patientregistration.main",
                null)));

        CustomAppLoaderUtil.registerTemplateForEncounterType(CoreMetadata.EncounterTypes.PATIENT_REGISTRATION,
                findExtensionById(NO_DETAILS_ENCOUNTER_TEMPLATE), "icon-register");

    }

    public void registerLacollinePatientRegistrationEncounterTypes() {
        // TODO: I *believe these are used in Lacolline, but not 100% sure
        CustomAppLoaderUtil.registerTemplateForEncounterType(CoreMetadata.EncounterTypes.PAYMENT,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-money");
        CustomAppLoaderUtil.registerTemplateForEncounterType(CoreMetadata.EncounterTypes.PRIMARY_CARE_VISIT,
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
