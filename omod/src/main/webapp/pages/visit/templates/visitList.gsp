<table id="visit-list">
    <tr>
        <th>${ ui.message("uicommons.start") }</th>
        <th>${ ui.message("uicommons.end") }</th>
        <th>${ ui.message("uicommons.location") }</th>
    </tr>

    <tr ng-repeat="v in visits" ng-click="goToVisit(v)" class="selectable" ng-class="{ 'selected-visit': v.uuid===visit.uuid, active: !v.stopDatetime }">
        <td>
            {{ v.startDatetime | serverDate }}
        </td>
        <td>
            {{ v.stopDatetime | serverDate }}
            <span ng-hide="v.stopDatetime">
                (Active)
            </span>
        </td>
        <td>
            @ {{ v.location | omrs.display }}
        </td>
    </tr>
</table>