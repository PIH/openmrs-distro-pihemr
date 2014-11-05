package org.openmrs.module.mirebalais.config;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ConfigDescriptor {

    @JsonProperty
    List<String> components;

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

}
