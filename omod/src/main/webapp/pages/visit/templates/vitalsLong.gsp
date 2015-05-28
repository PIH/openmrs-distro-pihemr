<div class="header" ng-include="'templates/defaultEncounterHeader.page'">
</div>

<div class="content">
    <p class="aligned">
        <label>Height:</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.height:true | obs:"value" }}</span>
    </p>
    <p class="aligned">
        <label>Weight:</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.weight:true | obs:"value" }}</span>
    </p>
    <p class="aligned">
        <label>Temperature:</label>
            <span class="value">{{ encounter.obs | byConcept:Concepts.temperature:true | obs:"value" }}</span>
    </p>
    <p class="aligned">
        <label>Heart Rate:</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.heartRate:true | obs:"value" }}</span>
    </p>
    <p class="aligned">
        <label>Respiratory Rate:</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.respiratoryRate:true | obs:"value" }}</span>
    </p>
    <p class="aligned">
        <label>Blood Pressure:</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.systolicBloodPressure:true | obs:"value" }} / {{ encounter.obs | byConcept:Concepts.diastolicBloodPressure:true | obs:"value" }}</span>
    </p>
    <p class="aligned">
        <label>O2 Sat:</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.oxygenSaturation:true | obs:"value" }}</span>
    </p>
</div>