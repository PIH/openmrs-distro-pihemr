package org.openmrs.module.mirebalais.apploader.apps.patientregistration;

import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeonePatientIdentifierTypes;
import org.openmrs.module.registrationapp.model.DropdownWidget;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;
import org.openmrs.module.pihcore.config.registration.SocialConfigDescriptor;
import org.openmrs.module.registrationapp.model.DropdownWidget;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.Section;

import java.util.Arrays;




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
        c.addSection(getEbolaScreeningSection());
        c.addSection(getContactsSection(false));
        c.addSection(getIdentifierSection());
        c.addSection(getIdCardPrintSection());
    }

    @Override
    public Section getIdentifierSection() {
        Section s = new Section();
        s.setId("patient-identification-section");
        s.setLabel("registrationapp.patient.identifiers.label");
        if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE) && config.getSite().equalsIgnoreCase("KGH")) {
            s.addQuestion(geWellbodyEmrId());
        } else if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE) && config.getSite().equalsIgnoreCase("WELLBODY")) {
            s.addQuestion(geKghEmrId());
        }

        return s;
    }

    private Question geWellbodyEmrId() {
        Question q = new Question();
        q.setId("wellbody-emr-id");
        q.setLegend("Wellbody EMR ID");
        q.setHeader("ui.i18n.PatientIdentifierType.name." + SierraLeonePatientIdentifierTypes.WELLBODY_EMR_ID.uuid());

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + SierraLeonePatientIdentifierTypes.WELLBODY_EMR_ID.uuid());
        f.setUuid(SierraLeonePatientIdentifierTypes.WELLBODY_EMR_ID.uuid());
        f.setType("patientIdentifier");
        f.setWidget(getTextFieldWidget(16));

        q.addField(f);
        return q;
    }

    private Question geKghEmrId() {
        Question q = new Question();
        q.setId("kgh-emr-id");
        q.setLegend("KGH EMR ID");
        q.setHeader("ui.i18n.PatientIdentifierType.name." + SierraLeonePatientIdentifierTypes.KGH_EMR_ID.uuid());

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + SierraLeonePatientIdentifierTypes.KGH_EMR_ID.uuid());
        f.setUuid(SierraLeonePatientIdentifierTypes.KGH_EMR_ID.uuid());
        f.setType("patientIdentifier");
        f.setWidget(getTextFieldWidget(16));

        q.addField(f);
        return q;
    }

    @Override
    public Section getSocialSection() {
    Section s = new Section();
    s.setId("social");
    s.setLabel("zl.registration.patient.social.label");
    SocialConfigDescriptor socConfig = config.getRegistrationConfig().getSocial(); 
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

        // ordered alphabetically with Unemployed and Other last
        w.getConfig().addOption("CIEL:162944", "zl.registration.patient.occupation.civilServant.label");
        w.getConfig().addOption("PIH:COMMERCE", "zl.registration.patient.occupation.commerce.label");
        w.getConfig().addOption("PIH:Commercial bike rider", "zl.registration.patient.occupation.motorcycletaxi");
        w.getConfig().addOption("PIH:Cowherd", "zl.registration.patient.occupation.cowherd.label");
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
        w.getConfig().addOption("PIH:Military", "zl.registration.patient.occupation.military.label");
        w.getConfig().addOption("PIH:MINER", "zl.registration.patient.occupation.miner.label");
        w.getConfig().addOption("PIH:Police", "zl.registration.patient.occupation.police.label");
        w.getConfig().addOption("PIH:PROFESSIONAL", "zl.registration.patient.occupation.professional.label");
        w.getConfig().addOption("PIH:RETIRED", "zl.registration.patient.occupation.retired.label");
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
    

     public Section getEbolaScreeningSection() {
        Section s = new Section();
        s.setId("ebolascreening");
        s.addQuestion(getEbolaScreeningQuestion());
        s.setLabel("zl.registration.patient.ebolaScreening.header");
        return s;
    }


    public Question getEbolaScreeningQuestion() {
        Question q = new Question();
        q.setId("ebolalabel");
        q.setLegend("zl.registration.patient.ebolaScreening.label");
        q.setHeader("zl.registration.patient.ebolaScreening.alertClinician");
        {
            Field f = new Field();
            f.setFormFieldName("obs.PIH:12246");
            f.setCssClasses(Arrays.asList("required"));
            f.setLabel("zl.registration.patient.ebolaScreening.feverTwoDays.label");
            f.setType("obs");
            f.setWidget(getYesNoDropdownWidget());
            q.addField(f);
        }
        {
            Field f = new Field();
            f.setFormFieldName("obs.PIH:7102");
            f.setCssClasses(Arrays.asList("required"));
            f.setLabel("zl.registration.patient.ebolaScreening.bleedingSigns.label");
            f.setType("obs");
            f.setWidget(getYesNoDropdownWidget());
            q.addField(f);
        }
        {
            Field f = new Field();
            f.setFormFieldName("obs.CIEL:1690");
            f.setCssClasses(Arrays.asList("required"));
            f.setLabel("zl.registration.patient.ebolaScreening.clinicalSuspicion.label");
            f.setType("obs");
            f.setWidget(getYesNoDropdownWidget());
            q.addField(f);
        }
        StringBuilder displayTemplate = new StringBuilder();
        displayTemplate.append("{{#if field.[0]}}{{ message 'Fever' }}: {{field.[0]}}, {{/if}}");
        displayTemplate.append("{{#if field.[1]}}{{ message 'Bleeding' }}: {{field.[1]}}, {{/if}}");
        displayTemplate.append("{{#if field.[2]}}{{ message 'Clinical Suspicion' }}: {{field.[2]}} {{/if}}");
        q.setDisplayTemplate(displayTemplate.toString());
        return q;
    }
    
   
}
