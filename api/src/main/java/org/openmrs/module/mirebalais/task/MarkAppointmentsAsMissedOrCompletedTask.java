package org.openmrs.module.mirebalais.task;

import org.joda.time.DateTime;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.Arrays;
import java.util.Date;

/**
 * Custom task to update appointment statuses; looks at all the appointments that happened the previous day or earlier and
 * 1) sets any with a StatusType of SCHEDULED to MISSED, and 2) sets any with a StatusType of ACTIVE to COMPLETED *if* that appointment
 * has a visit with one or more consult encounters associated with it
 */
public class MarkAppointmentsAsMissedOrCompletedTask extends AbstractTask {

    @Override
    public void execute() {

        AppointmentService appointmentService = Context.getService(AppointmentService.class);

        AdtService adtService = Context.getService(AdtService.class);

        Date endOfYesterday = new DateTime().withTime(23,59,59,999).minusDays(1).toDate();

        for (Appointment appointment : appointmentService.getAppointmentsByConstraints(null, endOfYesterday, null, null, null, null,
                Appointment.AppointmentStatus.getAppointmentsStatusByTypes(Arrays.asList(Appointment.AppointmentStatusType.SCHEDULED)))) {
            appointment.setStatus(Appointment.AppointmentStatus.MISSED);
            appointmentService.saveAppointment(appointment);
        }

        for (Appointment appointment : appointmentService.getAppointmentsByConstraints(null, endOfYesterday, null, null, null, null,
                Appointment.AppointmentStatus.getAppointmentsStatusByTypes(Arrays.asList(Appointment.AppointmentStatusType.ACTIVE)))) {

            if (appointment.getVisit() != null && adtService.wrap(appointment.getVisit()).hasConsultEncounter()) {
                appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
                appointmentService.saveAppointment(appointment);
            }
        }

    }
}
