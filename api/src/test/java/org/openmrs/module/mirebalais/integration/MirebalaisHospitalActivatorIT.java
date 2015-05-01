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
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.module.mirebalais.RuntimeProperties;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalaismetadata.MetadataManager;
import org.openmrs.module.pihcore.deploy.bundle.PihMetadataBundle;
import org.openmrs.module.pihcore.metadata.PatientIdentifierTypes;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SkipBaseSetup
public class MirebalaisHospitalActivatorIT extends BaseModuleContextSensitiveTest {

    private RuntimeProperties customProperties;

    @Before
    public void beforeEachTest() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        executeDataSet("globalPropertiesTestDataset.xml");
        executeDataSet("serializedReportingDataset.xml");
        executeDataSet("fromMirebalaisMetadataModule.xml");
        authenticate();
		installRequiredMetadata();
        MirebalaisHospitalActivator activator = new MirebalaisHospitalActivator();
        activator.setTestMode(true);
        activator.contextRefreshed();
        activator.started();
        customProperties = new RuntimeProperties();
    }

	private void installRequiredMetadata() {
		System.setProperty(PihMetadataBundle.SYSTEM_PROPERTY_SKIP_METADATA_SHARING_PACKAGE_REFRESH, "true");
		MetadataManager manager = Context.getRegisteredComponents(MetadataManager.class).get(0);
		manager.refresh();
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
        SequentialIdentifierGenerator dossierSequenceGenerator = service.getDossierSequenceGenerator(MirebalaisConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID);

        PatientIdentifierType zlIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(PatientIdentifierTypes.ZL_EMR_ID.uuid());
        PatientIdentifierType dossierNumberIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(PatientIdentifierTypes.DOSSIER_NUMBER.uuid());

        AutoGenerationOption autoGenerationOption = Context.getService(IdentifierSourceService.class).getAutoGenerationOption(zlIdentifierType);

        assertEquals(PatientIdentifierTypes.ZL_EMR_ID.uuid(), zlIdentifierType.getUuid());
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
        assertEquals(new Integer(7), dossierSequenceGenerator.getMaxLength());
        assertEquals(new Integer(7), dossierSequenceGenerator.getMinLength());
        assertEquals("0123456789", dossierSequenceGenerator.getBaseCharacterSet());
        assertEquals("000001", dossierSequenceGenerator.getFirstIdentifierBase());
        assertEquals(MirebalaisConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID, dossierSequenceGenerator.getUuid());
        assertEquals(dossierNumberIdentifierType, dossierSequenceGenerator.getIdentifierType());
        assertEquals(2, Context.getService(IdentifierSourceService.class).getAutoGenerationOptions(dossierNumberIdentifierType).size());

        assertNotNull(Context.getService(ImportPatientFromWebService.class).getRemoteServers().get("lacolline"));
    }

}
