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

        List<Concept> ncdDiagnosesConcepts = getNcdDiagnoses();

        List<Concept> conditionListConcepts = conditionListVelocityContextContentProvider.getActiveConditionsConcepts(patient);

        velocityContext.put("mentalHealthDiagnosesConcepts", mentalHealthDiagnosesConcepts);
        velocityContext.put("ncdDiagnosesConcepts", ncdDiagnosesConcepts);
        velocityContext.put("patientHasMentalHealthCondition",
                CollectionUtils.containsAny(mentalHealthDiagnosesConcepts, conditionListConcepts));
        velocityContext.put("patientHasNcdCondition", CollectionUtils.containsAny(ncdDiagnosesConcepts, conditionListConcepts));
    }

    private List<Concept> getNcdDiagnoses() {
        List<Concept> concepts = new ArrayList<Concept>();

        addProgramConceptToList(concepts, "DIABETES", "PIH");
        addProgramConceptToList(concepts, "HYPERTENSION", "PIH");
        addProgramConceptToList(concepts, "RENAL FAILURE", "PIH");
        addProgramConceptToList(concepts, "HEART FAILURE", "PIH");
        addProgramConceptToList(concepts, "LIVER FAILURE", "PIH");
        addProgramConceptToList(concepts, "ASTHMA", "PIH");
        addProgramConceptToList(concepts, "CHRONIC OBSTRUCTIVE PULMONARY DISEASE", "PIH");
        addProgramConceptToList(concepts, "Sickle-Cell Anemia", "PIH");

        return concepts;
    }

    private List<Concept> getMentalHealthDiagnoses() {
        List<Concept> concepts = new ArrayList<Concept>();
        addProgramConceptToList(concepts, "PSYCHOSIS", "PIH");
        addProgramConceptToList(concepts, "Bipolar disorder", "PIH");
        addProgramConceptToList(concepts, "SCHIZOPHRENIA", "PIH");
        addProgramConceptToList(concepts, "Psychosomatic problems", "PIH");
        addProgramConceptToList(concepts, "Hyperkinetic Behavior", "PIH");
        addProgramConceptToList(concepts, "Conduct disorder", "PIH");
        addProgramConceptToList(concepts, "Mental handicap", "PIH");
        addProgramConceptToList(concepts, "DEMENTIA", "PIH");
        addProgramConceptToList(concepts, "EPILEPSY", "PIH");
        addProgramConceptToList(concepts, "ANXIETY DISORDER", "PIH");
        addProgramConceptToList(concepts, "Post traumatic stress disorder", "PIH");
        addProgramConceptToList(concepts, "130967", "CIEL");
        addProgramConceptToList(concepts, "DEPRESSION", "PIH");
        addProgramConceptToList(concepts, "Manic episode", "PIH");
        addProgramConceptToList(concepts, "Mood disorder", "PIH");

        return concepts;
    }

    private void addProgramConceptToList(List<Concept> concepts, String code, String sourceName) {
        ConceptService conceptService = Context.getConceptService();
        Concept concept = conceptService.getConceptByMapping(code, sourceName);
        if (concept != null) {
            concepts.add(concept);
        }
    }
}
