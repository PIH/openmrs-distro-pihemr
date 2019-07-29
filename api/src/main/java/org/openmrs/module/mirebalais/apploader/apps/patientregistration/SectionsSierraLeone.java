package org.openmrs.module.mirebalais.apploader.apps.patientregistration;

import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.registrationapp.model.DropdownWidget;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.Question;
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
        //c.addSection(getEbolaScreeningSection());
        c.addSection(getSocialSection());
        c.addSection(getContactsSection(false));
        c.addSection(getIdCardPrintSection());

    }

    @Override
    public Question getOccupationQuestion() {
        Question q = new Question();
        q.setId("occupationLabel");
        q.setLegend("zl.registration.patient.occupation.label");
        q.setHeader("zl.registration.patient.occupation.question");

        Field f = new Field();
        f.setFormFieldName("obs.PIH:Occupation");
        f.setType("obs");

        DropdownWidget w = new DropdownWidget();

        // ordered alphabetically in English, with Unemployed and Other last
        w.getConfig().addOption("PIH:BREWER", "zl.registration.patient.occupation.brewer.label");
        w.getConfig().addOption("CIEL:162944", "zl.registration.patient.occupation.civilServant.label");
        w.getConfig().addOption("PIH:COMMERCE", "zl.registration.patient.occupation.commerce.label");
        w.getConfig().addOption("PIH:DRIVER", "zl.registration.patient.occupation.driver.label");
        w.getConfig().addOption("PIH:FACTORY WORKER", "zl.registration.patient.occupation.factoryWorker.label");
        w.getConfig().addOption("PIH:FARMER", "zl.registration.patient.occupation.farmer.label");
        w.getConfig().addOption("CIEL:159674", "zl.registration.patient.occupation.fisherman.label");
        w.getConfig().addOption("PIH:FRUIT OR VEGETABLE SELLER", "zl.registration.patient.occupation.fruitOrVegetableVendor.label");
        w.getConfig().addOption("PIH:HEALTH CARE WORKER", "zl.registration.patient.occupation.healthCareWorker.label");
        w.getConfig().addOption("PIH:1404", "zl.registration.patient.occupation.housework.label");
        w.getConfig().addOption("PIH:HOUSEWORK/FIELDWORK", "zl.registration.patient.occupation.houseworkFieldwork.label");
        w.getConfig().addOption("PIH:MANUAL LABORER", "zl.registration.patient.occupation.manualLaborer.label");
        w.getConfig().addOption("CIEL:162945", "zl.registration.patient.occupation.marketVendor.label");
        w.getConfig().addOption("PIH:MINER", "zl.registration.patient.occupation.miner.label");
        w.getConfig().addOption("PIH:PROFESSIONAL", "zl.registration.patient.occupation.professional.label");
        w.getConfig().addOption("PIH:RETIRED", "zl.registration.patient.occupation.retired.label");
        w.getConfig().addOption("PIH:SHEPHERD", "zl.registration.patient.occupation.shepherd.label");
        w.getConfig().addOption("PIH:SHOP OWNER", "zl.registration.patient.occupation.shopOwner.label");
        w.getConfig().addOption("PIH:STUDENT", "zl.registration.patient.occupation.student.label");
        w.getConfig().addOption("PIH:Teacher", "zl.registration.patient.occupation.teacher.label");
        w.getConfig().addOption("PIH:UNEMPLOYED", "zl.registration.patient.occupation.unemployed.label");
        w.getConfig().addOption("PIH:OTHER NON-CODED", "zl.registration.patient.occupation.other.label");

        w.getConfig().setExpanded(true);
        f.setWidget(toObjectNode(w));
        q.addField(f);

        return q;
    }

}
