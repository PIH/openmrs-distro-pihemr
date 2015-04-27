package org.openmrs.module.mirebalais.radiology;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.TestOrder;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderContext;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.radiologyapp.RadiologyOrder;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.hamcrest.core.Is.is;;
import static org.junit.Assert.assertThat;

public class RadiologyOrderNumberGeneratorComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ConceptService conceptService;

    @Before
    public void setup() throws Exception {
        executeDataSet("radiologyOrderNumberGeneratorTestDataset.xml");
    }

    @Test
    public void shouldGenerateStandardOrderNumber() {

        // new test order
        Order order = new TestOrder();
        order.setEncounter(encounterService.getEncounter(6));
        order.setPatient(patientService.getPatient(7));
        order.setConcept(conceptService.getConcept(5497));
        order.setOrderer(providerService.getProvider(1));
        order.setCareSetting(orderService.getCareSetting(1));
        order.setOrderType(orderService.getOrderType(2));
        order.setEncounter(encounterService.getEncounter(3));
        order.setDateActivated(new Date());
        OrderContext orderContext = new OrderContext();
        order = orderService.saveOrder(order, orderContext);

        assertThat(order.getOrderNumber(), is("ORD-10"));
    }

    @Test
    public void shouldGenerateRadiologyOrderNumber() {

        // new test order
        Order order = new RadiologyOrder();
        order.setEncounter(encounterService.getEncounter(6));
        order.setPatient(patientService.getPatient(7));
        order.setConcept(conceptService.getConcept(5497));
        order.setOrderer(providerService.getProvider(1));
        order.setCareSetting(orderService.getCareSetting(1));
        order.setOrderType(orderService.getOrderType(1001));
        order.setEncounter(encounterService.getEncounter(3));
        order.setDateActivated(new Date());
        OrderContext orderContext = new OrderContext();
        orderContext.setOrderType(orderService.getOrderType(1001));
        order = orderService.saveOrder(order, orderContext);

        assertThat(order.getOrderNumber(), is("0000000125"));
    }
}
