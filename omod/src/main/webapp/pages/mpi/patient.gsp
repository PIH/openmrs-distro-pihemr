<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("mirebalais.mpi.title") }", link: "${ ui.pageLink("mirebalais", "mpi/findPatient") }" },
        { label: "${ ui.format(patient.patient.familyName) }, ${ ui.format(patient.patient.givenName) }" , link: '${ui.pageLink("emr", "patient", [patientId: patient.id])}'},
    ];
</script>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient.patient ]) }

<script type="text/javascript">
    jq(function() {
        jq('#actions .cancel').click(function() {
            emr.navigateTo({
                provider: "mirebalais",
                page: "mpi/findPatient"
            });
        });

        jq('#actions button').first().focus();
    });
</script>



<div class="container half-width">

    <h1>${ ui.message("mirebalais.outpatientVitals.confirmPatientQuestion") }</h1>

    <div id="actions">
        <button class="confirm big right">
            <i class="icon-arrow-right"></i>
            ${ ui.message("mirebalais.mpi.confirm.yes") }
        </button>

        <button class="cancel big">
            <i class="icon-arrow-left"></i>
            ${ ui.message("mirebalais.checkin.confirm.no") }
        </button>
    </div>
</div>




