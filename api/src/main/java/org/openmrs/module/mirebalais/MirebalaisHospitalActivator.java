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

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appointmentscheduling.reporting.dataset.definition.AppointmentDataSetDefinition;
import org.openmrs.module.appointmentschedulingui.AppointmentSchedulingUIConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.emrapi.utils.GeneralUtils;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.importpatientfromws.api.ImportPatientFromWebService;
import org.openmrs.module.importpatientfromws.api.RemoteServerConfiguration;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalais.htmlformentry.CauseOfDeathListTagHandler;
import org.openmrs.module.mirebalais.task.MarkAppointmentsAsMissedOrCompletedTask;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.CoreMetadata;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.MirebalaisSpecificMetadata;
import org.openmrs.module.mirebalaisreports.MirebalaisReportsProperties;
import org.openmrs.module.paperrecord.CloseStaleCreateRequestsTask;
import org.openmrs.module.paperrecord.CloseStalePullRequestsTask;
import org.openmrs.module.paperrecord.PaperRecordConstants;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class MirebalaisHospitalActivator implements ModuleActivator {

    protected Log log = LogFactory.getLog(getClass());

    private MirebalaisCustomProperties customProperties;

    private ConfigureIdGenerators configureIdGenerators;

    public MirebalaisHospitalActivator() {
        customProperties = new MirebalaisCustomProperties();
    }

    /**
     * @see ModuleActivator#willRefreshContext()
     */
    public void willRefreshContext() {
        log.info("Refreshing Mirebalais Hospital Module");
    }

    /**
     * @see ModuleActivator#contextRefreshed()
     */
    public void contextRefreshed() {
        log.info("Mirebalais Hospital Module refreshed");
    }

    /**
     * @see ModuleActivator#willStart()
     */
    public void willStart() {
        log.info("Starting Mirebalais Hospital Module");
    }

    /**
     * @see ModuleActivator#started()
     */
    public void started() {
        try {
            MirebalaisHospitalService service = Context.getService(MirebalaisHospitalService.class);
            IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
            ReportService reportService = Context.getService(ReportService.class);
            ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);
            LocationService locationService = Context.getLocationService();
            PaperRecordProperties paperRecordProperties = Context.getRegisteredComponent("paperRecordProperties", PaperRecordProperties.class);
            FeatureToggleProperties featureToggleProperties = Context.getRegisteredComponent("featureToggles", FeatureToggleProperties.class);

            Context.getService(AppFrameworkService.class).disableApp("registrationapp.basicRegisterPatient");

            // the coreapps version of this points to the new patient summary, and we want the old dashboard for now
            Context.getService(AppFrameworkService.class).disableApp("coreapps.activeVisits");
            Context.getService(AppFrameworkService.class).disableApp("coreapps.awaitingAdmission");
            Context.getService(AppFrameworkService.class).disableApp("coreapps.systemAdministrationApp");
            Context.getService(AppFrameworkService.class).disableApp("coreapps.configuremetadata");

            removeOldGlobalProperties();
            setupIdentifierGeneratorsIfNecessary(service, identifierSourceService, locationService);
            setupConnectionToMasterPatientIndex();
            injectProviderIdentifierGenerator();
            setupCloseStalePullRequestsTask();
            setupCloseStaleCreateRequestsTask();
            setupMarkAppointmentAsMissedOrCompletedTask();
            setupHtmlFormEntryTagHandlers();
            setupHtmlForms();
            customizeDailyAppointmentsDataSet();
            scheduleReports(reportService, reportDefinitionService);

            if (featureToggleProperties.isFeatureEnabled("cdi")) {
                migratePaperRecordIdentifierLocation(paperRecordProperties);
            }

        } catch (Exception e) {
            Module mod = ModuleFactory.getModuleById(MirebalaisConstants.MIREBALAIS_MODULE_ID);
            ModuleFactory.stopModule(mod);
            throw new RuntimeException("failed to setup the required modules", e);
        }
        log.info("Mirebalais Hospital Module started");
    }

	/**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		log.info("Stopping Mirebalais Hospital Module");
	}

	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("Mirebalais Hospital Module stopped");
	}

    private void injectProviderIdentifierGenerator() {
        // injects the custom provider identifier generator into the Account Service
        Context.getService(AccountService.class).setProviderIdentifierGenerator(new MirebalaisProviderIdentifierGenerator());
    }


    private void setupIdentifierGeneratorsIfNecessary(MirebalaisHospitalService service,
                                                      IdentifierSourceService identifierSourceService,
                                                      LocationService locationService) {

        configureIdGenerators = new ConfigureIdGenerators(customProperties, identifierSourceService, locationService, service);
        createPatientIdGenerator(service);
        createDossierNumberGenerator(service);
    }

    private void createDossierNumberGenerator(MirebalaisHospitalService service) {
        PatientIdentifierType dossierIdentifierType = service.getDossierIdentifierType();
        SequentialIdentifierGenerator sequentialIdentifierGenerator = configureIdGenerators.sequentialIdentifierGeneratorToDossier(dossierIdentifierType);
        configureIdGenerators.setAutoGenerationOptionsForDossierNumberGenerator(sequentialIdentifierGenerator);
    }

    private void createPatientIdGenerator(MirebalaisHospitalService service) {
        PatientIdentifierType zlIdentifierType = service.getZlIdentifierType();
        RemoteIdentifierSource remoteZlIdentifierSource = configureIdGenerators.remoteZlIdentifierSource(zlIdentifierType);
        IdentifierPool localZlIdentifierPool = configureIdGenerators.localZlIdentifierSource(remoteZlIdentifierSource);
        configureIdGenerators.setAutoGenerationOptionsForZlIdentifier(localZlIdentifierPool);
    }

    private void removeOldGlobalProperties() {
        AdministrationService administrationService = Context.getAdministrationService();
        administrationService.purgeGlobalProperty(administrationService.getGlobalPropertyObject(EmrApiConstants.GP_CONSULT_ENCOUNTER_TYPE));
    }

    private void setupCloseStalePullRequestsTask() {

        SchedulerService schedulerService = Context.getSchedulerService();

        TaskDefinition task = schedulerService.getTaskByName(PaperRecordConstants.TASK_CLOSE_STALE_PULL_REQUESTS);

        if (task == null) {
            task = new TaskDefinition();
            task.setName(PaperRecordConstants.TASK_CLOSE_STALE_PULL_REQUESTS);
            task.setDescription(PaperRecordConstants.TASK_CLOSE_STALE_PULL_REQUESTS_DESCRIPTION);
            task.setTaskClass(CloseStalePullRequestsTask.class.getName());
            task.setStartTime(DateUtils.addMinutes(new Date(), 5));
            task.setRepeatInterval(new Long(3600));  // once an hour
            task.setStartOnStartup(true);
            try {
                schedulerService.scheduleTask(task);
            }
			catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule close stale pull requests task", e);
            }
        }
		else {
            boolean anyChanges = GeneralUtils.setPropertyIfDifferent(task, "description", PaperRecordConstants.TASK_CLOSE_STALE_PULL_REQUESTS_DESCRIPTION);
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "taskClass", CloseStalePullRequestsTask.class.getName());
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "repeatInterval", new Long(3600));
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "startOnStartup", true);
            if (anyChanges) {
                schedulerService.saveTask(task);
            }
            if (!task.getStarted()) {
                task.setStarted(true);
                try {
                    schedulerService.scheduleTask(task);
                }
				catch (SchedulerException e) {
                    throw new RuntimeException("Failed to schedule close stale pull requests task", e);
                }
            }
        }
    }

    private void setupCloseStaleCreateRequestsTask() {

        SchedulerService schedulerService = Context.getSchedulerService();

        TaskDefinition task = schedulerService.getTaskByName(PaperRecordConstants.TASK_CLOSE_STALE_CREATE_REQUESTS);

        if (task == null) {
            task = new TaskDefinition();
            task.setName(PaperRecordConstants.TASK_CLOSE_STALE_CREATE_REQUESTS);
            task.setDescription(PaperRecordConstants.TASK_CLOSE_STALE_CREATE_REQUESTS_DESCRIPTION);
            task.setTaskClass(CloseStaleCreateRequestsTask.class.getName());
            task.setStartTime(DateUtils.addMinutes(new Date(), 5));
            task.setRepeatInterval(new Long(3600));  // once an hour
            task.setStartOnStartup(true);
            try {
                schedulerService.scheduleTask(task);
            }
			catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule close stale create requests task", e);
            }
        }
		else {
            boolean anyChanges = GeneralUtils.setPropertyIfDifferent(task, "description", PaperRecordConstants.TASK_CLOSE_STALE_CREATE_REQUESTS_DESCRIPTION);
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "taskClass", CloseStaleCreateRequestsTask.class.getName());
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "repeatInterval", new Long(3600));
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "startOnStartup", true);
            if (anyChanges) {
                schedulerService.saveTask(task);
            }
            if (!task.getStarted()) {
                task.setStarted(true);
                try {
                    schedulerService.scheduleTask(task);
                }
				catch (SchedulerException e) {
                    throw new RuntimeException("Failed to schedule close stale create requests task", e);
                }
            }
        }
    }

    public void setupMarkAppointmentAsMissedOrCompletedTask() {

        SchedulerService schedulerService = Context.getSchedulerService();

        TaskDefinition task = schedulerService.getTaskByName(MirebalaisConstants.TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED);

        if (task == null) {
            task = new TaskDefinition();
            task.setName(MirebalaisConstants.TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED);
            task.setDescription(MirebalaisConstants.TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_DESCRIPTION);
            task.setTaskClass(MarkAppointmentsAsMissedOrCompletedTask.class.getName());
            task.setStartTime(DateUtils.addMinutes(new Date(), 5));   // doesn't really do anything since start on startup = true
            task.setRepeatInterval(MirebalaisConstants.TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_REPEAT_INTERVAL);
            task.setStartOnStartup(true);
            try {
                schedulerService.scheduleTask(task);
            }
			catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule mark appointments as missed or completed task", e);
            }
        }
		else {
            boolean anyChanges = GeneralUtils.setPropertyIfDifferent(task, "description", MirebalaisConstants.TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_DESCRIPTION);
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "taskClass", MarkAppointmentsAsMissedOrCompletedTask.class.getName());
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "repeatInterval", MirebalaisConstants.TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_REPEAT_INTERVAL);  // we can't pass in the constant directly for some reason because it is static
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "startOnStartup", true);
            if (anyChanges) {
                schedulerService.saveTask(task);
            }
            if (!task.getStarted()) {
                task.setStarted(true);
                try {
                    schedulerService.scheduleTask(task);
                }
				catch (SchedulerException e) {
                    throw new RuntimeException("Failed to schedule mark appointments as missed or completed task", e);
                }
            }
        }

    }

    private void setupConnectionToMasterPatientIndex() {
        String url = customProperties.getLacollineServerUrl();
        String username = customProperties.getLacollineUsername();
        String password = customProperties.getLacollinePassword();

        if (url == null || username == null || password == null) {
            log.warn("Not configuring link to Lacolline server (url, username, and password are required)");
            return;
        }

        Map<String, PatientIdentifierType> identifierTypeMap = new HashMap<String, PatientIdentifierType>();
        identifierTypeMap.put("a541af1e-105c-40bf-b345-ba1fd6a59b85", Context.getService(MirebalaisHospitalService.class).getZlIdentifierType());
        // TODO create PatientIdentifierType for Lacolline KE dossier number
        identifierTypeMap.put("e66645eb-03a8-4991-b4ce-e87318e37566", Context.getService(MirebalaisHospitalService.class).getExternalDossierIdentifierType());
        // TODO create PatientIdentifierType for Lacolline dental dossier number

        Map<String, Location> locationMap = new HashMap<String, Location>();
        locationMap.put("23e7bb0d-51f9-4d5f-b34b-2fbbfeea1960", Context.getLocationService().getLocationByUuid(MirebalaisConstants.LACOLLINE_LOCATION_UUID));

        Map<String, PersonAttributeType> attributeTypeMap = new HashMap<String, PersonAttributeType>();
        attributeTypeMap.put("340d04c4-0370-102d-b0e3-001ec94a0cc1", MetadataUtils.existing(PersonAttributeType.class, CoreMetadata.PersonAttributeTypes.TELEPHONE_NUMBER));

        RemoteServerConfiguration config = new RemoteServerConfiguration();
        config.setUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setIdentifierTypeMap(identifierTypeMap);
        config.setLocationMap(locationMap);
        config.setAttributeTypeMap(attributeTypeMap);

        Context.getService(ImportPatientFromWebService.class).registerRemoteServer("lacolline", config);
    }

    public void setupHtmlFormEntryTagHandlers() throws Exception {
        HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
        htmlFormEntryService.addHandler(MirebalaisConstants.HTMLFORMENTRY_CAUSE_OF_DEATH_LIST_TAG_NAME, new CauseOfDeathListTagHandler());
    }

    private void setupHtmlForms() throws Exception {

       try {
            ResourceFactory resourceFactory = ResourceFactory.getInstance();
            FormService formService = Context.getFormService();
            HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);

            List<String> htmlforms = Arrays.asList("mirebalais:htmlforms/admissionNote.xml",
                                                    "mirebalais:htmlforms/checkin.xml",
                                                    "mirebalais:htmlforms/liveCheckin.xml",
                                                    "mirebalais:htmlforms/surgicalPostOpNote.xml",
                                                    "mirebalais:htmlforms/vitals.xml",
                                                    "mirebalais:htmlforms/transferNote.xml",
                                                    "mirebalais:htmlforms/dischargeNote.xml",
                                                    "mirebalais:htmlforms/outpatientConsult.xml",
                                                    "mirebalais:htmlforms/edNote.xml",
                                                    "mirebalais:htmlforms/deathCertificate.xml");

            for (String htmlform : htmlforms) {
                HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService, htmlFormEntryService, htmlform);
            }

       }
       catch (Exception e) {
            // this is a hack to get component test to pass until we find the proper way to mock this
            if (ResourceFactory.getInstance().getResourceProviders() == null) {
                log.error("Unable to load HTML forms--this error is expected when running component tests");
            }
            else {
                throw e;
            }
       }
    }

    private void customizeDailyAppointmentsDataSet() {

        DataSetDefinitionService dataSetDefinitionService = Context.getService(DataSetDefinitionService.class);
        PaperRecordProperties paperRecordProperties = Context.getRegisteredComponents(PaperRecordProperties.class).get(0);

        AppointmentDataSetDefinition dsd =
                (AppointmentDataSetDefinition) dataSetDefinitionService.getDefinition(AppointmentSchedulingUIConstants.DAILY_SCHEDULED_APPOINTMENT_DATA_SET_DEFINITION_UUID, AppointmentDataSetDefinition.class);

        if (dsd == null || dsd.getId() == null) {
            throw new RuntimeException("Daily scheduled appointment data set definition not found");
        }

        // swap out the identifier column to show dossier number instead of primary identifier
        dsd.removeColumnDefinition("identifier");

        PatientIdentifierDataDefinition dd = new PatientIdentifierDataDefinition(null, paperRecordProperties.getPaperRecordIdentifierType());
        dd.setIncludeFirstNonNullOnly(true);
        dsd.addColumn("identifier", dd, "", new PropertyConverter(PatientIdentifier.class, "identifier"));

        dataSetDefinitionService.saveDefinition(dsd);
    }

    private void scheduleReports(ReportService reportService, ReportDefinitionService reportDefinitionService) {

        // TODO:  a cron job to clean old reports up?

        if (customProperties.getScheduleBackupReports()) {

            // schedule the all patients report to run at 4am and 4pm everyday
            ReportRequest allPatientsScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.ALL_PATIENTS_SCHEDULED_REPORT_REQUEST_UUID);
            if (allPatientsScheduledReportRequest == null) {
                allPatientsScheduledReportRequest = new ReportRequest();
            }
            ReportDefinition allPatientsReportDefinition = reportDefinitionService.getDefinitionByUuid(MirebalaisReportsProperties.ALL_PATIENTS_WITH_IDS_REPORT_DEFINITION_UUID);
            allPatientsScheduledReportRequest.setUuid(MirebalaisReportsProperties.ALL_PATIENTS_SCHEDULED_REPORT_REQUEST_UUID);
            allPatientsScheduledReportRequest.setReportDefinition(Mapped.noMappings(allPatientsReportDefinition));
            allPatientsScheduledReportRequest.setRenderingMode(getCsvReportRenderer(reportService, allPatientsReportDefinition));
            allPatientsScheduledReportRequest.setSchedule("0 0 4-23/12 * * ?");
            reportService.queueReport(allPatientsScheduledReportRequest);

            // schedule the appointments report to run  at 4am and 4pm everyday, retrieving all appointments for the next seven days
            ReportRequest appointmentsScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.APPOINTMENTS_SCHEDULED_REPORT_REQUEST_UUID);
            if (appointmentsScheduledReportRequest == null) {
                appointmentsScheduledReportRequest = new ReportRequest();
            }
            ReportDefinition appointmentsReportDefinition = reportDefinitionService.getDefinitionByUuid(MirebalaisReportsProperties.APPOINTMENTS_REPORT_DEFINITION_UUID);
            appointmentsScheduledReportRequest.setUuid(MirebalaisReportsProperties.APPOINTMENTS_SCHEDULED_REPORT_REQUEST_UUID);
            appointmentsScheduledReportRequest.setReportDefinition(Mapped.map(appointmentsReportDefinition, "startDate=${start_of_today},endDate=${start_of_today + 7d}"));
            appointmentsScheduledReportRequest.setRenderingMode(getCsvReportRenderer(reportService, appointmentsReportDefinition));
            appointmentsScheduledReportRequest.setSchedule("0 0 4-23/12 * * ?");
            reportService.queueReport(appointmentsScheduledReportRequest);

            // schedule the appointments report to run  at 4am and 4pm everyday retrieving all check-ins for the past seven days
            ReportRequest checkInsDataExportScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.CHECKINS_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            if (checkInsDataExportScheduledReportRequest == null) {
                checkInsDataExportScheduledReportRequest = new ReportRequest();
            }
            ReportDefinition checkInsDataExportReportDefinition = reportDefinitionService.getDefinitionByUuid(MirebalaisReportsProperties.CHECKINS_DATA_EXPORT_REPORT_DEFINITION_UUID);
            checkInsDataExportScheduledReportRequest.setUuid(MirebalaisReportsProperties.CHECKINS_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            checkInsDataExportScheduledReportRequest.setReportDefinition(Mapped.map(checkInsDataExportReportDefinition, "startDate=${start_of_today - 7d},endDate=${now}"));
            checkInsDataExportScheduledReportRequest.setRenderingMode(getCsvReportRenderer(reportService, checkInsDataExportReportDefinition));
            checkInsDataExportScheduledReportRequest.setSchedule("0 0 4-23/12 * * ?");

            reportService.queueReport(checkInsDataExportScheduledReportRequest);
        }
        else {

            ReportRequest allPatientsScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.ALL_PATIENTS_SCHEDULED_REPORT_REQUEST_UUID);
            if (allPatientsScheduledReportRequest != null) {
                reportService.purgeReportRequest(allPatientsScheduledReportRequest);
            }

            ReportRequest appointmentsScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.APPOINTMENTS_SCHEDULED_REPORT_REQUEST_UUID);
            if (appointmentsScheduledReportRequest != null) {
                reportService.purgeReportRequest(appointmentsScheduledReportRequest);
            }

            ReportRequest checkInsDataExportScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.CHECKINS_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            if (checkInsDataExportScheduledReportRequest != null) {
                reportService.purgeReportRequest(checkInsDataExportScheduledReportRequest);
            }

        }

    }

    private void migratePaperRecordIdentifierLocation(PaperRecordProperties paperRecordProperties) {

        Context.getAdministrationService().executeSQL("update patient_identifier set location_id = (select location_id from location where uuid='"+
                MirebalaisSpecificMetadata.MirebalaisHospitalLocations.MIREBALAIS_HOSPITAL_MAIN_CAMPUS + "')" +
                "where identifier_type = (select patient_identifier_type_id from patient_identifier_type where uuid = '" +
                paperRecordProperties.getPaperRecordIdentifierType().getUuid() + "')" +
                "and location_id = (select location_id from location where uuid='" +
                MirebalaisSpecificMetadata.MirebalaisHospitalLocations.MIREBALAIS_HOSPITAL + "')", false);

    }

    private RenderingMode getCsvReportRenderer(ReportService reportService, ReportDefinition reportDefinition) {

        for (RenderingMode candidate : reportService.getRenderingModes(reportDefinition)) {
            if (candidate.getDescriptor().startsWith("org.openmrs.module.reporting.report.renderer.CsvReportRenderer")) {
                return candidate;
            }
        }
        return null;
    }

    public void setCustomProperties(MirebalaisCustomProperties customProperties) {
        this.customProperties = customProperties;
    }

}
