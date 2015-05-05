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
import org.openmrs.GlobalProperty;
import org.openmrs.Privilege;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.layout.web.name.NameSupport;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants;
import org.openmrs.module.mirebalais.setup.AppointmentSchedulingSetup;
import org.openmrs.module.mirebalais.setup.ArchivesSetup;
import org.openmrs.module.mirebalais.setup.LegacyMasterPatientIndexSetup;
import org.openmrs.module.mirebalais.setup.NameTemplateSetup;
import org.openmrs.module.pihcore.setup.PatientIdentifierSetup;
import org.openmrs.module.mirebalais.setup.PrinterSetup;
import org.openmrs.module.mirebalais.setup.ReportSetup;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.config.ConfigLoader;
import org.openmrs.module.printer.PrinterService;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.util.OpenmrsConstants;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class MirebalaisHospitalActivator implements ModuleActivator {

    protected Log log = LogFactory.getLog(getClass());

    private RuntimeProperties customProperties;

    private Boolean testMode = false;

    public MirebalaisHospitalActivator() {
        customProperties = new RuntimeProperties();
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

        try {
            Config config = Context.getRegisteredComponents(Config.class).get(0); // currently only one of these

            // Reload configuration based on runtime properties values, defaulting to mirebalais if nothing found
            String configs = Context.getRuntimeProperties().getProperty(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY, "mirebalais");
            config.reload(ConfigLoader.load(configs));

            // configure name template (don't do this in Mirebalais yet)
            if (!config.getSite().equals(ConfigDescriptor.Site.MIREBALAIS)) {
                NameSupport nameSupport = Context.getRegisteredComponent("nameSupport", NameSupport.class);

                // hack: configure both name support beans, since two actually exist (?)
                NameTemplateSetup.configureNameTemplate(nameSupport);
                NameTemplateSetup.configureNameTemplate(NameSupport.getInstance());
            }

            log.info("Mirebalais Hospital Module refreshed");
        }
        catch (Exception e) {
            Module mod = ModuleFactory.getModuleById(MirebalaisConstants.MIREBALAIS_MODULE_ID);
            ModuleFactory.stopModule(mod);
            throw new RuntimeException("failed to setup the required modules", e);
        }
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

            Config config = Context.getRegisteredComponents(Config.class).get(0); // currently only one of these

            MirebalaisHospitalService service = Context.getService(MirebalaisHospitalService.class);
            IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
            AdministrationService administrationService = Context.getAdministrationService();
            ReportService reportService = Context.getService(ReportService.class);
            ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);
            SerializedObjectDAO serializedObjectDAO = Context.getRegisteredComponents(SerializedObjectDAO.class).get(0);
            LocationService locationService = Context.getLocationService();
            PrinterService printerService = Context.getService(PrinterService.class);
            PaperRecordProperties paperRecordProperties = Context.getRegisteredComponent("paperRecordProperties", PaperRecordProperties.class);
            FeatureToggleProperties featureToggleProperties = Context.getRegisteredComponent("featureToggles", FeatureToggleProperties.class);

            removeOldGlobalProperties();
            removeOldPrivileges();

            // register our custom print handlers
            PrinterSetup.registerPrintHandlers(printerService);

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

            if (!config.isComponentEnabled(CustomAppLoaderConstants.Components.CLINICIAN_DASHBOARD)) {
                updateGlobalProperty(CoreAppsConstants.GP_DEFAULT_DASHBOARD, "visits");
            }

            if (config.isComponentEnabled(CustomAppLoaderConstants.Components.CHECK_IN)) {
                updateGlobalPropertyFromConfig(config, MirebalaisConstants.CHECK_IN_PAYMENT_AMOUNT_DEFAULT_GP);
                updateGlobalPropertyFromConfig(config, MirebalaisConstants.CHECK_IN_PAYMENT_AMOUNTS_GP);
                updateGlobalPropertyFromConfig(config, MirebalaisConstants.CHECK_IN_PAYMENT_AMOUNT_LABELS_GP);
            }

            if (config.isComponentEnabled(CustomAppLoaderConstants.Components.RADIOLOGY) && config.getSite().equals(ConfigDescriptor.Site.MIREBALAIS)) {
                updateGlobalProperty(OpenmrsConstants.GP_ORDER_NUMBER_GENERATOR_BEAN_ID, MirebalaisConstants.RADIOLOGY_ORDER_NUMBER_GENERATOR_BEAN_ID);
            }

            if (!testMode) {   // super hack to ignore ReportSetup when running MirebalaisHospitalCompotentTest; TODO is to fix and get this to work
                if (config.isComponentEnabled(CustomAppLoaderConstants.Components.OVERVIEW_REPORTS) || config.isComponentEnabled(CustomAppLoaderConstants.Components.DATA_EXPORTS)) {
                    // must happen after location tags have been configured
                    ReportSetup.scheduleReports(reportService, reportDefinitionService, administrationService, serializedObjectDAO, config);
                }
            }

          /*  if (featureToggleProperties.isFeatureEnabled("cdi") && config.getSite().equals(ConfigDescriptor.Site.MIREBALAIS)) {
                migratePaperRecordLocation(paperRecordProperties);
            }
*/
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

    private void removeOldGlobalProperties() {
        AdministrationService administrationService = Context.getAdministrationService();
        administrationService.purgeGlobalProperty(administrationService.getGlobalPropertyObject(EmrApiConstants.GP_CONSULT_ENCOUNTER_TYPE));
    }

  /*  private void migratePaperRecordLocation(PaperRecordProperties paperRecordProperties) {

        Context.getAdministrationService().executeSQL("update patient_identifier set location_id = (select location_id from location where uuid='"+
                Locations.MIREBALAIS_HOSPITAL.uuid() + "')" +
                "where identifier_type = (select patient_identifier_type_id from patient_identifier_type where uuid = '" +
                paperRecordProperties.getPaperRecordIdentifierType().getUuid() + "')" +
                "and location_id = (select location_id from location where uuid='" +
                Locations.MIREBALAIS_CDI_PARENT.uuid() + "')", false);

        Context.getAdministrationService().executeSQL("update paperrecord_paper_record set record_location = (select location_id from location where uuid='" +
                Locations.MIREBALAIS_HOSPITAL.uuid() + "')" +
                "where record_location = (select location_id from location where uuid='" +
                Locations.MIREBALAIS_CDI_PARENT.uuid() + "')", false);

    }*/

    private void removeOldPrivileges() {
        UserService userService = Context.getUserService();
        Privilege privilege = userService.getPrivilege("App: appointmentschedulingui.scheduleAdmin");
        if (privilege != null) {
            userService.purgePrivilege(privilege);
        }
    }

    private void updateGlobalProperty(String name, Object value) {
        AdministrationService administrationService = Context.getAdministrationService();
        GlobalProperty gp = administrationService.getGlobalPropertyObject(name);
        gp.setPropertyValue(value == null ? "" : value.toString());
        administrationService.saveGlobalProperty(gp);
    }

    private void updateGlobalPropertyFromConfig(Config config, String name) {
        updateGlobalProperty(name, config.getGlobalProperty(name));
    }

    public void setCustomProperties(RuntimeProperties customProperties) {
        this.customProperties = customProperties;
    }

    public void setTestMode(Boolean testMode) {
        this.testMode = testMode;
    }
}
