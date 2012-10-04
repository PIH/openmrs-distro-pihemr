package org.openmrs.module.mirebalais;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;

import static org.openmrs.module.mirebalais.MirebalaisConstants.DOSSIER_NUMBER_ZL_IDENTIFIER_SOURCE_UUID;

public class ConfigurePropertiesToGenerateIds {
    private final MirebalaisCustomProperties customProperties;
    private final IdentifierSourceService identifierSourceService;
    private final MirebalaisHospitalService service;
    private final PatientIdentifierType zlIdentifierType;

    public ConfigurePropertiesToGenerateIds(MirebalaisCustomProperties customProperties, IdentifierSourceService identifierSourceService,
                                            MirebalaisHospitalService service) {
        this.customProperties = customProperties;
        this.identifierSourceService = identifierSourceService;
        this.service = service;

        if (customProperties == null || identifierSourceService == null || service == null){
            throw new IllegalStateException("All the dependencies are mandatory");
        }

        zlIdentifierType = service.getZlIdentifierType();
    }

    public void autoGenerationOptions(IdentifierPool localZlIdentifierPool) {
        AutoGenerationOption autoGen = identifierSourceService.getAutoGenerationOption(zlIdentifierType);
        if (autoGen == null) {
            autoGen = buildZlIdentifierAutoGenerationOptions(zlIdentifierType, localZlIdentifierPool);
            identifierSourceService.saveAutoGenerationOption(autoGen);
        }
    }

    public IdentifierPool localZlIdentifierSource(RemoteIdentifierSource remoteZlIdentifierSource) {
        IdentifierPool localZlIdentifierPool;
        try {
            localZlIdentifierPool = service.getLocalZlIdentifierPool();
        } catch (IllegalStateException ex) {
            localZlIdentifierPool = buildLocalZlIdentifierPool(zlIdentifierType, remoteZlIdentifierSource);
            identifierSourceService.saveIdentifierSource(localZlIdentifierPool);
        }
        return localZlIdentifierPool;
    }

    public RemoteIdentifierSource remoteZlIdentifierSource() {
        RemoteIdentifierSource remoteZlIdentifierSource;

        try {
            remoteZlIdentifierSource = service.getRemoteZlIdentifierSource();
            updateInformationFromPropertiesFile(remoteZlIdentifierSource);
        } catch (IllegalStateException ex) {
            remoteZlIdentifierSource = buildRemoteZlIdentifierSource(zlIdentifierType);
        }

        identifierSourceService.saveIdentifierSource(remoteZlIdentifierSource);
        return remoteZlIdentifierSource;
    }

    private AutoGenerationOption buildZlIdentifierAutoGenerationOptions(PatientIdentifierType zlIdentifierType,
                                                                       IdentifierPool localZlIdentifierPool) {
        AutoGenerationOption autoGen = new AutoGenerationOption();
        autoGen.setIdentifierType(zlIdentifierType);
        autoGen.setSource(localZlIdentifierPool);
        autoGen.setManualEntryEnabled(false);
        autoGen.setAutomaticGenerationEnabled(true);
        return autoGen;
    }

    private IdentifierPool buildLocalZlIdentifierPool(PatientIdentifierType zlIdentifierType,
                                                     RemoteIdentifierSource remoteZlIdentifierSource) {
        IdentifierPool localPool = new IdentifierPool();
        localPool.setName("Local Pool of ZL Identifiers");
        localPool.setUuid(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID);
        localPool.setSource(remoteZlIdentifierSource);
        localPool.setIdentifierType(zlIdentifierType);
        localPool.setMinPoolSize(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE);
        localPool.setBatchSize(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE);
        localPool.setSequential(true);
        return localPool;
    }

    private RemoteIdentifierSource buildRemoteZlIdentifierSource(PatientIdentifierType zlIdentifierType) {
        RemoteIdentifierSource remoteZlIdentifierSource = new RemoteIdentifierSource();
        remoteZlIdentifierSource.setName("Remote Source for ZL Identifiers");
        remoteZlIdentifierSource.setUuid(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID);
        remoteZlIdentifierSource.setIdentifierType(zlIdentifierType);
        updateInformationFromPropertiesFile(remoteZlIdentifierSource);
        return remoteZlIdentifierSource;
    }

    private void updateInformationFromPropertiesFile(RemoteIdentifierSource remoteZlIdentifierSource) {
        remoteZlIdentifierSource.setUrl(customProperties.getRemoteZlIdentifierSourceUrl());
        remoteZlIdentifierSource.setUser(customProperties.getRemoteZlIdentifierSourceUsername());
        remoteZlIdentifierSource.setPassword(customProperties.getRemoteZlIdentifierSourcePassword());
    }

    public void sequentialIdentifierGeneratorToDossier() {

        SequentialIdentifierGenerator dossierSequenceGenerator;
        try {
            dossierSequenceGenerator = service.getDossierSequenceGenerator();
        } catch (IllegalStateException e){
            dossierSequenceGenerator = buildSequenceGenerator();
            identifierSourceService.saveIdentifierSource(dossierSequenceGenerator);
        }

    }

    private SequentialIdentifierGenerator buildSequenceGenerator() {
        SequentialIdentifierGenerator sequentialIdentifierGenerator = new SequentialIdentifierGenerator();
        sequentialIdentifierGenerator.setName("Sequential Generator for Dossier");
        sequentialIdentifierGenerator.setUuid(DOSSIER_NUMBER_ZL_IDENTIFIER_SOURCE_UUID);
        sequentialIdentifierGenerator.setLength(7);
        sequentialIdentifierGenerator.setPrefix("A");
        sequentialIdentifierGenerator.setBaseCharacterSet("0123456789");
        sequentialIdentifierGenerator.setFirstIdentifierBase("000001");
        sequentialIdentifierGenerator.setIdentifierType(zlIdentifierType);
        return sequentialIdentifierGenerator;
    }
}