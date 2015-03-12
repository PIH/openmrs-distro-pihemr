<div class="col2">
    {{ encounter.obs | byConcept:Concepts.typeOfVisit:true | obs }}
</div>

<div class="col2">
    {{ encounter.obs | byConcept:Concepts.paymentInformation:true | obs }}
</div>