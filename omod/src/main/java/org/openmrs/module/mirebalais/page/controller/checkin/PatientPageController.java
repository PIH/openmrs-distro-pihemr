package org.openmrs.module.mirebalais.page.controller.checkin;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.htmlform.EnterHtmlFormWithSimpleUiTask;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.paperrecord.PaperRecord;
import org.openmrs.module.paperrecord.PaperRecordService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientPageController {

    static Predicate ANY_NOT_PENDING_CREATION = new Predicate() {
        @Override
        public boolean evaluate(Object o) {
            return !o.equals(PaperRecord.Status.PENDING_CREATION);
        }
    };

    private static String FORM_URL = "mirebalais:htmlforms/liveCheckin.xml";

    public void controller(@RequestParam("patientId") Patient patient,
                           @RequestParam(value="formUrl", required = false) String formUrl,
                           UiUtils ui,
                           EmrContext emrContext,
                           UiSessionContext uiSessionContext,
                           PageModel model,
                           @SpringBean("paperRecordService") PaperRecordService paperRecordService,
                           @InjectBeans PatientDomainWrapper patientDomainWrapper,
                           @InjectBeans EnterHtmlFormWithSimpleUiTask enterFormTask) {

        patientDomainWrapper.setPatient(patient);
        enterFormTask.setFormDefinitionFromUiResource(FORM_URL);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("patientId", patient.getId());

        Location currentLocation = uiSessionContext.getSessionLocation();
        List<PaperRecord> paperRecords = paperRecordService.getPaperRecords(patient, currentLocation);

        // only show the create paper record dialog if the patient does *not* have an existing record
        // and we are not currently at the central archives
        boolean createPaperRecord = !currentLocation.getUuid().equals(MirebalaisConstants.CENTRAL_ARCHIVES_LOCATION_UUID) &&
                (paperRecords == null || paperRecords.size() == 0 || !CollectionUtils.exists(paperRecords, ANY_NOT_PENDING_CREATION));

        params.put("createPaperRecord", createPaperRecord);
        params.put("pullPaperRecord", !createPaperRecord);
        enterFormTask.setReturnUrl(ui.pageLink("mirebalais", "checkin/findPatient", params));
        SimpleObject appHomepageBreadcrumb = SimpleObject.create("label", ui.escapeJs(ui.message("mirebalais.checkin.title")),
                "link", ui.pageLink("mirebalais", "checkin/findPatient"));
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
        model.addAttribute("patient", patientDomainWrapper);
    }
}
