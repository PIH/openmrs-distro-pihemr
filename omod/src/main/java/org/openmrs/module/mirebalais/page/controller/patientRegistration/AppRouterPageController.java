package org.openmrs.module.mirebalais.page.controller.patientRegistration;

import org.openmrs.module.patientregistration.PatientRegistrationConstants;
import org.openmrs.module.patientregistration.PatientRegistrationUtil;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

public class AppRouterPageController {

    public String controller(HttpSession session,
                             @RequestParam("task") String taskName,
                             @RequestParam(value = "testPatient", defaultValue = "false") boolean testPatient   ) {
        session.setAttribute(PatientRegistrationConstants.SESSION_REGISTRATION_TASK, taskName);

        String url = "redirect:/module/patientregistration/workflow/" + taskName + "Task.form";

        if (testPatient){
            url += "?testPatient=true";
        }


        return url;
    }
}
