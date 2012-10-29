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
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.TestOrder;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.TestUtils;
import org.openmrs.module.mirebalais.MirebalaisGlobalProperties;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.module.pacsintegration.PacsIntegrationGlobalProperties;
import org.openmrs.module.patientregistration.PatientRegistrationGlobalProperties;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.test.annotation.DirtiesContext;
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
	
	@Test
	@DirtiesContext
	@NotTransactional
	public void testMirthChannelIntegration() throws Exception {
		
		PatientService patientService = Context.getPatientService();
		EncounterService encounterService = Context.getEncounterService();
		OrderService orderService = Context.getOrderService();
		
		authenticate();
		
		// run the module activator so that the Mirth channels are configured
		MirebalaisHospitalActivator activator = new TestMirebalaisHospitalActivator();
		activator.started();
		
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
		
		// load the status
		out.write("status\n".getBytes());
		out.close();
		
		// confirm that the status shows that the Mirth channel has started
		String mirthStatus = IOUtils.toString(in);
		TestUtils.assertFuzzyContains("STARTED Read HL7 From OpenMRS Database", mirthStatus);
		TestUtils.assertFuzzyContains("STARTED Send HL7 To Pacs", mirthStatus);
		
		// stop all channels, clear messages and statistics, and restart in preparation for tests
		mirthShell = Runtime.getRuntime().exec(commands);
		out = mirthShell.getOutputStream();
		in = mirthShell.getInputStream();
		
		out.write("channel stop *\n".getBytes()); // stop all channels
		out.write("clearallmessages\n".getBytes());
		out.write("resetstats\n".getBytes());
		out.write("channel start *\n".getBytes()); // restart all channels
		out.close();
		
		// now test that when we create a new patient, a new patient message is created
		// if the test patient already exists, delete it and any existing orders
		if (patientService.getPatients("2ADMMN").size() > 0) {
			Patient patient = patientService.getPatients("2ADMMN").get(0);
			
			for (Order order : orderService.getOrdersByPatient(patient)) {
				orderService.purgeOrder(order);
			}
			
			for (Encounter encounter : encounterService.getEncountersByPatient(patient)) {
				encounterService.purgeEncounter(encounter);
			}
			
			Context.getPatientService().purgePatient(patient);
		}
		
		// TODO: eventually we should make sure all the necessary fields are included here
		// first create and save a patient
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
		
		// save the patient
		patientService.savePatient(patient);
		
		/**
		 *  Commenting this out because we are not currently sending ADT information to APCS
		String result = listenForResults();
		
		System.out.println(result);
		
		// make sure the appropriate message has been delivered
		TestUtils.assertContains("MSH|^~\\&|||||||ADT^A01||P|2.3", result);
		TestUtils.assertContains("PID|||2ADMMN||Test Patient^Mirth Integration||200003230000|M", result);
		
		// now resave the patient and verify that a patient updated message is sent
		// save the patient to trigger an update event
		patientService.savePatient(patient);
		
		result = listenForResults();
		
		System.out.println(result);
		
		TestUtils.assertContains("MSH|^~\\&|||||||ADT^A08||P|2.3", result);
		TestUtils.assertContains("PID|||2ADMMN||Test Patient^Mirth Integration||200003230000|M", result);

		 */
		
		// TODO: eventually we should make sure all the necessary fields are concluded here
		// TODO: specifically: sending facility, device location, universal service id, universal service id text, and modality
		
		// now create and save the order for this patient
		TestOrder order = new TestOrder();
		order
		        .setOrderType(Context.getOrderService().getOrderTypeByUuid(
		            Context.getAdministrationService().getGlobalProperty(
		                PacsIntegrationGlobalProperties.RADIOLOGY_ORDER_TYPE_UUID))); // TODO: change this based on how we actually end up doing orders
		order.setPatient(patient);
		order.setConcept(Context.getConceptService().getConceptByUuid("fc6de1c0-1a36-11e2-a310-aa00f871a3e1")); // chest x-ray, one view
		order.setAccessionNumber("ACCESSION NUMBER");
		Date radiologyDate = new Date();
		order.setStartDate(radiologyDate);
		order.setUrgency(Order.Urgency.STAT);
		order.setClinicalHistory("Patient fell off horse");
		
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setEncounterDatetime(new Date());
		encounter.setLocation(Context.getLocationService().getLocation("Unknown Location"));
		encounter.setEncounterType(Context.getEncounterService().getEncounterType(1));
		encounter.addOrder(order);
		encounterService.saveEncounter(encounter);
		
		String result = listenForResults();
		
		TestUtils.assertContains("MSH|^~\\&||Mirebalais|||||ORM^O01||P|2.3", result);
		TestUtils.assertContains("PID|||2ADMMN||Test Patient^Mirth Integration||200003230000|M", result);
		TestUtils.assertContains("ORC|NW", result);
		TestUtils.assertContains(
		    "OBR|||ACCESSION NUMBER|36554-4^Chest 1 view (XRay)|||||||||||||||||||||||^^^^^STAT||||^Patient fell off horse",
		    result);
	}
	
	private String listenForResults() throws IOException {
		
		ServerSocket listener = new ServerSocket(6660); // TODO: store this port in a global poroperty?
		listener.setSoTimeout(20000); // don't wait more than 20 seconds for an incoming connection
		
		Socket mirthConnection = listener.accept();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(mirthConnection.getInputStream()));
		
		StringBuilder sb = new StringBuilder();
		String line;
		
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		
		// TODO: need an acknowledgement?
		
		mirthConnection.close();
		listener.close();
		
		return sb.toString();
	}
	
	public class TestMirebalaisHospitalActivator extends MirebalaisHospitalActivator {
		
		@Override
		protected void setExistingGlobalProperty(String propertyName, String propertyValue) {
			try {
				super.setExistingGlobalProperty(propertyName, propertyValue);
			}
			catch (RuntimeException e) {
				// only log a warning if this is the "global property does not exist" exception
				if (e.getMessage().contains("global property") && e.getMessage().contains("does not exist")) {
					log.error(e.getMessage());
				} else {
					throw e;
				}
				
			}
			
		}
		
	}
}
