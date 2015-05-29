<div class="visit-element">
    <div class="header">
        <span class="one-third">
            <i class="icon-medical"></i>
            <span class="title">Allergies</span>
        </span>
        <span class="details">
            <current-allergies patient="visit.patient"></current-allergies>
        </span>
        <span class="overall-actions">
            <a ng-click="goToPage('allergyui', 'allergies', { patientId: visit.patient.uuid })"><i class="icon-pencil"></i></a>
        </span>
    </div>
</div>
