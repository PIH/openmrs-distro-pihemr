package org.openmrs.module.mirebalais.apploader;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.Extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.AWAITING_ADMISSION_ACTIONS_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.AWAITING_ADMISSION_ACTIONS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.DASHBOARD_TAB_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.ENCOUNTER_TEMPLATE_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.HEADER_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.HOME_PAGE_APPS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.HOME_PAGE_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.OVERALL_ACTIONS_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.OVERALL_ACTIONS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTING_DATA_EXPORT_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTING_DATA_EXPORT_REPORTS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTING_OVERVIEW_REPORTS_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTING_OVERVIEW_REPORTS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.SYSTEM_ADMINISTRATION_APPS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.SYSTEM_ADMINISTRATION_PAGE_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.VISIT_ACTIONS_EXTENSION_POINT;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.VISIT_ACTIONS_ORDER;



public class CustomAppLoaderUtil {


    static public AppDescriptor app(String id, String label, String icon, String url, String privilege, ObjectNode config) {

        AppDescriptor app = new AppDescriptor(id, id, label, url, icon, null, 0, privilege, null);

        if (config != null) {
            app.setConfig(config);
        }

        return app;
    }

    static public AppDescriptor findPatientTemplateApp(String id, String label, String icon, String privilege, String afterSelectedUrl, ArrayNode breadcrumbs) {

        AppDescriptor app = new AppDescriptor(id, id, label, "coreapps/findpatient/findPatient.page?app=" + id, icon, null, 0, privilege, null);

        app.setConfig(objectNode(
                "afterSelectedUrl", afterSelectedUrl,
                "label", label,
                "heading", label,
                "showLastViewedPatients", false,
                "breadcrumbs", breadcrumbs));

        return app;
    }

    static public AppDescriptor addToHomePage(AppDescriptor app) {
        appExtension(app, app.getId() + ".appLink",
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                HOME_PAGE_APPS_ORDER.indexOf(app.getId()),
                HOME_PAGE_EXTENSION_POINT);
        return app;
    }

    static public AppDescriptor addToOverallActions(AppDescriptor app, String label) {
        appExtension(app, app.getId() + ".overallActions.appLink",
                label,
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                OVERALL_ACTIONS_ORDER.indexOf(app.getId()),
                OVERALL_ACTIONS_EXTENSION_POINT);
        return app;
    }

    static public AppDescriptor addToSystemAdministrationPage(AppDescriptor app) {
        appExtension(app, app.getId() + ".systemAdministration.appLink",
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                SYSTEM_ADMINISTRATION_APPS_ORDER.indexOf(app.getId()),
                SYSTEM_ADMINISTRATION_PAGE_EXTENSION_POINT);
        return app;
    }

    static public Extension visitAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require, VISIT_ACTIONS_EXTENSION_POINT, VISIT_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension overallAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require, OVERALL_ACTIONS_EXTENSION_POINT, OVERALL_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension awaitingAdmissionAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require, AWAITING_ADMISSION_ACTIONS_EXTENSION_POINT, AWAITING_ADMISSION_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension dashboardTab(String id, String label, String privilege, String provider, String fragment) {
        return new Extension(id, null, DASHBOARD_TAB_EXTENSION_POINT, "link", label, null, 0,
                privilege, map("provider", provider, "fragment", fragment));
    }

    static public Extension encounterTemplate(String id, String templateProvider, String templateFragment) {
        return new Extension(id, null, ENCOUNTER_TEMPLATE_EXTENSION_POINT, "fragment", null, null, 0, null,
                map("templateId", id, "templateFragmentProviderName", templateProvider, "templateFragmentId", templateFragment));
    }

    static public Extension header(String id, String logo) {
        return new Extension(id, null, HEADER_EXTENSION_POINT, "config", null, null, 0, null, map("logo-icon-url", logo));
    }

    static public Extension fragmentExtension(String id, String provider, String fragment, String privilege, String extensionPoint) {
        return new Extension(id, null, extensionPoint, "include-fragment", null, null, 0,
                privilege, map("provider", provider, "fragment", fragment));
    }

    static public Extension overviewReport(String id, String label, String definitionUuid, String privilege, String linkId) {
        return report(id, label, definitionUuid, privilege, REPORTING_OVERVIEW_REPORTS_EXTENSION_POINT, REPORTING_OVERVIEW_REPORTS_ORDER.indexOf(id), linkId);

    }

    static public Extension dataExport(String id, String label, String definitionUuid, String privilege, String linkId) {
        return report(id, label, definitionUuid, privilege, REPORTING_DATA_EXPORT_EXTENSION_POINT, REPORTING_DATA_EXPORT_REPORTS_ORDER.indexOf(id), linkId);
    }

    static public Extension report(String id, String label, String definitionUuid, String privilege, String extensionPoint, int order, String linkId) {
        return new Extension(id, null, extensionPoint, "link", label,"reportingui/runReport.page?reportDefinition=" + definitionUuid,
                order, privilege, map("linkId", linkId));
    }

    static public Extension extension(String id, String label, String icon, String type, String urlOrScript, String privilege, String require, String extensionPoint, int order, Map<String,Object> extensionParams) {
        Extension extension = new Extension(id, null,extensionPoint, type, label, null, order, privilege, null);
        extension.setIcon(icon);

        if (StringUtils.isNotBlank(require)) {
            extension.setRequire(require);
        }

        if (type.equals("link")) {
            extension.setUrl(urlOrScript);
        }
        else if (type.equals("script")) {
            extension.setScript(urlOrScript);
        }
        else {
            throw new IllegalStateException("Invalid type: " + type);
        }

        if (extensionParams != null) {
            extension.setExtensionParams(extensionParams);
        }

        return extension;
    }


    static public Extension appExtension(AppDescriptor app, String id, String label, String icon, String type, String url,
                                         String requiredPrivilege, int order, String extensionPoint) {

        Extension extension = new Extension(id, app.getId(), extensionPoint, type, label, url, order, requiredPrivilege, null);
        extension.setIcon(icon);

        if (app.getExtensions() == null) {
            app.setExtensions(new ArrayList<Extension>());
        }

        app.getExtensions().add(extension);
        return extension;
    }

    static public String simpleHtmlFormLink(String definitionUiResource) {
        return "htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{patient.uuid}}&visitId={{visit.id}}&definitionUiResource=" + definitionUiResource;
    }

    static public String standardHtmlFormLink(String definitionUiResource) {
        return "htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{patient.uuid}}&visitId={{visit.id}}&definitionUiResource=" + definitionUiResource;
    }

    static public void addFeatureToggleToApp(AppDescriptor app, String featureToggle) {
        app.setFeatureToggle(featureToggle);
    }

    static public void addFeatureToggleToExtension(Extension ext, String featureToggle) {
        ext.setFeatureToggle(featureToggle);
    }

    static public void registerTemplateForEncounterType(String encounterTypeUuid, Extension template, String icon) {
        registerTemplateForEncounterType(encounterTypeUuid, template, icon, null, null, null);
    }

    static public void registerTemplateForEncounterType(String encounterTypeUuid, Extension template, String icon,
                                                        Boolean displayWithHtmlForm, Boolean editable,
                                                        String primaryEncounterRoleUuid) {

        Map<String,Object> extensionParams = template.getExtensionParams();

        if (!extensionParams.containsKey("supportedEncounterTypes")) {
            extensionParams.put("supportedEncounterTypes", new HashMap<String,Object>());
        }

        Map<String,Object> encounterTypeParams = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(icon)) {
            encounterTypeParams.put("icon", icon);
        }
        if (displayWithHtmlForm != null) {
            encounterTypeParams.put("displayWithHtmlForm", displayWithHtmlForm);
        }
        if (editable != null) {
            encounterTypeParams.put("editable", editable);
        }
        if (StringUtils.isNotBlank(primaryEncounterRoleUuid)) {
            encounterTypeParams.put("primaryEncounterRoleUuid", primaryEncounterRoleUuid);
        }

        ((Map<String,Object>) extensionParams.get("supportedEncounterTypes")).put(encounterTypeUuid, encounterTypeParams);
    }

    static public ArrayNode arrayNode(ObjectNode ... nodes) {
        ArrayNode arrayNode = new ObjectMapper().createArrayNode();
        for (int i = 0; i < nodes.length; i++) {
            arrayNode.add(nodes[i]);
        }
        return arrayNode;
    }

    static public ObjectNode objectNode(Object ... obj) {

        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        for (int i = 0; i < obj.length; i=i+2) {
            String key = (String) obj[i];
            Object value = obj[i+1];

            if (value instanceof Boolean) {
                objectNode.put(key, (Boolean) value);
            }
            else if (value instanceof String) {
                objectNode.put(key, (String) value);
            }
            else if (value instanceof Integer) {
                objectNode.put(key, (Integer) value);
            }
            else if (value instanceof ArrayNode) {
                objectNode.put(key, (ArrayNode) value);
            }
            else if (value instanceof ObjectNode) {
                objectNode.put(key, (ObjectNode) value);
            }
        }

        return objectNode;
    }

    static public Map<String,Object> map(Object ... obj) {

        Map<String,Object> map = new HashMap<String, Object>();

        for (int i = 0; i < obj.length; i=i+2) {
            String key = (String) obj[i];
            Object value = obj[i+1];
            map.put(key, value);
        }

        return map;
    }


}
