package org.openmrs.module.mirebalais.apploader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.AppTemplate;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.factory.AppFrameworkFactory;
import org.openmrs.module.mirebalais.config.Config;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.CoreMetadata;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.RadiologyMetadata;
import org.openmrs.module.mirebalaisreports.MirebalaisReportsProperties;
import org.openmrs.module.mirebalaisreports.definitions.FullDataExportBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.Apps;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.EncounterTemplates;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ExtensionPoints;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.Extensions;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTING_DATA_EXPORT_REPORTS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTING_OVERVIEW_REPORTS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addFeatureToggleToApp;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addFeatureToggleToExtension;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToClinicianDashboardFirstColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToClinicianDashboardSecondColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToHomePage;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToOverallActions;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToSystemAdministrationPage;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.app;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.arrayNode;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.awaitingAdmissionAction;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.dailyReport;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.dashboardTab;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.dataExport;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.encounterTemplate;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.extension;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.field;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.findPatientTemplateApp;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.fragmentExtension;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.header;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.map;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.objectNode;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.overallAction;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.overviewReport;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.patientRegistrationConfig;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.question;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.registerTemplateForEncounterType;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.section;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.simpleHtmlFormLink;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.standardHtmlFormLink;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.visitAction;

@Component
public class CustomAppLoaderFactory implements AppFrameworkFactory {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private Config config;

    @Autowired
    private FullDataExportBuilder fullDataExportBuilder;

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

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.VISIT_MANAGEMENT)) {
            enableVisitManagement();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.ACTIVE_VISITS)) {
            enableActiveVisits();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.CHECK_IN)) {
            enableCheckIn();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.VITALS)) {
            enableVitals();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.CONSULT)) {
            enableConsult();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.ED_CONSULT)) {
            enableEDConsult();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.ADT)) {
            enableADT();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.DEATH_CERTIFICATE)) {
            enableDeathCertificate();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.RADIOLOGY)) {
            enableRadiology();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.DISPENSING)) {
            enableDispensing();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.SURGERY)) {
            enableSurgery();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.OVERVIEW_REPORTS)) {
            enableOverviewReports();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.DATA_EXPORTS)) {
            enableDataExports();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.ARCHIVES)) {
            enableArchives();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.WRISTBANDS)) {
            enableWristbands();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.APPOINTMENT_SCHEDULING)) {
            enableAppointmentScheduling();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.SYSTEM_ADMINISTRATION)) {
            enableSystemAdministration();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.MANAGE_PRINTERS)) {
            enableManagePrinters();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.MY_ACCOUNT)) {
            enableMyAccount();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.PATIENT_REGISTRATION)) {
            enablePatientRegistration();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.LEGACY_MPI)) {
            enableLegacyMPI();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.LEGACY_PATIENT_REGISTRATION)) {
            enableLegacyPatientRegistration();
        }

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.LACOLLINE_PATIENT_REGISTRATION_ENCOUNTER_TYPES)) {
            registerLacollinePatientRegistrationEncounterTypes();
        }

        needsRefresh = false;
    }

    private void configureHeader(){
        extensions.add(header(Extensions.PIH_HEADER_EXTENSION, "/ms/uiframework/resource/mirebalais/images/partners_in_health_logo.png"));
    }

    private void setupDefaultEncounterTemplates() {

        extensions.add(encounterTemplate(CustomAppLoaderConstants.EncounterTemplates.DEFAULT,
                "coreapps",
                "patientdashboard/encountertemplate/defaultEncounterTemplate"));

        extensions.add(encounterTemplate(EncounterTemplates.NO_DETAILS,
                "coreapps",
                "patientdashboard/encountertemplate/noDetailsEncounterTemplate"));

    }

    private void enableVisitManagement() {

        extensions.add(overallAction(Extensions.CREATE_VISIT_OVERALL_ACTION,
                "coreapps.task.startVisit.label",
                "icon-check-in",
                "script",
                "visit.showQuickVisitCreationDialog({{patient.patientId}})",
                "Task: coreapps.createVisit",
                "!visit && !patient.person.dead"));

        extensions.add(overallAction(Extensions.CREATE_RETROSPECTIVE_VISIT_OVERALL_ACTION,
                "coreapps.task.createRetrospectiveVisit.label",
                "icon-plus",
                "script",
                "visit.showRetrospectiveVisitCreationDialog()",
                "Task: coreapps.createRetrospectiveVisit",
                null));

        extensions.add(overallAction(Extensions.MERGE_VISITS_OVERALL_ACTION,
                "coreapps.task.mergeVisits.label",
                "icon-link",
                "link",
                "coreapps/mergeVisits.page?patientId={{patient.uuid}}",
                "Task: coreapps.mergeVisits",
                null));

        // this provides the javascript the backs the three overall action buttons
        extensions.add(fragmentExtension(Extensions.VISIT_ACTIONS_INCLUDES,
                "coreapps",
                "patientdashboard/visitIncludes",
                null,
                ExtensionPoints.DASHBOARD_INCLUDE_FRAGMENTS));

    }

    private void enableActiveVisits() {

        String url;
        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.CLINICIAN_DASHBOARD)) {
            url = "/coreapps/clinicianfacing/patient.page?patientId={{patientId}}";
        }
        else {
            url = "/coreapps/patientdashboard/patientDashboard.page?patientId={{patientId}}";
        }

        apps.add(addToHomePage(app(Apps.ACTIVE_VISITS,
                "coreapps.activeVisits.app.label",
                "icon-check-in",
                "coreapps/activeVisits.page?app=" + Apps.ACTIVE_VISITS,
                "App: org.openmrs.module.coreapps.activeVisits",
                objectNode("patientPageUrl", url))));

    }

    private void enableCheckIn() {

        apps.add(addToHomePage(findPatientTemplateApp(Apps.CHECK_IN,
                "mirebalais.app.patientRegistration.checkin.label",
                "icon-paste",
                "App: mirebalais.checkin",
                "/mirebalais/checkin/checkin.page?patientId={{patientId}}",
                null)));

        extensions.add(visitAction(Extensions.CHECK_IN_VISIT_ACTION,
                "mirebalais.task.checkin.label",
                "icon-check-in",
                "link",
                simpleHtmlFormLink("mirebalais:htmlforms/checkin.xml"),
                "Task: mirebalais.checkinForm",
                null));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.CHECK_IN,
                findExtensionById(EncounterTemplates.DEFAULT), "icon-check-in");
    }

    private void enableVitals() {

        apps.add(addToHomePage(findPatientTemplateApp(Apps.VITALS,
                "mirebalais.outpatientVitals.title",
                "icon-vitals",
                "App: mirebalais.outpatientVitals",
                "/mirebalais/outpatientvitals/patient.page?patientId={{patientId}}",
                null)));

        extensions.add(visitAction(Extensions.VITALS_CAPTURE_VISIT_ACTION,
                "mirebalais.task.vitals.label",
                "icon-vitals",
                "link",
                simpleHtmlFormLink("mirebalais:htmlforms/vitals.xml"),
                "Task: emr.enterClinicalForms",
                "visit != null && visit.active"));

        apps.add(addToClinicianDashboardFirstColumn(app(Apps.MOST_RECENT_VITALS,
                "mirebalais.mostRecentVitals.label",
                "icon-vitals",
                null,
                "App: mirebalais.outpatientVitals",
                objectNode("encounterDateLabel", "mirebalais.mostRecentVitals.encounterDateLabel",
                        "encounterTypeUuid", CoreMetadata.EncounterTypes.VITALS)),
                "coreapps",
                "encounter/mostRecentEncounter"));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.VITALS,
                findExtensionById(EncounterTemplates.DEFAULT), "icon-vitals");

    }

    private void enableConsult() {

        extensions.add(visitAction(Extensions.CONSULT_NOTE_VISIT_ACTION,
                "emr.clinic.consult.title",
                "icon-stethoscope",
                "link",
                standardHtmlFormLink("mirebalais:htmlforms/outpatientConsult.xml&returnProvider=coreapps&returnPage=patientdashboard/patientDashboard"),
                null,
                "(user.get('fn').hasPrivilege('Task: emr.enterClinicalForms') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: emr.retroConsultNote') || (visit != null && (Date.now () - visit.stopDatetimeInMilliseconds)/(1000 * 60 * 60 * 24) <30 &&  user.get('fn').hasPrivilege('Task: emr.retroConsultNoteThisProviderOnly'))"));

        extensions.add(encounterTemplate(EncounterTemplates.CONSULT, "mirebalais", "patientdashboard/encountertemplate/consultEncounterTemplate"));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.CONSULTATION,
                findExtensionById(EncounterTemplates.CONSULT), "icon-stethoscope", null, true, null);
    }

    private void  enableEDConsult() {

        extensions.add(visitAction(Extensions.ED_CONSULT_NOTE_VISIT_ACTION,
                "emr.ed.consult.title",
                "icon-stethoscope",
                "link",
                standardHtmlFormLink("mirebalais:htmlforms/edNote.xml&returnProvider=coreapps&returnPage=patientdashboard/patientDashboard"),
                null,
                "(user.get('fn').hasPrivilege('Task: emr.enterClinicalForms') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: emr.retroConsultNote') || (visit != null && (Date.now () - visit.stopDatetimeInMilliseconds)/(1000 * 60 * 60 * 24) <30 &&  user.get('fn').hasPrivilege('Task: emr.retroConsultNoteThisProviderOnly'))"));

    }

    private void enableADT() {

        apps.add(addToHomePage(app(Apps.AWAITING_ADMISSION,
                "coreapps.app.awaitingAdmission.label",
                "icon-list-ul",
                "coreapps/adt/awaitingAdmission.page",
                "App: coreapps.awaitingAdmission",
                objectNode("patientPageUrl", "/coreapps/patientdashboard/patientDashboard.page?patientId={{patientId}}"))));

        apps.add(addToHomePage(app(Apps.INPATIENTS,
                "mirebalaisreports.app.inpatients.label",
                "icon-list-ol",
                "mirebalaisreports/inpatientList.page",
                "App: emr.inpatients",
                null)));

        extensions.add(awaitingAdmissionAction(Extensions.ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
                "mirebalais.task.admit.label",
                "icon-h-sign",
                "link",
                standardHtmlFormLink("mirebalais:htmlforms/admissionNote.xml&returnProvider=coreapps&returnPage=adt/awaitingAdmission&returnLabel=coreapps.app.awaitingAdmission.label"),
                "Task: emr.enterClinicalForms",
                null));

        extensions.add(awaitingAdmissionAction(Extensions.DENY_ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
                "uicommons.cancel",
                "icon-remove",
                "link",
                standardHtmlFormLink("mirebalais:htmlforms/cancelAdmission.xml&returnProvider=coreapps&returnPage=adt/awaitingAdmission"),
                "Task: emr.enterClinicalForms",
                null));

        extensions.add(visitAction(Extensions.ADMISSION_NOTE_VISIT_ACTION,
                "mirebalais.task.admit.label",
                "icon-h-sign",
                "link",
                standardHtmlFormLink("mirebalais:htmlforms/admissionNote.xml"),
                null,
                "(user.get('fn').hasPrivilege('Task: emr.enterClinicalForms') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: emr.retroConsultNote') || (visit != null && (Date.now () - visit.stopDatetimeInMilliseconds)/(1000 * 60 * 60 * 24) <30 &&  user.get('fn').hasPrivilege('Task: emr.retroConsultNoteThisProviderOnly'))"));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.ADMISSION,
                findExtensionById(EncounterTemplates.DEFAULT), "icon-signin", null, true, null);

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.CANCEL_ADMISSION,
                findExtensionById(EncounterTemplates.DEFAULT), "icon-ban-circle", true, true, null);

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.TRANSFER,
                findExtensionById(EncounterTemplates.NO_DETAILS), "icon-share", null, true, null);

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.EXIT_FROM_CARE,
                findExtensionById(EncounterTemplates.NO_DETAILS), "icon-signout", null, true, null);
    }

    private void enableDeathCertificate() {

        extensions.add(overallAction(Extensions.DEATH_CERTIFICATE_OVERALL_ACTION,
                "mirebalais.deathCertificate.enter.label",
                "icon-remove-circle",
                "link",
                simpleHtmlFormLink("mirebalais:htmlforms/deathCertificate.xml"),
                "Task: mirebalais.enterDeathCertificate",
                "!patient.person.dead"
        ));

        extensions.add(fragmentExtension(Extensions.DEATH_CERTIFICATE_HEADER_EXTENSION,
                "mirebalais",
                "deathcertificate/headerLink",
                "Task: mirebalais.enterDeathCertificate",
                ExtensionPoints.DEATH_INFO_HEADER));

        addFeatureToggleToExtension(findExtensionById(Extensions.DEATH_CERTIFICATE_OVERALL_ACTION), "deathNote");
        addFeatureToggleToExtension(findExtensionById(Extensions.DEATH_CERTIFICATE_HEADER_EXTENSION), "deathNote");
    }

    private void enableRadiology() {

        extensions.add(dashboardTab(Extensions.RADIOLOGY_TAB,
                "radiologyapp.radiology.label",
                "Task: org.openmrs.module.radiologyapp.tab",
                "radiologyapp",
                "radiologyTab"));

        extensions.add(visitAction(Extensions.ORDER_XRAY_VISIT_ACTION,
                "radiologyapp.task.order.CR.label",
                "icon-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=CR",
                null,
                "(user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.orderXray') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.retroOrder')"));

        extensions.add(visitAction(Extensions.ORDER_CT_VISIT_ACTION,
                "radiologyapp.task.order.CT.label",
                "icon-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=Ct",
                null,
                "(user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.orderCT') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.retroOrder')"));

        extensions.add(visitAction(Extensions.ORDER_ULTRASOUND_VISIT_ACTION,
                "radiologyapp.task.order.US.label",
                "icon-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=US",
                null,
                "(user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.orderUS') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.retroOrder')"));

        registerTemplateForEncounterType(RadiologyMetadata.EncounterTypes.RADIOLOGY_ORDER,
                findExtensionById(EncounterTemplates.DEFAULT), "icon-x-ray");

        registerTemplateForEncounterType(RadiologyMetadata.EncounterTypes.RADIOLOGY_STUDY,
                findExtensionById(EncounterTemplates.DEFAULT), "icon-x-ray");

        registerTemplateForEncounterType(RadiologyMetadata.EncounterTypes.RADIOLOGY_REPORT,
                findExtensionById(EncounterTemplates.DEFAULT), "icon-x-ray");
    }

    private void enableDispensing() {

        // TODO change this to use the coreapps find patient app?
        apps.add(addToHomePage(app(Apps.DISPENSING,
                "dispensing.app.label",
                "icon-medicine",
                "dispensing/findPatient.page",
                "App: dispensing.app.dispense",
                null)));

        extensions.add(visitAction(Extensions.DISPENSE_MEDICATION_VISIT_ACTION,
                "dispensing.app.label",
                "icon-medicine",
                "link",
                standardHtmlFormLink("dispensing:htmlforms/dispensing.xml"),
                "Task: mirebalais.dispensing",
                null));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.MEDICATION_DISPENSED,
                findExtensionById(EncounterTemplates.DEFAULT), "icon-medicine", true, true, "bad21515-fd04-4ff6-bfcd-78456d12f168");

    }

    private void enableSurgery() {

        extensions.add(visitAction(Extensions.SURGICAL_NOTE_VISIT_ACTION,
                "mirebalais.task.surgicalOperativeNote.label",
                "icon-paste",
                "link",
                standardHtmlFormLink("mirebalais:htmlforms/surgicalPostOpNote.xml"),
                "Task: emr.enterClinicalForms",
                null));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.POST_OPERATIVE_NOTE,
                findExtensionById(EncounterTemplates.DEFAULT), "icon-paste", true, true, "9b135b19-7ebe-4a51-aea2-69a53f9383af");
        }

    private void enableOverviewReports() {

        // both overReports and dataExports define this, so make sure if both are turned on we don't config it twice
        if (findAppById(Apps.REPORTS) == null) {
            apps.add(addToHomePage(app(Apps.REPORTS,
                    "reportingui.reportsapp.home.title",
                    "icon-list-alt",
                    "reportingui/reportsapp/home.page",
                    "App: reportingui.reports",
                    null)));
        }

        extensions.add(dailyReport(Extensions.DAILY_REGISTRATIONS_OVERVIEW_REPORT,
                "mirebalaisreports.dailyRegistrations.name",
                MirebalaisReportsProperties.DAILY_REGISTRATIONS_REPORT_DEFINITION_UUID,
                "App: reportingui.reports",
                "mirebalaisreports-dailyRegistrationsReport-link"));

        extensions.add(dailyReport(Extensions.DAILY_CHECK_INS_OVERVIEW_REPORT,
                "mirebalaisreports.dailyCheckInEncounters.name",
                MirebalaisReportsProperties.DAILY_CHECK_INS_REPORT_DEFINITION_UUID,
                "App: reportingui.reports",
                "mirebalaisreports-dailyCheckInsReport-link"));

        extensions.add(dailyReport(Extensions.DAILY_CLINICAL_ENCOUNTERS_OVERVIEW_REPORT,
                "mirebalaisreports.dailyClinicalEncounters.name",
                MirebalaisReportsProperties.DAILY_CLINICAL_ENCOUNTERS_REPORT_DEFINITION_UUID,
                "App: reportingui.reports",
                "mirebalaisreports-dailyClinicalEncountersReport-link"));

        // custom overview report
        extensions.add(extension(Extensions.DAILY_INPATIENTS_OVERVIEW_REPORT,
                "mirebalaisreports.inpatientStatsDailyReport.name",
                null,
                "link",
                "mirebalaisreports/inpatientStatsDailyReport.page",
                "App: reportingui.reports",
                null,
                ExtensionPoints.REPORTING_OVERVIEW_REPORTS,
                REPORTING_OVERVIEW_REPORTS_ORDER.indexOf(Extensions.DAILY_INPATIENTS_OVERVIEW_REPORT),
                map("linkId", "mirebalaisreports-inpatientDailyReport-link")));

        extensions.add(overviewReport(Extensions.MONTHLY_INPATIENTS_OVERVIEW_REPORT,
                "mirebalaisreports.inpatientStatsMonthlyReport.name",
                MirebalaisReportsProperties.INPATIENT_STATS_MONTHLY_REPORT_DEFINITION_UUID,
                "App: reportingui.reports",
                "mirebalaisreports-inpatientMonthlyReport-link"));

        extensions.add(extension(Extensions.NON_CODED_DIAGNOSES_DATA_QUALITY_REPORT,
                "mirebalaisreports.noncodeddiagnoses.name",
                null,
                "link",
                "mirebalaisreports/nonCodedDiagnoses.page",
                "App: reportingui.reports",
                null,
                ExtensionPoints.REPORTING_DATA_QUALITY,
                0,
                map("linkId", "mirebalaisreports-nonCodedDiagnosesReport-link")));
    }

    private void enableDataExports() {

        // both overReports and dataExports define this, so make sure if both are turned on we don't config it twice
        if (findAppById(Apps.REPORTS) == null) {
            apps.add(addToHomePage(app(Apps.REPORTS,
                    "reportingui.reportsapp.home.title",
                    "icon-list-alt",
                    "reportingui/reportsapp/home.page",
                    "App: reportingui.reports",
                    null)));
        }

        extensions.addAll(fullDataExportBuilder.getExtensions());

        extensions.add(extension(Extensions.REPORTING_AD_HOC_ANALYSIS,
                "reportingui.adHocAnalysis.label",
                null,
                "link",
                "reportingui/adHocManage.page",
                "App: reportingui.adHocAnalysis",
                null,
                ExtensionPoints.REPORTING_DATA_EXPORT,
                9999,
                null));

        extensions.add(dataExport(Extensions.USERS_AND_PROVIDERS_DATA_EXPORT,
                "mirebalaisreports.userAndProviders.name",
                MirebalaisReportsProperties.USERS_AND_PROVIDERS_REPORT_DEFINITION_UUID,
                "App: mirebalaisreports.dataexports",
                "mirebalaisreports-usersAndProvidersReport-link" ));

        // custom data export report LQAS report report
        extensions.add(extension(Extensions.LQAS_DATA_EXPORT,
                "mirebalaisreports.lqasdiagnoses.name",
                null,
                "link",
                "mirebalaisreports/lqasDiagnoses.page",
                "App: mirebalaisreports.dataexports",
                null,
                ExtensionPoints.REPORTING_DATA_EXPORT,
                REPORTING_DATA_EXPORT_REPORTS_ORDER.indexOf(Extensions.DAILY_INPATIENTS_OVERVIEW_REPORT) + 100,
                map("linkId", "mirebalaisreports-lqasDiagnosesReport-link")));

        extensions.add(dataExport(Extensions.ALL_PATIENTS_WITH_IDS_DATA_EXPORT,
                "mirebalaisreports.allpatientswithids.name",
                MirebalaisReportsProperties.ALL_PATIENTS_WITH_IDS_REPORT_DEFINITION_UUID,
                "App: mirebalaisreports.dataexports",
                "mirebalaisreports-allPatientsWithIdsReport-link"));

        extensions.add(dataExport(Extensions.APPOINTMENTS_DATA_EXPORT,
                "mirebalaisreports.appointments.name",
                MirebalaisReportsProperties.APPOINTMENTS_REPORT_DEFINITION_UUID,
                "App: mirebalaisreports.dataexports",
                "mirebalaisreports-appointments-link"));

        addFeatureToggleToExtension(findExtensionById(Extensions.REPORTING_AD_HOC_ANALYSIS), "reporting_adHocAnalysis");

    }

    private void enableArchives() {

        apps.add(addToHomePage(app(Apps.ARCHIVES_ROOM,
                "paperrecord.app.archivesRoom.label",
                "icon-folder-open",
                "paperrecord/archivesRoom.page",
                "App: emr.archivesRoom",
                null)));

        extensions.add(overallAction(Extensions.REQUEST_PAPER_RECORD_OVERALL_ACTION,
                "paperrecord.task.requestPaperRecord.label",
                "icon-folder-open",
                "script",
                "showRequestChartDialog()",
                "Task: emr.requestPaperRecord",
                null));

        extensions.add(overallAction(Extensions.PRINT_ID_CARD_OVERALL_ACTION,
                "paperrecord.task.printIdCardLabel.label",
                "icon-print",
                "script",
                "printIdCardLabel()",
                "Task: emr.printLabels",
                null));

        extensions.add(overallAction(Extensions.PRINT_PAPER_FORM_LABEL_OVERALL_ACTION,
                "paperrecord.task.printPaperFormLabel.label",
                "icon-print",
                "script",
                "printPaperFormLabel()",
                "Task: emr.printLabels",
                null));

        // this provides the javascript the backs the three overall action buttons
        extensions.add(fragmentExtension(Extensions.PAPER_RECORD_ACTIONS_INCLUDES,
                "paperrecord",
                "patientdashboard/overallActionsIncludes",
                null,
                ExtensionPoints.DASHBOARD_INCLUDE_FRAGMENTS));

    }

    public void enableWristbands() {

        extensions.add(overallAction(Extensions.PRINT_WRISTBAND_OVERALL_ACTION,
                "mirebalais.printWristband",
                "icon-print",
                "script",
                "printWristband()",
                "Task: emr.printLabels",
                null));

        // this provides the javascript the backs the overall action button
        extensions.add(fragmentExtension(Extensions.PRINT_WRISTBAND_ACTION_INCLUDES,
                "mirebalais",
                "wristband/printWristband",
                null,
                ExtensionPoints.DASHBOARD_INCLUDE_FRAGMENTS));

    }

    private void enableAppointmentScheduling() {

        apps.add(addToHomePage(app(Apps.APPOINTMENT_SCHEDULING_HOME,
                "appointmentschedulingui.home.title",
                "icon-calendar",
                "/appointmentschedulingui/home.page",
                "App: appointmentschedulingui.home",
                null)));

        apps.add(addToOverallActions(findPatientTemplateApp(Apps.SCHEDULE_APPOINTMENT,
                        "appointmentschedulingui.scheduleAppointment.buttonTitle",
                        "icon-calendar",
                        "Task: appointmentschedulingui.bookAppointments",
                        "/appointmentschedulingui/manageAppointments.page?patientId={{patientId}}&breadcrumbOverride={{breadcrumbOverride}}",
                        arrayNode(objectNode("icon", "icon-home", "link", "/index.html"),
                                objectNode("label", "appointmentschedulingui.home.title", "link", "/appointmentschedulingui/home.page"),
                                objectNode("label", "appointmentschedulingui.scheduleAppointment.buttonTitle"))),
                "appointmentschedulingui.scheduleAppointment.title"));

        extensions.add(overallAction(Extensions.REQUEST_APPOINTMENT_OVERALL_ACTION,
                "appointmentschedulingui.requestAppointment.label",
                "icon-calendar",
                "link",
                "/appointmentschedulingui/requestAppointment.page?patientId={{patient.uuid}}&returnProvider=coreapps&returnPage=patientdashboard/patientDashboard",
                "Task: appointmentschedulingui.requestAppointments",
                null));

        extensions.add(dashboardTab(Extensions.APPOINTMENTS_TAB,
                "appointmentschedulingui.appointmentsTab.label",
                "App: appointmentschedulingui.viewAppointments",
                "appointmentschedulingui",
                "appointmentsTab"));

        addFeatureToggleToExtension(findExtensionById(Extensions.APPOINTMENTS_TAB), "requestAppointments");
        addFeatureToggleToExtension(findExtensionById(Extensions.REQUEST_APPOINTMENT_OVERALL_ACTION), "requestAppointments");

    }

    private void enableSystemAdministration() {

        if (findAppById(Apps.SYSTEM_ADMINISTRATION) == null) {
            apps.add(addToHomePage(app(Apps.SYSTEM_ADMINISTRATION,
                    "coreapps.app.system.administration.label",
                    "icon-cogs",
                    "coreapps/systemadministration/systemAdministration.page",
                    "App: emr.systemAdministration",
                    null)));
        }

        apps.add(addToSystemAdministrationPage(app(Apps.MANAGE_ACCOUNTS,
                "emr.task.accountManagement.label",
                "icon-book",
                "emr/account/manageAccounts.page",
                "App: emr.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(Apps.MERGE_PATIENTS,
                "emr.mergePatients",
                "icon-group",
                "emr/mergePatients.page",
                "App: emr.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(Apps.REGISTER_TEST_PATIENT,
                "emr.testPatient.registration",
                "icon-register",
                "mirebalais/patientRegistration/appRouter.page?task=patientRegistration&testPatient=true",
                "App: emr.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(Apps.FEATURE_TOGGLES,
                "emr.advancedFeatures",
                "icon-search",
                "mirebalais/toggles.page",
                "App: emr.systemAdministration",
                null)));

        addFeatureToggleToApp(findAppById(Apps.REGISTER_TEST_PATIENT), "registerTestPatient");

    }

    private void enableManagePrinters() {

        if (findAppById(Apps.SYSTEM_ADMINISTRATION) == null) {
            apps.add(addToHomePage(app(Apps.SYSTEM_ADMINISTRATION,
                    "coreapps.app.system.administration.label",
                    "icon-cogs",
                    "coreapps/systemadministration/systemAdministration.page",
                    "App: emr.systemAdministration",
                    null)));
        }

        apps.add(addToSystemAdministrationPage(app(Apps.MANAGE_PRINTERS,
            "printer.managePrinters",
            "icon-print",
            "printer/managePrinters.page",
            "App: emr.systemAdministration",
            null)));

        apps.add(addToSystemAdministrationPage(app(Apps.DEFAULT_PRINTERS,
                "printer.defaultPrinters",
                "icon-print",
                "printer/defaultPrinters.page",
                "App: emr.systemAdministration",
                null)));

    }

    private void enableMyAccount() {

        apps.add(addToHomePage(app(Apps.MY_ACCOUNT,
                "emr.app.system.administration.myAccount.label",
                "icon-cog",
                "emr/account/myAccount.page",
                null, null)));

        addFeatureToggleToApp(findAppById(Apps.MY_ACCOUNT), "myAccountFeature");
    }

    private void enablePatientRegistration() {

        ObjectNode addressHierarchyField = objectNode("type", "personAddress",
                "label", "registrationapp.patient.address.question",
                "widget", objectNode(
                        "providerName", "registrationapp",
                        "fragmentId", "field/personAddressWithHierarchy",
                        "config", objectNode(
                                "shortcutFor", "address1",
                                "manualFields", arrayNode(
                                        "address2"
                                ))));
        ObjectNode addressHierarchyQuestion = question("personAddressQuestion", "Person.address", addressHierarchyField);
        addressHierarchyQuestion.put("displayTemplate", "{{field.[6]}}, {{field.[5]}}, {{field.[4]}}, {{field.[3]}}, {{field.[2]}}");

        apps.add(addToHomePage(app(Apps.PATIENT_REGISTRATION,
                "registrationapp.app.registerPatient.label",
                "icon-user",
                "registrationapp/registerPatient.page?appId=" + Apps.PATIENT_REGISTRATION,
                "App: patientregistration.main",
                patientRegistrationConfig("registrationapp/registerPatient.page?appId=" + Apps.PATIENT_REGISTRATION,
                        CoreMetadata.EncounterTypes.PATIENT_REGISTRATION,
                        CoreMetadata.EncounterRoles.ADMINISTRATIVE_CLERK,
                        section("contactInfo",
                                "registrationapp.patient.contactInfo.label",
                                addressHierarchyQuestion,
                                question("phoneNumberLabel",
                                        "registrationapp.patient.phone.label",
                                        field("phoneNumber",
                                                "registrationapp.patient.phone.question",
                                                "personAttribute",
                                                "14d4f066-15f5-102d-96e4-000c29c2a5d7",
                                                "uicommons",
                                                "field/text")
                                )
                        )
                )
        )));

        apps.add(addToClinicianDashboardSecondColumn(app(Apps.MOST_RECENT_REGISTRATION,
                        "mirebalais.mostRecentRegistration.label",
                        "icon-user",
                        null,
                        "App: patientregistration.main",  // TODO: should this have it's own privilege?
                        objectNode("encounterDateLabel", "mirebalais.mostRecentRegistration.encounterDateLabel",
                                "encounterTypeUuid", CoreMetadata.EncounterTypes.PATIENT_REGISTRATION,
                                "definitionUiResource", "mirebalais:htmlforms/patientRegistration.xml")),
                "coreapps",
                "encounter/mostRecentEncounter"));

        extensions.add(overallAction(Extensions.EDIT_PATIENT_DEMOGRAPHICS,
                "mirebalais.overallAction.editDemographics",
                "icon-edit",
                "link",
                "registrationapp/editPatientDemographics.page?patientId={{patient.patientId}}",
                "App: patientregistration.edit",
                null));

        extensions.add(overallAction(Extensions.EDIT_PATIENT_CONTACT_INFO,
                "mirebalais.overallAction.editContactInfo",
                "icon-edit",
                "link",
                "registrationapp/editPatientContactInfo.page?patientId={{patient.patientId}}&appId=" + Apps.PATIENT_REGISTRATION,
                "App: patientregistration.edit",
                null));

    }

    // legacy MPI used in Mirebalais to connect to Lacolline
    private void enableLegacyMPI() {
        apps.add(addToHomePage(app(Apps.LEGACY_MPI,
                "mirebalais.mpi.title",
                "icon-zoom-in",
                "mirebalais/mpi/findPatient.page",
                "App: mirebalais.mpi",
                null)));
    }

    private void enableLegacyPatientRegistration() {

        apps.add(addToHomePage(app(Apps.LEGACY_PATIENT_REGISTRATION,
                "mirebalais.app.patientRegistration.registration.label",
                "icon-register",
                "mirebalais/patientRegistration/appRouter.page?task=patientRegistration",
                "App: patientregistration.main",
                null)));

        apps.add(addToHomePage(app(Apps.LEGACY_PATIENT_REGISTRATION_ED,
                "mirebalais.app.patientRegistration.emergencyCheckin.label",
                "icon-hospital",
                "mirebalais/patientRegistration/appRouter.page?task=edCheckIn",
                "App: patientregistration.main",
                null)));

        apps.add(addToHomePage(app(Apps.LEGACY_PATIENT_LOOKUP,
                "mirebalais.app.patientRegistration.patientLookup.label",
                "icon-edit",
                "mirebalais/patientRegistration/appRouter.page?task=patientLookup",
                "App: patientregistration.edit",
                null)));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.PATIENT_REGISTRATION,
                findExtensionById(EncounterTemplates.NO_DETAILS), "icon-register");

    }

    private void registerLacollinePatientRegistrationEncounterTypes() {
        // TODO: I *believe* these are used in Lacolline, but not 100% sure
        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.PAYMENT,
                findExtensionById(EncounterTemplates.DEFAULT), "icon-money");
        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.PRIMARY_CARE_VISIT,
                findExtensionById(EncounterTemplates.DEFAULT), "icon-calendar");

    }

    public AppDescriptor findAppById(String id) {
        for (AppDescriptor app : apps) {
            if (app.getId().equals(id)) {
                return app;
            }
        }
        log.warn("App Not Found: " + id);
        return null;
    }

    public Extension findExtensionById(String id) {
        for (Extension extension : extensions) {
            if (extension.getId().equals(id)) {
                return extension;
            }
        }
        log.warn("Extension Not Found: " + id);
        return null;
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

    public void setConfig(Config config) {
        this.config = config;
    }
}
