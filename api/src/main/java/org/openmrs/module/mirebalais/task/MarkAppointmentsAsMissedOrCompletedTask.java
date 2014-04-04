package org.openmrs.module.mirebalais.task;

import java.util.Arrays;
import java.util.Date;

import org.joda.time.DateTime;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * Custom task to update appointment statuses; looks at all the appointment that happened the previous day or earlier and
 * sets any with a StatusType of SCHEDULED to MISSED, and any with a StatusType of ACTIVE to COMPLETED
 */
public class MarkAppointmentsAsMissedOrCompletedTask extends AbstractTask {

    @Override
    public void execute() {

        AppointmentService appointmentService = Context.getService(AppointmentService.class);

        Date endOfYesterday = new DateTime().withTime(23,59,59,999).minusDays(1).toDate();

        for (Appointment appointment : appointmentService.getAppointmentsByConstraints(null, endOfYesterday, null, null, null, null,
                Appointment.AppointmentStatus.getAppointmentsStatusByTypes(Arrays.asList(Appointment.AppointmentStatusType.SCHEDULED)))) {
            appointment.setStatus(Appointment.AppointmentStatus.MISSED);
            appointmentService.saveAppointment(appointment);
        }

        for (Appointment appointment : appointmentService.getAppointmentsByConstraints(null, endOfYesterday, null, null, null, null,
                Appointment.AppointmentStatus.getAppointmentsStatusByTypes(Arrays.asList(Appointment.AppointmentStatusType.ACTIVE)))) {
            appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
            appointmentService.saveAppointment(appointment);
        }

    }
}
