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

package org.openmrs.module.mirebalais.reports;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttributeType;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.mirebalaisreports.MirebalaisReportsProperties;
import org.openmrs.module.mirebalaisreports.library.EncounterDataLibrary;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;
import org.openmrs.module.pihcore.metadata.haiti.HaitiPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.module.pihcore.reporting.BaseReportTest;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@SkipBaseSetup
public class EncounterDataLibraryTest extends BaseReportTest {

    @Autowired
    private EncounterDataLibrary library;

    @Autowired
    private EncounterDataService encounterDataService;

    @Autowired
    MirebalaisReportsProperties mirebalaisReportsProperties;

    @Autowired
    TestDataManager data;

    @Autowired
    ProviderService providerService;

    private EncounterEvaluationContext context;
    private EncounterIdSet encounterIdSet;
    private Visit v1;
    private Encounter e1;
    private Encounter e2;
    private Encounter e3;
    private Encounter surgery;

    @Before
    public void setUp() throws Exception {
        VisitType visitType = emrApiProperties.getAtFacilityVisitType();
        EncounterType checkIn = Metadata.lookup(EncounterTypes.CHECK_IN);
        EncounterType admission = Metadata.lookup(EncounterTypes.ADMISSION);
        EncounterType consult = Metadata.lookup(EncounterTypes.CONSULTATION);
        EncounterType exitFromInpatient = Metadata.lookup(EncounterTypes.EXIT_FROM_CARE);
        Location visitLocation = Metadata.lookup(MirebalaisLocations.MIREBALAIS_HOSPITAL);
        Location outpatient = Metadata.lookup(MirebalaisLocations.OUTPATIENT_CLINIC);
        Location clinicRegistration = Metadata.lookup(MirebalaisLocations.CLINIC_REGISTRATION);
        Location mirebalaisHospital = Metadata.lookup(MirebalaisLocations.MIREBALAIS_HOSPITAL);
        Location womensWard = Metadata.lookup(MirebalaisLocations.WOMENS_INTERNAL_MEDICINE);
        PatientIdentifierType zlEmrId = Metadata.lookup(HaitiPatientIdentifierTypes.ZL_EMR_ID);
        PersonAttributeType unknownPatient = Metadata.lookup(PersonAttributeTypes.UNKNOWN_PATIENT);
        EncounterRole consultingClinician = mirebalaisReportsProperties.getConsultingClinicianEncounterRole();
        EncounterRole attendingSurgeonRole = mirebalaisReportsProperties.getAttendingSurgeonEncounterRole();
        Provider unknownProvider = providerService.getProvider(1);

        PersonAddress addr = new PersonAddress();
        addr.setAddress1("1050 Wishard Blvd.");
        addr.setAddress2("RG5");
        addr.setAddress3("RBI");
        addr.setCityVillage("Indianapolis");
        addr.setStateProvince("IN");

        User paulaMorris = data.user().personName("Paula", null, "Morris").username("pmorris").gender("F").save();
        Provider surgeon = data.randomProvider().personName("Bob", null, "MD").gender("M").save();

        Patient p1 = data.randomPatient().clearIdentifiers().identifier(zlEmrId, "Y2C4VA", mirebalaisHospital).personAttribute(unknownPatient, "false")
                .female().birthdate("1946-05-26", false).dateCreated("2013-10-01").uuid("be7890be-36a4-11e3-b90a-a351ac6b1528")
                .address(addr)
                .dead(true).deathDate("2013-12-01 00:00:00.0").causeOfDeath("unknown", "PIH").save();
        v1 = data.visit().patient(p1).started("2013-10-02 09:15:00").stopped("2013-10-14 04:30:00").location(visitLocation).visitType(visitType).save();
        e1 = data.encounter().visit(v1).encounterType(checkIn).location(clinicRegistration).encounterDatetime("2013-10-02 09:15:00").dateCreated("2013-10-01 00:00:00.0").creator(paulaMorris).save();
        e2 = data.encounter().visit(v1).encounterType(admission).location(womensWard).encounterDatetime("2013-10-02 12:30:00").dateCreated("2013-10-03 00:00:00.0").creator(paulaMorris).save();
        e3 = data.encounter().visit(v1).encounterType(consult).location(womensWard).encounterDatetime("2013-10-02 12:45:00")
                .dateCreated("2013-10-02 00:00:00.0").creator(paulaMorris).provider(consultingClinician, unknownProvider).save();

        data.obs().encounter(e3).concept("RETURN VISIT DATE", "PIH").value(DateUtil.parseDate("2013-11-02", "yyyy-MM-dd")).save();
        data.obs().encounter(e3).concept("CLINICAL IMPRESSION COMMENTS", "PIH").value("comment").save();
        data.obs().encounter(e3).concept("HUM Disposition categories", "PIH").value("Transfer within hospital", "PIH").save();
        data.obs().encounter(e3).concept("Transfer out location", "PIH").value("Non-ZL supported site", "PIH").save();
        data.obs().encounter(e3).concept("Type of trauma", "PIH").value("Transport Accident", "PIH").save();
        data.obs().encounter(e3).concept("Surgical service", "PIH").value("Vascular Surgery", "PIH").save();
        data.obs().encounter(e3).concept("DIAGNOSIS", "PIH").value("Bitten by suspected rabid animal", "PIH").save();
        data.obs().encounter(e3).concept("DIAGNOSIS", "PIH").value("NEONATAL SEPSIS", "PIH").save();
        data.obs().encounter(e3).concept("Diagnosis or problem, non-coded", "PIH").value("Something incurable").save();
        data.obs().encounter(e3).concept("Diagnosis or problem, non-coded", "PIH").value("Something benign").save();

        surgery = data.randomEncounter().patient(p1).provider(attendingSurgeonRole, surgeon).save();
        data.obs().encounter(surgery).concept("Name of assistant surgeon", "PIH").value("Dr. Paul").save();

        /*
    <obs obs_id="10006" person_id="1000" concept_id="1215" encounter_id="10003" obs_datetime="2013-10-02 12:45:00.0" location_id="32" comments="" creator="1" date_created="2013-10-02 12:45:00.0" voided="false" uuid="5c9967b4-36a5-11e3-b90a-a351ac6b1531" value_coded="1215"/>
    <person_name person_name_id="1000" person_id="1" preferred="true" given_name="Paula" family_name="Morris" creator="1" date_created="2005-09-22 00:00:00.0" voided="false"/>

         */

        Patient p2 = data.randomPatient().female().birthdate("1975-01-02", false).dateCreated("2013-10-01").uuid("d2c28390-36a4-11e3-b90a-a351ac6b1528").save();
        Visit v2 = data.visit().patient(p2).visitType(visitType).started("2013-10-01 17:30:00").stopped("2013-10-03 12:45:00").location(visitLocation).save();
        data.encounter().visit(v2).encounterType(checkIn).location(outpatient).encounterDatetime("2013-10-01 17:30:00").save();
        data.encounter().visit(v2).encounterType(admission).location(womensWard).encounterDatetime("2013-10-01 18:30:00").save();
        data.encounter().visit(v1).encounterType(exitFromInpatient).location(womensWard).encounterDatetime("2013-10-02 23:45:00").save();

        Context.flushSession();

        context = new EncounterEvaluationContext();

        encounterIdSet = new EncounterIdSet(e1.getId(), e2.getId(), e3.getId());
    }

    @Ignore("H2 doesn't support the DATE() function to cast a datetime to a date")
    @Test
    public void testReturnVisitDate() throws Exception {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getReturnVisitDate();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat(data.getData().get(e1.getId()), nullValue());
        assertThat(data.getData().get(e2.getId()), nullValue());
        assertThat((Timestamp) data.getData().get(e3.getId()), is(new Timestamp(DateUtil.parseDate("2013-11-02", "yyyy-MM-dd").getTime())));
    }

    @Test
    public void testComments() throws Exception {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getComments();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat(data.getData().get(e1.getId()), nullValue());
        assertThat(data.getData().get(e2.getId()), nullValue());
        assertThat((String) data.getData().get(e3.getId()), is("comment"));
    }

    @Test
    public void testDisposition() throws Exception {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getDisposition();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat(data.getData().get(e1.getId()), nullValue());
        assertThat(data.getData().get(e2.getId()), nullValue());
        assertThat((String) data.getData().get(e3.getId()), is("Transfert dans l’hopital"));
    }

    @Test
    public void testMostRecentZlEmrIdLocation() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getMostRecentZLEmrIdLocation();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String)data.getData().get(e1.getId()), is(MirebalaisLocations.MIREBALAIS_HOSPITAL.name()));
        assertThat((String)data.getData().get(e2.getId()), is(MirebalaisLocations.MIREBALAIS_HOSPITAL.name()));
        assertThat((String) data.getData().get(e3.getId()), is(MirebalaisLocations.MIREBALAIS_HOSPITAL.name()));
    }

    @Test
    public void testUnknownPatient() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getUnknownPatient();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String)data.getData().get(e1.getId()), is("false"));
        assertThat((String)data.getData().get(e2.getId()), is("false"));
        assertThat((String) data.getData().get(e3.getId()), is("false"));
    }

    @Test
    public void testGender() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getGender();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String)data.getData().get(e1.getId()), is("F"));
        assertThat((String)data.getData().get(e2.getId()), is("F"));
        assertThat((String)data.getData().get(e3.getId()), is("F"));
    }

    @Test
    public void testBirthDateYMD() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getBirthDateYMD();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String)data.getData().get(e1.getId()), is("1946-05-26"));
        assertThat((String)data.getData().get(e2.getId()), is("1946-05-26"));
        assertThat((String)data.getData().get(e3.getId()), is("1946-05-26"));
    }

    @Test
    public void testVitalStatusDeathDate() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getVitalStatusDeathDate();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((Timestamp) data.getData().get(e1.getId()), is(new Timestamp(DateUtil.parseDate("2013-12-01", "yyyy-MM-dd").getTime())));
        assertThat((Timestamp) data.getData().get(e2.getId()), is(new Timestamp(DateUtil.parseDate("2013-12-01", "yyyy-MM-dd").getTime())));
        assertThat((Timestamp) data.getData().get(e3.getId()), is(new Timestamp(DateUtil.parseDate("2013-12-01", "yyyy-MM-dd").getTime())));
    }

    @Test
    public void testPreferredAddressDepartment() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getPreferredAddressDepartment();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String)data.getData().get(e1.getId()), is("IN"));
        assertThat((String)data.getData().get(e2.getId()), is("IN"));
        assertThat((String)data.getData().get(e3.getId()), is("IN"));
    }

    @Test
    public void testPreferredAddressCommune() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getPreferredAddressCommune();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String)data.getData().get(e1.getId()), is("Indianapolis"));
        assertThat((String)data.getData().get(e2.getId()), is("Indianapolis"));
        assertThat((String)data.getData().get(e3.getId()), is("Indianapolis"));
    }

    @Test
    public void testPreferredAddressSection() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getPreferredAddressSection();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String)data.getData().get(e1.getId()), is("RBI"));
        assertThat((String)data.getData().get(e2.getId()), is("RBI"));
        assertThat((String)data.getData().get(e3.getId()), is("RBI"));
    }

    @Test
    public void testPreferredAddressLocality() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getPreferredAddressLocality();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String)data.getData().get(e1.getId()), is("1050 Wishard Blvd."));
        assertThat((String)data.getData().get(e2.getId()), is("1050 Wishard Blvd."));
        assertThat((String)data.getData().get(e3.getId()), is("1050 Wishard Blvd."));
    }

    @Test
    public void testPreferredAddressStreetLandmark() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getPreferredAddressStreetLandmark();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String)data.getData().get(e1.getId()), is("RG5"));
        assertThat((String)data.getData().get(e2.getId()), is("RG5"));
        assertThat((String)data.getData().get(e3.getId()), is("RG5"));
    }

    @Test
    public void testTransferOutLocation() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getTransferOutLocation();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat(data.getData().get(e1.getId()), nullValue());
        assertThat(data.getData().get(e2.getId()), nullValue());
        assertThat((String)data.getData().get(e3.getId()), is("Sites non ZL"));
    }

    @Test
    public void testTraumaType() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getTraumaType();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat(data.getData().get(e1.getId()), nullValue());
        assertThat(data.getData().get(e2.getId()), nullValue());
        assertThat((String)data.getData().get(e3.getId()), is("Accident de transport"));
    }

    @Test
    public void testCodedDiagnosis() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getCodedDiagnosis();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat(data.getData().get(e1.getId()), nullValue());
        assertThat(data.getData().get(e2.getId()), nullValue());
        assertThat(data.getData().get(e3.getId()).toString(), is("2"));
    }

    @Test
    public void testNonCodedDiagnosis() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getNonCodedDiagnosis();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat(data.getData().get(e1.getId()), nullValue());
        assertThat(data.getData().get(e2.getId()), nullValue());
		assertThat(data.getData().get(e3.getId()).toString(), is("2"));
    }

    @Test
    public void testEncounterName() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getEncounterTypeName();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String) data.getData().get(e1.getId()), is(EncounterTypes.CHECK_IN.name()));
        assertThat((String) data.getData().get(e2.getId()), is(EncounterTypes.ADMISSION.name()));
        assertThat((String) data.getData().get(e3.getId()), is(EncounterTypes.CONSULTATION.name()));
    }

    @Test
    public void testEncounterLocation() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getLocationName();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String) data.getData().get(e1.getId()), is("Biwo Resepsyon"));
        assertThat((String) data.getData().get(e2.getId()), is("Sal Fanm"));
        assertThat((String) data.getData().get(e3.getId()), is("Sal Fanm"));
    }

    @Test
    public void testSurgicalService() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getSurgicalService();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat(data.getData().get(e1.getId()), nullValue());
        assertThat(data.getData().get(e2.getId()), nullValue());
        assertThat((String) data.getData().get(e3.getId()), is("Chirurgie Vasculaire"));
    }

    @Test
    public void testOtherAssistant() throws EvaluationException {
        context.setBaseEncounters(new EncounterIdSet(surgery.getId()));
        EncounterDataDefinition definition = library.getOtherAssistant();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String) data.getData().get(surgery.getId()), is("Dr. Paul"));
    }

    @Test
    public void testAttending() throws EvaluationException {
        context.setBaseEncounters(new EncounterIdSet(surgery.getId()));
        EncounterDataDefinition definition = library.getAttendingSurgeonName();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String) data.getData().get(surgery.getId()), is("Bob MD"));
    }

    @Test
    public void testCreator() throws EvaluationException {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getCreator();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String) data.getData().get(e1.getId()), is("Paula Morris"));
        assertThat((String) data.getData().get(e2.getId()), is("Paula Morris"));
        assertThat((String) data.getData().get(e3.getId()), is("Paula Morris"));
    }

    @Test
    public void testVisitId() throws Exception {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getVisitId();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((Integer) data.getData().get(e1.getId()), is(v1.getId()));
        assertThat((Integer) data.getData().get(e2.getId()), is(v1.getId()));
        assertThat((Integer) data.getData().get(e3.getId()), is(v1.getId()));
    }

    @Test
    public void testVisitStartDatetime() throws Exception {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getVisitStartDatetime();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((Timestamp) data.getData().get(e1.getId()), is(Timestamp.valueOf("2013-10-02 09:15:00")));
        assertThat((Timestamp) data.getData().get(e2.getId()), is(Timestamp.valueOf("2013-10-02 09:15:00")));
        assertThat((Timestamp) data.getData().get(e3.getId()), is(Timestamp.valueOf("2013-10-02 09:15:00")));
    }

    @Test
    public void testVisitStopDatetime() throws Exception {
        context.setBaseEncounters(encounterIdSet);
        EncounterDataDefinition definition = library.getVisitStopDatetime();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((Timestamp) data.getData().get(e1.getId()), is(Timestamp.valueOf("2013-10-14 04:30:00")));
        assertThat((Timestamp) data.getData().get(e2.getId()), is(Timestamp.valueOf("2013-10-14 04:30:00")));
        assertThat((Timestamp) data.getData().get(e3.getId()), is(Timestamp.valueOf("2013-10-14 04:30:00")));
    }

}
