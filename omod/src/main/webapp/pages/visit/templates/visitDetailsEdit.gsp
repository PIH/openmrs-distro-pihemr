<div class="dialog-header">
    <h3>Edit Visit Details</h3>
</div>
<div class="dialog-content">
    <table>
        <thead>
        <tr>
            <th></th>
            <th>Old</th>
            <th>New</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>Start</td>
            <td>{{ visit.startDatetime | serverDate }}</td>
            <td>
                <date-with-popup ng-model="newStartDatetime" max-date="newStopDatetime || now"></date-with-popup>
                {{ newStartDatetime | serverDate:"hh:mm a" }} TODO
            </td>
        </tr>
        <tr>
            <td>End</td>
            <td>{{ visit.stopDatetime | serverDate }}</td>
            <td>
                <date-with-popup type="text" size="20" ng-model="newStopDatetime" min-date="newStartDatetime" max-date="now"></date-with-popup>
                {{ newStopDatetime | serverDate:"hh:mm a" }} TODO
            </td>
        </tr>
        <tr>
            <td>Location</td>
            <td>{{ visit.location | omrs.display }}</td>
            <td>TODO</td>
        </tr>
        </tbody>
    </table>
    <br/>
    <div>
        <button class="confirm right" ng-click="confirm({ start: newStartDatetime, stop: newStopDatetime, location: newLocation  })">${ ui.message("uicommons.save") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>
