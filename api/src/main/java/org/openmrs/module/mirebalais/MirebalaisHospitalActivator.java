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
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.emrapi.utils.GeneralUtils;
import org.openmrs.module.htmlformentry.HtmlFormEntryConstants;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.importpatientfromws.api.ImportPatientFromWebService;
import org.openmrs.module.importpatientfromws.api.RemoteServerConfiguration;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.namephonetics.NamePhoneticsConstants;
import org.openmrs.module.pacsintegration.PacsIntegrationConstants;
import org.openmrs.module.paperrecord.CloseStalePullRequestsTask;
import org.openmrs.module.radiologyapp.RadiologyConstants;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.openmrs.util.OpenmrsConstants;

import java.util.Date;
import java.util.HashMap;
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

            setupCoreGlobalProperties();
            setupHtmlFormEntryGlobalProperties();
            setupUiFrameworkGlobalProperties();
            setupNamePhoneticsGlobalProperties();
            setupEmrGlobalProperties();
            setupRadiologyGlobalProperties();
            setupMirebalaisGlobalProperties();
            setupPacsIntegrationGlobalProperties();
            setupCoreAppsGlobalProperties();
            setupIdentifierGeneratorsIfNecessary(service, identifierSourceService);
            setupConnectionToMasterPatientIndex();
            injectProviderIdentifierGenerator();
            setupCloseStalePullRequestsTask();
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


    private Map<String, Integer> getAppsOrderingMap() {
        Map<String, Integer> appsOrdering = new HashMap<String, Integer>();
        appsOrdering.put("emr.archivesRoom", 1);
        appsOrdering.put("emr.retrospectiveCheckin", 2);
        appsOrdering.put("patientregistration.main", 3);
        appsOrdering.put("emr.findPatient", 4);
        appsOrdering.put("emr.systemAdministration", 5);
        appsOrdering.put("emr.activeVisits", 6);
        return appsOrdering;
    }

    private void setupIdentifierGeneratorsIfNecessary(MirebalaisHospitalService service,
                                                      IdentifierSourceService identifierSourceService) {

        configureIdGenerators = new ConfigureIdGenerators(customProperties, identifierSourceService, service);

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
    }

    private void setupUiFrameworkGlobalProperties() {
        setExistingGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT, "dd MMM yyyy");
        setExistingGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT, "dd MMM yyyy hh:mm aa");
    }

    private void setupMirebalaisGlobalProperties() {
        setExistingGlobalProperty(
                "layout.address.format",
                "<org.openmrs.layout.web.address.AddressTemplate><nameMappings class=\"properties\"><property name=\"country\" value=\"mirebalais.address.country\"/><property name=\"stateProvince\" value=\"mirebalais.address.stateProvince\"/><property name=\"cityVillage\" value=\"mirebalais.address.cityVillage\"/><property name=\"address3\" value=\"mirebalais.address.neighborhoodCell\"/><property name=\"address1\" value=\"mirebalais.address.address1\"/><property name=\"address2\" value=\"mirebalais.address.address2\"/></nameMappings><sizeMappings class=\"properties\"><property name=\"country\" value=\"40\"/><property name=\"stateProvince\" value=\"40\"/><property name=\"cityVillage\" value=\"40\"/><property name=\"address3\" value=\"60\"/><property name=\"address1\" value=\"60\"/><property name=\"address2\" value=\"60\"/></sizeMappings><elementDefaults class=\"properties\"><property name=\"country\" value=\"Haiti\"/></elementDefaults><lineByLineFormat><string>address2</string><string>address1</string><string>address3 cityVillage</string><string>stateProvince country</string></lineByLineFormat></org.openmrs.layout.web.address.AddressTemplate>");
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

    private void setupEmrGlobalProperties() {

        // used when placing radiology orders
        setExistingGlobalProperty(EmrApiConstants.GP_ORDERING_PROVIDER_ENCOUNTER_ROLE, "c458d78e-8374-4767-ad58-9f8fe276e01c");

        // check_in clerk encounter role is set to Oupatient Application User Role
        setExistingGlobalProperty(EmrConstants.GP_CHECK_IN_CLERK_ENCOUNTER_ROLE, "cbfe0b9d-9923-404c-941b-f048adc8cdc0");

        // for consultations
        setExistingGlobalProperty(EmrConstants.GP_CONSULT_ENCOUNTER_TYPE, "92fd09b4-5335-4f7e-9f63-b2a663fd09a6");
        setExistingGlobalProperty(EmrConstants.GP_CLINICIAN_ENCOUNTER_ROLE, "4f10ad1a-ec49-48df-98c7-1391c6ac7f05");

        // paper record location = Mirebalais
        setExistingGlobalProperty(EmrConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE, "e66645eb-03a8-4991-b4ce-e87318e37566");
        setExistingGlobalProperty(EmrConstants.GP_EXTERNAL_DOSSIER_IDENTIFIER_TYPE, "9dbea4d4-35a9-4793-959e-952f2a9f5347");
        setExistingGlobalProperty(EmrConstants.GP_EXTRA_PATIENT_IDENTIFIER_TYPES, "e66645eb-03a8-4991-b4ce-e87318e37566,139766e8-15f5-102d-96e4-000c29c2a5d7");

        setExistingGlobalProperty(EmrConstants.GP_AT_FACILITY_VISIT_TYPE, "f01c54cb-2225-471a-9cd5-d348552c337c");
        setExistingGlobalProperty(EmrConstants.GP_CHECK_IN_ENCOUNTER_TYPE, "55a0d3ea-a4d7-4e88-8f01-5aceb2d3c61b");
        setExistingGlobalProperty(EmrConstants.PRIMARY_IDENTIFIER_TYPE, "ZL EMR ID");

        setExistingGlobalProperty(EmrConstants.PAYMENT_AMOUNT_CONCEPT, "5d1bc5de-6a35-4195-8631-7322941fe528");
        setExistingGlobalProperty(EmrConstants.PAYMENT_REASON_CONCEPT, "36ba7721-fae0-4da4-aef2-7e476cc04bdf");
        setExistingGlobalProperty(EmrConstants.PAYMENT_RECEIPT_NUMBER_CONCEPT, "20438dc7-c5b4-4d9c-8480-e888f4795123");
        setExistingGlobalProperty(EmrConstants.PAYMENT_CONSTRUCT_CONCEPT, "7a6330f1-9503-465c-8d63-82e1ad914b47");

        setExistingGlobalProperty(EmrConstants.GP_DIAGNOSIS_SET_OF_SETS, "8fcd0b0c-f977-4a66-a1b5-ad7ce68e6770");

        setExistingGlobalProperty(EmrApiConstants.GP_UNKNOWN_LOCATION, "8d6c993e-c2cc-11de-8d13-0010c6dffd0f");

        setExistingGlobalProperty(EmrApiConstants.GP_ADMISSION_ENCOUNTER_TYPE, "260566e1-c909-4d61-a96f-c1019291a09d");
        setExistingGlobalProperty(EmrApiConstants.GP_EXIT_FROM_INPATIENT_ENCOUNTER_TYPE, "b6631959-2105-49dd-b154-e1249e0fbcd7");
        setExistingGlobalProperty(EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_ENCOUNTER_TYPE, "436cfe33-6b81-40ef-a455-f134a9f7e580");
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

    private void setupCloseStalePullRequestsTask() {

        SchedulerService schedulerService = Context.getSchedulerService();

        TaskDefinition task = schedulerService.getTaskByName(EmrConstants.TASK_CLOSE_STALE_PULL_REQUESTS);

        if (task == null) {
            task = new TaskDefinition();
            task.setName(EmrConstants.TASK_CLOSE_STALE_PULL_REQUESTS);
            task.setDescription(EmrConstants.TASK_CLOSE_STALE_PULL_REQUESTS_DESCRIPTION);
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
            boolean anyChanges = GeneralUtils.setPropertyIfDifferent(task, "description", EmrConstants.TASK_CLOSE_STALE_PULL_REQUESTS_DESCRIPTION);
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

    public void setCustomProperties(MirebalaisCustomProperties customProperties) {
        this.customProperties = customProperties;
    }
}
