package org.openmrs.module.mirebalais;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appui.AppUiExtensions;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderFactory;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.objectNode;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.registerTemplateForEncounterType;

public class CustomAppLoaderTest {

    @Test
    public void shouldSetUpAppsAndExtensions() throws Exception {
        // TODO implement--right now we are just testing to make sure there are no runtime tests
        CustomAppLoaderFactory factory = new CustomAppLoaderFactory();
        factory.getExtensions();
        factory.getAppDescriptors();
    }

    @Test
    public void shouldCreateHeader() {
        Extension extension = CustomAppLoaderUtil.header("id", "logo");

        assertThat(extension.getId(), is("id"));
        assertThat(extension.getExtensionPointId(), is(AppUiExtensions.HEADER_CONFIG_EXTENSION));
        assertThat(extension.getType(), is("config"));
        assertThat((String) extension.getExtensionParams().get("logo-icon-url"), is("logo"));
    }

    @Test
    public void shouldCreateApp() {
        AppDescriptor app = CustomAppLoaderUtil.app("id", "label", "icon", "url", "privilege", objectNode("patientPageUrl", "patientPageUrl"));

        assertThat(app.getId(), is("id"));
        assertThat(app.getLabel(), is("label"));
        assertThat(app.getIcon(), is("icon"));
        assertThat(app.getUrl(), is("url"));
        assertThat(app.getRequiredPrivilege(), is("privilege"));
        assertThat(app.getConfig().get("patientPageUrl").getTextValue(), is("patientPageUrl"));

    }


    @Test
    public void shouldCreatePatientTemplateApp() {
        AppDescriptor app = CustomAppLoaderUtil.findPatientTemplateApp("id", "label", "icon", "privilege", "afterSelectedUrl",
                CustomAppLoaderUtil.arrayNode(CustomAppLoaderUtil.objectNode("label", "label1", "link", "link1"),
                        CustomAppLoaderUtil.objectNode("label", "label2", "link", "link2")));

        assertThat(app.getId(), is("id"));
        assertThat(app.getLabel(), is("label"));
        assertThat(app.getIcon(), is("icon"));
        assertThat(app.getUrl(), is("coreapps/findpatient/findPatient.page?app=id"));
        assertThat(app.getRequiredPrivilege(), is("privilege"));
        assertThat(app.getConfig().get("afterSelectedUrl").getTextValue(), is("afterSelectedUrl"));
        assertThat(app.getConfig().get("label").getTextValue(), is("label"));
        assertThat(app.getConfig().get("heading").getTextValue(), is("label"));
        assertThat(app.getConfig().get("showLastViewedPatients").getBooleanValue(), is(false));
        assertThat(app.getConfig().get("breadcrumbs").get(0).get("label").getTextValue(), is("label1"));
        assertThat(app.getConfig().get("breadcrumbs").get(0).get("link").getTextValue(), is("link1"));
        assertThat(app.getConfig().get("breadcrumbs").get(1).get("label").getTextValue(), is("label2"));
        assertThat(app.getConfig().get("breadcrumbs").get(1).get("link").getTextValue(), is("link2"));

    }

    @Test
    public void shouldAddAppToHomePage() {
        AppDescriptor app = CustomAppLoaderUtil.app("id", "label", "icon", "url", "privilege", objectNode("patientPageUrl", "patientPageUrl"));

        CustomAppLoaderUtil.addToHomePage(app);
        assertThat(app.getExtensions().size(), is(1));
        assertThat(app.getExtensions().get(0).getId(), is("id.appLink"));
        assertThat(app.getExtensions().get(0).getType(), is("link"));
        assertThat(app.getExtensions().get(0).getLabel(), is("label"));
        assertThat(app.getExtensions().get(0).getUrl(), is("url"));
        assertThat(app.getExtensions().get(0).getIcon(), is("icon"));
        assertThat(app.getExtensions().get(0).getRequiredPrivilege(), is("privilege"));
        assertThat(app.getExtensions().get(0).getExtensionPointId(), is(CustomAppLoaderConstants.ExtensionPoints.HOME_PAGE));
        assertThat(app.getConfig().get("patientPageUrl").getTextValue(), is("patientPageUrl"));
    }

    @Test
    public void shouldAddAppToSystemAdministrationPage() {
        AppDescriptor app = CustomAppLoaderUtil.app("id", "label", "icon", "url", "privilege", null);

        CustomAppLoaderUtil.addToSystemAdministrationPage(app);
        assertThat(app.getExtensions().size(), is(1));
        assertThat(app.getExtensions().get(0).getId(), is("id.systemAdministration.appLink"));
        assertThat(app.getExtensions().get(0).getType(), is("link"));
        assertThat(app.getExtensions().get(0).getLabel(), is("label"));
        assertThat(app.getExtensions().get(0).getUrl(), is("url"));
        assertThat(app.getExtensions().get(0).getIcon(), is("icon"));
        assertThat(app.getExtensions().get(0).getRequiredPrivilege(), is("privilege"));
        assertThat(app.getExtensions().get(0).getExtensionPointId(), is(CustomAppLoaderConstants.ExtensionPoints.SYSTEM_ADMINISTRATION_PAGE));
    }


    @Test
    public void shouldCreateVisitActionsExtension() {
        Extension extension = CustomAppLoaderUtil.visitAction(CustomAppLoaderConstants.Extensions.ORDER_XRAY_VISIT_ACTION, "label", "icon","link", "url", "privilege", "require");

        assertThat(extension.getId(), is(CustomAppLoaderConstants.Extensions.ORDER_XRAY_VISIT_ACTION));
        assertThat(extension.getLabel(), is("label"));
        assertThat(extension.getIcon(), is("icon"));
        assertThat(extension.getUrl(), is("url"));
        assertThat(extension.getScript(), nullValue());
        assertThat(extension.getRequiredPrivilege(), is("privilege"));
        assertThat(extension.getRequire(), is("require"));
        assertThat(extension.getType(), is("link"));
        assertThat(extension.getExtensionPointId(), is(CustomAppLoaderConstants.ExtensionPoints.VISIT_ACTIONS));
    }

    @Test
    public void shouldCreateOverallActionsExtension() {
        Extension extension = CustomAppLoaderUtil.overallAction(CustomAppLoaderConstants.Extensions.PRINT_PAPER_FORM_LABEL_OVERALL_ACTION, "label", "icon","script", "script", "privilege", "require");

        assertThat(extension.getId(), is(CustomAppLoaderConstants.Extensions.PRINT_PAPER_FORM_LABEL_OVERALL_ACTION));
        assertThat(extension.getLabel(), is("label"));
        assertThat(extension.getIcon(), is("icon"));
        assertThat(extension.getUrl(), is(nullValue()));
        assertThat(extension.getScript(), is("script"));
        assertThat(extension.getRequiredPrivilege(), is("privilege"));
        assertThat(extension.getRequire(), is("require"));
        assertThat(extension.getType(), is("script"));
        assertThat(extension.getExtensionPointId(), is(CustomAppLoaderConstants.ExtensionPoints.OVERALL_ACTIONS));
    }

    @Test
    public void shouldCreateAwaitingAdmissionActionsExtension() {
        Extension extension = CustomAppLoaderUtil.awaitingAdmissionAction(CustomAppLoaderConstants.Extensions.ADMISSION_FORM_AWAITING_ADMISSION_ACTION, "label", "icon", "link", "url", "privilege", "require");

        assertThat(extension.getId(), is(CustomAppLoaderConstants.Extensions.ADMISSION_FORM_AWAITING_ADMISSION_ACTION));
        assertThat(extension.getLabel(), is("label"));
        assertThat(extension.getIcon(), is("icon"));
        assertThat(extension.getUrl(), is("url"));
        assertThat(extension.getScript(), nullValue());
        assertThat(extension.getRequiredPrivilege(), is("privilege"));
        assertThat(extension.getRequire(), is("require"));
        assertThat(extension.getType(), is("link"));
        assertThat(extension.getExtensionPointId(), is(CustomAppLoaderConstants.ExtensionPoints.AWAITING_ADMISSION_ACTIONS));
    }


    @Test
    public void shouldCreateDashboardTab() {
        Extension extension = CustomAppLoaderUtil.dashboardTab("id", "label", "privilege", "provider", "fragment");

        assertThat(extension.getId(), is("id"));
        assertThat(extension.getExtensionPointId(), is("patientDashboard.tabs"));
        assertThat(extension.getType(), is("link"));
        assertThat(extension.getLabel(), is("label"));
        assertThat(extension.getRequiredPrivilege(), is("privilege"));
        assertThat((String) extension.getExtensionParams().get("provider"), is("provider"));
        assertThat((String) extension.getExtensionParams().get("fragment"), is("fragment"));
    }

    @Test
    public void shouldCreateOverviewReportExtension() {
        Extension extension = CustomAppLoaderUtil.overviewReport("id", "label", "uuid", "privilege", "linkId");

        assertThat(extension.getId(), is("id"));
        assertThat(extension.getLabel(), is("label"));
        assertThat(extension.getType(), is("link"));
        assertThat(extension.getExtensionPointId(), is(CustomAppLoaderConstants.ExtensionPoints.REPORTING_OVERVIEW_REPORTS));
        assertThat(extension.getRequiredPrivilege(), is("privilege"));
        assertThat(extension.getUrl(), is("reportingui/runReport.page?reportDefinition=uuid"));
        assertThat((String) extension.getExtensionParams().get("linkId"), is("linkId"));

    }

    @Test
    public void shouldCreateDataExportExtension() {
        Extension extension = CustomAppLoaderUtil.dataExport("id", "label", "uuid", "privilege", "linkId");

        assertThat(extension.getId(), is("id"));
        assertThat(extension.getLabel(), is("label"));
        assertThat(extension.getType(), is("link"));
        assertThat(extension.getExtensionPointId(), is(CustomAppLoaderConstants.ExtensionPoints.REPORTING_DATA_EXPORT));
        assertThat(extension.getRequiredPrivilege(), is("privilege"));
        assertThat(extension.getUrl(), is("reportingui/runReport.page?reportDefinition=uuid"));
        assertThat((String) extension.getExtensionParams().get("linkId"), is("linkId"));

    }

    @Test
    public void shouldCreateFragmentExtension() {
        Extension extension = CustomAppLoaderUtil.fragmentExtension("id", "provider", "fragment", "privilege", "extensionPoint");

        assertThat(extension.getId(), is ("id"));
        assertThat((String) extension.getExtensionParams().get("provider"), is("provider"));
        assertThat((String) extension.getExtensionParams().get("fragment"), is("fragment"));
        assertThat(extension.getRequiredPrivilege(), is("privilege"));
        assertThat(extension.getExtensionPointId(), is("extensionPoint"));
    }

    @Test
    public void shouldCreateAppExtension() {
        AppDescriptor app = new AppDescriptor();
        CustomAppLoaderUtil.appExtension(app, "id", "label", "icon", "type",
                "url", "requiredPrivilege", 1, "extensionPoint");

        assertThat(app.getExtensions().size(), is(1));
        assertThat(app.getExtensions().get(0).getId(), is("id"));
        assertThat(app.getExtensions().get(0).getExtensionPointId(), is("extensionPoint"));
        assertThat(app.getExtensions().get(0).getType(), is("type"));
        assertThat(app.getExtensions().get(0).getLabel(), is("label"));
        assertThat(app.getExtensions().get(0).getUrl(), is("url"));
        assertThat(app.getExtensions().get(0).getIcon(), is("icon"));
        assertThat(app.getExtensions().get(0).getOrder(), is(1));
        assertThat(app.getExtensions().get(0).getRequiredPrivilege(), is("requiredPrivilege"));
    }


    @Test
    public void shouldCreateEncounterTemplateExtension() {
        Extension  extension = CustomAppLoaderUtil.encounterTemplate("id", "provider", "fragment");

        assertThat(extension.getId(), is("id"));
        assertThat(extension.getExtensionPointId(), is("org.openmrs.referenceapplication.encounterTemplate"));
        assertThat(extension.getType(), is("fragment"));
        assertThat((String) extension.getExtensionParams().get("templateId"), is("id"));
        assertThat((String) extension.getExtensionParams().get("templateFragmentProviderName"), is("provider"));
        assertThat((String) extension.getExtensionParams().get("templateFragmentId"), is("fragment"));
        assertThat((String) extension.getExtensionParams().get("templateFragmentProviderName"), is("provider"));
    }

    @Test
    public void shouldRegisterTemplateForEncounterType() {

        List<Extension> extensions = new ArrayList<Extension>();
        Extension template = CustomAppLoaderUtil.encounterTemplate("id", "provider", "fragment");
        extensions.add(template);

        CustomAppLoaderFactory factory = new CustomAppLoaderFactory();
        factory.setExtensions(extensions);

        registerTemplateForEncounterType("encounterTypeUuid", factory.findExtensionById("id"), "icon",
                true, false, "primaryEncounterRoleUuid");

        assertTrue(template.getExtensionParams().containsKey("supportedEncounterTypes"));
        assertTrue(((Map<String, Object>) template.getExtensionParams().get("supportedEncounterTypes")).containsKey("encounterTypeUuid"));

        Map<String,Object> params = (Map<String, Object>) ((Map<String, Object>) template.getExtensionParams().get("supportedEncounterTypes")).get("encounterTypeUuid");
        assertThat((String) params.get("icon"), is("icon"));
        assertThat((String) params.get("primaryEncounterRoleUuid"), is("primaryEncounterRoleUuid"));
        assertThat((Boolean) params.get("displayWithHtmlForm"), is(true));
        assertThat((Boolean) params.get("editable"), is(false));

    }

    @Test
    public void shouldConvertToObjectNode() {
        ObjectNode objectNode = CustomAppLoaderUtil.objectNode("int", 1, "string", "string", "boolean", true);
        assertThat(objectNode.get("int").getIntValue(), is(1));
        assertThat(objectNode.get("string").getTextValue(), is("string"));
        assertThat(objectNode.get("boolean").getBooleanValue(), is(true));
    }

    @Test
    public void shouldConvertToMap() {
        Map<String,Object> map = CustomAppLoaderUtil.map("int", 1, "string", "string", "boolean", true);
        assertThat((Integer) map.get("int"), is (1));
        assertThat((String) map.get("string"), is ("string"));
        assertThat((Boolean) map.get("boolean"), is (true));
    }

}
