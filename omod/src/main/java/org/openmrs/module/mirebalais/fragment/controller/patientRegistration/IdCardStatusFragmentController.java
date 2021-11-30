package org.openmrs.module.mirebalais.fragment.controller.patientRegistration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

/**
 * Controller providing ajax-friendly methods for id cards
 */
public class IdCardStatusFragmentController {

    protected final Log log = LogFactory.getLog(getClass());

    public static final String ID_CARD_PRINTING_SUCCESSFUL = "9d1b04df-ee77-11e4-a257-54ee7513a7ff";

    public void controller(FragmentModel model, @FragmentParam("patientId") Patient patient,
                           @FragmentParam("app") AppDescriptor app,
                           UiUtils ui) {

        model.addAttribute("app", app);
        model.addAttribute("status", getPrintHistory(ui, patient));
    }

    /**
     * This method takes in a patientId and returns information about their print history in an ajax-friendly format
     */
    public SimpleObject getPrintHistory(UiUtils ui, @RequestParam("patientId") Patient patient) {

        int numSuccessful = 0;
        int numFailed = 0;
        Date latestAttemptDate = null;
        Boolean latestAttemptSuccessful = null;

        ObsForPersonDataDefinition d = new ObsForPersonDataDefinition();
        d.setQuestion(HtmlFormEntryUtil.getConcept(ID_CARD_PRINTING_SUCCESSFUL));
        List<Obs> found = DataUtil.evaluateForPerson(d, patient, List.class);

        if (found != null) {
            for (Obs o : found) {
                boolean mostRecent = latestAttemptDate == null || latestAttemptDate.before(o.getObsDatetime());
                boolean successful = o.getValueCoded().getUuid().equals(PihEmrConfigConstants.CONCEPT_YES_UUID);

                if (mostRecent) {
                    latestAttemptDate = o.getObsDatetime();
                    latestAttemptSuccessful = successful;
                }

                if (successful) {
                    numSuccessful++;
                } else {
                    numFailed++;
                }
            }
        }

        return SimpleObject.create(
                "numSuccessful", numSuccessful,
                "numFailed", numFailed,
                "latestAttemptDate", ui.format(latestAttemptDate),
                "latestAttemptSuccessful", latestAttemptSuccessful
        );
    }
}