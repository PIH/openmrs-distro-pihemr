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

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.PatientToAppointmentDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.dataset.definition.AppointmentDataSetDefinition;
import org.openmrs.module.appointmentschedulingui.AppointmentSchedulingUIConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.account.AccountDomainWrapper;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.module.paperrecord.PaperRecordConstants;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.pihcore.PihCoreActivator;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.ConceptsFromMetadataSharing;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SkipBaseSetup          // note that we skip the base setup because we don't want to include the standard test data
public class MirebalaisHospitalActivatorComponentTest extends BaseModuleContextSensitiveTest {

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.setProperty("pih.config", "mirebalais");
        return p;
    }

    @Autowired
    private AccountService accountService;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private DataSetDefinitionService dataSetDefinitionService;

    @Autowired
    private PaperRecordProperties paperRecordProperties;

    private MirebalaisHospitalActivator mirebalaisHospitalActivator;

    private PihCoreActivator pihCoreActivator;

    @Autowired
    private MetadataDeployService deployService;

    @Autowired
    private ConceptsFromMetadataSharing conceptsFromMetadataSharing;

    @Before
    public void beforeEachTest() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        executeDataSet("globalPropertiesTestDataset.xml");
        executeDataSet("mirebalaisProviderIdentifierGeneratorComponentTestDataset.xml");
        executeDataSet("fromMirebalaisMetadataModule.xml");
        executeDataSet("serializedReportingDataset.xml");
        authenticate();

        deployService.installBundle(conceptsFromMetadataSharing);

        // set up metatdata from pih core first
        pihCoreActivator = new PihCoreActivator();
        Config config = mock(Config.class);
        when(config.getCountry()).thenReturn(ConfigDescriptor.Country.HAITI);
        when(config.getSite()).thenReturn(ConfigDescriptor.Site.MIREBALAIS);
        pihCoreActivator.setConfig(config);
        pihCoreActivator.started();

        mirebalaisHospitalActivator = new MirebalaisHospitalActivator();
        mirebalaisHospitalActivator.setTestMode(true);  // TODO: get ReportSetup to work while testing so we no longer need this
        mirebalaisHospitalActivator.willRefreshContext();
        mirebalaisHospitalActivator.contextRefreshed();
        mirebalaisHospitalActivator.willStart();
        mirebalaisHospitalActivator.started();

    }

    @Test
    public void testThatActivatorDoesAllSetup() throws Exception {
		verifyGlobalPropertiesConfigured();
        verifyLocationAttributeNotOverwritten();
        verifyMirebalaisProviderIdentifierGeneratorConfigured();
        verifyCloseStalePullRequestsTaskScheduledAndStarted();
        verifyCloseStaleCreateRequestsTaskScheduledAndStarted();
        verifyMarkAppointmentsAsMissedOrCompletedScheduledAndStarted();
        verifyDailyAppointmentsDataSetUpdated();
        verifyLocationTags();
    }

	private void verifyGlobalPropertiesConfigured() throws Exception {
		// confirm that old global property has been removed
		assertNull(Context.getAdministrationService().getGlobalPropertyObject(EmrApiConstants.GP_CONSULT_ENCOUNTER_TYPE));
	}

    private void verifyLocationAttributeNotOverwritten() throws Exception {
        // make sure that when importing the location metadata package, the location
        // attribute we defined in the requiredDataTestDataset has not been overwritten

        Location location = Context.getLocationService().getLocation(1001);
        LocationAttributeType type = Context.getLocationService().getLocationAttributeType(1001);
        assertEquals(1, location.getActiveAttributes(type).size());
        assertEquals("Mark", location.getActiveAttributes(type).get(0).getValue().toString());
    }

    private void verifyMirebalaisProviderIdentifierGeneratorConfigured() {
        Person person = Context.getPersonService().getPerson(2);
        AccountDomainWrapper account = accountService.getAccountByPerson(person);
        accountService.saveAccount(account);
        assertEquals("MCEPM", account.getProvider().getIdentifier());
    }

    private void verifyCloseStalePullRequestsTaskScheduledAndStarted() {

        TaskDefinition taskDefinition = schedulerService.getTaskByName(PaperRecordConstants.TASK_CLOSE_STALE_PULL_REQUESTS);

        assertNotNull(taskDefinition);
        assertTrue(taskDefinition.getStarted());
        assertTrue(taskDefinition.getStartOnStartup());
        assertEquals(new Long(3600), taskDefinition.getRepeatInterval());

    }

    private void verifyCloseStaleCreateRequestsTaskScheduledAndStarted() {

        TaskDefinition taskDefinition = schedulerService.getTaskByName(PaperRecordConstants.TASK_CLOSE_STALE_CREATE_REQUESTS);

        assertNotNull(taskDefinition);
        assertTrue(taskDefinition.getStarted());
        assertTrue(taskDefinition.getStartOnStartup());
        assertEquals(new Long(3600), taskDefinition.getRepeatInterval());

    }

    private void  verifyMarkAppointmentsAsMissedOrCompletedScheduledAndStarted() {
        TaskDefinition taskDefinition = schedulerService.getTaskByName(MirebalaisConstants.TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED);

        assertNotNull(taskDefinition);
        assertTrue(taskDefinition.getStarted());
        assertTrue(taskDefinition.getStartOnStartup());
        assertEquals(MirebalaisConstants.TASK_MARK_APPOINTMENTS_AS_MISSED_OR_COMPLETED_REPEAT_INTERVAL, taskDefinition.getRepeatInterval());
    }


    private void verifyDailyAppointmentsDataSetUpdated() {

        AppointmentDataSetDefinition dsd =
                (AppointmentDataSetDefinition) dataSetDefinitionService.getDefinition(AppointmentSchedulingUIConstants.DAILY_SCHEDULED_APPOINTMENT_DATA_SET_DEFINITION_UUID, AppointmentDataSetDefinition.class);
        RowPerObjectColumnDefinition identifierColumn = (RowPerObjectColumnDefinition) dsd.getColumnDefinition("identifier");
        PatientToAppointmentDataDefinition patientToAppointmentDataDefinition = (PatientToAppointmentDataDefinition) identifierColumn.getDataDefinition().getParameterizable();
        PatientIdentifierDataDefinition patientIdentifierDataDefinition = (PatientIdentifierDataDefinition) patientToAppointmentDataDefinition.getJoinedDefinition();

        assertThat(patientIdentifierDataDefinition.getTypes().size(), is(1));
        assertThat(patientIdentifierDataDefinition.getTypes().get(0), is(paperRecordProperties.getPaperRecordIdentifierType()));
    }

    private void verifyLocationTags() {
        // test a couple of sentinel locations
        // mirebalais cdi parent should support neither login nor admission nor transfer
        Location location = Context.getLocationService().getLocationByUuid(MirebalaisLocations.MIREBALAIS_CDI_PARENT.uuid());
        assertThat(location.hasTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_LOGIN), Is.is(false));
        assertThat(location.hasTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION), Is.is(false));
        assertThat(location.hasTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER), Is.is(false));

        // outpatient clinic should support login and transfer
        location = Context.getLocationService().getLocationByUuid(MirebalaisLocations.OUTPATIENT_CLINIC.uuid());
        assertThat(location.hasTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_LOGIN), Is.is(true));
        assertThat(location.hasTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION), Is.is(false));
        assertThat(location.hasTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER), Is.is(true));

        // pre-natal should support login, admission and transfer
        location = Context.getLocationService().getLocationByUuid(MirebalaisLocations.ANTEPARTUM_WARD.uuid());
        assertThat(location.hasTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_LOGIN), Is.is(true));
        assertThat(location.hasTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION), Is.is(true));
        assertThat(location.hasTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER), Is.is(true));

        // outpatient pharmacy should support dispensing medication encounter
        location = Context.getLocationService().getLocationByUuid(MirebalaisLocations.OUTPATIENT_CLINIC_PHARMACY.uuid());
        assertThat(location.hasTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_DISPENSING), Is.is(true));
    }

}
