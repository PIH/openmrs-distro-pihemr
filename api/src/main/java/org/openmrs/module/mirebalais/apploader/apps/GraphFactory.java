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

}
