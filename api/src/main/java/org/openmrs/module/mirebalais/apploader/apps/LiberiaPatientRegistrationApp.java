package org.openmrs.module.mirebalais.apploader.apps;

import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.pihcore.deploy.bundle.core.EncounterRoleBundle;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.registrationapp.model.Field;
import org.openmrs.module.registrationapp.model.PersonAddressWithHierarchyWidget;
import org.openmrs.module.registrationapp.model.Question;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;
import org.openmrs.module.registrationapp.model.Section;

public class LiberiaPatientRegistrationApp extends PatientRegistrationApp {

    @Override
    public RegistrationAppConfig getRegistrationAppConfig() {
        RegistrationAppConfig c = new RegistrationAppConfig();
        c.setAfterCreatedUrl("registrationapp/registrationSummary.page?patientId={{patientId}}");
        c.setPatientDashboardLink(MirebalaisConstants.PATIENT_DASHBOARD_LINK);
        c.setRegistrationEncounter(EncounterTypes.PATIENT_REGISTRATION.uuid(), EncounterRoleBundle.EncounterRoles.ADMINISTRATIVE_CLERK);
        c.setAllowRetrospectiveEntry(true);
        c.setAllowUnknownPatients(false);
        c.setAllowManualIdentifier(false);
        return c;
    }

    @Override
    public Question getAddressQuestion() {
        Question q = new Question();
        q.setId("personAddressQuestion");
        q.setLegend("Person.address");
        q.setDisplayTemplate("{{nvl field.[5] '-'}}, {{field.[4]}}, {{field.[3]}}, {{field.[2]}}");

        Field f = new Field();
        f.setLabel("registrationapp.patient.address.question");
        f.setType("personAddress");

        PersonAddressWithHierarchyWidget w = new PersonAddressWithHierarchyWidget();
        w.getConfig().setShortcutFor("cityVillage");
        w.getConfig().addManualField("address1");
        f.setWidget(toObjectNode(w));

        q.addField(f);
        return q;
    }

    @Override
    public Section getSocialSection() {
        Section s = new Section();
        s.setId("social");
        s.setLabel("zl.registration.patient.social.label");
        s.addQuestion(getCivilStatusQuestion());
        s.addQuestion(getOccupationQuestion());
        return s;
    }

    public Section getContactsSection() {
        Section s = new Section();
        s.setId("contacts");
        s.setLabel("zl.registration.patient.contactPerson.label");
        s.addQuestion(getContactQuestion(false));
        return s;
    }

}



