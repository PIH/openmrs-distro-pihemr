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

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.PatientToAppointmentDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.dataset.definition.AppointmentDataSetDefinition;
import org.openmrs.module.appointmentschedulingui.AppointmentSchedulingUIConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.account.AccountDomainWrapper;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.MirebalaisGlobalProperties;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.module.pacsintegration.PacsIntegrationConstants;
import org.openmrs.module.paperrecord.PaperRecordConstants;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.RowPerObjectDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@SkipBaseSetup          // note that we skip the base setup because we don't want to include the standard test data
public class MirebalaisHospitalActivatorComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private AdministrationService adminService;

    @Autowired
    private DataSetDefinitionService dataSetDefinitionService;

    @Autowired
    private PaperRecordProperties paperRecordProperties;

    private MirebalaisHospitalActivator activator;

    @Before
    public void beforeEachTest() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        executeDataSet("globalPropertiesTestDataset.xml");
        executeDataSet("mirebalaisProviderIdentifierGeneratorComponentTestDataset.xml");
        executeDataSet("fromMirebalaisMetadataModule.xml");
        executeDataSet("serializedReportingDataset.xml");
        authenticate();
        activator = new MirebalaisHospitalActivator();
        activator.willRefreshContext();
        activator.contextRefreshed();
        activator.willStart();
        activator.started();

    }

    @Test
    public void testThatActivatorDoesAllSetup() throws Exception {
        verifyGlobalPropertiesConfigured();
        verifyDatetimeFormatting();
        verifyPacsIntegrationGlobalPropertiesConfigured();
        verifyLocationAttributeNotOverwritten();
        verifyMirebalaisProviderIdentifierGeneratorConfigured();
        verifyCloseStalePullRequestsTaskScheduledAndStarted();
        verifyCloseStaleCreateRequestsTaskScheduledAndStarted();
        verifyMarkAppointmentsAsMissedOrCompletedScheduledAndStarted();
        verifyDailyAppointmentsDataSetUpdated();
    }

    private void verifyGlobalPropertiesConfigured() throws Exception {
        assertEquals("fr", Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE));
        assertEquals(
                "<org.openmrs.layout.web.address.AddressTemplate><nameMappings class=\"properties\"><property name=\"country\" value=\"mirebalais.address.country\"/><property name=\"stateProvince\" value=\"mirebalais.address.stateProvince\"/><property name=\"cityVillage\" value=\"mirebalais.address.cityVillage\"/><property name=\"address3\" value=\"mirebalais.address.neighborhoodCell\"/><property name=\"address1\" value=\"mirebalais.address.address1\"/><property name=\"address2\" value=\"mirebalais.address.address2\"/></nameMappings><sizeMappings class=\"properties\"><property name=\"country\" value=\"40\"/><property name=\"stateProvince\" value=\"40\"/><property name=\"cityVillage\" value=\"40\"/><property name=\"address3\" value=\"60\"/><property name=\"address1\" value=\"60\"/><property name=\"address2\" value=\"60\"/></sizeMappings><elementDefaults class=\"properties\"><property name=\"country\" value=\"Haiti\"/></elementDefaults><lineByLineFormat><string>address2</string><string>address1</string><string>address3, cityVillage</string><string>stateProvince, country</string></lineByLineFormat></org.openmrs.layout.web.address.AddressTemplate>",
                MirebalaisGlobalProperties.ADDRESS_LAYOUT_FORMAT());
        assertEquals("false", Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_UPPER_AND_LOWER_CASE));
        assertEquals("false", Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_NON_DIGIT));
        assertEquals("false", Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_DIGIT));
        assertEquals("8", Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GP_PASSWORD_MINIMUM_LENGTH));

        // confirm that old global property has been removed
        assertNull(Context.getAdministrationService().getGlobalPropertyObject(EmrApiConstants.GP_CONSULT_ENCOUNTER_TYPE));

    }

    private void verifyDatetimeFormatting() {
        Date sampleDate = new DateTime(2012, 2, 22, 14, 23, 22).toDate();

        assertEquals("22 Feb 2012 02:23 PM", new SimpleDateFormat(adminService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT)).format(sampleDate));
        assertEquals("22 Feb 2012", new SimpleDateFormat(adminService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT)).format(sampleDate));
    }

    private void verifyPacsIntegrationGlobalPropertiesConfigured() throws Exception {
        assertEquals("a541af1e-105c-40bf-b345-ba1fd6a59b85", Context.getAdministrationService().getGlobalProperty(
                PacsIntegrationConstants.GP_PATIENT_IDENTIFIER_TYPE_UUID));
        assertEquals("en", Context.getAdministrationService().getGlobalProperty(
                PacsIntegrationConstants.GP_DEFAULT_LOCALE));
        assertEquals("Mirebalais", Context.getAdministrationService().getGlobalProperty(
                PacsIntegrationConstants.GP_SENDING_FACILITY));
        assertEquals("2889f378-f287-40a5-ac9c-ce77ee963ed7", Context.getAdministrationService().getGlobalProperty(
                PacsIntegrationConstants.GP_PROCEDURE_CODE_CONCEPT_SOURCE_UUID));
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



}
