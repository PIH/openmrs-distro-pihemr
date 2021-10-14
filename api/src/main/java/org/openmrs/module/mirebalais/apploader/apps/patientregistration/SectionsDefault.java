package org.openmrs.module.mirebalais.apploader.apps.patientregistration;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.mirebalais.require.RequireUtil;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.registration.AddressConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.BiometricsConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.ContactInfoConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.ContactPersonConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.DemographicsConfigDescriptor;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.registrationapp.model.DropdownWidget;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.FingerprintWidget;
import org.openmrs.module.registrationapp.model.PersonAddressWithHierarchyWidget;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;
import org.openmrs.module.registrationapp.model.Section;
import org.openmrs.module.registrationapp.model.TextAreaWidget;
import org.openmrs.module.registrationapp.model.TextFieldWidget;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionsDefault {

    private Config config;

    public SectionsDefault(Config config) {
        this.config = config;
    }

    public void addSections(RegistrationAppConfig c) {

        if (config.isComponentEnabled(Components.BIOMETRICS_FINGERPRINTS)) {
            c.addSection(getBiometricsSection());
        }

        c.addSection(getDemographicsSection());
        c.addSection(getContactInfoSection());
        c.addSection(getSocialSection());
        c.addSection(getContactsSection());

        if (config.isComponentEnabled(Components.ID_CARD_PRINTING)) {
            c.addSection(getIdCardPrintSection());
        }
    }

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

    public Question getMothersNameQuestion() {
        Question q = new Question();
        q.setId("mothersFirstNameLabel");
        q.setLegend("zl.registration.patient.mothersFirstName.label");
        q.setHeader("zl.registration.patient.mothersFirstName.question");

        Field f = new Field();
        f.setFormFieldName("mothersFirstName");
        f.setType("personAttribute");
        f.setUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_MOTHERS_FIRST_NAME_UUID);
        f.setWidget(getTextFieldWidget());
        if (config.getRegistrationConfig().getDemographics().getMothersName().getRequired()) {
            f.setCssClasses(Arrays.asList("required"));
        }
        q.addField(f);

        return q;
    }

    public Question getActiveCasefindingQuestion() {
        Question q = new Question();
        q.setId("activeCasefindingLabel");
        q.setLegend("zl.registration.patient.activeCasefinding.label");
        q.setHeader("zl.registration.patient.activeCasefinding.question");

        Field f = new Field();
        f.setFormFieldName("obs.PIH:Found through active casefinding");
        f.setType("obs");
        f.setWidget(getYesNoDropdownWidget());
        if (config.getRegistrationConfig().getSocial().getActiveCasefinding().getRequired()) {
            f.setCssClasses(Arrays.asList("required"));
        }
        q.addField(f);

        return q;
    }

    public Question getCanReadQuestion() {
        Question q = new Question();
        q.setId("canReadLabel");
        q.setLegend("zl.registration.patient.canRead.label");
        q.setHeader("zl.registration.patient.canRead.question");

        Field f = new Field();
        f.setFormFieldName("obs.CIEL:159400");
        f.setType("obs");
        f.setWidget(getYesNoDropdownWidget());
        if (config.getRegistrationConfig().getSocial().getCanRead().getRequired()) {
            f.setCssClasses(Arrays.asList("required"));
        }
        q.addField(f);

        return q;
    }

    public Question getIsDisabledQuestion() {
        Question q = new Question();
        q.setId("isDisabledLabel");
        q.setLegend("zl.registration.patient.isDisabled.label");
        q.setHeader("zl.registration.patient.isDisabled.question");

        Field f = new Field();
        f.setFormFieldName("obs.CIEL:162558");
        f.setType("obs");
        f.setWidget(getYesNoDropdownWidget());
        if (config.getRegistrationConfig().getSocial().getIsDisabled().getRequired()) {
            f.setCssClasses(Arrays.asList("required"));
        }
        q.addField(f);

        return q;
    }

    public Question getIsIndigenousQuestion() {
        Question q = new Question();
        q.setId("isIndigenousLabel");
        q.setLegend("zl.registration.patient.isIndigenous.label");
        q.setHeader("zl.registration.patient.isIndigenous.question");

        Field f = new Field();
        f.setFormFieldName("obs.PIH:Indigenous");
        f.setType("obs");
        f.setWidget(getYesNoDropdownWidget());
        if (config.getRegistrationConfig().getSocial().getIsIndigenous().getRequired()) {
            f.setCssClasses(Arrays.asList("required"));
        }
        q.addField(f);

        return q;
    }

    public Question getIsImmigrantQuestion() {
        Question q = new Question();
        q.setId("isImmigrantLabel");
        q.setLegend("zl.registration.patient.isImmigrant.label");
        q.setHeader("zl.registration.patient.isImmigrant.question");

        Field f = new Field();
        f.setFormFieldName("obs.PIH:Immigrant");
        f.setType("obs");
        f.setWidget(getYesNoDropdownWidget());
        if (config.getRegistrationConfig().getSocial().getIsImmigrant().getRequired()) {
            f.setCssClasses(Arrays.asList("required"));
        }
        q.addField(f);

        return q;
    }

    public Section getContactInfoSection() {
        Section s = new Section();
        s.setId("contactInfo");
        s.setLabel("registrationapp.patient.contactInfo.label");
        s.addQuestion(getAddressQuestion());
        s.addQuestion(getTelephoneNumberQuestion());
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


    public Section getSocialSection() {
        Section s = new Section();
        s.setId("social");
        s.setLabel("zl.registration.patient.social.label");
        s.addQuestion(getBirthplaceQuestion());
        s.addQuestion(getCivilStatusQuestion());
        s.addQuestion(getOccupationQuestion());
        return s;
    }

    public Question getBirthplaceQuestion() {
        Question q = new Question();
        q.setId("birthplaceLabel");
        q.setHeader("zl.registration.patient.birthplace.question");
        q.setLegend("zl.registration.patient.birthplace.label");
        Field f = new Field();
        f.setFormFieldName("obs.PIH:PLACE OF BIRTH");
        f.setType("obs");
        f.setWidget(getTextFieldWidget(50));
        q.addField(f);
        return q;
    }

    public Question getCivilStatusQuestion() {
        Question q = new Question();
        q.setId("civilStatusLabel");
        q.setHeader("zl.registration.patient.civilStatus.question");
        q.setLegend("zl.registration.patient.civilStatus.label");

        Field f = new Field();
        f.setFormFieldName("obs.PIH:CIVIL STATUS");
        f.setType("obs");

        DropdownWidget w = new DropdownWidget();
        w.getConfig().setExpanded(true);
        w.getConfig().addOption("PIH:SINGLE OR A CHILD", "zl.registration.patient.civilStatus.single.label");
        w.getConfig().addOption("PIH:MARRIED", "zl.registration.patient.civilStatus.married.label");
        w.getConfig().addOption("PIH:LIVING WITH PARTNER", "zl.registration.patient.civilStatus.livingWithPartner.label");
        w.getConfig().addOption("PIH:SEPARATED", "zl.h-cesregistration.patient.civilStatus.separated.label");
        w.getConfig().addOption("PIH:DIVORCED", "zl.registration.patient.civilStatus.divorced.label");
        w.getConfig().addOption("PIH:WIDOWED", "zl.registration.patient.civilStatus.widowed.label");
        f.setWidget(toObjectNode(w));

        q.addField(f);
        return q;
    }

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

    public Section getContactsSection() {
        Section s = new Section();
        s.setId("contacts");
        s.setLabel("zl.registration.patient.contactPerson.label");
        s.addQuestion(getContactNameAndRelationship());
        s.addQuestion(getContactAddress());
        s.addQuestion(getContactPhoneNumber());

        return s;
    }

    public Question getContactNameAndRelationship() {
        Question q = new Question();
        q.setId("contactNameLabel");
        q.setLegend("zl.registration.patient.contactPerson.name.label");
        q.setHeader("zl.registration.patient.contactPerson.name.question");

        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:NAMES AND FIRSTNAMES OF CONTACT");
            f.setLabel("zl.registration.patient.contactPerson.contactName.question");
            f.setType("obsgroup");
            if (config.getRegistrationConfig().getContactPerson() != null && config.getRegistrationConfig().getContactPerson().getRequired() == true) {
				f.setCssClasses(Arrays.asList("required"));
			}
            f.setWidget(getTextFieldWidget(30));
            q.addField(f);
        }
        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:RELATIONSHIPS OF CONTACT");
            f.setLabel("zl.registration.patient.contactPerson.relationships.label");
            f.setType("obsgroup");
            if (config.getRegistrationConfig().getContactPerson() != null && config.getRegistrationConfig().getContactPerson().getRequired() == true) {
				f.setCssClasses(Arrays.asList("required"));
			}
            f.setWidget(getTextFieldWidget(30));
            q.addField(f);
        }

        return q;
    }

    public Question getContactAddress() {

        Question q = new Question();
        q.setId("contactQuestionLabel");
        q.setHeader("zl.registration.patient.contactPerson.address.question");
        q.setLegend("zl.registration.patient.contactPerson.address.label");

        Field f = new Field();
        f.setFormFieldName("obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:ADDRESS OF PATIENT CONTACT");
        f.setLabel("zl.registration.patient.contactPerson.address.label");
        f.setType("obsgroup");
        f.setWidget(getTextAreaWidget(250));
        if (config.getRegistrationConfig().getContactPerson() != null && config.getRegistrationConfig().getContactPerson().getRequired() == true) {
			f.setCssClasses(Arrays.asList("required"));
		}
        q.addField(f);

        return q;
    }

    public Question getContactPhoneNumber() {

        Question q = new Question();
        q.setId("contactPhoneNumberQuestionLabel");
        q.setHeader("zl.registration.patient.contactPerson.phonenumber.question");
        q.setLegend("zl.registration.patient.contactPerson.phonenumber.label");

        {
            Field f = new Field();
            f.setFormFieldName("obsgroup.PIH:PATIENT CONTACTS CONSTRUCT.obs.PIH:TELEPHONE NUMBER OF CONTACT");
            f.setLabel("registrationapp.patient.phone.label");
            f.setType("obsgroup");
			ContactPersonConfigDescriptor contactPersonConfig = config.getRegistrationConfig().getContactPerson();
            if (contactPersonConfig != null && contactPersonConfig.getRequired() == true) {
				f.setCssClasses(Arrays.asList("required"));
			}
			if(contactPersonConfig != null && contactPersonConfig.getPhoneNumber() != null
					&& StringUtils.isNotBlank(contactPersonConfig.getPhoneNumber().getRegex())){
				f.setCssClasses(Arrays.asList("regex"));
				f.setWidget(getTextFieldWidget(30, contactPersonConfig.getPhoneNumber().getRegex()));
			} else {
				f.setWidget(getTextFieldWidget(30));
			}
            q.addField(f);
        }

        return q;
    }

    public Section getBiometricsSection() {
        Section s = new Section();
        s.setId("patient-biometrics-section");
        s.setLabel("zl.registration.patient.biometrics.label");
        s.setSkipConfirmation(true);
        s.addQuestion(getBiometricsFingerprintsQuestion());
        s.setRequire(RequireUtil.sessionLocationDoesNotHaveTag("Tablet Entry Location"));
        return s;
    }

    public Question getBiometricsFingerprintsQuestion() {
        Question q = new Question();
        q.setId("biometrics-fieldset");
        q.setCssClasses(Collections.singletonList("no-confirmation"));
        q.setLegend("zl.registration.patient.biometrics.fingerprints.label");
        q.setHeader("zl.registration.patient.biometrics.fingerprints.question");

        Field f = new Field();
        f.setType("fingerprint");
        f.setUuid(Metadata.getBiometricsReferenceNumberIdentifierType().getUuid());
        f.setWidget(getFingerprintWidget());

        q.addField(f);
        return q;
    }

    public ObjectNode getFingerprintWidget() {
        BiometricsConfigDescriptor biometricsConfig = config.getBiometricsConfig();
        FingerprintWidget w = new FingerprintWidget();
        FingerprintWidget.Config c = new FingerprintWidget.Config();
        c.setFormat(biometricsConfig.getTemplateFormat());
        c.setScanUrl(biometricsConfig.getScanUrl());
        c.setDevicesUrl(biometricsConfig.getDevicesUrl());
        c.addFinger(new FingerprintWidget.FingerprintFormField("leftIndexTemplate", "zl.registration.patient.biometrics.fingerprints.leftIndexLabel", "LEFT_INDEX_FINGER"));
        c.addFinger(new FingerprintWidget.FingerprintFormField("rightIndexTemplate", "zl.registration.patient.biometrics.fingerprints.rightIndexLabel", "RIGHT_INDEX_FINGER"));
        w.setConfig(c);
        return toObjectNode(w);
    }

    public Section getIdentifierSection() {
        Section s = new Section();
        s.setId("patient-identification-section");
        s.setLabel("registrationapp.patient.identifiers.label");
        return s;
    }

    protected String getAddressHierarchyDisplayTemplate(List<AddressHierarchyLevel> levels) {
        // We want the display template to hide country, and show a dash if the lowest free-text level is missing
        // TODO: Is this what we want?  Should we show a dash for all empty, non-required fields?
        StringBuilder displayTemplate = new StringBuilder();
        displayTemplate.append("{{nvl field.[" + levels.size() + "] '-'}}");
        for (int i = levels.size() - 1; i >= 2; i--) {
            displayTemplate.append(", {{field.[" + i + "]}}");
        }

        return displayTemplate.toString();
    }

    protected ObjectNode getAddressHierarchyWidget(List<AddressHierarchyLevel> levels,
                                                   Map<String,String> fieldMappings, Boolean required) {
        AddressConfigDescriptor addressConfig = config.getAddressConfig();
        PersonAddressWithHierarchyWidget w = new PersonAddressWithHierarchyWidget();
        if (addressConfig != null) {
            w.getConfig().setShortcutFor(addressConfig.getShortcutField());
            w.getConfig().setManualFields(addressConfig.getManualFields());
        } else {
            // Make the lowest level of the hierarchy free-text, and the second-lowest for the shortcut
            String shortcutFor = levels.get(levels.size() - 2).getAddressField().getName();
            String manualField = levels.get(levels.size() - 1).getAddressField().getName();

            w.getConfig().setShortcutFor(shortcutFor);
            w.getConfig().addManualField(manualField);
        }
        w.getConfig().setFieldMappings(fieldMappings);
        w.getConfig().setRequired(required);

        return toObjectNode(w);
    }

    public Section getIdCardPrintSection() {
        Section s = new Section();
        s.setId("idcardSection");
        s.setLabel("zl.registration.patient.idcard.label");
        s.addQuestion(getIdCardPrintQuestion());
        s.setRequire(RequireUtil.sessionLocationDoesNotHaveTag("Tablet Entry Location"));
        return s;
    }

    public Question getIdCardPrintQuestion() {
        Question q = new Question();
        q.setId("idcardLabel");
        q.setLegend("zl.registration.patient.idcard.label");
        q.setHeader("zl.registration.patient.idcard.question");

        Field f = new Field();
        f.setFormFieldName("obs.PIH:ID Card Printing Requested");
        f.setType("obs");

        f.setWidget(getYesNoDropdownWidget("PIH:YES"));

        q.addField(f);
        return q;
    }

    protected ObjectNode getTextFieldWidget() {
        return getTextFieldWidget(null);
    }

    protected ObjectNode getTextFieldWidget(Integer size) {
        TextFieldWidget w = new TextFieldWidget();
        if (size != null) {
            w.getConfig().setSize(size);
        }
        return toObjectNode(w);
    }

    protected ObjectNode getTextFieldWidget(Integer size, String regex) {
        TextFieldWidget w = new TextFieldWidget();
        if (size != null) {
            w.getConfig().setSize(size);
        }
        if (StringUtils.isNotBlank(regex)) {
            w.getConfig().setRegex(regex);
        }
        return toObjectNode(w);
    }

    protected ObjectNode getTextAreaWidget(Integer maxLength) {
        TextAreaWidget w = new TextAreaWidget();
        if (maxLength != null) {
            w.getConfig().setMaxlength(maxLength);
        }
        return toObjectNode(w);
    }

    protected ObjectNode getYesNoDropdownWidget() {
        return getYesNoDropdownWidget(null);
    }

    protected ObjectNode getYesNoDropdownWidget(String initialValue) {
        DropdownWidget w = new DropdownWidget();
        w.getConfig().setExpanded(true);
        w.getConfig().setHideEmptyLabel(true);
        w.getConfig().setInitialValue(initialValue);
        w.getConfig().addOption("PIH:YES", "coreapps.yes");
        w.getConfig().addOption("PIH:NO", "coreapps.no");
        return toObjectNode(w);
    }

    protected ObjectNode toObjectNode(Object o) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        return mapper.convertValue(o, ObjectNode.class);
    }

}
