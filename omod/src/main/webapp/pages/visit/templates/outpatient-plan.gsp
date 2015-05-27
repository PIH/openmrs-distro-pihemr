<div ng-repeat="encounter in visit.encounters|with:'encounterType':EncounterTypes.consultationPlan">
    <encounter encounter="encounter"></encounter>
</div>

<div class="new-encounter-button">
    <a ng-hide="hasDraftOrders()" class="button" ui-sref="drugOrders">
        Plan
    </a>
    <a ng-show="hasDraftOrders()" class="button" ui-sref="drugOrders">
        Plan <strong>*Unsaved Draft*</strong>
    </a>
</div>