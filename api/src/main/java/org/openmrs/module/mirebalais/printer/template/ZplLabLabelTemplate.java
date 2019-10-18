package org.openmrs.module.mirebalais.printer.template;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component("zplLabLabelTemplate")
public class ZplLabLabelTemplate {


    @Autowired
    protected EmrApiProperties emrApiProperties;

    public String generateLabel(Patient patient) {

        // build the command to send to the printer -- written in ZPL
        StringBuilder data = new StringBuilder();
        data.append("^XA");
        data.append("^CI28");   // specify Unicode encoding
        data.append("^PW650");  // set print width
        data.append("^MTT");   // set thermal transfer type

        /* Name  */
        String nameFont = "R";
        String name = getName(patient);
        String patientIdentifier = getIdentifier(patient);
        data.append("^FO30,20^A" + nameFont + "N^FD" + name + "^FS");

        /* Print the bar code and identifier  */
        data.append("^FO30,50^ATN^BY2,2,50^BCN^FD" + patientIdentifier + "^FS");

        /* Timestamp */
        String timestampFont = "R";
        String date = new SimpleDateFormat("dd MMM yyyy hh:mm aaa").format(new Date());
        data.append("^FO300,110^A" + timestampFont + "N^FD" + date + "^FS");

        /* Print command */
        data.append("^XZ");

        return data.toString();
    }

    public String getEncoding() {
        return "UTF-8";
    }

    // TODO (do we want to use template, or just use first name/last name)
    // TODO first name first or second?
    protected String getName(Patient patient) {
        PersonName pn = patient.getPersonName();
        return StringUtils.defaultIfEmpty(pn.getGivenName(), "") + " " + StringUtils.defaultIfEmpty(pn.getFamilyName(), "") + " ";
    }

    protected String getIdentifier(Patient patient) {
        PatientIdentifierType idType = emrApiProperties.getPrimaryIdentifierType();
        PatientIdentifier pi = patient.getPatientIdentifier(idType);
        if (pi == null || pi.isVoided()) {
            pi = patient.getPatientIdentifier();
        }
        return pi == null ? "" : pi.getIdentifier();
    }

    // just so we can mock during testing
    public void setEmrApiProperties(EmrApiProperties emrApiProperties) {
        this.emrApiProperties = emrApiProperties;
    }
}
