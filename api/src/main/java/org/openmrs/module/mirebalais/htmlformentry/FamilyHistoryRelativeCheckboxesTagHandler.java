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
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CommonConcepts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

/**
 * Usage:
 * <familyHistoryRelativeCheckboxes concept="uuid-of-asthma" label="Asthma" relatives="uuid-of-father,uuid-of-mother"/>
 * => Asthma: [ ]Father  [ ]Mother
 * <familyHistoryRelativeCheckboxes concept="uuid-of-cancer" label="Cancer" relatives="uuid-of-father,uuid-of-mother" specify="true"/>
 * => Cancer, specify:________   [ ]Father  [ ]Mother
 *
 * For each relative box that is checked, an obs group will be stored with concept CIEL:160593 and members:
 * * which disease? CIEL:160592 = @concept
 * * which relative? CIEL:1560 = @relatives[i]
 * * present? CIEL:1729 = YES
 * * comments? CIEL:160618 = comment text
 */
public class FamilyHistoryRelativeCheckboxesTagHandler extends SubstitutionTagHandler {

    private MessageSourceService messageSourceService;

    public FamilyHistoryRelativeCheckboxesTagHandler() {
        this.messageSourceService = Context.getMessageSourceService();
    }

    @Override
    protected List<AttributeDescriptor> createAttributeDescriptors() {
        return Collections.unmodifiableList(Arrays.asList(
                new AttributeDescriptor("concept", Concept.class),
                new AttributeDescriptor("relatives", Concept.class)
        ));
    }

    @Override
    protected String getSubstitution(FormEntrySession session, FormSubmissionController controller, Map<String, String> attributes) throws BadFormDesignException {
        Concept concept = HtmlFormEntryUtil.getConcept(attributes.get("concept"));
        if (concept == null) {
            throw new IllegalArgumentException("Concept not found: " + attributes.get("concept"));
        }
        List<Concept> relatives = new ArrayList<Concept>();
        for (String id : attributes.get("relatives").split(",")) {
            Concept c = HtmlFormEntryUtil.getConcept(id);
            if (c == null) {
                throw new IllegalArgumentException("Relative type not found: " + id);
            }
            relatives.add(c);
        }
        if (relatives.isEmpty()) {
            throw new IllegalArgumentException(MirebalaisConstants.HTMLFORMENTRY_FAMILY_HISTORY_RELATIVE_CHECKBOXES_TAG_NAME + " tag must have at least one relative specified");
        }

        boolean includeCommentField = parseBooleanAttribute(attributes.get("specify"), false);
        String label = attributes.get("label");
        // TODO translate label
        if (label == null) {
            label = concept.getName().getName();
        }

        FormEntryContext context = session.getContext();
        // maps from widget to existing obs for this widget
        Map<CheckboxWidget, Obs> relativeWidgets = new LinkedHashMap<CheckboxWidget, Obs>();
        for (Concept relative : relatives) {
            // group: family hx construct, member = which relative, member value = $relative
            // note: this assumes that all instances of this obs group have "is present = true"
            Obs existingObs = findExistingObs(context, HtmlFormEntryUtil.getConcept("CIEL:160593"), HtmlFormEntryUtil.getConcept("CIEL:1560"), relative);

            CheckboxWidget widget = new CheckboxWidget(relative.getName().getName(), relative.getUuid());
            widget.setInitialValue(existingObs);
            relativeWidgets.put(widget, existingObs);
            context.registerWidget(widget);
        }

        TextFieldWidget textFieldWidget = null;
        ErrorWidget errorWidget = new ErrorWidget();
        if (includeCommentField) {
            textFieldWidget = new TextFieldWidget();
            context.registerWidget(textFieldWidget);
            context.registerErrorWidget(textFieldWidget, errorWidget);
        }

        StringBuilder html = new StringBuilder();
        html.append("<div class=\"family-history-item\">");
        html.append("<div class=\"label\">");
        html.append(label);
        if (includeCommentField) {
            html.append(messageSourceService.getMessage("zl.familyHistoryRelativeCheckboxes.specifyLabel"));
            html.append(textFieldWidget.generateHtml(context));
            html.append(errorWidget.generateHtml(context));
        }
        html.append("</div>");

        for (Map.Entry<CheckboxWidget, Obs> entry : relativeWidgets.entrySet()) {
            html.append("<div class=\"relative\">");
            CheckboxWidget widget = entry.getKey();
            html.append(widget.generateHtml(context));
            html.append("</div>");
        }
        html.append("</div>");

        session.getSubmissionController().addAction(new Action(concept, relativeWidgets, textFieldWidget));
        return html.toString();
    }


    // TODO make this a reusable utility method (shared with PMHCheckboxTagHandler)
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

    private class Action implements FormSubmissionControllerAction {
        private Concept concept;
        private Map<CheckboxWidget, Obs> relativeWidgets;
        private TextFieldWidget commentsWidget;

        public Action(Concept concept, Map<CheckboxWidget, Obs> relativeWidgets, TextFieldWidget commentsWidget) {
            this.concept = concept;
            this.relativeWidgets = relativeWidgets;
            this.commentsWidget = commentsWidget;
        }

        @Override
        public Collection<FormSubmissionError> validateSubmission(FormEntryContext formEntryContext, HttpServletRequest httpServletRequest) {
            // The UI doesn't allow any invalid states
            return null;
        }

        @Override
        public void handleSubmission(FormEntrySession session, HttpServletRequest request) {
            FormSubmissionActions submissionActions = session.getSubmissionActions();
            FormEntryContext context = session.getContext();

            Concept construct = HtmlFormEntryUtil.getConcept("CIEL:160593");
            Concept which = HtmlFormEntryUtil.getConcept("CIEL:160592");
            Concept whichRelative = HtmlFormEntryUtil.getConcept("CIEL:1560");
            Concept present = HtmlFormEntryUtil.getConcept("CIEL:1729");
            Concept comments = HtmlFormEntryUtil.getConcept("CIEL:160618");

            String commentsValue = commentsWidget == null ? null : commentsWidget.getValue(context, request);

            for (Map.Entry<CheckboxWidget, Obs> entry : relativeWidgets.entrySet()) {
                CheckboxWidget widget = entry.getKey();
                Obs existingObs = entry.getValue();

                String relativeUuidIfChecked = (String) widget.getValue(context, request);
                boolean isPresent = StringUtils.isNotEmpty(relativeUuidIfChecked);

                if (existingObs != null) {
                    if (!isPresent) {
                        // if unchecked, we just void the whole group
                        submissionActions.modifyObs(existingObs, null, null, null, null);
                    }
                    else {
                        // assume the only thing that can change is comments (since this widget doesn't let you change anything else)
                        Obs existingComment = findMember(existingObs, comments);
                        if (existingComment != null) {
                            submissionActions.modifyObs(existingComment, comments, commentsValue, null, null);
                        }
                        else if (commentsValue != null) {
                            // I didn't find a better way to add an obs to an existing group
                            try {
                                submissionActions.beginObsGroup(existingObs);
                                submissionActions.createObs(comments, commentsValue, null, null);
                                submissionActions.endObsGroup();
                            } catch (InvalidActionException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                    }
                }
                else {
                    // create a new obs group is this is checked
                    if (isPresent) {
                        Obs group = new Obs();
                        group.setConcept(construct);

                        Obs whichObs = new Obs();
                        whichObs.setConcept(which);
                        whichObs.setValueCoded(concept);
                        group.addGroupMember(whichObs);

                        Obs whichRelativeObs = new Obs();
                        whichRelativeObs.setConcept(whichRelative);
                        whichRelativeObs.setValueCoded(HtmlFormEntryUtil.getConcept(relativeUuidIfChecked));
                        group.addGroupMember(whichRelativeObs);

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
            }
        }
    }

    // put this somewhere reusable
    private Obs findMember(Obs group, Concept memberConcept) {
        for (Obs candidate : group.getGroupMembers(false)) {
            if (candidate.getConcept().equals(memberConcept)) {
                return candidate;
            }
        }
        return null;
    }
}