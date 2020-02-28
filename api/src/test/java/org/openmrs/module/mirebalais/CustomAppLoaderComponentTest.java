package org.openmrs.module.mirebalais;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderFactory;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.SocioEconomicConcepts;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.registerTemplateForEncounterType;

@SkipBaseSetup
@Ignore
public class CustomAppLoaderComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private CustomAppLoaderFactory factory;

    @Autowired
    private MetadataDeployService deployService;

    @Autowired
    private SocioEconomicConcepts socioEconomicConcepts;

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.setProperty("pih.config", "default");
        return p;
    }

    @Before
    public void setup() throws Exception {
        setAutoIncrementOnTablesWithNativeIfNotAssignedIdentityGenerator();
        executeDataSet("org/openmrs/module/pihcore/coreMetadata.xml");
        authenticate();
    }

    @Test
    public void shouldSetUpAppsAndExtensions() throws Exception {
        deployService.installBundle(socioEconomicConcepts);
        factory.setConfig(new Config());
        factory.getExtensions();
        factory.getAppDescriptors();
    }

    @Test
    public void shouldRegisterTemplateForEncounterType() {

        List<Extension> extensions = new ArrayList<Extension>();
        Extension template = CustomAppLoaderUtil.encounterTemplate("id", "provider", "fragment");
        extensions.add(template);

        factory.setExtensions(extensions);

        registerTemplateForEncounterType(EncounterTypes.PATIENT_REGISTRATION, factory.findExtensionById("id"), "icon",
                true, false, "someLink", "primaryEncounterRoleUuid");

        assertTrue(template.getExtensionParams().containsKey("supportedEncounterTypes"));
        assertTrue(((Map<String, Object>) template.getExtensionParams().get("supportedEncounterTypes")).containsKey(EncounterTypes.PATIENT_REGISTRATION.uuid()));

        Map<String, Object> params = (Map<String, Object>) ((Map<String, Object>) template.getExtensionParams().get("supportedEncounterTypes")).get(EncounterTypes.PATIENT_REGISTRATION.uuid());
        assertThat((String) params.get("icon"), is("icon"));
        assertThat((String) params.get("primaryEncounterRoleUuid"), is("primaryEncounterRoleUuid"));
        assertThat((Boolean) params.get("displayWithHtmlForm"), is(true));
        assertThat((Boolean) params.get("editable"), is(false));
        assertThat((String) params.get("editUrl"), is("someLink"));

    }

}
