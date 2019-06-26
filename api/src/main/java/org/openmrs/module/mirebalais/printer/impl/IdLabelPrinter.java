package org.openmrs.module.mirebalais.printer.impl;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.mirebalais.printer.IdPrinter;
import org.openmrs.module.paperrecord.PaperRecordService;
import org.openmrs.module.paperrecord.template.DefaultZplPaperFormLabelTemplate;
import org.openmrs.module.printer.Printer;
import org.openmrs.module.printer.PrinterService;
import org.openmrs.module.printer.PrinterType;
import org.openmrs.module.printer.UnableToPrintException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Encapsulates the functionality needed to print a Label-based ID (currently used in Sierra Leone)
 */
@Component
public class IdLabelPrinter implements IdPrinter {

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Autowired
    MessageSourceService messageSourceService;

    @Autowired
    private PaperRecordService paperRecordService;

    @Autowired
    private PrinterService printerService;

    public boolean isAvailableAtLocation(Location location) {

        // First make sure that the passed location is associated with a medical record location
        try {
            paperRecordService.getMedicalRecordLocationAssociatedWith(location);
        }
        catch (Exception e) {
            return false;
        }

        // Then confirm that a printer is found at this location that is suitable
        Printer printer = printerService.getDefaultPrinter(location, PrinterType.LABEL);
        return printer != null;
    }

    public void print(Patient patient, Location location) throws UnableToPrintException {
        DefaultZplPaperFormLabelTemplate template = new DefaultZplPaperFormLabelTemplate();
        template.setEmrApiProperties(emrApiProperties);
        template.setMessageSourceService(messageSourceService);
        String data = template.generateLabel(patient, null);
        Printer printer = printerService.getDefaultPrinter(location, PrinterType.LABEL);

        if (printer == null) {
            throw new UnableToPrintException("No printer configured for location");
        }

        printerService.printViaSocket(data, printer, "UTF-8");
    }
}
