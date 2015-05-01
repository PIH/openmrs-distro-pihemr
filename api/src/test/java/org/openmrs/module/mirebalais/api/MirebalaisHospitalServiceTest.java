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

import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatadeploy.MissingMetadataException;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.RuntimeProperties;
import org.openmrs.module.mirebalais.setup.PatientIdentifierSetup;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.deploy.bundle.PatientIdentifierTypeBundle;
import org.openmrs.module.pihcore.descriptor.PatientIdentifierTypeDescriptor;
import org.openmrs.module.pihcore.metadata.PatientIdentifierTypes;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests {@link MirebalaisHospitalService}.
 */
public class MirebalaisHospitalServiceTest extends BaseModuleContextSensitiveTest {

	@Autowired
	MirebalaisHospitalService service;

	@Autowired
    PatientIdentifierTypeBundle patientIdentifierTypeBundle;

	@Autowired
	IdentifierSourceService identifierSourceService;

	@Autowired
	LocationService locationService;

	@Test
	public void shouldGetZlIdentifierType() throws Exception {
		patientIdentifierTypeBundle.install();
		PatientIdentifierType found = service.getZlIdentifierType();
		PatientIdentifierTypeDescriptor expected = PatientIdentifierTypes.ZL_EMR_ID;
		assertEquals(expected.uuid(), found.getUuid());
		assertEquals(expected.name(), found.getName());
		assertEquals(expected.description(), found.getDescription());
		assertEquals(expected.format(), found.getFormat());
		assertEquals(expected.formatDescription(), found.getFormatDescription());
		assertEquals(expected.locationBehavior(), found.getLocationBehavior());
		assertEquals(expected.validator().getName(), found.getValidator());
		assertEquals(expected.required(), found.getRequired());
	}
	
	@Test(expected = MissingMetadataException.class)
	public void getZlIdentifierType_shouldFailIfTypeIsNotInDatabase() {
		service.getZlIdentifierType();
	}

	@Test
	public void shouldGetLocalZlIdentifierSource() throws Exception {
		patientIdentifierTypeBundle.install();
		Config config = new Config();
		RuntimeProperties props = new RuntimeProperties();
		PatientIdentifierSetup.setupIdentifierGeneratorsIfNecessary(service, identifierSourceService, locationService, config, props);
		IdentifierSource zlIdentifierPool = service.getLocalZlIdentifierPool();
		assertNotNull(zlIdentifierPool);
		assertEquals(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID, zlIdentifierPool.getUuid());
	}
	
	@Test(expected = IllegalStateException.class)
	public void getLocalZlIdentifierSource_shouldFailIfSourceIsNotInDatabase() {
		service.getLocalZlIdentifierPool();
	}
}
