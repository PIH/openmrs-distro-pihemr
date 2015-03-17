package org.openmrs.module.mirebalais.page.controller.visit;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.MissingRequiredParameterException;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class VisitPageController {

    public void get(@SpringBean("visitService") VisitService visitService,
                    @InjectBeans PatientDomainWrapper patientDomainWrapper,
                    @RequestParam(required = false, value = "patient") Patient patient,
                    @RequestParam(required = false, value = "visit") Visit visit,
                    PageModel model) {
        if (visit == null) {
            if (patient == null) {
                throw new MissingRequiredParameterException("patient or visit is required");
            }
            visit = visitService.getVisitsByPatient(patient).get(0);
        }
        if (patient == null) {
            patient = visit.getPatient();
        }

        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
    }

}
