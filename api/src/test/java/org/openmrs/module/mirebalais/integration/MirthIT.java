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
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.TestUtils;
import org.openmrs.module.event.advice.GeneralEventAdvice;
import org.openmrs.module.mirebalais.MirebalaisGlobalProperties;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.module.pacsintegration.PacsIntegrationConstants;
import org.openmrs.module.pacsintegration.PacsIntegrationGlobalProperties;
import org.openmrs.module.patientregistration.PatientRegistrationGlobalProperties;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.test.annotation.NotTransactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
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
		return "mirebalais";
	}
	
	@Before
	public void beforeEachTest() throws Exception {
		authenticate();
		
		// run the module activator so that the Mirth channels are configured
		MirebalaisHospitalActivator activator = new MirebalaisHospitalActivator();
		activator.started();
		
		// create the test patient, if necessary
		if (Context.getPatientService().getPatients("2ADMMN").size() == 0) {
			Patient patient = new Patient();
			patient.setGender("M");
			
			Calendar birthdate = Calendar.getInstance();
			birthdate.set(2000, 2, 23);
			patient.setBirthdate(birthdate.getTime());
			
			PersonName name = new PersonName();
			name.setFamilyName("Test Patient");
			name.setGivenName("Mirth Integration");
			patient.addName(name);
			
			PatientIdentifier identifier = new PatientIdentifier();
			identifier.setIdentifierType(PatientRegistrationGlobalProperties.GLOBAL_PROPERTY_PRIMARY_IDENTIFIER_TYPE());
			identifier.setIdentifier("2ADMMN");
			identifier.setPreferred(true);
			identifier.setLocation(Context.getLocationService().getLocation("Unknown Location"));
			patient.addIdentifier(identifier);
			
			Context.getPatientService().savePatient(patient);
		}
		
	}
	
	@Test
	@NotTransactional
	public void testMirebalaisHospitalActivatorMirthChannelIntegration() throws Exception {
		
		// give Mirth channels a few seconds to start
		Thread.sleep(5000);
		
		// confirm that appropriate Mirth channels have been deployed
		String[] commands = new String[] {
		        "java",
		        "-classpath",
		        MirebalaisGlobalProperties.MIRTH_DIRECTORY() + "/*:" + MirebalaisGlobalProperties.MIRTH_DIRECTORY()
		                + "/cli-lib/*",
		        "com.mirth.connect.cli.launcher.CommandLineLauncher",
		        "-a",
		        "https://" + MirebalaisGlobalProperties.MIRTH_IP_ADDRESS() + ":"
		                + MirebalaisGlobalProperties.MIRTH_ADMIN_PORT(), "-u", MirebalaisGlobalProperties.MIRTH_USERNAME(),
		        "-p", MirebalaisGlobalProperties.MIRTH_PASSWORD(), "-v", "0.0.0" };
		Process mirthShell = Runtime.getRuntime().exec(commands);
		
		OutputStream out = mirthShell.getOutputStream();
		InputStream in = mirthShell.getInputStream();
		
		out.write("status\n".getBytes());
		out.close();
		
		String mirthStatus = IOUtils.toString(in);
		TestUtils.assertFuzzyContains("STARTED OpenMRS To Pacs", mirthStatus);
		
	}
	
	@Test
	@NotTransactional
	public void shouldSendMessageToMirth() throws Exception {
		
		// we need to manually configure the advice since the @StartModule annotation was causing problems (see tests in PacsIntegration module)
		Context.addAdvice(OrderService.class, new GeneralEventAdvice());
		
		// TODO: eventually we should make sure all the necessary fields are concluded here
		// TODO: specifically: sending facility, device location, universal service id, universal service id text, and modality
		
		// first create the patient that we are going to send the order for
		Patient patient = Context.getPatientService().getPatients("2ADMMN").get(0);
		
		// reate and save the order
		Order order = new Order();
		order.setOrderType(Context.getOrderService().getOrderTypeByUuid(
		    PacsIntegrationGlobalProperties.RADIOLOGY_ORDER_TYPE_UUID())); // TODO: change this based on how we actually end up doing orders
		order.setPatient(patient);
		order.setConcept(Context.getConceptService().getConceptByName("X-RAY CHEST")); // TODO: replace this with an actual radiology concept
		order.setAccessionNumber("ACCESSION NUMBER");
		Date radiologyDate = new Date();
		order.setStartDate(radiologyDate);
		Context.getOrderService().saveOrder(order);
		
		String result = listenForResults();
		
		TestUtils.assertContains("MSH|^~\\&|||||||ORM^O01||P|2.2|||||", result);
		TestUtils.assertContains("PID|||2ADMMN||Test Patient^Mirth Integration||200003230000|M||||||||||||||||||", result);
		TestUtils.assertContains("PV1||||||||||||||||||", result);
		TestUtils.assertContains("ORC|NW||||||||||||||||||", result);
		TestUtils.assertContains("OBR|||ACCESSION NUMBER|^|||||||||||||||^|||||||||||||||||"
		        + PacsIntegrationConstants.hl7DateFormat.format(radiologyDate), result);
		
		// TODO: should we tear down this channel after the test is complete?
		
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
