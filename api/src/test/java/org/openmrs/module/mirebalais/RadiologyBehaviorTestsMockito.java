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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalais.api.impl.MirebalaisHospitalServiceImpl;
import org.openmrs.validator.OrderValidator;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * For BDD of the radiology integration (proof of concept using Mockito)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
@SuppressWarnings("deprecation")
public class RadiologyBehaviorTestsMockito {
	
	MirebalaisHospitalService service;
	private OrderService mockOrderService;
	
    @Before
	public void beforeEachTest() {
		Concept cxr = new Concept();
		cxr.setConceptId(1);
		cxr.addName(new ConceptName("Chest Xray", Locale.ENGLISH));
		
		Concept ctScan = new Concept();
		ctScan.setConceptId(2);
		ctScan.addName(new ConceptName("CT Scan", Locale.ENGLISH));

		ConceptService mockConceptService = mock(ConceptService.class);
		when(mockConceptService.getConceptByUuid("cxr-uuid")).thenReturn(cxr);
		when(mockConceptService.getConceptByUuid("ct-scan-uuid")).thenReturn(cxr);
		
		AdministrationService mockAdminService = mock(AdministrationService.class);
		when(mockAdminService.getGlobalProperty(MirebalaisConstants.RADIOLOGY_ORDERABLE_CONCEPTS_GP)).thenReturn("cxr-uuid,ct-scan-uuid");
		when(mockAdminService.getGlobalProperty(MirebalaisConstants.RADIOLOGY_ORDERTYPE_GP)).thenReturn("radiology-order-type-uuid");
		
		mockOrderService = mock(OrderService.class);
		when(mockOrderService.getOrderTypeByUuid("radiology-order-type-uuid")).thenReturn(new OrderType());
		when(mockOrderService.saveOrder(Mockito.any(Order.class))).thenAnswer(new Answer<Order>() {
			@Override
            public Order answer(InvocationOnMock invocation) throws Throwable {
	            Order toSave = (Order) invocation.getArguments()[0];
	            validate(toSave, new OrderValidator());
	            return toSave;
            }
		});
		
		mockStatic(Context.class);
		when(Context.getConceptService()).thenReturn(mockConceptService);
		when(Context.getAdministrationService()).thenReturn(mockAdminService);
		when(Context.getOrderService()).thenReturn(mockOrderService);
		
		service = new MirebalaisHospitalServiceImpl();
	}
	
	@Test
	public void shouldListOrderableRadiologyTests() {
		List<Concept> radiologyTests = service.getRadiologyOrderables();
		Assert.assertNotNull(radiologyTests);
		Assert.assertEquals(2, radiologyTests.size());
	}
	
	@Test
	public void shouldPlaceOrderForRadiologyTest() {
		String testConceptUuid = "cxr-uuid";
		Concept cxr = Context.getConceptService().getConceptByUuid(testConceptUuid);
		Assert.assertNotNull(cxr);
		
		Patient p = new Patient();
		service.placeRadiologyOrder(p, cxr);
		
		verify(mockOrderService).saveOrder((Order) Mockito.any());
	}

	/**
     * @param target
     * @param validator
     */
    private void validate(Object target, Validator validator) {
    	if (target == null) {
    		throw new NullPointerException("Cannot validate null object");
    	}
    	Errors errors = new BeanPropertyBindingResult(target, "target");
	    validator.validate(target, errors);
	    if (errors.hasErrors()) {
	    	throw new RuntimeException("Validation errors: " + errors.toString());
	    }
    }
	
}
