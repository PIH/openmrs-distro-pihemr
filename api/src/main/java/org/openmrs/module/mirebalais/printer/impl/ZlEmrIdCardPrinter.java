package org.openmrs.module.mirebalais.printer.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.paperrecord.PaperRecordService;
import org.openmrs.module.pihcore.metadata.core.LocationAttributeTypes;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;
import org.openmrs.module.pihcore.metadata.haiti.HaitiPatientIdentifierTypes;
import org.openmrs.module.printer.Printer;
import org.openmrs.module.printer.PrinterService;
import org.openmrs.module.printer.PrinterType;
import org.openmrs.module.printer.UnableToPrintException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates the functionality needed to print a ZL EMR ID Card
 */
@Component
public class ZlEmrIdCardPrinter {

    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    PrinterService printerService;

    @Autowired
    PaperRecordService paperRecordService;

    @Autowired
    MessageSourceService messageSourceService;

    /**
     * @return true if ZL ID Card printing is possible at the given location
     */
    public boolean isAvailableAtLocation(Location location) {

        // First make sure that the passed location is associated with a medical record location
        try {
            paperRecordService.getMedicalRecordLocationAssociatedWith(location);
        }
        catch (Exception e) {
            return false;
        }

        // Then confirm that a printer is found at this location that is suitable
        Printer printer = printerService.getDefaultPrinter(location, PrinterType.ID_CARD);
        return printer != null;
    }

    /**
     * Prints a ZL EMR ID Card for the given patient at the given location
     */
    public void print(Patient patient, Location location) throws UnableToPrintException {

        Location issuingLocation;
        try {
            issuingLocation = paperRecordService.getMedicalRecordLocationAssociatedWith(location);
        }
        catch (Exception e) {
            throw new UnableToPrintException(getMessage("zl.registration.patient.idcard.locationNotAssociatedWithMedicalRecordLocation", location.getName()));
        }

        Printer printer = printerService.getDefaultPrinter(location, PrinterType.ID_CARD);

        if (printer == null) {
            throw new UnableToPrintException(getMessage("zl.registration.patient.idcard.noPrinterConfiguredForLocation", location.getName()));
        }

        DateFormat df = new SimpleDateFormat("dd MMM yyyy", Context.getLocale());

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("name", getName(patient));
        paramMap.put("patientIdentifier", getIdentifier(patient));
        paramMap.put("gender", patient.getGender());
        paramMap.put("birthdate", df.format(patient.getBirthdate()));
        paramMap.put("birthdateEstimated", patient.getBirthdateEstimated());
        paramMap.put("issuingLocation", getIssuingLocationName(issuingLocation));
        paramMap.put("issuedDate", df.format(new Date()));
        paramMap.put("telephoneNumber", getTelephoneNumber(patient));
        paramMap.put("customCardLabel", "Zanmi Lasante Patient ID Card");
        paramMap.put("addressLines", getAddressLines(patient));

        printerService.print(paramMap, printer, true);
    }

    /**
     * @return the person name in the format that it should be displayed on the id cards
     */
    protected String getName(Patient patient) {
        PersonName pn = patient.getPersonName();
        return StringUtils.defaultIfEmpty(pn.getFamilyName(), "") + ", " + StringUtils.defaultIfEmpty(pn.getGivenName(), "") + " ";
    }

    /**
     * @return the patient identifier in the format that it should be displayed on the id cards
     */
    protected String getIdentifier(Patient patient) {
        PatientIdentifierType idType = MetadataUtils.existing(PatientIdentifierType.class, HaitiPatientIdentifierTypes.ZL_EMR_ID.uuid());
        PatientIdentifier pi = patient.getPatientIdentifier(idType);
        if (pi == null || pi.isVoided()) {
            pi = patient.getPatientIdentifier();
        }
        return pi == null ? "" : pi.getIdentifier();
    }

    /**
     * @return the telephone number for the patient in the  format that it should be displayed on the id cards
     */
    protected String getTelephoneNumber(Patient patient) {
        String phoneNumber = "";
        PersonAttributeType type = MetadataUtils.existing(PersonAttributeType.class, PersonAttributeTypes.TELEPHONE_NUMBER.uuid());
        PersonAttribute attr = patient.getAttribute(type);
        if (attr != null) {
            phoneNumber = StringUtils.defaultIfEmpty(attr.getValue(), "");
        }
        return phoneNumber;
    }

    /**
     * @return the address lines in the format that it should be displayed on the id cards
     */
    protected List<String> getAddressLines(Patient patient) {

        PersonAddress address = patient.getPersonAddress();

        if (address == null) {
            return null;
        }

        List<String> addressLines = new ArrayList<String>();

        if (AddressSupport.getInstance().getDefaultLayoutTemplate() != null && AddressSupport.getInstance().getDefaultLayoutTemplate().getLines() != null) {

            for (List<Map<String, String>> line : AddressSupport.getInstance().getDefaultLayoutTemplate().getLines()) {

                // now iterate through all the tokens in the line and build the string to print
                StringBuilder output = new StringBuilder();
                for (Map<String,String> token : line) {
                    // find all the tokens on this line, and then add them to that output line
                    if(token.get("isToken").equals(AddressSupport.getInstance().getDefaultLayoutTemplate().getLayoutToken())) {
                        try {
                            String propertyValue = (String)PropertyUtils.getProperty(address, token.get("codeName"));
                            if (StringUtils.isNotBlank(propertyValue)) {
                                if (output.length() > 0) {
                                    output.append(", ");
                                }
                                output.append(propertyValue);
                            }
                        }
                        catch (Exception e) {
                            log.error("Invalid address property of " + token.get("codeName") + " configured in address layout template", e);
                        }
                    }
                }
                if (StringUtils.isNotBlank(output.toString())) {
                    addressLines.add(output.toString());
                }
            }
        }
        else {
            log.error("Address template not properly configured");
        }

        return addressLines;
    }

    /**
     * @return the issuing location in the format that it should be displayed on the id cards
     */
    protected String getIssuingLocationName(Location location) {

        LocationAttributeType attributeType = MetadataUtils.existing(LocationAttributeType.class, LocationAttributeTypes.NAME_TO_PRINT_ON_ID_CARD.uuid());
        List<LocationAttribute> nameToPrintOnIdCard = location.getActiveAttributes(attributeType);

        // there should never be more for than one specified name to print on the id card--max allowed for this attribute = 1
        if (nameToPrintOnIdCard != null && nameToPrintOnIdCard.size() > 0) {
            return (String) nameToPrintOnIdCard.get(0).getValue();
        }
        else {
            return location.getDisplayString();
        }
    }

    protected String getMessage(String key, String...params) {
        return messageSourceService.getMessage(key, params, Context.getLocale());
    }
}
