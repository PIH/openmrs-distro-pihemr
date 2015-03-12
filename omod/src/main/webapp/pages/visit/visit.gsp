<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("mirebalais", "visit/visit.css")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-app.js")
    ui.includeJavascript("uicommons", "services/visitService.js")
    ui.includeJavascript("uicommons", "services/encounterService.js")
    ui.includeJavascript("uicommons", "filters/display.js")
    ui.includeJavascript("uicommons", "handlebars/handlebars.js")
    ui.includeJavascript("mirebalais", "visit/constants.js")
    ui.includeJavascript("mirebalais", "visit/filters.js")
    ui.includeJavascript("mirebalais", "visit/visit-templates.js")
    ui.includeJavascript("mirebalais", "visit/visit.js")
%>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }

<div id="visit-app" ng-controller="VisitController">

    Go to another visit:
    <select ng-model="visitUuid" ng-options="visit.uuid as visit.display for visit in visits">
    </select>

    <div id="visit">
        <span class="visit-type">
            {{ visitTemplate.label | omrs.display }}
        </span>
        <span class="visit-dates">
            {{ visit.startDatetime | date:"medium" }}
            <span ng-show="visit.stopDatetime">- {{ visit.stopDatetime | date:"medium" }}</span>
            <em ng-hide="visit.stopDatetime">...ongoing</em>
        </span>
        <span class="visit-location">{{ visit.location | omrs.display }}</span>
        <span class="actions">
            <i class="icon-pencil"></i>
        </span>
    </div>

    <div ng-repeat="element in visitTemplate.elements">
        <display-element visit="visit" element="element"></display-element>
    </div>

</div>

<script type="text/javascript">
    angular.bootstrap("#visit-app", [ "visit" ])
</script>