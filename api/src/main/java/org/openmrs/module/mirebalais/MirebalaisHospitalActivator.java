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
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyImportUtil;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.patientregistration.PatientRegistrationGlobalProperties;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class MirebalaisHospitalActivator implements ModuleActivator {
	
	protected Log log = LogFactory.getLog(getClass());
	
	Map<String, String> currentMetadataVersions = new LinkedHashMap<String, String>();
	
	private String ADDRESS_HIERARCHY_CSV_FILE = "org/openmrs/module/mirebalais/addresshierarchy/haiti_address_hierarchy_entries.csv";
	
	private MirebalaisCustomProperties customProperties;
	
	public MirebalaisHospitalActivator() {
		// Note: the key of this map should be the *GROUP* uuid of the metadata sharing package, which you can
		// get either from the <groupUuid> element of header.xml, or the groupUuid http parameter while viewing the
		// package on the server you generated it on.
		// The value should be the filename as you downloaded it from the server you created it on.
		// In particular, you should keep the "-(versionNum)" in the filename, and update the string here
		// to match the downloaded version.
		currentMetadataVersions.put("32d52080-13fa-413e-a23e-6ff9a23c7a69", "HUM_Locations-1.zip");
		currentMetadataVersions.put("f12f5fb8-80a8-40d0-a20e-24af2642ce4c", "Roles_and_privileges-1.zip");
		currentMetadataVersions.put("f704dd02-ed65-46ba-b9b0-a5e728ce716b", "PIH_Haiti_Patient_Registration-4.zip");
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
		MirebalaisHospitalService service = Context.getService(MirebalaisHospitalService.class);
		IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
		
		installMetadataPackages();
		setupPatientRegistrationGlobalProperties();
		setupMirebalaisGlobalProperties();
		setupPacsIntegrationGlobalProperties();
		setupIdentifierGeneratorsIfNecessary(service, identifierSourceService);
		installMirthChannels();
		setupAddressHierarchy();
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
			metadataImporter.setImportConfig(ImportConfig.valueOf(ImportMode.PARENT_AND_CHILD));
			metadataImporter.loadSerializedPackageStream(getClass().getClassLoader().getResourceAsStream(filename));
			metadataImporter.importPackage();
			return true;
		}
		catch (Exception ex) {
			log.error("Failed to install metadata package " + filename, ex);
			return false;
		}
	}
	
	private void setupIdentifierGeneratorsIfNecessary(MirebalaisHospitalService service,
	        IdentifierSourceService identifierSourceService) {
		
		PatientIdentifierType zlIdentifierType = service.getZlIdentifierType();
		RemoteIdentifierSource remoteZlIdentifierSource = getOrCreateRemoteZlIdentifierSource(service, zlIdentifierType,
		    identifierSourceService);
		IdentifierPool localZlIdentifierPool = getOrCreateLocalZlIdentifierPool(service, zlIdentifierType,
		    remoteZlIdentifierSource, identifierSourceService);
		
		getOrCreateZlIdentifierAutoGenerationOptions(zlIdentifierType, localZlIdentifierPool, identifierSourceService);
	}
	
	void getOrCreateZlIdentifierAutoGenerationOptions(PatientIdentifierType zlIdentifierType,
	        IdentifierPool localZlIdentifierPool, IdentifierSourceService identifierSourceService) {
		AutoGenerationOption autoGen = identifierSourceService.getAutoGenerationOption(zlIdentifierType);
		if (autoGen == null) {
			autoGen = buildZlIdentifierAutoGenerationOptions(zlIdentifierType, localZlIdentifierPool);
			identifierSourceService.saveAutoGenerationOption(autoGen);
		}
	}
	
	IdentifierPool getOrCreateLocalZlIdentifierPool(MirebalaisHospitalService service,
	        PatientIdentifierType zlIdentifierType, RemoteIdentifierSource remoteZlIdentifierSource,
	        IdentifierSourceService identifierSourceService) {
		IdentifierPool localZlIdentifierPool;
		try {
			localZlIdentifierPool = service.getLocalZlIdentifierPool();
		}
		catch (IllegalStateException ex) {
			localZlIdentifierPool = buildLocalZlIdentifierPool(zlIdentifierType, remoteZlIdentifierSource);
			identifierSourceService.saveIdentifierSource(localZlIdentifierPool);
		}
		return localZlIdentifierPool;
	}
	
	RemoteIdentifierSource getOrCreateRemoteZlIdentifierSource(MirebalaisHospitalService service,
	        PatientIdentifierType zlIdentifierType, IdentifierSourceService identifierSourceService) {
		RemoteIdentifierSource remoteZlIdentifierSource;
		
		try {
			remoteZlIdentifierSource = service.getRemoteZlIdentifierSource();
			updateInformationFromPropertiesFile(remoteZlIdentifierSource);
		}
		catch (IllegalStateException ex) {
			remoteZlIdentifierSource = buildRemoteZlIdentifierSource(zlIdentifierType);
		}
		
		identifierSourceService.saveIdentifierSource(remoteZlIdentifierSource);
		return remoteZlIdentifierSource;
	}
	
	private AutoGenerationOption buildZlIdentifierAutoGenerationOptions(PatientIdentifierType zlIdentifierType,
	        IdentifierPool localZlIdentifierPool) {
		AutoGenerationOption autoGen = new AutoGenerationOption();
		autoGen.setIdentifierType(zlIdentifierType);
		autoGen.setSource(localZlIdentifierPool);
		autoGen.setManualEntryEnabled(false);
		autoGen.setAutomaticGenerationEnabled(true);
		return autoGen;
	}
	
	private IdentifierPool buildLocalZlIdentifierPool(PatientIdentifierType zlIdentifierType,
	        RemoteIdentifierSource remoteZlIdentifierSource) {
		IdentifierPool localPool = new IdentifierPool();
		localPool.setName("Local Pool of ZL Identifiers");
		localPool.setUuid(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID);
		localPool.setSource(remoteZlIdentifierSource);
		localPool.setIdentifierType(zlIdentifierType);
		localPool.setMinPoolSize(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE);
		localPool.setBatchSize(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE);
		localPool.setSequential(true);
		return localPool;
	}
	
	private RemoteIdentifierSource buildRemoteZlIdentifierSource(PatientIdentifierType zlIdentifierType) {
		RemoteIdentifierSource remoteZlIdentifierSource = new RemoteIdentifierSource();
		remoteZlIdentifierSource.setName("Remote Source for ZL Identifiers");
		remoteZlIdentifierSource.setUuid(REMOTE_ZL_IDENTIFIER_SOURCE_UUID);
		remoteZlIdentifierSource.setIdentifierType(zlIdentifierType);
		updateInformationFromPropertiesFile(remoteZlIdentifierSource);
		return remoteZlIdentifierSource;
	}
	
	private void updateInformationFromPropertiesFile(RemoteIdentifierSource remoteZlIdentifierSource) {
		remoteZlIdentifierSource.setUrl(customProperties.getRemoteZlIdentifierSourceUrl());
		remoteZlIdentifierSource.setUser(customProperties.getRemoteZlIdentifierSourceUsername());
		remoteZlIdentifierSource.setPassword(customProperties.getRemoteZlIdentifierSourcePassword());
	}
	
	private boolean installMirthChannels() {
		
		try {
			// first copy the channel files to a tmp directory
			File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory("mirth/tmp");
			
			Map<String, String> channels = new HashMap<String, String>();
			channels.put("OpenMRS To Pacs", "openMRSToPacsChannel");
			
			for (String channel : channels.values()) {
				InputStream channelStream = OpenmrsClassLoader.getInstance().getResourceAsStream(
				    "org/openmrs/module/mirebalais/mirth/" + channel + ".xml");
				File channelFile = new File(dir, channel + ".xml");
				FileUtils.writeStringToFile(channelFile, IOUtils.toString(channelStream));
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
			for (Map.Entry channel : channels.entrySet()) {
				OutputStream out = mirthShell.getOutputStream();
				out.write(("import \"" + dir.getAbsolutePath() + "/" + channel.getValue() + ".xml\" force\n").getBytes());
				out.write(("channel deploy \"" + channel.getKey() + "\"\n").getBytes());
				out.close();
			}
			
			return true;
		}
		catch (Exception ex) {
			log.error("Failed to install Mirth channels", ex);
			return false;
		}
		
	}
	
	/**
	 * Sets global property value or throws an exception if that global property does not already exist
	 * @param propertyName
	 * @param propertyValue
	 */
	private void setExistingGlobalProperty(String propertyName, String propertyValue) {
		AdministrationService administrationService = Context.getAdministrationService();
		GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
		if (gp == null) {
			throw new RuntimeException("global property " + propertyName + " does not exist");
		}
		gp.setPropertyValue(propertyValue);
		administrationService.saveGlobalProperty(gp);
	}
	
	private void setupMirebalaisGlobalProperties() {
		setExistingGlobalProperty("mirebalais.mirthUsername", "mirth");
		setExistingGlobalProperty("mirebalais.mirthPassword", "Mirth123");
		setExistingGlobalProperty("mirebalais.mirthDirectory", "/opt/mirthconnect");
		setExistingGlobalProperty("mirebalais.mirthIpAddress", "127.0.0.1");
		setExistingGlobalProperty("mirebalais.mirthAdminPort", "8443");
	}
	
	private void setupPacsIntegrationGlobalProperties() {
		setExistingGlobalProperty("pacsintegration.listenerUsername", "admin");
		setExistingGlobalProperty("pacsintegration.listenerPassword", "Admin123");
		setExistingGlobalProperty("pacsintegration.radiologyOrderTypeUuid", "84ce45a8-5e7c-48f7-a581-bb1d17d63a62");
	}
	
	private void setupPatientRegistrationGlobalProperties() {
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.SUPPORTED_TASKS,
		    "patientRegistration|primaryCareReception|primaryCareVisit|retrospectiveEntry|patientLookup|reporting|viewDuplicates");
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
		    "Primary care visit");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.CODED_DIAGNOSIS_CONCEPT,
		    "PIH: ZL Primary care diagnosis");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.AGE_RESTRICTED_CONCEPT,
		    "PIH: Haiti age restricted diseases");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.RECEIPT_NUMBER_CONCEPT,
		    "PIH: Receipt number|en:Receipt Number|ht:Nimewo Resi a");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PAYMENT_CONCEPT,
		    "PIH: Patient payment status|en:Payment type|ht:Fason pou peye");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PRIMARY_CARE_RECEPTION_ENCOUNTER_TYPE,
		    "Primary Care Reception");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PATIENT_REGISTRATION_ENCOUNTER_TYPE,
		    "Patient Registration");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.NUMERO_DOSSIER, "Nimewo Dosye");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.ID_CARD_PERSON_ATTRIBUTE_TYPE, "Telephone Number");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.ID_CARD_LABEL_TEXT, "Zanmi Lasante Patient ID Card");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.ICD10_CONCEPT_SOURCE, "ICD-10");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.BIRTH_YEAR_INTERVAL, "1");
		
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
