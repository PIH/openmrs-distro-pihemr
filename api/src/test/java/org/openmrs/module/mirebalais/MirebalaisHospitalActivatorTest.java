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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.pacsintegration.PacsIntegrationGlobalProperties;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.validator.ValidateUtil;

@SkipBaseSetup
public class MirebalaisHospitalActivatorTest extends BaseModuleContextSensitiveTest {

    @Before
    public void beforeEachTest() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        executeDataSet("globalPropertiesTestDataset.xml");
        authenticate();
    }

    @Test
    public void testMirebalaisHospitalActivatorStarted() throws Exception {

        int numConcepts = Context.getConceptService().getAllConcepts().size();
        MirebalaisHospitalActivator activator = new MirebalaisHospitalActivator();
        activator.started();        
        // confirm that new concept sources have been added
        Assert.assertTrue(Context.getConceptService().getAllConceptSources().size() > numConcepts);
        
        for (Concept concept : Context.getConceptService().getAllConcepts()) {
            ValidateUtil.validate(concept);
        }

    }

    @Test
    public void testMirebalaisGlobalPropertiesSetOnStartup() throws Exception {
        MirebalaisHospitalActivator activator = new MirebalaisHospitalActivator();
        activator.started();

        Assert.assertEquals(new Integer(8443), MirebalaisGlobalProperties.MIRTH_ADMIN_PORT());
        Assert.assertEquals(new Integer(6661), MirebalaisGlobalProperties.MIRTH_INPUT_PORT());
        Assert.assertEquals("/opt/mirthconnect", MirebalaisGlobalProperties.MIRTH_DIRECTORY());
        Assert.assertEquals("127.0.0.1", MirebalaisGlobalProperties.MIRTH_IP_ADDRESS());
        Assert.assertEquals("mirth",MirebalaisGlobalProperties.MIRTH_USERNAME() );
        Assert.assertEquals("Mirth123",MirebalaisGlobalProperties.MIRTH_PASSWORD());
    }

    @Test
    public void testPacsIntegrationGlobalPropertiesSetOnStartup() throws Exception {
        MirebalaisHospitalActivator activator = new MirebalaisHospitalActivator();
        activator.started();

        Assert.assertEquals("admin", PacsIntegrationGlobalProperties.LISTENER_USERNAME());
        Assert.assertEquals("test", PacsIntegrationGlobalProperties.LISTENER_PASSWORD());
        Assert.assertEquals("7abcc666-7777-45e1-8c99-2b4f0c4f888a", PacsIntegrationGlobalProperties.RADIOLOGY_ORDER_TYPE_UUID());
    }

}
