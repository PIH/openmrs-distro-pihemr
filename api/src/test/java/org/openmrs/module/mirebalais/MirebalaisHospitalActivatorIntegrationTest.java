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

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.io.InputStream;
import java.io.OutputStream;


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

        // confirm that new concepts have been added
		Assert.assertTrue(Context.getConceptService().getAllConcepts().size() > numConcepts);

        /** commented out until we install Mirth on the CI server

        // give Mirth channels a second to start
        Thread.sleep(1000);

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

        **/

	}
	
}
