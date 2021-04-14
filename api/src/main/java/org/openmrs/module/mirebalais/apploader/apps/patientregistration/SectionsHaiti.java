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
        w.getConfig().addOption("PIH:Stockbroker", "zl.registration.patient.occupation.stockbroker.label");
        w.getConfig().addOption("PIH:Guard", "zl.registration.patient.occupation.guard.label");
        w.getConfig().addOption("PIH:Agronomist", "zl.registration.patient.occupation.agronomist.label");
        w.getConfig().addOption("PIH:VILLAGE HEALTH WORKER", "zl.registration.patient.occupation.villageHealthWorker.label");
        w.getConfig().addOption("PIH:Assistant driver", "zl.registration.patient.occupation.assistantDriver.label");
        w.getConfig().addOption("PIH:Archivist", "zl.registration.patient.occupation.archivist.label");
        w.getConfig().addOption("PIH:Surveyor", "zl.registration.patient.occupation.surveyor.label");
        w.getConfig().addOption("PIH:Artisan", "zl.registration.patient.occupation.artisan.label");
        w.getConfig().addOption("PIH:Artist", "zl.registration.patient.occupation.artist.label");
        w.getConfig().addOption("PIH:Secretary", "zl.registration.patient.occupation.secretary.label");
        w.getConfig().addOption("PIH:LPN", "zl.registration.patient.occupation.licensedPracticalNurse.label");
        w.getConfig().addOption("PIH:Lawyer", "zl.registration.patient.occupation.lawyer.label");
        w.getConfig().addOption("PIH:SHEPHERD", "zl.registration.patient.occupation.shepherd.label");
        w.getConfig().addOption("PIH:Librarian", "zl.registration.patient.occupation.librarian.label");
        w.getConfig().addOption("PIH:Lottery worker", "zl.registration.patient.occupation.lotteryWorker.label");
        w.getConfig().addOption("PIH:Welder", "zl.registration.patient.occupation.welder.label");
        w.getConfig().addOption("PIH:Cashier", "zl.registration.patient.occupation.cashier.label");
        w.getConfig().addOption("PIH:Singer", "zl.registration.patient.occupation.singer.label");
        w.getConfig().addOption("PIH:Hunter", "zl.registration.patient.occupation.hunter.label");
        w.getConfig().addOption("PIH:DRIVER", "zl.registration.patient.occupation.driver.label");
        w.getConfig().addOption("PIH:Cook", "zl.registration.patient.occupation.cook.label");
        w.getConfig().addOption("PIH:Shoeshiner", "zl.registration.patient.occupation.shoeshiner.label");
        w.getConfig().addOption("PIH:COMMERCE", "zl.registration.patient.occupation.commerce.label");
        w.getConfig().addOption("PIH:Accountant", "zl.registration.patient.occupation.accountant.label");
        w.getConfig().addOption("PIH:Shoemaker", "zl.registration.patient.occupation.shoemaker.label");
        w.getConfig().addOption("PIH:Fashion designer", "zl.registration.patient.occupation.fashionDesigner.label");
        w.getConfig().addOption("PIH:FARMER", "zl.registration.patient.occupation.farmer.label");
        w.getConfig().addOption("PIH:DOCTOR", "zl.registration.patient.occupation.doctor.label");
        w.getConfig().addOption("PIH:Cabinetmaker", "zl.registration.patient.occupation.cabinetmaker.label");
        w.getConfig().addOption("PIH:Teacher", "zl.registration.patient.occupation.teacher.label");
        w.getConfig().addOption("PIH:Electrician", "zl.registration.patient.occupation.electrician.label");
        w.getConfig().addOption("PIH:Electronics technician", "zl.registration.patient.occupation.electronicsTechnician.label");
        w.getConfig().addOption("PIH:Animal breeder", "zl.registration.patient.occupation.animalBreeder.label");
        w.getConfig().addOption("PIH:STUDENT", "zl.registration.patient.occupation.student.label");
        w.getConfig().addOption("PIH:Beautician", "zl.registration.patient.occupation.beautician.label");
        w.getConfig().addOption("PIH:Florist", "zl.registration.patient.occupation.florist.label");
        w.getConfig().addOption("PIH:Football player", "zl.registration.patient.occupation.footballPlayer.label");
        w.getConfig().addOption("PIH:Foreman", "zl.registration.patient.occupation.foreman.label");
        w.getConfig().addOption("CIEL:162944", "zl.registration.patient.occupation.civilServant.label");
        w.getConfig().addOption("PIH:Bodyguard", "zl.registration.patient.occupation.bodyguard.label");
        w.getConfig().addOption("PIH:Manager", "zl.registration.patient.occupation.manager.label");
        w.getConfig().addOption("PIH:Clerk", "zl.registration.patient.occupation.clerk.label");
        w.getConfig().addOption("PIH:Bailiff", "zl.registration.patient.occupation.bailiff.label");
        w.getConfig().addOption("PIH:NURSE", "zl.registration.patient.occupation.nurse.label");
        w.getConfig().addOption("PIH:Informatician", "zl.registration.patient.occupation.informatician.label");
        w.getConfig().addOption("PIH:Electrical engineer", "zl.registration.patient.occupation.electricalEngineer.label");
        w.getConfig().addOption("PIH:Customs inspector", "zl.registration.patient.occupation.customsInspector.label");
        w.getConfig().addOption("PIH:Journalist", "zl.registration.patient.occupation.journalist.label");
        w.getConfig().addOption("PIH:Judge", "zl.registration.patient.occupation.judge.label");
        w.getConfig().addOption("PIH:Community leader", "zl.registration.patient.occupation.communityLeader.label");
        w.getConfig().addOption("PIH:Laundry worker", "zl.registration.patient.occupation.laundryWorker.label");
        w.getConfig().addOption("PIH:Construction worker", "zl.registration.patient.occupation.constructionWorker.label");
        w.getConfig().addOption("PIH:Butler", "zl.registration.patient.occupation.butler.label");
        w.getConfig().addOption("PIH:FACTORY WORKER", "zl.registration.patient.occupation.factoryWorker.label");
        w.getConfig().addOption("PIH:Warehouse worker", "zl.registration.patient.occupation.materialHandler.label");
        w.getConfig().addOption("PIH:Matron", "zl.registration.patient.occupation.matron.label");
        w.getConfig().addOption("PIH:Mechanic", "zl.registration.patient.occupation.mechanic.label");
        w.getConfig().addOption("PIH:Cleaner", "zl.registration.patient.occupation.cleaner.label");
        w.getConfig().addOption("PIH:HOUSEWORK/FIELDWORK", "zl.registration.patient.occupation.houseworkFieldwork.label");
        w.getConfig().addOption("PIH:Carpenter", "zl.registration.patient.occupation.carpenter.label");
        w.getConfig().addOption("PIH:Messenger", "zl.registration.patient.occupation.messenger.label");
        w.getConfig().addOption("PIH:MINER", "zl.registration.patient.occupation.miner.label");
        w.getConfig().addOption("PIH:Musician", "zl.registration.patient.occupation.musician.label");
        w.getConfig().addOption("PIH:MANUAL LABORER", "zl.registration.patient.occupation.manualLaborer.label");
        w.getConfig().addOption("PIH:Pastry chef", "zl.registration.patient.occupation.pastryChef.label");
        w.getConfig().addOption("PIH:Fisherman", "zl.registration.patient.occupation.fisherman.label");
        w.getConfig().addOption("PIH:Personal assistant", "zl.registration.patient.occupation.personalAssistant.label");
        w.getConfig().addOption("PIH:HEALTH CARE WORKER", "zl.registration.patient.occupation.healthCareWorker.label");
        w.getConfig().addOption("PIH:Photographer", "zl.registration.patient.occupation.photographer.label");
        w.getConfig().addOption("PIH:Plumber", "zl.registration.patient.occupation.plumber.label");
        w.getConfig().addOption("PIH:Police", "zl.registration.patient.occupation.police.label");
        w.getConfig().addOption("PIH:PROFESSIONAL", "zl.registration.patient.occupation.professional.label");
        w.getConfig().addOption("PIH:SHOP OWNER", "zl.registration.patient.occupation.shopOwner.label");
        w.getConfig().addOption("PIH:Radiologist", "zl.registration.patient.occupation.radiologist.label");
        w.getConfig().addOption("PIH:Refrigeration manager", "zl.registration.patient.occupation.refrigerationManager.label");
        w.getConfig().addOption("PIH:Waiter", "zl.registration.patient.occupation.waiter.label");
        w.getConfig().addOption("PIH:Tailor", "zl.registration.patient.occupation.tailor.label");
        w.getConfig().addOption("PIH:Laboratory technician", "zl.registration.patient.occupation.laboratoryTechnician.label");
        w.getConfig().addOption("PIH:Bookkeeper", "zl.registration.patient.occupation.bookkeeper.label");
        w.getConfig().addOption("PIH:Sex worker", "zl.registration.patient.occupation.sexWorker.label");
        w.getConfig().addOption("PIH:Vendor", "zl.registration.patient.occupation.vendor.label");
        w.getConfig().addOption("PIH:Zanmi Lasante employee", "zl.registration.patient.occupation.zlStaff.label");
        w.getConfig().addOption("PIH:RETIRED", "zl.registration.patient.occupation.retired.label");
        w.getConfig().addOption("PIH:UNEMPLOYED", "zl.registration.patient.occupation.unemployed.label");
        w.getConfig().addOption("PIH:OTHER NON-CODED", "zl.registration.patient.occupation.other.label");

        w.getConfig().setExpanded(true);
        w.getConfig().setAutocompleteEnabled(true);
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

    @Override
    public Section getIdentifierSection() {
        Section s = new Section();
        s.setId("patient-identification-section");
        s.setLabel("registrationapp.patient.identifiers.label");

        if (ConfigDescriptor.Specialty.HIV.equals(config.getSpecialty())) {
            s.addQuestion(getHivDossierNumber());
            s.addQuestion(getNumeroIdentificationFiscal());
            s.addQuestion(getCarteDIdentificationNationale());
        }

        return s;
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
