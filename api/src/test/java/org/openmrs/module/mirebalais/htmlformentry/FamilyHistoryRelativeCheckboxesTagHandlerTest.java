package org.openmrs.module.mirebalais.htmlformentry;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.htmlformentry.RegressionTestHelper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.mirebalais.setup.HtmlFormSetup;
import org.openmrs.module.pihcore.deploy.bundle.ClinicalConsultationConcepts;
import org.openmrs.module.pihcore.deploy.bundle.CommonConcepts;
import org.openmrs.module.pihcore.deploy.bundle.CoreConceptMetadataBundle;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.conceptSource;

@Ignore
public class FamilyHistoryRelativeCheckboxesTagHandlerTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private TestDataManager testData;

    private Concept ashthma;
    private Concept cancer;
    private Concept father;
    private Concept yes;

    @Before
    public void setUp() throws Exception {
        father = testData.concept().name("Father").datatype("N/A").conceptClass("Misc").uuid("3ce18444-26fe-102b-80cb-0017a47871b2").save();
        setupConcepts();
        ashthma = testData.concept().name("Asthma").datatype("N/A").conceptClass("Misc").uuid("3ccc4bf6-26fe-102b-80cb-0017a47871b2").save();
        cancer = testData.concept().name("Cancer").datatype("N/A").conceptClass("Misc").uuid("ac2e94e4-c86f-4b68-99d3-5b9acc762949").save();

        HtmlFormSetup.setupHtmlFormEntryTagHandlers();
    }

    private void setupConcepts() throws Exception {
        // copied from some pihcore concept setup, but tweaked to work with standardTestDataset.xml

        ConceptDatatype coded = MetadataUtils.existing(ConceptDatatype.class, CoreConceptMetadataBundle.ConceptDatatypes.CODED);
        ConceptDatatype text = MetadataUtils.existing(ConceptDatatype.class, CoreConceptMetadataBundle.ConceptDatatypes.TEXT);
        ConceptDatatype notApplicable = MetadataUtils.existing(ConceptDatatype.class, CoreConceptMetadataBundle.ConceptDatatypes.N_A);
        ConceptClass diagnosis = MetadataUtils.existing(ConceptClass.class, "938834bf-a745-4dbd-b611-f06a9a5a3060");
        ConceptClass question = MetadataUtils.existing(ConceptClass.class, "a82ef63c-e4e4-48d6-988a-fdd74d7541a7");
        ConceptClass misc = MetadataUtils.existing(ConceptClass.class, "ecdee8a7-d741-4fe7-8e01-f79cacbe97bc");
        ConceptMapType sameAs = MetadataUtils.existing(ConceptMapType.class, CoreConceptMetadataBundle.ConceptMapTypes.SAME_AS);
        // standardTestDataset actually has "is-a" instead of narrower than. Hopefully this works for our test
        ConceptMapType narrowerThan = MetadataUtils.existing(ConceptMapType.class, "1ce7a784-7d8f-11e1-909d-c80aa9edcf4e");

        ConceptSource ciel = conceptSource("CIEL", "Columbia International eHealth Laboratory concept ID", null, CoreConceptMetadataBundle.ConceptSources.CIEL);
        conceptService.saveConceptSource(ciel);

        // these are in standardTestDataset.xml
        Concept no = conceptService.getConcept(8);
        Concept unknown = conceptService.getConcept(22);
        yes = conceptService.getConcept(7); // need this to have a different UUID to match our dictionary
        yes.setUuid(CommonConcepts.Concepts.YES);
        conceptService.saveConcept(yes);

        Concept famHxDiagnosis = install(new ConceptBuilder(ClinicalConsultationConcepts.Concepts.FAMILY_HISTORY_DIAGNOSIS)
                .datatype(coded)
                .conceptClass(misc) // CIEL says Diagnosis, but this is wrong
                .name("109016BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Family history diagnosis", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("217715ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "160592").build())
                .build());

        // CIEL:1560 (Family Member) is both a question and an answer. At PIH we have two existing concepts for this purpose.
        // This one is only used as a question, so we mark it as NARROWER-THAN CIEL:1560. (And we want to test this here.)
        Concept famHxRelationship = install(new ConceptBuilder(ClinicalConsultationConcepts.Concepts.FAMILY_HISTORY_RELATIONSHIP)
                .datatype(coded)
                .conceptClass(question)
                .name("3e2256c6-26fe-102b-80cb-0017a47871b2", "RELATIONSHIP OF RELATIVE TO PATIENT", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("93bc0d72-07d4-102c-b5fa-0017a47871b2", "CONTACT_REL", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("f672a35a-d5db-102d-ad2a-000c29c2a5d7", "RELATION DU PARENT PAR RAPPORT AU PATIENT", Locale.FRENCH, null) // locale-preferred
                .description("ecf0bb66-07fe-102c-b5fa-0017a47871b2", "What is the relationship of this relative to this patient?", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("e9335480-d593-11e4-9dcf-b36e1005e77b")
                        .type(narrowerThan).ensureTerm(ciel, "1560").build())
                .answers(father) // other options not needed for this test
                .build());

        Concept famHxComment = install(new ConceptBuilder(ClinicalConsultationConcepts.Concepts.FAMILY_HISTORY_COMMENT)
                        .datatype(text)
                        .conceptClass(question)
                        .name("109055BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Family history comment", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                        .mapping(new ConceptMapBuilder("217741ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                                .type(sameAs).ensureTerm(ciel, "160618").build())
                        .build());

        Concept isSymptomPresent = install(new ConceptBuilder(ClinicalConsultationConcepts.Concepts.PAST_MEDICAL_HISTORY_PRESENCE)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("2009BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Sign/symptom present", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no, unknown)
                .mapping(new ConceptMapBuilder("171968ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1729").build())
                .build());

        Concept famHxConstruct = install(new ConceptBuilder(ClinicalConsultationConcepts.Concepts.FAMILY_HISTORY_CONSTRUCT)
                .datatype(notApplicable)
                .conceptClass(misc) // CIEL says Finding but that's not necessary for the test
                .name("109017BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Patient's family history list", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("109019BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Family history list", Locale.ENGLISH, null)
                .setMembers(
                        famHxDiagnosis,
                        famHxRelationship,
                        isSymptomPresent,
                        famHxComment)
                .mapping(new ConceptMapBuilder("217716ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "160593").build())
                .build());
    }

    private Concept install(Concept concept) {
        return conceptService.saveConcept(concept);
    }

    @Test
    public void testCreateAndEdit() throws Exception {
        final Date date = new Date();
        final Concept construct = MetadataUtils.existing(Concept.class, ClinicalConsultationConcepts.Concepts.FAMILY_HISTORY_CONSTRUCT);
        final Concept which = MetadataUtils.existing(Concept.class, ClinicalConsultationConcepts.Concepts.FAMILY_HISTORY_DIAGNOSIS);
        final Concept relative = MetadataUtils.existing(Concept.class, ClinicalConsultationConcepts.Concepts.FAMILY_HISTORY_RELATIONSHIP);
        final Concept presence = MetadataUtils.existing(Concept.class, ClinicalConsultationConcepts.Concepts.FAMILY_HISTORY_PRESENCE);
        final Concept comment = MetadataUtils.existing(Concept.class, ClinicalConsultationConcepts.Concepts.FAMILY_HISTORY_COMMENT);

        new RegressionTestHelper() {
            @Override
            public String getXmlDatasetPath() {
                return "";
            }

            @Override
            public String getFormName() {
                return "familyHistoryForm";
            }

            @Override
            public String[] widgetLabels() {
                // Cancer:!!1 is because we need to skip the text field that comes before the checkbox
                return new String[]{"Date:", "Location:", "Provider:", "Asthma:", "Cancer:!!1", "zl.familyHistoryRelativeCheckboxes.specifyLabel"};
            }

            @Override
            public void testBlankFormHtml(String html) {
                System.out.println(html);
            }

            @Override
            public void setupRequest(MockHttpServletRequest request, Map<String, String> widgets) {
                request.setParameter(widgets.get("Date:"), dateAsString(date));
                request.setParameter(widgets.get("Location:"), "2");
                request.setParameter(widgets.get("Provider:"), "1");
                request.setParameter(widgets.get("Asthma:"), father.getUuid());
                request.setParameter(widgets.get("Cancer:!!1"), father.getUuid());
                request.setParameter(widgets.get("zl.familyHistoryRelativeCheckboxes.specifyLabel"), "");
            }

            @Override
            public void testResults(SubmissionResults results) {
                results.assertNoErrors();
                results.assertEncounterCreated();
                results.assertObsGroupCreatedCount(2);
                results.assertObsGroupCreated(construct.getConceptId(),
                        which.getId(), ashthma,
                        relative.getId(), father,
                        presence.getId(), yes);
                results.assertObsGroupCreated(construct.getConceptId(),
                        which.getId(), cancer,
                        relative.getId(), father,
                        presence.getId(), yes);
            }

            @Override
            public boolean doEditEncounter() {
                return true;
            }

            @Override
            public String[] widgetLabelsForEdit() {
                return widgetLabels();
            }

            @Override
            public void setupEditRequest(MockHttpServletRequest request, Map<String, String> widgets) {
                request.setParameter(widgets.get("Date:"), dateAsString(date));
                request.setParameter(widgets.get("Location:"), "2");
                request.setParameter(widgets.get("Provider:"), "1");
                request.removeParameter(widgets.get("Asthma:")); // this is an unchecked checkbox (but HFE isn't smart enough to know this)
                request.setParameter(widgets.get("Cancer:!!1"), father.getUuid());
                request.setParameter(widgets.get("zl.familyHistoryRelativeCheckboxes.specifyLabel"), "Melanoma");
            }

            @Override
            public void testEditedResults(SubmissionResults results) {
                results.assertNoErrors();
                results.assertEncounterCreated();

                // there should be one voided obs group (asthma)
                results.assertObsVoided(construct.getConceptId(), null);

                // there should be one obs group (Cancer), now also with a comment
                results.assertObsGroupCreatedCount(1);
                results.assertObsGroupCreated(construct.getConceptId(),
                        which.getId(), cancer,
                        relative.getId(), father,
                        presence.getId(), yes,
                        comment.getId(), "Melanoma");
            }
        }.run();
    }

}