<fieldset>
    <legend>Order Sheet (this visit)</legend>
    <order-sheet visit="visit" date-format="encounterDateFormat"></order-sheet>
</fieldset>

<div ng-if="isLatestVisit">
    <a class="button" ui-sref="drugOrders">Review and Place Drug Orders</a>
    <strong ng-show="hasDraftOrders()">*Unsaved Draft Orders*</strong>
</div>

<div ng-repeat="encounter in visit.encounters|with:'encounterType':EncounterTypes.consultationPlan">
    {{ encounter.encounterType | omrs.display }}
    {{ encounter.encounterDatetime | date:"medium" }}
</div>