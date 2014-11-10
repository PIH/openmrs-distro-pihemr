package org.openmrs.module.mirebalais.config;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ConfigDescriptor {

    public enum Site {
        MIREBALAIS, LACOLLINE
    }

    @JsonProperty
    String welcomeMessage;

    @JsonProperty
    Site site;

    @JsonProperty
    List<String> components;

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

}
