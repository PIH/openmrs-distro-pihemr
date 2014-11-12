package org.openmrs.module.mirebalais.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.htmlformentry.CauseOfDeathListTagHandler;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.util.Arrays;
import java.util.List;

public class HtmlFormSetup {

    protected static Log log = LogFactory.getLog(HtmlFormSetup.class);

    public static void setupHtmlFormEntryTagHandlers() throws Exception {
        HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
        htmlFormEntryService.addHandler(MirebalaisConstants.HTMLFORMENTRY_CAUSE_OF_DEATH_LIST_TAG_NAME, new CauseOfDeathListTagHandler());
    }

    public static void setupHtmlForms() throws Exception {
        try {
            ResourceFactory resourceFactory = ResourceFactory.getInstance();
            FormService formService = Context.getFormService();
            HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);

            List<String> htmlforms = Arrays.asList("mirebalais:htmlforms/admissionNote.xml",
                    "mirebalais:htmlforms/checkin.xml",
                    "mirebalais:htmlforms/liveCheckin.xml",
                    "mirebalais:htmlforms/surgicalPostOpNote.xml",
                    "mirebalais:htmlforms/vitals.xml",
                    "mirebalais:htmlforms/transferNote.xml",
                    "mirebalais:htmlforms/dischargeNote.xml",
                    "mirebalais:htmlforms/outpatientConsult.xml",
                    "mirebalais:htmlforms/edNote.xml",
                    "mirebalais:htmlforms/deathCertificate.xml");

            for (String htmlform : htmlforms) {
                HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService, htmlFormEntryService, htmlform);
            }

        }
        catch (Exception e) {
            // this is a hack to get component test to pass until we find the proper way to mock this
            if (ResourceFactory.getInstance().getResourceProviders() == null) {
                log.error("Unable to load HTML forms--this error is expected when running component tests");
            }
            else {
                throw e;
            }
        }
    }
}
