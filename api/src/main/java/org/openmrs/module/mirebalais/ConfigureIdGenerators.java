package org.openmrs.module.mirebalais;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;

import static org.openmrs.module.mirebalais.MirebalaisConstants.DOSSIER_NUMBER_ZL_IDENTIFIER_SOURCE_UUID;

public class ConfigureIdGenerators {

    // hacky test

	private final MirebalaisCustomProperties customProperties;
	
	private final IdentifierSourceService identifierSourceService;
	
	private final MirebalaisHospitalService service;
	
	public ConfigureIdGenerators(MirebalaisCustomProperties customProperties,
	    IdentifierSourceService identifierSourceService, MirebalaisHospitalService service) {
		this.customProperties = customProperties;
		this.identifierSourceService = identifierSourceService;
		this.service = service;
		
		if (customProperties == null || identifierSourceService == null || service == null) {
			throw new IllegalStateException("All the dependencies are mandatory");
		}
		
	}
	
	public void autoGenerationOptions(IdentifierSource identifierSource) {
		AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(identifierSource
		        .getIdentifierType());
		if (autoGenerationOption == null) {
			AutoGenerationOption autoGen = buildZlIdentifierAutoGenerationOptions(identifierSource);
			identifierSourceService.saveAutoGenerationOption(autoGen);
		}
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
	
	private AutoGenerationOption buildZlIdentifierAutoGenerationOptions(IdentifierSource identifierSource) {
		AutoGenerationOption autoGen = new AutoGenerationOption();
		autoGen.setIdentifierType(identifierSource.getIdentifierType());
		autoGen.setSource(identifierSource);
		autoGen.setManualEntryEnabled(false);
		autoGen.setAutomaticGenerationEnabled(true);
		return autoGen;
	}
	
	private IdentifierPool buildLocalZlIdentifierPool(RemoteIdentifierSource remoteZlIdentifierSource) {
		IdentifierPool localPool = new IdentifierPool();
		localPool.setName("Local Pool of ZL Identifiers");
		localPool.setUuid(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID);
		localPool.setSource(remoteZlIdentifierSource);
		localPool.setIdentifierType(remoteZlIdentifierSource.getIdentifierType());
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
		sequentialIdentifierGenerator.setLength(7);
		sequentialIdentifierGenerator.setPrefix("A");
		sequentialIdentifierGenerator.setBaseCharacterSet("0123456789");
		sequentialIdentifierGenerator.setFirstIdentifierBase("000001");
		sequentialIdentifierGenerator.setIdentifierType(patientIdentifierType);
		return sequentialIdentifierGenerator;
	}
}
