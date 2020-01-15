package org.openmrs.module.mirebalais.hfe.velocity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.VelocityContext;
import org.openmrs.Concept;
import org.openmrs.Condition;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ConditionService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//this is a test
@Component
public class LrVelocityContextContentProvider implements VelocityContextContentProvider {

    @Override
    public void populateContext(FormEntrySession formEntrySession, VelocityContext velocityContext) {
        Patient patient = formEntrySession.getPatient();

        ConditionService conditionService = Context.getConditionService();

        List<Condition> conditionList = conditionService.getActiveConditions(patient);

        List<Concept> conditionListConcepts = new ArrayList<Concept>();
        for (Condition condition : conditionList) {
            if (condition.getCondition().getCoded() != null) {
                conditionListConcepts.add(condition.getCondition().getCoded());
            }
        }

        List<Concept> mentalHealthDiagnosesConcepts = getMentalHealthDiagnoses();

        velocityContext.put("conditionList", conditionList);
        velocityContext.put("conditionListConcepts", conditionListConcepts);
        velocityContext.put("mentalHealthDiagnosesConcepts", mentalHealthDiagnosesConcepts);
        velocityContext.put("patientHasMentalHealthCondition",
                CollectionUtils.containsAny(mentalHealthDiagnosesConcepts, conditionListConcepts));


	//NCD diagnosis concept lists
        List<Concept> ncdDiagnosesConcepts = getNcdDiagnoses();

        velocityContext.put("ncdDiagnosesConcepts", ncdDiagnosesConcepts);
	velocityContext.put("patientHasNcdCondition",
                CollectionUtils.containsAny(ncdDiagnosesConcepts, conditionListConcepts));

    }

    private List<Concept> getMentalHealthDiagnoses() {
        ConceptService conceptService = Context.getConceptService();
        return new ArrayList<Concept>(Arrays.asList(
                conceptService.getConceptByMapping("PSYCHOSIS", "PIH"),
                conceptService.getConceptByMapping("Bipolar disorder", "PIH"),
                conceptService.getConceptByMapping("SCHIZOPHRENIA", "PIH"),
                conceptService.getConceptByMapping("Psychosomatic problems", "PIH"),
                conceptService.getConceptByMapping("Hyperkinetic Behavior", "PIH"),
                conceptService.getConceptByMapping("Conduct disorder", "PIH"),
                conceptService.getConceptByMapping("Mental handicap", "PIH"),
                conceptService.getConceptByMapping("DEMENTIA", "PIH"),
                conceptService.getConceptByMapping("EPILEPSY", "PIH"),
                conceptService.getConceptByMapping("ANXIETY DISORDER", "PIH"),
                conceptService.getConceptByMapping("Post traumatic stress disorder", "PIH"),
                conceptService.getConceptByMapping("130967", "CIEL"),
                conceptService.getConceptByMapping("DEPRESSION", "PIH"),
                conceptService.getConceptByMapping("Manic episode", "PIH"),
                conceptService.getConceptByMapping("Mood disorder", "PIH")));
    }


    //NCD possible diagnosis list
    private List<Concept> getNcdDiagnoses() {
        ConceptService conceptService = Context.getConceptService();
        return new ArrayList<Concept>(Arrays.asList(
                conceptService.getConceptByMapping("DIABETES", "PIH"),
                conceptService.getConceptByMapping("HYPERTENSION", "PIH"),
                conceptService.getConceptByMapping("RENAL FAILURE, CHRONIC", "PIH"),
                conceptService.getConceptByMapping("CHRONIC HIV LUNG DISEASE", "PIH"),
                conceptService.getConceptByMapping("EPILEPSY", "PIH"),
                conceptService.getConceptByMapping("HEART FAILURE", "PIH"),
                conceptService.getConceptByMapping("LIVER FAILURE", "PIH"),
                conceptService.getConceptByMapping("SPLENOMEGALY", "PIH")));
    }
}
