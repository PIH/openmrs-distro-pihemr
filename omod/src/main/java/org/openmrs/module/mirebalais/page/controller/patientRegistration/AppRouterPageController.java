package org.openmrs.module.mirebalais.page.controller.patientRegistration;

import org.openmrs.module.patientregistration.PatientRegistrationConstants;
import org.openmrs.module.patientregistration.PatientRegistrationUtil;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

public class AppRouterPageController {

    public String controller(HttpSession session,
                             @RequestParam("task") String taskName) {
        session.setAttribute(PatientRegistrationConstants.SESSION_REGISTRATION_TASK, taskName);
        return "redirect:/module/patientregistration/workflow/"+ taskName + "Task.form";
    }
}
