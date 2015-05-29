<span>{{encounter.encounterDatetime | serverDate:encounterDateFormat}}</span>
<span ng-hide="encounter.location.uuid == encounter.visit.location.uuid">at {{encounter.location | omrs.display}}</span>
<span ng-hide="encounter.encounterDatetime == encounter.auditInfo.dateCreated">
    * Entered {{encounter.auditInfo.dateCreated | serverDate:encounterDateFormat}}
</span>
<span>
    * Entered by {{ encounter.auditInfo.creator | omrs.display }}
</span>
<span ng-show="encounter.auditInfo.dateChanged">
    * Edited {{encounter.auditInfo.dateChanged | serverDate:encounterDateFormat}}
</span>