package org.openmrs.module.mirebalais.setup;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.mirebalaisreports.MirebalaisReportsProperties;
import org.openmrs.module.mirebalaisreports.definitions.BasePihReportManager;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;

import java.util.Calendar;
import java.util.List;

public class ReportSetup {

    protected static Log log = LogFactory.getLog(ReportSetup.class);

    public static void setupReports(ReportService reportService, ReportDefinitionService reportDefinitionService,
                                    AdministrationService administrationService, SerializedObjectDAO serializedObjectDAO,
                                    Config config) {

        for (BasePihReportManager report : Context.getRegisteredComponents(BasePihReportManager.class)) {
            if (report.getCountries().contains(config.getCountry())  || report.getSites().contains(config.getSite())
                    && (StringUtils.isEmpty(report.getComponent()) || config.isComponentEnabled(report.getComponent()))) {
                setupReport(report, reportService, reportDefinitionService, administrationService, serializedObjectDAO);
            }
        }

        scheduleBackupReports(reportService, reportDefinitionService, config);
        scheduleMonthlyExportsReports(reportService, reportDefinitionService, config);
        cleanupOldReports(reportService);
    }

    private static void scheduleBackupReports(ReportService reportService, ReportDefinitionService reportDefinitionService, Config config) {
        // sets up reports currently only used on Mirebalais production server (as a backup)
        if (config.shouldScheduleBackupReports()) {

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

    private static void scheduleMonthlyExportsReports(ReportService reportService, ReportDefinitionService reportDefinitionService, Config config) {

        if (config.shouldScheduleMonthlyDataExports()) {
            // scheduled to run during the morning of the 5th of every month, for the previous month
            ReportRequest fullDataExportScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.FULL_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            if (fullDataExportScheduledReportRequest == null) {
                fullDataExportScheduledReportRequest = new ReportRequest();
            }

            ReportDefinition fullDataExportReportDefinition = reportDefinitionService.getDefinitionByUuid(MirebalaisReportsProperties.FULL_DATA_EXPORT_REPORT_DEFINITION_UUID);
            fullDataExportScheduledReportRequest.setUuid(MirebalaisReportsProperties.FULL_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            fullDataExportScheduledReportRequest.setReportDefinition(Mapped.map(fullDataExportReportDefinition, "startDate=${start_of_last_month},endDate=${end_of_last_month}"));
            fullDataExportScheduledReportRequest.setRenderingMode(getCsvReportRenderer(reportService, fullDataExportReportDefinition));
            fullDataExportScheduledReportRequest.setSchedule("0 0 4 5 * ?");  //4am on the 5th of the month
            reportService.queueReport(fullDataExportScheduledReportRequest);
        }
        else {
            ReportRequest fullDataExportScheduledReportRequest = reportService.getReportRequestByUuid(MirebalaisReportsProperties.FULL_DATA_EXPORT_SCHEDULED_REPORT_REQUEST_UUID);
            if (fullDataExportScheduledReportRequest != null) {
                reportService.purgeReportRequest(fullDataExportScheduledReportRequest);
            }
        }
    }

    private static void cleanupOldReports(ReportService reportingService) {

        // delete requests more than 6 months old, even if that have been saved--code adapted from deleteOldReportRequests from reporting module

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6);

        log.debug("Checking for saved reports older than six months Request date before " + cal.getTime());

        List<ReportRequest> oldRequests = reportingService.getReportRequests(null, null, cal.getTime(), null);

        log.debug("Found " + oldRequests.size() + " requests that qualify");

        for (ReportRequest request : oldRequests) {
            log.info("Request qualifies for deletion.  Deleting: " + request.getUuid());
            try {
                reportingService.purgeReportRequest(request);
            } catch (Exception e) {
                log.warn("Unable to delete old report request: " + request, e);
            }
        }
    }


    /**
     * This is only public for testing
     * @param manager
     */
    public static void setupReport(ReportManager manager, ReportService reportService, ReportDefinitionService reportDefinitionService,
                                   AdministrationService administrationService, SerializedObjectDAO serializedObjectDAO) {
        if (alreadyAtLatestVersion(manager, administrationService)) {
            return;
        }

        ReportDefinition reportDefinition = manager.constructReportDefinition();

        log.info("Saving new definition of " + reportDefinition.getName());

        ReportDefinition existing = reportDefinitionService.getDefinitionByUuid(reportDefinition.getUuid());
        if (existing != null) {
            // we need to overwrite the existing, rather than purge-and-recreate, to avoid deleting old ReportRequests
            log.debug("overwriting existing ReportDefinition");

            reportDefinition.setId(existing.getId());
           // Context.evictFromSession(existing);
        }
        else {
            // incompatible class changes for a serialized object could mean that getting the definition return null
            // and some serialization error gets logged. In that case we want to overwrite the invalid serialized definition
            SerializedObject invalidSerializedObject = serializedObjectDAO.getSerializedObjectByUuid(reportDefinition.getUuid());
            if (invalidSerializedObject != null) {
                reportDefinition.setId(invalidSerializedObject.getId());
               // Context.evictFromSession(invalidSerializedObject);
            }
//            serializedObjectDAO.purgeObject(invalidSerializedObject.getId());
        }

        reportDefinitionService.saveDefinition(reportDefinition);

        // purging a ReportDesign doesn't trigger any extra logic, so we can just purge-and-recreate here
        List<ReportDesign> existingDesigns = reportService.getReportDesigns(reportDefinition, null, true);
        if (existingDesigns.size() > 0) {
            log.debug("Deleting " + existingDesigns.size() + " old designs for " + reportDefinition.getName());
            for (ReportDesign design : existingDesigns) {
                reportService.purgeReportDesign(design);
            }
        }

        List<ReportDesign> designs = manager.constructReportDesigns(reportDefinition);
        for (ReportDesign design : designs) {
            reportService.saveReportDesign(design);
        }
        administrationService.setGlobalProperty(globalPropertyFor(manager), manager.getVersion());
    }

    private static boolean alreadyAtLatestVersion(ReportManager manager, AdministrationService administrationService) {
        String newVersion = manager.getVersion();
        String existingVersion = administrationService.getGlobalProperty(globalPropertyFor(manager));
        return existingVersion != null &&
                existingVersion.equals(newVersion) &&
                !newVersion.contains("-SNAPSHOT");
    }

    private static String globalPropertyFor(ReportManager manager) {
        return "mirebalaisreports." + manager.getUuid() + ".version";
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

