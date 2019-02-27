package org.openmrs.module.mirebalais.apploader.apps.patientregistration;

import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.registration.DemographicsConfigDescriptor;
import org.openmrs.module.registrationapp.model.Section;

public class SectionsSierraLeone extends SectionsDefault {

    private Config config;

    public SectionsSierraLeone(Config config) {
        super(config);
        this.config = config;
    }

    @Override
    public Section getDemographicsSection() {
        Section s = new Section();
        s.setId("demographics");
        s.setLabel("");
        DemographicsConfigDescriptor demsConfig = config.getRegistrationConfig().getDemographics();
        if (demsConfig != null) {
            if (demsConfig.getMothersName() != null) {
                s.addQuestion(getMothersNameQuestion());
            }
        }
        return s;
    }

}
