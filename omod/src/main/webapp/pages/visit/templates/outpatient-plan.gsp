<div ng-repeat="encounter in visit.encounters|with:'encounterType':EncounterTypes.consultationPlan">
    {{ encounter.encounterType | omrs.display }}
    {{ encounter.encounterDatetime | date:"medium" }}
</div>