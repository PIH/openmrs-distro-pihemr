package org.openmrs.module.mirebalais.hfe.velocity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.VelocityContext;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class PihVelocityContextContentProvider implements VelocityContextContentProvider {

    @Autowired
    private ConditionListVelocityContextContentProvider conditionListVelocityContextContentProvider;

    @Override
    public void populateContext(FormEntrySession formEntrySession, VelocityContext velocityContext) {
        Patient patient = formEntrySession.getPatient();

        List<Concept> mentalHealthDiagnosesConcepts = getMentalHealthDiagnoses();

        List<Concept> conditionListConcepts = conditionListVelocityContextContentProvider.getActiveConditionsConcepts(patient);

        velocityContext.put("mentalHealthDiagnosesConcepts", mentalHealthDiagnosesConcepts);
        velocityContext.put("patientHasMentalHealthCondition",
                CollectionUtils.containsAny(mentalHealthDiagnosesConcepts, conditionListConcepts));
    }

    private List<Concept> getMentalHealthDiagnoses() {
        List<Concept> concepts = new ArrayList<Concept>();
        addMentalHealthConceptToList(concepts, "PSYCHOSIS", "PIH");
        addMentalHealthConceptToList(concepts, "Bipolar disorder", "PIH");
        addMentalHealthConceptToList(concepts, "SCHIZOPHRENIA", "PIH");
        addMentalHealthConceptToList(concepts, "Psychosomatic problems", "PIH");
        addMentalHealthConceptToList(concepts, "Hyperkinetic Behavior", "PIH");
        addMentalHealthConceptToList(concepts, "Conduct disorder", "PIH");
        addMentalHealthConceptToList(concepts, "Mental handicap", "PIH");
        addMentalHealthConceptToList(concepts, "DEMENTIA", "PIH");
        addMentalHealthConceptToList(concepts, "EPILEPSY", "PIH");
        addMentalHealthConceptToList(concepts, "ANXIETY DISORDER", "PIH");
        addMentalHealthConceptToList(concepts, "Post traumatic stress disorder", "PIH");
        addMentalHealthConceptToList(concepts, "130967", "CIEL");
        addMentalHealthConceptToList(concepts, "DEPRESSION", "PIH");
        addMentalHealthConceptToList(concepts, "Manic episode", "PIH");
        addMentalHealthConceptToList(concepts, "Mood disorder", "PIH");

        return concepts;
    }

    private void addMentalHealthConceptToList(List<Concept> concepts, String code, String sourceName) {
        ConceptService conceptService = Context.getConceptService();
        Concept concept = conceptService.getConceptByMapping(code, sourceName);
        if (concept != null) {
            concepts.add(concept);
        }
    }
}
