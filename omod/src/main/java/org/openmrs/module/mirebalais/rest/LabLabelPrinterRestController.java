package org.openmrs.module.mirebalais.rest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/v1/mirebalais")
public class LabLabelPrinterRestController {

    public static final String LAB_LABEL_PRINTER = "/lablabelprinter";

    @Autowired
    PatientService patientService;

    @Autowired
    LocationService locationService;

    @RequestMapping(method = RequestMethod.GET, value = LAB_LABEL_PRINTER)
    @ResponseBody
    public Object printLabel(
            @RequestParam(value="patient") String patientUuid,
            @RequestParam(value="sessionLocation") String sessionLocationUuid) {

        SimpleObject response = new SimpleObject();

        if ( StringUtils.isNotBlank(patientUuid) ) {
            Patient patient = patientService.getPatientByUuid(patientUuid);
            Location location = locationService.getLocationByUuid(sessionLocationUuid);
            if ( patient != null && location != null){
                response = (SimpleObject) ConversionUtil.convertToRepresentation(patient, Representation.DEFAULT);
            }
        }
        return response;
    }

}
