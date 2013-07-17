package org.openmrs.module.mirebalais.page.controller.checkin;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.htmlform.EnterHtmlFormWithSimpleUiTask;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.mirebalais.MirebalaisGlobalProperties;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientPageController {


    public void controller(@RequestParam("patientId") Patient patient,
                           @RequestParam(value="formUrl", required = false) String formUrl,
                           UiUtils ui,
                           EmrContext emrContext,
                           PageModel model,
                           @InjectBeans PatientDomainWrapper patientDomainWrapper,
                           @InjectBeans EnterHtmlFormWithSimpleUiTask enterFormTask) {

        patientDomainWrapper.setPatient(patient);
        String returnFormUrl = "mirebalais:htmlforms/checkin.xml";
        if(StringUtils.isNotBlank(formUrl)){
            returnFormUrl=formUrl;
        }
        enterFormTask.setFormDefinitionFromUiResource(returnFormUrl);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("patientId", patient.getId());
        boolean createDossierNumber = true;
        boolean pullPaperRecord = false;
        String dossierType = MirebalaisGlobalProperties.PAPER_RECORD_IDENTIFIER_TYPE();
        PatientIdentifierType patientIdentifierTypeByUuid = Context.getPatientService().getPatientIdentifierTypeByUuid(dossierType);
        if(patientIdentifierTypeByUuid != null ){
            PatientIdentifier patientDossier = patient.getPatientIdentifier(patientIdentifierTypeByUuid);
            if(patientDossier != null && StringUtils.isNotBlank(patientDossier.getIdentifier())){
                createDossierNumber = false;
                pullPaperRecord = true;
            }
        }
        params.put("createDossierNumber", createDossierNumber);
        params.put("pullPaperRecord", pullPaperRecord);
        params.put("formUrl", "'" + formUrl + "'");
        enterFormTask.setReturnUrl(ui.pageLink("mirebalais", "checkin/findPatient", params));
        SimpleObject appHomepageBreadcrumb = SimpleObject.create("label", ui.escapeJs(ui.message("mirebalais.checkin.title")),
                "link", ui.pageLink("mirebalais", "checkin/findPatient", SimpleObject.create("formUrl", "'" + formUrl + "'")));
        SimpleObject patientPageBreadcrumb = SimpleObject.create("label", ui.escapeJs(patient.getFamilyName()) + ", " + ui.escapeJs(patient.getGivenName()), "link", ui.thisUrlWithContextPath());
        enterFormTask.setBreadcrumbOverride(ui.toJson(Arrays.asList(appHomepageBreadcrumb, patientPageBreadcrumb)));
        Form form = enterFormTask.getHtmlForm().getForm();

        List<Encounter> existingEncounters = new ArrayList<Encounter>();
        if (emrContext.getActiveVisit() != null) {
            for (Encounter encounter : emrContext.getActiveVisit().getVisit().getEncounters()) {
                if (!encounter.isVoided()
                        && form.getEncounterType()!=null
                        && form.getEncounterType().equals(encounter.getEncounterType())) {
                    existingEncounters.add(encounter);
                }
            }
        }

        model.addAttribute("visit", emrContext.getActiveVisit() != null ? emrContext.getActiveVisit().getVisit() : null);
        model.addAttribute("existingEncounters", existingEncounters);
        model.addAttribute("enterFormUrl", enterFormTask.getUrl(emrContext));
        model.addAttribute("formUrl", "'" + formUrl + "'");
        model.addAttribute("patient", patientDomainWrapper);
    }
}
