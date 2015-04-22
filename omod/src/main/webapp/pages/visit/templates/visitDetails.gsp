<span class="visit-type">
    {{ visitTemplate.label | omrs.display }}
</span>

<span class="visit-dates">
    <span ng-hide="editing">
        {{ visit.startDatetime | date:"medium" }}
        <span ng-show="visit.stopDatetime">- {{ visit.stopDatetime | date:"medium" }}</span>
        <em ng-hide="visit.stopDatetime">...ongoing</em>
    </span>
    <span ng-show="editing">
        Start:
        <input type="text" size="20" ng-model="newStartDatetime"/>

        Stop:
        <input type="text" size="20" ng-model="newStopDatetime"/>
    </span>
</span>

<span class="visit-location">{{ visit.location | omrs.display }}</span>

<span class="actions">
    <i class="icon-pencil edit-action" ng-click="startEditing()" ng-hide="editing"></i>
    <i class="icon-save edit-action" ng-click="apply()" ng-show="editing"></i>
    <i class="icon-remove delete-action" ng-click="cancel()" ng-show="editing"></i>
</span>