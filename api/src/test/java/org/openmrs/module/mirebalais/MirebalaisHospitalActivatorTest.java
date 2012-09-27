/*
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
package org.openmrs.module.mirebalais;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MirebalaisHospitalActivatorTest {
	
	private MirebalaisHospitalActivator mirebalaisHospitalActivator;
	
	@Before
	public void setUp() throws Exception {
		mirebalaisHospitalActivator = new MirebalaisHospitalActivator();
	}
	
	@Test
	public void shouldReturnRemoteZlIdentifierSourceWhenItExistsOnDb() {
		RemoteIdentifierSource remoteIdentifierSource = new RemoteIdentifierSource();
		
		MirebalaisHospitalService service = Mockito.mock(MirebalaisHospitalService.class);
		when(service.getRemoteZlIdentifierSource()).thenReturn(remoteIdentifierSource);
		
		IdentifierSourceService identifierSourceService = mock(IdentifierSourceService.class);
		
		RemoteIdentifierSource remoteZlIdentifierSource = mirebalaisHospitalActivator.getOrCreateRemoteZlIdentifierSource(
		    service, new PatientIdentifierType(), identifierSourceService);
		verify(identifierSourceService, never()).saveIdentifierSource(any(IdentifierSource.class));
		
		assertSame(remoteIdentifierSource, remoteZlIdentifierSource);
	}
	
	@Test
	public void shouldCreateRemoteZlIdentifierSourceWhenItDoesNotExistOnDb() {
		IdentifierSourceService identifierSourceService = mock(IdentifierSourceService.class);
		
		MirebalaisHospitalService service = mock(MirebalaisHospitalService.class);
		when(service.getRemoteZlIdentifierSource()).thenThrow(new IllegalStateException());
		
		PatientIdentifierType zlIdentifierType = new PatientIdentifierType();
		
		RemoteIdentifierSource remoteZlIdentifierSource = mirebalaisHospitalActivator.getOrCreateRemoteZlIdentifierSource(
		    service, zlIdentifierType, identifierSourceService);
		RemoteIdentifierSource remoteZlIdentifierSourceExpected = buildRemoteIdentifierExpected(zlIdentifierType);
		
		verify(identifierSourceService).saveIdentifierSource(eq(remoteZlIdentifierSourceExpected));
		assertEquals(remoteZlIdentifierSourceExpected, remoteZlIdentifierSource);
	}
	
	@Test
	public void shouldReturnLocalZlIdentifierPoolWhenItExistsOnDb() {
		IdentifierPool identifierPool = new IdentifierPool();
		identifierPool.setName("test from db");
		
		MirebalaisHospitalService service = mock(MirebalaisHospitalService.class);
		when(service.getLocalZlIdentifierPool()).thenReturn(identifierPool);
		
		IdentifierSourceService identifierSourceService = mock(IdentifierSourceService.class);
		
		IdentifierPool remoteZlIdentifierPool = mirebalaisHospitalActivator.getOrCreateLocalZlIdentifierPool(service,
		    new PatientIdentifierType(), new RemoteIdentifierSource(), identifierSourceService);
		verify(identifierSourceService, never()).saveIdentifierSource(any(IdentifierSource.class));
		
		assertSame(identifierPool, remoteZlIdentifierPool);
	}
	
	@Test
	public void shouldCreateLocalZlIdentifierPoolWhenItDoesNotExistOnDb() {
		MirebalaisHospitalService service = mock(MirebalaisHospitalService.class);
		when(service.getLocalZlIdentifierPool()).thenThrow(new IllegalStateException());
		
		IdentifierSourceService identifierSourceService = mock(IdentifierSourceService.class);
		
		PatientIdentifierType zlIdentifierType = new PatientIdentifierType();
		RemoteIdentifierSource remoteZlIdentifierSource = new RemoteIdentifierSource();
		
		IdentifierPool remoteZlIdentifierPool = mirebalaisHospitalActivator.getOrCreateLocalZlIdentifierPool(service,
		    zlIdentifierType, remoteZlIdentifierSource, identifierSourceService);
		
		IdentifierPool localPool = buildLocalPoolAsExpected(zlIdentifierType, remoteZlIdentifierSource);
		verify(identifierSourceService).saveIdentifierSource(eq(localPool));
		
		assertEquals(localPool, remoteZlIdentifierPool);
	}
	
	@Test
	public void shouldCreateZlIdentifierAutoGenerationOptionsWhenItDoesNotExistOnDb() {
		PatientIdentifierType zlIdentifierType = new PatientIdentifierType();
		IdentifierPool localZlIdentifierPool = new IdentifierPool();
		
		IdentifierSourceService identifierSourceService = mock(IdentifierSourceService.class);
		when(identifierSourceService.getAutoGenerationOption(zlIdentifierType)).thenReturn(null);
		
		mirebalaisHospitalActivator.getOrCreateZlIdentifierAutoGenerationOptions(zlIdentifierType, localZlIdentifierPool,
		    identifierSourceService);
		AutoGenerationOption autoGen = buildAutoGenerationOptionsAsExpected(zlIdentifierType, localZlIdentifierPool);
		
		verify(identifierSourceService).saveAutoGenerationOption(any(AutoGenerationOption.class));
		
	}
	
	private AutoGenerationOption buildAutoGenerationOptionsAsExpected(PatientIdentifierType zlIdentifierType,
	        IdentifierPool localZlIdentifierPool) {
		AutoGenerationOption autoGen = new AutoGenerationOption();
		autoGen.setIdentifierType(zlIdentifierType);
		autoGen.setSource(localZlIdentifierPool);
		autoGen.setManualEntryEnabled(false);
		autoGen.setAutomaticGenerationEnabled(true);
		return autoGen;
	}
	
	private IdentifierPool buildLocalPoolAsExpected(PatientIdentifierType zlIdentifierType,
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
	
	private RemoteIdentifierSource buildRemoteIdentifierExpected(PatientIdentifierType zlIdentifierType) {
		RemoteIdentifierSource remoteZlIdentifierSourceExpected = new RemoteIdentifierSource();
		remoteZlIdentifierSourceExpected.setName("Remote Source for ZL Identifiers");
		remoteZlIdentifierSourceExpected.setUuid(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID);
		remoteZlIdentifierSourceExpected.setUrl(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_URL);
		remoteZlIdentifierSourceExpected.setIdentifierType(zlIdentifierType);
		return remoteZlIdentifierSourceExpected;
	}
}
