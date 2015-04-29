<div class="header">
    <span class="one-third">
        <span class="title">{{ encounterStub.encounterType | omrs.display }}</span>
        <span>{{ encounterStub.encounterDatetime | date:encounterDateFormat }}</span>
    </span>
    <span class="two-thirds details">
        <span class="one-half">
            Diagnosis:
            <span ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
                {{ diag | diagnosis }}
            </span>
        </span>
        <span class="one-half">
            {{ encounter.obs | byConcept:Concepts.dispositionConstruct:true | groupMember:Concepts.disposition | obs }}
        </span>
    </span>
    <span class="actions">
        <a ng-show="canExpand()" ng-click="expand()"><i class="icon-caret-down"></i></a>
        <a ng-show="canContract()" ng-click="contract()"><i class="icon-caret-up"></i></a>
    </span>
</div>