/*
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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.validator.ValidateUtil;

@SkipBaseSetup
public class MirebalaisHospitalActivatorTest extends BaseModuleContextSensitiveTest {

    @Before
    public void beforeEachTest() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        authenticate();
    }

    @Test
    public void testMirebalaisHospitalActivatorStarted() throws Exception {

        MirebalaisHospitalActivator activator = new MirebalaisHospitalActivator();
        activator.started();
        
        for (Map.Entry<String, String> ver : activator.getCurrentMetadataVersions().entrySet()) {
        	String groupUuid = ver.getKey();
        	String filename = ver.getValue();
        	Matcher matcher = Pattern.compile("\\w+-(\\d+).zip").matcher(filename);
        	matcher.matches();
			Integer version = Integer.valueOf(matcher.group(1));
			ImportedPackage installed = Context.getService(MetadataSharingService.class).getImportedPackageByGroup(groupUuid);
			Assert.assertTrue("Failed to install " + filename, installed != null && installed.getVersion() == version);
        }

        for (Concept concept : Context.getConceptService().getAllConcepts()) {
            ValidateUtil.validate(concept);
        }

    }

}
