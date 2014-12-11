package org.openmrs.module.mirebalais.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.mirebalais.config.Config;
import org.openmrs.module.mirebalaisreports.MirebalaisReportsProperties;
import org.openmrs.module.mirebalaisreports.definitions.AllPatientsWithIdsReportManager;
import org.openmrs.module.mirebalaisreports.definitions.AppointmentsReportManager;
import org.openmrs.module.mirebalaisreports.definitions.FullDataExportBuilder;
import org.openmrs.module.mirebalaisreports.definitions.InpatientListReportManager;
import org.openmrs.module.mirebalaisreports.definitions.InpatientStatsDailyReportManager;
import org.openmrs.module.mirebalaisreports.definitions.InpatientStatsMonthlyReportManager;
import org.openmrs.module.mirebalaisreports.definitions.ReportManager;
import org.openmrs.module.mirebalaisreports.definitions.UsersAndProvidersReportManager;
import org.openmrs.module.mirebalaisreports.definitions.helper.DailyIndicatorByLocationReportDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;

import java.io.IOException;
import java.util.List;

public class ReportSetup {

    protected static Log log = LogFactory.getLog(ReportSetup.class);

    public static void scheduleReports(ReportService reportService, ReportDefinitionService reportDefinitionService,
                                       AdministrationService administrationService, SerializedObjectDAO serializedObjectDAO,
                                       Config config) {

        setupFullDataExports(reportService, reportDefinitionService, administrationService, serializedObjectDAO);
        setupOtherReports(reportService, reportDefinitionService, administrationService, serializedObjectDAO);
        setupBackupReports(reportService, reportDefinitionService, config);

    }


    private static void setupFullDataExports(ReportService reportService, ReportDefinitionService reportDefinitionService,
                                             AdministrationService administrationService, SerializedObjectDAO serializedObjectDAO) {
        FullDataExportBuilder fullDataExportBuilder = Context.getRegisteredComponents(FullDataExportBuilder.class).get(0);
        for (ReportManager manager : fullDataExportBuilder.getAllReportManagers()) {
            setupReport(manager, reportService, reportDefinitionService, administrationService, serializedObjectDAO);
        }
    }

    /**
     * Currently we require these to be white-listed, until we've gone through all ReportManagers, and ensured they are
     * ready to be included here
     */
    private static void setupOtherReports(ReportService reportService, ReportDefinitionService reportDefinitionService,
                                          AdministrationService administrationService, SerializedObjectDAO serializedObjectDAO) {

        setupReport(Context.getRegisteredComponents(AllPatientsWithIdsReportManager.class).get(0),
                reportService, reportDefinitionService, administrationService, serializedObjectDAO);

        setupReport(Context.getRegisteredComponents(InpatientStatsDailyReportManager.class).get(0),
                reportService, reportDefinitionService, administrationService, serializedObjectDAO);

        setupReport(Context.getRegisteredComponents(InpatientStatsMonthlyReportManager.class).get(0),
                reportService, reportDefinitionService, administrationService, serializedObjectDAO);

        setupReport(Context.getRegisteredComponents(InpatientListReportManager.class).get(0),
                reportService, reportDefinitionService, administrationService, serializedObjectDAO);

        setupReport(Context.getRegisteredComponents(UsersAndProvidersReportManager.class).get(0),
                reportService, reportDefinitionService, administrationService, serializedObjectDAO);

        setupReport(Context.getRegisteredComponents(AppointmentsReportManager.class).get(0),
                reportService, reportDefinitionService, administrationService, serializedObjectDAO);

        for (DailyIndicatorByLocationReportDefinition manager : Context.getRegisteredComponents(DailyIndicatorByLocationReportDefinition.class)) {
            setupReport(manager,reportService, reportDefinitionService, administrationService, serializedObjectDAO);;
        }
    }

    private static void setupBackupReports(ReportService reportService, ReportDefinitionService reportDefinitionService, Config config) {
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
            Context.evictFromSession(existing);
        }
        else {
            // incompatible class changes for a serialized object could mean that getting the definition return null
            // and some serialization error gets logged. In that case we want to overwrite the invalid serialized definition
            SerializedObject invalidSerializedObject = serializedObjectDAO.getSerializedObjectByUuid(reportDefinition.getUuid());
            if (invalidSerializedObject != null) {
                reportDefinition.setId(invalidSerializedObject.getId());
                Context.evictFromSession(invalidSerializedObject);
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

        try {
            List<ReportDesign> designs = manager.constructReportDesigns(reportDefinition);
            for (ReportDesign design : designs) {
                reportService.saveReportDesign(design);
            }
            administrationService.setGlobalProperty(globalPropertyFor(manager), manager.getVersion());
        }
        catch (IOException ex) {
            log.error("Error constructing report design for " + reportDefinition.getName(), ex);
        }
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

