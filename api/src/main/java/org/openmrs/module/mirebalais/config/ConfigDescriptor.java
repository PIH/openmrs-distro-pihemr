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

    // Mirebalais-Specific Properties

    // whether or not to schedule a set of reports to be exported to disk regularly as a backup in case of downtime (see scheduleBackupReports method in Mirebalais Module Activator)
    // generally this should only be turned on on production
    @JsonProperty
    Boolean scheduleBackupReports;

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

    public Boolean getScheduleBackupReports() {
        return scheduleBackupReports;
    }

    public void setScheduleBackupReports(Boolean scheduleBackupReports) {
        this.scheduleBackupReports = scheduleBackupReports;
    }
}
