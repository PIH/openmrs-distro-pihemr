package org.openmrs.module.mirebalais.htmlformentry;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.coreapps.htmlformentry.CodedOrFreeTextAnswerListWidget;
import org.openmrs.module.emrapi.diagnosis.CodedOrFreeTextAnswer;
import org.openmrs.module.htmlformentry.BadFormDesignException;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionActions;
import org.openmrs.module.htmlformentry.FormSubmissionController;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.HtmlFormEntryConstants;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.htmlformentry.InvalidActionException;
import org.openmrs.module.htmlformentry.action.FormSubmissionControllerAction;
import org.openmrs.module.htmlformentry.handler.SubstitutionTagHandler;
import org.openmrs.module.htmlformentry.widget.ErrorWidget;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.util.OpenmrsUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: eventually generalize this and move it to the reference application
 */
public class CauseOfDeathListTagHandler extends SubstitutionTagHandler {

    public static final String CAUSES_OF_DEATH_CONCEPT = "CIEL:1816";
    public static final String CODED_CAUSE_OF_DEATH_CONCEPT = "CIEL:1814";
    public static final String NON_CODED_CAUSE_OF_DEATH_CONCEPT = "CIEL:160218";
    public static final String DIAGNOSIS_SEQUENCE_CONCEPT = "CIEL:1815";

    @Override
    protected String getSubstitution(FormEntrySession session, FormSubmissionController controllerActions, Map<String, String> parameters) throws BadFormDesignException {
        Concept causeGroupConcept = HtmlFormEntryUtil.getConcept(CAUSES_OF_DEATH_CONCEPT);
        List<Obs> existingObs = session.getContext().removeExistingObs(causeGroupConcept);

        CodedOrFreeTextAnswerListWidget widget = new CodedOrFreeTextAnswerListWidget();
        widget.setUiUtils((UiUtils) session.getAttribute("uiUtils"));
        widget.setBetweenElementsCode("due_to_or_following");
        widget.setTitleCode("cause_of_death");
        widget.setPlaceholderCode("cause_of_death_instructions");
        if (existingObs != null && existingObs.size() > 0) {
            widget.setInitialValue(initialValue(existingObs));
        }

        session.getContext().registerWidget(widget);

        ErrorWidget errorWidget = new ErrorWidget();
        session.getContext().registerErrorWidget(widget, errorWidget);

        session.getSubmissionController().addAction(new Action(widget, errorWidget, existingObs));

        return widget.generateHtml(session.getContext());
    }

    private List<CodedOrFreeTextAnswer> initialValue(List<Obs> existingList) {
        Concept codedCauseConcept = HtmlFormEntryUtil.getConcept(CODED_CAUSE_OF_DEATH_CONCEPT);
        Concept nonCodedCauseConcept = HtmlFormEntryUtil.getConcept(NON_CODED_CAUSE_OF_DEATH_CONCEPT);
        final Concept orderConcept = HtmlFormEntryUtil.getConcept(DIAGNOSIS_SEQUENCE_CONCEPT);

        List<CodedOrFreeTextAnswer> list = new ArrayList<CodedOrFreeTextAnswer>();
        Collections.sort(existingList, new Comparator<Obs>() {
            @Override
            public int compare(Obs left, Obs right) {
                return OpenmrsUtil.compareWithNullAsGreatest(
                        getMemberValueNumeric(orderConcept, left),
                        getMemberValueNumeric(orderConcept, right));
            }
        });
        for (Obs existing : existingList) {
            Obs coded = findMember(existing, codedCauseConcept);
            if (coded != null) {
                list.add(new CodedOrFreeTextAnswer(coded));
            } else {
                Obs nonCoded = findMember(existing, nonCodedCauseConcept);
                list.add(new CodedOrFreeTextAnswer(nonCoded));
            }
        }
        return list;
    }

    private Obs findMember(Obs group, Concept concept) {
        for (Obs candidate : group.getGroupMembers()) {
            if (candidate.getConcept().equals(concept)) {
                return candidate;
            }
        }
        return null;
    }

    private Double getMemberValueNumeric(Concept memberConcept, Obs group) {
        Obs member = findMember(group, memberConcept);
        if (member == null) {
            return null;
        }
        // the Diagnosis Sequence Number concept used to have datatype String, but is now Numeric.
        return member.getValueNumeric() != null ? member.getValueNumeric() : Double.valueOf(member.getValueText());
    }

    public class Action implements FormSubmissionControllerAction {

        private CodedOrFreeTextAnswerListWidget widget;

        private ErrorWidget errorWidget;

        private List<Obs> existingObs;

        public Action(CodedOrFreeTextAnswerListWidget widget, ErrorWidget errorWidget, List<Obs> existingObs) {
            this.widget = widget;
            this.errorWidget = errorWidget;
            this.existingObs = existingObs;
        }

        @Override
        public Collection<FormSubmissionError> validateSubmission(FormEntryContext context, HttpServletRequest submission) {
            List<CodedOrFreeTextAnswer> answers = (List<CodedOrFreeTextAnswer>) widget.getValue(context, submission);
            // TODO is any validation required here?
            return null;
        }

        @Override
        public void handleSubmission(FormEntrySession session, HttpServletRequest submission) {
            Concept causeGroupConcept = HtmlFormEntryUtil.getConcept(CAUSES_OF_DEATH_CONCEPT);
            Concept codedCauseConcept = HtmlFormEntryUtil.getConcept(CODED_CAUSE_OF_DEATH_CONCEPT);
            Concept nonCodedCauseConcept = HtmlFormEntryUtil.getConcept(NON_CODED_CAUSE_OF_DEATH_CONCEPT);
            Concept orderConcept = HtmlFormEntryUtil.getConcept(DIAGNOSIS_SEQUENCE_CONCEPT);

            List<CodedOrFreeTextAnswer> answers = (List<CodedOrFreeTextAnswer>) widget.getValue(session.getContext(), submission);
            FormSubmissionActions submissionActions = session.getSubmissionActions();

            Map<Integer, Obs> existingBySequence = orderBy(existingObs, orderConcept);
            Concept rootCauseOfDeath = null;
            for (int i = 0; i < answers.size(); ++i) {
                int sequence = i + 1; // we want these to be one-based, not zero-based
                CodedOrFreeTextAnswer answer = answers.get(i);
                rootCauseOfDeath = answer.getCodedAnswer();

                Obs existing = existingBySequence.remove(sequence);
                if (same(existing, answer, codedCauseConcept, nonCodedCauseConcept)) {
                    continue;
                }

                if (existing != null) { // just edit the cause within the existing obs group
                    // find the coded or non-coded member of existing
                    Obs replace = null;
                    for (Obs candidate : existing.getGroupMembers()) {
                        if (!candidate.getConcept().equals(orderConcept)) {
                            replace = candidate;
                            break;
                        }
                    }
                    try {
                        submissionActions.beginObsGroup(existing);
                        if (answer.getNonCodedAnswer() != null) {
                            submissionActions.modifyObs(replace, nonCodedCauseConcept, answer.getNonCodedAnswer(), null, null);
                        } else {
                            submissionActions.modifyObs(replace, codedCauseConcept, answer.getValue(), null, null);
                        }
                        submissionActions.endObsGroup();
                    } catch (InvalidActionException e) {
                        throw new IllegalStateException(e);
                    }
                }
                else {
                    // create a whole new obs group

                    Obs valueObs = new Obs();
                    if (answer.getNonCodedAnswer() != null) {
                        valueObs.setConcept(nonCodedCauseConcept);
                        valueObs.setValueText(answer.getNonCodedAnswer());
                    } else {
                        valueObs.setConcept(codedCauseConcept);
                        if (answer.getSpecificCodedAnswer() != null) {
                            valueObs.setValueCodedName(answer.getSpecificCodedAnswer());
                            valueObs.setValueCoded(answer.getSpecificCodedAnswer().getConcept());
                        } else {
                            valueObs.setValueCoded(answer.getCodedAnswer());
                        }
                    }

                    Obs orderObs = new Obs();
                    orderObs.setConcept(orderConcept);
                    if (orderConcept.getDatatype().isNumeric()) {
                        orderObs.setValueNumeric(Double.valueOf(sequence));
                    } else if (orderConcept.getDatatype().isText()) {
                        orderObs.setValueText(Integer.toString(sequence));
                    } else {
                        throw new IllegalStateException("Sequence concept (" + orderConcept.getName() + ") must be Numeric or Text");
                    }

                    Obs group = new Obs();
                    group.setConcept(causeGroupConcept);
                    group.addGroupMember(orderObs);
                    group.addGroupMember(valueObs);

                    try {
                        submissionActions.beginObsGroup(group);
                        submissionActions.endObsGroup();
                    } catch (InvalidActionException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }

            // anything left in existingBySequence at this point should be voided since it wasn't resubmitted
            for (Obs groupToVoid : existingBySequence.values()) {
                submissionActions.modifyObs(groupToVoid, null, null, null, null);
            }

            // now set Cause Of Death on the patient's person record
            if (rootCauseOfDeath == null) {
                rootCauseOfDeath = getUnknownConcept();
            }
            submissionActions.getCurrentPerson().setDead(true);
            submissionActions.getCurrentPerson().setCauseOfDeath(rootCauseOfDeath);
            submissionActions.setPatientUpdateRequired(true);
        }

        private Concept getUnknownConcept() {
            try {
                String conceptId = Context.getAdministrationService().getGlobalProperty(HtmlFormEntryConstants.GP_UNKNOWN_CONCEPT);
                return HtmlFormEntryUtil.getConcept(conceptId);
            } catch (Exception ex) {
                throw new IllegalStateException("Error looking up \"Unknown\" concept for cause of death, which must be " +
                        "specified in a global property called \"" + HtmlFormEntryConstants.GP_UNKNOWN_CONCEPT + "\".", ex);
            }
        }

        /**
         * Checks for the same cause (assumes sequence order was verified by the caller)
         */
        private boolean same(Obs group, CodedOrFreeTextAnswer answer, Concept codedCauseConcept, Concept nonCodedCauseConcept) {
            if (group == null) {
                return false;
            }
            for (Obs member : group.getGroupMembers()) {
                if (member.getConcept().equals(codedCauseConcept)) {
                    if (answer.getSpecificCodedAnswer() != null) {
                        return answer.getSpecificCodedAnswer().equals(member.getValueCodedName());
                    } else if (answer.getCodedAnswer() != null) {
                        return answer.getCodedAnswer().equals(member.getValueCoded());
                    } else {
                        return false;
                    }
                }
                else if (member.getConcept().equals(nonCodedCauseConcept)) {
                    return answer.getNonCodedAnswer().equals(member.getValueText());
                }
            }
            return false;
        }

        private Map<Integer, Obs> orderBy(List<Obs> obsList, Concept concept) {
            Map<Integer, Obs> map = new HashMap<Integer, Obs>();
            if (obsList != null) {
                for (Obs group : obsList) {
                    Obs sequenceObs = findMember(group, concept);
                    int sequenceVal = sequenceObs.getValueNumeric() != null ? sequenceObs.getValueNumeric().intValue() :
                            Integer.valueOf(sequenceObs.getValueText());
                    map.put(sequenceVal, group);
                }
            }
            return map;
        }

    }

}
