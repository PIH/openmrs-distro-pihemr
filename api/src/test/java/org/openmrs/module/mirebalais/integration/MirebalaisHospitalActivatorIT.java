package org.openmrs.module.mirebalais.integration;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.EmrApiActivator;
import org.openmrs.module.importpatientfromws.api.ImportPatientFromWebService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.module.pihcore.PihCoreActivator;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.BiometricsConfigDescriptor;
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
public class MirebalaisHospitalActivatorIT extends PihCoreContextSensitiveTest {


    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.setProperty("pih.config", "mirebalais,mirebalais-production");
        return p;
    }

    @Autowired
    private MetadataDeployService deployService;

    @Autowired
    private ConceptsFromMetadataSharing conceptsFromMetadataSharing;

    @Before
    public void beforeEachTest() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        executeDataSet("globalPropertiesTestDataset.xml");
        executeDataSet("serializedReportingDataset.xml");
        executeDataSet("fromMirebalaisMetadataModule.xml");
        authenticate();

        deployService.installBundle(conceptsFromMetadataSharing);
        loadFromInitializer(Domain.PERSON_ATTRIBUTE_TYPES, "personAttributeTypes.csv");

        // run the emrapi activator
        EmrApiActivator emrApiActivator = new EmrApiActivator();
        emrApiActivator.started();

        // set up metatdata from pih core first
        PihCoreActivator pihCoreActivator = new PihCoreActivator();
        Config config = mock(Config.class);
        when(config.getCountry()).thenReturn(ConfigDescriptor.Country.HAITI);
        when(config.getSite()).thenReturn("Chiapas");
        when(config.getBiometricsConfig()).thenReturn(new BiometricsConfigDescriptor());
        pihCoreActivator.setConfig(config);
        pihCoreActivator.setTestingContext(true);
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
    @Ignore
    @DirtiesContext
    public void testThatActivatorDoesAllSetup() throws Exception {
        assertNotNull(Context.getService(ImportPatientFromWebService.class).getRemoteServers().get("lacolline"));
    }

}
