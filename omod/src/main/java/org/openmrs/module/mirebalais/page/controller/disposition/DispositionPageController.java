package org.openmrs.module.mirebalais.page.controller.disposition;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.concept.EmrConceptService;
import org.openmrs.module.emrapi.disposition.DispositionDescriptor;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DispositionPageController {

    private static final Map<String,String> dispositionToNextFormMap =  Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("PIH:ADMIT TO HOSPITAL", "admissionNote");
        put("PIH:DISCHARGED", "dischargeNote");
        put("PIH:Left without seeing a clinician", "dischargeNote");
        put("PIH:Departed without medical discharge", "dischargeNote");
        put("PIH:Transfer out of hospital", "dischargeNote");
        put("PIH:Transfer within hospital", "transferNote");
        put("PIH:DEATH", "deathNote");
    }});

    public String controller(@RequestParam(value = "encounterId", required = false) Encounter encounter,
                             @RequestParam(value = "patientId", required = false) String patientId,
                             @RequestParam(value = "visitId", required = false) String visitId,
                                @SpringBean("emrConceptService") EmrConceptService emrConceptService,
                                EmrApiProperties emrApiProperties,
                                UiUtils ui) throws IOException {

        DispositionDescriptor dispositionDescriptor = emrApiProperties.getDispositionDescriptor();

        String nextForm = null;
        Obs dispositionObs = null;

        // note that if there are multiple dispositions, this just takes the first
        if (encounter != null) {
            for (Obs candidate : encounter.getAllObs()) {
                if (dispositionDescriptor.isDisposition(candidate)) {
                    dispositionObs = dispositionDescriptor.getDispositionObs(candidate);
                }
            }
        }

        if (dispositionObs != null) {
           for (Map.Entry<String,String> entry : dispositionToNextFormMap.entrySet()) {
                if (emrConceptService.getConcept(entry.getKey()).equals(dispositionObs.getValueCoded())) {
                    nextForm = entry.getValue();
                }
           }
        }

        // if the next form is set to be the admission form, make sure the patient has not already been admitted
        if ("admissionNote".equals(nextForm) &&
                (encounter.getVisit() == null || new VisitDomainWrapper(encounter.getVisit(), emrApiProperties).isAdmitted())) {
            nextForm = null;
        }

        // if the next form is set to be the discharge form, make sure the patient has been admitted
        if ("dischargeNote".equals(nextForm) &&
                (encounter.getVisit() == null || !new VisitDomainWrapper(encounter.getVisit(), emrApiProperties).isAdmitted())) {
            nextForm = null;
        }

        Map<String,Object> params = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(patientId)) {
            params.put("patientId", patientId);
        }
        if (StringUtils.isNotBlank(visitId)) {
            params.put("visitId", visitId);
        }

        if (nextForm != null) {
            params.put("definitionUiResource", "mirebalais:htmlforms/" + nextForm + ".xml");
            return "redirect:" + ui.pageLinkWithoutContextPath("htmlformentryui", "htmlform/enterHtmlFormWithStandardUi", params) ;
        }
        else {
            return "redirect:" + ui.pageLinkWithoutContextPath("coreapps", "patientdashboard/patientDashboard", params) ;
        }

   }

}
