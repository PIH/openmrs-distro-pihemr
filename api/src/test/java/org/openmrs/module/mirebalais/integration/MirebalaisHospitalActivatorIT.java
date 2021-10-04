package org.openmrs.module.mirebalais.integration;

import org.junit.AfterClass;
import org.junit.Before;
import org.openmrs.module.emrapi.EmrApiActivator;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.module.pihcore.PihCoreActivator;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.BiometricsConfigDescriptor;
import org.openmrs.test.SkipBaseSetup;

import java.util.Properties;

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

    @Before
    public void beforeEachTest() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        executeDataSet("globalPropertiesTestDataset.xml");
        executeDataSet("serializedReportingDataset.xml");
        executeDataSet("fromMirebalaisMetadataModule.xml");
        authenticate();

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

        MirebalaisHospitalActivator activator = new MirebalaisHospitalActivator();
        activator.started();
    }

    @AfterClass
    public static void tearDown() {
        runtimeProperties = null;
    }
}
