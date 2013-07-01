package org.openmrs.module.mirebalais.page.controller.checkin;


import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class FindPatientPageController {

    public void controller(@RequestParam(value= "patientId", required = false) Patient patient,
                           @RequestParam(value = "pullPaperRecord", required = false) Boolean pullPaperRecord,
                           @RequestParam(value = "createDossierNumber", required = false) Boolean createDossierNumber,
                           @RequestParam(value="formUrl", required = false) String formUrl,
                           UiUtils ui,
                           EmrContext emrContext,
                           PageModel model,
                           @InjectBeans PatientDomainWrapper patientDomainWrapper){

        boolean pullRecord = false;
        if(pullPaperRecord!=null && pullPaperRecord){
            pullRecord = true;
        }
        boolean createDossier = false;
        if(createDossierNumber!=null && createDossierNumber){
            createDossier = true;
        }
        if(patient!=null){
            patientDomainWrapper.setPatient(patient);
        }else{
            patientDomainWrapper.setPatient(new Patient());
        }
        model.addAttribute("pullPaperRecord", pullRecord);
        model.addAttribute("createDossierNumber", createDossier);
        model.addAttribute("patient", patientDomainWrapper);
        if(StringUtils.isNotBlank(formUrl)){
            model.addAttribute("formUrl", formUrl);
        }else{
            model.addAttribute("formUrl", "");
        }
    }
}
