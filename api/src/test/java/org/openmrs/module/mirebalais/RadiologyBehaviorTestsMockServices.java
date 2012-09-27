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
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.AdministrationServiceImpl;
import org.openmrs.api.impl.ConceptServiceImpl;
import org.openmrs.api.impl.OrderServiceImpl;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalais.api.impl.MirebalaisHospitalServiceImpl;
import org.openmrs.validator.OrderValidator;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * For BDD of the radiology integration (proof of concept using Mock Service)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class RadiologyBehaviorTestsMockServices {
	
	MirebalaisHospitalService service;
	
	private MockOrderService mockOrderService;
	
	private MockConceptService mockConceptService;
	
	private MockAdministrationService mockAdministrationService;
	
	@Before
	public void beforeEachTest() {
		mockConceptService = new MockConceptService();
		mockOrderService = new MockOrderService();
		mockAdministrationService = new MockAdministrationService();
		;
		
		mockStatic(Context.class);
		when(Context.getConceptService()).thenReturn(mockConceptService);
		when(Context.getAdministrationService()).thenReturn(mockAdministrationService);
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
		Order created = service.placeRadiologyOrder(p, cxr);
		
		Assert.assertEquals(1, mockOrderService.savedOrders.size());
		Assert.assertTrue(mockOrderService.savedOrders.contains(created));
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
	
	/**
	 * Mock of OrderService for radiology integration tests 
	 */
	@SuppressWarnings("deprecation")
	class MockOrderService extends OrderServiceImpl {
		
		public List<Order> savedOrders = new ArrayList<Order>();
		
		/**
		 * @see org.openmrs.api.impl.OrderServiceImpl#getOrderTypeByUuid(java.lang.String)
		 */
		@Override
		public OrderType getOrderTypeByUuid(String uuid) throws APIException {
			if ("radiology-order-type-uuid".equals(uuid)) {
				return new OrderType();
			} else {
				return null;
			}
		}
		
		/**
		 * @see org.openmrs.api.impl.OrderServiceImpl#saveOrder(org.openmrs.Order)
		 */
		@Override
		public Order saveOrder(Order order) throws APIException {
			validate(order, new OrderValidator());
			savedOrders.add(order);
			return order;
		}
	}
	
	/**
	 * Mock of ConceptService for Radiology integration tests
	 */
	class MockConceptService extends ConceptServiceImpl {
		
		Map<String, Concept> db;
		
		String[] contents = { "1,cxr-uuid,Chest Xray", "2,ct-scan-uuid,CT Scan" };
		
		public MockConceptService() {
			db = new HashMap<String, Concept>();
			// TODO: move this to a generic utility class
			for (String s : contents) {
				String[] strings = s.split(",");
				Concept c = new Concept();
				c.setConceptId(Integer.valueOf(strings[0]));
				c.setUuid(strings[1]);
				c.addName(new ConceptName(strings[2], Locale.ENGLISH));
				db.put(strings[1], c);
			}
		}
		
		/**
		 * @see org.openmrs.api.impl.ConceptServiceImpl#getConceptByUuid(java.lang.String)
		 */
		@Override
		public Concept getConceptByUuid(String uuid) {
			return db.get(uuid);
		}
	}
	
	class MockAdministrationService extends AdministrationServiceImpl {
		
		Map<String, String> db;
		
		public MockAdministrationService() {
			db = new HashMap<String, String>();
			db.put(MirebalaisConstants.RADIOLOGY_ORDERABLE_CONCEPTS_GP, "cxr-uuid,ct-scan-uuid");
			db.put(MirebalaisConstants.RADIOLOGY_ORDERTYPE_GP, "radiology-order-type-uuid");
		}
		
		/**
		 * @see org.openmrs.api.impl.AdministrationServiceImpl#getGlobalProperty(java.lang.String)
		 */
		@Override
		public String getGlobalProperty(String propertyName) throws APIException {
			return db.get(propertyName);
		}
		
	}
}
