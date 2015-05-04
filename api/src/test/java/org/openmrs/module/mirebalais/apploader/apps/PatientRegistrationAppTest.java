package org.openmrs.module.mirebalais.apploader.apps;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.deploy.bundle.core.EncounterRoleBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.EncounterTypeBundle;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests the configuration of the patient registration app
 */
public class PatientRegistrationAppTest {

    @Test
    public void shouldCreateAppDescriptor() throws Exception {

        PatientRegistrationApp app = new PatientRegistrationApp();
        AppDescriptor d = app.build(new Config());

        assertThat(d.getId(), is(CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION));
        assertThat(d.getDescription(), is("registrationapp.registerPatient"));
        assertThat(d.getLabel(), is("registrationapp.app.registerPatient.label"));
        assertThat(d.getIcon(), is("icon-user"));
        assertThat(d.getUrl(), is("registrationapp/findPatient.page?appId=" + CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION));
        assertThat(d.getRequiredPrivilege(), is("App: registrationapp.registerPatient"));

        assertThat(d.getConfig().get("afterCreatedUrl").getTextValue(), is("mirebalais/patientRegistration/afterRegistration.page?patientId={{patientId}}&encounterId={{encounterId}}"));
        assertThat(d.getConfig().get("patientDashboardLink").getTextValue(), is(MirebalaisConstants.PATIENT_DASHBOARD_LINK));
        assertThat(d.getConfig().get("registrationEncounter").get("encounterType").getTextValue(), is(EncounterTypeBundle.EncounterTypes.PATIENT_REGISTRATION));
        assertThat(d.getConfig().get("registrationEncounter").get("encounterRole").getTextValue(), is(EncounterRoleBundle.EncounterRoles.ADMINISTRATIVE_CLERK));
        assertTrue(d.getConfig().get("allowRetrospectiveEntry").getBooleanValue());
        assertTrue(d.getConfig().get("allowUnknownPatients").getBooleanValue());
        assertTrue(d.getConfig().get("allowManualIdentifier").getBooleanValue());

        JsonNode demographicsSection = assertSectionFound(d.getConfig(), 0, "demographics", "", 2);
        assertPersonAttributeQuestionFound(demographicsSection, 0, PersonAttributeTypes.MOTHERS_FIRST_NAME.uuid(), true);
        assertPersonAttributeQuestionFound(demographicsSection, 1, PersonAttributeTypes.BIRTHPLACE.uuid(), true);

        JsonNode contactInfoSection = assertSectionFound(d.getConfig(), 1, "contactInfo", "registrationapp.patient.contactInfo.label", 2);
        assertSingleFieldQuestion(contactInfoSection, 0, "personAddress");
        assertPersonAttributeQuestionFound(contactInfoSection, 1, PersonAttributeTypes.TELEPHONE_NUMBER.uuid(), false);

        JsonNode socialSection = assertSectionFound(d.getConfig(), 2, "social", "zl.registration.patient.social.label", 3);
        assertObsQuestionFound(socialSection, 0, "obs.PIH:CIVIL STATUS");
        assertObsQuestionFound(socialSection, 1, "obs.PIH:2452");
        assertObsQuestionFound(socialSection, 2, "obs.PIH:Religion");

        assertSectionFound(d.getConfig(), 3, "contacts", "zl.registration.patient.contactPerson.label", 1);
    }

    private JsonNode assertSectionFound(JsonNode app, int sectionNumber, String id, String label, int numQuestions) {
        JsonNode s = getNodeList(app, "sections").get(sectionNumber);
        assertThat(s.get("id").getValueAsText(), is(id));
        assertThat(s.get("label").getValueAsText(), is(label));
        assertThat(getNodeList(s, "questions").size(), is(numQuestions));
        return s;
    }

    private JsonNode assertSingleFieldQuestion(JsonNode section, int questionNumber, String fieldType) {
        List<JsonNode> questions = getNodeList(section, "questions");
        List<JsonNode> fields = getNodeList(questions.get(questionNumber), "fields");
        assertEquals(fields.size(), 1);
        JsonNode field = fields.get(0);
        assertThat(field.get("type").getTextValue(), is(fieldType));
        return field;
    }

    private void assertPersonAttributeQuestionFound(JsonNode section, int questionNumber, String uuid, boolean required) {
        JsonNode field = assertSingleFieldQuestion(section, questionNumber, "personAttribute");
        assertThat(field.get("uuid").getTextValue(), is(uuid));
        JsonNode cssClasses = field.get("cssClasses");
        assertEquals(cssClasses != null && cssClasses.get(0).getTextValue().equals("required"), required);
    }

    private void assertObsQuestionFound(JsonNode section, int questionNumber, String concept) {
        JsonNode field = assertSingleFieldQuestion(section, questionNumber, "obs");
        assertThat(field.get("type").getTextValue(), is("obs"));
        assertThat(field.get("formFieldName").getTextValue(), is(concept));
    }

    private List<JsonNode> getNodeList(JsonNode node, String elementName) {
        List<JsonNode> ret = new ArrayList<JsonNode>();
        for (Iterator<JsonNode> sectionIter = node.get(elementName).getElements(); sectionIter.hasNext();) {
            ret.add(sectionIter.next());
        }
        return ret;
    }
}
