package org.openmrs.module.mirebalais.integration;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.importpatientfromws.api.ImportPatientFromWebService;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.MirebalaisCustomProperties;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SkipBaseSetup
public class MirebalaisHospitalActivatorIT extends BaseModuleContextSensitiveTest {

    private MirebalaisHospitalActivator activator;

    private MirebalaisCustomProperties customProperties;

    @Before
    public void beforeEachTest() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        executeDataSet("globalPropertiesTestDataset.xml");
        executeDataSet("serializedReportingDataset.xml");
        executeDataSet("fromMirebalaisMetadataModule.xml");
        authenticate();
        activator = new MirebalaisHospitalActivator();
        activator.started();
        customProperties = new MirebalaisCustomProperties();
    }

    @AfterClass
    public static void tearDown() {
        runtimeProperties = null;
    }

    @Test
    @DirtiesContext
    public void testThatActivatorDoesAllSetup() throws Exception {
        MirebalaisHospitalService service = Context.getService(MirebalaisHospitalService.class);


        IdentifierPool localZlIdentifierPool = service.getLocalZlIdentifierPool();
        RemoteIdentifierSource remoteZlIdentifierSource = service.getRemoteZlIdentifierSource();
        SequentialIdentifierGenerator dossierSequenceGenerator = service.getDossierSequenceGenerator();

        PatientIdentifierType zlIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(
                MirebalaisConstants.ZL_IDENTIFIER_TYPE_UUID);

        PatientIdentifierType dossierNumberIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(
                MirebalaisConstants.DOSSIER_NUMBER_IDENTIFIER_TYPE_UUID);

        AutoGenerationOption autoGenerationOption = Context.getService(IdentifierSourceService.class)
                .getAutoGenerationOption(zlIdentifierType);

        assertEquals(MirebalaisConstants.ZL_IDENTIFIER_TYPE_UUID, zlIdentifierType.getUuid());
        assertEquals(zlIdentifierType, autoGenerationOption.getIdentifierType());
        assertEquals(localZlIdentifierPool, autoGenerationOption.getSource());

        assertEquals(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID, localZlIdentifierPool.getUuid());
        assertEquals(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE, localZlIdentifierPool.getBatchSize());
        assertEquals(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE, localZlIdentifierPool.getMinPoolSize());

        assertEquals(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID, remoteZlIdentifierSource.getUuid());
        assertEquals(customProperties.getRemoteZlIdentifierSourceUrl(), remoteZlIdentifierSource.getUrl());
        assertEquals(customProperties.getRemoteZlIdentifierSourceUsername(), remoteZlIdentifierSource.getUser());
        assertEquals(customProperties.getRemoteZlIdentifierSourcePassword(), remoteZlIdentifierSource.getPassword());

        assertEquals("A", dossierSequenceGenerator.getPrefix());
        assertEquals(new Integer(7), dossierSequenceGenerator.getLength());
        assertEquals("0123456789", dossierSequenceGenerator.getBaseCharacterSet());
        assertEquals("000001", dossierSequenceGenerator.getFirstIdentifierBase());
        assertEquals(MirebalaisConstants.DOSSIER_NUMBER_ZL_IDENTIFIER_SOURCE_UUID, dossierSequenceGenerator.getUuid());
        assertEquals(dossierNumberIdentifierType, dossierSequenceGenerator.getIdentifierType());
        assertTrue(Context.getService(IdentifierSourceService.class).getAutoGenerationOption(dossierNumberIdentifierType).isManualEntryEnabled());

        assertNotNull(Context.getService(ImportPatientFromWebService.class).getRemoteServers().get("lacolline"));
    }

}
