package org.openmrs.module.mirebalais.page.controller.patientRegistration;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.mirebalais.printer.impl.ZlEmrIdCardPrinter;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class PrintIdCardPageController {

    public void controller(PageModel model, UiUtils ui, UiSessionContext uiSessionContext,
                           @RequestParam("patientId") Patient patient,
                           @RequestParam(value="locationId", required=false) Location location,
                           @RequestParam(value="returnUrl", required=false) String returnUrl,
                           @SpringBean ZlEmrIdCardPrinter zlEmrIdCardPrinter) {

        if (location == null) {
            location = uiSessionContext.getSessionLocation();
        }

        if (StringUtils.isBlank(returnUrl)) {
            returnUrl = ui.pageLink("registrationapp", "registrationSummary", ObjectUtil.toMap("patientId", patient.getPatientId()));
        }

        model.addAttribute("patient", patient);
        model.addAttribute("location", location);
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("printerAvailableAtLocation", zlEmrIdCardPrinter.isAvailableAtLocation(location));
    }
}
