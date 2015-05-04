package org.openmrs.module.mirebalais.htmlformentry;

import org.junit.Before;
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
import org.openmrs.module.pihcore.deploy.bundle.core.concept.ClinicalConsultationConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CommonConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.conceptSource;

public class PastMedicalHistoryCheckboxTagHandlerTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private TestDataManager testData;

    private Concept yes;
    private Concept ashthma;
    private Concept sti;
    private Concept epilepsy;

    @Before
    public void setUp() throws Exception {
        setupConcepts();

        ashthma = testData.concept().name("Asthma").datatype("N/A").conceptClass("Misc").uuid("3ccc4bf6-26fe-102b-80cb-0017a47871b2").save();
        sti = testData.concept().name("STI").datatype("N/A").conceptClass("Misc").uuid("3cce6116-26fe-102b-80cb-0017a47871b2").save();
        epilepsy = testData.concept().name("Epilepsy").datatype("N/A").conceptClass("Misc").uuid("3cce0a90-26fe-102b-80cb-0017a47871b2").save();

        HtmlFormSetup.setupHtmlFormEntryTagHandlers();
    }

    private Concept install(Concept concept) {
        return conceptService.saveConcept(concept);
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

        ConceptSource ciel = conceptSource("CIEL", "Columbia International eHealth Laboratory concept ID", null, CoreConceptMetadataBundle.ConceptSources.CIEL);
        conceptService.saveConceptSource(ciel);

        // these are in standardTestDataset.xml
        Concept no = conceptService.getConcept(8);
        Concept unknown = conceptService.getConcept(22);
        yes = conceptService.getConcept(7); // need this to have a different UUID to match our dictionary
        yes.setUuid(CommonConcepts.Concepts.YES);
        conceptService.saveConcept(yes);

        Concept pmhWhich = install(new ConceptBuilder(ClinicalConsultationConcepts.Concepts.PAST_MEDICAL_HISTORY_FINDING)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("1908BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Past medical history finding", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("1470FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Coded list of past medical history problems but not procedures which are coded under Past Surgical History.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("171868ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1628").build())
                .build());

        Concept isSymptomPresent = install(new ConceptBuilder(ClinicalConsultationConcepts.Concepts.PAST_MEDICAL_HISTORY_PRESENCE)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("2009BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Sign/symptom present", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no, unknown)
                .mapping(new ConceptMapBuilder("171968ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1729").build())
                .build());

        // TODO waiting for confirmation from Andy that this is appropriate as comments
        Concept pmhComment = install(new ConceptBuilder(ClinicalConsultationConcepts.Concepts.PAST_MEDICAL_HISTORY_COMMENT)
                .datatype(text)
                .conceptClass(question)
                .name("108123BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Past medical history added (text)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("217354ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "160221").build())
                .build());

        Concept pmhConstruct = install(new ConceptBuilder(ClinicalConsultationConcepts.Concepts.PAST_MEDICAL_HISTORY_CONSTRUCT)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1913BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Past medical history", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("86916BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "PMH", Locale.ENGLISH, ConceptNameType.SHORT)
                .setMembers(pmhWhich, isSymptomPresent, pmhComment)
                .mapping(new ConceptMapBuilder("171873ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                        .type(sameAs).ensureTerm(ciel, "1633").build())
                .build());
    }

    @Test
    public void testCreateAndEdit() throws Exception {
        final Date date = new Date();
        final Concept construct = MetadataUtils.existing(Concept.class, ClinicalConsultationConcepts.Concepts.PAST_MEDICAL_HISTORY_CONSTRUCT);
        final Concept which = MetadataUtils.existing(Concept.class, ClinicalConsultationConcepts.Concepts.PAST_MEDICAL_HISTORY_FINDING);
        final Concept presence = MetadataUtils.existing(Concept.class, ClinicalConsultationConcepts.Concepts.PAST_MEDICAL_HISTORY_PRESENCE);
        final Concept comment = MetadataUtils.existing(Concept.class, ClinicalConsultationConcepts.Concepts.PAST_MEDICAL_HISTORY_COMMENT);

        new RegressionTestHelper() {
            @Override
            public String getXmlDatasetPath() {
                return "";
            }

            @Override
            public String getFormName() {
                return "pastMedicalHistoryForm";
            }

            @Override
            public String[] widgetLabels() {
                return new String[] { "Date:", "Location:", "Provider:", "Asthma:", "Epilepsy:", "STI:", "zl.pastMedicalHistory.specifyLabel" };
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
                request.setParameter(widgets.get("Asthma:"), "present");
                request.setParameter(widgets.get("STI:"), "present");
                request.setParameter(widgets.get("zl.pastMedicalHistory.specifyLabel"), "");
            }

            @Override
            public void testResults(SubmissionResults results) {
                results.assertNoErrors();
                results.assertEncounterCreated();
                results.assertObsGroupCreatedCount(2);
                results.assertObsGroupCreated(construct.getConceptId(),
                        which.getId(), ashthma,
                        presence.getId(), yes);
                results.assertObsGroupCreated(construct.getConceptId(),
                        which.getId(), sti,
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
                request.removeParameter(widgets.get("Epilepsy:")); // this is an unchecked checkbox (but HFE isn't smart enough to know this)
                request.setParameter(widgets.get("STI:"), "present");
                request.setParameter(widgets.get("zl.pastMedicalHistory.specifyLabel"), "HIV");
            }

            @Override
            public void testEditedResults(SubmissionResults results) {
                results.assertNoErrors();
                results.assertEncounterCreated();

                // there should be one voided obs group (asthma)
                results.assertObsVoided(construct.getConceptId(), null);

                // there should be one obs group (STI), now also with a comment
                results.assertObsGroupCreatedCount(1);
                results.assertObsGroupCreated(construct.getConceptId(),
                        which.getId(), sti,
                        presence.getId(), yes,
                        comment.getId(), "HIV");
            }
        }.run();
    }

}