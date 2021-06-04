package org.openmrs.module.mirebalais.apploader.apps.patientregistration;

import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.registration.SocialConfigDescriptor;
import org.openmrs.module.pihcore.metadata.peru.PeruPatientIdentifierTypes;
import org.openmrs.module.registrationapp.model.DropdownWidget;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;
import org.openmrs.module.registrationapp.model.Section;

public class SectionsPeru extends SectionsDefault {

    private Config config;

    public SectionsPeru(Config config) {
        super(config);
        this.config = config;
    }

    @Override
    public void addSections(RegistrationAppConfig c) {
        c.addSection(getIdentifierSection());
        c.addSection(getDemographicsSection());
        c.addSection(getContactInfoSection());
        c.addSection(getSocialSection());
    }

    @Override
    public Section getIdentifierSection() {
        Section s = new Section();
        s.setId("patient-identification-section");
        s.setLabel("registrationapp.patient.identifiers.label");
        s.addQuestion(getDNIdocument());
        s.addQuestion(getPassportdocument());
        return s;
    }
    
    private Question getDNIdocument() {
        Question q = new Question();
        q.setId("national-id");
        q.setLegend("zl.registration.patient.documenttype.dni.label");
        q.setHeader("zl.registration.patient.documenttype.dni.label");

        Field f=new Field();
        f.setFormFieldName("patientIdentifier" + SesConfigConstants.PATIENTIDENTIFIERTYPE_DNI_UUID);
        f.setUuid(SesConfigConstants.PATIENTIDENTIFIERTYPE_DNI_UUID);
        f.setType("patientIdentifier");
        f.setWidget(getTextFieldWidget(8));

        q.addField(f);

        return q;
    }
    private Question getPassportdocument() {
        Question q = new Question();
        q.setId("passport-id");
        q.setLegend("zl.registration.patient.documenttype.passport.label");
        q.setHeader("zl.registration.patient.documenttype.passport.label");

        Field f=new Field();
        f.setFormFieldName("patientIdentifier" + SesConfigConstants.PATIENTIDENTIFIERTYPE_PASSPORT_UUID);
        f.setUuid(SesConfigConstants.PATIENTIDENTIFIERTYPE_PASSPORT_UUID);
        f.setType("patientIdentifier");
        f.setWidget(getTextFieldWidget(12));

        q.addField(f);

        return q;
    }
    

    @Override
    public Section getSocialSection() {
        Section s = new Section();
        s.setId("social");
        s.setLabel("zl.registration.patient.social.label");
        s.addQuestion(getCivilStatusQuestion());
        s.addQuestion(getLevelOfStudyQuestion());
        s.addQuestion(getOccupationQuestion());
        return s;
    }

    public Question getLevelOfStudyQuestion() {
        Question q = new Question();
        q.setId("levelOfStudyLabel");
        q.setLegend("zl.registration.patient.levelOfStudy.label");
        q.setHeader("zl.registration.patient.levelOfStudy.question");

        Field f = new Field();
        f.setFormFieldName("obs.PIH:HIGHEST LEVEL OF SCHOOL COMPLETED");
        f.setType("obs");

        DropdownWidget w = new DropdownWidget();
        w.getConfig().addOption("PIH:NONE", "pihcore.none.label");
        w.getConfig().addOption("PIH:PRIMARY EDUCATION COMPLETE", "zl.registration.patient.levelOfStudy.primary.label");
        w.getConfig().addOption("PIH:SECONDARY EDUCATION COMPLETE", "zl.registration.patient.levelOfStudy.secondary.label");
        w.getConfig().addOption("PIH:PROFESSIONAL", "zl.registration.patient.levelOfStudy.tertiary.label");
        w.getConfig().addOption("CIEL:159785", "zl.registration.patient.levelOfStudy.superior.label");  // Superior

        w.getConfig().setExpanded(true);
        f.setWidget(toObjectNode(w));
        q.addField(f);

        return q;
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
        w.getConfig().addOption("PIH:FARMER", "zl.registration.patient.occupation.farmer.label");  // Agricultur
        w.getConfig().addOption("PIH:HOUSEWORK/FIELDWORK", "zl.registration.patient.occupation.houseworkFieldwork.label");  // Ama de casa
        w.getConfig().addOption("PIH:MANUAL LABORER", "zl.registration.patient.occupation.manualLaborer.label");  // Carpintero
        w.getConfig().addOption("PIH:DRIVER", "zl.registration.patient.occupation.driver.label");  // Chofeur
        w.getConfig().addOption("PIH:COMMERCE", "zl.registration.patient.occupation.commerce.label");  // Commercial
        w.getConfig().addOption("PIH:STUDENT", "zl.registration.patient.occupation.student.label");  // Estudiante
        w.getConfig().addOption("PIH:Teacher", "zl.registration.patient.occupation.teacher.label");  // Maestro
        w.getConfig().addOption("PIH:Military", "zl.registration.patient.occupation.military.label");  // Militar
        w.getConfig().addOption("PIH:FACTORY WORKER", "zl.registration.patient.occupation.factoryWorker.label");  // Obrero
        w.getConfig().addOption("CIEL:159674", "zl.registration.patient.occupation.fisherman.label");  // Pescador
        w.getConfig().addOption("PIH:Police", "zl.registration.patient.occupation.police.label");  // Policia
        w.getConfig().addOption("PIH:PROFESSIONAL", "zl.registration.patient.occupation.professional.label");  // Profesional
        w.getConfig().addOption("PIH:HEALTH CARE WORKER", "zl.registration.patient.occupation.healthCareWorker.label");  // Profesional...
        w.getConfig().addOption("PIH:Cowherd", "zl.registration.patient.occupation.cowherd.label");  // Vaquero
        w.getConfig().addOption("PIH:RETIRED", "zl.registration.patient.occupation.retired.label");
        w.getConfig().addOption("PIH:OTHER NON-CODED", "zl.registration.patient.occupation.other.label");

        w.getConfig().setExpanded(true);
        f.setWidget(toObjectNode(w));
        q.addField(f);

        return q;
    }


}
