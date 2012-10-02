package org.openmrs.module.mirebalais.integration;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertEquals;

@SkipBaseSetup
public class MirebalaisHospitalActivatorIT extends BaseModuleContextSensitiveTest {
	
	private MirebalaisHospitalActivator activator;
	
	@Before
	public void beforeEachTest() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("requiredDataTestDataset.xml");
		executeDataSet("globalPropertiesTestDataset.xml");
		authenticate();
		activator = new MirebalaisHospitalActivator();
		activator.started();
	}
	
	@Test
	@DirtiesContext
	public void testThatActivatorDoesAllSetup() throws Exception {
		MirebalaisHospitalService service = Context.getService(MirebalaisHospitalService.class);
		IdentifierPool localZlIdentifierPool = service.getLocalZlIdentifierPool();
		RemoteIdentifierSource remoteZlIdentifierSource = service.getRemoteZlIdentifierSource();
		
		PatientIdentifierType zlIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(
		    MirebalaisConstants.ZL_IDENTIFIER_TYPE_UUID);
		AutoGenerationOption autoGenerationOption = Context.getService(IdentifierSourceService.class)
		        .getAutoGenerationOption(zlIdentifierType);
		
		assertEquals(MirebalaisConstants.ZL_IDENTIFIER_TYPE_UUID, zlIdentifierType.getUuid());
		assertEquals(zlIdentifierType, autoGenerationOption.getIdentifierType());
		assertEquals(localZlIdentifierPool, autoGenerationOption.getSource());
		
		assertEquals(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID, localZlIdentifierPool.getUuid());
		assertEquals(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE, localZlIdentifierPool.getBatchSize());
		assertEquals(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE, localZlIdentifierPool.getMinPoolSize());
		
		assertEquals(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID, remoteZlIdentifierSource.getUuid());
		assertEquals("http://localhost", remoteZlIdentifierSource.getUrl());
		assertEquals("user_test", remoteZlIdentifierSource.getUser());
		assertEquals("abc123", remoteZlIdentifierSource.getPassword());
		
	}
	
}
