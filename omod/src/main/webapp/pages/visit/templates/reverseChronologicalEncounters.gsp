<h4>${ ui.message("coreapps.patientDashBoard.encounters")} </h4>

<div ng-repeat="encounter in visit.encounters | filter:{voided:false}">
    <encounter encounter="encounter" encounter-date-format="encounterDateFormat"></encounter>
</div>