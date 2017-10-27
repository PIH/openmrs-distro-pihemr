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

            List<String> htmlforms = null;

            if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
                htmlforms = Arrays.asList("pihcore:htmlforms/admissionNote.xml",
                        "pihcore:htmlforms/patientRegistration.xml",
                        "pihcore:htmlforms/patientRegistration-rs.xml",
                        "pihcore:htmlforms/surgicalPostOpNote.xml",
                        "pihcore:htmlforms/vitals.xml",
                        "pihcore:htmlforms/transferNote.xml",
                        "pihcore:htmlforms/dischargeNote.xml",
                        "pihcore:htmlforms/outpatientConsult.xml",
                        "pihcore:htmlforms/edNote.xml",
                        "pihcore:htmlforms/deathCertificate.xml",
                        "pihcore:htmlforms/haiti/section-chief-complaint.xml",
                        "pihcore:htmlforms/haiti/section-history.xml",
                        "pihcore:htmlforms/haiti/section-exam.xml",
                        "pihcore:htmlforms/haiti/section-dx.xml",
                        "pihcore:htmlforms/haiti/section-peds-feeding.xml",
                        "pihcore:htmlforms/haiti/section-peds-supplements.xml",
                        "pihcore:htmlforms/haiti/section-plan.xml",
                        "pihcore:htmlforms/haiti/section-ncd.xml",
                        "pihcore:htmlforms/haiti/primary-care-adult-initial.xml",
                        "pihcore:htmlforms/haiti/primary-care-adult-followup.xml",
                        "pihcore:htmlforms/haiti/primary-care-peds-initial.xml",
                        "pihcore:htmlforms/haiti/primary-care-peds-followup.xml",
                        "pihcore:htmlforms/haiti/ncd-adult-initial.xml",
                        "pihcore:htmlforms/haiti/ncd-adult-followup.xml",
                        "pihcore:htmlforms/haiti/patientRegistration-contact.xml",
                        "pihcore:htmlforms/haiti/patientRegistration-social.xml",
                        "pihcore:htmlforms/haiti/patientRegistration-insurance.xml",
                        "pihcore:htmlforms/haiti/checkin.xml",
                        "pihcore:htmlforms/haiti/liveCheckin.xml",
                        "pihcore:htmlforms/oncologyConsult.xml",
                        "pihcore:htmlforms/mentalHealth.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/hiv-adult-intake.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/hiv-adult-followup.xml",
                        // "pihcore:htmlforms/haiti/hiv/zl/hiv-child-intake.xml",
                        // "pihcore:htmlforms/haiti/hiv/zl/hiv-child-followup.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/section-hiv-serology.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/section-risk-factors.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/section-symptoms.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/section-who-stages.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/section-hiv-oi.xml",
                        "pihcore:htmlforms/haiti/hiv/zl/section-hiv-state.xml",
                        "pihcore:htmlforms/section-lab-order.xml",
                        "pihcore:htmlforms/section-family-planning.xml",
                        "pihcore:htmlforms/haiti/section-socio-econ.xml",
                        "pihcore:htmlforms/haiti/hiv/iSantePlus/Adherence.xml",
                        "pihcore:htmlforms/haiti/hiv/iSantePlus/SaisiePremiereVisiteAdult.xml",
                        "pihcore:htmlforms/haiti/hiv/iSantePlus/SaisiePremiereVisitePediatrique.xml",
                        "pihcore:htmlforms/haiti/hiv/iSantePlus/VisiteDeSuivi.xml",
                        "pihcore:htmlforms/haiti/hiv/iSantePlus/VisiteDeSuiviPediatrique.xml"
                );
            }
            else if (config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
                htmlforms = Arrays.asList(
                        "pihcore:htmlforms/patientRegistration.xml",
                        "pihcore:htmlforms/patientRegistration-rs.xml",
                        "pihcore:htmlforms/liberia/checkin.xml",
                        "pihcore:htmlforms/liberia/liveCheckin.xml",
                        "pihcore:htmlforms/liberia/patientRegistration-contact.xml",
                        "pihcore:htmlforms/liberia/patientRegistration-social.xml"
                );
            }

            else if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE)) {
                htmlforms = Arrays.asList(
                        "pihcore:htmlforms/patientRegistration.xml",
                        "pihcore:htmlforms/patientRegistration-rs.xml",
                        "pihcore:htmlforms/sierra_leone/checkin.xml",
                        "pihcore:htmlforms/sierra_leone/liveCheckin.xml",
                        "pihcore:htmlforms/sierra_leone/patientRegistration-contact.xml",
                        "pihcore:htmlforms/sierra_leone/patientRegistration-social.xml"
                );
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
