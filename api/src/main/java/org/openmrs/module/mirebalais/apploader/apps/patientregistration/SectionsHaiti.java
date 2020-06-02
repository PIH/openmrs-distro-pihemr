package org.openmrs.module.mirebalais.apploader.apps.patientregistration;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.haiticore.metadata.HaitiPatientIdentifierTypes;
import org.openmrs.module.mirebalais.require.RequireUtil;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.metadata.core.LocationTags;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiPatientIdentifierTypes;
import org.openmrs.module.registrationapp.model.DropdownWidget;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;
import org.openmrs.module.registrationapp.model.Section;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SectionsHaiti extends SectionsDefault {

    private FeatureToggleProperties featureToggles;

    private Config config;

    public SectionsHaiti(Config config, FeatureToggleProperties featureToggles) {
        super(config);
        this.config = config;
        this.featureToggles = featureToggles;
    }

    @Override
    public void addSections(RegistrationAppConfig c) {
        if (config.isComponentEnabled(Components.BIOMETRICS_FINGERPRINTS)) {
            c.addSection(getBiometricsSection());
        }

        c.addSection(getDemographicsSection());
        c.addSection(getContactInfoSection());

        // exclude the cross-site MH laptops
        if (!ConfigDescriptor.Specialty.MENTAL_HEALTH.equals(config.getSpecialty())) {

            // remove toggle once enabled
            if (featureToggles.isFeatureEnabled("insuranceCollection")) {
                c.addSection(getInsuranceSection());
            }
        }

        c.addSection(getSocialSection());
        c.addSection(getContactsSection(true));
        c.addSection(getIdentifierSection());

        if (config.isComponentEnabled(Components.ID_CARD_PRINTING)) {
            c.addSection(getIdCardPrintSection());
        }
    }

    @Override
    public Question getMothersNameQuestion() {
        Question q = super.getMothersNameQuestion();
        q.setRequire(RequireUtil.sessionLocationDoesNotHaveTag(LocationTags.TABLET_ENTRY_LOCATION)); // we use a simplified registration in "tablet entry" locations
        return q;
    }

    public Section getContactsSection(boolean required) {
        Section s = super.getContactsSection(required);
        s.setRequire(RequireUtil.sessionLocationDoesNotHaveTag(LocationTags.TABLET_ENTRY_LOCATION)); // we use a simplified registration in "tablet entry" locations
        return s;
    }

    @Override
    public Section getSocialSection() {
        Section s = new Section();
        s.setId("social");
        s.setLabel("zl.registration.patient.social.label");
        s.setRequire(RequireUtil.sessionLocationDoesNotHaveTag(LocationTags.TABLET_ENTRY_LOCATION)); // we use a simplified registration in "tablet entry" locations
        s.addQuestion(getBirthplaceQuestion());
        s.addQuestion(getCivilStatusQuestion());
        s.addQuestion(getOccupationQuestion());
        s.addQuestion(getReligionQuestion());
        return s;
    }

    @Override
    public Question getBirthplaceQuestion() {
        Question q = new Question();
        q.setId("birthplaceLabel");
        q.setHeader("zl.registration.patient.birthplace.question");
        q.setLegend("zl.registration.patient.birthplace.label");

        Field f = new Field();
        //f.setFormFieldName("obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:ADDRESS OF PATIENT CONTACT");
        f.setLabel("zl.registration.patient.birthplace.label");
        f.setType("personAddress");

        // If there are address hierarchy levels configured, use the address hierarchy widget, otherwise use the standard address widget
        List<AddressHierarchyLevel> levels = Context.getService(AddressHierarchyService.class).getAddressHierarchyLevels();
        if (levels != null && levels.size() > 0) {
            //q.setDisplayTemplate(getAddressHierarchyDisplayTemplate(levels));
            f.setWidget(getAddressHierarchyWidget(levels, getPlaceOfBirthAddressFieldMappings(), true));
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

        // ordered alphabetically in French, with Unemployed and Other last
        w.getConfig().addOption("PIH:SHEPHERD", "zl.registration.patient.occupation.shepherd.label");
        w.getConfig().addOption("PIH:DRIVER", "zl.registration.patient.occupation.driver.label");
        w.getConfig().addOption("PIH:COMMERCE", "zl.registration.patient.occupation.commerce.label");
        w.getConfig().addOption("PIH:FARMER", "zl.registration.patient.occupation.farmer.label");
        w.getConfig().addOption("CIEL:162944", "zl.registration.patient.occupation.civilServant.label");
        w.getConfig().addOption("PIH:MANUAL LABORER", "zl.registration.patient.occupation.manualLaborer.label");
        w.getConfig().addOption("PIH:HEALTH CARE WORKER", "zl.registration.patient.occupation.healthCareWorker.label");
        w.getConfig().addOption("PIH:Zanmi Lasante employee", "zl.registration.patient.occupation.zlStaff.label");
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

    @Override
    public Question getContactAddress(boolean required) {

        Question q = new Question();
        q.setId("contactQuestionLabel");
        q.setHeader("zl.registration.patient.contactPerson.address.question");
        q.setLegend("zl.registration.patient.contactPerson.address.label");

        Field f = new Field();
        //f.setFormFieldName("obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:ADDRESS OF PATIENT CONTACT");
        f.setLabel("zl.registration.patient.contactPerson.address.label");
        f.setType("personAddress");

        // If there are address hierarchy levels configured, use the address hierarchy widget, otherwise use the standard address widget
        List<AddressHierarchyLevel> levels = Context.getService(AddressHierarchyService.class).getAddressHierarchyLevels();
        if (levels != null && levels.size() > 0) {
            //q.setDisplayTemplate(getAddressHierarchyDisplayTemplate(levels));
            f.setWidget(getAddressHierarchyWidget(levels, getContactAddressFieldMappings(), true));
        }
        else {
            Map<String, String> m = new HashMap<String, String>();
            m.put("providerName", "uicommons");
            m.put("fragmentId", "field/personAddress");
            f.setWidget(toObjectNode(m));
            if (required) { f.setCssClasses(Arrays.asList("required")); }
        }
        q.addField(f);

        return q;
    }

    private Section getInsuranceSection() {
        Section s = new Section();
        s.setId("insurance");
        s.setLabel("zl.registration.patient.insurance.label");
        s.setRequire(RequireUtil.sessionLocationDoesNotHaveTag(LocationTags.TABLET_ENTRY_LOCATION)); // hide in COVID locations (because of tablet entry)
        s.addQuestion(getInsuranceNameAndNumber());
        return s;
    }

    private Question getInsuranceNameAndNumber() {
        Question q = new Question();
        q.setId("insuranceNameLabel");
        q.setLegend("zl.registration.patient.insurance.insuranceName.question");
        q.setHeader("zl.registration.patient.insurance.insuranceName.label");

        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:Insurance CONSTRUCT.obs.PIH:Haiti insurance company name");
            f.setLabel("zl.registration.patient.insurance.insuranceName.question");
            f.setType("obsgroup");
            f.setWidget(getTextFieldWidget(30));

            DropdownWidget w = new DropdownWidget();
            w.getConfig().setExpanded(true);
            w.getConfig().addOption("PIH:NONE", "zl.registration.patient.insurance.none.label");
            w.getConfig().addOption("PIH:AIC","zl.registration.patient.insurance.aic.label");
            w.getConfig().addOption("PIH:INASSA", "zl.registration.patient.insurance.inassa.label");
            w.getConfig().addOption("PIH:CAH", "zl.registration.patient.insurance.cah.label");
            w.getConfig().addOption("PIH:CONAM", "zl.registration.patient.insurance.conam.label");
            w.getConfig().addOption("PIH:Sogebank Assurance", "zl.registration.patient.insurance.sogebank.label");
            w.getConfig().addOption("PIH:UniAssurances", "zl.registration.patient.insurance.uniAssurances.label");
            w.getConfig().addOption("PIH:GMC Henner", "zl.registration.patient.insurance.gmcHenner.label");
            w.getConfig().addOption("PIH:CIGNA (Vanbreda)", "zl.registration.patient.insurance.cigna.label");
            w.getConfig().addOption("PIH:UNKNOWN", "zl.registration.patient.insurance.unknown.label");
            w.getConfig().addOption("PIH:OTHER", "zl.registration.patient.insurance.other.label");

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

    private Map<String,String> getPlaceOfBirthAddressFieldMappings() {
        // Haiti-specific
        Map<String,String> fieldMappings = new HashMap<String, String>();
        fieldMappings.put(AddressField.COUNTRY.getName(), "obsgroup.PIH:Birthplace address construct.obs.PIH:Country");
        fieldMappings.put(AddressField.STATE_PROVINCE.getName(), "obsgroup.PIH:Birthplace address construct.obs.PIH:State Province");
        fieldMappings.put(AddressField.CITY_VILLAGE.getName(), "obsgroup.PIH:Birthplace address construct.obs.PIH:City Village");
        fieldMappings.put(AddressField.ADDRESS_3.getName(), "obsgroup.PIH:Birthplace address construct.obs.PIH:Address3");
        fieldMappings.put(AddressField.ADDRESS_1.getName(), "obsgroup.PIH:Birthplace address construct.obs.PIH:Address1");
        fieldMappings.put(AddressField.ADDRESS_2.getName(), "obsgroup.PIH:Birthplace address construct.obs.PIH:Address2");
        return fieldMappings;
    }

    private Question getReligionQuestion() {
        Question q = new Question();
        q.setId("religionLabel");
        q.setLegend("zl.registration.patient.religion.label");
        q.setHeader("zl.registration.patient.religion.question");

        Field f = new Field();
        f.setFormFieldName("obs.PIH:Religion");
        f.setType("obs");

        DropdownWidget w = new DropdownWidget();
        w.getConfig().setExpanded(true);
        w.getConfig().addOption("PIH:Voodoo", "zl.registration.patient.religion.voodoo.label");
        w.getConfig().addOption("PIH:Catholic", "zl.registration.patient.religion.catholic.label");
        w.getConfig().addOption("PIH:Baptist", "zl.registration.patient.religion.baptist.label");
        w.getConfig().addOption("PIH:Islam", "zl.registration.patient.religion.islam.label");
        w.getConfig().addOption("PIH:Pentecostal", "zl.registration.patient.religion.pentecostal.label");
        w.getConfig().addOption("PIH:Seventh Day Adventist", "zl.registration.patient.religion.adventist.label");
        w.getConfig().addOption("PIH:Jehovah's Witness", "zl.registration.patient.religion.jehovahsWitness.label");
        w.getConfig().addOption("PIH:OTHER NON-CODED", "zl.registration.patient.religion.other.label");
        f.setWidget(toObjectNode(w));

        q.addField(f);
        return q;
    }

    private Section getIdentifierSection() {
        Section s = new Section();
        s.setId("patient-identification-section");
        s.setLabel("registrationapp.patient.identifiers.label");

        if (featureToggles.isFeatureEnabled("additionalHaitiIdentifiers") &&
                !ConfigDescriptor.Specialty.MENTAL_HEALTH.equals(config.getSpecialty())) {
                s.addQuestion(getHivEmrId());
                s.addQuestion(getHivDossierNumber());
                s.addQuestion(getNumeroIdentificationFiscal());
                s.addQuestion(getCarteDIdentificationNationale());
        }

        return s;
    }

    private Question getHivDossierNumber() {
        Question q = new Question();
        q.setId("hivemr-dossier-id");
        q.setLegend("HIV Dossier");
        q.setHeader("ui.i18n.PatientIdentifierType.name." + PihHaitiPatientIdentifierTypes.HIV_DOSSIER_NUMBER.uuid());

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + PihHaitiPatientIdentifierTypes.HIV_DOSSIER_NUMBER.uuid());
        f.setUuid(PihHaitiPatientIdentifierTypes.HIV_DOSSIER_NUMBER.uuid());
        f.setType("patientIdentifier");
        f.setWidget(getTextFieldWidget(16));

        q.addField(f);
        return q;
    }

    private Question getHivEmrId() {
        Question q = new Question();
        q.setId("hivemr-v1-id");
        q.setLegend("HIVEMR-V1");
        q.setHeader("ui.i18n.PatientIdentifierType.name." + PihHaitiPatientIdentifierTypes.HIVEMR_V1.uuid());

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + PihHaitiPatientIdentifierTypes.HIVEMR_V1.uuid());
        f.setUuid(PihHaitiPatientIdentifierTypes.HIVEMR_V1.uuid());
        f.setType("patientIdentifier");
        f.setWidget(getTextFieldWidget(16));

        q.addField(f);
        return q;
    }

    private Question getNumeroIdentificationFiscal() {
        Question q = new Question();
        q.setId("numero-identification-fiscal");
        q.setLegend("NIF");
        q.setHeader("ui.i18n.PatientIdentifierType.name." + HaitiPatientIdentifierTypes.NIF_ID.uuid());

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + HaitiPatientIdentifierTypes.NIF_ID.uuid());
        //f.setLabel(HaitiPatientIdentifierTypes.NIF_ID.name());
        f.setUuid(HaitiPatientIdentifierTypes.NIF_ID.uuid());
        f.setType("patientIdentifier");
        f.setWidget(getTextFieldWidget(16));

        q.addField(f);
        return q;
    }

    private Question getCarteDIdentificationNationale() {
        Question q = new Question();
        q.setId("carte-d-identification-nationale");
        q.setLegend("CIN");
        q.setHeader("ui.i18n.PatientIdentifierType.name." + HaitiPatientIdentifierTypes.CIN_ID.uuid());

        Field f = new Field();
        f.setFormFieldName("patientIdentifier" + HaitiPatientIdentifierTypes.CIN_ID.uuid());
        //f.setLabel(HaitiPatientIdentifierTypes.CIN_ID.name());
        f.setUuid(HaitiPatientIdentifierTypes.CIN_ID.uuid());
        f.setType("patientIdentifier");
        f.setWidget(getTextFieldWidget(16));

        q.addField(f);
        return q;
    }

    private Map<String,String> getContactAddressFieldMappings() {
        // Haiti-specific
        Map<String,String> fieldMappings = new HashMap<String, String>();
        fieldMappings.put(AddressField.COUNTRY.getName(), "obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:Country");
        fieldMappings.put(AddressField.STATE_PROVINCE.getName(), "obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:State Province");
        fieldMappings.put(AddressField.CITY_VILLAGE.getName(), "obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:City Village");
        fieldMappings.put(AddressField.ADDRESS_3.getName(), "obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:Address3");
        fieldMappings.put(AddressField.ADDRESS_1.getName(), "obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:Address1");
        fieldMappings.put(AddressField.ADDRESS_2.getName(), "obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:Address2");
        return fieldMappings;
    }


}
