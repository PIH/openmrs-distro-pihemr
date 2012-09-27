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
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
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
import static org.openmrs.module.mirebalais.MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE;
import static org.openmrs.module.mirebalais.MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE;
import static org.openmrs.module.mirebalais.MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID;
import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD;
import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_URL;
import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME;
import static org.openmrs.module.mirebalais.MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID;

public class MirebalaisHospitalActivatorTest {

    private MirebalaisHospitalActivator mirebalaisHospitalActivator;
    private IdentifierSourceService identifierSourceService;
    private MirebalaisHospitalService service;
    private PatientIdentifierType defaultPatientIdentifierType;
    private MirebalaisCustomProperties customProperties;

    @Before
	public void setUp() throws Exception {
		mirebalaisHospitalActivator = new MirebalaisHospitalActivator();
        identifierSourceService = mock(IdentifierSourceService.class);
        service = mock(MirebalaisHospitalService.class);
        defaultPatientIdentifierType = new PatientIdentifierType();
        customProperties = mock(MirebalaisCustomProperties.class);
        mirebalaisHospitalActivator.setCustomProperties(customProperties);
    }

	@Test
	public void shouldUpdateRemoteZlIdentifierSourceWhenItExistsOnDbAndTheCustomPropertiesFileIsNotConfigured() {
		RemoteIdentifierSource remoteZlIdentifierSource = new RemoteIdentifierSource();
        when(service.getRemoteZlIdentifierSource()).thenReturn(remoteZlIdentifierSource);

        when(customProperties.getRemoteZlIdentifierSourceUsername()).thenReturn(REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME);
        when(customProperties.getRemoteZlIdentifierSourcePassword()).thenReturn(REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD);
        when(customProperties.getRemoteZlIdentifierSourceUrl()).thenReturn(REMOTE_ZL_IDENTIFIER_SOURCE_URL);

        RemoteIdentifierSource remoteZlIdentifierSourceExpected =
                mirebalaisHospitalActivator.getOrCreateRemoteZlIdentifierSource(service, defaultPatientIdentifierType, identifierSourceService);

		verify(identifierSourceService).saveIdentifierSource(remoteZlIdentifierSource);

		assertSame(remoteZlIdentifierSource, remoteZlIdentifierSourceExpected);
        assertEquals(remoteZlIdentifierSourceExpected.getUrl(),remoteZlIdentifierSource.getUrl());
        assertEquals(remoteZlIdentifierSourceExpected.getUser(),remoteZlIdentifierSource.getUser());
        assertEquals(remoteZlIdentifierSourceExpected.getPassword(),remoteZlIdentifierSource.getPassword());
	}

    @Test
    public void shouldUpdateRemoteZlIdentifierSourceWhenItExistsOnDbAndTheCustomPropertiesFileIsConfigured() {
        RemoteIdentifierSource remoteZlIdentifierSource = new RemoteIdentifierSource();
        when(service.getRemoteZlIdentifierSource()).thenReturn(remoteZlIdentifierSource);

        when(customProperties.getRemoteZlIdentifierSourceUrl()).thenReturn("http://localhost");
        when(customProperties.getRemoteZlIdentifierSourceUsername()).thenReturn("user_test");
        when(customProperties.getRemoteZlIdentifierSourcePassword()).thenReturn("abc123");

        RemoteIdentifierSource remoteZlIdentifierSourceExpected =
                mirebalaisHospitalActivator.getOrCreateRemoteZlIdentifierSource(service, defaultPatientIdentifierType, identifierSourceService);

        verify(identifierSourceService).saveIdentifierSource(remoteZlIdentifierSource);

        assertSame(remoteZlIdentifierSource, remoteZlIdentifierSourceExpected);
        assertEquals(remoteZlIdentifierSourceExpected.getUrl(),remoteZlIdentifierSource.getUrl());
        assertEquals(remoteZlIdentifierSourceExpected.getUser(),remoteZlIdentifierSource.getUser());
        assertEquals(remoteZlIdentifierSourceExpected.getPassword(),remoteZlIdentifierSource.getPassword());
    }

	@Test
	public void shouldCreateRemoteZlIdentifierSourceWhenItDoesNotExistOnDbAndTheCustomPropertiesFileIsNotConfigured() {
		when(service.getRemoteZlIdentifierSource()).thenThrow(new IllegalStateException());

        when(customProperties.getRemoteZlIdentifierSourceUsername()).thenReturn(REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME);
        when(customProperties.getRemoteZlIdentifierSourcePassword()).thenReturn(REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD);
        when(customProperties.getRemoteZlIdentifierSourceUrl()).thenReturn(REMOTE_ZL_IDENTIFIER_SOURCE_URL);

		RemoteIdentifierSource remoteZlIdentifierSource = mirebalaisHospitalActivator.getOrCreateRemoteZlIdentifierSource(
		    service, defaultPatientIdentifierType, identifierSourceService);

        RemoteIdentifierSource remoteZlIdentifierSourceExpected = buildRemoteIdentifierExpectedWithDefaultValues(defaultPatientIdentifierType);
		verify(identifierSourceService).saveIdentifierSource(eq(remoteZlIdentifierSourceExpected));

        assertEquals(remoteZlIdentifierSourceExpected, remoteZlIdentifierSource);
        assertEquals(remoteZlIdentifierSourceExpected.getUrl(),remoteZlIdentifierSource.getUrl());
        assertEquals(remoteZlIdentifierSourceExpected.getUser(),remoteZlIdentifierSource.getUser());
        assertEquals(remoteZlIdentifierSourceExpected.getPassword(),remoteZlIdentifierSource.getPassword());
	}

    @Test
    public void shouldCreateRemoteZlIdentifierSourceWhenItDoesNotExistOnDbAndTheCustomPropertiesFileIsConfigured() {
        when(service.getRemoteZlIdentifierSource()).thenThrow(new IllegalStateException());

        when(customProperties.getRemoteZlIdentifierSourceUrl()).thenReturn("http://localhost");
        when(customProperties.getRemoteZlIdentifierSourceUsername()).thenReturn("user_test");
        when(customProperties.getRemoteZlIdentifierSourcePassword()).thenReturn("abc123");

        RemoteIdentifierSource remoteZlIdentifierSource = mirebalaisHospitalActivator.getOrCreateRemoteZlIdentifierSource(
                service, defaultPatientIdentifierType, identifierSourceService);

        RemoteIdentifierSource remoteZlIdentifierSourceExpected = buildRemoteIdentifierExpectedWithCustomValues(defaultPatientIdentifierType, "http://localhost", "user_test", "abc123");

        verify(identifierSourceService).saveIdentifierSource(eq(remoteZlIdentifierSourceExpected));
        assertEquals(remoteZlIdentifierSourceExpected, remoteZlIdentifierSource);
        assertEquals(remoteZlIdentifierSourceExpected.getUrl(),remoteZlIdentifierSource.getUrl());
        assertEquals(remoteZlIdentifierSourceExpected.getUser(),remoteZlIdentifierSource.getUser());
        assertEquals(remoteZlIdentifierSourceExpected.getPassword(),remoteZlIdentifierSource.getPassword());
    }

	@Test
	public void shouldReturnLocalZlIdentifierPoolWhenItExistsOnDb() {
		IdentifierPool identifierPool = new IdentifierPool();
		when(service.getLocalZlIdentifierPool()).thenReturn(identifierPool);

		IdentifierPool remoteZlIdentifierPool = mirebalaisHospitalActivator.getOrCreateLocalZlIdentifierPool(service,
                defaultPatientIdentifierType, new RemoteIdentifierSource(), identifierSourceService);
		verify(identifierSourceService, never()).saveIdentifierSource(any(IdentifierSource.class));

		assertSame(identifierPool, remoteZlIdentifierPool);
	}

	@Test
	public void shouldCreateLocalZlIdentifierPoolWhenItDoesNotExistOnDb() {
		when(service.getLocalZlIdentifierPool()).thenThrow(new IllegalStateException());

		RemoteIdentifierSource remoteZlIdentifierSource = new RemoteIdentifierSource();

		IdentifierPool remoteZlIdentifierPool = mirebalaisHospitalActivator.getOrCreateLocalZlIdentifierPool(service,
		    defaultPatientIdentifierType, remoteZlIdentifierSource, identifierSourceService);

		IdentifierPool localPool = buildLocalPoolAsExpected(defaultPatientIdentifierType, remoteZlIdentifierSource);
		verify(identifierSourceService).saveIdentifierSource(eq(localPool));

		assertEquals(localPool, remoteZlIdentifierPool);
	}

	@Test
	public void shouldCreateZlIdentifierAutoGenerationOptionsWhenItDoesNotExistOnDb() {
		when(identifierSourceService.getAutoGenerationOption(defaultPatientIdentifierType)).thenReturn(null);

        IdentifierPool localZlIdentifierPool = new IdentifierPool();
        mirebalaisHospitalActivator.getOrCreateZlIdentifierAutoGenerationOptions(defaultPatientIdentifierType, localZlIdentifierPool,
		    identifierSourceService);
		AutoGenerationOption autoGen = buildAutoGenerationOptionsAsExpected(defaultPatientIdentifierType, localZlIdentifierPool);

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

    private RemoteIdentifierSource buildRemoteIdentifierExpectedWithCustomValues(PatientIdentifierType zlIdentifierType, String url, String user, String password) {
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
