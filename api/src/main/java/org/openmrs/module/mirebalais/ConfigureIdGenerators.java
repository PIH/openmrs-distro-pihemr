package org.openmrs.module.mirebalais;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;

public class ConfigureIdGenerators {

	private final RuntimeProperties customProperties;
	
	private final IdentifierSourceService identifierSourceService;

    private final LocationService locationService;

	private final MirebalaisHospitalService service;
	
	public ConfigureIdGenerators(RuntimeProperties customProperties,
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
	
	public void setAutoGenerationOptionsForDossierNumberGenerator(IdentifierSource identifierSource, Location location) {

        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(identifierSource
                .getIdentifierType(), location);

        if (autoGenerationOption == null) {
            autoGenerationOption = new AutoGenerationOption();
        }

        autoGenerationOption.setIdentifierType(identifierSource.getIdentifierType());
        autoGenerationOption.setSource(identifierSource);
        autoGenerationOption.setLocation(location);
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
            localZlIdentifierPool = new IdentifierPool();
            localZlIdentifierPool.setName("Local Pool of ZL Identifiers");
            localZlIdentifierPool.setUuid(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID);
            localZlIdentifierPool.setSource(remoteZlIdentifierSource);
            localZlIdentifierPool.setIdentifierType(remoteZlIdentifierSource.getIdentifierType());
            localZlIdentifierPool.setMinPoolSize(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE);
            localZlIdentifierPool.setBatchSize(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE);
            localZlIdentifierPool.setSequential(false);
			identifierSourceService.saveIdentifierSource(localZlIdentifierPool);
		}
		return localZlIdentifierPool;
	}

    public SequentialIdentifierGenerator localZlIdentifierGenerator(PatientIdentifierType zlPatientIdentifierType) {
        SequentialIdentifierGenerator localZlIdentifierGenerator;
        try {
            localZlIdentifierGenerator = service.getLocalZlIdentifierGenerator();
        }
        catch (IllegalStateException ex) {
            localZlIdentifierGenerator = new SequentialIdentifierGenerator();
            localZlIdentifierGenerator.setName("Local ZL Identifier Generator");
            localZlIdentifierGenerator.setUuid(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_GENERATOR_UUID);
            localZlIdentifierGenerator.setIdentifierType(zlPatientIdentifierType);
            localZlIdentifierGenerator.setBaseCharacterSet("ACDEFGHJKLMNPRTUVWXY1234567890");
            localZlIdentifierGenerator.setMaxLength(6);
            localZlIdentifierGenerator.setMinLength(6);
        }
        String prefix = customProperties.getLocalZlIdentifierGeneratorPrefix();
        int firstIdentifierBase = (int)Math.pow(10, (4-prefix.length()));
        localZlIdentifierGenerator.setPrefix(prefix);
        localZlIdentifierGenerator.setFirstIdentifierBase(Integer.toString(firstIdentifierBase));

        identifierSourceService.saveIdentifierSource(localZlIdentifierGenerator);
        return localZlIdentifierGenerator;
    }
	
	public RemoteIdentifierSource remoteZlIdentifierSource(PatientIdentifierType zlPatientIdentifierType) {
		RemoteIdentifierSource remoteZlIdentifierSource;

        SequentialIdentifierGenerator localGenerator = null;
        if (customProperties.getLocalZlIdentifierGeneratorEnabled()) {
            localGenerator = localZlIdentifierGenerator(zlPatientIdentifierType);
        }

		try {
			remoteZlIdentifierSource = service.getRemoteZlIdentifierSource();
		}
		catch (IllegalStateException ex) {
            remoteZlIdentifierSource = new RemoteIdentifierSource();
            remoteZlIdentifierSource.setName("Remote Source for ZL Identifiers");
            remoteZlIdentifierSource.setUuid(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID);
            remoteZlIdentifierSource.setIdentifierType(zlPatientIdentifierType);
		}

        String url = customProperties.getRemoteZlIdentifierSourceUrl();
        if (url != null && localGenerator != null) {
            url = url.replace("{LOCAL_SOURCE_ID}", localGenerator.getId().toString());
        }
        remoteZlIdentifierSource.setUrl(url);
        remoteZlIdentifierSource.setUser(customProperties.getRemoteZlIdentifierSourceUsername());
        remoteZlIdentifierSource.setPassword(customProperties.getRemoteZlIdentifierSourcePassword());
		
		identifierSourceService.saveIdentifierSource(remoteZlIdentifierSource);
		return remoteZlIdentifierSource;
	}
	
	public SequentialIdentifierGenerator sequentialIdentifierGeneratorForDossier(PatientIdentifierType patientIdentifierType, String prefix, String identifierSourceUuid) {
		SequentialIdentifierGenerator dossierSequenceGenerator;
		try {
			dossierSequenceGenerator = service.getDossierSequenceGenerator(identifierSourceUuid);
		}
		catch (IllegalStateException e) {
            dossierSequenceGenerator = new SequentialIdentifierGenerator();
            dossierSequenceGenerator.setName("Sequential Generator for Dossier");
            dossierSequenceGenerator.setUuid(identifierSourceUuid);
            dossierSequenceGenerator.setMaxLength(6 + prefix.length());
            dossierSequenceGenerator.setMinLength(6 + prefix.length());
            dossierSequenceGenerator.setPrefix(prefix);
            dossierSequenceGenerator.setBaseCharacterSet("0123456789");
            dossierSequenceGenerator.setFirstIdentifierBase("000001");
            dossierSequenceGenerator.setIdentifierType(patientIdentifierType);
			identifierSourceService.saveIdentifierSource(dossierSequenceGenerator);
		}
		
		return dossierSequenceGenerator;
	}
}
