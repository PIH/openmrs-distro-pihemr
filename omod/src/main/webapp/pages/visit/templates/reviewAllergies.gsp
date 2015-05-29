<div class="visit-element">
    <div class="header">
        <span>
            <i class="icon-medical"></i>
            <span class="title">Allergies</span>
        </span>
        <span class="overall-actions">
            <a ng-click="goToPage('allergyui', 'allergies', { patientId: visit.patient.uuid })"><i class="icon-pencil"></i></a>
        </span>
    </div>
    <div class="content">
        <current-allergies patient="visit.patient"></current-allergies>
    </div>
</div>
