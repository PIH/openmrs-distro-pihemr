<div class="visit-actions" ng-class="{ 'past-visit': visit.stopDatetime }">

    <p ng-show="visit.stopDatetime" class="label">
        <i class="icon-warning-sign small"></i>
        ${ ui.message("coreapps.patientDashboard.actionsForInactiveVisit") }
    </p>

    <a ng-repeat="action in visitActions | allowedWithContext" class="button task" ng-click="visitAction(action)">
        <i class="{{ action.icon }}"></i>
        {{ action.label | omrs.display }}
    </a>
</div>