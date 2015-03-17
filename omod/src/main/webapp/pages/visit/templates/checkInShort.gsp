<div class="header">
    <span class="one-third">
        <span class="title">{{ encounterStub.encounterType | omrs.display }}</span>
        <span>{{ encounterStub.encounterDatetime | date:dateFormat }}</span>
    </span>
    <span class="two-thirds details">
        <span class="one-half">{{ encounter.obs | byConcept:Concepts.typeOfVisit:true | obs }}</span>
        <span class="one-half">{{ encounter.obs | byConcept:Concepts.paymentInformation:true | obs }}</span>
    </span>
    <span class="actions">
        <a ng-show="canExpand()" ng-click="expand()"><i class="icon-caret-down"></i></a>
        <a ng-show="canContract()" ng-click="contract()"><i class="icon-caret-up"></i></a>
    </span>
</div>