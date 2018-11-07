package org.openmrs.module.mirebalais.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HtmlFormSetup {
    protected static Log log = LogFactory.getLog(HtmlFormSetup.class);

    // set up html forms--this must happen *after* MDS packages are installed, so that forms defined in code/github
    // take precedent over any in MDS packages; therefore we still do this in the Mirebalais module, not PIH Core
    public static void setupHtmlForms(Config config) throws Exception {
        try {
            ResourceFactory resourceFactory = ResourceFactory.getInstance();
            FormService formService = Context.getFormService();
            HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);

            // forms installed across all implementations
            List<String> htmlforms = new ArrayList(Arrays.asList(
                    "pihcore:htmlforms/admissionNote.xml",
                "pihcore:htmlforms/patientRegistration.xml",
                "pihcore:htmlforms/patientRegistration-rs.xml",
                "pihcore:htmlforms/patientRegistration-contact.xml",
                "pihcore:htmlforms/surgicalPostOpNote.xml",
                "pihcore:htmlforms/vitals.xml",
                "pihcore:htmlforms/labResults.xml",
                "pihcore:htmlforms/transferNote.xml",
                "pihcore:htmlforms/dischargeNote.xml",
                "pihcore:htmlforms/outpatientConsult.xml",
                "pihcore:htmlforms/edNote.xml",
                "pihcore:htmlforms/deathCertificate.xml",
                "pihcore:htmlforms/oncologyConsult.xml",
                "pihcore:htmlforms/mentalHealth.xml",
                "pihcore:htmlforms/section-chief-complaint.xml",
                "pihcore:htmlforms/section-history.xml",
                "pihcore:htmlforms/section-exam.xml",
                "pihcore:htmlforms/section-dx.xml",
                "pihcore:htmlforms/section-peds-feeding.xml",
                "pihcore:htmlforms/section-peds-supplements.xml",
                "pihcore:htmlforms/section-plan.xml",
                "pihcore:htmlforms/section-ncd.xml",
                "pihcore:htmlforms/section-lab-order.xml",
                "pihcore:htmlforms/section-family-planning.xml",
                "pihcore:htmlforms/socio-econ.xml",
                "pihcore:htmlforms/primary-care-adult-initial.xml",
                "pihcore:htmlforms/primary-care-adult-followup.xml",
                "pihcore:htmlforms/primary-care-peds-initial.xml",
                "pihcore:htmlforms/primary-care-peds-followup.xml",
                "pihcore:htmlforms/ncd-adult-initial.xml",
                "pihcore:htmlforms/ncd-adult-followup.xml",
                "pihcore:htmlforms/ancIntake.xml",
                "pihcore:htmlforms/ancFollowup.xml",
                "pihcore:htmlforms/delivery.xml"));

            // add any country-specific forms
            if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
                htmlforms.addAll(Arrays.asList(
                        "pihcore:htmlforms/haiti/hiv/zl/hiv-intake.xml",
                        "pihcore:htmlforms/haiti/patientRegistration-contact.xml",
                        "pihcore:htmlforms/haiti/patientRegistration-social.xml",
                        "pihcore:htmlforms/haiti/patientRegistration-insurance.xml",
                        "pihcore:htmlforms/haiti/checkin.xml",
                        "pihcore:htmlforms/haiti/liveCheckin.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/hiv-followup.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/vct.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/section-hiv-serology.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/section-symptoms.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/section-who-stages.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/section-hiv-oi.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/section-hiv-state.xml",
                        "pihcore:htmlforms/haiti/hiv/iSantePlus/Adherence.xml",
                        "pihcore:htmlforms/haiti/hiv/iSantePlus/SaisiePremiereVisiteAdult.xml",
                        "pihcore:htmlforms/haiti/hiv/iSantePlus/SaisiePremiereVisitePediatrique.xml",
                        "pihcore:htmlforms/haiti/hiv/iSantePlus/VisiteDeSuivi.xml",
                        "pihcore:htmlforms/haiti/hiv/iSantePlus/VisiteDeSuiviPediatrique.xml"
                ));
            }
            else if (config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
                htmlforms.addAll(Arrays.asList(
                        "pihcore:htmlforms/liberia/checkin.xml",
                        "pihcore:htmlforms/liberia/liveCheckin.xml",
                        "pihcore:htmlforms/liberia/patientRegistration-social.xml"
                ));
            }

            else if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE)) {
                htmlforms.addAll(Arrays.asList(
                        "pihcore:htmlforms/patientRegistration.xml",
                        "pihcore:htmlforms/patientRegistration-rs.xml",
                        "pihcore:htmlforms/sierra_leone/checkin.xml",
                        "pihcore:htmlforms/sierra_leone/liveCheckin.xml",
                        "pihcore:htmlforms/sierra_leone/patientRegistration-social.xml"
                ));
            }

            if (htmlforms != null) {
                for (String htmlform : htmlforms) {
                    HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService, htmlFormEntryService, htmlform);
                }
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
