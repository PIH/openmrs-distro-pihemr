package org.openmrs.module.mirebalais.page.controller.checkin;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.paperrecord.PaperRecord;
import org.openmrs.module.paperrecord.PaperRecordService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * This controller handles prompting the user to confirm that they want to request a paper
 * record (or create a new one if necessary). The user is redirected here after filling out
 * the check-in form. It redirects the user back to the beginning of the check-in app or the
 * emergency check-in/registration app, depending on context.
 */
public class RequestRecordPageController {

    static Predicate ANY_NOT_PENDING_CREATION = new Predicate() {
        @Override
        public boolean evaluate(Object o) {
            return !o.equals(PaperRecord.Status.PENDING_CREATION);
        }
    };

    public void controller(@RequestParam("patientId") Patient patient,
                           @RequestParam(value = "redirectToEmergency", required = false) Boolean redirectToEmergency,  // hack to redirect back to the patient registration emergency workflow if that is where we've come from
                           @SpringBean("paperRecordService") PaperRecordService paperRecordService,
                           PageModel pageModel,
                           UiSessionContext uiSessionContext) {

        Location currentLocation = uiSessionContext.getSessionLocation();
        List<PaperRecord> paperRecords = paperRecordService.getPaperRecords(patient, currentLocation);

        // only show the create paper record dialog if the patient does *not* have an existing record that is in some other state than pending creation
        // and we are not currently at the central archives
        boolean needToCreateRecord = !currentLocation.getUuid().equals(MirebalaisConstants.CENTRAL_ARCHIVES_LOCATION_UUID) &&
                (paperRecords == null || paperRecords.size() == 0 || !CollectionUtils.exists(paperRecords, ANY_NOT_PENDING_CREATION));

        pageModel.addAttribute("patient", patient);
        pageModel.addAttribute("needToCreateRecord", needToCreateRecord);
        pageModel.addAttribute("redirectToEmergency", redirectToEmergency);

    }

}
