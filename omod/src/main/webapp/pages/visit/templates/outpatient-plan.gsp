<fieldset ng-show="isLatestVisit">
    <legend>Active Orders</legend>
    <active-orders/>
</fieldset>

<fieldset>
    <legend>Order Sheet (this visit)</legend>
    <order-sheet visit="visit"></order-sheet>
</fieldset>

<div ng-repeat="encounter in visit.encounters|with:'encounterType':EncounterTypes.consultationPlan">
    {{ encounter.encounterType | omrs.display }}
    {{ encounter.encounterDatetime | date:"medium" }}
</div>