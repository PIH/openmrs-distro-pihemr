package org.openmrs.module.mirebalais.apploader.apps.patientregistration;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.deploy.bundle.core.EncounterRoleBundle;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.registrationapp.model.RegistrationAppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper class to help defining PatientRegistrationApp
 */
@Component
public class PatientRegistrationApp {

    @Autowired
    private FeatureToggleProperties featureToggles;

    public AppDescriptor getAppDescriptor(Config config) {
        AppDescriptor d = new AppDescriptor();
        d.setId(CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION);
        d.setDescription("registrationapp.registerPatient");
        d.setLabel("registrationapp.app.registerPatient.label");
        d.setIcon("icon-user");
        d.setUrl("registrationapp/findPatient.page?appId=" + CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION);
        d.setRequiredPrivilege("App: registrationapp.registerPatient");
        d.setConfig(toObjectNode(getRegistrationAppConfig(config)));
        return d;
    }

    private RegistrationAppConfig getRegistrationAppConfig(Config config) {
        RegistrationAppConfig c = new RegistrationAppConfig();
        c.setPatientDashboardLink("registrationapp/registrationSummary.page?appId=" +  CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION);
        c.setRegistrationEncounter(EncounterTypes.PATIENT_REGISTRATION.uuid(), EncounterRoleBundle.EncounterRoles.ADMINISTRATIVE_CLERK);
        c.setAllowRetrospectiveEntry(true);
        c.setAllowUnknownPatients(config.getRegistrationConfig().isAllowUnknownPatients());
        c.setAllowManualIdentifier(config.getRegistrationConfig().isAllowManualEntryOfPrimaryIdentifier());
        c.setAfterCreatedUrl(config.getRegistrationConfig().getAfterCreatedUrl());
        c.setAfterCreatedActions(config.getRegistrationConfig().getAfterCreatedActions());
        c.setMatchingPatientsPropertiesToDisplay(config.getRegistrationConfig().getMatchingPatientsPropertiesToDisplay());
        c.setMaxPatientSearchResults(config.getRegistrationConfig().getMaxPatientMatchResults());
        c.setIdentifierTypesToDisplay(config.getRegistrationConfig().getIdentifierTypesToDisplay());
        switch(config.getCountry()) {
            case HAITI:
                new SectionsHaiti(config, featureToggles).addSections(c);
                break;
            case MEXICO:
                new SectionsMexico(config).addSections(c);
                break;
            case LIBERIA:
                new SectionsLiberia(config).addSections(c);
                break;
            case SIERRA_LEONE:
                new SectionsSierraLeone(config).addSections(c);
                break;
            default:
                new SectionsDefault(config).addSections(c);
        }
        return c;
    }

    private ObjectNode toObjectNode(Object o) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        return mapper.convertValue(o, ObjectNode.class);
    }

}
