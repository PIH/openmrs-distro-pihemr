package org.openmrs.module.mirebalais;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.MirebalaisSpecificMetadata;

import static org.openmrs.module.mirebalais.MirebalaisConstants.DOSSIER_NUMBER_ZL_IDENTIFIER_SOURCE_UUID;

public class ConfigureIdGenerators {

	private final MirebalaisCustomProperties customProperties;
	
	private final IdentifierSourceService identifierSourceService;

    private final LocationService locationService;

    private MirebalaisGlobalProperties mirebalaisGlobalProperties;

	private final MirebalaisHospitalService service;
	
	public ConfigureIdGenerators(MirebalaisCustomProperties customProperties,
	    IdentifierSourceService identifierSourceService, LocationService locationService,
        MirebalaisHospitalService service) {

		this.customProperties = customProperties;
		this.identifierSourceService = identifierSourceService;
        this.locationService = locationService;
		this.service = service;
		
		if (customProperties == null || identifierSourceService == null || service == null) {
			throw new IllegalStateException("All the dependencies are mandatory");
		}
		
	}
	
	public void setAutoGenerationOptionsForDossierNumberGenerator(IdentifierSource identifierSource) {

        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(identifierSource
                .getIdentifierType());
        if (autoGenerationOption == null) {
            autoGenerationOption = new AutoGenerationOption();
        }

        autoGenerationOption.setIdentifierType(identifierSource.getIdentifierType());
        autoGenerationOption.setSource(identifierSource);
        autoGenerationOption.setLocation(locationService.getLocationByUuid(MirebalaisSpecificMetadata.MirebalaisHospitalLocations.MIREBALAIS_HOSPITAL));
        autoGenerationOption.setManualEntryEnabled(true);
        autoGenerationOption.setAutomaticGenerationEnabled(true);

        identifierSourceService.saveAutoGenerationOption(autoGenerationOption);

    }

    public void setAutoGenerationOptionsForZlIdentifier(IdentifierSource identifierSource) {

        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(identifierSource
                .getIdentifierType());
        if (autoGenerationOption == null) {
            autoGenerationOption = new AutoGenerationOption();
        }

        autoGenerationOption.setIdentifierType(identifierSource.getIdentifierType());
        autoGenerationOption.setSource(identifierSource);
        autoGenerationOption.setManualEntryEnabled(false);
        autoGenerationOption.setAutomaticGenerationEnabled(true);

        identifierSourceService.saveAutoGenerationOption(autoGenerationOption);

    }

	public IdentifierPool localZlIdentifierSource(RemoteIdentifierSource remoteZlIdentifierSource) {
		IdentifierPool localZlIdentifierPool;
		try {
			localZlIdentifierPool = service.getLocalZlIdentifierPool();
		}
		catch (IllegalStateException ex) {
			localZlIdentifierPool = buildLocalZlIdentifierPool(remoteZlIdentifierSource);
			identifierSourceService.saveIdentifierSource(localZlIdentifierPool);
		}
		return localZlIdentifierPool;
	}
	
	public RemoteIdentifierSource remoteZlIdentifierSource(PatientIdentifierType zlPatientIdentifierType) {
		RemoteIdentifierSource remoteZlIdentifierSource;
		
		try {
			remoteZlIdentifierSource = service.getRemoteZlIdentifierSource();
			updateInformationFromPropertiesFile(remoteZlIdentifierSource);
		}
		catch (IllegalStateException ex) {
			remoteZlIdentifierSource = buildRemoteZlIdentifierSource(zlPatientIdentifierType);
		}
		
		identifierSourceService.saveIdentifierSource(remoteZlIdentifierSource);
		return remoteZlIdentifierSource;
	}

	private IdentifierPool buildLocalZlIdentifierPool(RemoteIdentifierSource remoteZlIdentifierSource) {
		IdentifierPool localPool = new IdentifierPool();
		localPool.setName("Local Pool of ZL Identifiers");
		localPool.setUuid(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID);
		localPool.setSource(remoteZlIdentifierSource);
		localPool.setIdentifierType(remoteZlIdentifierSource.getIdentifierType());
		localPool.setMinPoolSize(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE);
		localPool.setBatchSize(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE);
		localPool.setSequential(false);
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
	
	public SequentialIdentifierGenerator sequentialIdentifierGeneratorToDossier(PatientIdentifierType patientIdentifierType) {
		
		SequentialIdentifierGenerator dossierSequenceGenerator;
		try {
			dossierSequenceGenerator = service.getDossierSequenceGenerator();
		}
		catch (IllegalStateException e) {
			dossierSequenceGenerator = buildSequenceGenerator(patientIdentifierType);
			identifierSourceService.saveIdentifierSource(dossierSequenceGenerator);
		}
		
		return dossierSequenceGenerator;
	}
	
	private SequentialIdentifierGenerator buildSequenceGenerator(PatientIdentifierType patientIdentifierType) {
		SequentialIdentifierGenerator sequentialIdentifierGenerator = new SequentialIdentifierGenerator();
		sequentialIdentifierGenerator.setName("Sequential Generator for Dossier");
		sequentialIdentifierGenerator.setUuid(DOSSIER_NUMBER_ZL_IDENTIFIER_SOURCE_UUID);
		sequentialIdentifierGenerator.setMaxLength(7);
        sequentialIdentifierGenerator.setMinLength(7);
		sequentialIdentifierGenerator.setPrefix("A");
		sequentialIdentifierGenerator.setBaseCharacterSet("0123456789");
		sequentialIdentifierGenerator.setFirstIdentifierBase("000001");
		sequentialIdentifierGenerator.setIdentifierType(patientIdentifierType);
		return sequentialIdentifierGenerator;
	}
}
