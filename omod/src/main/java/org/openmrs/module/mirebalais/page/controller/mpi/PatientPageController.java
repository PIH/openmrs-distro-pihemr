package org.openmrs.module.mirebalais.page.controller.mpi;

import org.openmrs.Patient;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class PatientPageController {

    public void controller(
            @RequestParam("patientId") Patient patient,
            UiUtils ui,
            EmrContext emrContext,
            PageModel model,
            @InjectBeans PatientDomainWrapper patientDomainWrapper){

        patientDomainWrapper.setPatient(patient);
        SimpleObject appHomepageBreadcrumb = SimpleObject.create("label", ui.message("mirebalais.mpi.title"), "link", ui.pageLink("mirebalais", "mpi/findPatient"));
        SimpleObject patientPageBreadcrumb = SimpleObject.create("label", patient.getFamilyName() + ", " + patient.getGivenName(), "link", ui.thisUrlWithContextPath());
        model.addAttribute("patient", patientDomainWrapper);
    }

}
