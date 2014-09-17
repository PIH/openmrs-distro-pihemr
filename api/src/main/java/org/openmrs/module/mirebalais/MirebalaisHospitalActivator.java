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
import org.openmrs.GlobalProperty;
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
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appointmentscheduling.reporting.dataset.definition.AppointmentDataSetDefinition;
import org.openmrs.module.appointmentschedulingui.AppointmentSchedulingUIConstants;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.emrapi.utils.GeneralUtils;
import org.openmrs.module.htmlformentry.HtmlFormEntryConstants;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.importpatientfromws.api.ImportPatientFromWebService;
import org.openmrs.module.importpatientfromws.api.RemoteServerConfiguration;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalais.htmlformentry.CauseOfDeathListTagHandler;
import org.openmrs.module.mirebalais.task.MarkAppointmentsAsMissedOrCompletedTask;
import org.openmrs.module.mirebalaisreports.MirebalaisReportsProperties;
import org.openmrs.module.namephonetics.NamePhoneticsConstants;
import org.openmrs.module.pacsintegration.PacsIntegrationConstants;
import org.openmrs.module.paperrecord.CloseStaleCreateRequestsTask;
import org.openmrs.module.paperrecord.CloseStalePullRequestsTask;
import org.openmrs.module.paperrecord.PaperRecordConstants;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.radiologyapp.RadiologyConstants;
import org.openmrs.module.reporting.ReportingConstants;
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
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

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


            Context.getService(AppFrameworkService.class).disableApp("registrationapp.basicRegisterPatient");

            // the coreapps version of this points to the new patient summary, and we want the old dashboard for now
            Context.getService(AppFrameworkService.class).disableApp("coreapps.activeVisits");
            Context.getService(AppFrameworkService.class).disableApp("coreapps.awaitingAdmission");

            PlatformTransactionManager platformTransactionManager = Context.getRegisteredComponents(PlatformTransactionManager.class).get(0);
            TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    setupCoreGlobalProperties();
                    setupHtmlFormEntryGlobalProperties();
                    setupUiFrameworkGlobalProperties();
                    setupNamePhoneticsGlobalProperties();
                    setupEmrApiGlobalProperties();
                    setupEmrGlobalProperties();
                    setupPaperRecordGlobalProperties();
                    setupRadiologyGlobalProperties();
                    setupMirebalaisGlobalProperties();
                    setupPacsIntegrationGlobalProperties();
                    setupCoreAppsGlobalProperties();
                    setupReportingGlobalProperties();
                    setupWebServicesRestGlobalProperties();
                }
            });

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

        } catch (Exception e) {
            Module mod = ModuleFactory.getModuleById(MirebalaisConstants.MIREBALAIS_MODULE_ID);
            ModuleFactory.stopModule(mod);
            throw new RuntimeException("failed to setup the required modules", e);
        }
        log.info("Mirebalais Hospital Module started");
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

        SequentialIdentifierGenerator sequentialIdentifierGenerator = configureIdGenerators
                .sequentialIdentifierGeneratorToDossier(dossierIdentifierType);

        configureIdGenerators.setAutoGenerationOptionsForDossierNumberGenerator(sequentialIdentifierGenerator);
    }

    private void createPatientIdGenerator(MirebalaisHospitalService service) {
        PatientIdentifierType zlIdentifierType = service.getZlIdentifierType();

        RemoteIdentifierSource remoteZlIdentifierSource = configureIdGenerators.remoteZlIdentifierSource(zlIdentifierType);
        IdentifierPool localZlIdentifierPool = configureIdGenerators.localZlIdentifierSource(remoteZlIdentifierSource);

        configureIdGenerators.setAutoGenerationOptionsForZlIdentifier(localZlIdentifierPool);

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

    /**
     * Sets global property value or throws an exception if that global property does not already exist
     * (Set as protected so we can override it for testing purposes)
     *
     * @param propertyName
     * @param propertyValue
     */
    protected void setExistingGlobalProperty(String propertyName, String propertyValue) {
        AdministrationService administrationService = Context.getAdministrationService();
        GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
        if (gp == null) {
            throw new RuntimeException("global property " + propertyName + " does not exist");
        }
        gp.setPropertyValue(propertyValue);
        administrationService.saveGlobalProperty(gp);
    }

    /**
     * Sets a global property value, creating a new GlobalProperty if none exists yet with that name
     * @param propertyName
     * @param propertyValue
     */
    protected void setGlobalProperty(String propertyName, String propertyValue) {
        AdministrationService administrationService = Context.getAdministrationService();
        GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
        if (gp == null) {
            gp = new GlobalProperty(propertyName);
        }
        gp.setPropertyValue(propertyValue);
        administrationService.saveGlobalProperty(gp);
    }

    private void setupCoreGlobalProperties() {
        setExistingGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, "ht, fr, en");
        setExistingGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE, "fr");
        setExistingGlobalProperty(OpenmrsConstants.GP_PASSWORD_MINIMUM_LENGTH, "8");
        setExistingGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_DIGIT, "false");
        setExistingGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_NON_DIGIT, "false");
        setExistingGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_UPPER_AND_LOWER_CASE, "false");
    }

    private void setupHtmlFormEntryGlobalProperties() {
        setExistingGlobalProperty(HtmlFormEntryConstants.GP_DATE_FORMAT, "dd MMM yyyy");
        setExistingGlobalProperty(HtmlFormEntryConstants.GP_SHOW_DATE_FORMAT, "false");
        setGlobalProperty(HtmlFormEntryConstants.GP_UNKNOWN_CONCEPT, "3cd6fac4-26fe-102b-80cb-0017a47871b2");
    }

    private void setupUiFrameworkGlobalProperties() {
        setExistingGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT, "dd MMM yyyy");
        setExistingGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT, "dd MMM yyyy hh:mm aa");
    }

    private void setupMirebalaisGlobalProperties() {
        setExistingGlobalProperty(
                "layout.address.format",
                "<org.openmrs.layout.web.address.AddressTemplate><nameMappings class=\"properties\"><property name=\"country\" value=\"mirebalais.address.country\"/><property name=\"stateProvince\" value=\"mirebalais.address.stateProvince\"/><property name=\"cityVillage\" value=\"mirebalais.address.cityVillage\"/><property name=\"address3\" value=\"mirebalais.address.neighborhoodCell\"/><property name=\"address1\" value=\"mirebalais.address.address1\"/><property name=\"address2\" value=\"mirebalais.address.address2\"/></nameMappings><sizeMappings class=\"properties\"><property name=\"country\" value=\"40\"/><property name=\"stateProvince\" value=\"40\"/><property name=\"cityVillage\" value=\"40\"/><property name=\"address3\" value=\"60\"/><property name=\"address1\" value=\"60\"/><property name=\"address2\" value=\"60\"/></sizeMappings><elementDefaults class=\"properties\"><property name=\"country\" value=\"Haiti\"/></elementDefaults><lineByLineFormat><string>address2</string><string>address1</string><string>address3, cityVillage</string><string>stateProvince, country</string></lineByLineFormat></org.openmrs.layout.web.address.AddressTemplate>");
    }

    private void setupPacsIntegrationGlobalProperties() {
        setExistingGlobalProperty(PacsIntegrationConstants.GP_PATIENT_IDENTIFIER_TYPE_UUID,
                "a541af1e-105c-40bf-b345-ba1fd6a59b85");
        setExistingGlobalProperty(PacsIntegrationConstants.GP_DEFAULT_LOCALE, "en");
        setExistingGlobalProperty(PacsIntegrationConstants.GP_SENDING_FACILITY, "Mirebalais");
        setExistingGlobalProperty(PacsIntegrationConstants.GP_PROCEDURE_CODE_CONCEPT_SOURCE_UUID,
                "2889f378-f287-40a5-ac9c-ce77ee963ed7");
        setExistingGlobalProperty(PacsIntegrationConstants.GP_LOCATION_CODE_ATTRIBUTE_TYPE_UUID,
                "64f01c78-191d-4947-a201-7e0a7f0caf21");
        setExistingGlobalProperty(PacsIntegrationConstants.GP_HL7_LISTENER_PORT, "6663");
    }

    private void setupEmrApiGlobalProperties() {

        // used when placing radiology orders
        setExistingGlobalProperty(EmrApiConstants.GP_ORDERING_PROVIDER_ENCOUNTER_ROLE, "c458d78e-8374-4767-ad58-9f8fe276e01c");

        // check_in clerk encounter role is set to Oupatient Application User Role
        setExistingGlobalProperty(EmrApiConstants.GP_CHECK_IN_CLERK_ENCOUNTER_ROLE, "cbfe0b9d-9923-404c-941b-f048adc8cdc0");

        // for consultations
        setExistingGlobalProperty((EmrApiConstants.GP_VISIT_NOTE_ENCOUNTER_TYPE), "92fd09b4-5335-4f7e-9f63-b2a663fd09a6");
        setExistingGlobalProperty(EmrApiConstants.GP_CLINICIAN_ENCOUNTER_ROLE, "4f10ad1a-ec49-48df-98c7-1391c6ac7f05");

        setExistingGlobalProperty(EmrApiConstants.GP_EXTRA_PATIENT_IDENTIFIER_TYPES, "e66645eb-03a8-4991-b4ce-e87318e37566,139766e8-15f5-102d-96e4-000c29c2a5d7");

        setExistingGlobalProperty(EmrApiConstants.GP_AT_FACILITY_VISIT_TYPE, "f01c54cb-2225-471a-9cd5-d348552c337c");
        setExistingGlobalProperty(EmrApiConstants.GP_CHECK_IN_ENCOUNTER_TYPE, "55a0d3ea-a4d7-4e88-8f01-5aceb2d3c61b");
        setExistingGlobalProperty(EmrApiConstants.PRIMARY_IDENTIFIER_TYPE, "ZL EMR ID");

        setExistingGlobalProperty(EmrApiConstants.GP_DIAGNOSIS_SET_OF_SETS, "8fcd0b0c-f977-4a66-a1b5-ad7ce68e6770");

        setExistingGlobalProperty(EmrApiConstants.GP_UNKNOWN_LOCATION, "8d6c993e-c2cc-11de-8d13-0010c6dffd0f");

        setExistingGlobalProperty(EmrApiConstants.GP_ADMISSION_ENCOUNTER_TYPE, "260566e1-c909-4d61-a96f-c1019291a09d");
        setExistingGlobalProperty(EmrApiConstants.GP_EXIT_FROM_INPATIENT_ENCOUNTER_TYPE, "b6631959-2105-49dd-b154-e1249e0fbcd7");
        setExistingGlobalProperty(EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_ENCOUNTER_TYPE, "436cfe33-6b81-40ef-a455-f134a9f7e580");

        setExistingGlobalProperty(EmrApiConstants.GP_ADMISSION_FORM, "43acf930-eb1b-11e2-91e2-0800200c9a66");
        setExistingGlobalProperty(EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_FORM, "d068bc80-fb95-11e2-b778-0800200c9a66");
        setExistingGlobalProperty(EmrApiConstants.GP_EXIT_FROM_INPATIENT_FORM, "e0a26c20-fba6-11e2-b778-0800200c9a66");
    }

    private void setupEmrGlobalProperties() {
        setExistingGlobalProperty(EmrConstants.PAYMENT_AMOUNT_CONCEPT, "5d1bc5de-6a35-4195-8631-7322941fe528");
        setExistingGlobalProperty(EmrConstants.PAYMENT_REASON_CONCEPT, "36ba7721-fae0-4da4-aef2-7e476cc04bdf");
        setExistingGlobalProperty(EmrConstants.PAYMENT_RECEIPT_NUMBER_CONCEPT, "20438dc7-c5b4-4d9c-8480-e888f4795123");
        setExistingGlobalProperty(EmrConstants.PAYMENT_CONSTRUCT_CONCEPT, "7a6330f1-9503-465c-8d63-82e1ad914b47");
    }

    private void setupPaperRecordGlobalProperties() {
        // paper record location = Mirebalais
        setExistingGlobalProperty(PaperRecordConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE, "e66645eb-03a8-4991-b4ce-e87318e37566");
        setExistingGlobalProperty(PaperRecordConstants.GP_EXTERNAL_DOSSIER_IDENTIFIER_TYPE, "9dbea4d4-35a9-4793-959e-952f2a9f5347");
    }

    private void setupRadiologyGlobalProperties() {
        setExistingGlobalProperty(RadiologyConstants.GP_RADIOLOGY_ORDER_ENCOUNTER_TYPE, "1b3d1e13-f0b1-4b83-86ea-b1b1e2fb4efa");
        setExistingGlobalProperty(RadiologyConstants.GP_RADIOLOGY_STUDY_ENCOUNTER_TYPE, "5b1b4a4e-0084-4137-87db-dba76c784439");
        setExistingGlobalProperty(RadiologyConstants.GP_RADIOLOGY_REPORT_ENCOUNTER_TYPE, "d5ca53a7-d3b5-44ac-9aa2-1491d2a4b4e9");
        setExistingGlobalProperty(RadiologyConstants.GP_RADIOLOGY_TEST_ORDER_TYPE, "5a3a8d2e-97c3-4797-a6a8-5417e6e699ec");
        setExistingGlobalProperty(RadiologyConstants.GP_XRAY_ORDERABLES_CONCEPT, "35c24af8-6d60-4189-95c6-7e91e421d11f");
        setExistingGlobalProperty(RadiologyConstants.GP_CT_SCAN_ORDERABLES_CONCEPT, "381d653b-a6b7-438a-b9f0-5034b5272def");
        setExistingGlobalProperty(RadiologyConstants.GP_ULTRASOUND_ORDERABLES_CONCEPT, "a400b7e5-6b2f-404f-84d0-6eb2ca611a7d");
        setExistingGlobalProperty(RadiologyConstants.GP_RADIOLOGY_TECHNICIAN_ENCOUNTER_ROLE, "8f4d96e2-c97c-4285-9319-e56b9ba6029c");
        setExistingGlobalProperty(RadiologyConstants.GP_PRINCIPAL_RESULTS_INTERPRETER_ENCOUNTER_ROLE, "08f73be2-9452-44b5-801b-bdf7418c2f71");
        setExistingGlobalProperty(RadiologyConstants.GP_LEAD_RADIOLOGY_TECH_NAME, "Edner");
        setExistingGlobalProperty(RadiologyConstants.GP_LEAD_RADIOLOGY_TECH_CONTACT_INFO, "4868-9765");
        setExistingGlobalProperty(RadiologyConstants.GP_CONTRAST_ORDERABLES_CONCEPT, "60e6cd5a-3070-4b41-a36d-4730d4e85733");
        setExistingGlobalProperty(RadiologyConstants.GP_CREATININE_LEVEL_CONCEPT, "668cd2a5-60dd-4dc4-889b-e09f072c6a1a");
    }

    private void setupNamePhoneticsGlobalProperties() {
        setExistingGlobalProperty(NamePhoneticsConstants.GIVEN_NAME_GLOBAL_PROPERTY, "Double Metaphone Alternate");
        setExistingGlobalProperty(NamePhoneticsConstants.MIDDLE_NAME_GLOBAL_PROPERTY, "Double Metaphone Alternate");
        setExistingGlobalProperty(NamePhoneticsConstants.FAMILY_NAME_GLOBAL_PROPERTY, "Double Metaphone Alternate");
        setExistingGlobalProperty(NamePhoneticsConstants.FAMILY_NAME2_GLOBAL_PROPERTY, "Double Metaphone Alternate");
    }

    private void setupCoreAppsGlobalProperties() {
        setExistingGlobalProperty(CoreAppsConstants.GP_DEFAULT_PATIENT_IDENTIFIER_LOCATION, "a084f714-a536-473b-94e6-ec317b152b43");
    }

    private void setupReportingGlobalProperties() {
        setExistingGlobalProperty(ReportingConstants.DEFAULT_LOCALE_GP_NAME, "fr");
        setExistingGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION, "");
    }

    private void setupWebServicesRestGlobalProperties() {
        // up the number of results that rest web services returns (for the appointment scheduling module)
        setExistingGlobalProperty("webservices.rest.maxResultsAbsolute", "1000");
        setExistingGlobalProperty("webservices.rest.maxResultsDefault", "500");
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
            } catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule close stale pull requests task", e);
            }
        } else {
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
                } catch (SchedulerException e) {
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
            } catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule close stale create requests task", e);
            }
        } else {
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
                } catch (SchedulerException e) {
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
            } catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule mark appointments as missed or completed task", e);
            }
        } else {
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
                } catch (SchedulerException e) {
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
        attributeTypeMap.put("340d04c4-0370-102d-b0e3-001ec94a0cc1", Context.getPersonService().getPersonAttributeTypeByUuid(MirebalaisConstants.TELEPHONE_NUMBER_ATTRIBUTE_TYPE_UUID));

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
