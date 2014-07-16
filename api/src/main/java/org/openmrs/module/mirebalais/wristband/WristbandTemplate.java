package org.openmrs.module.mirebalais.wristband;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Template for the wristbands we print in Mirebalais; created the code (in ZPL II language) to send to the wristband printer
 */
@Component
public class WristbandTemplate {

    private static DateFormat df  = new SimpleDateFormat("dd MMM yyyy");

    @Autowired
    private AdtService adtService;

    @Autowired
    private MessageSourceService messageSourceService;

    @Autowired
    private PaperRecordProperties paperRecordProperties;

    @Autowired
    private EmrApiProperties emrApiProperties;

    public String generateWristband(Patient patient, Location location) {

        StringBuffer data = new StringBuffer();

        data.append("^XA");
        data.append("^CI28");   // specify Unicode encoding
        data.append("^MTD");    // set direct transfer type
        data.append("^FWB");    // set orientation

        // visit location & current data
        data.append("^FO050,200^FB2150,1,0,L,0^AS^FD" + adtService.getLocationThatSupportsVisits(location).getName() + " "
                + df.format(new Date()) + "^FS");

        // patient name: for now, only printing given and family names
        String patientName = null;

        if(patient.getPersonName() != null ){
            patientName = (patient.getPersonName().getGivenName() != null ? patient.getPersonName().getGivenName() : "") + " "
                    + (patient.getPersonName().getFamilyName() != null ? patient.getPersonName().getFamilyName() : "");
        }

        data.append("^FO100,200^FB2150,1,0,L,0^AU^FD" + patientName + "^FS");

        // birthdate. gender, and patient identifier
        data.append("^FO160,200^FB2150,1,0,L,0^AU^FD" + df.format(patient.getBirthdate()) +  "  "
                + messageSourceService.getMessage("coreapps.gender." + patient.getGender()) + "  ");
        PatientIdentifier paperRecordIdentifier = patient.getPatientIdentifier(paperRecordProperties.getPaperRecordIdentifierType());
        if (paperRecordIdentifier != null) {
            data.append(paperRecordIdentifier.getIdentifier());
        }
        data.append("^FS");

        // address
        PersonAddress address = patient.getPersonAddress();

        if (address != null) {

            // address field
            if (StringUtils.isNotBlank(address.getAddress2())) {
                data.append("^FO220,200^FB2150,1,0,L,0^AS^FD" + address.getAddress2() + "^FS");
            }

            // rest of address
            // coded directly to the hierarchy we are using, for better or worse
            // could be changed to be based on address template, as the paper record template (or at least against the address hierarchy)
            String addressStr = (StringUtils.isNotBlank(address.getAddress1()) ? address.getAddress1() + ", " : "")
                    + (StringUtils.isNotBlank(address.getAddress3()) ? address.getAddress3() + ", " : "")
                    + (StringUtils.isNotBlank(address.getCityVillage()) ? address.getCityVillage() + ", " : "")
                    + address.getStateProvince();

            data.append("^FO270,200^FB2150,1,0,L,0^AS^FD" + addressStr + "^FS");
        }

        // barcode with primary identifier
        PatientIdentifier primaryIdentifier = patient.getPatientIdentifier(emrApiProperties.getPrimaryIdentifierType());
        if (primaryIdentifier != null) {
            data.append("^FO100,2400^AT^BY4^BC,150,N^FD" + primaryIdentifier.getIdentifier() + "^XZ");
        }

        return data.toString();
    }

    /**
     * Setters are just here to insert mocks during testing
     */
    protected void setAdtService(AdtService adtService) {
        this.adtService = adtService;
    }

    protected void setMessageSourceService(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

    protected void setPaperRecordProperties(PaperRecordProperties paperRecordProperties) {
        this.paperRecordProperties = paperRecordProperties;
    }

    protected void setEmrApiProperties(EmrApiProperties emrApiProperties) {
        this.emrApiProperties = emrApiProperties;
    }
}
