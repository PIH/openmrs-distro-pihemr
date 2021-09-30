package org.openmrs.module.mirebalais.setup;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Privilege;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.layout.name.NameSupport;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.RuntimeProperties;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderFactory;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.config.ConfigLoader;
import org.openmrs.module.pihcore.config.registration.BiometricsConfigDescriptor;
import org.openmrs.module.pihcore.setup.AttachmentsSetup;
import org.openmrs.module.pihcore.setup.CloseStaleVisitsSetup;
import org.openmrs.module.pihcore.setup.DrugListSetup;
import org.openmrs.module.pihcore.setup.GlobalResourceSetup;
import org.openmrs.module.pihcore.setup.HtmlFormSetup;
import org.openmrs.module.pihcore.setup.InitializerSetup;
import org.openmrs.module.pihcore.setup.LiquibaseSetup;
import org.openmrs.module.pihcore.setup.LocationTagSetup;
import org.openmrs.module.pihcore.setup.MergeActionsSetup;
import org.openmrs.module.pihcore.setup.MetadataMappingsSetup;
import org.openmrs.module.pihcore.setup.MetadataSharingSetup;
import org.openmrs.module.pihcore.setup.NameTemplateSetup;
import org.openmrs.module.pihcore.setup.PacIntegrationSetup;
import org.openmrs.module.pihcore.setup.PatientIdentifierSetup;
import org.openmrs.module.printer.PrinterService;
import org.openmrs.module.registrationcore.RegistrationCoreConstants;
import org.openmrs.module.reporting.config.ReportLoader;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.pihcore.PihCoreConstants.GP_COMPONENT_PREFIX;
import static org.openmrs.module.pihcore.PihCoreConstants.GP_CONFIGURED_SITE;

@Component
public class ConfigurationSetup {

    @Autowired
    Config config;

    @Autowired
    NameSupport nameSupport;

    @Autowired
    MetadataMappingService metadataMappingService;

    @Autowired
    PatientService patientService;

    @Autowired
    LocationService locationService;

    @Autowired
    IdentifierSourceService identifierSourceService;

    @Autowired
    DispositionService dispositionService;

    @Autowired
    ConceptService conceptService;

    @Autowired
    PrinterService printerService;

    @Autowired
    CustomAppLoaderFactory customAppLoaderFactory;

    protected static final Log log = LogFactory.getLog(ConfigurationSetup.class);

    private static String status = "pihcore.status.notStarted";
    private static Date statusDate;

    private static void setStatus(String message) {
        status = message;
        statusDate = new Date();
        log.warn(statusDate + ": " + status);
    }

    public static String getStatus() {
        return status;
    }

    public static Date getStatusDate() {
        return statusDate;
    }

    /**
     * For our purposes, there is no practical difference between "started" and "contextRefreshed", since we do not support context refreshing
     * We'll combine those things that are in those methods, starting out ordering with contextRefreshed prior to started, but then
     * moving around as we analyze dependencies and whether the original order was correct or not
     *
     * This combines all of the logic found in the pihcore and mirebalais activators across these methods.
     * For now, I'm creating this class in mirebalais since it depends on things still in mirebalais
     * Ideally we'd move this and all of the other classees over to pihcore.
     *
     * Regarding use of components, proposal would be to narrow the scope of these to entirely used for app/extension configuration.
     * Components are currently found to be used in the following places:
     *  1. In activators (see comments below, all of these usages should be able to be replaced or removed)
     *  2. In CALF - ideally all of our component usage would be here, and we could build the notion of components into our app loading configuration
     *    - Also related to CALF:  RequireUtil.patientVisitWithinPastThirtyDays
     *  3. Setting global property values "pihcore.component.<component>=true" for all enabled components, for use in ETL
     *  4. A handful of other places:
     *    - HomepageOverrideController - swap with feature toggle, that toggles to spa login page if toggled on
     *    - RedirectToDeathNoteIfMissing - swap with feature toggle, if death note is enabled; or possibly a configuration setting (gp or pihconfig value)
     *    - PihRadiologyOrdersMergeActions - don't think a component is appropriate here.  we'd want this to happen regardless of current component status, if orders of this type exist
     *    - PacIntegrationSetup - don't think a comonent is appropriate here.  why not always have the HL7 listener initialized?  Otherwise, add a GP to PACS integration module that can be configured
     */

    // Anything in here just needs to be setup once, and does not depend on any downstream configuration settings,
    // nor is it changeable by any configuration settings

    public void setupBase() throws Exception {
        CloseStaleVisitsSetup.setupCloseStaleVisitsTask();
        MergeActionsSetup.registerMergeActions();
        HtmlFormSetup.setupHtmlFormEntryTagHandlers();
        MetadataSharingSetup.setMetadataSharingResolvers();
        PrinterSetup.registerPrintHandlers(printerService);
        setupCommCareUser();
        removeOldPrivileges(); // TODO: This can likely be removed altogether at this point, or moved to liquibase
        ReportSetup.cleanupOldReports(); // TODO: could move this to liquibase or to a fixed scheduled task
    }

    // Anything in here depends on configuration settings and needs to be refreshed in a specific order,
    // as some configurations depend on settings or metadata setup in previous configurations.

    public void configureSystem() throws Exception {

        setStatus("Configuration Setup Initiated");

        // Load in PIH Config
        setStatus("Loading pih.config descriptor");
        config.reload(ConfigLoader.loadFromRuntimeProperties());

        // Configure the Name Template based on the config (TODO: Move this to config / configure outside of Java code)
        // hack: configure both name support beans, since two actually exist (?)
        setStatus("Configuring name template");
        NameTemplateSetup.configureNameTemplate(nameSupport, config);
        NameTemplateSetup.configureNameTemplate(NameSupport.getInstance(), config);

        // Execute any liquibase changesets defined in the pih/liquibase domain
        setStatus("Executing liquibase scripts in configuration");
        LiquibaseSetup.setup();

        // Setup all metadata that are before Concepts in Iniz loading order
        setStatus("Loading initializer pre-concept domains");
        InitializerSetup.loadPreConceptDomains(config);

        // Setup any global properties defined in pih config (TODO: Move this to gp iniz domain)
        // TODO: We need to consider the setting component GPs here
        setStatus("Setting additional global properties");
        setGlobalProperties(config);

        // Setup Metadata Mappings for Extra Identifier Types, as specified in PIH Config (TODO: Move this to metadatamapping iniz domain)
        if (config != null && config.getExtraIdentifierTypes() != null && config.getExtraIdentifierTypes().size() > 0) {
            setStatus("Configuring extra identifier types metadata mappings");
            List<PatientIdentifierType> extraIdentifierTypes = new ArrayList<PatientIdentifierType>();
            for (String uuid : config.getExtraIdentifierTypes()) {
                extraIdentifierTypes.add(patientService.getPatientIdentifierTypeByUuid(uuid));
            }
            metadataMappingService.mapMetadataItems(extraIdentifierTypes, EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_EXTRA_PATIENT_IDENTIFIER_TYPES);
        }

        // Setup Location Tags from PIH Config (TODO: Move this to use locations or locationtagmaps domains in Iniz)
        setStatus("Configuring location tags from pih config");
        LocationTagSetup.setupLocationTags(locationService, config);

        // Setup primary identifier type metadata mappings (TODO: Move this to metadatamapping iniz domain with site-based exclusion filters)
        setStatus("Configuring primary identifier type");
        MetadataMappingsSetup.setupPrimaryIdentifierTypeBasedOnCountry(metadataMappingService, patientService, config);

        // Setup identifier generators (TODO: This is a remaining configuration initiative piece.  Move to iniz and/or pih config)
        setStatus("Configuring identifier generators");
        PatientIdentifierSetup.setupIdentifierGeneratorsIfNecessary(identifierSourceService, locationService, config);

        // TODO: Move the below to config.  All this is ultimately doing is ensuring the setting of a single global property:
        //  registrationcore.identifierSourceId = <uuid of identifier source configured as autogeneration option for the emrapi primary identifier type>
        setStatus("Configuring global properties for registration modules");
        ModuleFactory.getStartedModuleById("registrationapp").getModuleActivator().started();

        // Initialize PACS HL7 listener, if the PACS_INTEGRATION component is enabled in pih config
        // TODO:  1. Determine if we can just enable this all the time, and if that will cause any adverse effects
        // TODO:  2. Or determine if we can enable it all the time, but switch inside the listener on the component, to determine whether to act or not
        // TODO:  3. If we leave this like this, determine if we can call this setup method over and over in a running server, or if we need to code around that
        //           In testing, when running configure, I get this:  java.lang.RuntimeException: java.net.BindException: Address already in use (Bind failed)
        setStatus("Configuring PACS HL7 Listener");
        PacIntegrationSetup.setup(config);

        // TODO: This seems harmless here for refreshing, but we should determine if
        // TODO: 1. Has this run everywhere it needs to, and can we remove this code altogether now
        // TODO: 2. If not, can we move this to a liquibase changeset instead
        setStatus("Migrating attachment concepts if necessary");
        AttachmentsSetup.migrateAttachmentsConceptsIfNecessary(conceptService);

        // Configure global scripts and css files defined in the configuration directory
        setStatus("Configuring global resources");
        GlobalResourceSetup.includeGlobalResources();

        // Rebuild search index (TODO: Confirm this is intended to run here, prior to metadata loading, rather than at the end of the process(
        if (config.shouldRebuildSearchIndex()) {
            setStatus("Rebuilding search index");
            Context.updateSearchIndex();
        }

        // TODO: Note here below are from MetadataSetupTask, which can be run in a separate thread or not

        // Install Concepts, etc from MDS Packages
        setStatus("Installing MDS packages");
        MetadataSharingSetup.installMetadataSharingPackages();

        // Load remaining Initializer domains that could depend on Concepts
        setStatus("Loading initializer post-concept domains");
        InitializerSetup.loadPostConceptDomains(config);

        // Load PIH drug list
        setStatus("Installing drug list");
        DrugListSetup.installDrugList();

        // Configure the Disposition Config based on PIH Config
        // TODO: Note, in the various activators, this is done multiple times across many started and contextRefreshed methods
        // TODO: Confirm that moving this here makes sense - after most metadata has been loaded
        if (config != null && config.getDispositionConfig() != null) {
            setStatus("Configuring disposition config");
            dispositionService.setDispositionConfig(config.getDispositionConfig());
        }

        // Load HTML Forms  TODO: Move this over to use the initializer methods for this
        setStatus("Loading HTML forms");
        HtmlFormSetup.loadHtmlForms(false);

        // Load Reports  TODO: Start a discussion about moving this functionality over to initializer
        setStatus("Loading reports");
        ReportLoader.loadReportsFromConfig();

        // TODO: This depends on specific metadata to exist, so needs to go in here
        // TODO: But we should consider if
        //  a) this is still actually needed/used, and
        //  b) we probably don't need the component check at all, as this is only setup if the lacolline runtime properties are found
        //  c) if it's used, we could likely try to make it more entirely configurable from config
        if (config.isComponentEnabled(Components.LEGACY_MPI)) {
            setStatus("Setting up connection to Legacy MPI");
            LegacyMasterPatientIndexSetup.setupConnectionToMasterPatientIndex(new RuntimeProperties());
        }

        // TODO: Can we just always have this scheduled task running and within the task itself, checkcheck if the component is enabled and it isn't enabled, it can just return without doing anything
        if (config.isComponentEnabled(Components.ARCHIVES)) {
            setStatus("Setting up Archives Scheduled Tasks");
            ArchivesSetup.setupCloseStaleCreateRequestsTask();
            ArchivesSetup.setupCloseStalePullRequestsTask();
        }

        if (config.isComponentEnabled(Components.APPOINTMENT_SCHEDULING)) {
            setStatus("Setting up Appointment Scheduling Tasks");
            // TODO: Can we just always have this scheduled task running and within the task itself, checkcheck if the component is enabled and it isn't enabled, it can just return without doing anything
            AppointmentSchedulingSetup.setupMarkAppointmentAsMissedOrCompletedTask();
            if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
                // TODO: This seems like something that should be moved to configuration.  We should add a GP to the appointment scheduling module
                //  or wherever this is created to control which identifier type to use in the report, and then set this GP in Iniz config
                AppointmentSchedulingSetup.customizeDailyAppointmentsDataSet();
            }
        }

        if (config.isComponentEnabled(Components.OVERVIEW_REPORTS) || config.isComponentEnabled(Components.DATA_EXPORTS)) {
            setStatus("Setting up scheduled report requests");
            // TODO: These scheduled report configurations should be moved to entirely to config and that config supported by reporting module natively
            ReportSetup.scheduleBackupReports(config);
            ReportSetup.scheduleMonthlyExportsReports(config);
        }

        setStatus("Reloading all apps and extensions");
        reloadAppsAndExtensions();
        setStatus("Configuration Setup Completed Successfully");
    }

    public void reloadAppsAndExtensions() {
        customAppLoaderFactory.reloadAllAppsAndExtensions();
    }

    public void setGlobalProperties(Config config) {
        if (config != null && config.getGlobalProperties() != null) {
            Map<String, String> globalProperties = config.getGlobalProperties();
            for (String name : globalProperties.keySet()) {
                setGlobalProperty(name, globalProperties.get(name));
            }
        }
        if (config != null) {
            BiometricsConfigDescriptor biometricsDescriptor = config.getBiometricsConfig();
            setGlobalProperty(RegistrationCoreConstants.GP_BIOMETRICS_IMPLEMENTATION, biometricsDescriptor.getBiometricEngine());
        }
        // configure default dashboard in coreapps
        updateGlobalProperty(CoreAppsConstants.GP_DASHBOARD_URL, config.getDashboardUrl());
        // configure default visits page in coreapps
        updateGlobalProperty(CoreAppsConstants.GP_VISITS_PAGE_URL, config.getVisitPageUrl());
        // configure default specific visit detail page in coreapps
        updateGlobalProperty(CoreAppsConstants.GP_VISITS_PAGE_WITH_SPECIFIC_URL, config.getVisitsPageWithSpecificUrl());

        // TODO: Move this to Iniz, in a mirebalais-specific gp.xml file
        if (config.isComponentEnabled(Components.RADIOLOGY) && config.getSite().equalsIgnoreCase("MIREBALAIS")) {
            updateGlobalProperty(OpenmrsConstants.GP_ORDER_NUMBER_GENERATOR_BEAN_ID, MirebalaisConstants.RADIOLOGY_ORDER_NUMBER_GENERATOR_BEAN_ID);
        }

        // Update global properties for any enabled components, so database queries can utilize these via SQL
        AdministrationService adminService = Context.getAdministrationService();
        adminService.purgeGlobalProperties(adminService.getGlobalPropertiesByPrefix(GP_COMPONENT_PREFIX));
        setGlobalProperty(GP_CONFIGURED_SITE, "");
        if (config != null) {
            for (String component : config.getEnabledComponents()) {
                setGlobalProperty(GP_COMPONENT_PREFIX + component, "true");
            }
            setGlobalProperty(GP_CONFIGURED_SITE, config.getSite());
        }
    }

    public User setupCommCareUser(){

        User user = null;
        String commCareUserName = Context.getRuntimeProperties().getProperty("commcare.username", null);
        String commCareUserPassword = Context.getRuntimeProperties().getProperty("commcare.password", null);
        if (StringUtils.isNotBlank(commCareUserName) && StringUtils.isNotBlank(commCareUserPassword)) {
            user = Context.getUserService().getUserByUsername(commCareUserName);
            if (user == null) {
                // create a new user if one does not already exist
                user = new User();
            }
            user.setUsername(commCareUserName);
            Person person = user.getPerson();
            if (person == null) {
                person = new Person();
                person.addName(new PersonName("CommCare", "", "Integration"));
                person.setGender("M");
                user.setPerson(person);
            }

            user.addRole(Context.getUserService().getRole("Authenticated"));
            user.addRole(Context.getUserService().getRole("Privilege Level: Full"));
            if (user.getId() == null ) {
                // create a new record
                user = Context.getUserService().createUser(user, commCareUserPassword);
            } else {
                // update an existing record
                user = Context.getUserService().saveUser(user);
            }
            person = user.getPerson();
            Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(person);
            if (providers == null || providers.size() < 1 ){
                // if the user does not have a Provider yet then create one
                Provider provider = new Provider();
                provider.setUuid(PihCoreConstants.COMMCARE_PROVIDER_UUID);
                provider.setPerson(person);
                Context.getProviderService().saveProvider(provider);
            }
        }

        return user;
    }

    protected void setGlobalProperty(String propertyName, String propertyValue) {
        AdministrationService administrationService = Context.getAdministrationService();
        GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
        if (gp == null) {
            gp = new GlobalProperty(propertyName);
        }
        gp.setPropertyValue(propertyValue);
        administrationService.saveGlobalProperty(gp);
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

    private void removeOldPrivileges() {
        UserService userService = Context.getUserService();
        Privilege privilege = userService.getPrivilege("App: appointmentschedulingui.scheduleAdmin");
        if (privilege != null) {
            userService.purgePrivilege(privilege);
        }
    }

}
