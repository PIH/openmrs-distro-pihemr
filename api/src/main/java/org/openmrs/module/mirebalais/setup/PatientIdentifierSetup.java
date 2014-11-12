package org.openmrs.module.mirebalais.setup;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.ConfigureIdGenerators;
import org.openmrs.module.mirebalais.MirebalaisCustomProperties;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants;
import org.openmrs.module.mirebalais.config.Config;

public class PatientIdentifierSetup {

    public static void setupIdentifierGeneratorsIfNecessary(MirebalaisHospitalService service,
                                                      IdentifierSourceService identifierSourceService,
                                                      LocationService locationService,
                                                      Config config,
                                                      MirebalaisCustomProperties customProperties) {

        ConfigureIdGenerators configureIdGenerators = new ConfigureIdGenerators(customProperties, identifierSourceService, locationService, service);

        createPatientIdGenerator(service, configureIdGenerators);

        if (config.isComponentEnabled(CustomAppLoaderConstants.Components.ARCHIVES)) {
            createDossierNumberGenerator(service, configureIdGenerators);
        }

    }

    private static void createPatientIdGenerator(MirebalaisHospitalService service, ConfigureIdGenerators configureIdGenerators) {
        PatientIdentifierType zlIdentifierType = service.getZlIdentifierType();
        RemoteIdentifierSource remoteZlIdentifierSource = configureIdGenerators.remoteZlIdentifierSource(zlIdentifierType);
        IdentifierPool localZlIdentifierPool = configureIdGenerators.localZlIdentifierSource(remoteZlIdentifierSource);
        configureIdGenerators.setAutoGenerationOptionsForZlIdentifier(localZlIdentifierPool);
    }

    private static void createDossierNumberGenerator(MirebalaisHospitalService service, ConfigureIdGenerators configureIdGenerators) {
        PatientIdentifierType dossierIdentifierType = service.getDossierIdentifierType();
        SequentialIdentifierGenerator sequentialIdentifierGenerator = configureIdGenerators.sequentialIdentifierGeneratorToDossier(dossierIdentifierType);
        configureIdGenerators.setAutoGenerationOptionsForDossierNumberGenerator(sequentialIdentifierGenerator);
    }
}
