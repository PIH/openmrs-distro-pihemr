package org.openmrs.module.mirebalais.fragment.controller.patientRegistration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.pihcore.printer.impl.ZlEmrIdCardPrinter;
import org.openmrs.module.paperrecord.UnableToPrintLabelException;
import org.openmrs.module.printer.UnableToPrintException;
import org.openmrs.module.printer.UnableToPrintViaSocketException;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

/**
  Used for the ID label overall registration action in Sierra Leone
 */

public class LabelFragmentController {

    private final Log log = LogFactory.getLog(getClass());

    public SimpleObject printLabel(UiUtils ui,
                                     @RequestParam("patientId") Patient patient,
                                     @RequestParam(value = "locationId", required = false) Location location,
                                     @SpringBean ZlEmrIdCardPrinter zlEmrIdCardPrinter,
                                     UiSessionContext uiSessionContext) throws UnableToPrintLabelException {
        try {

            if (location == null) {
                location = uiSessionContext.getSessionLocation();
            }

            zlEmrIdCardPrinter.print(patient, location);

            return SimpleObject.create("success", true, "message", "Label printed");

        }
        catch (UnableToPrintViaSocketException e) {
            log.warn("User " + uiSessionContext.getCurrentUser() + " unable to print at location "
                    + location, e);
            return SimpleObject.create("success", false, "message", "Unable to print label");
        }
        catch (UnableToPrintException e) {
            return SimpleObject.create("success", false, "message", e.getMessage());
        }
    }

}
