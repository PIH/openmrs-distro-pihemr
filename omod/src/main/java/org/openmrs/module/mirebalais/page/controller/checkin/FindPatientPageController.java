package org.openmrs.module.mirebalais.page.controller.checkin;


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
                           UiUtils ui,
                           EmrContext emrContext,
                           PageModel model,
                           @InjectBeans PatientDomainWrapper patientDomainWrapper){

        boolean pullRecord = false;
        if(pullPaperRecord!=null && pullPaperRecord){
            pullRecord = true;
        }
        if(patient!=null){
            patientDomainWrapper.setPatient(patient);
        }else{
            patientDomainWrapper.setPatient(new Patient());
        }
        model.addAttribute("pullPaperRecord", pullRecord);
        model.addAttribute("patient", patientDomainWrapper);
    }
}
