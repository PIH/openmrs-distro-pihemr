/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mirebalais.api;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(MirebalaisHospitalService.class).someMethod();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface MirebalaisHospitalService extends OpenmrsService {
	
	void configureZlIdentifierSources();
	
	IdentifierPool getLocalZlIdentifierPool();

    SequentialIdentifierGenerator getLocalZlIdentifierGenerator();

	RemoteIdentifierSource getRemoteZlIdentifierSource();
	
	PatientIdentifierType getZlIdentifierType();

    PatientIdentifierType getExternalDossierIdentifierType();
	
	SequentialIdentifierGenerator getDossierSequenceGenerator(String identifierSourceUuid);

	PatientIdentifierType getDossierIdentifierType();

    /**
     * Gets the next available radiology order number seed
     *
     * @return the order number seed
     */
    public Long getNextRadiologyOrderNumberSeedSequenceValue();
}
