<div class="header">
    <span class="one-third" ng-include="'templates/standardEncounterHeading.page'"></span>
    <span class="one-half details">
        <span class="one-half">
            BP:
            {{ encounter.obs | byConcept:Concepts.systolicBloodPressure:true | obs:"value" }}/{{ encounter.obs | byConcept:Concepts.diastolicBloodPressure:true | obs:"value" }}
        </span>
        <span class="one-half">
            {{ encounter.obs | byConcept:Concepts.temperature:true | obs }}
        </span>
    </span>
    <span class="overall-actions" ng-include="'templates/standardEncounterActions.page'"></span>
</div>