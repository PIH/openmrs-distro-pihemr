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

package org.openmrs.module.mirebalais.integration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.MirebalaisGlobalProperties;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.pacsintegration.PacsIntegrationGlobalProperties;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.validator.ValidateUtil;

@SkipBaseSetup
public class MirebalaisHospitalActivatorIT extends BaseModuleContextSensitiveTest {

    @Before
    public void beforeEachTest() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        executeDataSet("globalPropertiesTestDataset.xml");
        authenticate();
    }

    @Test
    public void testThatActivatorDoesAllSetup() throws Exception {
        MirebalaisHospitalActivator activator = new MirebalaisHospitalActivator();
        activator.started();

        verifyMetadataPackagesConfigured();
        verifyGlobalPropertiesConfigured();
        verifyPacsIntegrationGlobalPropertiesConfigured();
        verifyIdentifierSourcesConfigured();
    }

    private void verifyMetadataPackagesConfigured() throws Exception {

        // TODO rewrite this

        // this doesn't strictly belong here, but we include it as an extra sanity check on the MDS module
        for (Concept concept : Context.getConceptService().getAllConcepts()) {
            ValidateUtil.validate(concept);
        }
    }

    private void verifyGlobalPropertiesConfigured() throws Exception {
        Assert.assertEquals(new Integer(8443), MirebalaisGlobalProperties.MIRTH_ADMIN_PORT());
        Assert.assertEquals(new Integer(6661), MirebalaisGlobalProperties.MIRTH_INPUT_PORT());
        Assert.assertEquals("/opt/mirthconnect", MirebalaisGlobalProperties.MIRTH_DIRECTORY());
        Assert.assertEquals("127.0.0.1", MirebalaisGlobalProperties.MIRTH_IP_ADDRESS());
        Assert.assertEquals("mirth",MirebalaisGlobalProperties.MIRTH_USERNAME() );
        Assert.assertEquals("Mirth123",MirebalaisGlobalProperties.MIRTH_PASSWORD());
    }

    private void verifyPacsIntegrationGlobalPropertiesConfigured() throws Exception {
        Assert.assertEquals("admin", PacsIntegrationGlobalProperties.LISTENER_USERNAME());
        Assert.assertEquals("test", PacsIntegrationGlobalProperties.LISTENER_PASSWORD());
        Assert.assertEquals("7abcc666-7777-45e1-8c99-2b4f0c4f888a", PacsIntegrationGlobalProperties.RADIOLOGY_ORDER_TYPE_UUID());
    }

    private void verifyIdentifierSourcesConfigured() throws Exception {
        MirebalaisHospitalService service = Context.getService(MirebalaisHospitalService.class);
        IdentifierPool localZlIdentifierPool = service.getLocalZlIdentifierPool();
        RemoteIdentifierSource remoteZlIdentifierSource = service.getRemoteZlIdentifierSource();

        PatientIdentifierType zlIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(MirebalaisConstants.ZL_IDENTIFIER_TYPE_UUID);
        AutoGenerationOption autoGenerationOption = Context.getService(IdentifierSourceService.class).getAutoGenerationOption(zlIdentifierType);

        Assert.assertEquals(MirebalaisConstants.ZL_IDENTIFIER_TYPE_UUID, zlIdentifierType.getUuid());
        Assert.assertEquals(zlIdentifierType, autoGenerationOption.getIdentifierType());
        Assert.assertEquals(localZlIdentifierPool, autoGenerationOption.getSource());

        Assert.assertEquals(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID, localZlIdentifierPool.getUuid());
        Assert.assertEquals(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE, localZlIdentifierPool.getBatchSize());
        Assert.assertEquals(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE, localZlIdentifierPool.getMinPoolSize());

        Assert.assertEquals(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID, remoteZlIdentifierSource.getUuid());
        Assert.assertEquals(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_URL, remoteZlIdentifierSource.getUrl());
    }

}
