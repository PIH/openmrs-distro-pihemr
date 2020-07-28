package org.openmrs.module.mirebalais.apploader;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.AppTemplate;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.factory.AppFrameworkFactory;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.apploader.apps.GraphFactory;
import org.openmrs.module.mirebalais.apploader.apps.patientregistration.PatientRegistrationApp;
import org.openmrs.module.mirebalaisreports.MirebalaisReportsProperties;
import org.openmrs.module.mirebalaisreports.definitions.BaseReportManager;
import org.openmrs.module.mirebalaisreports.definitions.FullDataExportBuilder;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.core.EncounterRoleBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.RelationshipTypeBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.VisitTypeBundle;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.core.LocationTags;
import org.openmrs.module.pihcore.metadata.core.Privileges;
import org.openmrs.module.pihcore.metadata.core.program.ANCProgram;
import org.openmrs.module.pihcore.metadata.core.program.AsthmaProgram;
import org.openmrs.module.pihcore.metadata.core.program.Covid19Program;
import org.openmrs.module.pihcore.metadata.core.program.DiabetesProgram;
import org.openmrs.module.pihcore.metadata.core.program.EpilepsyProgram;
import org.openmrs.module.pihcore.metadata.core.program.HIVProgram;
import org.openmrs.module.pihcore.metadata.core.program.HypertensionProgram;
import org.openmrs.module.pihcore.metadata.core.program.MCHProgram;
import org.openmrs.module.pihcore.metadata.core.program.MalnutritionProgram;
import org.openmrs.module.pihcore.metadata.core.program.MentalHealthProgram;
import org.openmrs.module.pihcore.metadata.core.program.NCDProgram;
import org.openmrs.module.pihcore.metadata.core.program.OncologyProgram;
import org.openmrs.module.pihcore.metadata.core.program.ZikaProgram;
import org.openmrs.module.pihcore.metadata.mexico.MexicoEncounterTypes;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeoneEncounterTypes;
import org.openmrs.module.reporting.config.ReportDescriptor;
import org.openmrs.module.reporting.config.ReportLoader;
import org.openmrs.ui.framework.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.Apps;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.EncounterTemplates;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ExtensionPoints;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.Extensions;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addFeatureToggleToExtension;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToAsthmaDashboardFirstColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToClinicianDashboardFirstColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToClinicianDashboardSecondColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToDiabetesDashboardFirstColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToDiabetesDashboardSecondColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToEpilepsyDashboardSecondColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToHivDashboardFirstColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToHivDashboardSecondColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToHomePage;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToHomePageWithoutUsingRouter;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToHypertensionDashboardFirstColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToHypertensionDashboardSecondColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToMalnutritionDashboardSecondColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToMentalHealthDashboardSecondColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToProgramDashboardFirstColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToProgramSummaryDashboardFirstColumn;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToProgramSummaryListPage;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToRegistrationSummaryContent;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToRegistrationSummarySecondColumnContent;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.addToSystemAdministrationPage;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.andCreateVisit;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.app;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.arrayNode;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.awaitingAdmissionAction;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.cloneApp;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.cloneAsHivOverallAction;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.cloneAsHivVisitAction;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.cloneAsOncologyOverallAction;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.cloneAsOncologyVisitAction;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.containsExtension;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.dailyReport;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.dashboardTab;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.dataExport;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.editSimpleHtmlFormLink;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.encounterTemplate;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.enterSimpleHtmlFormLink;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.enterStandardHtmlFormLink;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.extension;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.findPatientTemplateApp;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.fragmentExtension;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.header;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.map;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.monitoringReport;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.objectNode;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.overallAction;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.overallRegistrationAction;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.overviewReport;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.registerTemplateForEncounterType;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.visitAction;
import static org.openmrs.module.mirebalais.require.RequireUtil.and;
import static org.openmrs.module.mirebalais.require.RequireUtil.or;
import static org.openmrs.module.mirebalais.require.RequireUtil.patientAgeUnknown;
import static org.openmrs.module.mirebalais.require.RequireUtil.patientDoesNotActiveVisit;
import static org.openmrs.module.mirebalais.require.RequireUtil.patientHasActiveVisit;
import static org.openmrs.module.mirebalais.require.RequireUtil.patientIsAdult;
import static org.openmrs.module.mirebalais.require.RequireUtil.patientIsChild;
import static org.openmrs.module.mirebalais.require.RequireUtil.patientIsFemale;
import static org.openmrs.module.mirebalais.require.RequireUtil.patientNotDead;
import static org.openmrs.module.mirebalais.require.RequireUtil.patientVisitWithinPastThirtyDays;
import static org.openmrs.module.mirebalais.require.RequireUtil.sessionLocationHasTag;
import static org.openmrs.module.mirebalais.require.RequireUtil.userHasPrivilege;
import static org.openmrs.module.mirebalais.require.RequireUtil.visitDoesNotHaveEncounterOfType;
import static org.openmrs.module.mirebalais.require.RequireUtil.visitHasEncounterOfType;
import static org.openmrs.module.mirebalaisreports.definitions.BaseReportManager.REPORTING_DATA_EXPORT_REPORTS_ORDER;


@Component("customAppLoaderFactory")
public class CustomAppLoaderFactory implements AppFrameworkFactory {

    private final Log log = LogFactory.getLog(getClass());

    private Config config;

    private FeatureToggleProperties featureToggles;

    private PatientRegistrationApp patientRegistrationApp;

    private GraphFactory graphs;

    private FullDataExportBuilder fullDataExportBuilder;

    private List<AppDescriptor> apps = new ArrayList<AppDescriptor>();

    private List<Extension> extensions = new ArrayList<Extension>();

    private Boolean readyForRefresh = false;

    private String patientVisitsPageUrl = "";
private String patientVisitsPageWithSpecificVisitUrl = "";

    @Autowired
    public CustomAppLoaderFactory(Config config,
                                  FeatureToggleProperties featureToggles,
                                  PatientRegistrationApp patientRegistrationApp,
                                  FullDataExportBuilder fullDataExportBuilder,
                                  GraphFactory graphs) {
        this.config = config;
        this.featureToggles = featureToggles;
        this.patientRegistrationApp = patientRegistrationApp;
        this.fullDataExportBuilder = fullDataExportBuilder;
        this.graphs = graphs;
    }

    @Override
    public List<AppDescriptor> getAppDescriptors() throws IOException {
        if (readyForRefresh) {
            loadAppsAndExtensions();
        }
        return apps;
    }

    @Override
    public List<Extension> getExtensions() throws IOException {
        if (readyForRefresh) {
            loadAppsAndExtensions();
        }
        return extensions;
    }

    @Override
    public List<AppTemplate> getAppTemplates() throws IOException {
        return null;
    }


    private String addParametersToUrl(String url, Map<String, String> parameters){
        String urlParams = null;
        if ( StringUtils.isNotBlank(url) && parameters != null && parameters.size() > 0) {
            int separatorIndex = url.indexOf("?");
            StringBuilder sb = new StringBuilder()
                    .append(url.substring(0, separatorIndex))
                    .append("?");
            for (String param : parameters.keySet()) {
                String value = parameters.get(param);
                sb.append(param).append("=").append(value).append("&");
            }
            sb.append(url.substring(separatorIndex + 1));
            urlParams = sb.toString();
        }

        return urlParams;
    }

    private void loadAppsAndExtensions() {

        configureHeader(config);
        setupDefaultEncounterTemplates();

        //  whether we are using the new visit note
        if (config.isComponentEnabled(Components.VISIT_NOTE)) {
            patientVisitsPageUrl = "/pihcore/visit/visit.page?patient={{patient.uuid}}#/visitList";
            patientVisitsPageWithSpecificVisitUrl = "/pihcore/visit/visit.page?patient={{patient.uuid}}&visit={{visit.uuid}}#/overview";
        } else {
            patientVisitsPageUrl = "/coreapps/patientdashboard/patientDashboard.page?patientId={{patient.patientId}}";
            patientVisitsPageWithSpecificVisitUrl = patientVisitsPageUrl + "&visitId={{visit.visitId}}";
        }

        if (config.isComponentEnabled(Components.VISIT_MANAGEMENT)) {
            enableVisitManagement();
        }

        if (config.isComponentEnabled(Components.ACTIVE_VISITS)) {
            enableActiveVisits();
        }

        if (config.isComponentEnabled(Components.CHECK_IN)) {
            enableCheckIn(config);
        }

        if (config.isComponentEnabled(Components.UHM_VITALS) ||
                config.isComponentEnabled(Components.VITALS)) {
            enableVitals();
        }

        if (config.isComponentEnabled(Components.CONSULT)) {
            enableConsult();
        }

        if (config.isComponentEnabled(Components.ED_CONSULT)) {
            enableEDConsult();
        }

        if (config.isComponentEnabled(Components.ADT)) {
            enableADT();
        }

        if (config.isComponentEnabled(Components.DEATH_CERTIFICATE)) {
            enableDeathCertificate();
        }

        if (config.isComponentEnabled(Components.RADIOLOGY)) {
            enableRadiology();
        }

        if (config.isComponentEnabled(Components.DISPENSING)) {
            enableDispensing();
        }

        if (config.isComponentEnabled(Components.SURGERY)) {
            enableSurgery();
        }

        if (config.isComponentEnabled(Components.LAB_RESULTS)) {
            enableLabResults();
        }

        if (config.isComponentEnabled(Components.OVERVIEW_REPORTS)) {
            enableOverviewReports();
        }

        if (config.isComponentEnabled(Components.MONITORING_REPORTS)) {
            enableMonitoringReports();
        }

        if (config.isComponentEnabled(Components.DATA_EXPORTS)) {
            enableDataExports();
        }

        if (config.isComponentEnabled(Components.ARCHIVES)) {
            enableArchives();
        }

        if (config.isComponentEnabled(Components.WRISTBANDS)) {
            enableWristbands();
        }

        if (config.isComponentEnabled(Components.APPOINTMENT_SCHEDULING)) {
            enableAppointmentScheduling();
        }

        if (config.isComponentEnabled(Components.SYSTEM_ADMINISTRATION)) {
            enableSystemAdministration();
        }

        if (config.isComponentEnabled(Components.MANAGE_PRINTERS)) {
            enableManagePrinters();
        }

        if (config.isComponentEnabled(Components.MY_ACCOUNT)) {
            enableMyAccount();
        }

        if (config.isComponentEnabled(Components.PATIENT_REGISTRATION)) {
            enablePatientRegistration();
        }

        if (config.isComponentEnabled(Components.LEGACY_MPI)) {
            enableLegacyMPI();
        }

        if (config.isComponentEnabled(Components.LACOLLINE_PATIENT_REGISTRATION_ENCOUNTER_TYPES)) {
            registerLacollinePatientRegistrationEncounterTypes();
        }

        if (config.isComponentEnabled(Components.CLINICIAN_DASHBOARD)) {
            enableClinicianDashboard();
        }

        if (config.isComponentEnabled(Components.ALLERGIES)) {
            enableAllergies();
        }

        // will need to add chart search module back to distro if we want to use this again
        if (config.isComponentEnabled(Components.CHART_SEARCH)) {
            enableChartSearch();
        }

        if (config.isComponentEnabled(Components.WAITING_FOR_CONSULT)) {
            enableWaitingForConsult();
        }

        if (config.isComponentEnabled(Components.PRIMARY_CARE)) {
            enablePrimaryCare();
        }

        if (config.isComponentEnabled(Components.ED_TRIAGE)) {
            enableEDTriage();
        }

        if (config.isComponentEnabled(Components.ED_TRIAGE_QUEUE)) {
            enableEDTriageQueue();
        }

        if (config.isComponentEnabled(Components.CHW_APP)) {
            enableCHWApp();
        }

        if (config.isComponentEnabled(Components.BIOMETRICS_FINGERPRINTS)) {
            enableBiometrics(config);
        }

        if (config.isComponentEnabled(Components.TODAYS_VISITS)) {
            enableTodaysVisits();
        }

        if (config.isComponentEnabled(Components.PATHOLOGY_TRACKING)) {
            enablePathologyTracking();
        }

        if (config.isComponentEnabled(Components.LABS)) {
            enableLabs();
        }

        if (config.isComponentEnabled(Components.GROWTH_CHART)) {
            enableGrowthChart();
        }

        if (config.isComponentEnabled(Components.PROGRAMS)) {
            enablePrograms(config);
        }

        if (config.isComponentEnabled(Components.RELATIONSHIPS)) {
            enableRelationships();
        }

        if (config.isComponentEnabled(Components.PROVIDER_RELATIONSHIPS)) {
            enableProviderRelationships();
        }

        if (config.isComponentEnabled(Components.EXPORT_PATIENTS)) {
            enableExportPatients();
        }

        if (config.isComponentEnabled(Components.IMPORT_PATIENTS)) {
            enableImportPatients();
        }

        if (config.isComponentEnabled(Components.PATIENT_DOCUMENTS)) {
            enablePatientDocuments();
        }

        if (config.isComponentEnabled(Components.CONDITION_LIST)) {
            enableConditionList();
        }

        if (config.isComponentEnabled(Components.VCT)) {
            enableVCT();
        }

        if (config.isComponentEnabled(Components.SOCIO_ECONOMICS)) {
            enableSocioEconomics();
        }

//        if (config.isComponentEnabled(Components.ORDER_ENTRY)) {
//            enableOrderEntry();
//        }

        if (config.isComponentEnabled(Components.COHORT_BUILDER)) {
            enableCohortBuilder();
        }

        if (config.isComponentEnabled(Components.CHEMOTHERAPY)) {
            enableChemotherapy();
        }

        if (config.isComponentEnabled(Components.MCH_FORMS)) {
            enableMCHForms();
        }

        if (config.isComponentEnabled(Components.J9)) {
            enableJ9();
        }

        if (config.isComponentEnabled(Components.COVID19)) {
            enableCovid19();
        }

        readyForRefresh = false;
    }

    private void configureHeader(Config config) {
        extensions.add(header(Extensions.PIH_HEADER_EXTENSION, "/ms/uiframework/resource/file/configuration/pih/logo/logo.png"));
    }

    // TODO will these be needed/used after we switch to the visit note view?
    private void setupDefaultEncounterTemplates() {

        extensions.add(encounterTemplate(CustomAppLoaderConstants.EncounterTemplates.DEFAULT,
                "coreapps",
                "patientdashboard/encountertemplate/defaultEncounterTemplate"));

        extensions.add(encounterTemplate(EncounterTemplates.NO_DETAILS,
                "coreapps",
                "patientdashboard/encountertemplate/noDetailsEncounterTemplate"));

        extensions.add(encounterTemplate(EncounterTemplates.ED_TRIAGE,
                "edtriageapp",
                "edtriageEncounterTemplate"));

    }

    // TODO does this need to be modified for the new visit note at all?
    private void enableVisitManagement() {

        extensions.add(overallAction(Extensions.CREATE_VISIT_OVERALL_ACTION,
                "coreapps.task.startVisit.label",
                "fas fa-fw icon-check-in",
                "script",
                "visit.showQuickVisitCreationDialog({{patient.patientId}})",
                "Task: coreapps.createVisit",
                and(patientDoesNotActiveVisit(), patientNotDead())));

        extensions.add(overallAction(Extensions.CREATE_RETROSPECTIVE_VISIT_OVERALL_ACTION,
                "coreapps.task.createRetrospectiveVisit.label",
                "fas fa-fw fa-plus",
                "script",
                "visit.showRetrospectiveVisitCreationDialog()",
                "Task: coreapps.createRetrospectiveVisit",
                null));

        extensions.add(overallAction(Extensions.MERGE_VISITS_OVERALL_ACTION,
                "coreapps.task.mergeVisits.label",
                "fas fa-fw fa-link",
                "link",
                "coreapps/mergeVisits.page?patientId={{patient.uuid}}",
                "Task: coreapps.mergeVisits",
                null));

        // this provides the javascript & dialogs the backs the overall action buttons (to start/end visits, etc)
        extensions.add(fragmentExtension(Extensions.VISIT_ACTIONS_INCLUDES,
                "coreapps",
                "patientdashboard/visitIncludes",
                null,
                ExtensionPoints.DASHBOARD_INCLUDE_FRAGMENTS,
                map("patientVisitsPage", patientVisitsPageWithSpecificVisitUrl,
                        "visitType", VisitTypeBundle.VisitTypes.CLINIC_OR_HOSPITAL_VISIT)));

    }

    private void enableActiveVisits() {

        apps.add(addToHomePage(app(Apps.ACTIVE_VISITS_LIST,
                "coreapps.activeVisits.app.label",
                "fas fa-fw icon-check-in",
                "pihcore/reports/activeVisitsList.page?app=" + Apps.ACTIVE_VISITS,
                "App: coreapps.activeVisits",
                objectNode("patientPageUrl", patientVisitsPageWithSpecificVisitUrl))));

    }

    private void enableCheckIn(Config config) {

        // currently, this app is hard-coded to the default check-in form and requires archives room (?)
        if (config.isComponentEnabled(Components.CHECK_IN_HOMEPAGE_APP)) {
            apps.add(addToHomePage(findPatientTemplateApp(Apps.CHECK_IN,
                    "mirebalais.app.patientRegistration.checkin.label",
                    "fas fa-fw fa-paste",
                    "App: mirebalais.checkin",
                    "/pihcore/checkin/checkin.page?patientId={{patientId}}",
                    //     "/registrationapp/registrationSummary.page?patientId={{patientId}}&breadcrumbOverrideProvider=coreapps&breadcrumbOverridePage=findpatient%2FfindPatient&breadcrumbOverrideApp=" + Apps.CHECK_IN + "&breadcrumbOverrideLabel=mirebalais.app.patientRegistration.checkin.label",
                    null),
                    sessionLocationHasTag(LocationTags.CHECKIN_LOCATION)));
        }

        extensions.add(visitAction(Extensions.CHECK_IN_VISIT_ACTION,
                "mirebalais.task.checkin.label",
                "fas fa-fw icon-check-in",
                "link",
                enterSimpleHtmlFormLink(PihCoreUtil.getFormResource("checkin.xml")),
                "Task: mirebalais.checkinForm",
                sessionLocationHasTag(LocationTags.CHECKIN_LOCATION)));

        extensions.add(overallRegistrationAction(Extensions.CHECK_IN_REGISTRATION_ACTION,
                "mirebalais.task.checkin.label",
                "fas fa-fw icon-check-in",
                "link",
                enterSimpleHtmlFormLink(PihCoreUtil.getFormResource("liveCheckin.xml")) + andCreateVisit(),
                "Task: mirebalais.checkinForm",
                sessionLocationHasTag(LocationTags.CHECKIN_LOCATION)));

        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        registerTemplateForEncounterType(EncounterTypes.CHECK_IN,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw icon-check-in", true, true,
                editSimpleHtmlFormLink(PihCoreUtil.getFormResource("checkin.xml")), null);
    }

    private void enableVitals() {

        if (config.isComponentEnabled(Components.UHM_VITALS)) {
            // custom vitals app used in Mirebalais
            apps.add(addToHomePage(findPatientTemplateApp(Apps.UHM_VITALS,
                    "mirebalais.outpatientVitals.title",
                    "fas fa-fw fa-heartbeat",
                    "App: mirebalais.outpatientVitals",
                    "/mirebalais/outpatientvitals/patient.page?patientId={{patientId}}",
                    null),
                    sessionLocationHasTag(LocationTags.VITALS_LOCATION)));
        } else {
            apps.add(addToHomePage(app(Apps.VITALS,
                    "pihcore.vitalsList.title",
                    "fas fa-fw fa-heartbeat",
                    "/pihcore/vitals/vitalsList.page",
                    "App: mirebalais.outpatientVitals",  // TODO rename this permission to not be mirebalais-specific?
                    null)));

        }

        extensions.add(visitAction(Extensions.VITALS_CAPTURE_VISIT_ACTION,
                "mirebalais.task.vitals.label",
                "fas fa-fw fa-heartbeat",
                "link",
                enterSimpleHtmlFormLink(PihCoreUtil.getFormResource("vitals.xml")),
                null,
                and(sessionLocationHasTag(LocationTags.VITALS_LOCATION),
                        or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_VITALS_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

        AppDescriptor mostRecentVitals = app(Apps.MOST_RECENT_VITALS,
                "mirebalais.mostRecentVitals.label",
                "fas fa-fw fa-heartbeat",
                null,
                "App: mirebalais.outpatientVitals",
                objectNode("encounterDateLabel", "mirebalais.mostRecentVitals.encounterDateLabel",
                        "encounterTypeUuid", EncounterTypes.VITALS.uuid(),
                        "editable", Boolean.TRUE,
                        "edit-provider", "htmlformentryui",
                        "edit-fragment", "htmlform/editHtmlFormWithSimpleUi",
                        "definitionUiResource", PihCoreUtil.getFormResource("vitals.xml"),
                        "returnProvider", "coreapps",
                        "returnPage", "clinicianfacing/patient"));

        apps.add(addToClinicianDashboardSecondColumn(mostRecentVitals, "coreapps", "encounter/mostRecentEncounter"));
        apps.add(addToHivDashboardSecondColumn(cloneApp(mostRecentVitals, Apps.HIV_LAST_VITALS), "coreapps", "encounter/mostRecentEncounter"));

        if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE) ) {
            apps.add(addToClinicianDashboardFirstColumn(app(Apps.VITALS_SUMMARY,
                    "mirebalais.vitalsTrend.label",
                    "fas fa-fw fa-heartbeat",
                    null,
                    null,
                    objectNode(
                            "widget", "obsacrossencounters",
                            "icon", "fas fa-fw fa-heartbeat",
                            "label", "mirebalais.vitalsTrend.label",
                            "encounterType", EncounterTypes.VITALS.uuid(),
                            "detailsUrl", patientVisitsPageUrl,
                            "headers", "zl.date,mirebalais.vitals.short.heartRate.title,mirebalais.vitals.short.temperature.title,mirebalais.vitals.systolic.bp.short.title,mirebalais.vitals.diastolic.bp.short.title,mirebalais.vitals.respiratoryRate.short.title",
                            "concepts", MirebalaisConstants.HEART_RATE_UUID + "," +
                                    MirebalaisConstants.TEMPERATURE_UUID + "," +
                                    MirebalaisConstants.SYSTOLIC_BP_CONCEPT_UUID + "," +
                                    MirebalaisConstants.DIASTOLIC_BP_CONCEPT_UUID  + "," +
                                    MirebalaisConstants.RESPIRATORY_RATE_UUID,
                            "maxRecords", "5"
                    )),
                    "coreapps", "dashboardwidgets/dashboardWidget"));
        }

        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        registerTemplateForEncounterType(EncounterTypes.VITALS,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-heartbeat", null, true,
                editSimpleHtmlFormLink(PihCoreUtil.getFormResource("vitals.xml")), null);

    }

    private void enableConsult() {

        extensions.add(visitAction(Extensions.CONSULT_NOTE_VISIT_ACTION,
                "emr.clinic.consult.title",
                "fas fa-fw fa-stethoscope",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("outpatientConsult.xml")),
                null,
                and(sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION),
                        or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        extensions.add(encounterTemplate(EncounterTemplates.CONSULT, "mirebalais", "patientdashboard/encountertemplate/consultEncounterTemplate"));

        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        registerTemplateForEncounterType(EncounterTypes.CONSULTATION,
                findExtensionById(EncounterTemplates.CONSULT), "fas fa-fw fa-stethoscope", null, true, null, null);
    }

    private void enableEDConsult() {

        extensions.add(visitAction(Extensions.ED_CONSULT_NOTE_VISIT_ACTION,
                "emr.ed.consult.title",
                "fas fa-fw fa-stethoscope",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("edNote.xml")),
                null,
                and(sessionLocationHasTag(LocationTags.ED_NOTE_LOCATION),
                        or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_ED_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));
    }

    private void enableADT() {

        apps.add(addToHomePage(app(Apps.AWAITING_ADMISSION,
                "coreapps.app.awaitingAdmission.label",
                "fas fa-fw fa-list-ul",
                "coreapps/adt/awaitingAdmission.page?app=" + Apps.AWAITING_ADMISSION,
                "App: coreapps.awaitingAdmission",
                objectNode("patientPageUrl", config.getDashboardUrl()))));

        apps.add(addToHomePage(app(Apps.INPATIENTS,
                "mirebalaisreports.app.inpatients.label",
                "fas fa-fw fa-hospital",
                "mirebalaisreports/inpatientList.page",
                "App: emr.inpatients",
                null),
                sessionLocationHasTag(LocationTags.INPATIENTS_APP_LOCATION)));

        extensions.add(awaitingAdmissionAction(Extensions.ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
                "mirebalais.task.admit.label",
                "fas fa-fw fa-hospital-symbol",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("admissionNote.xml") + "&returnProvider=coreapps&returnPage=adt/awaitingAdmission&returnLabel=coreapps.app.awaitingAdmission.label"),
                "Task: emr.enterAdmissionNote",
                null));

        extensions.add(awaitingAdmissionAction(Extensions.DENY_ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
                "uicommons.cancel",
                "fas fa-fw fa-user-minus",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("cancelAdmission.xml") + "&returnProvider=coreapps&returnPage=adt/awaitingAdmission"),
                "Task: emr.enterAdmissionNote",
                null));

        extensions.add(visitAction(Extensions.ADMISSION_NOTE_VISIT_ACTION,
                "mirebalais.task.admit.label",
                "fas fa-fw fa-hospital-symbol",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("admissionNote.xml")),
                null,
                and(sessionLocationHasTag(LocationTags.ADMISSION_NOTE_LOCATION),
                        or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_ADMISSION_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

        // TODO will these be needed after we stop using the old patient visits page view?
        registerTemplateForEncounterType(EncounterTypes.ADMISSION,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-sign-in-alt", null, true, null, null);

        registerTemplateForEncounterType(EncounterTypes.CANCEL_ADMISSION,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-ban", true, true, null, null);

        registerTemplateForEncounterType(EncounterTypes.TRANSFER,
                findExtensionById(EncounterTemplates.NO_DETAILS), "fas fa-fw fa-share", null, true, null, null);

        registerTemplateForEncounterType(EncounterTypes.EXIT_FROM_CARE,
                findExtensionById(EncounterTemplates.NO_DETAILS), "fas fa-fw fa-sign-out-alt", null, true, null, null);
    }

    private void enableDeathCertificate() {

        extensions.add(overallAction(Extensions.DEATH_CERTIFICATE_OVERALL_ACTION,
                "mirebalais.deathCertificate.death_certificate",
                "fas fa-fw fa-times-circle",
                "link",
                enterSimpleHtmlFormLink(PihCoreUtil.getFormResource("deathCertificate.xml")),
                "Task: mirebalais.enterDeathCertificate",
                "!patient.person.dead"
        ));

        extensions.add(fragmentExtension(Extensions.DEATH_CERTIFICATE_HEADER_EXTENSION,
                "mirebalais",
                "deathcertificate/headerLink",
                "Task: mirebalais.enterDeathCertificate",
                ExtensionPoints.DEATH_INFO_HEADER,
                null));
    }

    private void enableRadiology() {

        extensions.add(dashboardTab(Extensions.RADIOLOGY_TAB,
                "radiologyapp.radiology.label",
                "Task: org.openmrs.module.radiologyapp.tab",
                "radiologyapp",
                "radiologyTab"));

        extensions.add(visitAction(Extensions.ORDER_XRAY_VISIT_ACTION,
                "radiologyapp.task.order.CR.label",
                "fas fa-fw fa-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=CR",
                null,
                and(sessionLocationHasTag(LocationTags.ORDER_RADIOLOGY_STUDY_LOCATION),
                        or(and(userHasPrivilege(Privileges.TASK_RADIOLOGYAPP_ORDER_XRAY), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_RADIOLOGYAPP_RETRO_ORDER)))));

        extensions.add(visitAction(Extensions.ORDER_CT_VISIT_ACTION,
                "radiologyapp.task.order.CT.label",
                "fas fa-fw fa-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=Ct",
                null,
                and(sessionLocationHasTag(LocationTags.ORDER_RADIOLOGY_STUDY_LOCATION),
                        or(and(userHasPrivilege(Privileges.TASK_RADIOLOGYAPP_ORDER_CT), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_RADIOLOGYAPP_RETRO_ORDER)))));

        extensions.add(visitAction(Extensions.ORDER_ULTRASOUND_VISIT_ACTION,
                "radiologyapp.task.order.US.label",
                "fas fa-fw fa-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=US",
                null,
                and(sessionLocationHasTag(LocationTags.ORDER_RADIOLOGY_STUDY_LOCATION),
                        or(and(userHasPrivilege(Privileges.TASK_RADIOLOGYAPP_ORDER_US), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_RADIOLOGYAPP_RETRO_ORDER)))));

        if (config.isComponentEnabled(Components.CLINICIAN_DASHBOARD)) {
            apps.add(addToClinicianDashboardFirstColumn(app(Apps.RADIOLOGY_ORDERS_APP,
                    "radiologyapp.app.orders",
                    "fas fa-fw fa-camera",
                    "null",
                    "Task: org.openmrs.module.radiologyapp.tab",
                    null),
                    "radiologyapp", "radiologyOrderSection"));

            apps.add(addToClinicianDashboardFirstColumn(app(Apps.RADIOLOGY_APP,
                    "coreapps.clinicianfacing.radiology",
                    "fas fa-fw fa-camera",
                    "null",
                    "Task: org.openmrs.module.radiologyapp.tab",
                    null),
                    "radiologyapp", "radiologySection"));
        }

        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        registerTemplateForEncounterType(EncounterTypes.RADIOLOGY_ORDER,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-x-ray");

        registerTemplateForEncounterType(EncounterTypes.RADIOLOGY_STUDY,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-x-ray");

        registerTemplateForEncounterType(EncounterTypes.RADIOLOGY_REPORT,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-x-ray");
    }

    private void enableDispensing() {

        // TODO change this to use the coreapps find patient app?
        apps.add(addToHomePage(app(Apps.DISPENSING,
                "dispensing.app.label",
                "fas fa-fw fa-pills",
                "dispensing/findPatient.page",
                "App: dispensing.app.dispense",
                null),
                sessionLocationHasTag(LocationTags.DISPENSING_LOCATION)));

        extensions.add(visitAction(Extensions.DISPENSE_MEDICATION_VISIT_ACTION,
                "dispensing.app.label",
                "fas fa-fw fa-pills",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("dispensing.xml")),
                "Task: mirebalais.dispensing",
                sessionLocationHasTag(LocationTags.DISPENSING_LOCATION)));

        // ToDo:  Add this back when the widget is changes to show all obs groups (not just one) per encounter

        apps.add(addToClinicianDashboardFirstColumn(app(Apps.DISPENSING_SUMMARY,
                "mirebalais.dispensing.title",
                "fas fa-fw fa-pills",
                "dispensing/patient.page?patientId={{patient.uuid}}",
                null,
                objectNode(
                        "widget", "obsacrossencounters",
                        "icon", "fas fa-fw fa-pills",
                        "label", "mirebalais.dispensing.title",
                        "encounterType", EncounterTypes.MEDICATION_DISPENSED.uuid(),
                        "detailsUrl", "dispensing/dispensingSummary.page?patientId={{patient.uuid}}",
                        "concepts", MirebalaisConstants.MED_DISPENSED_NAME_UUID,
                        "maxRecords", "5"  // TODO what should this be?
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));


        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        registerTemplateForEncounterType(EncounterTypes.MEDICATION_DISPENSED,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-pills", true, true, null, "bad21515-fd04-4ff6-bfcd-78456d12f168");

    }

    private void enableSurgery() {

        extensions.add(visitAction(Extensions.SURGICAL_NOTE_VISIT_ACTION,
                "mirebalais.task.surgicalOperativeNote.label",
                "fas fa-fw fa-paste",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("surgicalPostOpNote.xml")),
                Privileges.TASK_EMR_ENTER_SURGICAL_NOTE.privilege(),
                sessionLocationHasTag(LocationTags.SURGERY_NOTE_LOCATION)));

        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        registerTemplateForEncounterType(EncounterTypes.POST_OPERATIVE_NOTE,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-paste", true, true, null, "9b135b19-7ebe-4a51-aea2-69a53f9383af");
    }

    private void enableOverviewReports() {

        // both overviewReports and dataExports define this, so make sure if both are turned on we don't config it twice
        if (findAppById(Apps.REPORTS) == null) {
            apps.add(addToHomePage(app(Apps.REPORTS,
                    "reportingui.reportsapp.home.title",
                    "fas fa-fw fa-chart-bar",
                    "reportingui/reportsapp/home.page",
                    "App: reportingui.reports",
                    null)));
        }

        for (BaseReportManager report : Context.getRegisteredComponents(BaseReportManager.class)) {
            if (report.getCountries().contains(config.getCountry()) || report.getSites().contains(config.getSite())) {

                if (report.getCategory() == BaseReportManager.Category.OVERVIEW) {
                    extensions.add(overviewReport("mirebalaisreports.overview." + report.getName(),
                            report.getMessageCodePrefix() + "name",
                            report.getUuid(),
                            "App: reportingui.reports",
                            report.getOrder(),
                            "mirebalaisreports-" + report.getName() + "-link"));
                } else if (report.getCategory() == BaseReportManager.Category.DAILY) {
                    extensions.add(dailyReport("mirebalaisreports.dailyReports." + report.getName(),
                            report.getMessageCodePrefix() + "name",
                            report.getUuid(),
                            "App: reportingui.reports",
                            report.getOrder(),
                            "mirebalaisreports-" + report.getName() + "-link"));
                }

            }
        }

        // TODO: Get rid of these hacked-in reports in favor of proper configuration
        // quick-and-dirty reports for Liberia
        if (config.getCountry() == ConfigDescriptor.Country.LIBERIA || config.getCountry() == ConfigDescriptor.Country.SIERRA_LEONE) {
            extensions.add(extension(Extensions.REGISTRATION_SUMMARY_BY_AGE_REPORT,
                    "mirebalaisreports.registrationoverview.title",
                    null,
                    "link",
                    "mirebalaisreports/registrationsByAge.page",
                    "App: reportingui.reports",
                    null,
                    ExtensionPoints.REPORTING_OVERVIEW_REPORTS,
                    1,
                    map("linkId", "mirebalaisreports-registrationoverview-link")));

            extensions.add(extension(Extensions.CHECK_IN_SUMMARY_BY_AGE_REPORT,
                    "mirebalaisreports.checkinoverview.title",
                    null,
                    "link",
                    "mirebalaisreports/checkInsByAge.page",
                    "App: reportingui.reports",
                    null,
                    ExtensionPoints.REPORTING_OVERVIEW_REPORTS,
                    1,
                    map("linkId", "mirebalaisreports-checkinoverview-link")));

        } else if (config.getCountry() == ConfigDescriptor.Country.HAITI) {
            // special non-coded report in it's own section for Haiti
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

            if (config.getSite() == ConfigDescriptor.Site.MIREBALAIS) {
                // TODO in particular, get rid of this hacked in report, seems like it should be easy enough to do?
                // custom daily inpatients report
                extensions.add(extension(Extensions.DAILY_INPATIENTS_OVERVIEW_REPORT,
                        "mirebalaisreports.inpatientStatsDailyReport.name",
                        null,
                        "link",
                        "mirebalaisreports/inpatientStatsDailyReport.page",
                        "App: reportingui.reports",
                        null,
                        ExtensionPoints.REPORTING_OVERVIEW_REPORTS,
                        3,
                        map("linkId", "mirebalaisreports-inpatientDailyReport-link")));
            }
        }
    }

    private void enableMonitoringReports() {

        // overReports, monitoring reports, and dataExports define this, so make sure if both are turned on we don't config it twice
        if (findAppById(Apps.REPORTS) == null) {
            apps.add(addToHomePage(app(Apps.REPORTS,
                    "reportingui.reportsapp.home.title",
                    "fas fa-fw fa-list-alt",
                    "reportingui/reportsapp/home.page",
                    "App: reportingui.reports",
                    null)));
        }

        for (BaseReportManager report : Context.getRegisteredComponents(BaseReportManager.class)) {
            if (report.getCategory() == BaseReportManager.Category.MONITORING &&
                    (report.getCountries().contains(config.getCountry()) || report.getSites().contains(config.getSite()))) {
                extensions.add(monitoringReport("mirebalaisreports.monitoring." + report.getName(),
                        report.getMessageCodePrefix() + "name",
                        report.getUuid(),
                        "App: reportingui.reports",
                        report.getOrder(),
                        "mirebalaisreports-" + report.getName() + "-link"));
            }
        }

    }

    private void enableDataExports() {

        // overReports, monitoring reports, and dataExports define this, so make sure if both are turned on we don't config it twice
        if (findAppById(Apps.REPORTS) == null) {
            apps.add(addToHomePage(app(Apps.REPORTS,
                    "reportingui.reportsapp.home.title",
                    "fas fa-fw fa-list-alt",
                    "reportingui/reportsapp/home.page",
                    "App: reportingui.reports",
                    null)));
        }

        // reports defined through Reporting Config (move to PIH Core at some point?)
        List<ReportDescriptor> reportDescriptors =  ReportLoader.loadReportDescriptors();
        if (reportDescriptors != null) {
            for (ReportDescriptor reportDescriptor : reportDescriptors) {
                if (reportDescriptor.getConfig() != null) {
                    String component = reportDescriptor.getConfig().containsKey("component") ? reportDescriptor.getConfig().get("component").toString() : null;
                    Integer order = reportDescriptor.getConfig().containsKey("order") ? Integer.valueOf(reportDescriptor.getConfig().get("order").toString()) : 9999;
                    if (component != null && config.isComponentEnabled(component)) {
                        extensions.add(dataExport("mirebalaisreports.dataExports." + reportDescriptor.getKey(),
                                reportDescriptor.getName(),
                                reportDescriptor.getUuid(),
                                "App: mirebalaisreports.dataexports",
                                order,
                                "mirebalaisreports-" + reportDescriptor.getKey() + "-link"));
                    }
                }
            }
        }

        // TODO: review what this does as compared to below
        // TODO: hopefully we can remove some or all of this once we migrate Reports to config
        // legacy reports defined through BaseReportManagers and Full Data Export Builder
        extensions.addAll(fullDataExportBuilder.getExtensions());

        for (BaseReportManager report : Context.getRegisteredComponents(BaseReportManager.class)) {
            if (report.getCategory() == BaseReportManager.Category.DATA_EXPORT &&
                    (report.getCountries().contains(config.getCountry()) || report.getSites().contains(config.getSite()))) {
                extensions.add(dataExport("mirebalaisreports.dataExports." + report.getName(),
                        report.getMessageCodePrefix() + "name",
                        report.getUuid(),
                        "App: mirebalaisreports.dataexports",
                        report.getOrder(),
                        "mirebalaisreports-" + report.getName() + "-link"));
            }
        }

        // TODO: Replace this with property configuration in config
        if (config.getSite().equals(ConfigDescriptor.Site.MIREBALAIS)) {

            // custom data export report LQAS report report
            extensions.add(extension(Extensions.LQAS_DATA_EXPORT,
                    "mirebalaisreports.lqasdiagnoses.name",
                    null,
                    "link",
                    "mirebalaisreports/lqasDiagnoses.page",
                    "App: mirebalaisreports.dataexports",
                    null,
                    ExtensionPoints.REPORTING_DATA_EXPORT,
                    REPORTING_DATA_EXPORT_REPORTS_ORDER.indexOf(MirebalaisReportsProperties.LQAS_DIAGNOSES_REPORT_DEFINITION_UUID) + 1000,
                    map("linkId", "mirebalaisreports-lqasDiagnosesReport-link")));
        }

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

        addFeatureToggleToExtension(findExtensionById(Extensions.REPORTING_AD_HOC_ANALYSIS), "reporting_adHocAnalysis");
    }

    private void enableArchives() {

        apps.add(addToHomePage(app(Apps.ARCHIVES_ROOM,
                "paperrecord.app.archivesRoom.label",
                "fas fa-fw fa-folder-open",
                "paperrecord/archivesRoom.page",
                "App: emr.archivesRoom",
                null)));
                // ToDo:  Only for archives location
                // sessionLocationHasTag(LocationTags.ARCHIVES_LOCATION)));

        extensions.add(overallAction(Extensions.REQUEST_PAPER_RECORD_OVERALL_ACTION,
                "paperrecord.task.requestPaperRecord.label",
                "fas fa-fw fa-folder-open",
                "script",
                "showRequestChartDialog()",
                "Task: emr.requestPaperRecord",
                null));

        extensions.add(overallAction(Extensions.PRINT_ID_CARD_OVERALL_ACTION,
                "paperrecord.task.printIdCardLabel.label",
                "fas fa-fw fa-print",
                "script",
                "printIdCardLabel()",
                "Task: emr.printLabels",
                null));

        extensions.add(overallAction(Extensions.PRINT_PAPER_FORM_LABEL_OVERALL_ACTION,
                "paperrecord.task.printPaperFormLabel.label",
                "fas fa-fw fa-print",
                "script",
                "printPaperFormLabel()",
                "Task: emr.printLabels",
                null));

        addPaperRecordActionsIncludesIfNeeded();
    }

    public void enableWristbands() {

        extensions.add(overallAction(Extensions.PRINT_WRISTBAND_OVERALL_ACTION,
                "mirebalais.printWristband",
                "fas fa-fw fa-print",
                "script",
                "printWristband()",
                "Task: emr.printWristband",
                null));

        // this provides the javascript the backs the print wrist action button
        extensions.add(fragmentExtension(Extensions.PRINT_WRISTBAND_ACTION_INCLUDES,
                "mirebalais",
                "wristband/printWristband",
                null,
                ExtensionPoints.DASHBOARD_INCLUDE_FRAGMENTS,
                null));

    }

    private void enableAppointmentScheduling() {

        AppDescriptor apppointmentScheduling = app(Apps.APPOINTMENT_SCHEDULING_HOME,
                "appointmentschedulingui.scheduleAppointment.new.title",
                "fas fa-fw fa-calendar-alt",
                "appointmentschedulingui/home.page",
                "App: appointmentschedulingui.home",
                null);

        apps.add(addToHomePage((apppointmentScheduling),
                sessionLocationHasTag(LocationTags.APPOINTMENT_LOCATION)));

        apps.add(findPatientTemplateApp(Apps.SCHEDULE_APPOINTMENT,
                "appointmentschedulingui.scheduleAppointment.buttonTitle",
                "fas fa-fw fa-calendar-alt",
                "Task: appointmentschedulingui.bookAppointments",
                "/appointmentschedulingui/manageAppointments.page?patientId={{patientId}}&breadcrumbOverride={{breadcrumbOverride}}",
                arrayNode(objectNode("icon", "fas fa-fw fa-home", "link", "/index.htm"),
                        objectNode("label", "appointmentschedulingui.home.title", "link", "/appointmentschedulingui/home.page"),
                        objectNode("label", "appointmentschedulingui.scheduleAppointment.buttonTitle"))));

        extensions.add(overallAction(Extensions.SCHEDULE_APPOINTMENT_OVERALL_ACTION,
                "appointmentschedulingui.scheduleAppointment.new.title",
                "fas fa-fw fa-calendar-alt",
                "link",
                "appointmentschedulingui/manageAppointments.page?patientId={{patient.uuid}}",
                "Task: appointmentschedulingui.bookAppointments",
                null));

        extensions.add(overallAction(Extensions.REQUEST_APPOINTMENT_OVERALL_ACTION,
                "appointmentschedulingui.requestAppointment.label",
                "fas fa-fw fa-calendar-alt",
                "link",
                "appointmentschedulingui/requestAppointment.page?patientId={{patient.uuid}}",
                "Task: appointmentschedulingui.requestAppointments",
                null));

        extensions.add(dashboardTab(Extensions.APPOINTMENTS_TAB,
                "appointmentschedulingui.appointmentsTab.label",
                "App: appointmentschedulingui.viewAppointments",
                "appointmentschedulingui",
                "appointmentsTab"));

        if (config.isComponentEnabled(Components.CLINICIAN_DASHBOARD)) {
            addToClinicianDashboardFirstColumn(apppointmentScheduling,
                    "appointmentschedulingui", "miniPatientAppointments");
        }

    }

    private void enableSystemAdministration() {

        if (findAppById(Apps.SYSTEM_ADMINISTRATION) == null) {
            apps.add(addToHomePage(app(Apps.SYSTEM_ADMINISTRATION,
                    "coreapps.app.system.administration.label",
                    "fas fa-fw fa-cogs",
                    "coreapps/systemadministration/systemAdministration.page",
                    "App: coreapps.systemAdministration",
                    null)));
        }

        apps.add(addToSystemAdministrationPage(app(Apps.MANAGE_ACCOUNTS,
                "emr.task.accountManagement.label",
                "fas fa-fw fa-book",
                "emr/account/manageAccounts.page",
                "App: coreapps.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(Apps.MERGE_PATIENTS,
                "coreapps.mergePatientsLong",
                "fas fa-fw fa-users",
                "coreapps/datamanagement/mergePatients.page?app=coreapps.mergePatients",
                "App: coreapps.systemAdministration",
                objectNode("breadcrumbs", arrayNode(objectNode("icon", "fas fa-fw fa-home", "link", "/index.htm"),
                        objectNode("label", "coreapps.app.systemAdministration.label", "link", "/coreapps/systemadministration/systemAdministration.page"),
                        objectNode("label", "coreapps.mergePatientsLong")),
                        "dashboardUrl", (config.getAfterMergeUrl() != null) ? (config.getAfterMergeUrl()) : (config.getDashboardUrl())))));

        apps.add(addToSystemAdministrationPage(app(Apps.FEATURE_TOGGLES,
                "emr.advancedFeatures",
                "fas fa-fw fa-search",
                "mirebalais/toggles.page",
                "App: coreapps.systemAdministration",
                null)));
    }

    private void enableManagePrinters() {

        if (findAppById(Apps.SYSTEM_ADMINISTRATION) == null) {
            apps.add(addToHomePage(app(Apps.SYSTEM_ADMINISTRATION,
                    "coreapps.app.system.administration.label",
                    "fas fa-fw fa-cogs",
                    "coreapps/systemadministration/systemAdministration.page",
                    "App: coreapps.systemAdministration",
                    null)));
        }

        apps.add(addToSystemAdministrationPage(app(Apps.PRINTER_ADMINISTRATION,
                "printer.administration",
                "fas fa-fw fa-print",
                "printer/printerAdministration.page",
                "App: coreapps.systemAdministration",
                null)));

    }

    private void enableMyAccount() {

        apps.add(addToHomePage(app(Apps.MY_ACCOUNT,
                "emr.app.system.administration.myAccount.label",
                "fas fa-fw fa-cog",
                "emr/account/myAccount.page",
                null, null)));

    }

    private void enablePatientRegistration() {

        apps.add(addToHomePage(patientRegistrationApp.getAppDescriptor(config),
                sessionLocationHasTag(LocationTags.REGISTRATION_LOCATION)));

        // Show additional identifiers (from form section "patient-identification-section")
        //   - in Mexico
        //   - in non-mental-health Haiti if the additionalHaitiIdentifiers feature toggle is enabled
        if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO) || (
                featureToggles.isFeatureEnabled("additionalHaitiIdentifiers") &&
                        config.getCountry().equals(ConfigDescriptor.Country.HAITI) &&
                        !ConfigDescriptor.Specialty.MENTAL_HEALTH.equals(config.getSpecialty()))) {  // reversed to make this null safe
            apps.add(addToRegistrationSummarySecondColumnContent(app(Apps.ADDITIONAL_IDENTIFIERS,
                    "zl.registration.patient.additionalIdentifiers",
                    "fas fa-fw fa-user",
                    null,
                    "App: registrationapp.registerPatient",
                    null),
                    "registrationapp",
                    "summary/section",
                    map("sectionId", "patient-identification-section")));
        }

        apps.add(addToRegistrationSummaryContent(app(Apps.MOST_RECENT_REGISTRATION_SUMMARY,
                "mirebalais.mostRecentRegistration.label",
                "fas fa-fw fa-user",
                null,
                "App: registrationapp.registerPatient",
                objectNode("encounterDateLabel", "mirebalais.mostRecentRegistration.encounterDateLabel",
                        "encounterTypeUuid", EncounterTypes.PATIENT_REGISTRATION.uuid(),
                        "definitionUiResource", PihCoreUtil.getFormResource("patientRegistration-rs.xml"),
                        "editable", true,
                        "creatable", true)),
                "coreapps",
                "encounter/mostRecentEncounter"));

        if (config.isComponentEnabled(Components.PROVIDER_RELATIONSHIPS)) {
            apps.add(addToRegistrationSummarySecondColumnContent(app(Apps.PROVIDER_RELATIONSHIPS_REGISTRATION_SUMMARY,
                    "pihcore.providerRelationshipsDashboardWidget.label",
                    "fas fa-fw fa-users",
                    null,
                    null,
                    objectNode(
                            "widget", "relationships",
                            "baseAppPath", "/registrationapp",
                            "editable", "true",
                            "editPrivilege", CoreAppsConstants.PRIVILEGE_EDIT_RELATIONSHIPS,
                            "dashboardPage", "/registrationapp/registrationSummary.page?patientId={{patientUuid}}&appId=registrationapp.registerPatient",
                            "providerPage", "/coreapps/providermanagement/editProvider.page?personUuid={{personUuid}}",
                            "includeRelationshipTypes", RelationshipTypeBundle.RelationshipTypes.CHW_TO_PATIENT,
                            "icon", "fas fa-fw fa-users",
                            "label", "pihcore.providerRelationshipsDashboardWidget.label"
                    )),
                    "coreapps", "dashboardwidgets/dashboardWidget"));
        }

        if (config.isComponentEnabled(Components.RELATIONSHIPS)) {
            apps.add(addToRegistrationSummarySecondColumnContent(app(Apps.RELATIONSHIPS_REGISTRATION_SUMMARY,
                    "pihcore.relationshipsDashboardWidget.label",
                    "fas fa-fw fa-users",
                    null,
                    null, // TODO restrict by privilege or location)
                    objectNode(
                            "widget", "relationships",
                            "baseAppPath", "/registrationapp",
                            "editable", "true",
                            "editPrivilege", CoreAppsConstants.PRIVILEGE_EDIT_RELATIONSHIPS,
                            "dashboardPage", "/registrationapp/registrationSummary.page?patientId={{patientUuid}}&appId=registrationapp.registerPatient",
                            "providerPage", "/coreapps/providermanagement/editProvider.page?personUuid={{personUuid}}",
                            "includeRelationshipTypes", RelationshipTypeBundle.RelationshipTypes.SPOUSE_PARTNER
                                    + "," + PihCoreConstants.RELATIONSHIP_SIBLING
                                    + "," + PihCoreConstants.RELATIONSHIP_PARENT_CHILD,
                            "icon", "fas fa-fw fa-users",
                            "label", "pihcore.relationshipsDashboardWidget.label"
                    )),
                    "coreapps", "dashboardwidgets/dashboardWidget"));
        }


        if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO) ||
                (config.getCountry().equals(ConfigDescriptor.Country.HAITI) &&
                        !ConfigDescriptor.Specialty.MENTAL_HEALTH.equals(config.getSpecialty()))) {  // reversed to make this null safe
            apps.add(addToRegistrationSummaryContent(app(Apps.MOST_RECENT_REGISTRATION_INSURANCE,
                    "zl.registration.patient.insurance.insuranceName.label",
                    "fas fa-fw fa-address-card",
                    null,
                    "App: registrationapp.registerPatient",
                    objectNode("encounterDateLabel", "mirebalais.mostRecentRegistration.encounterDateLabel",
                            "encounterTypeUuid", EncounterTypes.PATIENT_REGISTRATION.uuid(),
                            "definitionUiResource", PihCoreUtil.getFormResource("patientRegistration-insurance.xml"),
                            "editable", true)),
                    "coreapps",
                    "encounter/mostRecentEncounter"));
        }
        apps.add(addToRegistrationSummaryContent(app(Apps.MOST_RECENT_REGISTRATION_SOCIAL,
                "zl.registration.patient.social.label",
                "fas fa-fw fa-user",
                null,
                "App: registrationapp.registerPatient",
                objectNode("encounterDateLabel", "mirebalais.mostRecentRegistration.encounterDateLabel",
                        "encounterTypeUuid", EncounterTypes.PATIENT_REGISTRATION.uuid(),
                        "definitionUiResource", PihCoreUtil.getFormResource("patientRegistration-social.xml"),
                        "editable", true)),
                "coreapps",
                "encounter/mostRecentEncounter"));


        if (!config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            apps.add(addToRegistrationSummarySecondColumnContent(app(Apps.MOST_RECENT_REGISTRATION_CONTACT,
                    "zl.registration.patient.contactPerson.label",
                    "fas fa-fw fa-phone",
                    null,
                    "App: registrationapp.registerPatient",
                    objectNode("encounterDateLabel", "mirebalais.mostRecentRegistration.encounterDateLabel",
                            "encounterTypeUuid", EncounterTypes.PATIENT_REGISTRATION.uuid(),
                            "definitionUiResource", PihCoreUtil.getFormResource("patientRegistration-contact.xml"),
                            "editable", true)),
                    "coreapps",
                    "encounter/mostRecentEncounter"));
        }

        if (config.isComponentEnabled(Components.CHECK_IN)) {
            apps.add(addToRegistrationSummarySecondColumnContent(app(Apps.MOST_RECENT_CHECK_IN,
                    "pihcore.mostRecentCheckin.label",
                    "fas fa-fw fa-check",
                    null,
                    "App: registrationapp.registerPatient",
                    objectNode("encounterDateLabel", "pihcore.mostRecentCheckin.encounterDateLabel",
                            "encounterTypeUuid", EncounterTypes.CHECK_IN.uuid(),
                            "definitionUiResource", PihCoreUtil.getFormResource("checkin.xml"),
                            "editable", true,
                            "edit-provider", "htmlformentryui",
                            "edit-fragment", "htmlform/editHtmlFormWithSimpleUi")),
                    "coreapps",
                    "encounter/mostRecentEncounter"));
        }

        if (config.isComponentEnabled(Components.ID_CARD_PRINTING)) {
            apps.add(addToRegistrationSummarySecondColumnContent(app(Apps.ID_CARD_PRINTING_STATUS,
                    "zl.registration.patient.idcard.status",
                    "fas fa-fw fa-barcode",
                    null,
                    "App: registrationapp.registerPatient",
                    null),
                    "mirebalais",
                    "patientRegistration/idCardStatus"));
        }

        if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE) ) {
            apps.add(addToRegistrationSummaryContent(app(Apps.MOST_RECENT_REGISTRATION_EBOLA_SCREENING,
                    "zl.registration.patient.ebolaScreening.label",
                    "fas fa-fw fa-user",
                    null,
                    "App: registrationapp.registerPatient",
                    objectNode("encounterDateLabel", "mirebalais.mostRecentRegistration.encounterDateLabel",
                            "encounterTypeUuid", EncounterTypes.PATIENT_REGISTRATION.uuid(),
                            "definitionUiResource", PihCoreUtil.getFormResource("patientRegistration-ebolaScreening.xml"),
                            "editable", true)),
                    "coreapps",
                    "encounter/mostRecentEncounter"));
        }

        extensions.add(overallRegistrationAction(Extensions.REGISTER_NEW_PATIENT,
                "registrationapp.home",
                "fas fa-fw fa-user",
                "link",
                "registrationapp/findPatient.page?appId=" + Apps.PATIENT_REGISTRATION,
                "App: registrationapp.registerPatient",
                sessionLocationHasTag(LocationTags.REGISTRATION_LOCATION)));

        extensions.add(overallRegistrationAction(Extensions.MERGE_INTO_ANOTHER_PATIENT,
                "coreapps.mergePatientsShort",
                "fas fa-fw fa-users",
                "link",
                "coreapps/datamanagement/mergePatients.page?app=coreapps.mergePatients&patient1={{patient.patientId}}",
                "App: registrationapp.registerPatient",
                null));

        if (config.isComponentEnabled(Components.CLINICIAN_DASHBOARD)) {
            extensions.add(overallRegistrationAction(Extensions.CLINICIAN_FACING_PATIENT_DASHBOARD,
                    "registrationapp.clinicalDashboard",
                    "fas fa-fw fa-stethoscope",
                    "link",
                    "coreapps/clinicianfacing/patient.page?patientId={{patient.patientId}}&appId=" + Apps.PATIENT_REGISTRATION,
                    "App: coreapps.patientDashboard",
                    null));

            extensions.add(overallAction(Extensions.REGISTRATION_SUMMARY_OVERALL_ACTION,
                    "registrationapp.patient.registrationSummary",
                    "fas fa-fw fa-user",
                    "link",
                    "registrationapp/registrationSummary.page?patientId={{patient.patientId}}&appId=" + Apps.PATIENT_REGISTRATION,
                    "App: registrationapp.registerPatient",
                    null));
        }

        if (config.isComponentEnabled(Components.VISIT_MANAGEMENT)) {
            extensions.add(overallRegistrationAction(Extensions.VISITS_DASHBOARD,
                    "pihcore.visitDashboard",
                    "fas fa-fw fa-user",
                    "link",
                    patientVisitsPageUrl,
                    "App: coreapps.patientDashboard",
                    null));
        }

        if (config.isComponentEnabled(Components.ARCHIVES)) {
            extensions.add(overallRegistrationAction(Extensions.PRINT_PAPER_FORM_LABEL,
                    "paperrecord.task.printPaperFormLabel.label",
                    "fas fa-fw fa-print",
                    "script",
                    "printPaperFormLabel()",
                    "Task: emr.printLabels",
                    null));
        }

        if (config.isComponentEnabled(Components.ID_CARD_PRINTING)) {
            extensions.add(overallRegistrationAction(Extensions.PRINT_ID_CARD_REGISTRATION_ACTION,
                    "zl.registration.patient.idcard.label",
                    "fas fa-fw fa-barcode",
                    "link",
                    "mirebalais/patientRegistration/printIdCard.page?patientId={{patient.patientId}}",
                    "App: registrationapp.registerPatient",
                    null));
        }

        addPaperRecordActionsIncludesIfNeeded();

    }

    // legacy MPI used in Mirebalais to connect to Lacolline
    private void enableLegacyMPI() {
        apps.add(addToHomePageWithoutUsingRouter(app(Apps.LEGACY_MPI,
                "mirebalais.mpi.title",
                "fas fa-fw fa-search-plus",
                "mirebalais/mpi/findPatient.page",
                "App: mirebalais.mpi",
                null)));
    }

    private void enableClinicianDashboard() {

        apps.add(app(Apps.CLINICIAN_DASHBOARD,
                "mirebalais.app.clinicianDashboard.label",
                "fas fa-fw fa-medkit",
                "coreapps/clinicianfacing/patient.page?app=" + Apps.CLINICIAN_DASHBOARD,
                CoreAppsConstants.PRIVILEGE_PATIENT_DASHBOARD,
                objectNode(
                        "visitUrl", patientVisitsPageWithSpecificVisitUrl,
                        "visitsUrl", patientVisitsPageUrl
                )));
        AppDescriptor visitSummary = app(Apps.VISITS_SUMMARY,
                "coreapps.clinicianfacing.visits",
                "fas fa-fw fa-calendar-alt",
                null,
                null,
                objectNode("visitType", VisitTypeBundle.VisitTypes.CLINIC_OR_HOSPITAL_VISIT));

        apps.add(addToClinicianDashboardFirstColumn(visitSummary, "coreapps", "clinicianfacing/visitsSection"));
        apps.add(addToHivDashboardSecondColumn(cloneApp(visitSummary, Apps.HIV_VISIT_SUMMARY), "coreapps", "clinicianfacing/visitsSection"));

        if (config.isComponentEnabled(Components.HOME_VISITS_ON_CLINICIAN_DASHBOARD)) {
            HashMap<String, String> visitParams = new HashMap<String, String>();
            visitParams.put("suppressActions", "true");
            visitParams.put("visitType", VisitTypeBundle.VisitTypes.HOME_VISIT);

            AppDescriptor homeVisitsSummary = app(Apps.HOME_VISITS_SUMMARY,
                    "mirebalais.home.visits",
                    "fas fa-fw fa-calendar-alt",
                    null,
                    null,
                    objectNode(
                            "visitType", VisitTypeBundle.VisitTypes.HOME_VISIT,
                            "visitsUrl", addParametersToUrl(patientVisitsPageUrl, visitParams),
                            "visitUrl",  addParametersToUrl(patientVisitsPageWithSpecificVisitUrl, visitParams),
                            "showVisitTypeOnPatientHeaderSection", true,
                            "label", "mirebalais.home.visits"));

            apps.add(addToClinicianDashboardFirstColumn(homeVisitsSummary, "coreapps", "clinicianfacing/visitsSection"));
        }

        if (config.isComponentEnabled(Components.BMI_ON_CLINICIAN_DASHBOARD)) {
            apps.add(addToClinicianDashboardFirstColumn(
                    graphs.getBmiGraph(ExtensionPoints.CLINICIAN_DASHBOARD_FIRST_COLUMN),
                    "coreapps",
                    "dashboardwidgets/dashboardWidget"));
        }

        // link for new pihcore visit view
        //"visitUrl", "pihcore/visit/visit.page?visit={{visit.uuid}}"

     /*   if (config.isComponentEnabled(CustomAppLoaderConstants.Components.PRESCRIPTIONS)) {
            // TODO we should actually define an app here, not use the existing app
            addToClinicianDashboardSecondColumn(app, "coreapps", "patientdashboard/activeDrugOrders");
        }
*/
    }

    private void enableAllergies() {
        apps.add(addToClinicianDashboardSecondColumn(app(Apps.ALLERGY_SUMMARY,
                "allergyui.allergies",
                "fas fa-fw fa-allergies",
                null,
                null,
                null),
                "allergyui", "allergies"));
    }

    private void enableOncology() {

        configureBasicProgramDashboard(OncologyProgram.ONCOLOGY);

        extensions.add(visitAction(Extensions.ONCOLOGY_CONSULT_NOTE_VISIT_ACTION,
                "pih.task.oncologyConsultNote.label",
                "fas fa-fw fa-paste",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("oncologyConsult.xml")),
                Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE.privilege(),
                and(sessionLocationHasTag(LocationTags.ONCOLOGY_CONSULT_LOCATION),
                        visitDoesNotHaveEncounterOfType(EncounterTypes.ONCOLOGY_INITIAL_VISIT),
                        visitDoesNotHaveEncounterOfType(EncounterTypes.ONCOLOGY_CONSULT),
                        or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

        // will we need this template after we stop using old patient visits view?
        registerTemplateForEncounterType(EncounterTypes.ONCOLOGY_CONSULT,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-paste", true, true,
                null, EncounterRoleBundle.EncounterRoles.CONSULTING_CLINICIAN);

        extensions.add(visitAction(Extensions.ONCOLOGY_INITIAL_VISIT_ACTION,
                "pih.task.oncologyInitialConsult.label",
                "fas fa-fw fa-paste",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("oncologyIntake.xml")),
                Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE.privilege(),
                and(sessionLocationHasTag(LocationTags.ONCOLOGY_CONSULT_LOCATION),
                        visitDoesNotHaveEncounterOfType(EncounterTypes.ONCOLOGY_INITIAL_VISIT),
                        visitDoesNotHaveEncounterOfType(EncounterTypes.ONCOLOGY_CONSULT),
                        or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

        // will we need this template after we stop using old patient visits view?
        registerTemplateForEncounterType(EncounterTypes.ONCOLOGY_INITIAL_VISIT,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-paste", true, true,
                null, EncounterRoleBundle.EncounterRoles.CONSULTING_CLINICIAN);

        extensions.add(visitAction(Extensions.CHEMOTHERAPY_VISIT_ACTION,
                "pih.task.chemotherapySession.label",
                "fas fa-fw fa-retweet",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("chemotherapyTreatment.xml")),
                Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE.privilege(),
                and(sessionLocationHasTag(LocationTags.CHEMOTHERAPY_LOCATION),
                        or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));
    }

    public void enableChemotherapy() {

        Extension chemoOrdering = overallAction(Extensions.CHEMO_ORDERING_VISIT_ACTION,
                "pih.task.orderChemo",
                "fas fa-fw fa-pills",
                "link",
                "owa/openmrs-owa-oncology/index.html?patientId={{patient.uuid}}/#physicianDashboard",
                Privileges.TASK_EMR_ENTER_CONSULT_NOTE.privilege(),
                and(sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION),
                        or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config)))));

        extensions.add(chemoOrdering);

        Extension chemoRecording = visitAction(Extensions.CHEMO_RECORDING_VISIT_ACTION,
                "pih.task.recordChemo",
                "fas fa-fw fa-pills",
                "link",
                "owa/openmrs-owa-oncology/index.html?patientId={{patient.uuid}}&visitId={{visit.uuid}}/#nurseDashboard",
                Privileges.TASK_EMR_ENTER_CONSULT_NOTE.privilege(),
                and(sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION),
                        or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config)))));

        extensions.add(chemoRecording);

        extensions.add(cloneAsOncologyOverallAction(chemoOrdering));
        extensions.add(cloneAsOncologyVisitAction(chemoRecording));

        registerTemplateForEncounterType(EncounterTypes.CHEMOTHERAPY_SESSION,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-retweet", true, true,
                null, EncounterRoleBundle.EncounterRoles.CONSULTING_CLINICIAN);
    }

    private void enableLabResults() {

        extensions.add(visitAction(Extensions.LAB_RESULTS_VISIT_ACTION,
                "pih.task.labResults.label",
                "fas fa-fw fa-vial",
                "link",
                enterSimpleHtmlFormLink(PihCoreUtil.getFormResource("labResults.xml")),
                Privileges.TASK_EMR_ENTER_LAB_RESULTS.privilege(),
                sessionLocationHasTag(LocationTags.LAB_RESULTS_LOCATION)));

        // will we need this template after we stop using old patient visits view?
        registerTemplateForEncounterType(EncounterTypes.LAB_RESULTS,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-vial", true, true,
                editSimpleHtmlFormLink(PihCoreUtil.getFormResource("labResults.xml")), EncounterRoleBundle.EncounterRoles.CONSULTING_CLINICIAN);

    }

    private void enableNCDs() {

        configureBasicProgramDashboard(NCDProgram.NCD);

        String definitionUiResource = PihCoreUtil.getFormResource("ncd-adult-initial.xml");
        if (!config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            definitionUiResource = definitionUiResource + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl;
        }

        extensions.add(visitAction(Extensions.NCD_INITIAL_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + EncounterTypes.NCD_INITIAL_CONSULT.uuid(),
                "fas fa-fw fa-heart",
                "link",
                enterStandardHtmlFormLink(definitionUiResource),  // always redirect to visit page after clicking this link
                Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE.privilege(),
                and(sessionLocationHasTag(LocationTags.NCD_CONSULT_LOCATION),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.NCD_INITIAL_CONSULT),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.NCD_FOLLOWUP_CONSULT),
                        or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

        definitionUiResource = PihCoreUtil.getFormResource("ncd-adult-followup.xml");
        if (!config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            definitionUiResource = definitionUiResource + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl;
        }

        extensions.add(visitAction(Extensions.NCD_FOLLOWUP_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + EncounterTypes.NCD_FOLLOWUP_CONSULT.uuid(),
                "fas fa-fw fa-heart",
                "link",
                enterStandardHtmlFormLink(definitionUiResource),  // always redirect to visit page after clicking this link
                Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE.privilege(),
                and(sessionLocationHasTag(LocationTags.NCD_CONSULT_LOCATION),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.NCD_INITIAL_CONSULT),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.NCD_FOLLOWUP_CONSULT),
                        or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));
    }

    private void enableMCHForms() {

        // ToDo: Fix privileges for these 3 forms.  Not every user should have privileges.
        extensions.add(visitAction(Extensions.MCH_ANC_INTAKE_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + EncounterTypes.ANC_INTAKE.uuid(),
                "fas fa-fw fa-gift",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("ancIntake.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                Privileges.TASK_EMR_ENTER_MCH.privilege(),
                and(sessionLocationHasTag(LocationTags.MCH_LOCATION),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.ANC_INTAKE),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.ANC_FOLLOWUP),
                    and(patientIsFemale()))));

        extensions.add(visitAction(Extensions.MCH_ANC_FOLLOWUP_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + EncounterTypes.ANC_FOLLOWUP.uuid(),
                "fas fa-fw fa-gift",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("ancFollowup.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                Privileges.TASK_EMR_ENTER_MCH.privilege(),
                and(sessionLocationHasTag(LocationTags.MCH_LOCATION),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.ANC_INTAKE),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.ANC_FOLLOWUP),
                    and(patientIsFemale()))));

        extensions.add(visitAction(Extensions.MCH_DELIVERY_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + EncounterTypes.MCH_DELIVERY.uuid(),
                "fas fa-fw fa-baby",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("delivery.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                Privileges.TASK_EMR_ENTER_MCH.privilege(),
                and(sessionLocationHasTag(LocationTags.MCH_LOCATION),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.MCH_DELIVERY),
                    and(patientIsFemale()))));
    }

    private void enableANCProgram() {
        configureBasicProgramDashboard(ANCProgram.ANC);
    }

    private void enableMCHProgram() {
        configureBasicProgramDashboard(MCHProgram.MCH);
    }

    private void enableVaccinationOnly() {
        extensions.add(visitAction(Extensions.VACCINATION_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + EncounterTypes.VACCINATION.uuid(),
                "fas fa-fw fa-umbrella",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("vaccination-only.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                Privileges.TASK_EMR_ENTER_VACCINATION.privilege(),
                and(sessionLocationHasTag(LocationTags.VACCINATION_LOCATION),
                        visitDoesNotHaveEncounterOfType(EncounterTypes.VACCINATION))));
    }

    private void enableMentalHealthForm() {

        String definitionUiResource = PihCoreUtil.getFormResource("mentalHealth.xml");
        if (!config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            definitionUiResource = definitionUiResource + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl;
        }

        extensions.add(visitAction(Extensions.MENTAL_HEALTH_VISIT_ACTION,
                "pih.task.mentalHealth.label",
                "fas fa-fw fa-user",
                "link",
                enterStandardHtmlFormLink(definitionUiResource),
                Privileges.TASK_EMR_ENTER_MENTAL_HEALTH_NOTE.privilege(),
                and(sessionLocationHasTag(LocationTags.MENTAL_HEALTH_LOCATION),
                        visitDoesNotHaveEncounterOfType(EncounterTypes.MENTAL_HEALTH_ASSESSMENT))));

        // will we need this template after we stop using old patient visits view?
        registerTemplateForEncounterType(EncounterTypes.MENTAL_HEALTH_ASSESSMENT,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-user", true, true,
                null, EncounterRoleBundle.EncounterRoles.CONSULTING_CLINICIAN);
    }

    private void enableVCT() {

        extensions.add(visitAction(Extensions.VCT_VISIT_ACTION,
                "pih.task.vct.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/zl/vct.xml")),
                Privileges.TASK_EMR_ENTER_VCT.privilege(),
                and(sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION),
                        visitDoesNotHaveEncounterOfType(EncounterTypes.VCT))));
    }

    private void enableSocioEconomics() {
        extensions.add(visitAction(Extensions.SOCIO_ECONOMICS_VISIT_ACTION,
                "pih.task.socioEcon.label",
                "fas fa-fw fa-home",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("socio-econ.xml")),
                Privileges.TASK_EMR_ENTER_SOCIO.privilege(),
                and(sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION),
                        visitDoesNotHaveEncounterOfType(EncounterTypes.SOCIO_ECONOMICS))));
    }

    private void enableChartSearch() {
        extensions.add(overallAction(Extensions.CHART_SEARCH_OVERALL_ACTION,
                "pihcore.chartSearch.label",
                "fas fa-fw fa-search",
                "link",
                "chartsearch/chartsearch.page?patientId={{patient.patientId}}",
                Privileges.TASK_EMR_ENTER_CONSULT_NOTE.privilege(), // TODO correct permission!
                null));
    }

    private void enableWaitingForConsult() {

        apps.add(addToHomePage(app(Apps.WAITING_FOR_CONSULT,
                "pihcore.waitingForConsult.title",
                "fas fa-fw fa-stethoscope",
                "pihcore/visit/waitingForConsult.page",
                Privileges.APP_WAITING_FOR_CONSULT.privilege(),
                null)));
    }

    private void enableTodaysVisits() {

        apps.add(addToHomePage(app(Apps.TODAYS_VISITS,
                "pihcore.todaysVisits.title",
                "fas fa-fw icon-check-in",
                "pihcore/visit/todaysVisits.page",
                Privileges.APP_TODAYS_VISITS.privilege(),
                null)));

    }

    private void enableCHWApp() {
        if (findAppById(Apps.CHW_MGMT) == null) {
            apps.add(addToHomePage(app(Apps.CHW_MGMT,
                    "chwapp.label",
                    "fas fa-fw fa-users",
                    "/coreapps/providermanagement/providerList.page",
                    Privileges.APP_CHW.privilege(),
                    null),
                    sessionLocationHasTag(LocationTags.PROVIDER_MANAGEMENT_LOCATION)));
        }
    }

    private void enableEDTriage() {
        apps.add(addToHomePage(findPatientTemplateApp(Apps.ED_TRIAGE,
                "edtriageapp.label",
                "fas fa-fw fa-ambulance",
                Privileges.APP_ED_TRIAGE.privilege(),
                "/edtriageapp/edtriageEditPatient.page?patientId={{patientId}}&appId=" + Apps.ED_TRIAGE
                        + "&dashboardUrl=" + config.getDashboardUrl(),
                null),
                sessionLocationHasTag(LocationTags.ED_TRIAGE_LOCATION)));

        extensions.add(visitAction(Extensions.ED_TRIAGE_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + EncounterTypes.EMERGENCY_TRIAGE.uuid(),
                "fas fa-fw fa-ambulance",
                "link",
                "/edtriageapp/edtriageEditPatient.page?patientId={{patient.uuid}}&appId=" + Apps.ED_TRIAGE,
                null,
                and(sessionLocationHasTag(LocationTags.ED_TRIAGE_LOCATION), patientHasActiveVisit())));

        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        registerTemplateForEncounterType(EncounterTypes.EMERGENCY_TRIAGE,
                findExtensionById(EncounterTemplates.ED_TRIAGE), "fas fa-fw fa-ambulance", false, true,
                "edtriageapp/edtriageEditPatient.page?patientId={{patient.uuid}}&encounterId={{encounter.uuid}}&appId=edtriageapp.app.triageQueue&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}&editable=true",
                null);
    }

    private void enableEDTriageQueue() {
        apps.add(addToHomePage(app(Apps.ED_TRIAGE_QUEUE,
                "edtriageapp.queue.label",
                "fas fa-fw fa-list-ol",
                "/edtriageapp/edtriageViewQueue.page?appId=" + Apps.ED_TRIAGE_QUEUE,
                Privileges.APP_ED_TRIAGE_QUEUE.privilege(),
                objectNode("dashboardUrl", config.getDashboardUrl())),
                sessionLocationHasTag(LocationTags.ED_TRIAGE_LOCATION)));
    }

    private void enablePrimaryCare() {

        if (config.getCountry() == ConfigDescriptor.Country.HAITI) {

            extensions.add(visitAction(Extensions.PRIMARY_CARE_PEDS_INITIAL_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + EncounterTypes.PRIMARY_CARE_PEDS_INITIAL_CONSULT.uuid(),
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("primary-care-peds-initial.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                    null,
                    and(sessionLocationHasTag(LocationTags.PRIMARY_CARE_CONSULT_LOCATION),
                            visitDoesNotHaveEncounterOfType(EncounterTypes.PRIMARY_CARE_PEDS_INITIAL_CONSULT),
                            visitDoesNotHaveEncounterOfType(EncounterTypes.PRIMARY_CARE_PEDS_FOLLOWUP_CONSULT),
                            or(patientIsChild(), patientAgeUnknown(), patientDoesNotActiveVisit()),
                            or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_PRIMARY_CARE_CONSULT_NOTE), patientHasActiveVisit()),
                                    userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                    and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

            extensions.add(visitAction(Extensions.PRIMARY_CARE_PEDS_FOLLOWUP_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + EncounterTypes.PRIMARY_CARE_PEDS_FOLLOWUP_CONSULT.uuid(),
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("primary-care-peds-followup.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                    null,
                    and(sessionLocationHasTag(LocationTags.PRIMARY_CARE_CONSULT_LOCATION),
                            visitDoesNotHaveEncounterOfType(EncounterTypes.PRIMARY_CARE_PEDS_INITIAL_CONSULT),
                            visitDoesNotHaveEncounterOfType(EncounterTypes.PRIMARY_CARE_PEDS_FOLLOWUP_CONSULT),
                            or(patientIsChild(), patientAgeUnknown(), patientDoesNotActiveVisit()),
                            or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_PRIMARY_CARE_CONSULT_NOTE), patientHasActiveVisit()),
                                    userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                    and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

            extensions.add(visitAction(Extensions.PRIMARY_CARE_ADULT_INITIAL_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + EncounterTypes.PRIMARY_CARE_ADULT_INITIAL_CONSULT.uuid(),
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("primary-care-adult-initial.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                    null,
                    and(sessionLocationHasTag(LocationTags.PRIMARY_CARE_CONSULT_LOCATION),
                            visitDoesNotHaveEncounterOfType(EncounterTypes.PRIMARY_CARE_ADULT_INITIAL_CONSULT),
                            visitDoesNotHaveEncounterOfType(EncounterTypes.PRIMARY_CARE_ADULT_FOLLOWUP_CONSULT),
                            or(patientIsAdult(), patientAgeUnknown(), patientDoesNotActiveVisit()),
                            or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_PRIMARY_CARE_CONSULT_NOTE), patientHasActiveVisit()),
                                    userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                    and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

            extensions.add(visitAction(Extensions.PRIMARY_CARE_ADULT_FOLLOWUP_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + EncounterTypes.PRIMARY_CARE_ADULT_FOLLOWUP_CONSULT.uuid(),
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("primary-care-adult-followup.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                    null,
                    and(sessionLocationHasTag(LocationTags.PRIMARY_CARE_CONSULT_LOCATION),
                            visitDoesNotHaveEncounterOfType(EncounterTypes.PRIMARY_CARE_ADULT_INITIAL_CONSULT),
                            visitDoesNotHaveEncounterOfType(EncounterTypes.PRIMARY_CARE_ADULT_FOLLOWUP_CONSULT),
                            or(patientIsAdult(), patientAgeUnknown(), patientDoesNotActiveVisit()),
                            or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_PRIMARY_CARE_CONSULT_NOTE), patientHasActiveVisit()),
                                    userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                    and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

        } else if (config.getCountry() == ConfigDescriptor.Country.MEXICO) {

            extensions.add(visitAction(Extensions.MEXICO_CONSULT_ACTION,
                    "ui.i18n.EncounterType.name." + MexicoEncounterTypes.MEXICO_CONSULT.uuid(),
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("consult.xml")),
                    null,
                    sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION)));

        } else if (config.getCountry() == ConfigDescriptor.Country.SIERRA_LEONE) {

            extensions.add(visitAction(Extensions.SIERRA_LEONE_OUTPATIENT_INITIAL_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + SierraLeoneEncounterTypes.SIERRA_LEONE_OUTPATIENT_INITIAL.uuid(),
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("outpatient-initial.xml")
                            + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),
                    null,
                    and(sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION),
                        visitDoesNotHaveEncounterOfType(SierraLeoneEncounterTypes.SIERRA_LEONE_OUTPATIENT_INITIAL),
                        visitDoesNotHaveEncounterOfType(SierraLeoneEncounterTypes.SIERRA_LEONE_OUTPATIENT_FOLLOWUP))));

            extensions.add(visitAction(Extensions.SIERRA_LEONE_OUTPATIENT_FOLLOWUP_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + SierraLeoneEncounterTypes.SIERRA_LEONE_OUTPATIENT_FOLLOWUP.uuid(),
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("outpatient-followup.xml")
                            + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),
                    null,
                    and(sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION),
                        visitDoesNotHaveEncounterOfType(SierraLeoneEncounterTypes.SIERRA_LEONE_OUTPATIENT_INITIAL),
                        visitDoesNotHaveEncounterOfType(SierraLeoneEncounterTypes.SIERRA_LEONE_OUTPATIENT_FOLLOWUP))));
        }

    }

    private void enableHIV() {

        configureBasicProgramDashboard(HIVProgram.HIV);

        // ZL HIV forms
        Extension hivInitial = visitAction(Extensions.HIV_ZL_INITIAL_VISIT_ACTION,
                "pih.task.hivIntake.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/zl/hiv-intake.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),
                Privileges.TASK_EMR_ENTER_HIV_CONSULT_NOTE.privilege(),
                and(sessionLocationHasTag(LocationTags.HIV_CONSULT_LOCATION),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.ZL_HIV_INTAKE),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.ZL_HIV_FOLLOWUP),
                    or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_HIV_CONSULT_NOTE), patientHasActiveVisit()),
                            userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                            and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config)))));

        extensions.add(hivInitial);
        extensions.add(cloneAsHivVisitAction(hivInitial));

        Extension hivFollowup = visitAction(Extensions.HIV_ZL_FOLLOWUP_VISIT_ACTION,
                "pih.task.hivFollowup.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/zl/hiv-followup.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),
                Privileges.TASK_EMR_ENTER_HIV_CONSULT_NOTE.privilege(),
                and(sessionLocationHasTag(LocationTags.HIV_CONSULT_LOCATION),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.ZL_HIV_INTAKE),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.ZL_HIV_FOLLOWUP),
                    or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_HIV_CONSULT_NOTE), patientHasActiveVisit()),
                            userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                            and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config)))));

        extensions.add(hivFollowup);
        extensions.add(cloneAsHivVisitAction(hivFollowup));

        extensions.add(cloneAsHivVisitAction(findExtensionById(Extensions.VITALS_CAPTURE_VISIT_ACTION)));
        extensions.add(cloneAsHivOverallAction(findExtensionById(Extensions.CREATE_VISIT_OVERALL_ACTION)));

        // TODO pull this out to clone existing main DASHBOARD_VISIT_INCLUDES
        // this provides the javascript & dialogs the backs the overall action buttons (to start/end visits, etc)
        extensions.add(fragmentExtension(Extensions.HIV_DASHBOARD_VISIT_INCLUDES,
                "coreapps",
                "patientdashboard/visitIncludes",
                null,
                HIVProgram.HIV.uuid() + ".includeFragments",
                map("patientVisitsPage", patientVisitsPageWithSpecificVisitUrl)));

        // additional columns to add to the HIV Program Dashboard
        apps.add(addToHivDashboardFirstColumn(app(Apps.HIV_OBS_CHART,
                "pih.app.hivObsChart.title",
                "fas fa-fw fa-list-alt",
                null,
                null,
                objectNode(
                        "widget", "obsacrossencounters",
                        "icon", "fas fa-fw fa-list-alt",
                        "label", "pih.app.hivObsChart.title",
                        "concepts", MirebalaisConstants.WEIGHT_CONCEPT_UUID + "," + MirebalaisConstants.CD4_COUNT_UUID + "," + MirebalaisConstants.VIRAL_LOAD_UUID,
                        "maxRecords", "6"  // TODO what should this be?
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        // ToDo:  Add alerts using dataIntegrityViolations, alerts, or pentaho table
        // ToDo:  This is a placeholder
        apps.add(addToHivDashboardSecondColumn(app(Apps.HIV_ALERTS,
                "pih.app.alerts.title",
                "fas fa-fw fa-exclamation-circle",
                null,
                null,
                objectNode(
                        "widget", "latestobsforconceptlist",
                        "icon", "fas fa-fw fa-exclamation-circle",
                        "label", "pih.app.alerts.title",
                        "concepts", MirebalaisConstants.WEIGHT_CONCEPT_UUID
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        // additional columns to add to the HIV Program Dashboard
        apps.add(addToHivDashboardFirstColumn(app(Apps.HIV_SUMMARY,
                "pih.app.patientSummary.title",
                "fas fa-fw fa-user-md",
                null,
                null,
                objectNode(
                        "widget", "latestobsforconceptlist",
                        "icon", "fas fa-fw fa-user-md",
                        "label", "pih.app.patientSummary.title",
                        "concepts", MirebalaisConstants.NEXT_RETURN_VISIT_UUID + "," + MirebalaisConstants.CD4_COUNT_UUID + "," + MirebalaisConstants.VIRAL_LOAD_UUID
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        // Viral Load
        apps.add(addToHivDashboardFirstColumn(app(Apps.HIV_VL_GRAPH,
                "pih.app.hivvlGraph.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "icon", "fas fa-fw fa-chart-bar",
                        "label", "pih.app.hivvlGraph.title",
                        "conceptId", MirebalaisConstants.VIRAL_LOAD_UUID,
                        "type", "logarithmic",
                        "maxResults", "5"  // TODO what should this be?
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));


        apps.add(addToHivDashboardSecondColumn(
                graphs.getBmiGraph(".hiv"),
                "coreapps",
                "dashboardwidgets/dashboardWidget"));

    }

    private void enableCovid19() {

        // ToDo: Fix privileges and locations for these forms.
        extensions.add(visitAction(Extensions.COVID19_INITIAL_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + EncounterTypes.COVID19_INTAKE.uuid(),
                "fas fa-fw fa-sun",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("covid19Intake.xml")),
                Privileges.TASK_EMR_ENTER_COVID.privilege(),
                and(sessionLocationHasTag(LocationTags.COVID_LOCATION),
                    visitDoesNotHaveEncounterOfType(EncounterTypes.COVID19_INTAKE))));

        extensions.add(visitAction(Extensions.COVID19_FOLLOWUP_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + EncounterTypes.COVID19_FOLLOWUP.uuid(),
                "fas fa-fw fa-sun",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("covid19Followup.xml")),
                Privileges.TASK_EMR_ENTER_COVID.privilege(),
                and(sessionLocationHasTag(LocationTags.COVID_LOCATION),
                    visitHasEncounterOfType(EncounterTypes.COVID19_INTAKE))));

        extensions.add(visitAction(Extensions.COVID19_DISCHARGE_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + EncounterTypes.COVID19_DISCHARGE.uuid(),
                "fas fa-fw fa-sun",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("covid19Discharge.xml")),
                Privileges.TASK_EMR_ENTER_COVID.privilege(),
                and(sessionLocationHasTag(LocationTags.COVID_LOCATION),
                        visitDoesNotHaveEncounterOfType(EncounterTypes.COVID19_DISCHARGE),
                        visitHasEncounterOfType(EncounterTypes.COVID19_INTAKE))));
    }

    // not currently used
    private void enableHIViSantePlus() {
        // iSantePlus forms were added but  should not appear
        extensions.add(visitAction(Extensions.HIV_ADULT_INITIAL_VISIT_ACTION,
                "pih.task.hivIntakeISantePlus.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/iSantePlus/SaisiePremiereVisiteAdult.xml")),
                Privileges.TASK_EMR_ENTER_HIV_CONSULT_NOTE.privilege(),
                and(patientIsAdult())));

        extensions.add(visitAction(Extensions.HIV_PEDS_INITIAL_VISIT_ACTION,
                "pih.task.hivIntakeISantePlus.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/iSantePlus/SaisiePremiereVisitePediatrique.xml")),
                null,
                and(patientIsChild())));

        extensions.add(visitAction(Extensions.HIV_ADULT_FOLLOWUP_VISIT_ACTION,
                "pih.task.hivFollowupISantePlus.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/iSantePlus/VisiteDeSuivi.xml")),
                Privileges.TASK_EMR_ENTER_HIV_CONSULT_NOTE.privilege(),
                and(patientIsAdult())));

        extensions.add(visitAction(Extensions.HIV_PEDS_FOLLOWUP_VISIT_ACTION,
                "pih.task.hivFollowupISantePlus.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/iSantePlus/VisiteDeSuiviPediatrique.xml")),
                Privileges.TASK_EMR_ENTER_HIV_CONSULT_NOTE.privilege(),
                and(patientIsChild())));

        extensions.add(visitAction(Extensions.HIV_ADHERENCE_VISIT_ACTION,
                "pih.task.hivAdherence.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/iSantePlus/Adherence.xml")),
                Privileges.TASK_EMR_ENTER_HIV_CONSULT_NOTE.privilege(),
                null));
    }

    private void enableAsthmaProgram() {
        configureBasicProgramDashboard(AsthmaProgram.ASTHMA);

        apps.add(addToAsthmaDashboardFirstColumn(app(Apps.ASTHMA_SYMPTOMS_OBS_TABLE,
                "pih.app.asthma.symptomsObsTable.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsacrossencounters",
                        "icon", "fas fa-fw fa-list-alt",
                        "label", "pih.app.asthma.symptomsObsTable.title",
                        "concepts", MirebalaisConstants.ASTHMA_DAYTIME_SYMPTOMS_TWICE_WEEKLY + ','
                                + MirebalaisConstants.ASTHMA_DAYTIME_SYMPTOMS_ONCE_WEEKLY + ','
                                + MirebalaisConstants.ASTHMA_MEDS_TWICE_WEEKLY + ','
                                + MirebalaisConstants.LIMITATION_OF_ACTIVITY,
                        "maxRecords", "40"  // MEX-127
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));
    }

    private void enableDiabetesProgram() {

        configureBasicProgramDashboard(DiabetesProgram.DIABETES);

        apps.add(addToDiabetesDashboardFirstColumn(app(Apps.ABDOMINAL_CIRCUMFERENCE_GRAPH,
                "pih.app.abdominalCircumference.graph.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "label", "pih.app.abdominalCircumference.graph.title",
                        "icon", "fas fa-fw fa-chart-bar",
                        "conceptId", MirebalaisConstants.ABDOMINAL_CIRCUMFERENCE_CONCEPT_UUID,
                        "maxRecords", "4"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToDiabetesDashboardFirstColumn(app(Apps.FOOT_EXAM_OBS_TABLE,
                "pih.app.footExamObsTable.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsacrossencounters",
                        "icon", "fas fa-fw fa-list-alt",
                        "label", "pih.app.footExamObsTable.title",
                        "concepts", MirebalaisConstants.FOOT_EXAM_CONCEPT_UUID,
                        "maxRecords", "100"  // MEX-127 - should be ten or so rows
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToDiabetesDashboardFirstColumn(app(Apps.URINARY_ALBUMIN_OBS_TABLE,
                "pih.app.urinaryAlbuminObsTable.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsacrossencounters",
                        "icon", "fas fa-fw fa-list-alt",
                        "label", "pih.app.urinaryAlbuminObsTable.title",
                        "concepts", MirebalaisConstants.URINARY_ALBUMIN_CONCEPT_UUID,
                        "maxRecords", "10"  // MEX-127 - should be 3 rows
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToDiabetesDashboardFirstColumn(app(Apps.ALC_TOBAC_USE_SUMMARY,
                "pih.app.patientSummary.title",
                "fas fa-fw fa-user-md",
                null,
                null,
                objectNode(
                        "widget", "latestobsforconceptlist",
                        "icon", "fas fa-fw fa-user-md",
                        "label", "pih.app.patientSummary.title",
                        "concepts", MirebalaisConstants.ALCOHOL_USE_CONCEPT_UUID + ','
                                + MirebalaisConstants.TOBACCO_USE_CONCEPT_UUID
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToDiabetesDashboardSecondColumn(app(Apps.GLUCOSE_GRAPH,
                "pih.app.glucose.graph.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "label", "pih.app.glucose.graph.title",
                        "icon", "fas fa-fw fa-chart-bar",
                        "conceptId", MirebalaisConstants.GLUCOSE_CONCEPT_UUID,
                        "maxRecords", "12"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToDiabetesDashboardSecondColumn(app(Apps.HBA1C_GRAPH,
                "pih.app.hba1c.graph.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "label", "pih.app.hba1c.graph.title",
                        "icon", "fas fa-fw fa-chart-bar",
                        "conceptId", MirebalaisConstants.HBA1C_CONCEPT_UUID,
                        "maxRecords", "4"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToDiabetesDashboardSecondColumn(
                graphs.getCholesterolGraph(".diabetes"),
                "coreapps",
                "dashboardwidgets/dashboardWidget"));
    }

    private void enableEpilepsyProgram() {

        configureBasicProgramDashboard(EpilepsyProgram.EPILEPSY);

        apps.add(addToEpilepsyDashboardSecondColumn(app(Apps.EPILEPSY_SUMMARY,
                "pih.app.patientSummary.title",
                "fas fa-fw fa-user-md",
                null,
                null,
                objectNode(
                        "widget", "latestobsforconceptlist",
                        "icon", "fas fa-fw fa-user-md",
                        "label", "pih.app.patientSummary.title",
                        "concepts", MirebalaisConstants.EPI_SEIZURES_BASELINE
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToEpilepsyDashboardSecondColumn(app(Apps.EPILEPSY_SEIZURES,
                "pih.app.epilepsy.seizureGraph",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "label", "pih.app.epilepsy.seizureGraph",
                        "icon", "fas fa-fw fa-chart-bar",
                        "conceptId", MirebalaisConstants.EPI_SEIZURES,
                        "maxResults", "30"  // MEX-127
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

    }

    private void enableHypertensionProgram() {

        configureBasicProgramDashboard(HypertensionProgram.HYPERTENSION);

        apps.add(addToHypertensionDashboardFirstColumn(
                graphs.getBloodPressureGraph(".htn"),
                "coreapps",
                "dashboardwidgets/dashboardWidget"));

        apps.add(addToHypertensionDashboardFirstColumn(app(Apps.BLOOD_PRESSURE_OBS_TABLE,
                "pih.app.bloodPressure.obsTable.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsacrossencounters",
                        "icon", "fas fa-fw fa-list-alt",
                        "label", "pih.app.bloodPressure.obsTable.title",
                        "concepts", MirebalaisConstants.SYSTOLIC_BP_CONCEPT_UUID + ","
                                + MirebalaisConstants.DIASTOLIC_BP_CONCEPT_UUID,
                        "maxRecords", "100"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToHypertensionDashboardSecondColumn(
                graphs.getBmiGraph(".htn"),
                "coreapps",
                "dashboardwidgets/dashboardWidget"));

        apps.add(addToHypertensionDashboardSecondColumn(
                graphs.getCholesterolGraph(".htn"),
                "coreapps",
                "dashboardwidgets/dashboardWidget"));
    }


    private void enableMentalHealthProgram() {
        configureBasicProgramDashboard(MentalHealthProgram.MENTAL_HEALTH);

        if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO) || config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            apps.add(addToMentalHealthDashboardSecondColumn(
                    graphs.getPHQ9Graph(ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN),
                    "coreapps",
                    "dashboardwidgets/dashboardWidget"));
        }

        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
                apps.add(addToMentalHealthDashboardSecondColumn(
                        graphs.getWHODASGraph(ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN),
                        "coreapps",
                        "dashboardwidgets/dashboardWidget"));
                }

        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            apps.add(addToMentalHealthDashboardSecondColumn(
                    graphs.getZLDSIGraph(ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN),
                    "coreapps",
                    "dashboardwidgets/dashboardWidget"));
        }

        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI) || config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            apps.add(addToMentalHealthDashboardSecondColumn(
                    graphs.getSeizureFrequencyGraph(ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN),
                    "coreapps",
                    "dashboardwidgets/dashboardWidget"));
        }

        if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO)) {
            apps.add(addToMentalHealthDashboardSecondColumn(
                    graphs.getGAD7Graph(ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN),
                    "coreapps",
                    "dashboardwidgets/dashboardWidget"));
        }
    }

    private void enableMalnutritionProgram() {
        configureBasicProgramDashboard(MalnutritionProgram.MALNUTRITION);

        apps.add(addToMalnutritionDashboardSecondColumn(
                graphs.getBmiGraph(".malnutrition"),
                "coreapps",
                "dashboardwidgets/dashboardWidget"));

        apps.add(addToMalnutritionDashboardSecondColumn(app(Apps.HEAD_CIRCUMFERENCE_GRAPH,
                "pih.app.headCircumferenceGraph.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "icon", "fas fa-fw fa-chart-bar",
                        "conceptId", MirebalaisConstants.HEAD_CIRC_CONCEPT_UUID,
                        "maxResults", "12"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

    }

    private void enableBiometrics(Config config) {

        extensions.add(fragmentExtension(Extensions.BIOMETRICS_FIND_PATIENT,
                "registrationapp",
                "biometrics/fingerprintSearch",
                null,   // shouldn't need a privilege, since this is injected into the patient search
                ExtensionPoints.PATIENT_SEARCH,
                map("scanUrl", config.getBiometricsConfig().getScanUrl(),
                        "devicesUrl", config.getBiometricsConfig().getDevicesUrl())));


        apps.add(addToRegistrationSummarySecondColumnContent(app(Apps.BIOMETRICS_SUMMARY,
                "registrationapp.biometrics.summary",
                "fas fa-fw fa-fingerprint",
                null,
                "App: registrationapp.registerPatient",
                objectNode(
                        "registrationAppId", Apps.PATIENT_REGISTRATION,
                        "icon", "fas fa-fw fa-fingerprint")),
                "registrationapp",
                "summary/biometricsSummary"));
    }

    private void enablePathologyTracking() {

        apps.add(addToHomePage(app(Apps.PATHOLOGY_TRACKING,
                "labtrackingapp.app.label",
                "fas fa-fw fa-microscope",
                "/labtrackingapp/labtrackingViewQueue.page?appId=" + Apps.PATHOLOGY_TRACKING,
                Privileges.APP_LAB_TRACKING_MONITOR_ORDERS.privilege(),
                null),
                sessionLocationHasTag(LocationTags.ORDER_PATHOLOGY_LOCATION)));

        extensions.add(visitAction(Extensions.ORDER_LAB_VISIT_ACTION,
                "labtrackingapp.orderPathology.label",
                "fas fa-fw fa-microscope",
                "link",
                "/labtrackingapp/labtrackingAddOrder.page?patientId={{patient.uuid}}&visitId={{visit.id}}",
                Privileges.TASK_LAB_TRACKING_PLACE_ORDERS.privilege(),
                sessionLocationHasTag(LocationTags.ORDER_PATHOLOGY_LOCATION)));

        apps.add(addToClinicianDashboardSecondColumn(app(Apps.PATHOLOGY_SUMMARY,
                "labtrackingapp.pathology",
                "fas fa-fw fa-microscope",
                null,
                Privileges.TASK_LAB_TRACKING_PLACE_ORDERS.privilege(),
                null),
                "labtrackingapp", "labtrackingPatientDashboard"));
    }

    private void enableLabs() {
        /* this really represents the Labs component, that has a sub-menu linking to multiple apps*/
        apps.add(addToHomePage(app(Apps.LABS,
                "pih.app.labs.label",
                "fas fa-fw fa-vial",
                "owa/labworkflow/index.html",
                Privileges.APP_LABS.privilege(),
                null),
                null));

        apps.add(findPatientTemplateApp(Apps.ORDER_LABS,
                "pih.app.labs.ordering",
                "icon",
                Privileges.TASK_ORDER_LABS.privilege(),
                "/owa/orderentry/index.html?patient={{patientId}}&page=laborders&breadcrumbOverride={{breadcrumbOverride}}&returnUrl=%2F" + WebConstants.CONTEXT_PATH + "%2Fowa%2Flabworkflow%2Findex.html",
                arrayNode(objectNode("icon", "fas fa-fw fa-home", "link", "/index.htm"),
                        objectNode("label", "pih.app.labs.label", "link", "/owa/labworkflow/index.html"),
                        objectNode("label", "coreapps.findPatient.app.label"))
                ));

        extensions.add(overallAction(Extensions.ORDER_LABS_OVERALL_ACTION,
                "pihcore.orderLabs.overallAction.label",
                "fas fa-fw fa-vial",
                "link",
                "owa/orderentry/index.html?patient={{patient.uuid}}&page=laborders",
                Privileges.TASK_ORDER_LABS.privilege(),
                null));

        extensions.add(overallAction(Extensions.VIEW_LABS_OVERALL_ACTION,
                "pihcore.viewLabs.overallAction.label",
                "fas fa-fw fa-vial",
                "link",
                "owa/labworkflow/index.html?patient={{patient.uuid}}#/LabResults",
                Privileges.TASK_VIEW_LABS.privilege(),
                null));

        apps.add(addToClinicianDashboardFirstColumn(app(Apps.COVID_LAB_RESULTS,
                "pihcore.labResults.covid",
                "fas fa-fw fa-sun",
                null,
                null,
                objectNode(
                        "widget", "latestObsForConceptList",
                        "icon", "fas fa-fw fa-sun",
                        "label", "pihcore.labResults.covid",
                        "concepts", MirebalaisConstants.SARS_COV2_ANTIBODY_TEST + "," + MirebalaisConstants.SARS_COV2_ANTIGEN_TEST + "," + MirebalaisConstants.SARS_COV2_RT_PCR_TEST + "," + MirebalaisConstants.SARS_COV2_XPERT_TEST,
                        "conceptNameType", "shortName",
                        "maxRecords", "4"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

    }

    private void enableGrowthChart() {

        extensions.add(overallAction(Extensions.VIEW_GROWTH_CHART_ACTION,
                "pihcore.viewGrowthChart.overallAction.label",
                "fas fa-fw fa-chart-line",
                "link",
                "growthchart/growthCharts.page?patientId={{patient.uuid}}",
                Privileges.TASK_VIEW_GROWTH_CHARTS.privilege(),
                null));
    }

    private void enableCohortBuilder() {

        apps.add(addToHomePage(app(Apps.COHORT_BUILDER_APP,
                "pih.app.cohortBuilder.label",
                "fas fa-fw icon-check-in",
                "owa/cohortbuilder/index.html#/",
                 Privileges.APP_COHORT_BUILDER.privilege(),null)));

    }


    private void enablePrograms(Config config) {

        List<String> supportedPrograms = new ArrayList<String>();

        if (config.isComponentEnabled(Components.ANC_PROGRAM)) {
            supportedPrograms.add(ANCProgram.ANC.uuid());
            enableANCProgram();
        }

        if (config.isComponentEnabled(Components.ASTHMA_PROGRAM)) {
            supportedPrograms.add(AsthmaProgram.ASTHMA.uuid());
            enableAsthmaProgram();
        }

        if (config.isComponentEnabled(Components.DIABETES_PROGRAM)) {
            supportedPrograms.add(DiabetesProgram.DIABETES.uuid());
            enableDiabetesProgram();
        }

        if (config.isComponentEnabled(Components.EPILEPSY_PROGRAM)) {
            supportedPrograms.add(EpilepsyProgram.EPILEPSY.uuid());
            enableEpilepsyProgram();
        }

        if (config.isComponentEnabled(Components.HIV)) {
            supportedPrograms.add(HIVProgram.HIV.uuid());
            enableHIV();
        }

        if (config.isComponentEnabled(Components.HYPERTENSION_PROGRAM)) {
            supportedPrograms.add(HypertensionProgram.HYPERTENSION.uuid());
            enableHypertensionProgram();
        }

        if (config.isComponentEnabled(Components.MALNUTRITION_PROGRAM)) {
            supportedPrograms.add(MalnutritionProgram.MALNUTRITION.uuid());
            enableMalnutritionProgram();
        }

        if (config.isComponentEnabled(Components.MENTAL_HEALTH)) {
            enableMentalHealthForm();
            supportedPrograms.add(MentalHealthProgram.MENTAL_HEALTH.uuid());
            enableMentalHealthProgram();
        }

        if (config.isComponentEnabled(Components.MENTAL_HEALTH_FORM)) {
            enableMentalHealthForm();
        }

        if (config.isComponentEnabled(Components.MENTAL_HEALTH_PROGRAM)) {
            supportedPrograms.add(MentalHealthProgram.MENTAL_HEALTH.uuid());
            enableMentalHealthProgram();
        }

        if (config.isComponentEnabled(Components.NCD)) {
            supportedPrograms.add(NCDProgram.NCD.uuid());
            enableNCDs();
        }

        if (config.isComponentEnabled(Components.VACCINATION_FORM)) {
            enableVaccinationOnly();
        }

        if (config.isComponentEnabled(Components.ONCOLOGY)) {
            supportedPrograms.add(OncologyProgram.ONCOLOGY.uuid());
            enableOncology();
        }

        if (config.isComponentEnabled(Components.MCH)) {
            enableMCHForms();
            supportedPrograms.add(MCHProgram.MCH.uuid());
            enableMCHProgram();
        }

        if (config.isComponentEnabled(Components.MCH_PROGRAM)) {
            supportedPrograms.add(MCHProgram.MCH.uuid());
            enableMCHProgram();
        }

        if (config.isComponentEnabled(Components.ZIKA)) {
            supportedPrograms.add(ZikaProgram.ZIKA.uuid());
            configureBasicProgramDashboard(ZikaProgram.ZIKA);
        }

        if (config.isComponentEnabled(Components.COVID19)) {
            supportedPrograms.add(Covid19Program.COVID19.uuid());
            configureBasicProgramDashboard(Covid19Program.COVID19);
        }

        // TODO better/more granular privileges?
        if (supportedPrograms.size() > 0) {

            apps.add(addToHomePage(app(Apps.PROGRAM_SUMMARY_LIST,
                    "pih.app.programSummaryList.title",
                    "fas fa-fw fa-chart-pie",
                    "/coreapps/applist/appList.page?app=" + Apps.PROGRAM_SUMMARY_LIST,
                    Privileges.APP_COREAPPS_SUMMARY_DASHBOARD.privilege(),
                    null),
                    null));

            apps.add(addToClinicianDashboardSecondColumn(app(Apps.PROGRAMS_LIST,
                    "coreapps.programsListDashboardWidget.label",
                    "fas fa-fw fa-stethoscope",  // TODO figure out right icon
                    null,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD.privilege(),
                    objectNode(
                            "widget", "programs",
                            "icon", "fas fa-fw fa-stethoscope",
                            "label", "coreapps.programsDashboardWidget.label",
                            "dateFormat", "dd MMM yyyy",
                            "supportedPrograms", StringUtils.join(supportedPrograms, ','),
                            "enableProgramDashboards", "true"
                    )),
                    "coreapps", "dashboardwidgets/dashboardWidget"));
        }
    }

    private void configureBasicProgramDashboard(ProgramDescriptor program) {
        apps.add(addToProgramDashboardFirstColumn(program,
                app("pih.app." + program.uuid() + ".patientProgramSummary",
                "coreapps.currentEnrollmentDashboardWidget.label",
                "fas fa-fw fa-stethoscope",  // TODO figure out right icon
                null,
                Privileges.APP_COREAPPS_PATIENT_DASHBOARD.privilege(),
                objectNode(
                        "widget", "programstatus",
                        "icon", "fas fa-fw fa-stethoscope",
                        "label", "coreapps.currentEnrollmentDashboardWidget.label",
                        "dateFormat", "dd MMM yyyy",
                        "program", program.uuid(),
                        "locationTag", LocationTags.PROGRAM_LOCATION.uuid()
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToProgramDashboardFirstColumn(program,
                app("pih.app." + program.uuid() + ".patientProgramHistory",
                "coreapps.programHistoryDashboardWidget.label",
                "fas fa-fw fa-stethoscope",  // TODO figure out right icon
                null,
                Privileges.APP_COREAPPS_PATIENT_DASHBOARD.privilege(),
                objectNode(
                        "icon", "fas fa-fw fa-stethoscope",
                        "label", "coreapps.programHistoryDashboardWidget.label",
                        "dateFormat", "dd MMM yyyy",
                        "program", program.uuid(),
                        "includeActive", false,
                        "locationTag", LocationTags.PROGRAM_LOCATION.uuid()
                )),
                "coreapps", "program/programHistory"));

        // TODO correct the privilege
        apps.add(addToProgramSummaryListPage(app("pih.app." + program.uuid() + ".programSummary.dashboard",
                "pih.app." + program.uuid() +".programSummary.dashboard",
                "fas fa-fw fa-list-alt",
                "/coreapps/summarydashboard/summaryDashboard.page?app=" + "pih.app." + program.uuid() + ".programSummary.dashboard",
                Privileges.APP_COREAPPS_SUMMARY_DASHBOARD.privilege(),
                objectNode(
                        "program", program.uuid()
                )),
                null));

        apps.add(addToProgramSummaryDashboardFirstColumn(program,
                app("pih.app." + program.uuid() + " .programStatistics",
                "pih.app." + program.uuid() + ".programStatistics.title",
                "fas fa-fw fa-bars",  // TODO figure out right icon
                null,
                null, // TODO restrict by privilege or location)
                objectNode(
                        "widget", "programstatistics",
                        "icon", "fas fa-fw fa-bars",
                        "label", "pih.app." + program.uuid() + ".programStatistics.title",
                        "dateFormat", "dd MMM yyyy",
                        "program", program.uuid()
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));
    }

    private void enableExportPatients() {
        apps.add(addToSystemAdministrationPage(app(Apps.PATIENT_EXPORT,
                "pihcore.patient.export",
                "fas fa-fw fa-external-link-alt",
                "pihcore/export/exportPatients.page",
                "App: coreapps.systemAdministration",
                null)));
    }

    private void enableImportPatients() {
        apps.add(addToSystemAdministrationPage(app(Apps.PATIENT_IMPORT,
                "pihcore.patient.import",
                "fas fa-fw fa-sign-in-alt",
                "pihcore/export/importPatients.page",
                "App: coreapps.systemAdministration",
                null)));
    }

    private void enableProviderRelationships() {

        apps.add(addToClinicianDashboardFirstColumn(app(Apps.PROVIDER_RELATIONSHIPS_CLINICAL_SUMMARY,
                "pihcore.providerRelationshipsDashboardWidget.label",
                "fas fa-fw fa-users",
                null,
                null,
                objectNode(
                    "widget", "relationships",
                    "editPrivilege", CoreAppsConstants.PRIVILEGE_EDIT_RELATIONSHIPS,
                    "dashboardPage", "/coreapps/clinicianfacing/patient.page?patientId={{patientUuid}}",
                    "providerPage", "/coreapps/providermanagement/editProvider.page?personUuid={{personUuid}}",
                    "includeRelationshipTypes", RelationshipTypeBundle.RelationshipTypes.CHW_TO_PATIENT,
                    "icon", "fas fa-fw fa-users",
                    "label", "pihcore.providerRelationshipsDashboardWidget.label"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));
    }

    private void enableRelationships() {

        apps.add(addToClinicianDashboardSecondColumn(app(Apps.RELATIONSHIPS_CLINICAL_SUMMARY,
                "pihcore.relationshipsDashboardWidget.label",
                "fas fa-fw fa-users",
                null,
                null, // TODO restrict by privilege or location)
                objectNode(
                        "widget", "relationships",
                        "editPrivilege", CoreAppsConstants.PRIVILEGE_EDIT_RELATIONSHIPS,
                        "dashboardPage", "/coreapps/clinicianfacing/patient.page?patientId={{patientUuid}}",
                        "providerPage", "/coreapps/providermanagement/editProvider.page?personUuid={{personUuid}}",
                        "includeRelationshipTypes", RelationshipTypeBundle.RelationshipTypes.SPOUSE_PARTNER
                                + "," + PihCoreConstants.RELATIONSHIP_SIBLING
                                + "," + PihCoreConstants.RELATIONSHIP_PARENT_CHILD,
                        "icon", "fas fa-fw fa-users",
                        "label", "pihcore.relationshipsDashboardWidget.label"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));
    }

    // TODO we probably will break this down in a different way instead of "order entry"... like perhaps "drugOrders" and "labOrders"... but for demoing starting like thist
    // TODO this widget was also moved from Order Enry UI to Core Apps, we need to test everything is still working before reenabling
    private void enableOrderEntry() {
        apps.add(addToClinicianDashboardSecondColumn(app(Apps.ACTIVE_DRUG_ORDERS,
                "coreapps.patientdashboard.activeDrugOrders",
                null,
                null,
                null, // TODO restrict by privilege?
               null),
                "coreapps", "patientdashboard/activeDrugOrders"));

    }

    private void enablePatientDocuments() {
        apps.add(addToClinicianDashboardSecondColumn(app(Apps.PATIENT_DOCUMENTS,
                "pihcore.patientDocuments.label",
                "fas fa-fw fa-paperclip",
                null,
                Privileges.APP_ATTACHMENTS_PAGE.privilege(),
                null),
                "attachments", "dashboardWidget"));

        extensions.add(overallAction(Extensions.PATIENT_DOCUMENTS_OVERALL_ACTION,
                "pihcore.patientDocuments.overallAction.label",
                "fas fa-fw fa-paperclip",
                "link",
                "attachments/attachments.page?patient={{patient.uuid}}&patientId={{patient.patientId}}",
                Privileges.APP_ATTACHMENTS_PAGE.privilege(),
                null));
    }


    private void enableConditionList() {

        AppDescriptor conditionList = app(Apps.CONDITION_LIST,
                null, // TODO: add our own label?
                null,  // TODO: add our own icon?
                null,
                Privileges.TASK_MANAGE_CONDITIONS_LIST.privilege(),
                null);

        apps.add(addToClinicianDashboardFirstColumn(conditionList, "coreapps", "conditionlist/conditions"));
        apps.add(addToHivDashboardSecondColumn(cloneApp(conditionList, Apps.HIV_CONDITION_LIST), "coreapps", "conditionlist/conditions"));
    }

    private void enableJ9() {
        apps.add(addToHomePage(app(Apps.J9_REFERRALS,
                "pih.app.j9Referrals.title",
                "fa fa-fw fa-baby",
                "spa/referrals-queue",
                Privileges.TASK_EMR_ENTER_MCH.privilege(),
                null),
                sessionLocationHasTag(LocationTags.MCH_LOCATION)));
    }

    private void registerLacollinePatientRegistrationEncounterTypes() {
        // TODO: I *believe* these are used in Lacolline, but not 100% sure
        registerTemplateForEncounterType(EncounterTypes.PAYMENT,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-money-bill-alt");
        registerTemplateForEncounterType(EncounterTypes.PRIMARY_CARE_VISIT,
                findExtensionById(EncounterTemplates.DEFAULT), "fas fa-fw fa-calendar");

    }

    private void addPaperRecordActionsIncludesIfNeeded() {

        // this provides the javascript the backs the three overall action buttons
        // we need to make sure we don't add it twice
        if (! containsExtension(extensions, Extensions.PAPER_RECORD_ACTIONS_INCLUDES)) {
            extensions.add(fragmentExtension(Extensions.PAPER_RECORD_ACTIONS_INCLUDES,
                    "paperrecord",
                    "patientdashboard/overallActionsIncludes",
                    null,
                    ExtensionPoints.DASHBOARD_INCLUDE_FRAGMENTS,
                    null));
        }
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

    public void setReadyForRefresh(Boolean readyForRefresh) {
        this.readyForRefresh = readyForRefresh;
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
