<div ng-repeat="e in encounterStubs">
    <encounter encounter="e" encounterDateFormat="encounterDateFormat"></encounter>
</div>

<div class="new-encounter-button" ng-class="{'add-multiple-encounter': encounterStubs.length}" ng-show="canAdd" ng-include="'templates/action.page'">
</div>