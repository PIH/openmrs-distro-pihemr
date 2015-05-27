<div ng-repeat="encounter in visit.encounters|filter:{voided:false}|with:'encounterType':EncounterTypes.consultationPlan">
    <encounter encounter="encounter"></encounter>
</div>

<div class="new-encounter-button">
    <a ng-hide="hasDraftOrders()" class="button" ui-sref="drugOrders">
        <i class="icon-list-ol"></i>
        Plan
    </a>
    <a ng-show="hasDraftOrders()" class="button" ui-sref="drugOrders">
        <i class="icon-list-ol"></i>
        Plan <strong>*Unsaved Draft*</strong>
    </a>
</div>