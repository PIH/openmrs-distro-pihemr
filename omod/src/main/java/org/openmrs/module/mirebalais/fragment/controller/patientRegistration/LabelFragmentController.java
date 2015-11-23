package org.openmrs.module.mirebalais.fragment.controller.patientRegistration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.paperrecord.UnableToPrintLabelException;
import org.openmrs.module.paperrecord.template.DefaultZplPaperFormLabelTemplate;
import org.openmrs.module.printer.Printer;
import org.openmrs.module.printer.PrinterService;
import org.openmrs.module.printer.PrinterType;
import org.openmrs.module.printer.UnableToPrintViaSocketException;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

// TODO this is only a demo for Sierra Leone ID labe printing--clean up or remove if not used

public class LabelFragmentController {

    private final Log log = LogFactory.getLog(getClass());

    public SimpleObject printLabel(UiUtils ui,
                                     @RequestParam("patientId") Patient patient,
                                     @RequestParam(value = "locationId", required = false) Location location,
                                     @SpringBean("printerService") PrinterService printerService,
                                    @SpringBean("emrApiProperties") EmrApiProperties emrApiProperties,
                                     @SpringBean("messageSourceService") MessageSourceService messageSourceService,
                                     UiSessionContext uiSessionContext) throws UnableToPrintLabelException {
        try {

            if (location == null) {
                location = uiSessionContext.getSessionLocation();
            }

            // TODO hack, we should wire in the DefaultZlpPaperFormLabel bean here instead
            DefaultZplPaperFormLabelTemplate template = new DefaultZplPaperFormLabelTemplate();
            template.setEmrApiProperties(emrApiProperties);
            template.setMessageSourceService(messageSourceService);
            String data = template.generateLabel(patient, null);
            Printer printer = printerService.getDefaultPrinter(location, PrinterType.LABEL);

            if (printer == null) {
                // TODO: better warning if no default printer
                return SimpleObject.create("success", false, "message", "No printer configured for " + ui.format(location));
            }

            printerService.printViaSocket(data, printer, "UTF-8");

            // TODO switch all these to message codes
            return SimpleObject.create("success", true, "message", "Label printed for " + " " + printer.getPhysicalLocation().getName());

        } catch (UnableToPrintViaSocketException e) {
            log.warn("User " + uiSessionContext.getCurrentUser() + " unable to print at location "
                    + location, e);
            return SimpleObject.create("success", false, "message", "Unable to print label");
        }
    }

}
