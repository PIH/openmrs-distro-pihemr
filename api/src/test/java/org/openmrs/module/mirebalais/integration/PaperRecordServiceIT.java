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

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.paperrecord.PaperRecord;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.paperrecord.PaperRecordService;
import org.openmrs.module.paperrecord.PaperRecordServiceImpl;
import org.openmrs.module.paperrecord.db.PaperRecordDAO;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@SkipBaseSetup
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
@Ignore
public class PaperRecordServiceIT extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private IdentifierSourceService identifierSourceService;
	
	private PatientService patientService;
	
	private AdministrationService administrationService;

    private PaperRecordDAO paperRecordDAO;

	private PaperRecordService paperRecordService;
	
	private PaperRecordProperties paperRecordProperties;
	
	@Before
	public void setUp() {
		paperRecordService = new PaperRecordServiceImpl();
		patientService = mock(PatientService.class);
		administrationService = mock(AdministrationService.class);
		paperRecordProperties = mock(PaperRecordProperties.class);
        paperRecordDAO = mock(PaperRecordDAO.class);

		((PaperRecordServiceImpl) paperRecordService).setIdentifierSourceService(identifierSourceService);
		((PaperRecordServiceImpl) paperRecordService).setPatientService(patientService);
		((PaperRecordServiceImpl) paperRecordService).setPaperRecordProperties(paperRecordProperties);
        ((PaperRecordServiceImpl) paperRecordService).setPaperRecordDAO(paperRecordDAO);

        // so we handle the hack in PaperRecordServiceImpl where internal methods are fetched via Context.getService
        mockStatic(Context.class);
        when(Context.getService(PaperRecordService.class)).thenReturn(paperRecordService);
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
	public void shouldCreateTwoDifferentDossierNumbers() throws Exception {
		authenticate();
		
		PatientIdentifierType patientIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid("e66645eb-03a8-4991-b4ce-e87318e37566");
		when(paperRecordProperties.getPaperRecordIdentifierType()).thenReturn(patientIdentifierType);
		
		Location location = new Location(15);
		LocationTag locationTag = new LocationTag(15);
		locationTag.setName("tag");
		location.addTag(locationTag);
		when(paperRecordProperties.getMedicalRecordLocationLocationTag()).thenReturn(locationTag);
		
		PaperRecord paperRecord1  = ((PaperRecordServiceImpl) paperRecordService).createPaperRecord(
                new Patient(), location);

		assertTrue(paperRecord1.getPatientIdentifier().getIdentifier().matches("A\\d{6}"));

        PaperRecord paperRecord2 = ((PaperRecordServiceImpl) paperRecordService).createPaperRecord(
                new Patient(), location);

        assertTrue(paperRecord2.getPatientIdentifier().getIdentifier().matches("A\\d{6}"));
		assertThat(paperRecord1.getPatientIdentifier().getIdentifier(), not(paperRecord2.getPatientIdentifier().getIdentifier()));
	}
	
}
