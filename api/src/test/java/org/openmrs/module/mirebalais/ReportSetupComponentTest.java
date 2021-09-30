package org.openmrs.module.mirebalais;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.mirebalais.setup.ReportSetup;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.manager.BaseReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@SkipBaseSetup
@Ignore
public class ReportSetupComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    SerializedObjectDAO serializedObjectDAO;

    @Autowired @Qualifier("reportingReportDefinitionService")
    ReportDefinitionService reportDefinitionService;

    @Autowired @Qualifier("reportingReportService")
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
        executeDataSet("org/openmrs/module/pihcore/coreMetadata.xml");
        executeDataSet("badReportDefinition.xml");
        authenticate();

        TestReportManager manager = new TestReportManager();
        ReportManagerUtil.setupReport(manager);

        ReportDefinition reportDefinition = reportDefinitionService.getDefinitionByUuid(manager.getUuid());
        assertNotNull(reportDefinition);
        assertThat(reportDefinition.getName(), is("mirebalaisreports.dailyRegistrations.name"));

    }

    public class TestReportManager extends BaseReportManager {

        public TestReportManager() {}

        @Override
        public String getUuid() {
            return "2e91bd04-4c7a-11e3-9325-f3ae8db9f6a7";
        }

        @Override
        public String getName() {
            return "dailyRegistrations";
        }

        @Override
        public String getVersion() {
            return "1.4-SNAPSHOT";
        }

        @Override
        public String getDescription() {
            return "dailyRegistrations";
        }

        @Override
        public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
            return new ArrayList<ReportDesign>();
        }

        @Override
        public ReportDefinition constructReportDefinition() {
            ReportDefinition rd = new ReportDefinition();
            rd.setUuid(getUuid());
            rd.setName(getName());
            rd.setDescription(getDescription());
            SqlDataSetDefinition dsd = new SqlDataSetDefinition();
            dsd.setSqlQuery("select count(*) from encounter_type");
            rd.addDataSetDefinition("sql", dsd, new HashMap<String, Object>());
            return rd;
        }
    }

}
