<div class="col2">
    BP:
    {{ encounter.obs | byConcept:Concepts.systolicBloodPressure:true | obs:"value" }}
    /
    {{ encounter.obs | byConcept:Concepts.diastolicBloodPressure:true | obs:"value" }}
</div>

<div class="col2">
    {{ encounter.obs | byConcept:Concepts.temperature:true | obs }}
</div>