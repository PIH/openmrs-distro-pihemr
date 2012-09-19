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


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.module.patientregistration.PatientRegistrationGlobalProperties;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class MirebalaisHospitalActivator implements ModuleActivator {
	
	protected Log log = LogFactory.getLog(getClass());
	
	Map<String, String> currentMetadataVersions = new LinkedHashMap<String, String>();
	
	public MirebalaisHospitalActivator() {
		currentMetadataVersions.put("93fb4477-843f-4184-9486-1cb879a302da", "Mirebalais_Core_Metadata-1.zip");
		currentMetadataVersions.put("3fa49824-cf28-4f2a-bc06-1a26ae6abeca", "PIH_Haiti_Patient_Registration-1.zip");		
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
		installMetadataPackages();
		setupPatientRegistrationGlobalProperties();
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
     * Sets global property value or throws an exception if that global property does not already exist
     * @param propertyName
     * @param propertyValue 
     */
    private void setExistingGlobalProperty(String propertyName, String propertyValue){
    	AdministrationService administrationService = Context.getAdministrationService();
    	GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
    	if(gp==null){
    		throw new RuntimeException("global property " + propertyName + " does not exist");
    	}
    	gp.setPropertyValue(propertyValue);
    	administrationService.saveGlobalProperty(gp);
    	
    }
    
    public void setupPatientRegistrationGlobalProperties(){
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.SUPPORTED_TASKS, "patientRegistration|primaryCareReception|primaryCareVisit|retrospectiveEntry|patientLookup|reporting|viewDuplicates");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.SEARCH_CLASS, "org.openmrs.module.patientregistration.search.DefaultPatientRegistrationSearch");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.LABEL_PRINT_COUNT, "1");    	
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.PROVIDER_ROLES, "LacollineProvider");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.PROVIDER_IDENTIFIER_PERSON_ATTRIBUTE_TYPE, "Provider Identifier");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.PRIMARY_IDENTIFIER_TYPE, "ZL EMR ID");    	
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.URGENT_DIAGNOSIS_CONCEPT, "PIH: Haiti nationally urgent diseases");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.NOTIFY_DIAGNOSIS_CONCEPT, "PIH: Haiti nationally notifiable diseases");    
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.NON_CODED_DIAGNOSIS_CONCEPT, "PIH: ZL Primary care diagnosis non-coded");      	
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.NEONATAL_DISEASES_CONCEPT, "PIH: Haiti neonatal diseases");    	
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.PRIMARY_CARE_VISIT_ENCOUNTER_TYPE, "Primary care visit");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.CODED_DIAGNOSIS_CONCEPT, "PIH: ZL Primary care diagnosis");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.AGE_RESTRICTED_CONCEPT, "PIH: Haiti age restricted diseases");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.RECEIPT_NUMBER_CONCEPT, "PIH: Receipt number|en:Receipt Number|ht:Nimewo Resi a");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.PAYMENT_CONCEPT, "PIH: Patient payment status|en:Payment type|ht:Fason pou peye");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.PRIMARY_CARE_RECEPTION_ENCOUNTER_TYPE, "Primary Care Reception");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.PATIENT_REGISTRATION_ENCOUNTER_TYPE, "Patient Registration");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.NUMERO_DOSSIER, "Nimewo Dosye");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.ID_CARD_PERSON_ATTRIBUTE_TYPE, "Telephone Number");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.ID_CARD_LABEL_TEXT, "Zanmi Lasante Patient ID Card");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.ICD10_CONCEPT_SOURCE, "ICD-10");
    	setExistingGlobalProperty(PatientRegistrationGlobalProperties.BIRTH_YEAR_INTERVAL, "1");
    	
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
				throw new RuntimeException("Filename must match PackageNameWithNoSpaces-1.zip");
			Integer version = Integer.valueOf(matcher.group(1));
			
			ImportedPackage installed = Context.getService(MetadataSharingService.class).getImportedPackageByGroup(groupUuid);
			if (installed != null && installed.getVersion() >= version) {
				log.info("Metadata package " + filename + " is already installed with version " + installed.getVersion());
				return false;
			}
			
			if (getClass().getClassLoader().getResource(filename) == null) {
				throw new RuntimeException("Cannot find " + filename + " for group " + groupUuid + ". Make sure it's in api/src/main/resources");
			}
			
			PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
			metadataImporter.setImportConfig(ImportConfig.valueOf(ImportMode.PARENT_AND_CHILD));
			metadataImporter.loadSerializedPackageStream(getClass().getClassLoader().getResourceAsStream(filename));
			metadataImporter.importPackage();
			return true;
    	} catch (Exception ex) {
    		log.error("Failed to install metadata package " + filename, ex);
    		return false;
    	}
    }
    
}
