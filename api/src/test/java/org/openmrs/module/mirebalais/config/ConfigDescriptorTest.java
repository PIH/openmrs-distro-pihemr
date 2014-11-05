package org.openmrs.module.mirebalais.config;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConfigDescriptorTest {

    @Test
    public void testParsing() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("testConfig.json");
        ConfigDescriptor configDescriptor = new ObjectMapper().readValue(inputStream, ConfigDescriptor.class);
        assertThat(configDescriptor.getComponents().size(), is(2));
        assertThat(configDescriptor.getComponents().contains("someComponent"), is(true));
        assertThat(configDescriptor.getComponents().contains("anotherComponent"), is(true));
        assertThat(configDescriptor.getComponents().contains("missingComponent"), is(false));
    }

}
