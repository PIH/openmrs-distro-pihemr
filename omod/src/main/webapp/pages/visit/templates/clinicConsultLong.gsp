Diagnoses:
<div ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct">
    {{ diag | obs:"value" }}
</div>

{{ encounter.obs | byConcept:Concepts.dispositionConstruct:true | groupMember:Concepts.disposition | obs }}