package org.openmrs.module.mirebalais.fragment.controller.patientRegistration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.mirebalais.printer.impl.ZlEmrIdCardPrinter;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.AdministrativeConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CommonConcepts;
import org.openmrs.module.pihcore.metadata.haiti.HaitiPatientIdentifierTypes;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

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
     * If it is valid, it will save an Observation to the patient record indicating that an id card was successfully printed
     * @return a SimpleObject containing a flag indicating success, and a message suitable for display
     */
    public SimpleObject recordSuccessfulPrintAttempt(UiUtils ui, UiSessionContext uiSessionContext,
                                       @RequestParam("patientId") Patient patient,
                                       @RequestParam("identifier") String identifier) {

        StatusMessage status = new StatusMessage(false, ui.message("zl.registration.patient.idcard.invalidForPatient", identifier, ui.format(patient.getPersonName())));
        for (PatientIdentifier pi : patient.getIdentifiers()) {
            if (pi.getIdentifierType().getUuid().equals(HaitiPatientIdentifierTypes.ZL_EMR_ID.uuid())) {
                if (pi.getIdentifier().equals(identifier)) {
                    status = new StatusMessage(true, "");
                    savePrintingStatusObs(patient, uiSessionContext.getSessionLocation(), true);
                }
            }
        }
        return SimpleObject.fromObject(status, ui, "success", "message");
    }

    /**
     * This method takes in a patientId saves an Observation to the patient record indicating that an id card was not successfully printed
     */
    public void recordFailedPrintAttempt(UiSessionContext uiSessionContext,
                                         @RequestParam("patientId") Patient patient) {

        savePrintingStatusObs(patient, uiSessionContext.getSessionLocation(), false);
    }

    /**
     * Saves an Obs indicating whether printing was successful or not, and returns this Obs
     * @return the created Obs
     */
    protected Obs savePrintingStatusObs(Patient patient, Location location, boolean status) {
        Obs o = new Obs();
        o.setPerson(patient);
        o.setConcept(MetadataUtils.existing(Concept.class, AdministrativeConcepts.Concepts.ID_CARD_PRINTING_SUCCESSFUL));
        String answerUuid = status ? CommonConcepts.Concepts.YES : CommonConcepts.Concepts.NO;
        o.setValueCoded(MetadataUtils.existing(Concept.class, answerUuid));
        o.setObsDatetime(new Date());
        o.setLocation(location);
        return Context.getObsService().saveObs(o, null);
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