package org.openmrs.module.mirebalais.wristband;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyUtil;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Template for the wristbands we print in Mirebalais; created the code (in ZPL II language) to send to the wristband printer
 */
@Component
public class WristbandTemplate {

    // we always print in French
    private static Locale locale = new Locale("fr");

    public static Boolean LOWEST_LEVEL_ON_SEPARATE_LINE = true;

    public static Boolean SKIP_HIGHEST_LEVEL = true;

    private static DateFormat fullDate  = new SimpleDateFormat("dd MMM yyyy", locale);

    private static DateFormat yearOnly = new SimpleDateFormat("yyyy", locale);

    @Autowired
    private AdtService adtService;

    @Autowired
    private MessageSourceService messageSourceService;

    @Autowired
    private PaperRecordProperties paperRecordProperties;

    @Autowired
    private EmrApiProperties emrApiProperties;

    // TODO figure out why this isn't getting autowired properly (at least for tests)
    //@Autowired
    private AddressHierarchyService addressHierarchyService;


    public String generateWristband(Patient patient, Location location) {

        // TODO figure out why this isn't getting autowired properly (at least for tests)
        if (addressHierarchyService == null) {
            addressHierarchyService = Context.getService(AddressHierarchyService.class);
        }

        StringBuffer data = new StringBuffer();

        data.append("^XA");
        data.append("^CI28");   // specify Unicode encoding
        data.append("^MTD");    // set direct transfer type
        data.append("^FWB");    // set orientation

        // visit location & current data
        data.append("^FO050,200^FB2150,1,0,L,0^AS^FD" + adtService.getLocationThatSupportsVisits(location).getName() + " "
                + fullDate.format(new Date()) + "^FS");

        // patient name: for now, only printing given and family names
        String patientName = null;

        if(patient.getPersonName() != null ){
            patientName = (patient.getPersonName().getGivenName() != null ? patient.getPersonName().getGivenName() : "") + " "
                    + (patient.getPersonName().getFamilyName() != null ? patient.getPersonName().getFamilyName() : "");
        }

        data.append("^FO100,200^FB2150,1,0,L,0^AU^FD" + patientName + "^FS");

        // birthdate (we only show year if birthdate is estiamted
        DateFormat df = patient.getBirthdateEstimated() ? yearOnly : fullDate;
        data.append("^FO160,200^FB2150,1,0,L,0^AU^FD" + df.format(patient.getBirthdate()) +  "^FS");

        // age
        data.append("^FO160,200^FB1850,1,0,L,0^AT^FD" + messageSourceService.getMessage("coreapps.ageYears", Collections.singletonList(patient.getAge()).toArray(), locale) +"^FS");

        // gender
        data.append("^FO160,200^FB1650,1,0,L,0^AU^FD" + messageSourceService.getMessage("coreapps.gender." + patient.getGender(), null, locale) + "  ");

        // paper record identifiers
        PatientIdentifier paperRecordIdentifier = patient.getPatientIdentifier(paperRecordProperties.getPaperRecordIdentifierType());
        if (paperRecordIdentifier != null) {
            data.append(paperRecordIdentifier.getIdentifier());
        }
        data.append("^FS");

        // address (based on address hierarchy)
        PersonAddress address = patient.getPersonAddress();
        AddressHierarchyLevel level = addressHierarchyService.getBottomAddressHierarchyLevel();
        int numberOfLevels = addressHierarchyService.getAddressHierarchyLevelsCount();

        if (address != null && numberOfLevels > 0) {

            int levelCount = 1;

            if (LOWEST_LEVEL_ON_SEPARATE_LINE) {
                String lowestLevelStr = AddressHierarchyUtil.getAddressFieldValue(address, level.getAddressField());
                if (StringUtils.isNotBlank(address.getAddress2())) {
                    data.append("^FO220,200^FB2150,1,0,L,0^AS^FD" + lowestLevelStr + "^FS");
                }
                levelCount++;
            }

            StringBuffer addressStr = new StringBuffer();

            while (levelCount < numberOfLevels || (!SKIP_HIGHEST_LEVEL && levelCount <= numberOfLevels)
                    && level.getParent() != null) {  // level.getParent() should never equal null as long as levelCount <= numberOfLevels, but just to be safe we will check

                level = level.getParent();

                String levelStr = AddressHierarchyUtil.getAddressFieldValue(address, level.getAddressField());

                if (StringUtils.isNotBlank(levelStr)) {
                    addressStr.append(levelStr + ", ");
                }

                levelCount++;
            }

            if (StringUtils.isNotBlank(addressStr.toString())) {
                // trim off trailing comma and space
                addressStr.delete(addressStr.length() - 2, addressStr.length());
                data.append("^FO270,200^FB2150,1,0,L,0^AS^FD" + addressStr.toString() + "^FS");
            }
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

    public void setAddressHierarchyService(AddressHierarchyService addressHierarchyService) {
        this.addressHierarchyService = addressHierarchyService;
    }
}
