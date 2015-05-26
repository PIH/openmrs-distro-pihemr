<span class="visit-dates">
    <span ng-hide="editing">
        <i class="icon-time small"></i>
        {{ visit.startDatetime | serverDate:"medium" }}
        <span ng-show="visit.stopDatetime">- {{ visit.stopDatetime | serverDate:"medium" }}</span>
        <em ng-hide="visit.stopDatetime">...ongoing</em>
    </span>
    <span ng-show="editing">
        <h3>This is broken</h3>
        <br/>
        Start:
        <br/>
        <date-with-popup ng-model="newStartDatetime" max-date="newStopDatetime || now"></date-with-popup>
        <timepicker ng-model="newStartDatetime"></timepicker>

        <br/>
        Stop:
        <br/>
        <date-with-popup type="text" size="20" ng-model="newStopDatetime" min-date="newStartDatetime" max-date="now"></date-with-popup>
        <timepicker ng-model="newStopDatetime"></timepicker>
    </span>
</span>

<span class="visit-location">
    <i class="icon-hospital small"></i>
    {{ visit.location | omrs.display }}
</span>

<span class="actions">
    <i class="icon-pencil edit-action" ng-click="startEditing()" ng-hide="editing"></i>
    <i class="icon-save edit-action" ng-click="apply()" ng-show="editing"></i>
    <i class="icon-remove delete-action" ng-click="cancel()" ng-show="editing"></i>
</span>