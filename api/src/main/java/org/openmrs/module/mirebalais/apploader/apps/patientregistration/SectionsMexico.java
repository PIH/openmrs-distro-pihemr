package org.openmrs.module.mirebalais.apploader.apps.patientregistration;

import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.registration.DemographicsConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.SocialConfigDescriptor;
import org.openmrs.module.registrationapp.model.DropdownWidget;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;
import org.openmrs.module.registrationapp.model.Section;

import java.util.HashMap;
import java.util.Map;

public class SectionsMexico extends SectionsDefault {

    private Config config;

    public SectionsMexico(Config config) {
        super(config);
        this.config = config;
    }

    @Override
    public void addSections(RegistrationAppConfig c) {
        c.addSection(getDemographicsSection());
        c.addSection(getContactInfoSection());
        c.addSection(getInsuranceSection());
        c.addSection(getSocialSection());
        c.addSection(getRelationshipsSection());
        c.addSection(getContactsSection(false));
    }

    private Section getInsuranceSection() {
        Section s = new Section();
        s.setId("insurance");
        s.setLabel("zl.registration.patient.insurance.label");
        s.addQuestion(getProsperaInfo());
        s.addQuestion(getMexicoInsurance());
        return s;
    }

    private Question getProsperaInfo() {
        Question q = new Question();
        q.setId("prosperaLabel");
        q.setLegend("zl.registration.patient.insurance.prospera.label");

        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:Prospera Construct.obs.PIH:Has Prospera");
            f.setLabel("zl.registration.patient.insurance.hasProspera.label");
            f.setType("obsgroup");
            f.setWidget(getYesNoDropdownWidget());
            q.addField(f);
        }
        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:Prospera Construct.obs.PIH:Insurance policy number");
            f.setLabel("zl.registration.patient.insurance.prosperaNumber.label");
            f.setType("obsgroup");
            f.setWidget(getTextFieldWidget(30));
            q.addField(f);
        }
        return q;
    }

    private Question getMexicoInsurance() {
        Question q = new Question();
        q.setId("insuranceLabel");
        q.setLegend("zl.registration.patient.insurance.insuranceName.label");

        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:Insurance CONSTRUCT.obs.PIH:Mexico Insurance Coded");
            f.setLabel("zl.registration.patient.insurance.insuranceName.question");
            f.setType("obsgroup");
            f.setWidget(getTextFieldWidget(30));

            DropdownWidget w = new DropdownWidget();
            w.getConfig().setExpanded(true);
            w.getConfig().addOption("PIH:Seguro Popular","zl.registration.patient.insurance.seguro_popular.label");
            w.getConfig().addOption("PIH:Mexican Social Security Institute", "zl.registration.patient.insurance.imss.label");
            w.getConfig().addOption("PIH:Institute of Social Security of Workers of the State of Chiapas", "zl.registration.patient.insurance.isstech.label");
            w.getConfig().addOption("PIH:OTHER", "zl.registration.patient.insurance.other.label");
            w.getConfig().addOption("PIH:NONE", "zl.registration.patient.insurance.none.label");

            f.setWidget(toObjectNode(w));
            q.addField(f);
        }
        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:Insurance CONSTRUCT.obs.PIH:Insurance policy number");
            f.setLabel("zl.registration.patient.insurance.insuranceNumber.label");
            f.setType("obsgroup");
            f.setWidget(getTextFieldWidget(30));
            q.addField(f);
        }
        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:Insurance CONSTRUCT.obs.PIH:Insurance company name (text)");
            f.setLabel("zl.registration.patient.insurance.insuranceNameOther.label");
            f.setType("obsgroup");
            f.setWidget(getTextFieldWidget(30));
            q.addField(f);
        }
        return q;
    }

    @Override
    public Section getSocialSection() {
        Section s = new Section();
        s.setId("social");
        s.setLabel("zl.registration.patient.social.label");
        SocialConfigDescriptor socConfig = config.getRegistrationConfig().getSocial();
        if (socConfig != null) {
            if (socConfig.getIsImmigrant() != null) {
                s.addQuestion(getIsImmigrantQuestion());
            }
            if (socConfig.getIsIndigenous() != null) {
                s.addQuestion(getIsIndigenousQuestion());
            }
            if (socConfig.getActiveCasefinding() != null) {
                s.addQuestion(getActiveCasefindingQuestion());
            }
        }
        s.addQuestion(getCivilStatusQuestion());
        s.addQuestion(getOccupationQuestion());
        return s;
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

        // ordered alphabetically in Spanish, with Unemployed and Other last
        w.getConfig().addOption("PIH:SHEPHERD", "zl.registration.patient.occupation.shepherd.label");
        w.getConfig().addOption("PIH:DRIVER", "zl.registration.patient.occupation.driver.label");
        w.getConfig().addOption("PIH:COMMERCE", "zl.registration.patient.occupation.commerce.label");
        w.getConfig().addOption("PIH:FARMER", "zl.registration.patient.occupation.farmer.label");
        w.getConfig().addOption("CIEL:162944", "zl.registration.patient.occupation.civilServant.label");
        w.getConfig().addOption("PIH:MANUAL LABORER", "zl.registration.patient.occupation.manualLaborer.label");
        w.getConfig().addOption("PIH:HEALTH CARE WORKER", "zl.registration.patient.occupation.healthCareWorker.label");
        w.getConfig().addOption("PIH:MINER", "zl.registration.patient.occupation.miner.label");
        w.getConfig().addOption("PIH:1404", "zl.registration.patient.occupation.housework.label");
        w.getConfig().addOption("PIH:HOUSEWORK/FIELDWORK", "zl.registration.patient.occupation.houseworkFieldwork.label");
        w.getConfig().addOption("PIH:FACTORY WORKER", "zl.registration.patient.occupation.factoryWorker.label");
        w.getConfig().addOption("PIH:Teacher", "zl.registration.patient.occupation.teacher.label");
        w.getConfig().addOption("PIH:PROFESSIONAL", "zl.registration.patient.occupation.professional.label");
        w.getConfig().addOption("PIH:SHOP OWNER", "zl.registration.patient.occupation.shopOwner.label");
        w.getConfig().addOption("CIEL:159674", "zl.registration.patient.occupation.fisherman.label");
        w.getConfig().addOption("PIH:RETIRED", "zl.registration.patient.occupation.retired.label");
        w.getConfig().addOption("PIH:FRUIT OR VEGETABLE SELLER", "zl.registration.patient.occupation.fruitOrVegetableVendor.label");
        w.getConfig().addOption("CIEL:162945", "zl.registration.patient.occupation.marketVendor.label");
        w.getConfig().addOption("PIH:STUDENT", "zl.registration.patient.occupation.student.label");
        w.getConfig().addOption("PIH:UNEMPLOYED", "zl.registration.patient.occupation.unemployed.label");
        w.getConfig().addOption("PIH:OTHER NON-CODED", "zl.registration.patient.occupation.other.label");

        w.getConfig().setExpanded(true);
        f.setWidget(toObjectNode(w));
        q.addField(f);

        return q;
    }

    private Section getRelationshipsSection() {
        Section s = new Section();
        s.setId("relationshipsInfo");
        s.setLabel("registrationapp.person.relationship");

        Question q = new Question();
        q.setId("relationshipsInfoQuestion");
        q.setLegend("registrationapp.person.relationship.label");
        q.setHeader("registrationapp.person.relationship.question");

        Field f = new Field();
        f.setType("personRelationships");

        Map<String, String> m = new HashMap<String, String>();
        m.put("providerName", "registrationapp");
        m.put("fragmentId", "field/personRelationship");
        f.setWidget(toObjectNode(m));

        q.addField(f);
        s.addQuestion(q);
        return s;
    }


}
