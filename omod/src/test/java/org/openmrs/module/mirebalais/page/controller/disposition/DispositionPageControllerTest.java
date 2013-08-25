package org.openmrs.module.mirebalais.page.controller.disposition;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.concept.EmrConceptService;
import org.openmrs.module.emrapi.disposition.DispositionDescriptor;
import org.openmrs.ui.framework.UiUtils;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DispositionPageControllerTest {

    private EmrApiProperties emrApiProperties;

    private EmrConceptService emrConceptService;

    private UiUtils uiUtils;

    private Concept dispositionObsGroupConcept = new Concept();

    private Concept dispositionObsConcept = new Concept();

    private Concept admission = new Concept();

    private Concept discharge = new Concept();

    private Concept leftWithConsult = new Concept();

    private Concept leftWithoutDischarge = new Concept();

    private Concept transferOut = new Concept();

    private Concept transfer = new Concept();

    private Concept death = new Concept();

    private Patient patient = new Patient(1);

    private Visit visit = new Visit(1);

    private Encounter encounter = new Encounter(1);

    private Obs dispositionObsGroup = new Obs();

    private Obs dispositionObs = new Obs();

    private EncounterType admissionEncounterType = new EncounterType();

    private Map<String,Object> params = new HashMap<String, Object>();

    @Before
    public void setup() {

        encounter.setPatient(patient);
        encounter.setVisit(visit);
        dispositionObsGroup.setConcept(dispositionObsGroupConcept);
        dispositionObs.setConcept(dispositionObsConcept);
        dispositionObs.setValueCoded(admission);
        dispositionObsGroup.addGroupMember(dispositionObs);
        encounter.addObs(dispositionObsGroup);

        emrApiProperties = mock(EmrApiProperties.class);
        emrConceptService = mock(EmrConceptService.class);
        uiUtils = mock(UiUtils.class);

        params.put("patientId", 1);
        params.put("visitId", 2);

        DispositionDescriptor dispositionDescriptor = new DispositionDescriptor();
        dispositionDescriptor.setDispositionConcept(dispositionObsConcept);
        dispositionDescriptor.setDispositionSetConcept(dispositionObsGroupConcept);

        when(emrApiProperties.getDispositionDescriptor()).thenReturn(dispositionDescriptor);
        when(emrApiProperties.getAdmissionEncounterType()).thenReturn(admissionEncounterType);
        when(emrConceptService.getConcept("PIH:ADMIT TO HOSPITAL")).thenReturn(admission);
        when(emrConceptService.getConcept("PIH:DISCHARGED")).thenReturn(discharge);
        when(emrConceptService.getConcept("PIH:Left without seeing a clinician")).thenReturn(leftWithConsult);
        when(emrConceptService.getConcept("PIH:Departed without medical discharge")).thenReturn(leftWithoutDischarge);
        when(emrConceptService.getConcept("PIH:Transfer out of hospital")).thenReturn(transferOut);
        when(emrConceptService.getConcept("PIH:Transfer within hospital")).thenReturn(transfer);
        when(emrConceptService.getConcept("PIH:DEATH")).thenReturn(death);

    }

    @Test
    public void test_shouldRedirectToAdmissionForm() throws Exception {
        dispositionObs.setValueCoded(admission);
        new DispositionPageController().controller(encounter, "1", "1", emrConceptService, emrApiProperties, uiUtils);
        verify(uiUtils).pageLinkWithoutContextPath(eq("htmlformentryui"), eq("htmlform/enterHtmlFormWithStandardUi"), argThat(new ContainsEntry("definitionUiResource", "mirebalais:htmlforms/admissionNote.xml"))) ;
    }

    @Test
    public void test_shouldRedirectToDeathForm() throws Exception {
        dispositionObs.setValueCoded(death);
        new DispositionPageController().controller(encounter, "1", "1", emrConceptService, emrApiProperties, uiUtils);
        verify(uiUtils).pageLinkWithoutContextPath(eq("htmlformentryui"), eq("htmlform/enterHtmlFormWithStandardUi"), argThat(new ContainsEntry("definitionUiResource", "mirebalais:htmlforms/deathNote.xml"))) ;
    }

    @Test
    public void test_shouldRedirectToDischargeForm() throws Exception {

        Encounter admissionEncounter = new Encounter();
        admissionEncounter.setEncounterType(admissionEncounterType);
        visit.addEncounter(admissionEncounter);

        dispositionObs.setValueCoded(discharge);
        new DispositionPageController().controller(encounter, "1", "1", emrConceptService, emrApiProperties, uiUtils);
        verify(uiUtils).pageLinkWithoutContextPath(eq("htmlformentryui"), eq("htmlform/enterHtmlFormWithStandardUi"), argThat(new ContainsEntry("definitionUiResource", "mirebalais:htmlforms/dischargeNote.xml")));
    }

    @Test
    public void test_shouldNotRedirectToAdmissionFormIfPatientIsAlreadyAdmitted() throws Exception {

        Encounter admissionEncounter = new Encounter();
        admissionEncounter.setEncounterType(admissionEncounterType);
        visit.addEncounter(admissionEncounter);

        dispositionObs.setValueCoded(admission);
        new DispositionPageController().controller(encounter, "1", "2", emrConceptService, emrApiProperties, uiUtils);
        verify(uiUtils).pageLinkWithoutContextPath(eq("coreapps"), eq("patientdashboard/patientDashboard"), anyMap());
    }

    @Test
    public void test_shouldNotRedirectToDischargeFormIfPatientNotAdmitted() throws Exception {
        dispositionObs.setValueCoded(discharge);
        new DispositionPageController().controller(encounter,"1", "1", emrConceptService, emrApiProperties, uiUtils);
        verify(uiUtils).pageLinkWithoutContextPath(eq("coreapps"), eq("patientdashboard/patientDashboard"), anyMap());
    }


    private class ContainsEntry extends ArgumentMatcher<Map<String,Object>> {

        private String entry;
        private String value;

        private ContainsEntry(String entry, String value) {
            this.entry = entry;
            this.value = value;
        }

        @Override
        public boolean matches(Object o) {
            assertThat((Map<String,Object>) o, hasEntry(entry, (Object) value));
            return true;
        }
    }


}
