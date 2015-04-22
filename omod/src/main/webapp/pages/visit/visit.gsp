<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("mirebalais", "visit/visit.css")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-app.js")
    ui.includeJavascript("uicommons", "services/visitService.js")
    ui.includeJavascript("uicommons", "services/encounterService.js")
    ui.includeJavascript("uicommons", "services/orderService.js")
    ui.includeJavascript("uicommons", "filters/display.js")
    ui.includeJavascript("uicommons", "handlebars/handlebars.js")
    ui.includeJavascript("uicommons", "moment.min.js")
    ui.includeJavascript("mirebalais", "visit/constants.js")
    ui.includeJavascript("mirebalais", "visit/filters.js")
    ui.includeJavascript("mirebalais", "visit/visit-templates.js")
    ui.includeJavascript("mirebalais", "visit/allergies.js")
    ui.includeJavascript("mirebalais", "visit/orders.js")
    //ui.includeJavascript("mirebalais", "visit/vaccinations.js")
    ui.includeJavascript("mirebalais", "visit/visit.js")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(patient.formattedName) }" },
        { label: "${ui.message("coreapps.patientDashBoard.visits")}" }
    ];
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<div id="visit-app" ng-controller="VisitController">

    <div id="choose-another-visit" ng-mouseenter="showOtherVisits = true" ng-mouseleave="showOtherVisits = false">
        <a>Go to another visit</a>
        <p class="popup" ng-show="showOtherVisits">
            <select ng-model="visitUuid" ng-options="visit.uuid as visit.display for visit in visits">
            </select>
        </p>
    </div>

    <div id="visit">
        <visit-details visit="visit"></visit-details>
    </div>

    <div ng-repeat="element in visitTemplate.elements">
        <display-element visit="visit" element="element" date-format="{{encounterDateFormat}}"></display-element>
    </div>

</div>

<script type="text/javascript">
    angular.bootstrap("#visit-app", [ "visit" ])
</script>