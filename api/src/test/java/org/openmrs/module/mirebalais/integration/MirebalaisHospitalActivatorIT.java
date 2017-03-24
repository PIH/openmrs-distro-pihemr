package org.openmrs.module.mirebalais.integration;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.EmrApiActivator;
import org.openmrs.module.haiticore.org.openmrs.module.haiticore.metadata.HaitiPersonAttributeTypeBundle;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.importpatientfromws.api.ImportPatientFromWebService;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.pihcore.PihCoreActivator;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.ConceptsFromMetadataSharing;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SkipBaseSetup
public class MirebalaisHospitalActivatorIT extends BaseModuleContextSensitiveTest {

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.setProperty("pih.config", "mirebalais,mirebalais-production");
        return p;
    };

    @Autowired
    private MetadataDeployService deployService;

    @Autowired
    private ConceptsFromMetadataSharing conceptsFromMetadataSharing;

    @Autowired
    private HaitiPersonAttributeTypeBundle personAttributeTypeBundle;

    @Before
    public void beforeEachTest() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        executeDataSet("globalPropertiesTestDataset.xml");
        executeDataSet("serializedReportingDataset.xml");
        executeDataSet("fromMirebalaisMetadataModule.xml");
        authenticate();

        deployService.installBundle(conceptsFromMetadataSharing);
        personAttributeTypeBundle.install();

        // run the emrapi activator
        EmrApiActivator emrApiActivator = new EmrApiActivator();
        emrApiActivator.started();

        // set up metatdata from pih core first
        PihCoreActivator pihCoreActivator = new PihCoreActivator();
        Config config = mock(Config.class);
        when(config.getCountry()).thenReturn(ConfigDescriptor.Country.HAITI);
        when(config.getSite()).thenReturn(ConfigDescriptor.Site.MIREBALAIS);
        pihCoreActivator.setConfig(config);
        pihCoreActivator.started();

        MirebalaisHospitalActivator activator = new MirebalaisHospitalActivator();
        activator.setTestMode(true);
        activator.contextRefreshed();
        activator.started();
    }

    @AfterClass
    public static void tearDown() {
        runtimeProperties = null;
    }

    @Test
    @DirtiesContext
    public void testThatActivatorDoesAllSetup() throws Exception {
        MirebalaisHospitalService service = Context.getService(MirebalaisHospitalService.class);
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        LocationService locationService = Context.getLocationService();

        /**
        ConfigureIdGenerators configureIdGenerators = new ConfigureIdGenerators(identifierSourceService, locationService);

        IdentifierPool localZlIdentifierPool = service.getLocalZlIdentifierPool();
        RemoteIdentifierSource remoteZlIdentifierSource = service.getRemoteZlIdentifierSource();
        SequentialIdentifierGenerator dossierSequenceGenerator = service.getDossierSequenceGenerator(PihCoreConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID);

        PatientIdentifierType zlIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(HaitiPatientIdentifierTypes.ZL_EMR_ID.uuid());
        PatientIdentifierType dossierNumberIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(HaitiPatientIdentifierTypes.DOSSIER_NUMBER.uuid());

        AutoGenerationOption autoGenerationOption = Context.getService(IdentifierSourceService.class).getAutoGenerationOption(zlIdentifierType);

        assertEquals(HaitiPatientIdentifierTypes.ZL_EMR_ID.uuid(), zlIdentifierType.getUuid());
        assertEquals(zlIdentifierType, autoGenerationOption.getIdentifierType());
        assertEquals(localZlIdentifierPool, autoGenerationOption.getSource());

        assertEquals(PihCoreConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID, localZlIdentifierPool.getUuid());
        assertEquals(PihCoreConstants.LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE, localZlIdentifierPool.getBatchSize());
        assertEquals(PihCoreConstants.LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE, localZlIdentifierPool.getMinPoolSize());

        assertEquals(PihCoreConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID, remoteZlIdentifierSource.getUuid());
        assertEquals(configureIdGenerators.getRemoteZlIdentifierSourceUrl(), remoteZlIdentifierSource.getUrl());
        assertEquals(configureIdGenerators.getRemoteZlIdentifierSourceUsername(), remoteZlIdentifierSource.getUser());
        assertEquals(configureIdGenerators.getRemoteZlIdentifierSourcePassword(), remoteZlIdentifierSource.getPassword());

        assertEquals("A", dossierSequenceGenerator.getPrefix());
        assertEquals(new Integer(7), dossierSequenceGenerator.getMaxLength());
        assertEquals(new Integer(7), dossierSequenceGenerator.getMinLength());
        assertEquals("0123456789", dossierSequenceGenerator.getBaseCharacterSet());
        assertEquals("000001", dossierSequenceGenerator.getFirstIdentifierBase());
        assertEquals(PihCoreConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID, dossierSequenceGenerator.getUuid());
        assertEquals(dossierNumberIdentifierType, dossierSequenceGenerator.getIdentifierType());
        assertEquals(2, Context.getService(IdentifierSourceService.class).getAutoGenerationOptions(dossierNumberIdentifierType).size());
         **/


        assertNotNull(Context.getService(ImportPatientFromWebService.class).getRemoteServers().get("lacolline"));
    }

}
