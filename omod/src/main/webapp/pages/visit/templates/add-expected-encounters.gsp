<div class="visit-actions" ng-class="{ 'past-visit': visit.stopDatetime }">
    <p ng-show="visit.stopDatetime" class="label">
        <i class="icon-warning-sign small"></i>
        ${ ui.message("coreapps.patientDashboard.actionsForInactiveVisit") }
    </p>

    <span ng-repeat="action in getExpectedEncounterActions()" class="new-encounter-button" ng-class="{'add-multiple-encounter': encounterStubs.length}" ng-include="'templates/action.page'">
    </span>
</div>
