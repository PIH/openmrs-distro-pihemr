package org.openmrs.module.mirebalais.page.controller.checkin;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.MirebalaisGlobalProperties;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This controller handles prompting the user to confirm that they want to request a paper
 * record (or create a new one if necessary). The user is redirected here after filling out
 * the check-in form. It redirects the user back to the beginning of the check-in app or the
 * emergency check-in/registration app, depending on context.
 */
public class RequestRecordPageController {

    public void controller(@RequestParam("patientId") Patient patient,
                           @RequestParam(value = "redirectToEmergency", required = false) Boolean redirectToEmergency,  // hack to redirect back to the patient registration emergency workflow if that is where we've come from
                           PageModel pageModel,
                           UiSessionContext uiSessionContext) {


        boolean needToCreateRecord = true;

        String dossierType = MirebalaisGlobalProperties.PAPER_RECORD_IDENTIFIER_TYPE();
        PatientIdentifierType patientIdentifierTypeByUuid = Context.getPatientService().getPatientIdentifierTypeByUuid(dossierType);

        if(patientIdentifierTypeByUuid != null ){
            PatientIdentifier patientDossier = patient.getPatientIdentifier(patientIdentifierTypeByUuid);
            // only show the create paper record dialog if the patient does *not* have a dossier identifier,
            // and we are not currently at the central archives
            if(patientDossier != null && StringUtils.isNotBlank(patientDossier.getIdentifier()) ||
                    uiSessionContext.getSessionLocation().getUuid().equals(MirebalaisConstants.CENTRAL_ARCHIVES_LOCATION_UUID)){
                    needToCreateRecord = false;
            }
        }

        pageModel.addAttribute("patient", patient);
        pageModel.addAttribute("needToCreateRecord", needToCreateRecord);
        pageModel.addAttribute("redirectToEmergency", redirectToEmergency);

    }

}
