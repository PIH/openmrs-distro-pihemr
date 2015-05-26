<div class="header">
    <span class="one-third" ng-include="'templates/standardEncounterHeading.page'"></span>
    <span class="one-half details">
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
    <span class="overall-actions" ng-include="'templates/standardEncounterActions.page'"></span>
</div>