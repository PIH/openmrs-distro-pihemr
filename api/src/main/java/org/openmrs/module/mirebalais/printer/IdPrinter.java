package org.openmrs.module.mirebalais.printer;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.printer.UnableToPrintException;

public interface IdPrinter {

    boolean isAvailableAtLocation(Location location);

    void print(Patient patient, Location location) throws UnableToPrintException;

}
