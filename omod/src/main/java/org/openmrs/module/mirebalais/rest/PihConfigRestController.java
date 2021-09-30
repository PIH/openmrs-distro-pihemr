package org.openmrs.module.mirebalais.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.mirebalais.setup.ConfigurationSetup;
import org.openmrs.module.reporting.config.ReportDescriptor;
import org.openmrs.module.reporting.config.ReportLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.HttpStatus.OK;

/**
 * TODO: This will ultimately go in PIH Core, but need to add it here for now due to lingering mirebalais dependencies
 */
@Controller
public class PihConfigRestController {

    protected Log log = LogFactory.getLog(getClass());

    private static String REQUIRED_PRIVILEGE = "App: coreapps.systemAdministration";

    @Autowired
    ConfigurationSetup configurationSetup;

    @RequestMapping(value = "/rest/v1/pihcore/config", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateConfig() {
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return HttpStatus.UNAUTHORIZED;
        }
        try {
            configurationSetup.configureSystem();
            return OK;
        }
        catch (Exception e) {
            log.error(e);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @RequestMapping(value = "/rest/v1/pihcore/config/reports/{reportKey}", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateReport(@PathVariable("reportKey") String reportKey) {
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return HttpStatus.UNAUTHORIZED;
        }
        for (ReportDescriptor descriptor : ReportLoader.loadReportDescriptors()) {
            if (reportKey.equalsIgnoreCase(descriptor.getKey()) || reportKey.equalsIgnoreCase(descriptor.getUuid())) {
                ReportLoader.loadReportFromDescriptor(descriptor);
            }
        }
        return OK;
    }

    @RequestMapping(value = "/rest/v1/pihcore/config/reports", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateAllReports() {
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return HttpStatus.UNAUTHORIZED;
        }
        ReportLoader.loadReportsFromConfig();
        return OK;
    }

    @RequestMapping(value = "/rest/v1/pihcore/config/appframework", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateAppsAndExtensions() {
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return HttpStatus.UNAUTHORIZED;
        }
        configurationSetup.reloadAppsAndExtensions();
        return OK;
    }
}
