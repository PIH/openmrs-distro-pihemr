package org.openmrs.module.mirebalais.reports;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.mirebalaisreports.definitions.LqasDiagnosesReportManager;
import org.openmrs.module.pihcore.reporting.BaseReportTest;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Ignore("H2 doesn't seem to like the SQL, though it's legal in MySQL")
@SkipBaseSetup
public class LqasDiagnosesReportManagerComponentTest extends BaseReportTest {

    @Autowired
    private LqasDiagnosesReportManager reportManager;

    @Test
    public void testReport() throws Exception {
        Date startDate = new Date();
        Date endDate = new Date();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(reportManager.getStartDateParameter().getName(), startDate);
        params.put(reportManager.getEndDateParameter().getName(), endDate);
        EvaluationContext evaluationContext = reportManager.initializeContext(params);

        ReportDefinition reportDefinition = reportManager.constructReportDefinition();
        RenderingMode mode = reportManager.getRenderingModes().get(0);
        ReportData reportData = reportDefinitionService.evaluate(reportDefinition, evaluationContext);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mode.getRenderer().render(reportData, mode.getArgument(), out);
        File outputFile = new File(System.getProperty("java.io.tmpdir"), "test.xls");
        ReportUtil.writeByteArrayToFile(outputFile, out.toByteArray());

        InputStream is = new FileInputStream(outputFile);
        POIFSFileSystem fs = new POIFSFileSystem(is);
        HSSFWorkbook wb = new HSSFWorkbook(fs);

        Assert.assertEquals(1, wb.getNumberOfSheets());
    }

}
