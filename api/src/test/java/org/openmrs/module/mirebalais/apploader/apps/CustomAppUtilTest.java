package org.openmrs.module.mirebalais.apploader.apps;

import org.junit.Test;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomAppUtilTest {

    @Test
    public void shouldFindResourceAtTopLevel() {

        File file = mock(File.class);

        ResourceFactory resourceFactory = mock(ResourceFactory.class);
        when(resourceFactory.getResource("pihcore", "htmlforms/checkin.xml")).thenReturn(file);

        Config config = mock(Config.class);
        when(config.getCountry()).thenReturn(ConfigDescriptor.Country.OTHER);
        when(config.getSite()).thenReturn(ConfigDescriptor.Site.OTHER);

        String resourcePath = CustomAppLoaderUtil.determineResourcePath(resourceFactory, config, "pihcore", "htmlforms", "checkin.xml");
        assertThat(resourcePath, is("pihcore:htmlforms/checkin.xml"));

    }

    @Test
    public void shouldFindResourceAtCountryLevel() {

        File file = mock(File.class);

        ResourceFactory resourceFactory = mock(ResourceFactory.class);
        when(resourceFactory.getResource("pihcore", "htmlforms/checkin.xml")).thenReturn(file);
        when(resourceFactory.getResource("pihcore", "htmlforms/other/checkin.xml")).thenReturn(file);

        Config config = mock(Config.class);
        when(config.getCountry()).thenReturn(ConfigDescriptor.Country.OTHER);
        when(config.getSite()).thenReturn(ConfigDescriptor.Site.OTHER);

        String resourcePath = CustomAppLoaderUtil.determineResourcePath(resourceFactory, config, "pihcore", "htmlforms", "checkin.xml");
        assertThat(resourcePath, is("pihcore:htmlforms/other/checkin.xml"));

    }

    @Test
    public void shouldFindResourceAtSiteLevel() {

        File file = mock(File.class);

        ResourceFactory resourceFactory = mock(ResourceFactory.class);
        when(resourceFactory.getResource("pihcore", "htmlforms/checkin.xml")).thenReturn(file);
        when(resourceFactory.getResource("pihcore", "htmlforms/other/checkin.xml")).thenReturn(file);
        when(resourceFactory.getResource("pihcore", "htmlforms/other/other/checkin.xml")).thenReturn(file);

        Config config = mock(Config.class);
        when(config.getCountry()).thenReturn(ConfigDescriptor.Country.OTHER);
        when(config.getSite()).thenReturn(ConfigDescriptor.Site.OTHER);

        String resourcePath = CustomAppLoaderUtil.determineResourcePath(resourceFactory, config, "pihcore", "htmlforms", "checkin.xml");
        assertThat(resourcePath, is("pihcore:htmlforms/other/other/checkin.xml"));

    }


    @Test
    public void shouldFindHtmlResourcePathAtCountryLevel() {

        File file = mock(File.class);

        ResourceFactory resourceFactory = mock(ResourceFactory.class);
        when(resourceFactory.getResource("pihcore", "htmlforms/checkin.xml")).thenReturn(file);
        when(resourceFactory.getResource("pihcore", "htmlforms/other/checkin.xml")).thenReturn(file);

        Config config = mock(Config.class);
        when(config.getCountry()).thenReturn(ConfigDescriptor.Country.OTHER);
        when(config.getSite()).thenReturn(ConfigDescriptor.Site.OTHER);

        String resourcePath = CustomAppLoaderUtil.determineHtmlFormPath(resourceFactory, config, "checkin");
        assertThat(resourcePath, is("pihcore:htmlforms/other/checkin.xml"));

    }
}
