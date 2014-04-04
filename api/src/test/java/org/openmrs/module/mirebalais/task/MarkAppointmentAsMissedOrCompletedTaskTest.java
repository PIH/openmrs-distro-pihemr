package org.openmrs.module.mirebalais.task;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;

public class MarkAppointmentAsMissedOrCompletedTaskTest extends BaseModuleContextSensitiveTest {

    private AppointmentService appointmentService;

    @Before
    public void before() throws Exception {
        appointmentService = Context.getService(AppointmentService.class);
        executeDataSet("appointmentTestDataset.xml");
    }


    @Test
    public void shouldMarkOldAppointmentsAsMissedOrCompleted() {

        new MarkAppointmentsAsMissedOrCompletedTask().execute();

        assertThat(appointmentService.getAppointment(1).getStatus(), is(AppointmentStatus.MISSED));
        assertThat(appointmentService.getAppointment(2).getStatus(), is(AppointmentStatus.MISSED));
        assertThat(appointmentService.getAppointment(3).getStatus(), is(AppointmentStatus.COMPLETED));
        assertThat(appointmentService.getAppointment(4).getStatus(), is(AppointmentStatus.COMPLETED));
        assertThat(appointmentService.getAppointment(5).getStatus(), is(AppointmentStatus.CANCELLED));
        assertThat(appointmentService.getAppointment(6).getStatus(), is(AppointmentStatus.CANCELLED_AND_NEEDS_RESCHEDULE));
        assertThat(appointmentService.getAppointment(7).getStatus(), is(AppointmentStatus.COMPLETED));
        assertThat(appointmentService.getAppointment(8).getStatus(), is(AppointmentStatus.COMPLETED));

    }

    @Test
    public void shouldNotMarkNewAppointmentsAsMissedOrCompleted() {

        new MarkAppointmentsAsMissedOrCompletedTask().execute();

        assertThat(appointmentService.getAppointment(9).getStatus(), is(AppointmentStatus.SCHEDULED));
        assertThat(appointmentService.getAppointment(10).getStatus(), is(AppointmentStatus.WAITING));

    }
}
