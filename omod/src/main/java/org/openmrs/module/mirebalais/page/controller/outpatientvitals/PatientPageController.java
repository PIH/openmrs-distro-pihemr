/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.mirebalais.page.controller.outpatientvitals;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.htmlform.EnterHtmlFormWithSimpleUiTask;
import org.openmrs.module.emrapi.PatientDomainWrapper;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class PatientPageController {

    public void controller(@RequestParam("patientId") Patient patient,
                           UiUtils ui,
                           EmrContext emrContext,
                           PageModel model,
                           @InjectBeans PatientDomainWrapper patientDomainWrapper,
                           @InjectBeans EnterHtmlFormWithSimpleUiTask enterFormTask) {

        patientDomainWrapper.setPatient(patient);

        enterFormTask.setFormDefinitionFromUiResource("mirebalais:htmlforms/vitals.xml");
        enterFormTask.setReturnUrl(ui.pageLink("mirebalais", "outpatientvitals/findPatient"));
        SimpleObject appHomepageBreadcrumb = SimpleObject.create("label", ui.message("mirebalais.outpatientVitals.title"), "link", ui.pageLink("mirebalais", "outpatientvitals/findPatient"));
        SimpleObject patientPageBreadcrumb = SimpleObject.create("label", patient.getFamilyName() + ", " + patient.getGivenName(), "link", ui.thisUrlWithContextPath());
        enterFormTask.setBreadcrumbOverride(ui.toJson(Arrays.asList(appHomepageBreadcrumb, patientPageBreadcrumb)));
        Form form = enterFormTask.getHtmlForm().getForm();

        List<Encounter> existingEncounters = new ArrayList<Encounter>();
        if (emrContext.getActiveVisitSummary() != null) {
            for (Encounter encounter : emrContext.getActiveVisitSummary().getVisit().getEncounters()) {
                if (!encounter.isVoided()
                        && form.equals(encounter.getForm())) {
                    existingEncounters.add(encounter);
                }
            }
        }

        model.addAttribute("existingEncounters", existingEncounters);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("enterFormUrl", enterFormTask.getUrl(emrContext));
    }
}
