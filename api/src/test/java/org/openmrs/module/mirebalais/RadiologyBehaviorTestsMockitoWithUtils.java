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
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.GlobalProperty;
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

import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
@SuppressWarnings("deprecation")
public class RadiologyBehaviorTestsMockitoWithUtils {
	
	MirebalaisHospitalService service;
	private OrderService mockOrderService;
	
    @Before
	public void beforeEachTest() {
    	ConceptService mockConceptService = mockConceptService();
		addMockConcept(mockConceptService, 1, "cxr-uuid", "Chest Xray");
		addMockConcept(mockConceptService, 2, "ct-scan-uuid", "CT Scan");
    	
		AdministrationService mockAdminService = mockAdministrationService();
		addMockGlobalProperty(mockAdminService, MirebalaisConstants.RADIOLOGY_ORDERABLE_CONCEPTS_GP, "cxr-uuid,ct-scan-uuid");
		addMockGlobalProperty(mockAdminService, MirebalaisConstants.RADIOLOGY_ORDERTYPE_GP, "radiology-order-type-uuid");
		
		mockOrderService = mockOrderService();
		addMockOrderType(mockOrderService, 1, "radiology-order-type-uuid", "Radiology Order");
		
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
    
    // EVERYTHING BELOW HERE GOES INTO VARIOUS TESTUTIL CLASSES //////////////////
    
    private ConceptService mockConceptService() {
    	return mock(ConceptService.class);
    }
	
    /**
     * TODO: move to TestUtil class
     * Modifies mockConceptService by adding another concept, as specified 
     * 
     * @param mockConceptService
     * @param conceptId
     * @param uuid
     * @param name
     */
    private void addMockConcept(ConceptService mockConceptService, int conceptId, String uuid, String name) {
	    Concept c = new Concept();
	    c.setConceptId(conceptId);
	    c.setUuid(uuid);
	    c.addName(new ConceptName(name, Locale.ENGLISH));
	    
	    when(mockConceptService.getConcept(conceptId)).thenReturn(c);
	    when(mockConceptService.getConceptByUuid(uuid)).thenReturn(c);
    }
    
    private AdministrationService mockAdministrationService() {
    	return mock(AdministrationService.class);
    }

    private void addMockGlobalProperty(AdministrationService mockAdministrationService, String propertyName, String propertyValue) {
	    GlobalProperty gp = new GlobalProperty(propertyName, propertyValue);
	    when(mockAdministrationService.getGlobalProperty(propertyName)).thenReturn(propertyValue);
	    when(mockAdministrationService.getGlobalPropertyObject(propertyName)).thenReturn(gp);
    }
    
    private OrderService mockOrderService() {
    	OrderService ret = mock(OrderService.class);
    	
    	when(ret.saveOrder(Mockito.any(Order.class))).thenAnswer(new Answer<Order>() {
			@Override
            public Order answer(InvocationOnMock invocation) throws Throwable {
	            Order toSave = (Order) invocation.getArguments()[0];
	            validate(toSave, new OrderValidator());
	            return toSave;
            }
		});
    	
    	return ret;
    }

    private void addMockOrderType(OrderService mockOrderService, int orderTypeId, String uuid, String name) {
    	OrderType ot = new OrderType();
    	ot.setOrderTypeId(orderTypeId);
    	ot.setUuid(uuid);
    	ot.setName(name);
	    when(mockOrderService.getOrderType(orderTypeId)).thenReturn(ot);
	    when(mockOrderService.getOrderTypeByUuid(uuid)).thenReturn(ot);
    }
    
}
