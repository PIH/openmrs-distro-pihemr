package org.openmrs.module.mirebalais;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.mirebalais.setup.HtmlFormSetup;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpSession;

import java.io.InputStream;

@SkipBaseSetup
@Ignore
public class HtmlFormComponentTest extends BaseModuleWebContextSensitiveTest {

    @Before
    public void beforeEachTest() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        authenticate();
        HtmlFormSetup.setupHtmlFormEntryTagHandlers();
    }

    @Test
    public void testHtmlForms() throws Exception {
        String[] formsToTest = {
                "deathCertificate.xml",
                "patientRegistration.xml",
                "patientRegistration-rs.xml",
                "patientRegistration-social.xml",
                "zl/primary-care-adult-history.xml",
                "zl/primary-care-adult-exam-dx.xml"
        };

        for (String formName : formsToTest) {
            // loading up all of the UI Framework beans to fetch this resource is problematic, so just get it directly:
            InputStream in = getClass().getClassLoader().getResourceAsStream("web/module/resources/htmlforms/" + formName);
            String xml = IOUtils.toString(in);
            IOUtils.closeQuietly(in);
            HtmlForm form = HtmlFormUtil.getHtmlFormFromResourceXml(Context.getFormService(), Context.getService(HtmlFormEntryService.class), xml);

            FormEntrySession fes = new FormEntrySession(new Patient(), form, FormEntryContext.Mode.ENTER, new MockHttpSession());
            try {
                String html = fes.getHtmlToDisplay();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                Assert.fail("Failed to load " + formName + ": " + ex.getMessage());
            }
        }
    }


}
