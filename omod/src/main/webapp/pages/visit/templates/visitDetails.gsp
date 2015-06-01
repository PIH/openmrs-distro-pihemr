<span class="visit-dates">
    <span>
        <i class="icon-time small"></i>
        {{ visit.startDatetime | serverDate:dateFormat }}
        <span ng-show="visit.stopDatetime">- {{ visit.stopDatetime | serverDate:dateFormat }}</span>
        <span ng-hide="visit.stopDatetime" class="lozenge active">
            active
        </span>
    </span>
</span>

<span class="visit-location">
    <i class="icon-hospital small"></i>
    {{ visit.location | omrs.display }}
</span>

<span class="actions">
    <i class="icon-pencil edit-action" ng-click="edit()" ng-hide="editing"></i>
</span>