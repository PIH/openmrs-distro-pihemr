<span class="encounter-type">{{ encounterStub.encounterType | omrs.display }}</span>
<span class="encounter-datetime">{{ encounterStub.encounterDatetime | date:'medium' }}</span>
<span class="encounter-location">{{ encounterStub.location | omrs.display }}</span>
<span class="encounter-provider">{{ encounterStub.provider | omrs.display }}</span>
<span class="actions">
    <a ng-show="canExpand()" ng-click="expand()"><i class="icon-caret-down"></i></a>
    <a ng-show="canContract()" ng-click="contract()"><i class="icon-caret-up"></i></a>
</span>