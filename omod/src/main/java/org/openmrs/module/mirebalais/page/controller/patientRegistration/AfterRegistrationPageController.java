package org.openmrs.module.mirebalais.page.controller.patientRegistration;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.AdministrativeConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CommonConcepts;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public class AfterRegistrationPageController {

    public String controller(@RequestParam("patientId") Patient patient,
                             @RequestParam("encounterId") Encounter encounter,
                             UiUtils ui) {

        String returnUrl = ui.pageLink("registrationapp", "findPatient", ObjectUtil.toMap("appId", CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION));

        if (idCardPrintingRequested(encounter)) {
            Integer locationId = encounter.getLocation().getLocationId();
            Map<String, Object> m = ObjectUtil.toMap("patientId", patient.getId(), "locationId", locationId, "returnUrl", returnUrl);
            return "redirect:" + ui.pageLink("mirebalais", "patientRegistration/printIdCard", m);
        }
        else {
            return "redirect:" + returnUrl;
        }
    }

    /**
     * @return true if the passed encounter has an observation indicating that a request was made to print an id card, false otherwise
     */
    protected boolean idCardPrintingRequested(Encounter encounter) {
        boolean ret = false;
        if (encounter != null) {
            for (Obs o : encounter.getAllObs()) {
                if (o.getConcept().getUuid().equals(AdministrativeConcepts.Concepts.ID_CARD_PRINTING_REQUESTED)) {
                    if (o.getValueCoded() != null && o.getValueCoded().getUuid().equals(CommonConcepts.Concepts.YES)) {
                        ret = true;
                    }
                }
            }
        }
        return ret;
    }
}
