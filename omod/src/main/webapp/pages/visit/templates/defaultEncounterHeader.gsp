<span class="one-third">
    <span class="title">{{ encounterStub.encounterType | omrs.display }}</span>
    <span>{{ encounterStub.encounterDatetime | date:dateFormat }}</span>
</span>
<span class="encounter-location">{{ encounterStub.location | omrs.display }}</span>
<span class="encounter-provider">{{ encounterStub.provider | omrs.display }}</span>
<span class="actions">
    <a ng-show="canExpand()" ng-click="expand()"><i class="icon-caret-down"></i></a>
    <a ng-show="canContract()" ng-click="contract()"><i class="icon-caret-up"></i></a>
</span>