package org.openmrs.module.mirebalais.setup;

import org.apache.commons.lang.time.DateUtils;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.reporting.dataset.definition.AppointmentDataSetDefinition;
import org.openmrs.module.appointmentschedulingui.AppointmentSchedulingUIConstants;
import org.openmrs.module.emrapi.utils.GeneralUtils;
import org.openmrs.module.mirebalais.task.MarkAppointmentsAsMissedOrCompletedTask;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Date;

public class AppointmentSchedulingSetup {

    public static final String TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED = "Mirebalais - Mark Appointments Missed or Completed";
    public static final String TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_DESCRIPTION = "Marks appointments in the past as missed or completed based on Mirebalais business logic";
    public static final Long TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_REPEAT_INTERVAL = new Long(14400); // every four hours


    public static void setupMarkAppointmentAsMissedOrCompletedTask() {

        SchedulerService schedulerService = Context.getSchedulerService();

        TaskDefinition task = schedulerService.getTaskByName(TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED);

        if (task == null) {
            task = new TaskDefinition();
            task.setName(TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED);
            task.setDescription(TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_DESCRIPTION);
            task.setTaskClass(MarkAppointmentsAsMissedOrCompletedTask.class.getName());
            task.setStartTime(DateUtils.addMinutes(new Date(), 5));   // doesn't really do anything since start on startup = true
            task.setRepeatInterval(TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_REPEAT_INTERVAL);
            task.setStartOnStartup(true);
            try {
                schedulerService.scheduleTask(task);
            }
            catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule mark appointments as missed or completed task", e);
            }
        }
        else {
            boolean anyChanges = GeneralUtils.setPropertyIfDifferent(task, "description", TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_DESCRIPTION);
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "taskClass", MarkAppointmentsAsMissedOrCompletedTask.class.getName());
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "repeatInterval", TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_REPEAT_INTERVAL);  // we can't pass in the constant directly for some reason because it is static
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "startOnStartup", true);
            if (anyChanges) {
                schedulerService.saveTaskDefinition(task);
            }
            if (!task.getStarted()) {
                task.setStarted(true);
                try {
                    schedulerService.scheduleTask(task);
                }
                catch (SchedulerException e) {
                    throw new RuntimeException("Failed to schedule mark appointments as missed or completed task", e);
                }
            }
        }
    }

    public static void customizeDailyAppointmentsDataSet() {

        DataSetDefinitionService dataSetDefinitionService = Context.getService(DataSetDefinitionService.class);
        PaperRecordProperties paperRecordProperties = Context.getRegisteredComponents(PaperRecordProperties.class).get(0);

        AppointmentDataSetDefinition dsd =
                (AppointmentDataSetDefinition) dataSetDefinitionService.getDefinition(AppointmentSchedulingUIConstants.DAILY_SCHEDULED_APPOINTMENT_DATA_SET_DEFINITION_UUID, AppointmentDataSetDefinition.class);

        if (dsd == null || dsd.getId() == null) {
            throw new RuntimeException("Daily scheduled appointment data set definition not found");
        }

        // swap out the identifier column to show dossier number instead of primary identifier
        dsd.removeColumnDefinition("identifier");

        PatientIdentifierDataDefinition dd = new PatientIdentifierDataDefinition(null, paperRecordProperties.getPaperRecordIdentifierType());
        dd.setIncludeFirstNonNullOnly(true);
        dsd.addColumn("identifier", dd, "", new PropertyConverter(PatientIdentifier.class, "identifier"));

        dataSetDefinitionService.saveDefinition(dsd);
    }
}
