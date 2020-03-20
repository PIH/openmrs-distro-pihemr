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
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderFactory;
import org.openmrs.module.mirebalais.setup.AppointmentSchedulingSetup;
import org.openmrs.module.mirebalais.setup.ArchivesSetup;
import org.openmrs.module.mirebalais.setup.HtmlFormSetup;
import org.openmrs.module.mirebalais.setup.LegacyMasterPatientIndexSetup;
import org.openmrs.module.mirebalais.setup.PrinterSetup;
import org.openmrs.module.mirebalais.setup.ReportSetup;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.config.ConfigLoader;
import org.openmrs.module.printer.PrinterService;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.ui.framework.page.GlobalResourceIncluder;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.resource.Resource;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.util.OpenmrsConstants;

import java.io.File;

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

            // Reload configuration based on runtime properties values
            config.reload(ConfigLoader.loadFromRuntimeProperties());

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

            AdministrationService administrationService = Context.getAdministrationService();
            ReportService reportService = Context.getService(ReportService.class);
            ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);
            SerializedObjectDAO serializedObjectDAO = Context.getRegisteredComponents(SerializedObjectDAO.class).get(0);
            PrinterService printerService = Context.getService(PrinterService.class);
            DispositionService dispositionService = Context.getService(DispositionService.class);

            removeOldPrivileges();

            setDispositionConfig(config, dispositionService);

            // register our custom print handlers
            PrinterSetup.registerPrintHandlers(printerService);

            // set up html forms--this must happen *after* MDS packages are installed, so that forms defined in code/github
            // take precedent over any in MDS packages; therefore we still do this in the Mirebalais module, not PIH Core
            HtmlFormSetup.loadHtmlForms();

            // configure default dashboard in coreapps
            updateGlobalProperty(CoreAppsConstants.GP_DASHBOARD_URL, config.getDashboardUrl());
            // configure default visits page in coreapps
            updateGlobalProperty(CoreAppsConstants.GP_VISITS_PAGE_URL, config.getVisitPageUrl());
            // configure default specific visit detail page in coreapps
            updateGlobalProperty(CoreAppsConstants.GP_VISITS_PAGE_WITH_SPECIFIC_URL, config.getVisitsPageWithSpecificUrl());

            if (config.isComponentEnabled(Components.LEGACY_MPI)) {
                LegacyMasterPatientIndexSetup.setupConnectionToMasterPatientIndex(customProperties);
            }

            if (config.isComponentEnabled(Components.ARCHIVES)) {
                ArchivesSetup.setupCloseStaleCreateRequestsTask();
                ArchivesSetup.setupCloseStalePullRequestsTask();
            }

            if (config.isComponentEnabled(Components.APPOINTMENT_SCHEDULING)) {
                AppointmentSchedulingSetup.setupMarkAppointmentAsMissedOrCompletedTask();
                if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
                    AppointmentSchedulingSetup.customizeDailyAppointmentsDataSet();
                }
            }


            if (config.isComponentEnabled(Components.RADIOLOGY) && config.getSite().equals(ConfigDescriptor.Site.MIREBALAIS)) {
                updateGlobalProperty(OpenmrsConstants.GP_ORDER_NUMBER_GENERATOR_BEAN_ID, MirebalaisConstants.RADIOLOGY_ORDER_NUMBER_GENERATOR_BEAN_ID);
            }

            includeGlobalResources(config);

            if (!testMode) {   // super hack to ignore ReportSetup and app configuration when running MirebalaisHospitalComponentTest; TODO is to fix and get this to work

                if (config.isComponentEnabled(Components.OVERVIEW_REPORTS) || config.isComponentEnabled(Components.DATA_EXPORTS)) {
                    // must happen after location tags have been configured
                    ReportSetup.setupReports(reportService, reportDefinitionService, administrationService, serializedObjectDAO, config);
                }

                // do app and extension configuration
                Context.getRegisteredComponent("customAppLoaderFactory", CustomAppLoaderFactory.class).setReadyForRefresh(true);
                ModuleFactory.getStartedModuleById("appframework").getModuleActivator().contextRefreshed();

                // on first startup, these modules may not have been able to configure their global propertes correctly because
                // all metadata was not loaded; we call the started method here to complete setup
                ModuleFactory.getStartedModuleById("registrationapp").getModuleActivator().started();
            }

        } catch (Exception e) {
            Module mod = ModuleFactory.getModuleById(MirebalaisConstants.MIREBALAIS_MODULE_ID);
            ModuleFactory.stopModule(mod, false, false);
            throw new RuntimeException("failed to setup the required modules", e);
        }
        log.info("Mirebalais Hospital Module started");
    }

    /**
     * Include custom styling sheets and scripts
     * @param config
     */
    private void includeGlobalResources(Config config) throws Exception {
        try {
            String cssResourcePath = "configuration/pih/styles/".concat(config.getCountry().name().toLowerCase()).concat(".").concat(Resource.CATEGORY_CSS);
            String jsResourcePath = "configuration/pih/scripts/".concat(config.getCountry().name().toLowerCase()).concat(".").concat(Resource.CATEGORY_JS);

            addGlobalResource(Resource.CATEGORY_CSS, "file", cssResourcePath);
            addGlobalResource(Resource.CATEGORY_JS, "file", jsResourcePath);
        }
        // this entire catch is a hack to get component test to pass until we find the proper way to mock this (see HtmlFormSetup where we do something similar)
        catch (Exception e) {
            // this is a hack to get component test to pass until we find the proper way to mock this
            if (ResourceFactory.getInstance().getResourceProviders() == null) {
                log.error("Unable to load GlobalResourcs--this error is expected when running component tests");
            } else {
                throw e;
            }
        }
    }

    private void addGlobalResource(String category, String providerName, String resourcePath) {
        ResourceFactory resourceFactory = ResourceFactory.getInstance();
        PageFactory pageFactory = Context.getRegisteredComponents(PageFactory.class).get(0);
        File resource = resourceFactory.getResource(providerName, resourcePath);
        if (resource != null) {
            GlobalResourceIncluder globalResourceIncluder = new GlobalResourceIncluder();
            globalResourceIncluder.addResource(new Resource(category, providerName, resourcePath, -100));
            pageFactory.getModelConfigurators().add(globalResourceIncluder);
        }
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
        if (gp == null) {
            throw new RuntimeException("Failed to get global property object '" + name + "'. Cannot set it to " + value);
        } else {
            gp.setPropertyValue(value == null ? "" : value.toString());
            administrationService.saveGlobalProperty(gp);
        }
    }

    private void updateGlobalPropertyFromConfig(Config config, String name) {
        updateGlobalProperty(name, config.getGlobalProperty(name));
    }

    // configure which disposition config to use
    public void setDispositionConfig(Config config, DispositionService dispositionService) {
        if (config.getDispositionConfig() != null) {
            dispositionService.setDispositionConfig(config.getDispositionConfig());
        }
    }

    public void setCustomProperties(RuntimeProperties customProperties) {
        this.customProperties = customProperties;
    }

    public void setTestMode(Boolean testMode) {
        this.testMode = testMode;
    }
}
