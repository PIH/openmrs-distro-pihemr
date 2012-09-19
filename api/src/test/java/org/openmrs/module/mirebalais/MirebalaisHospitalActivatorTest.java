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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.validator.ConceptValidator;
import org.openmrs.validator.ValidateUtil;
import org.springframework.validation.ValidationUtils;

@SkipBaseSetup
public class MirebalaisHospitalActivatorTest extends BaseModuleContextSensitiveTest {

    @Before
    public void beforeEachTest() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        executeDataSet("globalproperty.xml");
        executeDataSet("mirthGlobalPropertiesTestDataset.xml");
        authenticate();
    }

    @Test
    public void testMirebalaisHospitalActivatorStarted() throws Exception {

        int numConcepts = Context.getConceptService().getAllConcepts().size();
        MirebalaisHospitalActivator activator = new MirebalaisHospitalActivator();
        activator.started();

        // confirm that new concepts have been added
        Assert.assertTrue(Context.getConceptService().getAllConcepts().size() > numConcepts);

        for (Concept concept : Context.getConceptService().getAllConcepts()) {
            ValidateUtil.validate(concept);
        }

    }

}
