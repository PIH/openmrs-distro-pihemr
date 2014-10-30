package org.openmrs.module.mirebalais;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.AppTemplate;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.factory.AppFrameworkFactory;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.CoreMetadata;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.RadiologyMetadata;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomAppLoaderFactory implements AppFrameworkFactory {

    // Apps

    // TODO would ne nice to rename this to "pih.checkin"
    // TODO for checkin would need to fix requestRecord.gsp:69
    public static final String CHECK_IN_APP = "mirebalais.liveCheckin";
    public static final String VITALS_APP = "pih.app.vitals";
    public static final String AWAITING_ADMISSION_APP = "coreapps.app.awaitingAdmission";
    public static final String ARCHIVES_ROOM_APP = "paperrecord.app.archivesRoom";
    public static final String SYSTEM_ADMINISTRATION_APP = "coreapps.app.systemAdministration";
    public static final String APPOINTMENT_SCHEDULING_HOME_APP = "appointmentschedulingui.app";
    public static final String DISPENSING_APP = "dispensing.app";
    public static final String SCHEDULE_APPOINTMENT_APP = "appointmentschedulingui.schedulingAppointmentApp";
    public static final String MY_ACCOUNT_APP = "emr.myAccount";

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


    // Encounter templates
    public static final String DEFAULT_ENCOUNTER_TEMPLATE = "defaultEncounterTemplate";
    public static final String CONSULT_ENCOUNTER_TEMPLATE = "consultEncounterTemplate";
    public static final String NO_DETAILS_ENCOUNTER_TEMPLATE = "noDetailsEncounterTemplate";

    // order of lists define the order apps and extensions appear
    public static final List<String> HOME_PAGE_APPS_ORDER = Arrays.asList(
            CHECK_IN_APP,
            VITALS_APP,
            LEGACY_PATIENT_REGISTRATION_APP,
            LEGACY_PATIENT_REGISTRATION_ED_APP,
            LEGACY_PATIENT_LOOKUP_APP,
            DISPENSING_APP,
            APPOINTMENT_SCHEDULING_HOME_APP,
            ARCHIVES_ROOM_APP,
            AWAITING_ADMISSION_APP,
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
        extensions.add(header(PIH_HEADER_EXTENSION,"/ms/uiframework/resource/mirebalais/images/partners_in_health_logo.png"));
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

        addFeatureToggle(findExtensionById(DEATH_CERTIFICATE_VISIT_ACTION), "deathNote");
        addFeatureToggle(findExtensionById(DEATH_CERTIFICATE_HEADER_EXTENSION), "deathNote");
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

        addFeatureToggle(findExtensionById(APPOINTMENTS_TAB), "requestAppointments");
        addFeatureToggle(findExtensionById(REQUEST_APPOINTMENT_OVERALL_ACTION), "requestAppointments");

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

        addFeatureToggle(findAppById(REGISTER_TEST_PATIENT_APP), "registerTestPatient");
    }

    public void enableMyAccount() {

        apps.add(addToHomePage(app(MY_ACCOUNT_APP,
                "emr.app.system.administration.myAccount.label",
                "icon-cog",
                "emr/account/myAccount.page",
                null, null)));

        addFeatureToggle(findAppById(MY_ACCOUNT_APP), "myAccountFeature");
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
        // TODO: I *believe these are used in Lacolline, but not 100% sure
        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.PAYMENT,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-money");
        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.PRIMARY_CARE_VISIT,
                findExtensionById(DEFAULT_ENCOUNTER_TEMPLATE), "icon-calendar");

    }

    static public AppDescriptor app(String id, String label, String icon, String url, String privilege, ObjectNode config) {

        AppDescriptor app = new AppDescriptor(id, id, label, url, icon, null, 0, privilege, null);

        if (config != null) {
            app.setConfig(config);
        }

        return app;
    }

    static public AppDescriptor findPatientTemplateApp(String id, String label, String icon, String privilege, String afterSelectedUrl, ArrayNode breadcrumbs) {

        AppDescriptor app = new AppDescriptor(id, id, label, "coreapps/findpatient/findPatient.page?app=" + id, icon, null, 0, privilege, null);

        app.setConfig(objectNode(
                "afterSelectedUrl", afterSelectedUrl,
                "label", label,
                "heading", label,
                "showLastViewedPatients", false,
                "breadcrumbs", breadcrumbs));

        return app;
    }

    static public AppDescriptor addToHomePage(AppDescriptor app) {
        appExtension(app, app.getId() + ".appLink",
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                HOME_PAGE_APPS_ORDER.indexOf(app.getId()),
                HOME_PAGE_EXTENSION_POINT);
        return app;
    }

    static public AppDescriptor addToOverallActions(AppDescriptor app, String label) {
        appExtension(app, app.getId() + ".overallActions.appLink",
                label,
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                OVERALL_ACTIONS_ORDER.indexOf(app.getId()),
               OVERALL_ACTIONS_EXTENSION_POINT);
        return app;
    }

    static public AppDescriptor addToSystemAdministrationPage(AppDescriptor app) {
        appExtension(app, app.getId() + ".systemAdministration.appLink",
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                SYSTEM_ADMINISTRATION_APPS_ORDER.indexOf(app.getId()),
                SYSTEM_ADMINISTRATION_PAGE_EXTENSION_POINT);
        return app;
    }

    static public Extension visitAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  action(id, label, icon, type, urlOrScript, privilege, require, VISIT_ACTIONS_EXTENSION_POINT, VISIT_ACTIONS_ORDER.indexOf(id));
    }

    static public Extension overallAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  action(id, label, icon, type, urlOrScript, privilege, require, OVERALL_ACTIONS_EXTENSION_POINT, OVERALL_ACTIONS_ORDER.indexOf(id));
    }

    static public Extension awaitingAdmissionAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  action(id, label, icon, type, urlOrScript, privilege, require, AWAITING_ADMISSION_ACTIONS_EXTENSION_POINT, AWAITING_ADMISSION_ACTIONS_ORDER.indexOf(id));
    }

    static private Extension action(String id, String label, String icon, String type, String urlOrScript, String privilege, String require, String extensionPoint, int order) {
        Extension extension = new Extension(id, null,extensionPoint, type, label, null, order, privilege, null);
        extension.setIcon(icon);

        if (StringUtils.isNotBlank(require)) {
            extension.setRequire(require);
        }

        if (type.equals("link")) {
            extension.setUrl(urlOrScript);
        }
        else if (type.equals("script")) {
            extension.setScript(urlOrScript);
        }
        else {
            throw new IllegalStateException("Invalid type: " + type);
        }
        return extension;
    }

    static public Extension dashboardTab(String id, String label, String privilege, String provider, String fragment) {
        return new Extension(id, null, DASHBOARD_TAB_EXTENSION_POINT, "link", label, null, 0,
                privilege, map("provider", provider, "fragment", fragment));

    }

    static public Extension encounterTemplate(String id, String templateProvider, String templateFragment) {
        return new Extension(id, null, ENCOUNTER_TEMPLATE_EXTENSION_POINT, "fragment", null, null, 0, null,
                map("templateId", id, "templateFragmentProviderName", templateProvider, "templateFragmentId", templateFragment));
    }

    static public Extension header(String id, String logo) {
        return new Extension(id, null, HEADER_EXTENSION_POINT, "config", null, null, 0, null, map("logo-icon-url", logo));
    }

    static public Extension fragmentExtension(String id, String provider, String fragment, String privilege, String extensionPoint) {
        return new Extension(id, null, extensionPoint, "include-fragment", null, null, 0,
                privilege, map("provider", provider, "fragment", fragment));
    }

    static public Extension appExtension(AppDescriptor app, String id, String label, String icon, String type, String url,
                                           String requiredPrivilege, int order, String extensionPoint) {

        Extension extension = new Extension(id, app.getId(), extensionPoint, type, label, url, order, requiredPrivilege, null);
        extension.setIcon(icon);

        if (app.getExtensions() == null) {
            app.setExtensions(new ArrayList<Extension>());
        }

        app.getExtensions().add(extension);
        return extension;
    }

    static public String simpleHtmlFormLink(String definitionUiResource) {
        return "htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{patient.uuid}}&visitId={{visit.id}}&definitionUiResource=" + definitionUiResource;
    }

    static public String standardHtmlFormLink(String definitionUiResource) {
        return "htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{patient.uuid}}&visitId={{visit.id}}&definitionUiResource=" + definitionUiResource;
    }

    static public void addFeatureToggle(AppDescriptor app, String featureToggle) {
        app.setFeatureToggle(featureToggle);
    }

    static public void addFeatureToggle(Extension ext, String featureToggle) {
        ext.setFeatureToggle(featureToggle);
    }

    static public void registerTemplateForEncounterType(String encounterTypeUuid, Extension template, String icon) {
        registerTemplateForEncounterType(encounterTypeUuid, template, icon, null, null, null);
    }

    static public void registerTemplateForEncounterType(String encounterTypeUuid, Extension template, String icon,
                                                 Boolean displayWithHtmlForm, Boolean editable,
                                                 String primaryEncounterRoleUuid) {

        Map<String,Object> extensionParams = template.getExtensionParams();

        if (!extensionParams.containsKey("supportedEncounterTypes")) {
            extensionParams.put("supportedEncounterTypes", new HashMap<String,Object>());
        }

        Map<String,Object> encounterTypeParams = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(icon)) {
            encounterTypeParams.put("icon", icon);
        }
        if (displayWithHtmlForm != null) {
            encounterTypeParams.put("displayWithHtmlForm", displayWithHtmlForm);
        }
        if (editable != null) {
            encounterTypeParams.put("editable", editable);
        }
        if (StringUtils.isNotBlank(primaryEncounterRoleUuid)) {
            encounterTypeParams.put("primaryEncounterRoleUuid", primaryEncounterRoleUuid);
        }

        ((Map<String,Object>) extensionParams.get("supportedEncounterTypes")).put(encounterTypeUuid, encounterTypeParams);
    }

    static public ArrayNode arrayNode(ObjectNode ... nodes) {
        ArrayNode arrayNode = new ObjectMapper().createArrayNode();
        for (int i = 0; i < nodes.length; i++) {
            arrayNode.add(nodes[i]);
        }
        return arrayNode;
    }

    static public ObjectNode objectNode(Object ... obj) {

        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        for (int i = 0; i < obj.length; i=i+2) {
            String key = (String) obj[i];
            Object value = obj[i+1];

            if (value instanceof Boolean) {
                objectNode.put(key, (Boolean) value);
            }
            else if (value instanceof String) {
                objectNode.put(key, (String) value);
            }
            else if (value instanceof Integer) {
                objectNode.put(key, (Integer) value);
            }
            else if (value instanceof ArrayNode) {
                objectNode.put(key, (ArrayNode) value);
            }
            else if (value instanceof ObjectNode) {
                objectNode.put(key, (ObjectNode) value);
            }
        }

        return objectNode;
    }

    static public Map<String,Object> map(Object ... obj) {

        Map<String,Object> map = new HashMap<String, Object>();

        for (int i = 0; i < obj.length; i=i+2) {
            String key = (String) obj[i];
            Object value = obj[i+1];
            map.put(key, value);
        }

        return map;
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
