package org.openmrs.module.mirebalais.printer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.printer.Printer;
import org.openmrs.module.printer.UnableToPrintViaSocketException;
import org.openmrs.module.printer.handler.PrintHandler;
import org.openmrs.module.printer.handler.SocketPrintHandler;

import java.util.List;
import java.util.Map;

public class P110iPrintHandler extends SocketPrintHandler implements PrintHandler {

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public String getDisplayName() {
        return "P110i Print Handler";
    }

    @Override
    public String getBeanName() {
        return "p110iPrintHandler";
    }

    /**
     * Should include the following parameters in the param map.
     * All parameters are string, except where noted
     *
     * name
     * gender (M or F)
     * birthdate
     * birthdateEstimated (boolean)
     * addressLines (optional)
     * patientIdentifier
     * issuingLocation (optional)
     * issuedDate
     * telephoneNumber (optional)
     * customCardLabel (optional)
     *
     * @throws UnableToPrintViaSocketException
     */
    @Override
    public void print(Printer printer, Map<String, Object> paramMap) throws UnableToPrintViaSocketException
    {
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

        //normal font height for short names
        String fontHeight = "75";
        if(name.length() > 28){
            //use shorter fonts for longer names
            fontHeight = "50";
        }
        if(name.length() > 41){
            //if the patient name is very long then truncate it
            name = StringUtils.substring(name, 0, 40);
        }

        // build the command to send to the printer -- written in EPCL
        String ESC = "\u001B";

        StringBuilder data = new StringBuilder();

        data.append(ESC + "+RIB\n");   // specify monochrome ribbon type
        data.append(ESC + "+C 4\n");   // specify thermal intensity
        data.append(ESC + "F\n");	   // clear monochrome buffer

        data.append(ESC + "B 75 550 0 0 0 3 100 0 "+ patientIdentifier + "\n");    // layout bar code and patient identifier
        data.append(ESC + "T 75 600 0 1 0 45 1 "+ patientIdentifier + "\n");

        data.append(ESC + "T 75 80 0 1 0 " + fontHeight + " 1 "+ name + "\n");
        data.append(ESC + "L 20 90 955 5 1\n");   //divider line

        data.append(ESC + "T 75 350 0 0 0 25 1 Sèks\n");
        data.append(ESC + "T 75 400 0 1 0 50 1 " + (gender.equals("F") ? "Fi" : "Gason") + "\n");

        data.append(ESC + "T 250 350 0 0 0 25 1 Dat ou fèt " + (birthdateEstimated != null && birthdateEstimated ? " (Estime)" : " ") + "\n");
        data.append(ESC + "T 250 400 0 1 0 50 1 " + birthdate + "\n");

        int verticalPosition = 150;
        if (addressLines != null && addressLines.size() >0 ) {
            for (String addressLine : addressLines) {
                data.append(ESC + "T 75 " + verticalPosition + " 0 1 0 50 1 " + addressLine + "\n");
                verticalPosition = verticalPosition + 50;
            }
        }

        if (StringUtils.isNotBlank(telephoneNumber)) {
            data.append(ESC + "T 600 350 0 0 0 25 1 Nimewo Telefòn\n");
            data.append(ESC + "T 600 400 0 1 0 50 1 " + telephoneNumber + "\n");
        }

        // custom card label, if specified
        if (StringUtils.isNotBlank(customCardLabel)) {
            data.append(ESC + "T 420 510 0 1 0 45 1 " + customCardLabel + "\n");
        }

        // date issued and location issued are aligned to bottom of the card
        data.append(ESC + "T 420 550 0 0 0 25 1 Dat kat la fet la\n");
        data.append(ESC + "T 420 600 0 1 0 50 1 " + issuedDate + "\n");    // date issued is today

        data.append(ESC + "T 720 550 0 0 0 25 1 Kote kat la fet la\n");
        data.append(ESC + "T 720 600 0 1 0 50 1 " + (StringUtils.isNotBlank(issuingLocation) ? issuingLocation : "") + "\n");

        data.append(ESC + "L 20 420 955 5 1\n");   //divider line

        data.append(ESC + "I\n");		// trigger the actual print job

        paramMap.put("data", data.toString());
        paramMap.put("wait", 8000);
        paramMap.put("encoding", "Windows-1252");

        // call the socket print handler functionality
        super.print(printer, paramMap);
    }


}
