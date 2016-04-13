package org.openmrs.module.mirebalais.reports;

import org.joda.time.DateTime;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.LocationService;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.mirebalaisreports.definitions.AppointmentsReportManager;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.module.pihcore.reporting.BaseReportTest;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SkipBaseSetup
public class AppointmentsReportManagerTest extends BaseReportTest {

    @Autowired
    private AppointmentsReportManager reportManager;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private PaperRecordProperties paperRecordProperties;

    @Test
    public void testAppointmentsExport() throws Exception {

        Patient patient = data.randomPatient()
                .identifier(emrApiProperties.getPrimaryIdentifierType(), "2AA00V", Metadata.lookup(MirebalaisLocations.MIREBALAIS_HOSPITAL))
                .identifier(paperRecordProperties.getPaperRecordIdentifierType(), "A000001", Metadata.lookup(MirebalaisLocations.MIREBALAIS_HOSPITAL))
                .personAttribute(Metadata.lookup(PersonAttributeTypes.TELEPHONE_NUMBER), "123-4567")
                .save();

        Date startDate = new DateTime(2014,1,1,9,0,0).toDate();
        Date endDate = new DateTime(2014,1,1,11,0,0).toDate();

        Location mirebalaisHospital = locationService.getLocationByUuid("a084f714-a536-473b-94e6-ec317b152b43");
        Provider scheduledProvider = data.randomProvider().save();

        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setName("Outpatient consult");
        appointmentType.setDuration(30);
        appointmentService.saveAppointmentType(appointmentType);

        AppointmentBlock block = new AppointmentBlock();
        block.setTypes(Collections.singleton(appointmentType));
        block.setLocation(mirebalaisHospital);
        block.setProvider(scheduledProvider);
        block.setStartDate(startDate);
        block.setEndDate(endDate);
        appointmentService.saveAppointmentBlock(block);

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setAppointmentBlock(block);
        timeSlot.setStartDate(startDate);
        timeSlot.setEndDate(endDate);
        appointmentService.saveTimeSlot(timeSlot);

        Appointment appointment = new Appointment();
        appointment.setAppointmentType(appointmentType);
        appointment.setPatient(patient);
        appointment.setTimeSlot(timeSlot);
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointment.setReason("Sick");
        appointment.setCancelReason("Provider sick");
        appointmentService.saveAppointment(appointment);

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.parseDate("2014-01-01", "yyyy-MM-dd"));
        context.addParameterValue("endDate", DateUtil.parseDate("2014-01-20", "yyyy-MM-dd"));

        ReportDefinition reportDefinition = reportManager.constructReportDefinition();
        ReportData reportData = reportDefinitionService.evaluate(reportDefinition, context);

        DataSet appointmentDataSet = reportData.getDataSets().get("appointments");
        DataSetRow row = appointmentDataSet.iterator().next();

        assertThat((String) row.getColumnValue("givenName"), is(patient.getGivenName()));
        assertThat((String) row.getColumnValue("familyName"), is(patient.getFamilyName()));
        assertThat((String) row.getColumnValue("zlEmrId"), is("2AA00V"));
        assertThat((String) row.getColumnValue("dossierNumber"), is("A000001"));
        assertThat((String) row.getColumnValue("telephoneNumber"), is("123-4567"));
        assertThat((String) row.getColumnValue("status"), is("appointmentschedulingui.scheduleAppointment.status.type.completed"));  // TODO: find out why this doesn't resolve locallization properly within tests
        assertThat((String) row.getColumnValue("reason"), is("Sick"));
        assertThat((String) row.getColumnValue("cancelReason"), is("Provider sick"));
        assertThat((String) row.getColumnValue("serviceType"), is("Outpatient consult"));
        assertThat((String) row.getColumnValue("location"), is("Mirebalais"));
        assertThat((String) row.getColumnValue("provider"), is(scheduledProvider.getName()));
        assertThat((String) row.getColumnValue("date"), is("01 Jan 2014"));
        assertThat((String) row.getColumnValue("startTime"), is("09:00 AM"));
        assertThat((String) row.getColumnValue("endTime"), is("11:00 AM"));
        assertThat((Boolean) row.getColumnValue("confidential"), is(false));
    }

}
