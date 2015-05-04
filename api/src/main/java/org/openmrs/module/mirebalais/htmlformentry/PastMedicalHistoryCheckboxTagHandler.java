package org.openmrs.module.mirebalais.htmlformentry;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.htmlformentry.BadFormDesignException;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionActions;
import org.openmrs.module.htmlformentry.FormSubmissionController;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.htmlformentry.InvalidActionException;
import org.openmrs.module.htmlformentry.action.FormSubmissionControllerAction;
import org.openmrs.module.htmlformentry.handler.AttributeDescriptor;
import org.openmrs.module.htmlformentry.handler.SubstitutionTagHandler;
import org.openmrs.module.htmlformentry.widget.CheckboxWidget;
import org.openmrs.module.htmlformentry.widget.ErrorWidget;
import org.openmrs.module.htmlformentry.widget.TextFieldWidget;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CommonConcepts;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

/**
 * Usage:
 * <pastMedicalHistoryCheckbox concept="uuid-of-asthma" />
 * => [ ] Asthma
 * <pastMedicalHistoryCheckbox concept="uuid-of-STI" label="STI" specify="true"/>
 * => [ ] STI, specify:_______
 *
 * If you don't specify label, the preferred concept name in the context locale will be used
 *
 * If checked, will be stored as an obs group with concept CIEL:1633, and members:
 * * which? CIEL:1628 = @concept
 * * present? CIEL:1729 = YES
 * * comments: CIEL:160221 = comment text
 */
public class PastMedicalHistoryCheckboxTagHandler extends SubstitutionTagHandler {

    public static final String PRESENT = "present";

    private MessageSourceService messageSourceService;

    public PastMedicalHistoryCheckboxTagHandler() {
        this.messageSourceService = Context.getMessageSourceService();
    }

    @Override
    protected List<AttributeDescriptor> createAttributeDescriptors() {
        return Collections.singletonList(new AttributeDescriptor("concept", Concept.class));
    }

    @Override
    protected String getSubstitution(FormEntrySession session, FormSubmissionController controller, Map<String, String> attributes) throws BadFormDesignException {
        Concept concept = HtmlFormEntryUtil.getConcept(attributes.get("concept"));
        if (concept == null) {
            throw new IllegalArgumentException("Concept not found: " + attributes.get("concept"));
        }
        boolean includeCommentField = parseBooleanAttribute(attributes.get("specify"), false);
        String label = attributes.get("label");
        // TODO translate label
        if (label == null) {
            label = concept.getName().getName();
        }

        FormEntryContext context = session.getContext();

        CheckboxWidget checkboxWidget = new CheckboxWidget(label, PRESENT);
        context.registerWidget(checkboxWidget);

        TextFieldWidget textFieldWidget = null;
        ErrorWidget errorWidget = new ErrorWidget();
        if (includeCommentField) {
            textFieldWidget = new TextFieldWidget();
            context.registerWidget(textFieldWidget);
            context.registerErrorWidget(textFieldWidget, errorWidget);
        }

        StringBuilder html = new StringBuilder();
        html.append("<div class=\"past-medical-history-item\">");
        html.append(checkboxWidget.generateHtml(context));
        if (includeCommentField) {
            html.append(messageSourceService.getMessage("zl.pastMedicalHistory.specifyLabel"));
            html.append(textFieldWidget.generateHtml(context));
            html.append(errorWidget.generateHtml(context));
        }
        html.append("</div>");

        Obs existingObs = findExistingObs(context, HtmlFormEntryUtil.getConcept("CIEL:1633"), HtmlFormEntryUtil.getConcept("CIEL:1628"), concept);
        session.getSubmissionController().addAction(new Action(concept, checkboxWidget, textFieldWidget, existingObs));

        return html.toString();
    }

    private Obs findExistingObs(FormEntryContext context, Concept construct, Concept withMemberConcept, Concept withMemberValue) {
        for (Map.Entry<Obs, Set<Obs>> entry : context.getExistingObsInGroups().entrySet()) {
            Obs candidateGroup = entry.getKey();
            if (candidateGroup.getConcept().equals(construct)) {
                for (Obs member : entry.getValue()) {
                    if (member.getConcept().equals(withMemberConcept)
                            && member.getValueCoded().equals(withMemberValue)) {
                        context.getExistingObsInGroups().remove(candidateGroup);
                        // TODO do we also need to remove this from somewhere in context.getExistingObs()?
                        return candidateGroup;
                    }
                }
            }
        }
        return null;
    }

    public class Action implements FormSubmissionControllerAction {
        private Concept concept;
        private CheckboxWidget presenceWidget;
        private TextFieldWidget commentsWidget;
        private Obs existingObs;

        public Action(Concept concept, CheckboxWidget presenceWidget, TextFieldWidget commentsWidget, Obs existingObs) {
            this.concept = concept;
            this.presenceWidget = presenceWidget;
            this.commentsWidget = commentsWidget;
            this.existingObs = existingObs;
        }

        @Override
        public Collection<FormSubmissionError> validateSubmission(FormEntryContext context, HttpServletRequest request) {
            Object presence = presenceWidget.getValue(context, request);
            String comments = commentsWidget == null ? null : commentsWidget.getValue(context, request);
            if (StringUtils.isNotEmpty(comments) && !PRESENT.equals(presence)) {
                return Collections.singleton(new FormSubmissionError(commentsWidget,
                        messageSourceService.getMessage("zl.pastMedicalHistory.commentsWithoutPresenceError")));
            }
            return null;
        }

        @Override
        public void handleSubmission(FormEntrySession session, HttpServletRequest request) {
            FormSubmissionActions submissionActions = session.getSubmissionActions();
            FormEntryContext context = session.getContext();

            Concept construct = HtmlFormEntryUtil.getConcept("CIEL:1633");
            Concept which = HtmlFormEntryUtil.getConcept("CIEL:1628");
            Concept present = HtmlFormEntryUtil.getConcept("CIEL:1729");
            Concept comments = HtmlFormEntryUtil.getConcept("CIEL:160221");

            boolean presentValue = "present".equals(presenceWidget.getValue(context, request));
            String commentsValue = commentsWidget == null ? null : commentsWidget.getValue(context, request);

            if (existingObs == null) {
                // create a new obs group if this is checked
                if (presentValue) {
                    Obs group = new Obs();
                    group.setConcept(construct);

                    Obs whichObs = new Obs();
                    whichObs.setConcept(which);
                    whichObs.setValueCoded(concept);
                    group.addGroupMember(whichObs);

                    Obs presentObs = new Obs();
                    presentObs.setConcept(present);
                    presentObs.setValueCoded(MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES));
                    group.addGroupMember(presentObs);

                    if (StringUtils.isNotBlank(commentsValue)) {
                        Obs commentsObs = new Obs();
                        commentsObs.setConcept(comments);
                        commentsObs.setValueText(commentsValue);
                        group.addGroupMember(commentsObs);
                    }
                    try {
                        submissionActions.beginObsGroup(group);
                        submissionActions.endObsGroup();
                    } catch (InvalidActionException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
            else {
                // assert: this is an obs group for the correct value of the "which" concept

                if (!presentValue) {
                    // if unchecked, we just void the whole group
                    submissionActions.modifyObs(existingObs, null, null, null, null);
                }
                else {
                    // assume that existingPresence is correct, since this tag doesn't let us edit it (yet)
                    Obs existingComment = findMember(existingObs, comments);
                    if (existingComment == null) {
                        if (StringUtils.isNotBlank(commentsValue)) {
                            try {
                                submissionActions.beginObsGroup(existingObs);
                                submissionActions.createObs(comments, commentsValue, null, null);
                                submissionActions.endObsGroup();
                            } catch (InvalidActionException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                    }
                    else {
                        if (StringUtils.isBlank(commentsValue)) {
                            submissionActions.modifyObs(existingComment, null, null, null, null);
                        }
                        else if (!existingComment.getValueText().equals(commentsValue)) {
                            submissionActions.modifyObs(existingComment, comments, commentsValue, null, null);
                        }
                    }
                }
            }
        }

        private Obs findMember(Obs group, Concept concept) {
            for (Obs candidate : group.getGroupMembers()) {
                if (candidate.getConcept().equals(concept)) {
                    return candidate;
                }
            }
            return null;
        }
    }
}
