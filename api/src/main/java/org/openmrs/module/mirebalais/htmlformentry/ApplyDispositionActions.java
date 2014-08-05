package org.openmrs.module.mirebalais.htmlformentry;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.disposition.Disposition;
import org.openmrs.module.emrapi.disposition.DispositionDescriptor;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.emrapi.disposition.actions.DispositionAction;
import org.openmrs.module.emrapi.encounter.EncounterDomainWrapper;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;

/**
 * If the saved encounter has a disposition obs group and obs, then any actions associated with that disposition will
 * be applied.
 */
public class ApplyDispositionActions implements CustomFormSubmissionAction {

    @Override
    public void applyAction(FormEntrySession session) {
        DispositionService dispositionService = Context.getService(DispositionService.class);
        applyDispositionActions(session, dispositionService);
    }

    void applyDispositionActions(FormEntrySession session, DispositionService dispositionService) {
        DispositionDescriptor dispositionDescriptor = dispositionService.getDispositionDescriptor();
        Encounter encounter = session.getEncounter();

        Disposition disposition = null;
        Obs dispositionObsGroup = null;

        for (Obs candidate : encounter.getObsAtTopLevel(false)) {
            if (dispositionDescriptor.isDisposition(candidate)) {
                dispositionObsGroup = candidate;
                disposition = dispositionService.getDispositionFromObsGroup(dispositionObsGroup);
            }
        }

        if (disposition != null) {
            for (String actionBeanName : disposition.getActions()) {
                DispositionAction action = getBean(actionBeanName, DispositionAction.class);
                action.action(new EncounterDomainWrapper(encounter), dispositionObsGroup, null);
            }
        }
    }

    <T> T getBean(String beanId, Class<T> clazz) {
        return Context.getRegisteredComponent(beanId, clazz);
    }

}
