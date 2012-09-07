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
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;


/**
 *
 */
@SkipBaseSetup
public class MirebalaisHospitalActivatorIntegrationTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void beforeEachTest() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("requiredDataTestDataset.xml");
		authenticate();
	}
	
	@Test
	public void testMirebalaisHospitalActivatorStarted() throws Exception {
		int numConcepts = Context.getConceptService().getAllConcepts().size();
		MirebalaisHospitalActivator activator = new MirebalaisHospitalActivator();
		activator.started();
		Assert.assertTrue(Context.getConceptService().getAllConcepts().size() > numConcepts);
	}
	
}
