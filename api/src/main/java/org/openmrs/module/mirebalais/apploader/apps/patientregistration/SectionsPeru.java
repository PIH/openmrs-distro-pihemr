package org.openmrs.module.mirebalais.apploader.apps.patientregistration;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.pihcore.SesConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.registration.ContactInfoConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.SocialConfigDescriptor;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.registrationapp.model.DropdownWidget;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;
import org.openmrs.module.registrationapp.model.Section;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        c.addSection(getContactsSection(false));
    }

    @Override
    public Section getIdentifierSection() {
        Section s = new Section();
        s.setId("patient-identification-section");
        s.setLabel("registrationapp.patient.identifiers.label");
        s.addQuestion(getDNIdocument());
        s.addQuestion(getPassportdocument());
        s.addQuestion(getInmigrationCard());
        return s;
    }

    private Question getDNIdocument() {
        Question q = new Question();
        q.setId("national-id");
        q.setLegend("zl.registration.patient.documenttype.dni.label");
        q.setHeader("zl.registration.patient.documenttype.dni.label");

        Field f = new Field();
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

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + SesConfigConstants.PATIENTIDENTIFIERTYPE_PASSPORT_UUID);
        f.setUuid(SesConfigConstants.PATIENTIDENTIFIERTYPE_PASSPORT_UUID);
        f.setType("patientIdentifier");
        f.setWidget(getTextFieldWidget(12));

        q.addField(f);

        return q;
    }
    private Question getInmigrationCard(){
        Question q=new Question();
        q.setId("inmigration-id");
        q.setLegend("zl.registration.patient.documenttype.inmigrationCard.label");
        q.setHeader("zl.registration.patient.documenttype.inmigrationCard.label");

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + SesConfigConstants.PATIENTIDENTIFIERTYPE_INMIGRATIONCARD_UUID);
        f.setUuid(SesConfigConstants.PATIENTIDENTIFIERTYPE_INMIGRATIONCARD_UUID);
        f.setType("patientIdentifier");
        f.setWidget(getTextFieldWidget(12));

        q.addField(f);

        return q;
    }
    public Question getIdentityGender(){
        Question q=new Question();
        q.setId("IdentityGender");
        q.setLegend("zl.registration.patient.IdentityGender.label");
        q.setHeader("zl.registration.patient.IdentityGender.question");
        {
            Field f = new Field();
            f.setFormFieldName("obs.PIH:Gender identity");
            f.setType("obs");

            DropdownWidget w = new DropdownWidget();
            w.getConfig().addOption("PIH:2843", "zl.registration.patient.IdentityGender.Male");
            w.getConfig().addOption("PIH:2844", "zl.registration.patient.IdentityGender.Female");
            w.getConfig().addOption("PIH:13683", "zl.registration.patient.IdentityGender.TransMan");
            w.getConfig().addOption("PIH:13684", "zl.registration.patient.IdentityGender.TransWoman");
            w.getConfig().addOption("PIH:13685", "zl.registration.patient.IdentityGender.GenderNon");
            w.getConfig().addOption("PIH:OTHER NON-CODED", "zl.registration.patient.occupation.other.label");

            w.getConfig().setExpanded(true);
            f.setWidget(toObjectNode(w));
            q.addField(f);
        }
        {
            Field f=new Field();
            f.setFormFieldName("obs.PIH:13700");
            f.setLabel("zl.ifOtherSpecify");
            f.setType("obs");
            f.setWidget(getTextFieldWidget(30));
            q.addField(f);
        }

        return q;
    }

    public Section getContactInfoSection() {
        Section s = new Section();
        s.setId("contactInfo");
        s.setLabel("registrationapp.patient.contactInfo.label");
        s.addQuestion(getAddressQuestion());
        s.addQuestion(getTelephoneNumberQuestion());
        s.addQuestion(getCellphoneNumberQuestion());
        s.addQuestion(getEmailQuestion());
        return s;
    }

    public Question getAddressQuestion() {
        Question q = new Question();
        q.setId("personAddressQuestion");
        q.setLegend("registrationapp.patient.address");
        q.setHeader("registrationapp.patient.address.question");

        Field f = new Field();
        f.setType("personAddress");

        // If there are address hierarchy levels configured, use the address hierarchy widget, otherwise use the standard address widget
        List<AddressHierarchyLevel> levels = Context.getService(AddressHierarchyService.class).getAddressHierarchyLevels();
        if (levels != null && levels.size() > 0) {
            q.setDisplayTemplate(getAddressHierarchyDisplayTemplate(levels));
            f.setWidget(getAddressHierarchyWidget(levels, null, true));
        }
        else {
            Map<String, String> m = new HashMap<String, String>();
            m.put("providerName", "uicommons");
            m.put("fragmentId", "field/personAddress");
            f.setWidget(toObjectNode(m));
        }

        q.addField(f);
        return q;
    }

    public Question getTelephoneNumberQuestion() {
        Question q = new Question();
        q.setId("phoneNumberLabel");
        q.setLegend("registrationapp.patient.phone.label");
        q.setHeader("registrationapp.patient.phone.question");

        Field f = new Field();
        f.setFormFieldName("phoneNumber");
        f.setType("personAttribute");
        f.setUuid(Metadata.getPhoneNumberAttributeType().getUuid());

        ContactInfoConfigDescriptor contactInfoConfig = config.getRegistrationConfig().getContactInfo();
        if(contactInfoConfig != null && contactInfoConfig.getPhoneNumber() != null
                && StringUtils.isNotBlank(contactInfoConfig.getPhoneNumber().getRegex())){
            f.setCssClasses(Arrays.asList("regex"));
            f.setWidget(getTextFieldWidget(null, contactInfoConfig.getPhoneNumber().getRegex()));
        } else {
            f.setWidget(getTextFieldWidget());
        }

        q.addField(f);
        return q;
    }

    public Question getCellphoneNumberQuestion(){
        Question q = new Question();
        q.setId("cellphoneNumberLabel");
        q.setLegend("zl.registration.patient.cellphone.label");
        q.setHeader("zl.registration.patient.cellphone.question");

        Field f = new Field();
        f.setFormFieldName("personAttributeType"+ SesConfigConstants.PERSONATTRIBUTETYPE_CELLPHONE_NUMBER_UUID);
        f.setUuid(SesConfigConstants.PERSONATTRIBUTETYPE_CELLPHONE_NUMBER_UUID);
        f.setType("personAttribute");
        f.setWidget(getTextFieldWidget(9));

        q.addField(f);

        return q;
    }

    public Question getEmailQuestion(){
        Question q = new Question();
        q.setId("emailLabel");
        q.setLegend("zl.registration.patient.email.label");
        q.setHeader("zl.registration.patient.email.question");

        Field f = new Field();
        f.setFormFieldName("personAttributeType"+ SesConfigConstants.PERSONATTRIBUTETYPE_ELECTRONIC_EMAIL_UUID);
        f.setUuid(SesConfigConstants.PERSONATTRIBUTETYPE_ELECTRONIC_EMAIL_UUID);
        f.setType("personAttribute");
        f.setWidget(getTextFieldWidget(254));

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
        s.addQuestion(getIdentityGender());
        s.addQuestion(getOccupationQuestion());
        s.addQuestion(getBirthplaceQuestion());
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

        {
            Field f = new Field();
            f.setFormFieldName("obs.PIH:Occupation");
            f.setType("obs");

            DropdownWidget w = new DropdownWidget();

            // ordered alphabetically in Spanish, with Unemployed and Other last
            w.getConfig().addOption("PIH:FARMER", "zl.registration.patient.occupation.farmer.label");  // Agricultor
            w.getConfig().addOption("PIH:Lawyer","zl.registration.patient.occupation.lawyer.label"); //Abogado
            w.getConfig().addOption("PIH:HOUSEWORK/FIELDWORK", "zl.registration.patient.occupation.houseworkFieldwork.label");  // Ama de casa
            w.getConfig().addOption("PIH:MANUAL LABORER", "zl.registration.patient.occupation.manualLaborer.label");  // Carpintero
            w.getConfig().addOption("PIH:DRIVER", "zl.registration.patient.occupation.driver.label");  // Chofer
            w.getConfig().addOption("PIH:COMMERCE", "zl.registration.patient.occupation.commerce.label");  // Commercial
            w.getConfig().addOption("PIH:Accountant","zl.registration.patient.occupation.accountant.label");//Contador
            w.getConfig().addOption("PIH:STUDENT", "zl.registration.patient.occupation.student.label");  // Estudiante
            w.getConfig().addOption("PIH:NURSE","zl.registration.patient.occupation.nurse.label");//Enfermero
            w.getConfig().addOption("PIH:Electrical engineer","zl.registration.patient.occupation.electricalEngineer.label");//Ingeniero Electrico
            w.getConfig().addOption("PIH:Teacher", "zl.registration.patient.occupation.teacher.label");  // Maestro
            w.getConfig().addOption("PIH:Military", "zl.registration.patient.occupation.military.label");  // Militar
            w.getConfig().addOption("PIH:FACTORY WORKER", "zl.registration.patient.occupation.factoryWorker.label");  // Obrero
            w.getConfig().addOption("CIEL:159674", "zl.registration.patient.occupation.fisherman.label");  // Pescador
            w.getConfig().addOption("PIH:Journalist","zl.registration.patient.occupation.journalist.label");
            w.getConfig().addOption("PIH:Police", "zl.registration.patient.occupation.police.label");  // Policia
            w.getConfig().addOption("PIH:HEALTH CARE WORKER", "zl.registration.patient.occupation.healthCareWorker.label");  // Profesional...
            w.getConfig().addOption("PIH:RETIRED", "zl.registration.patient.occupation.retired.label");
            w.getConfig().addOption("PIH:OTHER NON-CODED", "zl.registration.patient.occupation.other.label");

            w.getConfig().setExpanded(true);
            f.setWidget(toObjectNode(w));
            q.addField(f);
        }
        {
            Field f=new Field();
            f.setFormFieldName("obs.PIH:2452");
            f.setLabel("zl.ifOtherSpecify");
            f.setType("obs");
            f.setWidget(getTextFieldWidget(30));
            q.addField(f);
        }

        return q;
    }


}
