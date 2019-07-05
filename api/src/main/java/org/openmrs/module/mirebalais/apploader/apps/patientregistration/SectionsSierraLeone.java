package org.openmrs.module.mirebalais.apploader.apps.patientregistration;

import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;

public class SectionsSierraLeone extends SectionsDefault {

    private Config config;

    public SectionsSierraLeone(Config config) {
        super(config);
        this.config = config;
    }

    public void addSections(RegistrationAppConfig c) {

        c.addSection(getDemographicsSection());
        c.addSection(getContactInfoSection());
        c.addSection(getSocialSection());
        c.addSection(getContactsSection(false));
        c.addSection(getIdCardPrintSection());

    }

}
