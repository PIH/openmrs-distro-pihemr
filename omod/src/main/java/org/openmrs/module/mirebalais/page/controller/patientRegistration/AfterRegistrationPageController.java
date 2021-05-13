package org.openmrs.module.mirebalais.page.controller.patientRegistration;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants;
import org.openmrs.module.pihcore.LiberiaConfigConstants;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.AdministrativeConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CommonConcepts;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public class AfterRegistrationPageController {

    public String controller(@SpringBean Config config,
                             @RequestParam("patientId") Patient patient,
                             @RequestParam("encounterId") Encounter encounter,
                             UiUtils ui) {

        String returnUrl = ui.pageLink("registrationapp", "findPatient", ObjectUtil.toMap("appId", CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION));

        if (config.getCountry() == ConfigDescriptor.Country.LIBERIA) {
            String redirectUrl = getRedirectUrlForLiberia(config, patient);
            if (redirectUrl == null) {
                return "redirect:" + returnUrl;
            } else {
                redirectUrl += "&returnUrl=" + returnUrl;
                return "redirect:" + redirectUrl;
            }
        }

        if (idCardPrintingRequested(encounter)) {
            Integer locationId = encounter.getLocation().getLocationId();
            Map<String, Object> m = ObjectUtil.toMap("patientId", patient.getId(), "locationId", locationId, "returnUrl", returnUrl);
            return "redirect:" + ui.pageLink("mirebalais", "patientRegistration/printIdCard", m);
        } else {
            return "redirect:" + returnUrl;
        }
    }

    /**
     * Get the next URL based on whether the registration is new patient or existing-on-paper-records patient.
     * @param config
     * @param patient
     * @return
     */
    private String getRedirectUrlForLiberia(Config config, Patient patient) {
        PatientIdentifier pi = patient.getPatientIdentifier(LiberiaConfigConstants.PATIENTIDENTIFIERTYPE_LIBERIAEMRID_UUID);
        if (pi == null) {
            return null;
        }

        String redirectUrl = "htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId=" + patient.getUuid() + "&definitionUiResource=";

        String liberiaEmrId = pi.getIdentifier();
        if (liberiaEmrId != null && liberiaEmrId.startsWith(config.getPrimaryIdentifierPrefix())) {
            redirectUrl += PihCoreUtil.getFormResource("liveCheckin.xml") + "&createVisit=true";
        } else {
            redirectUrl += PihCoreUtil.getFormResource("checkin.xml");
        }
        return redirectUrl;
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
