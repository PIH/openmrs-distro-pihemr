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
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
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
        installMirthChannels();
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
	
    /**
     * @return the currentMetadataVersions
     */
    public Map<String, String> getCurrentMetadataVersions() {
	    return currentMetadataVersions;
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

    private boolean installMirthChannels() {

        try {
            // first copy the channel files to a tmp directory
            File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory("mirth/tmp");

            Map<String,String> channels = new HashMap<String,String>();
            channels.put("OpenMRS To Pacs", "openMRSToPacsChannel");

            for (String channel : channels.values()) {
                InputStream channelStream= OpenmrsClassLoader.getInstance().getResourceAsStream("org/openmrs/module/mirebalais/mirth/" + channel + ".xml");
                File channelFile = new File(dir, channel + ".xml");
                FileUtils.writeStringToFile(channelFile, IOUtils.toString(channelStream));
                IOUtils.closeQuietly(channelStream);
            }

            // now call up the Mirth shell
            String[] commands = new String[] {"java", "-classpath", MirebalaisGlobalProperties.MIRTH_DIRECTORY()+ "/*:" + MirebalaisGlobalProperties.MIRTH_DIRECTORY() + "/cli-lib/*",
                    "com.mirth.connect.cli.launcher.CommandLineLauncher",
                    "-a", "https://" + MirebalaisGlobalProperties.MIRTH_IP_ADDRESS() + ":" + MirebalaisGlobalProperties.MIRTH_ADMIN_PORT(),
                    "-u", MirebalaisGlobalProperties.MIRTH_USERNAME(), "-p", MirebalaisGlobalProperties.MIRTH_PASSWORD(), "-v", "0.0.0"};
            Process mirthShell = Runtime.getRuntime().exec(commands);

            // TODO: figure out what to do to verify that this succeeds

            // deploy the channels
            for (Map.Entry channel : channels.entrySet()) {
                OutputStream out = mirthShell.getOutputStream();
                out.write(("import \"" +  dir.getAbsolutePath() + "/" + channel.getValue() + ".xml\" force\n").getBytes());
                out.write(("channel deploy \"" + channel.getKey()  + "\"\n").getBytes());
                out.close();
            }

            return true;
        }
        catch (Exception ex) {
            log.error("Failed to install Mirth channels", ex);
            return false;
        }

    }
    
}
