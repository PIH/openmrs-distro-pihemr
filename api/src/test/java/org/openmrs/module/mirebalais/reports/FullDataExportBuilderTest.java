package org.openmrs.module.mirebalais.reports;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.emrapi.disposition.Disposition;
import org.openmrs.module.emrapi.disposition.DispositionDescriptor;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.mirebalaisreports.MirebalaisReportsProperties;
import org.openmrs.module.mirebalaisreports.definitions.FullDataExportBuilder;
import org.openmrs.module.pihcore.reporting.dataset.manager.EncounterDataSetManagerTest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
@SkipBaseSetup
public class FullDataExportBuilderTest extends EncounterDataSetManagerTest {

    @Autowired
    FullDataExportBuilder builder;

    @Autowired
    MirebalaisReportsProperties mirebalaisReportsProperties;

    @Autowired
    DispositionService originalDispositionService;

    @Override
    @Before
    public void setup() throws Exception {
        super.setup();
        Disposition someDisposition = new Disposition();
        someDisposition.setConceptCode("PIH:ADMIT TO HOSPITAL");
        DispositionService dispositionService = mock(DispositionService.class);
        when(dispositionService.getDispositionByUniqueId(anyString())).thenReturn(someDisposition);
        when(dispositionService.getDispositionDescriptor()).thenReturn(new DispositionDescriptor());
        mirebalaisReportsProperties.setDispositionService(dispositionService);
    }

    @After
    public void tearDown() throws Exception {
        mirebalaisReportsProperties.setDispositionService(originalDispositionService);
    }

    @Test
    public void shouldSetupTheReportWithAllDataSets() throws Exception {
        FullDataExportBuilder.Configuration configuration = new FullDataExportBuilder.Configuration("uuid", "prefix", null);
        ReportDefinition reportDefinition = builder.buildReportManager(configuration).constructReportDefinition();
        Assert.assertEquals(16, reportDefinition.getDataSetDefinitions().size());
    }

    @Test
    public void shouldSetupTheReportWithSelectedDataSets() throws Exception {
        FullDataExportBuilder.Configuration configuration = new FullDataExportBuilder.Configuration("uuid", "prefix", Arrays.asList("patients", "visits"));
        ReportDefinition reportDefinition = builder.buildReportManager(configuration).constructReportDefinition();
        Assert.assertEquals(2, reportDefinition.getDataSetDefinitions().size());
    }

    @Test
    public void shouldOrderTheDataSetsInTheReportCorrectly() throws Exception {
        FullDataExportBuilder.Configuration configuration = new FullDataExportBuilder.Configuration("uuid", "prefix", null);
        ReportDefinition reportDefinition = builder.buildReportManager(configuration).constructReportDefinition();
        int i=0;
        for (String dsName : reportDefinition.getDataSetDefinitions().keySet()) {
			Assert.assertEquals(builder.dataSetOptions.get(i), dsName);
            i++;
        }
    }

}
