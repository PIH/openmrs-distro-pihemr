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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.api.impl.MirebalaisHospitalServiceImpl;
import org.openmrs.module.mirebalaismetadata.CoreMetadata;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link MirebalaisHospitalService}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MirebalaisHospitalServiceTest {
	
	private MirebalaisHospitalService service;
	
	@Before
	public void setUp() throws Exception {
		service = new MirebalaisHospitalServiceImpl();
	}
	
	@Test
	public void shouldGetZlIdentifierType() {
		PatientIdentifierType zlIdentifierTypeMock = new PatientIdentifierType();
		zlIdentifierTypeMock.setUuid(CoreMetadata.PatientIdentifierTypes.ZL_EMR_ID);
		
		PatientService patientServiceMock = mock(PatientService.class);
		when(patientServiceMock.getPatientIdentifierTypeByUuid(CoreMetadata.PatientIdentifierTypes.ZL_EMR_ID)).thenReturn(
		    zlIdentifierTypeMock);
		
		PowerMockito.mockStatic(Context.class);
		when(Context.getPatientService()).thenReturn(patientServiceMock);
		
		PatientIdentifierType zlIdentifierType = service.getZlIdentifierType();
		assertNotNull(zlIdentifierType);
		assertEquals(CoreMetadata.PatientIdentifierTypes.ZL_EMR_ID, zlIdentifierType.getUuid());
	}
	
	@Test(expected = IllegalStateException.class)
	public void getZlIdentifierType_shouldFailIfTypeIsNotInDatabase() {
		PatientService patientServiceMock = mock(PatientService.class);
		when(patientServiceMock.getPatientIdentifierTypeByUuid(CoreMetadata.PatientIdentifierTypes.ZL_EMR_ID))
		        .thenReturn(null);
		
		PowerMockito.mockStatic(Context.class);
		when(Context.getPatientService()).thenReturn(patientServiceMock);
		
		PatientIdentifierType zlIdentifierType = service.getZlIdentifierType();
	}
	
	@Test
	public void shouldGetLocalZlIdentifierSource() {
		IdentifierPool zlIdentifierPoolMock = new IdentifierPool();
		zlIdentifierPoolMock.setUuid(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID);
		
		IdentifierSourceService identifierSourceServiceMock = mock(IdentifierSourceService.class);
		when(identifierSourceServiceMock.getIdentifierSourceByUuid(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID))
		        .thenReturn(zlIdentifierPoolMock);
		
		PowerMockito.mockStatic(Context.class);
		when(Context.getService(IdentifierSourceService.class)).thenReturn(identifierSourceServiceMock);
		
		IdentifierSource zlIdentifierPool = service.getLocalZlIdentifierPool();
		assertNotNull(zlIdentifierPool);
		assertEquals(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID, zlIdentifierPool.getUuid());
	}
	
	@Test(expected = IllegalStateException.class)
	public void getLocalZlIdentifierSource_shouldFailIfSourceIsNotInDatabase() {
		IdentifierSourceService identifierSourceServiceMock = mock(IdentifierSourceService.class);
		when(identifierSourceServiceMock.getIdentifierSourceByUuid(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID))
		        .thenReturn(null);
		
		PowerMockito.mockStatic(Context.class);
		when(Context.getService(IdentifierSourceService.class)).thenReturn(identifierSourceServiceMock);
		
		IdentifierSource zlIdentifierPool = service.getLocalZlIdentifierPool();
	}
	
}
