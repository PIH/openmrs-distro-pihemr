<div class="header" ng-include="'templates/defaultEncounterHeader.page'">
</div>

<div class="content">
    <p class="aligned">
        <label>${ ui.message("mirebalais.vitals.height.title") }</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.height:true | obs:"value" }}${ ui.message("emr.units.centimeters") }</span>
    </p>
    <p class="aligned">
        <label>${ ui.message("mirebalais.vitals.weight.title") }</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.weight:true | obs:"value" }}${ ui.message("emr.units.kilograms") }</span>
    </p>
    <p class="aligned">
        <label>${ ui.message("mirebalais.vitals.temperature.title") }</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.temperature:true | obs:"value" }}${ ui.message("emr.units.degreesCelsius") }</span>
    </p>
    <p class="aligned">
        <label>${ ui.message("mirebalais.vitals.heartRate.title") }</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.heartRate:true | obs:"value" }}${ ui.message("emr.units.perMinute") }</span>
    </p>
    <p class="aligned">
        <label>${ ui.message("mirebalais.vitals.respiratoryRate.title") }</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.respiratoryRate:true | obs:"value" }}${ ui.message("emr.units.perMinute") }</span>
    </p>
    <p class="aligned">
        <label>${ ui.message("mirebalais.vitals.bloodPressure.title") }</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.systolicBloodPressure:true | obs:"value" }} / {{ encounter.obs | byConcept:Concepts.diastolicBloodPressure:true | obs:"value" }}</span>
    </p>
    <p class="aligned">
        <label>${ ui.message("mirebalais.vitals.o2sat.title") }</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.oxygenSaturation:true | obs:"value" }}${ ui.message("emr.units.percent") }</span>
    </p>
</div>