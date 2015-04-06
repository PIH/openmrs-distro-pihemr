package org.openmrs.module.mirebalais;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.CoreConceptMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

/**
 * These are placeholder versions of concepts that are actually imported via MDS packages, that we need to have
 * available in unit tests to be able to test forms (and we don't load the big concepts MDS package during unit tests)
 */
@Component
@Requires(CoreConceptMetadataBundle.class)
public class ConceptsFromMetadataSharing extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installNewVersion() throws Exception {
        install(diagnosis("ASTHMA"));
        install(diagnosis("EPILEPSY"));
        install(diagnosis("HYPERTENSION"));
        install(diagnosis("SEXUALLY TRANSMITTED INFECTION"));
        install(diagnosis("WEIGHT LOSS"));
        install(diagnosis("MEASLES"));
        install(diagnosis("TUBERCULOSIS"));
        install(diagnosis("VARICELLA"));
        install(diagnosis("Diphtheria"));
        install(diagnosis("ACUTE RHEUMATIC FEVER"));
        install(diagnosis("DIABETES"));
        install(diagnosisWithCielMapping("Cancer", "116031"));
    }

    private Concept diagnosis(String name) {
        Concept concept = new Concept();
        concept.setDatatype(notApplicable);
        concept.setConceptClass(diagnosis);
        concept.addName(new ConceptName(name, Locale.ENGLISH));
        concept.addConceptMapping(new ConceptMapBuilder(uuid()).type(sameAs).ensureTerm(pih, name).build());
        return concept;
    }

    private Concept diagnosisWithCielMapping(String name, String cielMapping) {
        Concept concept = diagnosis(name);
        concept.addConceptMapping(new ConceptMapBuilder(uuid()).type(sameAs).ensureTerm(ciel, cielMapping).build());
        return concept;
    }

    private String uuid() {
        return UUID.randomUUID().toString();
    }
}
