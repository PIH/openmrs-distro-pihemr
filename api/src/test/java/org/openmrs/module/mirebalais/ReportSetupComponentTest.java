package org.openmrs.module.mirebalais;

import org.junit.Test;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.mirebalais.setup.ReportSetup;
import org.openmrs.module.mirebalaisreports.definitions.DailyRegistrationsReportManager;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.assertNotNull;

@SkipBaseSetup
public class ReportSetupComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    DailyRegistrationsReportManager dailyRegistrationsReportManager;

    @Autowired
    SerializedObjectDAO serializedObjectDAO;

    @Autowired
    ReportDefinitionService reportDefinitionService;

    @Autowired
    ReportService reportService;

    @Autowired @Qualifier("adminService")
    AdministrationService administrationService;


    /**
     * Tests the case where a persisted ReportDefinition is invalid (typically because of an incompatible change to a
     * definition class, while it is being developed)
     * @throws Exception
     */
    @Test
    public void testOverwritingInvalidSerializedReport() throws Exception {
        executeDataSet("reportSetupCoreMetadata.xml");
        executeDataSet("badReportDefinition.xml");
        authenticate();

        ReportSetup.setupReport(dailyRegistrationsReportManager, reportService, reportDefinitionService, administrationService, serializedObjectDAO);

        ReportDefinition reportDefinition = reportDefinitionService.getDefinitionByUuid(dailyRegistrationsReportManager.getUuid());
        assertNotNull(reportDefinition);
    }


}
