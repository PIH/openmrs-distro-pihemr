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

package org.openmrs.module.mirebalais.reports;

import org.junit.Test;
import org.openmrs.module.mirebalaisreports.definitions.InpatientStatsDailyReportManager;
import org.openmrs.module.pihcore.reporting.BaseInpatientReportTest;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@SkipBaseSetup
public class InpatientStatsDailyReportManagerTest extends BaseInpatientReportTest {

    @Autowired
    private InpatientStatsDailyReportManager manager;

    @Autowired
    private ReportDefinitionService reportDefinitionService;

    @Test
    public void testRunningReport() throws Exception {
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("day", DateUtil.parseDate("2013-10-03", "yyyy-MM-dd"));

        ReportDefinition reportDefinition = manager.constructReportDefinition();
        ReportData evaluated = reportDefinitionService.evaluate(reportDefinition, context);

        Map<String, Integer> results = new HashMap<String, Integer>();
        for (DataSet dataSet : evaluated.getDataSets().values()) {
            MapDataSet mds = (MapDataSet) dataSet;
            for (DataSetColumn column : mds.getMetaData().getColumns()) {
                CohortIndicatorAndDimensionResult val = (CohortIndicatorAndDimensionResult) mds.getData(column);
                results.put(column.getName(), val.getValue().intValue());
                // System.out.println(column.getName() + " = " + column.getLabel() + " = " + val.getValue().intValue());
            }
        }

        // Men's Internal Medicine
        assertAndRemove(results, "censusAtStart:e5db0599-89e8-44fa-bfa2-07e47d63546f", 1);
        assertAndRemove(results, "admissions:e5db0599-89e8-44fa-bfa2-07e47d63546f", 1);
        assertAndRemove(results, "discharged:e5db0599-89e8-44fa-bfa2-07e47d63546f", 1);
        assertAndRemove(results, "censusAtEnd:e5db0599-89e8-44fa-bfa2-07e47d63546f", 1);

        // Surgical Ward
        assertAndRemove(results, "transfersIn:7d6cc39d-a600-496f-a320-fd4985f07f0b", 1);
        assertAndRemove(results, "censusAtEnd:7d6cc39d-a600-496f-a320-fd4985f07f0b", 1);

        // Women's Internal Medicine
        assertAndRemove(results, "censusAtStart:2c93919d-7fc6-406d-a057-c0b640104790", 2);
        assertAndRemove(results, "transfersOut:2c93919d-7fc6-406d-a057-c0b640104790", 1);
        assertAndRemove(results, "censusAtEnd:2c93919d-7fc6-406d-a057-c0b640104790", 1);

        assertAndRemove(results, "edcheckin", 1);
        assertAndRemove(results, "orvolume", 1);
        assertAndRemove(results, "possiblereadmission", 1);

        // everything else should be 0
        for (Integer actual : results.values()) {
            assertThat(actual, is(0));
        }
    }

    private void assertAndRemove(Map<String, Integer> results, String key, int expected) {
        assertThat(key + " should be " + expected, results.get(key), is(expected));
        results.remove(key);
    }

}
