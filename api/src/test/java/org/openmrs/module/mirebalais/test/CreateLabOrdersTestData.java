package org.openmrs.module.mirebalais.test;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

/**
 * Creates a configurable large set of Test Orders for testing the labworkflow app. This test could be run from the command line:
 * mvn test -DfailIfNoTests=false -Dtest=CreateLabOrdersTestData -DnumberOfPatients=5 -DnumberOfOrders=20 -DnumberOfDays=2
 * - numberOfPatients = number of test patients to be created
 * - numberOfOrders = number of Test Orders to be created
 * - numberOfDays = spread the number of orders per number of days back from current date
 */

@SkipBaseSetup
public class CreateLabOrdersTestData extends BaseModuleContextSensitiveTest {

    public static final String TEST_ORDER_ENCOUNTER_TYPE = EncounterTypes.TEST_ORDER.uuid();
    public static final String MAIN_LABORATORY_LOCATION = MirebalaisLocations.MAIN_LABORATORY.uuid();
    public static final String[] CONCEPTS_UUIDS = {
            "d9520d86-6042-4b82-a324-2abf3d4fdfa5", //Complete hematogram
            "1b169fff-a6e9-4f96-8c07-de33ec8aafcf", //Manual hematogram
            "4e76e0f3-3667-4de9-94d5-4a1227bd6c04", //Sickle cell panel
            "163436AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", //Coagulation profile
            "3ccc7158-26fe-102b-80cb-0017a47871b2", //Hemoglobin
            "3ceda710-26fe-102b-80cb-0017a47871b2", //CD4 count
            "3cd6c946-26fe-102b-80cb-0017a47871b2" //HIV rapid
    };

    public Concept[] TEST_ORDER_CONCEPTS = null;
    public Provider DEFAULT_PROVIDER = null;
    public CareSetting INPATIENT_CARE_SETTING = null;
    public Random generator = new Random();

    @Autowired
    protected TestDataManager testDataManager;

    @Autowired
    protected PatientService patientService;

    @Autowired
    protected EncounterService encounterService;

    @Autowired
    protected ConceptService conceptService;

    @Autowired
    protected OrderService orderService;

    @Autowired
    protected ProviderService providerService;

    @Override
    public Boolean useInMemoryDatabase() {
        return false;
    }

    @Override
    public Properties getRuntimeProperties() {
        return TestUtil.getRuntimeProperties(this.getWebappName());
    }

    @Override
    public void deleteAllData() {
        return;
    }

    @Before
    public void setup() throws Exception {
        authenticate();
        DEFAULT_PROVIDER = providerService.getProvider(1);
        INPATIENT_CARE_SETTING = orderService.getCareSettingByUuid("c365e560-c3ec-11e3-9c1a-0800200c9a66");
        ArrayList<Concept> concepts = new ArrayList<Concept>();
        for (int i = 0; i < CONCEPTS_UUIDS.length; i++) {
            concepts.add(conceptService.getConceptByUuid(CONCEPTS_UUIDS[i]));
        }
        TEST_ORDER_CONCEPTS = new Concept[concepts.size()];
        TEST_ORDER_CONCEPTS = concepts.toArray(TEST_ORDER_CONCEPTS);

    }

    protected List<Patient> createPatients(int numberOfPatients) throws SQLException {
        List<Patient> patients = new ArrayList<Patient>();
        if ( numberOfPatients > 0 ) {
            for (int i = 0; i < numberOfPatients; i++) {
                Patient patient = testDataManager.randomPatient().save();
                patientService.savePatient(patient);
                patients.add(patient);
            }
        }
        return patients;
    }

    protected TestOrder createTestOrder(Patient patient, Date dateCreated) throws NoSuchFieldException, IllegalAccessException {

        int randomConceptIndex = generator.nextInt(TEST_ORDER_CONCEPTS.length);
        TestOrder order = new TestOrder();
        order.setConcept(TEST_ORDER_CONCEPTS[randomConceptIndex]);
        order.setPatient(patient);
        order.setDateActivated(dateCreated);
        order.setUrgency(Order.Urgency.ROUTINE);
        order.setOrderer(DEFAULT_PROVIDER);
        order.setDateCreated(dateCreated);
        order.setCareSetting(INPATIENT_CARE_SETTING);

        return order;
    }

    protected void createLabOrders(List<Patient> patients, int numberOfOrders, int numberOfDays) throws NoSuchFieldException, IllegalAccessException, SQLException {

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
        Date startDate = new Date(today.getTime() - numberOfDays * 24 * 3600 * 1000L ); //Subtract numberOfDays days

        int numberOfPatients = patients.size();
        int ordersPerPatient = numberOfOrders / numberOfPatients;
        int ordersPerDay = ordersPerPatient / numberOfDays;

        for (int day = 1; day < numberOfDays + 1; day++) {
            Date encounterDate = new Date(today.getTime() - day * 24 * 3600 * 1000L ); //Subtract numberOfDays days
            for (Patient patient : patients) {
                Encounter encounter = testDataManager.encounter().patient(patient).encounterType(TEST_ORDER_ENCOUNTER_TYPE).location(MAIN_LABORATORY_LOCATION).encounterDatetime(encounterDate).save();
                for (int j = 0; j < ordersPerDay; j++) {
                    encounter.addOrder(createTestOrder(patient, encounterDate));
                }
                encounterService.saveEncounter(encounter);
                getConnection().commit();
            }
        }

    }

    @Test
    @Transactional
    @Rollback(false)
    public void createLargeDataSet() throws Exception {

        String numberOfPatients = System.getProperty("numberOfPatients");
        String numberOfOrders = System.getProperty("numberOfOrders");
        String numberOfDays = System.getProperty("numberOfDays");
        if ( StringUtils.isNotBlank(numberOfPatients) && StringUtils.isNotBlank(numberOfOrders) && StringUtils.isNotBlank(numberOfDays) ) {
            System.out.println("Creating " + numberOfPatients + " test patients with " + numberOfOrders + " test orders within the last " + numberOfDays + " days from today");
            Assert.assertNotNull(numberOfPatients);
            List<Patient> patientList = createPatients(Integer.parseInt(numberOfPatients));
            Assert.assertEquals(patientList.size(), Integer.parseInt(numberOfPatients));
            createLabOrders(patientList, Integer.parseInt(numberOfOrders), Integer.parseInt(numberOfDays));
        } else {
            Assert.assertNull(numberOfPatients);
        }
    }
}
