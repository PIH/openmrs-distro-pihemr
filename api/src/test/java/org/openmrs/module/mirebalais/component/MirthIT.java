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

package org.openmrs.module.mirebalais.component;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.TestUtils;
import org.openmrs.module.event.advice.GeneralEventAdvice;
import org.openmrs.module.mirebalais.MirebalaisGlobalProperties;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.module.pacsintegration.PacsIntegrationGlobalProperties;
import org.openmrs.module.pacsintegration.api.PacsIntegrationService;
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
import java.util.Date;
import java.util.Properties;

@SkipBaseSetup
public class MirthIT extends BaseModuleContextSensitiveTest {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
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
		
		Order order = new Order();
		order.setOrderType(Context.getOrderService().getOrderTypeByUuid(PacsIntegrationGlobalProperties.RADIOLOGY_ORDER_TYPE_UUID()));
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
