package org.openmrs.module.mirebalais.setup;

import org.apache.commons.lang.time.DateUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.utils.GeneralUtils;
import org.openmrs.module.paperrecord.CloseStaleCreateRequestsTask;
import org.openmrs.module.paperrecord.CloseStalePullRequestsTask;
import org.openmrs.module.paperrecord.PaperRecordConstants;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Date;

public class ArchivesSetup {

    public static void setupCloseStalePullRequestsTask() {

        SchedulerService schedulerService = Context.getSchedulerService();

        TaskDefinition task = schedulerService.getTaskByName(PaperRecordConstants.TASK_CLOSE_STALE_PULL_REQUESTS);

        if (task == null) {
            task = new TaskDefinition();
            task.setName(PaperRecordConstants.TASK_CLOSE_STALE_PULL_REQUESTS);
            task.setDescription(PaperRecordConstants.TASK_CLOSE_STALE_PULL_REQUESTS_DESCRIPTION);
            task.setTaskClass(CloseStalePullRequestsTask.class.getName());
            task.setStartTime(DateUtils.addMinutes(new Date(), 5));
            task.setRepeatInterval(new Long(3600));  // once an hour
            task.setStartOnStartup(true);
            try {
                schedulerService.scheduleTask(task);
            }
            catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule close stale pull requests task", e);
            }
        }
        else {
            boolean anyChanges = GeneralUtils.setPropertyIfDifferent(task, "description", PaperRecordConstants.TASK_CLOSE_STALE_PULL_REQUESTS_DESCRIPTION);
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "taskClass", CloseStalePullRequestsTask.class.getName());
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "repeatInterval", new Long(3600));
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "startOnStartup", true);
            if (anyChanges) {
                schedulerService.saveTask(task);
            }
            if (!task.getStarted()) {
                task.setStarted(true);
                try {
                    schedulerService.scheduleTask(task);
                }
                catch (SchedulerException e) {
                    throw new RuntimeException("Failed to schedule close stale pull requests task", e);
                }
            }
        }
    }

    public static void  setupCloseStaleCreateRequestsTask() {

        SchedulerService schedulerService = Context.getSchedulerService();

        TaskDefinition task = schedulerService.getTaskByName(PaperRecordConstants.TASK_CLOSE_STALE_CREATE_REQUESTS);

        if (task == null) {
            task = new TaskDefinition();
            task.setName(PaperRecordConstants.TASK_CLOSE_STALE_CREATE_REQUESTS);
            task.setDescription(PaperRecordConstants.TASK_CLOSE_STALE_CREATE_REQUESTS_DESCRIPTION);
            task.setTaskClass(CloseStaleCreateRequestsTask.class.getName());
            task.setStartTime(DateUtils.addMinutes(new Date(), 5));
            task.setRepeatInterval(new Long(3600));  // once an hour
            task.setStartOnStartup(true);
            try {
                schedulerService.scheduleTask(task);
            }
            catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule close stale create requests task", e);
            }
        }
        else {
            boolean anyChanges = GeneralUtils.setPropertyIfDifferent(task, "description", PaperRecordConstants.TASK_CLOSE_STALE_CREATE_REQUESTS_DESCRIPTION);
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "taskClass", CloseStaleCreateRequestsTask.class.getName());
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "repeatInterval", new Long(3600));
            anyChanges |= GeneralUtils.setPropertyIfDifferent(task, "startOnStartup", true);
            if (anyChanges) {
                schedulerService.saveTask(task);
            }
            if (!task.getStarted()) {
                task.setStarted(true);
                try {
                    schedulerService.scheduleTask(task);
                }
                catch (SchedulerException e) {
                    throw new RuntimeException("Failed to schedule close stale create requests task", e);
                }
            }
        }
    }
}
