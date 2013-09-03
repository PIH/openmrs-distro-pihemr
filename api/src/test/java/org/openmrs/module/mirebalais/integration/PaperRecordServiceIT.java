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

package org.openmrs.module.mirebalais.integration;

import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.paperrecord.PaperRecordService;
import org.openmrs.module.paperrecord.PaperRecordServiceImpl;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.NotTransactional;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SkipBaseSetup
public class PaperRecordServiceIT extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private IdentifierSourceService identifierSourceService;
	
	private PatientService patientService;
	
	private AdministrationService administrationService;
	
	private PaperRecordService paperRecordService;
	
	private PaperRecordProperties paperRecordProperties;
	
	@Before
	public void setUp() {
		paperRecordService = new PaperRecordServiceImpl();
		patientService = mock(PatientService.class);
		administrationService = mock(AdministrationService.class);
		paperRecordProperties = mock(PaperRecordProperties.class);
		
		((PaperRecordServiceImpl) paperRecordService).setAdministrationService(administrationService);
		((PaperRecordServiceImpl) paperRecordService).setIdentifierSourceService(identifierSourceService);
		((PaperRecordServiceImpl) paperRecordService).setPatientService(patientService);
		((PaperRecordServiceImpl) paperRecordService).setPaperRecordProperties(paperRecordProperties);
	}
	
	@AfterClass
	public static void tearDown() {
		runtimeProperties = null;
	}
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Override
	public String getWebappName() {
		return "mirebalais";
	}
	
	@Test
	@DirtiesContext
	@NotTransactional
	public void shouldCreateTwoDifferentDossierNumbers() throws Exception {
		authenticate();
		
		PatientIdentifierType patientIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid("e66645eb-03a8-4991-b4ce-e87318e37566");
		when(paperRecordProperties.getPaperRecordIdentifierType()).thenReturn(patientIdentifierType);
		
		Location location = new Location(15);
		LocationTag locationTag = new LocationTag(15);
		locationTag.setName("tag");
		location.addTag(locationTag);
		when(paperRecordProperties.getMedicalRecordLocationLocationTag()).thenReturn(locationTag);
		
		String paperMedicalRecordNumber = ((PaperRecordServiceImpl) paperRecordService).createPaperMedicalRecordNumber(
                new Patient(), location).toString();
		assertTrue(paperMedicalRecordNumber.matches("A\\d{6}"));
		assertThat(((PaperRecordServiceImpl) paperRecordService).createPaperMedicalRecordNumber(new Patient(),
		    location).toString(), Matchers.not(eq(paperMedicalRecordNumber)));
	}
	
}
