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

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.mirebalaisreports.MirebalaisReportsProperties;
import org.openmrs.module.mirebalaisreports.definitions.InpatientStatsMonthlyReportManager;
import org.openmrs.module.pihcore.reporting.BaseInpatientReportTest;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openmrs.module.reporting.common.ReportingMatchers.isCohortWithExactlyIds;

// TODO get this working (if we don't move back into the Mirebalais Reports module)
@Ignore
@SkipBaseSetup
public class InpatientStatsMonthlyReportManagerTest extends BaseInpatientReportTest {

    @Autowired
    MirebalaisReportsProperties mirebalaisProps;

    @Autowired
    EmrApiProperties emrProps;

    @Autowired
    TestDataManager data;

    @Autowired
    InpatientStatsMonthlyReportManager reportManager;

    @Autowired
    ReportDefinitionService reportDefinitionService;

    @Autowired @Qualifier("adminService")
    AdministrationService administrationService;

    // we need to have access to certain messages to make the Excel sheet work right (i.e. formatting the location name)
    MessageSourceService originalMessageSourceService;
    MessageSourceService messageSourceService;

    @Before
    public void setUpMockMessageSourceService() {
        // org.openmrs.module.reporting.ReportingConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE() will cache this so we need
        // to explicitly clear that cache after the test, to avoid affecting other tests. (The automatic rollback of the
        // DB write will not trigger a GlobalPropertyListener.)
        administrationService.setGlobalProperty(ReportingConstants.DEFAULT_LOCALE_GP_NAME, "fr");

        ServiceContext serviceContext = ServiceContext.getInstance();

        originalMessageSourceService = serviceContext.getMessageSourceService();

        messageSourceService = mock(MessageSourceService.class);
        when(messageSourceService.getMessage("ui.i18n.Location.name.e5db0599-89e8-44fa-bfa2-07e47d63546f", null, Locale.FRENCH)).thenReturn("Sal Gason");
        serviceContext.setMessageSourceService(messageSourceService);
    }

    @After
    public void restoreOriginalMessageSourceService() {
        if (originalMessageSourceService != null) {
            ServiceContext.getInstance().setMessageSourceService(originalMessageSourceService);
        }
        // Force org.openmrs.module.reporting.ReportingConstants to clear its cached locale value to avoid affecting
        // other tests
        new ReportingConstants().globalPropertyChanged(new GlobalProperty(ReportingConstants.DEFAULT_LOCALE_GP_NAME, null));
    }

    @Test
    public void testReport() throws Exception {
        Location mensWard = mirebalaisProps.getMensInternalMedicineLocation();

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("month", DateUtil.parseYmd("2013-10-01"));
        context.addParameterValue("location", mensWard);

        ReportDefinition reportDefinition = reportManager.constructReportDefinition();
        ReportData evaluated = reportDefinitionService.evaluate(reportDefinition, context);

        assertThat(evaluated.getDataSets().size(), is(1));

        // new TsvReportRenderer().render(evaluated, null, System.out);

        DataSet dataSet = evaluated.getDataSets().values().iterator().next();
        Iterator<DataSetRow> rowIterator = dataSet.iterator();
        for (int day = 1; day <= 31; ++day) {
            DataSetRow row = rowIterator.next();
            assertThat((Date) row.getColumnValue("parameter.startDate"), is(DateUtil.parseYmd("2013-10-" + day)));
            assertThat((Date) row.getColumnValue("parameter.endDate"), is(DateUtil.parseYmdhms("2013-10-" + day + " 23:59:59.999")));
            assertThat((Location) row.getColumnValue("parameter.location"), is(mensWard));

            if (day == 1) {
                // patient4 is admitted
                assertThat((Cohort) row.getColumnValue("censusAtStart"), isCohortWithExactlyIds());
                assertThat((Cohort) row.getColumnValue("admissionDuring"), isCohortWithExactlyIds(patient4.getId()));
                assertThat((Cohort) row.getColumnValue("censusAtEnd"), isCohortWithExactlyIds(patient4.getId()));
            }
            else if (day == 2) {
                // patient4 is still around, patient3 was admitted and left
                assertThat((Cohort) row.getColumnValue("censusAtStart"), isCohortWithExactlyIds(patient4.getId()));
                assertThat((Cohort) row.getColumnValue("admissionDuring"), isCohortWithExactlyIds(patient3.getId()));
                assertThat((Cohort) row.getColumnValue("leftWithoutCompletingTxDuring"), isCohortWithExactlyIds(patient3.getId()));
                assertThat((Cohort) row.getColumnValue("censusAtEnd"), isCohortWithExactlyIds(patient4.getId()));
            }
            else if (day == 3) {
                // patient4 discharged, patient3 comes back
                assertThat((Cohort) row.getColumnValue("censusAtStart"), isCohortWithExactlyIds(patient4.getId()));
                assertThat((Cohort) row.getColumnValue("admissionDuring"), isCohortWithExactlyIds(patient3.getId()));
                assertThat((Cohort) row.getColumnValue("dischargedDuring"), isCohortWithExactlyIds(patient4.getId()));
                assertThat((Cohort) row.getColumnValue("censusAtEnd"), isCohortWithExactlyIds(patient3.getId()));
            }
            else if (day == 4 || day == 5 || day == 6) {
                // patient3 still around
                assertThat((Cohort) row.getColumnValue("censusAtStart"), isCohortWithExactlyIds(patient3.getId()));
                assertThat((Cohort) row.getColumnValue("admissionDuring"), isCohortWithExactlyIds());
                assertThat((Cohort) row.getColumnValue("censusAtEnd"), isCohortWithExactlyIds(patient3.getId()));
            }
            else if (day == 7) {
                // patient3 transferred out
                assertThat((Cohort) row.getColumnValue("censusAtStart"), isCohortWithExactlyIds(patient3.getId()));
                assertThat((Cohort) row.getColumnValue("admissionDuring"), isCohortWithExactlyIds());
                assertThat((Cohort) row.getColumnValue("transferOutOfHumDuring"), isCohortWithExactlyIds(patient3.getId()));
                assertThat((Cohort) row.getColumnValue("censusAtEnd"), isCohortWithExactlyIds());
            }
            else {
                // nobody around
                assertThat((Cohort) row.getColumnValue("censusAtStart"), isCohortWithExactlyIds());
                assertThat((Cohort) row.getColumnValue("admissionDuring"), isCohortWithExactlyIds());
                assertThat((Cohort) row.getColumnValue("censusAtEnd"), isCohortWithExactlyIds());
            }
        }

        List<ReportDesign> reportDesigns = reportManager.constructReportDesigns(reportDefinition);
        final ReportDesign design = reportDesigns.iterator().next();

        // hack to avoid having to save things to the DB
        ExcelTemplateRenderer renderer = new ExcelTemplateRenderer() {
            public ReportDesign getDesign(String argument) {
                return design;
            }
        };

        renderer.render(evaluated, "xxx:xls", new FileOutputStream("/tmp/InpatientStatsMonthly.xls"));
    }

}
