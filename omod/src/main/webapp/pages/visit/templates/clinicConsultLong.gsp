<div class="header" ng-include="'templates/defaultEncounterHeader.page'">
</div>

<div class="content">
    Diagnoses:
    <div ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct">
        {{ diag | obs:"value" }}
    </div>

    <div ng-show="orders">
        <h4>Orders</h4>
        <p ng-repeat="order in orders | orderBy:'dateActivated'" class="aligned">
            {{ order.dateActivated | serverDate:dateFormat }}
            {{ order | orderInstructions }}
        </p>
    </div>

    {{ encounter.obs | byConcept:Concepts.dispositionConstruct:true | groupMember:Concepts.disposition | obs }}

    <div class="book-keeping" ng-include="'templates/standardEncounterBookkeeping.page'"></div>
</div>