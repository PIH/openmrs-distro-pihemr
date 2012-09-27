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
import org.mockito.Mockito;
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
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;


public class MirebalaisHospitalActivatorTest {

    private MirebalaisHospitalActivator mirebalaisHospitalActivator;

    @Before
    public void setUp() throws Exception {
        mirebalaisHospitalActivator = new MirebalaisHospitalActivator();
    }

    @Test
    public void shouldReturnRemoteZlIdentifierSourceWhenItExistsOnDb(){
        RemoteIdentifierSource remoteIdentifierSource = new RemoteIdentifierSource();

        MirebalaisHospitalService service = Mockito.mock(MirebalaisHospitalService.class);
        when(service.getRemoteZlIdentifierSource()).thenReturn(remoteIdentifierSource);

        IdentifierSourceService identifierSourceService = mock(IdentifierSourceService.class);

        RemoteIdentifierSource remoteZlIdentifierSource = mirebalaisHospitalActivator.getOrCreateRemoteZlIdentifierSource(service,new PatientIdentifierType(), identifierSourceService);
        verify(identifierSourceService, never()).saveIdentifierSource(any(IdentifierSource.class));

        assertSame(remoteIdentifierSource, remoteZlIdentifierSource);
    }

    @Test
    public void shouldCreateRemoteZlIdentifierSourceWhenItDoesNotExistOnDbAndThereIsNoPropertiesFileConfiguration(){

        MirebalaisHospitalService service = mock(MirebalaisHospitalService.class);
        when(service.getRemoteZlIdentifierSource()).thenThrow(new IllegalStateException());

        MirebalaisCustomProperties customProperties = mockingPropertiesServiceWhenFileIsNotLoaded();
        mirebalaisHospitalActivator.setCustomProperties(customProperties);

        PatientIdentifierType zlIdentifierType = new PatientIdentifierType();

        IdentifierSourceService identifierSourceService = mock(IdentifierSourceService.class);
        RemoteIdentifierSource remoteZlIdentifierSource = mirebalaisHospitalActivator.getOrCreateRemoteZlIdentifierSource(service, zlIdentifierType, identifierSourceService);

        RemoteIdentifierSource remoteZlIdentifierSourceExpected = buildRemoteIdentifierExpected(zlIdentifierType);
        verify(identifierSourceService).saveIdentifierSource(eq(remoteZlIdentifierSourceExpected));

        assertEquals(remoteZlIdentifierSourceExpected,remoteZlIdentifierSource);
        assertEquals(remoteZlIdentifierSourceExpected.getUrl(),remoteZlIdentifierSource.getUrl());
        assertEquals(remoteZlIdentifierSourceExpected.getUser(),remoteZlIdentifierSource.getUser());
        assertEquals(remoteZlIdentifierSourceExpected.getPassword(),remoteZlIdentifierSource.getPassword());
    }

    private MirebalaisCustomProperties mockingPropertiesServiceWhenFileIsNotLoaded() {
        MirebalaisCustomProperties customProperties = mock(MirebalaisCustomProperties.class);
        when(customProperties.getRemoteZlIdentifierSourceUsername()).thenReturn(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME);
        when(customProperties.getRemoteZlIdentifierSourcePassword()).thenReturn(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD);
        when(customProperties.getRemoteZlIdentifierSourceUrl()).thenReturn(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_URL);
        return customProperties;
    }

    private MirebalaisCustomProperties mockingPropertiesServiceWhenFileIsLoaded() {
        MirebalaisCustomProperties customProperties = mock(MirebalaisCustomProperties.class);
        when(customProperties.getRemoteZlIdentifierSourceUsername()).thenReturn("user_test");
        when(customProperties.getRemoteZlIdentifierSourcePassword()).thenReturn("abc123");
        when(customProperties.getRemoteZlIdentifierSourceUrl()).thenReturn("http://localhost");
        return customProperties;
    }

    @Test
    public void shouldCreateRemoteZlIdentifierSourceWhenItDoesNotExistOnDbAndThereIsAPropertiesFileConfiguration(){
        MirebalaisHospitalService service = mock(MirebalaisHospitalService.class);
        when(service.getRemoteZlIdentifierSource()).thenThrow(new IllegalStateException());

        MirebalaisCustomProperties customProperties = mockingPropertiesServiceWhenFileIsLoaded();
        mirebalaisHospitalActivator.setCustomProperties(customProperties);

        PatientIdentifierType zlIdentifierType = new PatientIdentifierType();

        IdentifierSourceService identifierSourceService = mock(IdentifierSourceService.class);
        RemoteIdentifierSource remoteZlIdentifierSource = mirebalaisHospitalActivator.getOrCreateRemoteZlIdentifierSource(service, zlIdentifierType, identifierSourceService);

        RemoteIdentifierSource remoteZlIdentifierSourceExpected = buildRemoteIdentifierExpectedUsingPropertiesFile(zlIdentifierType);
        verify(identifierSourceService).saveIdentifierSource(eq(remoteZlIdentifierSourceExpected));

        assertEquals(remoteZlIdentifierSourceExpected,remoteZlIdentifierSource);
        assertEquals(remoteZlIdentifierSourceExpected.getUrl(),remoteZlIdentifierSource.getUrl());
        assertEquals(remoteZlIdentifierSourceExpected.getUser(),remoteZlIdentifierSource.getUser());
        assertEquals(remoteZlIdentifierSourceExpected.getPassword(),remoteZlIdentifierSource.getPassword());
    }

    @Test
    public void shouldReturnLocalZlIdentifierPoolWhenItExistsOnDb(){
        IdentifierPool identifierPool = new IdentifierPool();

        MirebalaisHospitalService service = mock(MirebalaisHospitalService.class);
        when(service.getLocalZlIdentifierPool()).thenReturn(identifierPool);

        IdentifierSourceService identifierSourceService = mock(IdentifierSourceService.class);

        IdentifierPool remoteZlIdentifierPool = mirebalaisHospitalActivator.getOrCreateLocalZlIdentifierPool(service, new PatientIdentifierType(), new RemoteIdentifierSource(), identifierSourceService);
        verify(identifierSourceService, never()).saveIdentifierSource(any(IdentifierSource.class));

        assertSame(identifierPool,remoteZlIdentifierPool);
    }

    @Test
    public void shouldCreateLocalZlIdentifierPoolWhenItDoesNotExistOnDb(){
        MirebalaisHospitalService service = mock(MirebalaisHospitalService.class);
        when(service.getLocalZlIdentifierPool()).thenThrow(new IllegalStateException());

        IdentifierSourceService identifierSourceService = mock(IdentifierSourceService.class);

        PatientIdentifierType zlIdentifierType = new PatientIdentifierType();
        RemoteIdentifierSource remoteZlIdentifierSource = new RemoteIdentifierSource();

        IdentifierPool remoteZlIdentifierPool = mirebalaisHospitalActivator.getOrCreateLocalZlIdentifierPool(service, zlIdentifierType, remoteZlIdentifierSource, identifierSourceService);

        IdentifierPool localPool = buildLocalPoolAsExpected(zlIdentifierType, remoteZlIdentifierSource);
        verify(identifierSourceService).saveIdentifierSource(eq(localPool));

        assertEquals(localPool, remoteZlIdentifierPool);
    }

    @Test
    public void shouldCreateZlIdentifierAutoGenerationOptionsWhenItDoesNotExistOnDb(){
        PatientIdentifierType zlIdentifierType = new PatientIdentifierType();
        IdentifierPool localZlIdentifierPool = new IdentifierPool();

        IdentifierSourceService identifierSourceService = mock(IdentifierSourceService.class);
        when(identifierSourceService.getAutoGenerationOption(zlIdentifierType)).thenReturn(null);

        mirebalaisHospitalActivator.getOrCreateZlIdentifierAutoGenerationOptions(zlIdentifierType, localZlIdentifierPool, identifierSourceService);
        AutoGenerationOption autoGen = buildAutoGenerationOptionsAsExpected(zlIdentifierType, localZlIdentifierPool);

        verify(identifierSourceService).saveAutoGenerationOption(any(AutoGenerationOption.class));

    }

    private AutoGenerationOption buildAutoGenerationOptionsAsExpected(PatientIdentifierType zlIdentifierType, IdentifierPool localZlIdentifierPool) {
        AutoGenerationOption autoGen = new AutoGenerationOption();
        autoGen.setIdentifierType(zlIdentifierType);
        autoGen.setSource(localZlIdentifierPool);
        autoGen.setManualEntryEnabled(false);
        autoGen.setAutomaticGenerationEnabled(true);
        return autoGen;
    }

    private IdentifierPool buildLocalPoolAsExpected(PatientIdentifierType zlIdentifierType, RemoteIdentifierSource remoteZlIdentifierSource) {
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
        remoteZlIdentifierSourceExpected.setUser(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME);
        remoteZlIdentifierSourceExpected.setPassword(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD);
        return remoteZlIdentifierSourceExpected;
    }

    private RemoteIdentifierSource buildRemoteIdentifierExpectedUsingPropertiesFile(PatientIdentifierType zlIdentifierType) {
        RemoteIdentifierSource remoteZlIdentifierSourceExpected = new RemoteIdentifierSource();
        remoteZlIdentifierSourceExpected.setName("Remote Source for ZL Identifiers");
        remoteZlIdentifierSourceExpected.setUuid(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID);
        remoteZlIdentifierSourceExpected.setUrl("http://localhost");
        remoteZlIdentifierSourceExpected.setIdentifierType(zlIdentifierType);
        remoteZlIdentifierSourceExpected.setUser("user_test");
        remoteZlIdentifierSourceExpected.setPassword("abc123");
        return remoteZlIdentifierSourceExpected;
    }
}
