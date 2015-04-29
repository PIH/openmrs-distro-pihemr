package org.openmrs.module.mirebalais.fragment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.mirebalais.printer.impl.ZlEmrIdCardPrinter;
import org.openmrs.module.mirebalaismetadata.constants.PatientIdentifierTypes;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller providing ajax-friendly methods for id cards
 */
public class IdCardFragmentController {

    protected final Log log = LogFactory.getLog(getClass());

    /**
     * This method will attempt to print a new id card for the given patient at the given location,
     * @return a SimpleObject containing a flag indicating success, and a message suitable for display
     */
    public SimpleObject printIdCard(UiUtils ui, UiSessionContext uiSessionContext,
                                    @RequestParam("patientId") Patient patient,
                                    @RequestParam(value = "locationId", required = false) Location location,
                                    @SpringBean ZlEmrIdCardPrinter printer) {

        if (location == null) {
            location = uiSessionContext.getSessionLocation();
        }

        StatusMessage status;

        try {
            printer.print(patient, location);
            status = new StatusMessage(true, ui.message("zl.registration.patient.idcard.successMessage", location.getName()));
        }
        catch (Exception e) {
            status = new StatusMessage(false, ui.message(e.getMessage()));
            log.warn("User " + uiSessionContext.getCurrentUser() + " unable to print ID card at location " + location, e);
        }

        return SimpleObject.fromObject(status, ui, "success", "message");
    }

    /**
     * This method takes in a patientId and an identifier and validates that this is a valid identifier for this patient
     * @return a SimpleObject containing a flag indicating success, and a message suitable for display
     */
    public SimpleObject recordSuccessfulPrintAttempt(UiUtils ui,
                                       @RequestParam("patientId") Patient patient,
                                       @RequestParam("identifier") String identifier) {

        StatusMessage status = new StatusMessage(false, ui.message("zl.registration.patient.idcard.invalidForPatient", identifier, ui.format(patient.getPersonName())));
        for (PatientIdentifier pi : patient.getIdentifiers()) {
            if (pi.getIdentifierType().getUuid().equals(PatientIdentifierTypes.ZL_EMR_ID.uuid())) {
                if (pi.getIdentifier().equals(identifier)) {
                    status = new StatusMessage(true, "");

                    // TODO: Record an observation here
                }
            }
        }
        return SimpleObject.fromObject(status, ui, "success", "message");
    }

    public void recordFailedPrintAttempt(UiUtils ui,
                                         @RequestParam("patientId") Patient patient,
                                         @RequestParam("returnUrl") String returnUrl) {

        // TODO: Record an observation here

    }

    /**
     * Simple bean that represents the model for the returned json
     */
    public static class StatusMessage {
        private boolean success;
        private String message;

        public StatusMessage(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}