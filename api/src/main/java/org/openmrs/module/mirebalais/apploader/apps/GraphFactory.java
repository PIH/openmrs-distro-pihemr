package org.openmrs.module.mirebalais.apploader.apps;

import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants;
import org.openmrs.module.pihcore.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.app;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderUtil.objectNode;

@Component
public class GraphFactory {

    private Config config;

    @Autowired
    public GraphFactory(Config config) {
        this.config = config;
    }

    public AppDescriptor getBloodPressureGraph(String extensionPoint) {
        return app(
                CustomAppLoaderConstants.Apps.BLOOD_PRESSURE_GRAPH + extensionPoint,
                "pih.app.bloodPressure.graph.title",
                "icon-bar-chart",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.bloodPressure.graph.title",
                        "conceptId", MirebalaisConstants.DIASTOLIC_BP_CONCEPT_UUID + ","
                                + MirebalaisConstants.SYSTOLIC_BP_CONCEPT_UUID,
                        "maxResults", "10"
                ));
    }

    public AppDescriptor getBmiGraph(String extensionPoint) {
        return app(
                CustomAppLoaderConstants.Apps.BMI_GRAPH + extensionPoint,
                "pih.app.bmiGraph.title",
                "icon-bar-chart",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.bmiGraph.title",
                        "conceptId", MirebalaisConstants.WEIGHT_CONCEPT_UUID + ","
                                + MirebalaisConstants.HEIGHT_CONCEPT_UUID,
                        "function", "(bmi, "
                                + MirebalaisConstants.HEIGHT_CONCEPT_UUID + ", "
                                + MirebalaisConstants.WEIGHT_CONCEPT_UUID + ");", // the order of the parameters is important
                        "maxResults", "12"  // TODO what should this be?
                ));
    }

    public AppDescriptor getCholesterolGraph(String extensionPoint) {
        return app(
                CustomAppLoaderConstants.Apps.CHOLESTEROL_GRAPH + extensionPoint,
                "pih.app.cholesterolGraph.title",
                "icon-bar-chart",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.cholesterolGraph.title",
                        "conceptId", MirebalaisConstants.TOTAL_CHOLESTEROL_CONCEPT_UUID + ","
                                + MirebalaisConstants.HDL_CONCEPT_UUID + ","
                                + MirebalaisConstants.LDL_CONCEPT_UUID,
                        "maxRecords", "10"
                ));
    }

    public AppDescriptor getWHODASGraph(String extensionPoint) {
        return app(
                CustomAppLoaderConstants.Apps.WHODAS_GRAPH + extensionPoint,
                "pih.app.whodas.graph.title",
                "icon-bar-chart",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.whodas.graph.title",
                        "conceptId", MirebalaisConstants.WHODAS,
                        "maxRecords", "12"
                ));
    }

    public AppDescriptor getZLDSIGraph(String extensionPoint) {
        return app(
                CustomAppLoaderConstants.Apps.ZLDSI_GRAPH + extensionPoint,
                "pih.app.zldsi.graph.title",
                "icon-bar-chart",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.zldsi.graph.title",
                        "conceptId", MirebalaisConstants.ZLDSI,
                        "maxRecords", "12"
                ));
    }

    public AppDescriptor getSEIZUREFREQUENCYGraph(String extensionPoint) {
        return app(
                CustomAppLoaderConstants.Apps.SEIZURE_FREQUENCY_GRAPH + extensionPoint,
                "pih.app.seizure.frequency.graph.title",
                "icon-bar-chart",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.seizure.frequency.graph.title",
                        "conceptId", MirebalaisConstants.SEIZURE_FREQUENCY,
                        "maxRecords", "12"
                ));
    }

    public AppDescriptor getPHQ9Graph(String extensionPoint) {
        return app(
                CustomAppLoaderConstants.Apps.PHQ9_GRAPH + extensionPoint,
                "pih.app.phq9.graph.title",
                "icon-bar-chart",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.phq9.graph.title",
                        "conceptId", MirebalaisConstants.PHQ9,
                        "maxRecords", "12"
                ));
    }

    public AppDescriptor getGAD7Graph(String extensionPoint) {
        return app(
                CustomAppLoaderConstants.Apps.GAD7_GRAPH + extensionPoint,
                "pih.app.gad7.graph.title",
                "icon-bar-chart",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.gad7.graph.title",
                        "conceptId", MirebalaisConstants.GAD7,
                        "maxRecords", "12"
                ));
    }

}
