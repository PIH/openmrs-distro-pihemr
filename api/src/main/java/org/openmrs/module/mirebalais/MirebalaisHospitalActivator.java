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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyImportUtil;
import org.openmrs.module.appframework.AppDescriptor;
import org.openmrs.module.appframework.api.AppFrameworkService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.namephonetics.NamePhoneticsConstants;
import org.openmrs.module.pacsintegration.PacsIntegrationGlobalProperties;
import org.openmrs.module.patientregistration.PatientRegistrationGlobalProperties;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class MirebalaisHospitalActivator implements ModuleActivator {
	
	protected Log log = LogFactory.getLog(getClass());
	
	Map<String, String> currentMetadataVersions = new LinkedHashMap<String, String>();
	
	private String ADDRESS_HIERARCHY_CSV_FILE = "org/openmrs/module/mirebalais/addresshierarchy/haiti_address_hierarchy_entries.csv";
	
	private MirebalaisCustomProperties customProperties;
	
	private ConfigureIdGenerators configureIdGenerators;
	
	public MirebalaisHospitalActivator() {
		// Note: the key of this map should be the *GROUP* uuid of the metadata sharing package, which you can
		// get either from the <groupUuid> element of header.xml, or the groupUuid http parameter while viewing the
		// package on the server you generated it on.
		// The value should be the filename as you downloaded it from the server you created it on.
		// In particular, you should keep the "-(versionNum)" in the filename, and update the string here
		// to match the downloaded version.
		currentMetadataVersions.put("32d52080-13fa-413e-a23e-6ff9a23c7a69", "Mirebalais_Hospital_locations-7.zip");
		currentMetadataVersions.put("f12f5fb8-80a8-40d0-a20e-24af2642ce4c", "Roles_and_privileges-7.zip");
		currentMetadataVersions.put("fa25ad0c-66cc-4715-8464-58570f7b5132", "PIH_Haiti_Patient_Registration-16.zip");
		currentMetadataVersions.put("be592ba7-1fa2-4a71-a147-3c828e67e901", "PACS_Integration-1.zip");
		currentMetadataVersions.put("e666ee52-c381-461f-b59d-7528da25473c", "AdministrativeEncounterRole-1.zip");
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
			
			installMetadataPackages();
			setupNamePhoneticsGlobalProperties();
			setupPatientRegistrationGlobalProperties();
			setupEmrGlobalProperties();
			setupMirebalaisGlobalProperties();
			setupPacsIntegrationGlobalProperties();
			setupIdentifierGeneratorsIfNecessary(service, identifierSourceService);
			installMirthChannels();
			setupAddressHierarchy();
			sortApps();
		}
		catch (Exception e) {
			Module mod = ModuleFactory.getModuleById(MirebalaisConstants.MIREBALAIS_MODULE_ID);
			ModuleFactory.stopModule(mod);
			throw new RuntimeException("failed to setup the required modules", e);
		}
		log.info("Mirebalais Hospital Module started");
	}
	
	private void sortApps() {
		AppFrameworkService appFrameworkService = Context.getService(AppFrameworkService.class);
		Map<String, Integer> appsOrdering = getAppsOrderingMap();
		
		List<AppDescriptor> allApps = appFrameworkService.getAllApps();
		for (AppDescriptor app : allApps) {
			app.setOrder(appsOrdering.get(app.getId()));
		}
		appFrameworkService.setAllApps(allApps);
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
		configureIdGenerators.autoGenerationOptions(sequentialIdentifierGenerator);
	}
	
	private void createPatientIdGenerator(MirebalaisHospitalService service) {
		PatientIdentifierType zlIdentifierType = service.getZlIdentifierType();
		
		RemoteIdentifierSource remoteZlIdentifierSource = configureIdGenerators.remoteZlIdentifierSource(zlIdentifierType);
		IdentifierPool localZlIdentifierPool = configureIdGenerators.localZlIdentifierSource(remoteZlIdentifierSource);
		configureIdGenerators.autoGenerationOptions(localZlIdentifierPool);
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
	
	private void installMetadataPackages() {
		for (Map.Entry<String, String> e : currentMetadataVersions.entrySet()) {
			installMetadataPackageIfNecessary(e.getKey(), e.getValue());
		}
	}
	
	/**
	 * Checks whether the given version of the MDS package has been installed yet, and if not, install it
	 *
	 * @param groupUuid
	 * @param filename should end in "-${versionNumber}.zip"
	 * @return whether any changes were made to the db
	 * @throws IOException
	 */
	private boolean installMetadataPackageIfNecessary(String groupUuid, String filename) {
		try {
			Matcher matcher = Pattern.compile("\\w+-(\\d+).zip").matcher(filename);
			if (!matcher.matches())
				throw new RuntimeException("Filename must match PackageNameWithNoSpaces-v1.zip");
			Integer version = Integer.valueOf(matcher.group(1));
			
			ImportedPackage installed = Context.getService(MetadataSharingService.class)
			        .getImportedPackageByGroup(groupUuid);
			if (installed != null && installed.getVersion() >= version) {
				log.info("Metadata package " + filename + " is already installed with version " + installed.getVersion());
				return false;
			}
			
			if (getClass().getClassLoader().getResource(filename) == null) {
				throw new RuntimeException("Cannot find " + filename + " for group " + groupUuid
				        + ". Make sure it's in api/src/main/resources");
			}
			
			PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
			metadataImporter.setImportConfig(ImportConfig.valueOf(ImportMode.MIRROR));
			metadataImporter.loadSerializedPackageStream(getClass().getClassLoader().getResourceAsStream(filename));
			metadataImporter.importPackage();
			return true;
		}
		catch (Exception ex) {
			log.error("Failed to install metadata package " + filename, ex);
			return false;
		}
	}
	
	private boolean installMirthChannels() {
		
		try {
			// first copy the channel files to a tmp directory
			File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory("mirth/tmp");
			
			Map<String, String> channels = new HashMap<String, String>();
			channels.put("Read HL7 From OpenMRS Database", "readHL7FromOpenmrsDatabaseChannel");
			channels.put("Send HL7 To Pacs", "sendHL7ToPacsChannel");
			
			for (String channel : channels.values()) {
				// load the channel xml file
				InputStream channelStream = OpenmrsClassLoader.getInstance().getResourceAsStream(
				    "org/openmrs/module/mirebalais/mirth/" + channel + ".xml");
				File channelFile = new File(dir, channel + ".xml");
				String channelString = IOUtils.toString(channelStream);
				
				// do a search and replace to add the appropriate the mysql username and password from runtime properties
				channelString = channelString.replaceAll("\\$\\{mysqlUsername\\}", customProperties.getMirthMysqlUsername());
				channelString = channelString.replaceAll("\\$\\{mysqlPassword\\}", customProperties.getMirthMysqlPassword());
				
				// write the channel out to a tmp file
				FileUtils.writeStringToFile(channelFile, channelString);
				IOUtils.closeQuietly(channelStream);
			}
			
			// now call up the Mirth shell
			String[] commands = new String[] {
			        "java",
			        "-classpath",
			        MirebalaisGlobalProperties.MIRTH_DIRECTORY() + "/*:" + MirebalaisGlobalProperties.MIRTH_DIRECTORY()
			                + "/cli-lib/*",
			        "com.mirth.connect.cli.launcher.CommandLineLauncher",
			        "-a",
			        "https://" + MirebalaisGlobalProperties.MIRTH_IP_ADDRESS() + ":"
			                + MirebalaisGlobalProperties.MIRTH_ADMIN_PORT(), "-u",
			        MirebalaisGlobalProperties.MIRTH_USERNAME(), "-p", MirebalaisGlobalProperties.MIRTH_PASSWORD(), "-v",
			        "0.0.0" };
			Process mirthShell = Runtime.getRuntime().exec(commands);
			
			// TODO: figure out what to do to verify that this succeeds
			
			// deploy the channels
			
			OutputStream out = mirthShell.getOutputStream();
			for (Map.Entry channel : channels.entrySet()) {
				out.write("channel stop *\n".getBytes()); // stop all channels
				out.write(("import \"" + dir.getAbsolutePath() + "/" + channel.getValue() + ".xml\" force\n").getBytes());
				out.write(("channel deploy \"" + channel.getKey() + "\"\n").getBytes());
				out.write("channel start *\n".getBytes()); // restart all channels
			}
			out.close();
			
			return true;
		}
		catch (Exception ex) {
			throw new RuntimeException("Failed to install Mirth channels", ex);
		}
		
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
	 * Sets global property value or creates global property if needed
	 * @param propertyName
	 * @param propertyValue
	 */
	private void setOrCreateGlobalProperty(String propertyName, String propertyValue) {
		AdministrationService administrationService = Context.getAdministrationService();
		GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
		if (gp == null) {
			gp = new GlobalProperty();
			gp.setProperty(propertyName);
		}
		gp.setPropertyValue(propertyValue);
		administrationService.saveGlobalProperty(gp);
	}
	
	private void setupMirebalaisGlobalProperties() {
		setExistingGlobalProperty(
		    "layout.address.format",
		    "<org.openmrs.layout.web.address.AddressTemplate><nameMappings class=\"properties\"><property name=\"country\" value=\"mirebalais.address.country\"/><property name=\"stateProvince\" value=\"mirebalais.address.stateProvince\"/><property name=\"cityVillage\" value=\"mirebalais.address.cityVillage\"/><property name=\"address3\" value=\"mirebalais.address.neighborhoodCell\"/><property name=\"address1\" value=\"mirebalais.address.address1\"/><property name=\"address2\" value=\"mirebalais.address.address2\"/></nameMappings><sizeMappings class=\"properties\"><property name=\"country\" value=\"40\"/><property name=\"stateProvince\" value=\"40\"/><property name=\"cityVillage\" value=\"40\"/><property name=\"address3\" value=\"60\"/><property name=\"address1\" value=\"60\"/><property name=\"address2\" value=\"60\"/></sizeMappings><elementDefaults class=\"properties\"><property name=\"country\" value=\"Haiti\"/></elementDefaults><lineByLineFormat><string>address2</string><string>address1</string><string>address3 cityVillage</string><string>stateProvince country</string></lineByLineFormat></org.openmrs.layout.web.address.AddressTemplate>");
		
		setOrCreateGlobalProperty("mirebalais.mirthUsername", "mirth");
		setOrCreateGlobalProperty("mirebalais.mirthPassword", "Mirth123");
		setOrCreateGlobalProperty("mirebalais.mirthDirectory", "/opt/mirthconnect");
		setOrCreateGlobalProperty("mirebalais.mirthIpAddress", "127.0.0.1");
		setOrCreateGlobalProperty("mirebalais.mirthAdminPort", "8443");
	}
	
	private void setupPacsIntegrationGlobalProperties() {
		setExistingGlobalProperty(PacsIntegrationGlobalProperties.LISTENER_USERNAME, "admin");
		setExistingGlobalProperty(PacsIntegrationGlobalProperties.LISTENER_PASSWORD, "Admin123");
		setExistingGlobalProperty(PacsIntegrationGlobalProperties.RADIOLOGY_ORDER_TYPE_UUID,
		    "13116a48-15f5-102d-96e4-000c29c2a5d7");
		setExistingGlobalProperty(PacsIntegrationGlobalProperties.PATIENT_IDENTIFIER_TYPE_UUID,
		    "a541af1e-105c-40bf-b345-ba1fd6a59b85");
		setExistingGlobalProperty(PacsIntegrationGlobalProperties.DEFAULT_LOCALE, "en");
		setExistingGlobalProperty(PacsIntegrationGlobalProperties.SENDING_FACILITY, "Mirebalais");
		setExistingGlobalProperty(PacsIntegrationGlobalProperties.PROCEDURE_CODE_CONCEPT_SOURCE_UUID,
		    "2889f378-f287-40a5-ac9c-ce77ee963ed7");
	}
	
	private void setupEmrGlobalProperties() {
		// TODO create an encounter type to represent placing orders in our MDS package
		setExistingGlobalProperty(EmrConstants.GP_PLACE_ORDERS_ENCOUNTER_TYPE, "1373cf95-06e8-468b-a3da-360ac1cf026d");
		
		// TODO make sure we have the order type correct, and created via a MDS package
		setExistingGlobalProperty(EmrConstants.GP_TEST_ORDER_TYPE, "13116a48-15f5-102d-96e4-000c29c2a5d7");
		
		// TODO add a Clinician encounter role to our MDS packages
		setExistingGlobalProperty(EmrConstants.GP_CLINICIAN_ENCOUNTER_ROLE, "a0b03050-c99b-11e0-9572-0800200c9a66");
		
		// check_in clerk encounter role is set to Oupatient Application User Role
		setExistingGlobalProperty(EmrConstants.GP_CHECK_IN_CLERK_ENCOUNTER_ROLE, "cbfe0b9d-9923-404c-941b-f048adc8cdc0");
		
		// paper record location = Mirebalais
		setExistingGlobalProperty(EmrConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE, "e66645eb-03a8-4991-b4ce-e87318e37566");
		
		setExistingGlobalProperty(EmrConstants.GP_XRAY_ORDERABLES_CONCEPT, "35c24af8-6d60-4189-95c6-7e91e421d11f");
		setExistingGlobalProperty(EmrConstants.GP_CT_SCAN_ORDERABLES_CONCEPT, "381d653b-a6b7-438a-b9f0-5034b5272def");
		setExistingGlobalProperty(EmrConstants.GP_ULTRASOUND_ORDERABLES_CONCEPT, "a400b7e5-6b2f-404f-84d0-6eb2ca611a7d");
		setExistingGlobalProperty(EmrConstants.GP_AT_FACILITY_VISIT_TYPE, "f01c54cb-2225-471a-9cd5-d348552c337c");
		setExistingGlobalProperty(EmrConstants.GP_CHECK_IN_ENCOUNTER_TYPE, "55a0d3ea-a4d7-4e88-8f01-5aceb2d3c61b");
		setExistingGlobalProperty(EmrConstants.PRIMARY_IDENTIFIER_TYPE, "ZL EMR ID");

        setOrCreateGlobalProperty(EmrConstants.PAYMENT_AMOUNT_CONCEPT_UUID, "5d1bc5de-6a35-4195-8631-7322941fe528");
        setOrCreateGlobalProperty(EmrConstants.PAYMENT_REASON_CONCEPT_UUID, "36ba7721-fae0-4da4-aef2-7e476cc04bdf");
        setOrCreateGlobalProperty(EmrConstants.PAYMENT_RECEIPT_NUMBER_CONCEPT_UUID, "20438dc7-c5b4-4d9c-8480-e888f4795123");
		
	}
	
	private void setupNamePhoneticsGlobalProperties() {
		setExistingGlobalProperty(NamePhoneticsConstants.GIVEN_NAME_GLOBAL_PROPERTY, "Double Metaphone Alternate");
		setExistingGlobalProperty(NamePhoneticsConstants.MIDDLE_NAME_GLOBAL_PROPERTY, "Double Metaphone Alternate");
		setExistingGlobalProperty(NamePhoneticsConstants.FAMILY_NAME_GLOBAL_PROPERTY, "Double Metaphone Alternate");
		setExistingGlobalProperty(NamePhoneticsConstants.FAMILY_NAME2_GLOBAL_PROPERTY, "Double Metaphone Alternate");
	}
	
	private void setupPatientRegistrationGlobalProperties() {
		setExistingGlobalProperty(
		    PatientRegistrationGlobalProperties.SUPPORTED_TASKS,
		    "patientRegistration|primaryCareReception|primaryCareVisit|edCheckIn|retrospectiveEntry|patientLookup|reporting|viewDuplicates");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.SEARCH_CLASS,
		    "org.openmrs.module.patientregistration.search.DefaultPatientRegistrationSearch");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.LABEL_PRINT_COUNT, "1");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PROVIDER_ROLES, "LacollineProvider");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PROVIDER_IDENTIFIER_PERSON_ATTRIBUTE_TYPE,
		    "Provider Identifier");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PRIMARY_IDENTIFIER_TYPE, "ZL EMR ID");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.URGENT_DIAGNOSIS_CONCEPT,
		    "PIH: Haiti nationally urgent diseases");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.NOTIFY_DIAGNOSIS_CONCEPT,
		    "PIH: Haiti nationally notifiable diseases");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.NON_CODED_DIAGNOSIS_CONCEPT,
		    "PIH: ZL Primary care diagnosis non-coded");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.NEONATAL_DISEASES_CONCEPT,
		    "PIH: Haiti neonatal diseases");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PRIMARY_CARE_VISIT_ENCOUNTER_TYPE,
		    "Primary Care Visit");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.CODED_DIAGNOSIS_CONCEPT,
		    "PIH: ZL Primary care diagnosis");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.AGE_RESTRICTED_CONCEPT,
		    "PIH: Haiti age restricted diseases");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.RECEIPT_NUMBER_CONCEPT,
		    "PIH: Receipt number|en:Receipt Number|ht:Nimewo Resi a");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PAYMENT_AMOUNT_CONCEPT,
		    "PIH: Payment amount|en:Payment amount|ht:TODO TRANSLATE");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.VISIT_REASON_CONCEPT,
		    "PIH: Reason for HUM visit|en:Visit reason|ht:TODO TRANSLATE");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PRIMARY_CARE_RECEPTION_ENCOUNTER_TYPE, "Check-in");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PATIENT_REGISTRATION_ENCOUNTER_TYPE,
		    "Patient Registration");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.NUMERO_DOSSIER, "Nimewo Dosye");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.ID_CARD_PERSON_ATTRIBUTE_TYPE, "Telephone Number");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PATIENT_VIEWING_ATTRIBUTE_TYPES, "Telephone Number");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.ID_CARD_LABEL_TEXT, "Zanmi Lasante Patient ID Card");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.ICD10_CONCEPT_SOURCE, "ICD-10");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.BIRTH_YEAR_INTERVAL, "1");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.MEDICAL_RECORD_LOCATION_TAG,
		    "71c99f93-bc0c-4a44-b573-a7ac096ff636");
		
	}
	
	private void setupAddressHierarchy() {
		AddressHierarchyService ahService = Context.getService(AddressHierarchyService.class);
		
		// first check to see if we need to configure the address hierarchy levels
		int numberOfLevels = ahService.getAddressHierarchyLevelsCount();
		
		// if not 0 or 6 levels, we are in a weird state we can't recover from
		if (numberOfLevels != 0 && numberOfLevels != 6) {
			throw new RuntimeException("Unable to configure address hierarchy as it is currently misconfigured with "
			        + numberOfLevels + "levels");
		}
		
		// add the address hierarchy levels & entries if they don't exist, otherwise verify that they are correct
		if (numberOfLevels == 0) {
			AddressHierarchyLevel country = new AddressHierarchyLevel();
			country.setAddressField(AddressField.COUNTRY);
			ahService.saveAddressHierarchyLevel(country);
			
			AddressHierarchyLevel stateProvince = new AddressHierarchyLevel();
			stateProvince.setAddressField(AddressField.STATE_PROVINCE);
			stateProvince.setParent(country);
			ahService.saveAddressHierarchyLevel(stateProvince);
			
			AddressHierarchyLevel cityVillage = new AddressHierarchyLevel();
			cityVillage.setAddressField(AddressField.CITY_VILLAGE);
			cityVillage.setParent(stateProvince);
			ahService.saveAddressHierarchyLevel(cityVillage);
			
			AddressHierarchyLevel address3 = new AddressHierarchyLevel();
			address3.setAddressField(AddressField.ADDRESS_3);
			address3.setParent(cityVillage);
			ahService.saveAddressHierarchyLevel(address3);
			
			AddressHierarchyLevel address1 = new AddressHierarchyLevel();
			address1.setAddressField(AddressField.ADDRESS_1);
			address1.setParent(address3);
			ahService.saveAddressHierarchyLevel(address1);
			
			AddressHierarchyLevel address2 = new AddressHierarchyLevel();
			address2.setAddressField(AddressField.ADDRESS_2);
			address2.setParent(address1);
			ahService.saveAddressHierarchyLevel(address2);
			
			// load in the csv file
			InputStream file = getClass().getClassLoader().getResourceAsStream(ADDRESS_HIERARCHY_CSV_FILE);
			AddressHierarchyImportUtil.importAddressHierarchyFile(file, "\\|");
		}
		// at least verify that the right levels exist
		// TODO: perhaps do more validation here?
		else {
			AddressField[] fields = { AddressField.COUNTRY, AddressField.STATE_PROVINCE, AddressField.CITY_VILLAGE,
			        AddressField.ADDRESS_3, AddressField.ADDRESS_1, AddressField.ADDRESS_2 };
			int i = 0;
			
			for (AddressHierarchyLevel level : ahService.getOrderedAddressHierarchyLevels(true)) {
				if (level.getAddressField() != fields[i]) {
					throw new RuntimeException("Address field " + i + " improperly configured: is "
					        + level.getAddressField() + " but should be " + fields[i]);
				}
				i++;
				
			}
		}
	}
	
	public Map<String, String> getCurrentMetadataVersions() {
		return currentMetadataVersions;
	}
	
	void setCustomProperties(MirebalaisCustomProperties customProperties) {
		this.customProperties = customProperties;
	}
}
