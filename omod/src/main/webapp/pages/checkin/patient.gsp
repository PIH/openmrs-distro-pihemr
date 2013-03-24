<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("mirebalais.checkin.title") }", link: "${ ui.pageLink("mirebalais", "checkin/findPatient") }" },
        { label: "${ ui.format(patient.patient.familyName) }, ${ ui.format(patient.patient.givenName) }" , link: '${ui.pageLink("emr", "patient", [patientId: patient.id])}'},
    ];
</script>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient.patient ]) }

<script type="text/javascript">
    jq(function() {
        jq('#actions .cancel').click(function() {
            emr.navigateTo({
                provider: "mirebalais",
                page: "checkin/findPatient"
            });
        });
        jq('#actions .confirm').click(function() {
            //start a new visit
            emr.getFragmentActionWithCallback('emr', 'visit/visit', 'start',
                    { patientId: '${ patient.patient.id }', locationId: sessionLocationModel.id(), stopActiveVisit: true },
                    function(data) {
                        emr.navigateTo({
                            applicationUrl: '${ enterFormUrl }'
                        });
                    });

        });
        jq('#actions button').first().focus();
    });
</script>
<style>
#existing-encounters {
    margin-top: 2em;
}
</style>

<% if (!emrContext.activeVisitSummary) { %>

<div class="container half-width">

    <h1>${ ui.message("mirebalais.outpatientVitals.confirmPatientQuestion") }</h1>

    <div id="actions">
        <button class="confirm big right">
            <i class="icon-arrow-right"></i>
            ${ ui.message("mirebalais.checkin.confirm.yes") }
        </button>

        <button class="cancel big">
            <i class="icon-arrow-left"></i>
            ${ ui.message("mirebalais.checkin.confirm.no") }
        </button>
    </div>
</div>

<% } else { %>

<h1>
    ${ ui.message("mirebalias.checkin.newVisit") }
</h1>


<div id="actions">
    <button class="confirm big right">
        <i class="icon-arrow-right"></i>
        ${ ui.message("mirebalais.checkin.confirm.newvisit.yes") }
    </button>

    <button class="cancel big">
        <i class="icon-arrow-left"></i>
        ${ ui.message("mirebalais.checkin.confirm.no") }
    </button>
</div>

<% } %>

<% if (existingEncounters.size() > 0) { %>

<div id="existing-encounters">
    <h3>${ ui.message("mirebalais.checkin.checkinThisVisit") }</h3>
    <table>
        <thead>
        <tr>
            <th>${ ui.message("mirebalais.outpatientVitals.when") }</th>
            <th>${ ui.message("mirebalais.outpatientVitals.where") }</th>
            <th>${ ui.message("mirebalais.outpatientVitals.enteredBy") }</th>
        </tr>
        </thead>
        <tbody>
        <% if (existingEncounters.size() == 0) { %>
        <tr>
            <td colspan="3">${ ui.message("emr.none") }</td>
        </tr>
        <% } %>
        <% existingEncounters.each { enc ->
            def minutesAgo = (long) ((System.currentTimeMillis() - enc.encounterDatetime.time) / 1000 / 60)
        %>
        <tr>
            <td>${ ui.message("mirebalais.outpatientVitals.minutesAgo", minutesAgo) }</td>
            <td>${ ui.format(enc.location) }</td>
            <td>${ ui.format(enc.creator) }</td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>
<% } %>