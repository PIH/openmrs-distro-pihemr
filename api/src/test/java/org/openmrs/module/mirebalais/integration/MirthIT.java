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
import org.openmrs.module.pacsintegration.ConversionUtils;
import org.openmrs.module.pacsintegration.Message;
import org.openmrs.module.pacsintegration.OrmMessage;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@SkipBaseSetup
public class MirthIT extends BaseModuleContextSensitiveTest {

    protected final Log log = LogFactory.getLog(getClass());

    @Before
    public void setupDatabaseAndInstallMirthChannels() throws Exception {

        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        executeDataSet("mirthGlobalPropertiesTestDataset.xml");
        executeDataSet("mirthIntegrationTestDataset.xml");
        authenticate();

        // run the module activator so that the Mirth channels are configured
        MirebalaisHospitalActivator activator = new MirebalaisHospitalActivator();
        activator.started();
    }


    @Test
    public void testMirebalaisHospitalActivatorMirthChannelIntegration() throws Exception {

        // give Mirth channels a few seconds to start
        Thread.sleep(5000);

        // confirm that appropriate Mirth channels have been deployed
        String[] commands = new String[] {"java", "-classpath", MirebalaisGlobalProperties.MIRTH_DIRECTORY()+ "/*:" + MirebalaisGlobalProperties.MIRTH_DIRECTORY() + "/cli-lib/*",
                "com.mirth.connect.cli.launcher.CommandLineLauncher",
                "-a", "https://" + MirebalaisGlobalProperties.MIRTH_IP_ADDRESS() + ":" + MirebalaisGlobalProperties.MIRTH_ADMIN_PORT(),
                "-u", MirebalaisGlobalProperties.MIRTH_USERNAME(), "-p", MirebalaisGlobalProperties.MIRTH_PASSWORD(), "-v", "0.0.0"};
        Process mirthShell = Runtime.getRuntime().exec(commands);

        OutputStream out = mirthShell.getOutputStream();
        InputStream in = mirthShell.getInputStream();

        out.write("status\n".getBytes());
        out.close();

        String mirthStatus = IOUtils.toString(in);
        TestUtils.assertFuzzyContains("STARTED OpenMRS To Pacs", mirthStatus);
    }

    @Test
    public void shouldSendMessageToMirth() throws Exception {

        Order order = Context.getOrderService().getOrder(1001);
        OrmMessage ormMessage = ConversionUtils.createORMMessage(order, "SC");

        // TODO: these are to mock the fields we aren't current handling--these should eventually be removed so that we properly test these fields once we handle them
        ormMessage.setDeviceLocation("E");
        ormMessage.setSendingFacility("A");
        ormMessage.setUniversalServiceID("B");
        ormMessage.setUniversalServiceIDText("C");
        ormMessage.setModality("D");

        sendMessage(ormMessage);

        String result = listenForResults();

        TestUtils.assertContains("MSH|^~\\&||A|||||ORM^O01||P|2.2|||||", result);
        TestUtils.assertContains("PID|||6TS-4||Chebaskwony^Collet||197608250000|F||||||||||||||||||", result);
        TestUtils.assertContains("PV1||||||||||||||||||", result);
        TestUtils.assertContains("ORC|SC||||||||||||||||||", result);
        TestUtils.assertContains("OBR|||54321|B^C|||||||||||||||E^D|||||||||||||||||200808080000", result);

        // TODO: do we want we tear down the Mirth channel after this?

    }


    private void sendMessage(Message message) throws IOException {
        Socket socket = new Socket(MirebalaisGlobalProperties.MIRTH_IP_ADDRESS(), MirebalaisGlobalProperties.MIRTH_INPUT_PORT());
        IOUtils.write(ConversionUtils.serialize(message), socket.getOutputStream());
        socket.close();
    }

    private String listenForResults() throws IOException {

        ServerSocket listener = new ServerSocket(6660);       // TODO: store this port in a global poroperty?
        listener.setSoTimeout(5000);  // don't wait more than 5 seconds for an incoming connection

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
