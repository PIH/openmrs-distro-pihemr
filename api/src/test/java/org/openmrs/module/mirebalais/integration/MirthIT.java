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

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.TestUtils;
import org.openmrs.module.mirebalais.MirebalaisGlobalProperties;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

@SkipBaseSetup
public class MirthIT extends BaseModuleContextSensitiveTest {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Override
	public String getWebappName() {
		return "openmrs_test";
	}
	
	@Before
	public void beforeEachTest() throws Exception {
		authenticate();
		
		// run the module activator so that the Mirth channels are configured
		MirebalaisHospitalActivator activator = new MirebalaisHospitalActivator();
		activator.started();
		
		Context.flushSession();
	}
	
	@Test
	public void shouldSendMessageToMirth() throws Exception {
		
		// TODO: eventually we should make sure all the necessary fields are concluded here
		
		Order order = new Order();
		order.setOrderType(Context.getOrderService().getOrderTypeByUuid("84ce45a8-5e7c-48f7-a581-bb1d17d63a62"));
		order.setPatient(Context.getPatientService().getPatient(3));
		order.setConcept(Context.getConceptService().getConcept(239));
		order.setStartDate(new Date());
		Context.getOrderService().saveOrder(order);
		Context.flushSession();
		
		String result = listenForResults();
		
		TestUtils.assertContains("MSH|^~\\&||A|||||ORM^O01||P|2.2|||||", result);
		TestUtils.assertContains("PID|||6TS-4||Chebaskwony^Collet||197608250000|F||||||||||||||||||", result);
		TestUtils.assertContains("PV1||||||||||||||||||", result);
		TestUtils.assertContains("ORC|SC||||||||||||||||||", result);
		TestUtils.assertContains("OBR|||54321|B^C|||||||||||||||E^D|||||||||||||||||200808080000", result);
		
		// TODO: do we want we tear down the Mirth channel after this?
		
	}
	
	private String listenForResults() throws IOException {
		
		ServerSocket listener = new ServerSocket(6660); // TODO: store this port in a global poroperty?
		listener.setSoTimeout(5000); // don't wait more than 5 seconds for an incoming connection
		
		Socket mirthConnection = listener.accept();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(mirthConnection.getInputStream()));
		
		StringBuilder sb = new StringBuilder();
		String line;
		
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		
		// TODO: need an acknowledgement?
		
		mirthConnection.close();
		
		return sb.toString();
	}
	
}
