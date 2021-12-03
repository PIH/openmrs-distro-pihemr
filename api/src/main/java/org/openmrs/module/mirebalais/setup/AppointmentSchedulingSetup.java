package org.openmrs.module.mirebalais.setup;

import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.reporting.dataset.definition.AppointmentDataSetDefinition;
import org.openmrs.module.appointmentschedulingui.AppointmentSchedulingUIConstants;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;

public class AppointmentSchedulingSetup {

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
