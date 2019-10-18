package org.openmrs.module.mirebalais.printer;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.mirebalais.printer.template.ZplLabLabelTemplate;
import org.openmrs.module.printer.Printer;
import org.openmrs.module.printer.PrinterServiceImpl;
import org.openmrs.module.printer.handler.SocketPrintHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZplLabLabelTemplateTest {

    private ZplLabLabelTemplate template;

    private PatientIdentifierType primaryIdentifierType;

    @Before
    public void setup() {

        primaryIdentifierType = new PatientIdentifierType();

        template = new ZplLabLabelTemplate();
        EmrApiProperties emrApiProperties = mock(EmrApiProperties.class);
        when(emrApiProperties.getPrimaryIdentifierType()).thenReturn(primaryIdentifierType);
        template.setEmrApiProperties(emrApiProperties);
    }


    @Test
    public void shouldGenerateLabel() {

        Patient patient = new Patient();
        PersonName name = new PersonName();

        name.setGivenName("Mark");
        name.setFamilyName("Goodrich");
        patient.addName(name);

        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifier("YXB123");
        patientIdentifier.setIdentifierType(primaryIdentifierType);
        patient.addIdentifier(patientIdentifier);

        String data = template.generateLabel(patient);

        String expectedDate = new SimpleDateFormat("dd MMM yyyy hh:mm aaa").format(new Date());
        assertThat(data, is("^XA^CI28^PW650^MTT^FO30,20^ARN^FDMark Goodrich ^FS^FO30,50^ATN^BY2,2,50^BCN^FDYXB123^FS^FO300,110^ARN^FD" + expectedDate + "^FS^XZ"));

    }

    @Test
    @Ignore // just used to print against a test printer when one is on the network
    public void shouldPrintToPrinter() throws Exception{

        Patient patient = new Patient();
        PersonName name = new PersonName();

        name.setGivenName("Mark");

        name.setFamilyName("Goodrich");
        patient.addName(name);

        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifier("YXB123");
        patientIdentifier.setIdentifierType(primaryIdentifierType);
        patient.addIdentifier(patientIdentifier);

        String data = template.generateLabel(patient);

        Printer printer = new Printer();
        printer.setIpAddress("10.10.216.207");
        printer.setPort("9100");
        printer.setId(1);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("data", data);
        paramMap.put("encoding", "UTF-8");

        new PrinterServiceImpl().print(paramMap, printer, false, new SocketPrintHandler());

    }
}
