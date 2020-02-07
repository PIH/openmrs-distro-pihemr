package org.openmrs.module.mirebalais.hfe.velocity;

import org.apache.velocity.VelocityContext;
import org.openmrs.Concept;
import org.openmrs.Condition;
import org.openmrs.Patient;
import org.openmrs.api.ConditionService;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ConditionListVelocityContextContentProvider implements VelocityContextContentProvider {

    @Autowired
    private ConditionService conditionService;

    @Override
    public void populateContext(FormEntrySession formEntrySession, VelocityContext velocityContext) {
        Patient patient = formEntrySession.getPatient();

        List<Condition> conditionList = getActiveConditions(patient);

        List<Concept> conditionListConcepts = getActiveConditionsConcepts(patient);

        velocityContext.put("conditionList", conditionList);
        velocityContext.put("conditionListConcepts", conditionListConcepts);
    }

    protected List<Condition> getActiveConditions(Patient patient) {
        return conditionService.getActiveConditions(patient);
    }

    protected List<Concept> getActiveConditionsConcepts(Patient patient) {
        List<Condition> conditionList = getActiveConditions(patient);
        List<Concept> conditionListConcepts = new ArrayList<Concept>();
        for (Condition condition : conditionList) {
            if (condition.getCondition().getCoded() != null) {
                conditionListConcepts.add(condition.getCondition().getCoded());
            }
        }

        return conditionListConcepts;
    }
}
