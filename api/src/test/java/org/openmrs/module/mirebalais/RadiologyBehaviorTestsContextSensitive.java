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
package org.openmrs.module.mirebalais;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

/**
 * For BDD of the radiology component (proof of concept the old way)
 */
@SuppressWarnings("deprecation")
public class RadiologyBehaviorTestsContextSensitive extends BaseModuleContextSensitiveTest {
	
	MirebalaisHospitalService service;
	
	@Before
	public void beforeEachTest() {
		service = Context.getService(MirebalaisHospitalService.class);
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty(MirebalaisConstants.RADIOLOGY_ORDERABLE_CONCEPTS_GP,
		            "c607c80f-1ea9-4da3-bb88-6276ce8868dd,a09ab2c5-878e-4905-b25d-5784167d0216"));
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty(MirebalaisConstants.RADIOLOGY_ORDERTYPE_GP, "e23733ab-787e-4096-8ba2-577a902d2c2b"));
	}
	
	@Test
	public void shouldListOrderableRadiologyTests() {
		List<Concept> radiologyTests = service.getRadiologyOrderables();
		Assert.assertNotNull(radiologyTests);
		Assert.assertEquals(2, radiologyTests.size());
	}
	
	@Test
	public void shouldPlaceOrderForRadiologyTest() {
		Concept cxr = Context.getConceptService().getConcept(5089);
		Assert.assertNotNull(cxr);
		
		Patient p = Context.getPatientService().getPatient(6);
		Order created = service.placeRadiologyOrder(p, cxr);
		
		Assert.assertEquals(1, Context.getOrderService().getOrdersByPatient(p).size());
		Assert.assertEquals(created, Context.getOrderService().getOrdersByPatient(p).get(0));
	}
	
}
