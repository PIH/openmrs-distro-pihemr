package org.openmrs.module.mirebalais.apploader.apps;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleClassLoader;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.net.URL;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest( { ModuleClassLoader.class, ModuleFactory.class } )
public class CustomAppUtilTest {

    private ModuleClassLoader moduleClassLoader;

    @Before
    public void setup() {

        moduleClassLoader = mock(ModuleClassLoader.class);

        mockStatic(ModuleFactory.class);
        mockStatic(ModuleClassLoader.class);

        when(ModuleFactory.getStartedModuleById("pihcore")).thenReturn(null);
        when(ModuleFactory.getModuleClassLoader(any(Module.class))).thenReturn(moduleClassLoader);
    }

    @Test
    public void shouldFindResourceAtTopLevel() {

        URL url = mock(URL.class);

        when(moduleClassLoader.getResource("web/module/resources/htmlforms/checkin.xml")).thenReturn(url);

        Config config = mock(Config.class);
        when(config.getCountry()).thenReturn(ConfigDescriptor.Country.OTHER);
        when(config.getSite()).thenReturn(ConfigDescriptor.Site.OTHER);

        String resourcePath = CustomAppLoaderUtil.determineResourcePath(config, "pihcore", "htmlforms", "checkin.xml");
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

        String resourcePath = CustomAppLoaderUtil.determineResourcePath(config, "pihcore", "htmlforms", "checkin.xml");
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

        String resourcePath = CustomAppLoaderUtil.determineResourcePath(config, "pihcore", "htmlforms", "checkin.xml");
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

        String resourcePath = CustomAppLoaderUtil.determineHtmlFormPath(config, "checkin");
        assertThat(resourcePath, is("pihcore:htmlforms/other/checkin.xml"));

    }
}
