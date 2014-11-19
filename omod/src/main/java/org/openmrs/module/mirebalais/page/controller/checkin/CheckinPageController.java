package org.openmrs.module.mirebalais.page.controller.checkin;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Check-In app is an implementation of the core apps find patient app
 * After the user finds a patient he/she is redirected to this page, which
 * displays a "confirm this is the right patient" question, as well as the
 * previous check-ins for the patient. After the user confirms that this is the
 * right patient, it redirects to the check form (liveCheckin.xml).
 */
public class CheckinPageController {

    public void controller(@RequestParam("patientId") Patient patient,
                           UiUtils ui,
                           UiSessionContext uiSessionContext,
                           PageModel model,
                           EmrApiProperties emrApiProperties,
                           @InjectBeans PatientDomainWrapper patientDomainWrapper) {

        patientDomainWrapper.setPatient(patient);
        VisitDomainWrapper activeVisit = patientDomainWrapper.getActiveVisit(uiSessionContext.getSessionLocation());

        List<Encounter> existingEncounters = new ArrayList<Encounter>();
        if (activeVisit != null) {
            for (Encounter encounter : activeVisit.getVisit().getEncounters()) {
                if (!encounter.isVoided()
                        && emrApiProperties.getCheckInEncounterType().equals(encounter.getEncounterType())) {
                    existingEncounters.add(encounter);
                }
            }
        }

        SimpleObject appHomepageBreadcrumb = SimpleObject.create("label", ui.escapeJs(ui.message("mirebalais.checkin.title"))) ;
        SimpleObject patientPageBreadcrumb = SimpleObject.create("label", ui.escapeJs(patient.getFamilyName()) + ", " + ui.escapeJs(patient.getGivenName()), "link", ui.thisUrlWithContextPath());
        model.addAttribute("breadcrumbOverride", ui.toJson(Arrays.asList(appHomepageBreadcrumb, patientPageBreadcrumb)));
        model.addAttribute("activeVisit", activeVisit);
        model.addAttribute("existingEncounters", existingEncounters);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("appName", CustomAppLoaderConstants.Apps.CHECK_IN);
    }
}
