package org.openmrs.module.mirebalais.htmlformentry;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.emrapi.disposition.Disposition;
import org.openmrs.module.emrapi.disposition.DispositionDescriptor;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.emrapi.disposition.actions.DispositionAction;
import org.openmrs.module.emrapi.encounter.EncounterDomainWrapper;
import org.openmrs.module.emrapi.test.AuthenticatedUserTestHelper;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore
public class ApplyDispositionActionsTest extends AuthenticatedUserTestHelper {

    private ApplyDispositionActions applyDispositionActions;

    private FormEntrySession session;

    private DispositionService dispositionService;

    private Concept dispositionObsGroupConcept = new Concept();

    private Concept dispositionObsConcept = new Concept();

    private Concept nonDispositionObsGroupConcept = new Concept();

    private Concept nonDispositionObsConcept = new Concept();

    private Concept death = new Concept();

    private Patient patient = new Patient(1);

    private Visit visit = new Visit(1);

    private Encounter encounter;

    private Obs deathDispositionObsGroup = new Obs();

    private Obs deathDispositionObs = new Obs();

    private Obs nonDispositionObsGroup = new Obs();

    private Obs nonDispositionObs = new Obs();

    private Disposition deathDisposition = new Disposition();

    private DispositionAction dischargeIfAdmittedDispositionAction;

    private DispositionAction markPatientDeadDispositionAction;

    @Before
    public void setUp() throws Exception {
        encounter = new Encounter(1);
        encounter.setDateCreated(new Date());
        encounter.setPatient(patient);
        encounter.setVisit(visit);

        deathDispositionObsGroup.setConcept(dispositionObsGroupConcept);
        deathDispositionObs.setConcept(dispositionObsConcept);
        deathDispositionObs.setValueCoded(death);
        deathDispositionObsGroup.addGroupMember(deathDispositionObs);

        nonDispositionObsGroup.setConcept(nonDispositionObsGroupConcept);
        nonDispositionObs.setConcept(nonDispositionObsConcept);
        nonDispositionObsGroup.addGroupMember(nonDispositionObs);

        deathDisposition.setActions(Arrays.asList("dischargeIfAdmittedDispositionAction", "markPatientDeadDispositionAction"));

        dispositionService = mock(DispositionService.class);
        dischargeIfAdmittedDispositionAction = mock(DispositionAction.class);
        markPatientDeadDispositionAction = mock(DispositionAction.class);

        ServiceContext.getInstance().setPersonService(mock(PersonService.class));
        ServiceContext.getInstance().setApplicationContext(mock(ApplicationContext.class));

        HtmlForm htmlForm = new HtmlForm();
        htmlForm.setXmlData("<htmlform></htmlform>");
        session = new FormEntrySession(patient, encounter, FormEntryContext.Mode.ENTER, htmlForm, null);

        DispositionDescriptor dispositionDescriptor = new DispositionDescriptor();
        dispositionDescriptor.setDispositionConcept(dispositionObsConcept);
        dispositionDescriptor.setDispositionSetConcept(dispositionObsGroupConcept);

        when(dispositionService.getDispositionDescriptor()).thenReturn(dispositionDescriptor);
        when(dispositionService.getDispositionFromObsGroup(deathDispositionObsGroup)).thenReturn(deathDisposition);

        applyDispositionActions = new ApplyDispositionActions() {
            @Override
            <T> T getBean(String beanId, Class<T> clazz) {
                if (beanId.equals("dischargeIfAdmittedDispositionAction")) {
                    return (T) dischargeIfAdmittedDispositionAction;
                }
                else if (beanId.equals("markPatientDeadDispositionAction")) {
                    return (T) markPatientDeadDispositionAction;
                }
                else {
                    return null;
                }
            }
        };
    }

    @Test
    public void shouldApplyAllActionsAssociatedWithDeathDisposition() {
        encounter.addObs(deathDispositionObsGroup);
        applyDispositionActions.applyDispositionActions(session, dispositionService);
        verify(dischargeIfAdmittedDispositionAction).action(argThat(new IsExpectedEncounterDomainWrapper(encounter)), eq(deathDispositionObsGroup), anyMap());
        verify(markPatientDeadDispositionAction).action(argThat(new IsExpectedEncounterDomainWrapper(encounter)), eq(deathDispositionObsGroup), anyMap());
    }

    @Test
    public void shouldDoNothingIfNoDispositionSpecified() {
        encounter.addObs(nonDispositionObsGroup);
        applyDispositionActions.applyDispositionActions(session, dispositionService);
        verify(dischargeIfAdmittedDispositionAction, never()).action(argThat(new IsExpectedEncounterDomainWrapper(encounter)), eq(deathDispositionObsGroup), anyMap());
        verify(markPatientDeadDispositionAction,never()).action(argThat(new IsExpectedEncounterDomainWrapper(encounter)), eq(deathDispositionObsGroup), anyMap());
    }

    private class IsExpectedEncounterDomainWrapper extends ArgumentMatcher<EncounterDomainWrapper> {

        private Encounter expectedEncounter;

        public IsExpectedEncounterDomainWrapper(Encounter expectedEncounter) {
            this.expectedEncounter = expectedEncounter;
        }

        @Override
        public boolean matches(Object actualEncounterDomainWrapper) {
            return expectedEncounter.equals(((EncounterDomainWrapper) actualEncounterDomainWrapper).getEncounter());
        }
    }

}