package org.openmrs.module.mirebalais.task;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;

public class MarkAppointmentAsMissedOrCompletedTaskTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Autowired
    private TestDataManager testDataManager;

    @Before
    public void before() throws Exception {
        executeDataSet("appointmentTestDataset.xml");
    }


    @Test
    public void shouldMarkPastScheduledAppointmentsAsMissed() {

        new MarkAppointmentsAsMissedOrCompletedTask().execute();

        assertThat(appointmentService.getAppointment(1).getStatus(), is(AppointmentStatus.MISSED));
        assertThat(appointmentService.getAppointment(2).getStatus(), is(AppointmentStatus.MISSED));

        // status of other appointments should not be changed
        assertThat(appointmentService.getAppointment(3).getStatus(), is(AppointmentStatus.COMPLETED));
        assertThat(appointmentService.getAppointment(4).getStatus(), is(AppointmentStatus.INCONSULTATION));
        assertThat(appointmentService.getAppointment(5).getStatus(), is(AppointmentStatus.CANCELLED));
        assertThat(appointmentService.getAppointment(6).getStatus(), is(AppointmentStatus.CANCELLED_AND_NEEDS_RESCHEDULE));
        assertThat(appointmentService.getAppointment(7).getStatus(), is(AppointmentStatus.WALKIN));
        assertThat(appointmentService.getAppointment(8).getStatus(), is(AppointmentStatus.WAITING));

    }

    @Test
    public void shouldNotMarkFutureAppointmentsAsMissedOrCompleted() {

        new MarkAppointmentsAsMissedOrCompletedTask().execute();

        assertThat(appointmentService.getAppointment(9).getStatus(), is(AppointmentStatus.SCHEDULED));
        assertThat(appointmentService.getAppointment(10).getStatus(), is(AppointmentStatus.WAITING));

    }

    @Test
    public void shouldMarkActiveAppointmentsAsCompleteIfConsultAsPartOfVisit() {

        new MarkAppointmentsAsMissedOrCompletedTask().execute();

        // sanity check: status isn't changed by default, since there are no consultations associated with this visit
        assertThat(appointmentService.getAppointment(4).getStatus(), is(AppointmentStatus.INCONSULTATION));
        assertThat(appointmentService.getAppointment(7).getStatus(), is(AppointmentStatus.WALKIN));
        assertThat(appointmentService.getAppointment(8).getStatus(), is(AppointmentStatus.WAITING));

        // now add a visit to one appointment, and a visit with a consult to the other
        Appointment appt4 = appointmentService.getAppointment(4);
        Visit visit4 = testDataManager.visit()
                .patient(appt4.getPatient())
                .visitType(1)
                .started(new DateTime(2005, 1, 1, 0, 0, 0).toDate())
                .save();
        appt4.setVisit(visit4);
        appointmentService.saveAppointment(appt4);

        Appointment appt7 = appointmentService.getAppointment(7);
        Visit visit7 = testDataManager.visit()
                .patient(appt4.getPatient())
                .visitType(1)
                .started(new DateTime(2005, 1, 1, 0, 0, 0).toDate())
                .encounter(testDataManager.encounter()
                        .encounterDatetime(new DateTime(2005, 1, 1, 0, 0, 0).toDate())
                        .encounterType(emrApiProperties.getConsultEncounterType())
                        .get())
                .save();
        appt7.setVisit(visit7);
        appointmentService.saveAppointment(appt7);

        // run the task again
        new MarkAppointmentsAsMissedOrCompletedTask().execute();

        // should not be changed because associated visit did not have consult
        assertThat(appointmentService.getAppointment(4).getStatus(), is(AppointmentStatus.INCONSULTATION));

        // should be changed to COMPLETED since associated visit had consult
        assertThat(appointmentService.getAppointment(7).getStatus(), is(AppointmentStatus.COMPLETED));

        // should not be changed since no associated visit
        assertThat(appointmentService.getAppointment(8).getStatus(), is(AppointmentStatus.WAITING));

    }

}
