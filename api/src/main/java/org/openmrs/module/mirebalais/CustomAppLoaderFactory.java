package org.openmrs.module.mirebalais;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.AppTemplate;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.factory.AppFrameworkFactory;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.CoreMetadata;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomAppLoaderFactory implements AppFrameworkFactory {

    // Apps

    // TODO would ne nice to rename these to "pih.checkin", "pih.vitals", etc, but
    // TODO for checkin would need to fix requestRecord.gsp:69
    // TODO also would need to think about privileges
    public static final String CHECK_IN_APP = "mirebalais.liveCheckin";
    public static final String VITALS_APP = "mirebalais.outpatientVitals";
    public static final String AWAITING_ADMISSION_APP = "coreapps.app.awaitingAdmission";

    // Extensions
    public static final String CHECK_IN_VISIT_ACTION = "pih.checkin.visitAction";
    public static final String VITALS_CAPTURE_VISIT_ACTION = "mirebalais.vitals";
    public static final String ORDER_XRAY_VISIT_ACTION = "org.openmrs.module.radiologyapp.orderXray";
    public static final String ORDER_CT_VISIT_ACTION = "org.openmrs.module.radiologyapp.orderCT";
    public static final String ORDER_ULTRASOUND_VISIT_ACTION = "org.openmrs.module.radiologyapp.orderUS";

    public static final String REQUEST_PAPER_RECORD_OVERALL_ACTION = "org.openmrs.module.paperrecord.requestPaperRecord";
    public static final String PRINT_ID_CARD_OVERALL_ACTION = "org.openmrs.module.paperrecord.printIdCardLabel";
    public static final String PRINT_PAPER_FORM_LABEL_OVERALL_ACTION = "org.openmrs.module.paperrecord.printPaperFormLabel";

    public static final String RADIOLOGY_TAB = "org.openmrs.module.radiologyapp.tab";

    public static final String PIH_HEADER_EXTENSION = "pih.header";

    // Extension Points
    public static final String OVERALL_ACTIONS_EXTENSION_POINT = "patientDashboard.overallActions";
    public static final String VISIT_ACTIONS_EXTENSION_POINT = "patientDashboard.visitActions";
    public static final String ENCOUNTER_TEMPLATE_EXTENSION_POINT = "org.openmrs.referenceapplication.encounterTemplate";
    public static final String HOME_PAGE_EXTENSION_POINT = "org.openmrs.referenceapplication.homepageLink";
    public static final String HEADER_EXTENSION_POINT = "org.openmrs.module.appui.header.config";
    public static final String DASHBOARD_TAB_EXTENSION_POINT = "patientDashboard.tabs";

    // Encounter templates
    public static final String DEFAULT_ENCOUNTER_TEMPLATE = "openmrs.defaultEncounterTemplate";
    public static final String CONSULT_ENCOUNTER_TEMPLATE = "pih.consultEncounterTemplate";
    public static final String NO_DETAILS_ENCOUNTER_TEMPLATE = "pih.noDetailsEncounterTemplate";

    // order of lists define the order apps and extensions appear
    public static final List<String> HOME_PAGE_APPS_ORDER = Arrays.asList(
            CHECK_IN_APP,
            VITALS_APP,
            AWAITING_ADMISSION_APP);

    public static final List<String> OVERALL_ACTIONS_ORDER = Arrays.asList(
            REQUEST_PAPER_RECORD_OVERALL_ACTION,
            PRINT_PAPER_FORM_LABEL_OVERALL_ACTION,
            PRINT_ID_CARD_OVERALL_ACTION);

    public static final List<String> VISIT_ACTIONS_ORDER = Arrays.asList(
            CHECK_IN_VISIT_ACTION,
            VITALS_CAPTURE_VISIT_ACTION,
            ORDER_XRAY_VISIT_ACTION,
            ORDER_CT_VISIT_ACTION,
            ORDER_ULTRASOUND_VISIT_ACTION);


    private List<AppDescriptor> apps;

    private List<Extension> extensions;

    // TODO have this be set to true on a context refresh?  would also need to trigger another a context refresh
    private Boolean needsRefresh = true;

    @Override
    public List<AppDescriptor> getAppDescriptors() throws IOException {
        if (needsRefresh) {
            loadAppsAndExtensions();
        }
        return apps;
    }

    @Override
    public List<Extension> getExtensions() throws IOException {
        if (needsRefresh) {
            loadAppsAndExtensions();
        }
        return extensions;
    }

    @Override
    public List<AppTemplate> getAppTemplates() throws IOException {
        return null;
    }


    private void loadAppsAndExtensions() {

        apps = new ArrayList<AppDescriptor>();
        extensions = new ArrayList<Extension>();

        configureHeader();
        setupDefaultEncounterTemplates();
        enableCheckIn();
        enableVitalsCapture();
        enableAwaitingAdmission();
        enableRadiology();
        enableArchives();

        needsRefresh = false;
    }

    private void configureHeader(){
        extensions.add(header(PIH_HEADER_EXTENSION,"/ms/uiframework/resource/mirebalais/images/partners_in_health_logo.png"));
    }

    private void setupDefaultEncounterTemplates() {

        extensions.add(encounterTemplate(DEFAULT_ENCOUNTER_TEMPLATE,
                "coreapps",
                "patientdashboard/encountertemplate/defaultEncounterTemplate"));

        extensions.add(encounterTemplate(NO_DETAILS_ENCOUNTER_TEMPLATE,
                "coreapps",
                "patientdashboard/encountertemplate/noDetailsEncounterTemplate"));
    }

    private void enableCheckIn() {

        apps.add(addToHomePage(findPatientTemplateApp(CHECK_IN_APP,
                "mirebalais.app.patientRegistration.checkin.label",
                "icon-paste",
                "App: mirebalais.checkin",
                "/mirebalais/checkin/checkin.page?patientId={{patientId}}")));

        extensions.add(visitAction(CHECK_IN_VISIT_ACTION,
                "mirebalais.task.checkin.label",
                "icon-check-in",
                "link",
                "htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{patient.uuid}}&visitId={{visit.id}}&definitionUiResource=mirebalais:htmlforms/checkin.xml",
                "Task: mirebalais.checkinForm",
                null));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.CHECK_IN, DEFAULT_ENCOUNTER_TEMPLATE, "icon-check-in");
    }

    private void enableVitalsCapture() {

        apps.add(addToHomePage(findPatientTemplateApp(VITALS_APP,
                "mirebalais.outpatientVitals.title",
                "icon-vitals",
                "App: mirebalais.outpatientVitals",
                "/mirebalais/outpatientvitals/patient.page?patientId={{patientId}}")));

        extensions.add(visitAction(VITALS_CAPTURE_VISIT_ACTION,
                "mirebalais.task.vitals.label",
                "icon-vitals",
                "link",
                "htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{patient.uuid}}&visitId={{visit.id}}&definitionUiResource=mirebalais:htmlforms/vitals.xml",
                "Task: emr.enterClinicalForms",
                "visit != null && visit.active"));

        registerTemplateForEncounterType(CoreMetadata.EncounterTypes.VITALS, DEFAULT_ENCOUNTER_TEMPLATE, "icon-vitals");

    }

    private void enableAwaitingAdmission() {

        apps.add(addToHomePage(app(AWAITING_ADMISSION_APP,
                "coreapps.app.awaitingAdmission.label",
                "icon-list-ul",
                "coreapps/adt/awaitingAdmission.page",
                "App: coreapps.awaitingAdmission",
                objectNode("patientPageUrl", "/coreapps/patientdashboard/patientDashboard.page?patientId={{patientId}}"))));

    }

    private void enableRadiology() {

        extensions.add(dashboardTab(RADIOLOGY_TAB,
                "radiologyapp.radiology.label",
                "Task: org.openmrs.module.radiologyapp.tab",
                "radiologyapp",
                "radiologyTab"));

        extensions.add(visitAction(ORDER_XRAY_VISIT_ACTION,
                "radiologyapp.task.order.CR.label",
                "icon-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=CR",
                null,
                "(user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.orderXray') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.retroOrder')"));

        extensions.add(visitAction(ORDER_CT_VISIT_ACTION,
                "radiologyapp.task.order.CT.label",
                "icon-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=Ct",
                null,
                "(user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.orderCT') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.retroOrder')"));

        extensions.add(visitAction(ORDER_ULTRASOUND_VISIT_ACTION,
                "radiologyapp.task.order.US.label",
                "icon-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=US",
                null,
                "(user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.orderUS') && visit != null && visit.active) || user.get('fn').hasPrivilege('Task: org.openmrs.module.radiologyapp.retroOrder')"));

    }

    private void enableArchives() {

        extensions.add(overallAction(REQUEST_PAPER_RECORD_OVERALL_ACTION,
                "paperrecord.task.requestPaperRecord.label",
                "icon-folder-open",
                "script",
                "showRequestChartDialog()",
                "Task: emr.requestPaperRecord",
                null));

        extensions.add(overallAction(PRINT_ID_CARD_OVERALL_ACTION,
                "paperrecord.task.printIdCardLabel.label",
                "icon-print-open",
                "script",
                "printIdCardLabel()",
                "Task: emr.printLabels",
                null));

        extensions.add(overallAction(PRINT_PAPER_FORM_LABEL_OVERALL_ACTION,
                "paperrecord.task.printPaperFormLabel.label",
                "icon-print-open",
                "script",
                "printPaperFormLabel()",
                "Task: emr.printLabels",
                null));

    }

    static public AppDescriptor app(String id, String label, String icon, String url, String privilege, ObjectNode config) {

        AppDescriptor app = new AppDescriptor(id, id, label, url, icon, null, 0, privilege, null);

        if (config != null) {
            app.setConfig(config);
        }

        return app;
    }

    static public AppDescriptor findPatientTemplateApp(String id, String label, String icon, String privilege, String afterSelectedUrl) {

        AppDescriptor app = new AppDescriptor(id, id, label, "coreapps/findpatient/findPatient.page?app=" + id, icon, null, 0, privilege, null);

        app.setConfig(objectNode(
                "afterSelectedUrl", afterSelectedUrl,
                "label", label,
                "heading", label,
                "showLastViewedPatients", false));

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

    static public Extension visitAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  action(id, label, icon, type, urlOrScript, privilege, require, VISIT_ACTIONS_EXTENSION_POINT, VISIT_ACTIONS_ORDER.indexOf(id));
    }

    static public Extension overallAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  action(id, label, icon, type, urlOrScript, privilege, require, OVERALL_ACTIONS_EXTENSION_POINT, OVERALL_ACTIONS_ORDER.indexOf(id));
    }

    static private Extension action(String id, String label, String icon, String type, String urlOrScript, String privilege, String require, String extensionPoint, int order) {
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
        return extension;
    }

    static public Extension dashboardTab(String id, String label, String privilege, String provider, String fragment) {
        return new Extension(id, null, DASHBOARD_TAB_EXTENSION_POINT, "link", label, null, 0,
                privilege,map("provider", provider, "fragment", fragment));

    }

    static public Extension encounterTemplate(String id, String templateProvider, String templateFragment) {
        return new Extension(id, null, ENCOUNTER_TEMPLATE_EXTENSION_POINT, "fragment", null, null, 0, null,
                map("templateId", id, "templateFragmentProviderName", templateProvider, "templateFragmentId", templateFragment));
    }

    static public Extension header(String id, String logo) {
        return new Extension(id, null, HEADER_EXTENSION_POINT, "config", null, null, 0, null, map("logo-icon-url", logo));
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

    // TODO rework and make static?
    public void registerTemplateForEncounterType(String encounterTypeUuid, String templateId, String icon) {
        registerTemplateForEncounterType(encounterTypeUuid, templateId, icon, null, null, null, null);
    }

    public void registerTemplateForEncounterType(String encounterTypeUuid, String templateId, String icon,
                                                       Boolean displayWithHtmlForm, Boolean editable,
                                                       String primaryEncounterRoleUuid, String comment) {

        Map<String,Object> extensionParams = findExtensionById(templateId).getExtensionParams();

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
        if (StringUtils.isNotBlank(comment)) {
            encounterTypeParams.put("_comment",comment);
        }

        ((Map<String,Object>) extensionParams.get("supportedEncounterTypes")).put(encounterTypeUuid, encounterTypeParams);
    }

    public Extension findExtensionById(String id) {
        for (Extension extension : extensions) {
            if (extension.getId().equals(id)) {
                return extension;
            }
        }
        throw new IllegalStateException("Extension Not Found: " + id);
    }

    public void setNeedsRefresh(Boolean needsRefresh) {
        this.needsRefresh = needsRefresh;
    }

    // used for mocking
    public void setApps(List<AppDescriptor> apps) {
        this.apps = apps;
    }

    public void setExtensions(List<Extension> extensions) {
        this.extensions = extensions;
    }


}
