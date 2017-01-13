package org.openmrs.module.mirebalais.wristband;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiPropertiesAndMappingsBundle;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;


public class WristbandComponentTest extends BaseModuleContextSensitiveTest {

    private static Locale locale = new Locale("fr");

    private static DateFormat df  = new SimpleDateFormat("dd MMM yyyy", locale);

    @Autowired
    private TestDataManager testDataManager;

    @Autowired
    private WristbandTemplate wristbandTemplate;

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Autowired
    private PaperRecordProperties paperRecordProperties;

    @Autowired
    private HaitiPropertiesAndMappingsBundle haitiPropertiesAndMappingsBundle;

    @Autowired
    private MetadataDeployService metadataDeployService;

    @Before
    public void setup() throws Exception {
        executeDataSet("wristbandTestDataset.xml");
        metadataDeployService.installBundle(haitiPropertiesAndMappingsBundle);
    }

    @Test
    public void testWristbandTemplate() {

        Date today = new Date();

        Location location = testDataManager.location()
                .name("Hôpital Universitaire de Mirebalais")
                .tag(emrApiProperties.getSupportsVisitsLocationTag())
                .save();

        Patient patient = testDataManager.patient()
                .identifier(emrApiProperties.getPrimaryIdentifierType(), "X2ECEX", location)
                .identifier(paperRecordProperties.getPaperRecordIdentifierType(), "A000005", location)
                .birthdate(new DateTime(1940,7,7,5,5,5).toDate())
                .gender("M")
                .name("Ringo", "Starr")
                .save();

        String output = wristbandTemplate.generateWristband(patient, location);

        assertThat(output, containsString("^XA^CI28^MTD^FWB"));
        assertThat(output, containsString("^FO050,200^FB2150,1,0,L,0^AS^FDHôpital Universitaire de Mirebalais " + df.format(today) + "^FS"));
        assertThat(output, containsString("^FO100,200^FB2150,1,0,L,0^AU^FDRingo Starr^FS"));
        assertThat(output, containsString("^FO160,200^FB2150,1,0,L,0^AU^FD07 juil. 1940^FS"));
        assertThat(output, containsString("^FO160,200^FB1850,1,0,L,0^AT^FDcoreapps.ageYears^FS"));   // no message source service, se we are just going to get back the message code for age years and gender
        assertThat(output, containsString("^FO160,200^FB1650,1,0,L,0^AU^FDcoreapps.gender.M  A 000005^FS"));
        assertThat(output, containsString("^FO100,2400^AT^BY4^BC,150,N^FDX2ECEX^XZ"));
    }
}
