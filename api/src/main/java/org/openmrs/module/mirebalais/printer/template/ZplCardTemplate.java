package org.openmrs.module.mirebalais.printer.template;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component("zplCardTemplate")
public class ZplCardTemplate {

    private final Log log = LogFactory.getLog(getClass());

    private static int LARGER_FONT_NAME_MAX_SIZE = 26;

    private static int SMALLER_FONT_NAME_MAX_SIZE = 35;

    // TODO make this dynamic if we start supporting other countries\
    private static Locale locale = new Locale("ht");

    @Autowired
    protected MessageSourceService messageSourceService;

    @Autowired
    protected EmrApiProperties emrApiProperties;

    public String generateLabel(Map<String, Object> paramMap) {

        String name = (String) paramMap.get("name");
        String gender = (String) paramMap.get("gender");
        String birthdate = (String) paramMap.get("birthdate");
        Boolean birthdateEstimated = (Boolean) paramMap.get("birthdateEstimated");
        String patientIdentifier = (String) paramMap.get("patientIdentifier");
        List<String> addressLines = (paramMap.containsKey("addressLines") ? (List<String>) paramMap.get("addressLines") : null);
        String telephoneNumber =  (paramMap.containsKey("telephoneNumber") ? (String) paramMap.get("telephoneNumber") : null);
        String issuingLocation = (paramMap.containsKey("issuingLocation") ? (String) paramMap.get("issuingLocation") : null);
        String issuedDate = (String) paramMap.get("issuedDate");
        String customCardLabel = (paramMap.containsKey("customCardLabel") ? (String) paramMap.get("customCardLabel") : null);

        // build the command to send to the printer -- written in ZPL
        StringBuilder data = new StringBuilder();
        data.append("^XA");
        data.append("^CI28");   // specify Unicode encoding
        data.append("^PW1300");  // set print width
        data.append("^MTT");   // set thermal transfer type

        /* Name  */
        String fontHeight = "V"; //large font

        if (StringUtils.isNotBlank(name)) {
            if (name.length() > LARGER_FONT_NAME_MAX_SIZE){
                fontHeight ="U"; //smaller font
            }
            if (name.length() > SMALLER_FONT_NAME_MAX_SIZE){
                name = StringUtils.substring(name,  0, SMALLER_FONT_NAME_MAX_SIZE);
            }
            data.append("^FO220,20^A" + fontHeight + "N^FD" + name + "^FS");
        }

        /* Divider line */
        data.append("^FO220,100^GB850,0,5^FS");

        /* Address (using address template) */
        int verticalPosition = 120;
        if (addressLines != null && addressLines.size() > 0) {
            for (String addressLine : addressLines) {
                data.append("^FO220," + verticalPosition + "^ATN^FD" + addressLine + "^FS");
                verticalPosition = verticalPosition + 50;
            }
        }

        /* Gender */
        data.append("^FO220,325^ASN^FD" + messageSourceService.getMessage("coreapps.gender", null, locale) + "^FS");
        data.append("^FO220,365^ATN^FD");
        if (StringUtils.isNotBlank(gender)) {
            data.append(messageSourceService.getMessage("coreapps.gender." + gender, null, locale));
        }
        data.append("^FS");

        /* Birthdate */
        data.append("^FO400,325^ASN^FD" + (birthdateEstimated ?
                messageSourceService.getMessage("pihcore.birthdate_estimated", null, locale) :
                messageSourceService.getMessage("pihcore.birthdate", null, locale)) + "^FS");
        data.append("^FO400,365^ATN^FD");
        if (StringUtils.isNotBlank(birthdate)) {
            data.append(birthdate);
        }
        data.append("^FS");

        /* Telephone */
        data.append("^FO740,325^ASN^FD" + messageSourceService.getMessage("ui.i18n.PersonAttributeType.name." + PersonAttributeTypes.TELEPHONE_NUMBER.uuid(), null, locale) + "^FS");
        data.append("^FO740,365^ATN^FD");
        if (StringUtils.isNotBlank(telephoneNumber)) {
            data.append(telephoneNumber);
        }
        data.append("^FS");

        /* Divider line */
        data.append("^FO220,420^GB850,0,5^FS");

        /* Print the bar code, based on the identifier */
        data.append("^FO220,450^ATN^BY2,2,10^BCN,100,Y^FD" + patientIdentifier + "^FS");    // print barcode & identifier

        /* Custom label */
        data.append("^FO500,440^ATN^FD" + customCardLabel + "^FS");

        /* Date and location of issue */
        data.append("^FO500,500^ASN^FD" + messageSourceService.getMessage("pihcore.idcard.issuedOn", null, locale) + "^FS");
        data.append("^FO500,540^ATN^FD");
        data.append(issuedDate);
        data.append("^FS");
        data.append("^FO840,500^ASN^FD" +  messageSourceService.getMessage("pihcore.idcard.issuedAt", null, locale) + "^FS");
        data.append("^FO840,540^ATN^FD");
        data.append(issuingLocation);
        data.append("^FS");

        /* Print command */
        data.append("^XZ");

        return data.toString();
    }

    public String getEncoding() {
        return "UTF-8";
    }
}
