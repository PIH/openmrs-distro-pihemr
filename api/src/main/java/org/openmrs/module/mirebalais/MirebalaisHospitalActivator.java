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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants;
import org.openmrs.module.mirebalais.config.Config;
import org.openmrs.module.mirebalais.config.ConfigDescriptor;
import org.openmrs.module.mirebalais.setup.AppointmentSchedulingSetup;
import org.openmrs.module.mirebalais.setup.ArchivesSetup;
import org.openmrs.module.mirebalais.setup.HtmlFormSetup;
import org.openmrs.module.mirebalais.setup.LegacyMasterPatientIndexSetup;
import org.openmrs.module.mirebalais.setup.LocationTagSetup;
import org.openmrs.module.mirebalais.setup.PatientIdentifierSetup;
import org.openmrs.module.mirebalais.setup.ReportSetup;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.ZanmiLocations;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class MirebalaisHospitalActivator implements ModuleActivator {

    protected Log log = LogFactory.getLog(getClass());

    private MirebalaisCustomProperties customProperties;

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
            Config config = Context.getRegisteredComponents(Config.class).get(0); // currently only one of these
            FeatureToggleProperties featureToggleProperties = Context.getRegisteredComponent("featureToggles", FeatureToggleProperties.class);

            removeOldGlobalProperties();
            injectProviderIdentifierGenerator();

            PatientIdentifierSetup.setupIdentifierGeneratorsIfNecessary(service, identifierSourceService, locationService, config, customProperties);
            LocationTagSetup.setupLocationTags(locationService, config, featureToggleProperties);

            // for now, we install all forms everywhere
            HtmlFormSetup.setupHtmlFormEntryTagHandlers();
            HtmlFormSetup.setupHtmlForms();

            if (config.isComponentEnabled(CustomAppLoaderConstants.Components.LEGACY_MPI)) {
                LegacyMasterPatientIndexSetup.setupConnectionToMasterPatientIndex(customProperties);
            }

            if (config.isComponentEnabled(CustomAppLoaderConstants.Components.ARCHIVES)) {
                ArchivesSetup.setupCloseStaleCreateRequestsTask();
                ArchivesSetup.setupCloseStalePullRequestsTask();
            }

            if (config.isComponentEnabled(CustomAppLoaderConstants.Components.APPOINTMENT_SCHEDULING)) {
                AppointmentSchedulingSetup.setupMarkAppointmentAsMissedOrCompletedTask();
                AppointmentSchedulingSetup.customizeDailyAppointmentsDataSet();
            }

            ReportSetup.scheduleReports(reportService, reportDefinitionService, config);

            if (featureToggleProperties.isFeatureEnabled("cdi") && config.getSite().equals(ConfigDescriptor.Site.MIREBALAIS)) {
                migratePaperRecordLocation(paperRecordProperties);
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

    private void removeOldGlobalProperties() {
        AdministrationService administrationService = Context.getAdministrationService();
        administrationService.purgeGlobalProperty(administrationService.getGlobalPropertyObject(EmrApiConstants.GP_CONSULT_ENCOUNTER_TYPE));
    }

    private void migratePaperRecordLocation(PaperRecordProperties paperRecordProperties) {

        Context.getAdministrationService().executeSQL("update patient_identifier set location_id = (select location_id from location where uuid='"+
                ZanmiLocations.MirebalaisLocations.MIREBALAIS_HOSPITAL_MAIN_CAMPUS + "')" +
                "where identifier_type = (select patient_identifier_type_id from patient_identifier_type where uuid = '" +
                paperRecordProperties.getPaperRecordIdentifierType().getUuid() + "')" +
                "and location_id = (select location_id from location where uuid='" +
                ZanmiLocations.MirebalaisLocations.MIREBALAIS_HOSPITAL + "')", false);

        Context.getAdministrationService().executeSQL("update paperrecord_paper_record set record_location = (select location_id from location where uuid='" +
                ZanmiLocations.MirebalaisLocations.MIREBALAIS_HOSPITAL_MAIN_CAMPUS + "')" +
                "where record_location = (select location_id from location where uuid='" +
                ZanmiLocations.MirebalaisLocations.MIREBALAIS_HOSPITAL + "')", false);

    }

    public void setCustomProperties(MirebalaisCustomProperties customProperties) {
        this.customProperties = customProperties;
    }

}
