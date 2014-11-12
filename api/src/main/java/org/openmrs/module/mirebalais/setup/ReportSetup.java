package org.openmrs.module.mirebalais.setup;

import org.openmrs.module.mirebalais.MirebalaisCustomProperties;
import org.openmrs.module.mirebalaisreports.MirebalaisReportsProperties;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;

public class ReportSetup {

    // sets up daily reports, currently only used on Mirebalais production server (as a backup)
    public static void scheduleReports(ReportService reportService, ReportDefinitionService reportDefinitionService, MirebalaisCustomProperties customProperties) {

        if (customProperties.getScheduleBackupReports()) {

            // schedule the all patients report to run at 4am and 4pm everyday
            ReportRequest allPatientsScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.ALL_PATIENTS_SCHEDULED_REPORT_REQUEST_UUID);
            if (allPatientsScheduledReportRequest == null) {
                allPatientsScheduledReportRequest = new ReportRequest();
            }
            ReportDefinition allPatientsReportDefinition = reportDefinitionService.getDefinitionByUuid(MirebalaisReportsProperties.ALL_PATIENTS_WITH_IDS_REPORT_DEFINITION_UUID);
            allPatientsScheduledReportRequest.setUuid(MirebalaisReportsProperties.ALL_PATIENTS_SCHEDULED_REPORT_REQUEST_UUID);
            allPatientsScheduledReportRequest.setReportDefinition(Mapped.noMappings(allPatientsReportDefinition));
            allPatientsScheduledReportRequest.setRenderingMode(getCsvReportRenderer(reportService, allPatientsReportDefinition));
            allPatientsScheduledReportRequest.setSchedule("0 0 4-23/12 * * ?");
            reportService.queueReport(allPatientsScheduledReportRequest);

            // schedule the appointments report to run  at 4am and 4pm everyday, retrieving all appointments for the next seven days
            ReportRequest appointmentsScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.APPOINTMENTS_SCHEDULED_REPORT_REQUEST_UUID);
            if (appointmentsScheduledReportRequest == null) {
                appointmentsScheduledReportRequest = new ReportRequest();
            }
            ReportDefinition appointmentsReportDefinition = reportDefinitionService.getDefinitionByUuid(MirebalaisReportsProperties.APPOINTMENTS_REPORT_DEFINITION_UUID);
            appointmentsScheduledReportRequest.setUuid(MirebalaisReportsProperties.APPOINTMENTS_SCHEDULED_REPORT_REQUEST_UUID);
            appointmentsScheduledReportRequest.setReportDefinition(Mapped.map(appointmentsReportDefinition, "startDate=${start_of_today},endDate=${start_of_today + 7d}"));
            appointmentsScheduledReportRequest.setRenderingMode(getCsvReportRenderer(reportService, appointmentsReportDefinition));
            appointmentsScheduledReportRequest.setSchedule("0 0 4-23/12 * * ?");
            reportService.queueReport(appointmentsScheduledReportRequest);

            // schedule the check-ins report to run  at 4am and 4pm everyday retrieving all check-ins for the past seven days
            ReportRequest checkInsDataExportScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.CHECKINS_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            if (checkInsDataExportScheduledReportRequest == null) {
                checkInsDataExportScheduledReportRequest = new ReportRequest();
            }
            ReportDefinition checkInsDataExportReportDefinition = reportDefinitionService.getDefinitionByUuid(MirebalaisReportsProperties.CHECKINS_DATA_EXPORT_REPORT_DEFINITION_UUID);
            checkInsDataExportScheduledReportRequest.setUuid(MirebalaisReportsProperties.CHECKINS_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            checkInsDataExportScheduledReportRequest.setReportDefinition(Mapped.map(checkInsDataExportReportDefinition, "startDate=${start_of_today - 7d},endDate=${now}"));
            checkInsDataExportScheduledReportRequest.setRenderingMode(getCsvReportRenderer(reportService, checkInsDataExportReportDefinition));
            checkInsDataExportScheduledReportRequest.setSchedule("0 0 4-23/12 * * ?");

            reportService.queueReport(checkInsDataExportScheduledReportRequest);
        }
        else {

            ReportRequest allPatientsScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.ALL_PATIENTS_SCHEDULED_REPORT_REQUEST_UUID);
            if (allPatientsScheduledReportRequest != null) {
                reportService.purgeReportRequest(allPatientsScheduledReportRequest);
            }

            ReportRequest appointmentsScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.APPOINTMENTS_SCHEDULED_REPORT_REQUEST_UUID);
            if (appointmentsScheduledReportRequest != null) {
                reportService.purgeReportRequest(appointmentsScheduledReportRequest);
            }

            ReportRequest checkInsDataExportScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.CHECKINS_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            if (checkInsDataExportScheduledReportRequest != null) {
                reportService.purgeReportRequest(checkInsDataExportScheduledReportRequest);
            }

        }

    }

    private static RenderingMode getCsvReportRenderer(ReportService reportService, ReportDefinition reportDefinition) {

        for (RenderingMode candidate : reportService.getRenderingModes(reportDefinition)) {
            if (candidate.getDescriptor().startsWith("org.openmrs.module.reporting.report.renderer.CsvReportRenderer")) {
                return candidate;
            }
        }
        return null;
    }

}

