package org.openmrs.module.mirebalais.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;

import java.util.Calendar;
import java.util.List;

public class ReportSetup {

    protected static Log log = LogFactory.getLog(ReportSetup.class);

    //***** REPORT DEFINITIONS *****
    public static final String FULL_DATA_EXPORT_REPORT_DEFINITION_UUID = "8c3752e2-20bb-11e3-b5bd-0bec7fb71852";
    public static final String ALL_PATIENTS_WITH_IDS_REPORT_DEFINITION_UUID = "d534683e-20bd-11e3-b5bd-0bec7fb71852";
    public static final String CHECKINS_DATA_EXPORT_REPORT_DEFINITION_UUID = "1c72b461-fc74-11e3-8248-08002769d9ae";
    public static final String APPOINTMENTS_REPORT_DEFINITION_UUID = "7fd11020-e5d1-11e3-ac10-0800200c9a66";

    //***** SCHEDULED REPORT REQUESTS *****
    public static final String ALL_PATIENTS_SCHEDULED_REPORT_REQUEST_UUID = "733cd7c0-2ed0-11e4-8c21-0800200c9a66";
    public static final String APPOINTMENTS_SCHEDULED_REPORT_REQUEST_UUID = "f19ff350-2ed9-11e4-8c21-0800200c9a66";
    public static final String CHECKINS_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID = "f9e01270-2ed9-11e4-8c21-0800200c9a66";
    public static final String FULL_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID = "2619c140-5b0e-11e5-a837-0800200c9a66";

    public static void scheduleBackupReports(Config config) {
        ReportService reportService = Context.getService(ReportService.class);
        ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);
    	// TODO: we no longer use this, so this can likely be removed in the future (and doesn't need to be migrated
		// when we refactor our setup process)
        // sets up reports currently only used on Mirebalais production server (as a backup)
        if (config.shouldScheduleBackupReports()) {

            // schedule the all patients report to run at 4am and 4pm everyday
            ReportRequest allPatientsScheduledReportRequest = reportService.getReportRequestByUuid(ALL_PATIENTS_SCHEDULED_REPORT_REQUEST_UUID);
            if (allPatientsScheduledReportRequest == null) {
                allPatientsScheduledReportRequest = new ReportRequest();
            }
            ReportDefinition allPatientsReportDefinition = reportDefinitionService.getDefinitionByUuid(ALL_PATIENTS_WITH_IDS_REPORT_DEFINITION_UUID);
            allPatientsScheduledReportRequest.setUuid(ALL_PATIENTS_SCHEDULED_REPORT_REQUEST_UUID);
            allPatientsScheduledReportRequest.setReportDefinition(Mapped.noMappings(allPatientsReportDefinition));
            allPatientsScheduledReportRequest.setRenderingMode(getCsvReportRenderer(reportService, allPatientsReportDefinition));
            allPatientsScheduledReportRequest.setSchedule("0 0 4-23/12 * * ?");
            reportService.queueReport(allPatientsScheduledReportRequest);

            // schedule the appointments report to run  at 4am and 4pm everyday, retrieving all appointments for the next seven days
            ReportRequest appointmentsScheduledReportRequest = reportService.getReportRequestByUuid(APPOINTMENTS_SCHEDULED_REPORT_REQUEST_UUID);
            if (appointmentsScheduledReportRequest == null) {
                appointmentsScheduledReportRequest = new ReportRequest();
            }
            ReportDefinition appointmentsReportDefinition = reportDefinitionService.getDefinitionByUuid(APPOINTMENTS_REPORT_DEFINITION_UUID);
            appointmentsScheduledReportRequest.setUuid(APPOINTMENTS_SCHEDULED_REPORT_REQUEST_UUID);
            appointmentsScheduledReportRequest.setReportDefinition(Mapped.map(appointmentsReportDefinition, "startDate=${start_of_today},endDate=${start_of_today + 7d}"));
            appointmentsScheduledReportRequest.setRenderingMode(getCsvReportRenderer(reportService, appointmentsReportDefinition));
            appointmentsScheduledReportRequest.setSchedule("0 0 4-23/12 * * ?");
            reportService.queueReport(appointmentsScheduledReportRequest);

            // schedule the check-ins report to run  at 4am and 4pm everyday retrieving all check-ins for the past seven days
            ReportRequest checkInsDataExportScheduledReportRequest = reportService.getReportRequestByUuid(CHECKINS_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            if (checkInsDataExportScheduledReportRequest == null) {
                checkInsDataExportScheduledReportRequest = new ReportRequest();
            }
            ReportDefinition checkInsDataExportReportDefinition = reportDefinitionService.getDefinitionByUuid(CHECKINS_DATA_EXPORT_REPORT_DEFINITION_UUID);
            checkInsDataExportScheduledReportRequest.setUuid(CHECKINS_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            checkInsDataExportScheduledReportRequest.setReportDefinition(Mapped.map(checkInsDataExportReportDefinition, "startDate=${start_of_today - 7d},endDate=${now}"));
            checkInsDataExportScheduledReportRequest.setRenderingMode(getCsvReportRenderer(reportService, checkInsDataExportReportDefinition));
            checkInsDataExportScheduledReportRequest.setSchedule("0 0 4-23/12 * * ?");
            reportService.queueReport(checkInsDataExportScheduledReportRequest);

        }
        else {

            ReportRequest allPatientsScheduledReportRequest = reportService.getReportRequestByUuid(ALL_PATIENTS_SCHEDULED_REPORT_REQUEST_UUID);
            if (allPatientsScheduledReportRequest != null) {
                reportService.purgeReportRequest(allPatientsScheduledReportRequest);
            }

            ReportRequest appointmentsScheduledReportRequest = reportService.getReportRequestByUuid(APPOINTMENTS_SCHEDULED_REPORT_REQUEST_UUID);
            if (appointmentsScheduledReportRequest != null) {
                reportService.purgeReportRequest(appointmentsScheduledReportRequest);
            }

            ReportRequest checkInsDataExportScheduledReportRequest = reportService.getReportRequestByUuid(CHECKINS_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            if (checkInsDataExportScheduledReportRequest != null) {
                reportService.purgeReportRequest(checkInsDataExportScheduledReportRequest);
            }

        }
    }

    public static void scheduleMonthlyExportsReports(Config config) {
        ReportService reportService = Context.getService(ReportService.class);
        ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);
        if (config.shouldScheduleMonthlyDataExports()) {
            // scheduled to run during the morning of the 5th of every month, for the previous month
            ReportRequest fullDataExportScheduledReportRequest = reportService.getReportRequestByUuid(FULL_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            if (fullDataExportScheduledReportRequest == null) {
                fullDataExportScheduledReportRequest = new ReportRequest();
            }

            ReportDefinition fullDataExportReportDefinition = reportDefinitionService.getDefinitionByUuid(FULL_DATA_EXPORT_REPORT_DEFINITION_UUID);
            fullDataExportScheduledReportRequest.setUuid(FULL_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            fullDataExportScheduledReportRequest.setReportDefinition(Mapped.map(fullDataExportReportDefinition, "startDate=${start_of_last_month},endDate=${end_of_last_month}"));
            fullDataExportScheduledReportRequest.setRenderingMode(getCsvReportRenderer(reportService, fullDataExportReportDefinition));
            fullDataExportScheduledReportRequest.setSchedule("0 0 4 5 * ?");  //4am on the 5th of the month
            reportService.queueReport(fullDataExportScheduledReportRequest);
        }
        else {
            ReportRequest fullDataExportScheduledReportRequest = reportService.getReportRequestByUuid(FULL_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            if (fullDataExportScheduledReportRequest != null) {
                reportService.purgeReportRequest(fullDataExportScheduledReportRequest);
            }
        }
    }

    public static void cleanupOldReports() {

        // delete requests more than 6 months old, even if that have been saved--code adapted from deleteOldReportRequests from reporting module
        ReportService reportService = Context.getService(ReportService.class);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6);

        log.debug("Checking for saved reports older than six months Request date before " + cal.getTime());

        List<ReportRequest> oldRequests = reportService.getReportRequests(null, null, cal.getTime(), null);

        log.debug("Found " + oldRequests.size() + " requests that qualify");

        for (ReportRequest request : oldRequests) {
            log.info("Request qualifies for deletion.  Deleting: " + request.getUuid());
            try {
                reportService.purgeReportRequest(request);
            } catch (Exception e) {
                log.warn("Unable to delete old report request: " + request, e);
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

