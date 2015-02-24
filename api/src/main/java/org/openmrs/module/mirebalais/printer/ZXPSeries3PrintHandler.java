package org.openmrs.module.mirebalais.printer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.printer.Printer;
import org.openmrs.module.printer.UnableToPrintException;
import org.openmrs.module.printer.handler.PrintHandler;

import java.util.List;
import java.util.Map;

public class ZXPSeries3PrintHandler implements PrintHandler {

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public String getDisplayName() {
        return "ZXP Series 3 Print Handler";
    }

    @Override
    public String getBeanName() {
        return "zxpSeries3PrintHandler";
    }

    @Override
    public void print(Printer printer, Map<String, Object> paramMap) throws UnableToPrintException {

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


    }
}
