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
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.module.mirebalais.MirebalaisConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID;
import static org.openmrs.module.mirebalais.MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE;
import static org.openmrs.module.mirebalais.MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE;
import static org.openmrs.module.mirebalais.MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID;
import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD;
import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_URL;
import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME;
import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID;

public class ConfigureIdGeneratorsTest {
	
	private ConfigureIdGenerators configureIdGenerators;
	
	private IdentifierSourceService identifierSourceService;

    private LocationService locationService;
	
	private MirebalaisHospitalService service;
	
	private PatientIdentifierType patientIdentifierType;
	
	private RuntimeProperties customProperties;
	
	@Before
	public void setUp() throws Exception {
		identifierSourceService = mock(IdentifierSourceService.class);
        locationService = mock(LocationService.class);
		service = mock(MirebalaisHospitalService.class);
		
		patientIdentifierType = new PatientIdentifierType();
		
		customProperties = mock(RuntimeProperties.class);
		configureIdGenerators = new ConfigureIdGenerators(customProperties, identifierSourceService, locationService, service);
	}
	
	@Test
	public void shouldUpdateRemoteZlIdentifierSourceWhenItExistsOnDbAndTheCustomPropertiesFileIsNotConfigured() {
		RemoteIdentifierSource remoteZlIdentifierSource = new RemoteIdentifierSource();
		when(service.getRemoteZlIdentifierSource()).thenReturn(remoteZlIdentifierSource);
		
		when(customProperties.getRemoteZlIdentifierSourceUsername()).thenReturn(REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME);
		when(customProperties.getRemoteZlIdentifierSourcePassword()).thenReturn(REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD);
		when(customProperties.getRemoteZlIdentifierSourceUrl()).thenReturn(REMOTE_ZL_IDENTIFIER_SOURCE_URL);
		
		RemoteIdentifierSource remoteZlIdentifierSourceExpected = configureIdGenerators
		        .remoteZlIdentifierSource(patientIdentifierType);
		
		verify(identifierSourceService).saveIdentifierSource(remoteZlIdentifierSource);
		
		assertSame(remoteZlIdentifierSource, remoteZlIdentifierSourceExpected);
		assertEquals(remoteZlIdentifierSourceExpected.getUrl(), remoteZlIdentifierSource.getUrl());
		assertEquals(remoteZlIdentifierSourceExpected.getUser(), remoteZlIdentifierSource.getUser());
		assertEquals(remoteZlIdentifierSourceExpected.getPassword(), remoteZlIdentifierSource.getPassword());
	}
	
	@Test
	public void shouldUpdateRemoteZlIdentifierSourceWhenItExistsOnDbAndTheCustomPropertiesFileIsConfigured() {
		RemoteIdentifierSource remoteZlIdentifierSource = new RemoteIdentifierSource();
		when(service.getRemoteZlIdentifierSource()).thenReturn(remoteZlIdentifierSource);
		
		when(customProperties.getRemoteZlIdentifierSourceUrl()).thenReturn("http://localhost");
		when(customProperties.getRemoteZlIdentifierSourceUsername()).thenReturn("user_test");
		when(customProperties.getRemoteZlIdentifierSourcePassword()).thenReturn("abc123");
		
		RemoteIdentifierSource remoteZlIdentifierSourceExpected = configureIdGenerators
		        .remoteZlIdentifierSource(patientIdentifierType);
		
		verify(identifierSourceService).saveIdentifierSource(remoteZlIdentifierSource);
		
		assertSame(remoteZlIdentifierSource, remoteZlIdentifierSourceExpected);
		assertEquals(remoteZlIdentifierSourceExpected.getUrl(), remoteZlIdentifierSource.getUrl());
		assertEquals(remoteZlIdentifierSourceExpected.getUser(), remoteZlIdentifierSource.getUser());
		assertEquals(remoteZlIdentifierSourceExpected.getPassword(), remoteZlIdentifierSource.getPassword());
	}
	
	@Test
	public void shouldConfigureDossierNumberGeneratorWhenThereIsNoConfigurationInDatabase() {
		when(service.getDossierSequenceGenerator(MirebalaisConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID)).thenThrow(IllegalStateException.class);
		
		SequentialIdentifierGenerator sequentialIdentifierGenerator = configureIdGenerators
		        .sequentialIdentifierGeneratorForDossier(patientIdentifierType,
                        MirebalaisConstants.UHM_DOSSIER_NUMBER_PREFIX,
                        MirebalaisConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID);
		
		SequentialIdentifierGenerator sequentialIdentifierGeneratorAsExpected = buildSequentialIdentifierGeneratorAsExpected();
		
		verify(identifierSourceService).saveIdentifierSource(eq(sequentialIdentifierGeneratorAsExpected));
		
		assertEquals(sequentialIdentifierGeneratorAsExpected, sequentialIdentifierGenerator);
		assertEquals("A", sequentialIdentifierGenerator.getPrefix());
		assertEquals(new Integer(7), sequentialIdentifierGenerator.getMaxLength());
        assertEquals(new Integer(7), sequentialIdentifierGenerator.getMinLength());
		assertEquals("0123456789", sequentialIdentifierGenerator.getBaseCharacterSet());
		assertEquals("000001", sequentialIdentifierGenerator.getFirstIdentifierBase());
		assertEquals(MirebalaisConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID, sequentialIdentifierGenerator.getUuid());
	}
	
	@Test
	public void shouldConfigureDossierNumberGeneratorWhenThereOneConfigurationInDatabase() {
		SequentialIdentifierGenerator sequentialIdentifierGeneratorAsExpected = buildSequentialIdentifierGeneratorAsExpected();
		when(service.getDossierSequenceGenerator(MirebalaisConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID)).thenReturn(sequentialIdentifierGeneratorAsExpected);
		
		SequentialIdentifierGenerator sequentialIdentifierGenerator = configureIdGenerators
		        .sequentialIdentifierGeneratorForDossier(patientIdentifierType,
                        MirebalaisConstants.UHM_DOSSIER_NUMBER_PREFIX,
                        MirebalaisConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID);
		
		verify(identifierSourceService, never()).saveIdentifierSource(any(SequentialIdentifierGenerator.class));
		
		assertSame(sequentialIdentifierGeneratorAsExpected, sequentialIdentifierGenerator);
		assertEquals("A", sequentialIdentifierGeneratorAsExpected.getPrefix());
		assertEquals(new Integer(7), sequentialIdentifierGeneratorAsExpected.getMaxLength());
        assertEquals(new Integer(7), sequentialIdentifierGeneratorAsExpected.getMinLength());
		assertEquals("0123456789", sequentialIdentifierGeneratorAsExpected.getBaseCharacterSet());
		assertEquals("000001", sequentialIdentifierGeneratorAsExpected.getFirstIdentifierBase());
		assertEquals(MirebalaisConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID, sequentialIdentifierGeneratorAsExpected
		        .getUuid());
	}
	
	private SequentialIdentifierGenerator buildSequentialIdentifierGeneratorAsExpected() {
		SequentialIdentifierGenerator sequentialIdentifierGenerator = new SequentialIdentifierGenerator();
		sequentialIdentifierGenerator.setUuid(UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID);
		sequentialIdentifierGenerator.setName("Sequential Generator for Dossier");
		sequentialIdentifierGenerator.setMaxLength(7);
        sequentialIdentifierGenerator.setMinLength(7);
		sequentialIdentifierGenerator.setPrefix("A");
		sequentialIdentifierGenerator.setBaseCharacterSet("0123456789");
		sequentialIdentifierGenerator.setFirstIdentifierBase("000001");
		sequentialIdentifierGenerator.setIdentifierType(patientIdentifierType);
		return sequentialIdentifierGenerator;
	}
	
	@Test
	public void shouldCreateRemoteZlIdentifierSourceWhenItDoesNotExistOnDbAndTheCustomPropertiesFileIsNotConfigured() {
		when(service.getRemoteZlIdentifierSource()).thenThrow(new IllegalStateException());
		
		when(customProperties.getRemoteZlIdentifierSourceUsername()).thenReturn(REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME);
		when(customProperties.getRemoteZlIdentifierSourcePassword()).thenReturn(REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD);
		when(customProperties.getRemoteZlIdentifierSourceUrl()).thenReturn(REMOTE_ZL_IDENTIFIER_SOURCE_URL);
		
		RemoteIdentifierSource remoteZlIdentifierSource = configureIdGenerators
		        .remoteZlIdentifierSource(patientIdentifierType);
		
		RemoteIdentifierSource remoteZlIdentifierSourceExpected = buildRemoteIdentifierExpectedWithDefaultValues(patientIdentifierType);
		verify(identifierSourceService).saveIdentifierSource(eq(remoteZlIdentifierSourceExpected));
		
		assertEquals(remoteZlIdentifierSourceExpected, remoteZlIdentifierSource);
		assertEquals(remoteZlIdentifierSourceExpected.getUrl(), remoteZlIdentifierSource.getUrl());
		assertEquals(remoteZlIdentifierSourceExpected.getUser(), remoteZlIdentifierSource.getUser());
		assertEquals(remoteZlIdentifierSourceExpected.getPassword(), remoteZlIdentifierSource.getPassword());
	}
	
	@Test
	public void shouldCreateRemoteZlIdentifierSourceWhenItDoesNotExistOnDbAndTheCustomPropertiesFileIsConfigured() {
		when(service.getRemoteZlIdentifierSource()).thenThrow(new IllegalStateException());
		
		when(customProperties.getRemoteZlIdentifierSourceUrl()).thenReturn("http://localhost");
		when(customProperties.getRemoteZlIdentifierSourceUsername()).thenReturn("user_test");
		when(customProperties.getRemoteZlIdentifierSourcePassword()).thenReturn("abc123");
		
		RemoteIdentifierSource remoteZlIdentifierSource = configureIdGenerators
		        .remoteZlIdentifierSource(patientIdentifierType);
		
		RemoteIdentifierSource remoteZlIdentifierSourceExpected = buildRemoteIdentifierExpectedWithCustomValues(
		    patientIdentifierType, "http://localhost", "user_test", "abc123");
		
		verify(identifierSourceService).saveIdentifierSource(eq(remoteZlIdentifierSourceExpected));
		assertEquals(remoteZlIdentifierSourceExpected, remoteZlIdentifierSource);
		assertEquals(remoteZlIdentifierSourceExpected.getUrl(), remoteZlIdentifierSource.getUrl());
		assertEquals(remoteZlIdentifierSourceExpected.getUser(), remoteZlIdentifierSource.getUser());
		assertEquals(remoteZlIdentifierSourceExpected.getPassword(), remoteZlIdentifierSource.getPassword());
	}
	
	@Test
	public void shouldReturnLocalZlIdentifierPoolWhenItExistsOnDb() {
		IdentifierPool identifierPool = new IdentifierPool();
		when(service.getLocalZlIdentifierPool()).thenReturn(identifierPool);
		
		IdentifierPool remoteZlIdentifierPool = configureIdGenerators.localZlIdentifierSource(new RemoteIdentifierSource());
		verify(identifierSourceService, never()).saveIdentifierSource(any(IdentifierSource.class));
		
		assertSame(identifierPool, remoteZlIdentifierPool);
	}
	
	@Test
	public void shouldCreateLocalZlIdentifierPoolWhenItDoesNotExistOnDb() {
		when(service.getLocalZlIdentifierPool()).thenThrow(new IllegalStateException());
		
		RemoteIdentifierSource remoteZlIdentifierSource = new RemoteIdentifierSource();
		
		IdentifierPool remoteZlIdentifierPool = configureIdGenerators.localZlIdentifierSource(remoteZlIdentifierSource);
		
		IdentifierPool localPool = buildLocalPoolAsExpected(patientIdentifierType, remoteZlIdentifierSource);
		verify(identifierSourceService).saveIdentifierSource(eq(localPool));
		
		assertEquals(localPool, remoteZlIdentifierPool);
	}
	
	@Ignore
	public void shouldCreateZlIdentifierAutoGenerationOptionsWhenItDoesNotExistOnDb() {
		when(identifierSourceService.getAutoGenerationOption(patientIdentifierType)).thenReturn(null);
		
		IdentifierPool localZlIdentifierPool = new IdentifierPool();
		configureIdGenerators.setAutoGenerationOptionsForZlIdentifier(localZlIdentifierPool) ;
		AutoGenerationOption autoGen = buildAutoGenerationOptionsAsExpected(patientIdentifierType, localZlIdentifierPool);
		
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
		localPool.setUuid(LOCAL_ZL_IDENTIFIER_POOL_UUID);
		localPool.setSource(remoteZlIdentifierSource);
		localPool.setIdentifierType(zlIdentifierType);
		localPool.setMinPoolSize(LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE);
		localPool.setBatchSize(LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE);
		localPool.setSequential(true);
		return localPool;
	}
	
	private RemoteIdentifierSource buildRemoteIdentifierExpectedWithDefaultValues(PatientIdentifierType zlIdentifierType) {
		RemoteIdentifierSource remoteZlIdentifierSourceExpected = new RemoteIdentifierSource();
		remoteZlIdentifierSourceExpected.setName("Remote Source for ZL Identifiers");
		remoteZlIdentifierSourceExpected.setUuid(REMOTE_ZL_IDENTIFIER_SOURCE_UUID);
		remoteZlIdentifierSourceExpected.setUrl(REMOTE_ZL_IDENTIFIER_SOURCE_URL);
		remoteZlIdentifierSourceExpected.setUser(REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME);
		remoteZlIdentifierSourceExpected.setPassword(REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD);
		remoteZlIdentifierSourceExpected.setIdentifierType(zlIdentifierType);
		return remoteZlIdentifierSourceExpected;
	}
	
	private RemoteIdentifierSource buildRemoteIdentifierExpectedWithCustomValues(PatientIdentifierType zlIdentifierType,
	        String url, String user, String password) {
		RemoteIdentifierSource remoteZlIdentifierSourceExpected = new RemoteIdentifierSource();
		remoteZlIdentifierSourceExpected.setName("Remote Source for ZL Identifiers");
		remoteZlIdentifierSourceExpected.setUuid(REMOTE_ZL_IDENTIFIER_SOURCE_UUID);
		remoteZlIdentifierSourceExpected.setUrl(url);
		remoteZlIdentifierSourceExpected.setUser(user);
		remoteZlIdentifierSourceExpected.setPassword(password);
		remoteZlIdentifierSourceExpected.setIdentifierType(zlIdentifierType);
		return remoteZlIdentifierSourceExpected;
	}
}
