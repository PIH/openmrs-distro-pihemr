package org.openmrs.module.mirebalais.rest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.mirebalais.printer.template.ZplLabLabelTemplate;
import org.openmrs.module.printer.Printer;
import org.openmrs.module.printer.PrinterService;
import org.openmrs.module.printer.PrinterType;
import org.openmrs.module.printer.UnableToPrintException;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/rest/v1/mirebalais")
public class LabLabelPrinterRestController {

    public static final String LAB_LABEL_PRINTER = "/lablabelprinter";

    @Autowired
    PatientService patientService;

    @Autowired
    LocationService locationService;

    @Autowired
    PrinterService printerService;

    @Autowired
    MessageSourceService messageSourceService;

    @RequestMapping(method = RequestMethod.GET, value = LAB_LABEL_PRINTER)
    @ResponseBody
    public Object printLabel(
            @RequestParam(value="patient") String patientUuid,
            @RequestParam(value="sessionLocation") String sessionLocationUuid) {

        SimpleObject response = new SimpleObject();

        if ( StringUtils.isNotBlank(patientUuid) ) {

            Patient patient = patientService.getPatientByUuid(patientUuid);
            Location location = locationService.getLocationByUuid(sessionLocationUuid);

            if (patient != null && location != null){

                Printer printer = printerService.getDefaultPrinter(location, PrinterType.LABEL);

                if (printer != null) {
                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    ZplLabLabelTemplate zplLabLabelTemplate = Context.getRegisteredComponent("zplLabLabelTemplate", ZplLabLabelTemplate.class);
                    paramMap.put("data", zplLabLabelTemplate.generateLabel(patient));
                    paramMap.put("encoding", zplLabLabelTemplate.getEncoding());
                    paramMap.put("wait", 500);
                    try {
                        printerService.print(paramMap, printer, false);
                        response = (SimpleObject) ConversionUtil.convertToRepresentation(patient, Representation.DEFAULT);
                    }
                    catch (UnableToPrintException e) {
                        response.put("error", true);
                        response.put("message", messageSourceService.getMessage("mirebalais.error.unableToContactPrinter"));
                    }
                }
                else {
                    response.put("error", true);
                    response.put("message", messageSourceService.getMessage("mirebalais.error.noLabelPrinterConfiguredforLocation"));
                }
            }
        }
        return response;
    }

}
