package org.openmrs.module.mirebalais.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Config {

    private final Log log = LogFactory.getLog(getClass());

    public static final String PIH_CONFIGURATION_RUNTIME_PROPERTY = "pih.config";

    private ConfigDescriptor descriptor;

    private PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    private ObjectMapper objectMapper = new ObjectMapper();

    public Config() {
        descriptor = new ConfigDescriptor();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        String config = Context.getRuntimeProperties().getProperty(PIH_CONFIGURATION_RUNTIME_PROPERTY);

        if (StringUtils.isBlank(config)) {
            config = "mirebalais";  // we default to mirebalais for now
        }

        InputStream configStream = findConfig(config.trim());

        if (configStream != null) {
            try {
                descriptor = objectMapper.readValue(configStream, ConfigDescriptor.class);
            }
            catch (IOException e) {
                throw new IllegalStateException("Unable to load config file for configuration " + config, e);
            }
        }
        else {
            throw new IllegalStateException("Unable to find config file for configuration " + config);
        }
    }

    public Boolean isComponentEnabled(String component) {
        return descriptor.getComponents().contains(component);
    }

    public String getWelcomeMessage() {
        return descriptor.getWelcomeMessage();
    }


    public ConfigDescriptor.Site getSite() {
        return descriptor.getSite();
    }

    private InputStream findConfig(String config) {

        String configFilename = "pih-config-" + config + ".json";
        Exception exception = null;

        // first see if is in the .OpenMRS directory (which will override any file of the same name on the classpath)
        File configFile = new File(OpenmrsUtil.getApplicationDataDirectory() + File.separatorChar + config);

        if (configFile.exists()) {
            try {
                return new FileInputStream(configFile);
            }
            catch (IOException e){
                exception = e;
            }
        }

        // if not found, check the classpath
        try {
            Resource[] appConfigJsonResource = resourceResolver.getResources("classpath*:/config/" + configFilename);
            if (appConfigJsonResource != null && appConfigJsonResource.length > 0) {
                if (appConfigJsonResource.length > 1) {
                    log.error("Multiple files named " + configFilename + " found, using one arbitrarily");
                }
                return appConfigJsonResource[0].getInputStream();
            }
        }
        catch (IOException e) {
            exception  = e;
        }

        log.error("Unable to find appframework configuration file " + configFilename + " either in /appconfig on the classpath or in the OpenMRS application directory", exception);
        return null;
    }
}


