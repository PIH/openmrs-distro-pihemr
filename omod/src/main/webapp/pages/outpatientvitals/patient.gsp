<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("mirebalais.outpatientVitals.title") }", link: "${ ui.pageLink("mirebalais", "outpatientvitals/findPatient") }" },
        { label: "${ ui.format(patient.patient.familyName) }, ${ ui.format(patient.patient.givenName) }" , link: '${ui.pageLink("emr", "patient", [patientId: patient.id])}'},
    ];
</script>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient.patient ]) }

<script type="text/javascript">
    jq(function() {
        jq('#actions .cancel').click(function() {
            emr.navigateTo({
                provider: "mirebalais",
                page: "outpatientvitals/findPatient"
            });
        });
        jq('#actions .confirm').click(function() {
            emr.navigateTo({
                applicationUrl: '${ enterFormUrl }'
            });
        }).focus();
    });
</script>
<style>
    #existing-encounters {
        margin-top: 2em;
    }
</style>

<% if (emrContext.activeVisitSummary) { %>

    <div class="container half-width">

        <h1>${ ui.message("mirebalais.outpatientVitals.confirmPatientQuestion") }</h1>

        <div id="actions">
            <button class="confirm big right">
                <i class="icon-arrow-right"></i>
                ${ ui.message("mirebalais.outpatientVitals.confirm.yes") }
            </button>

            <button class="cancel big">
                <i class="icon-arrow-left"></i>
                ${ ui.message("mirebalais.outpatientVitals.confirm.no") }
            </button>
        </div>

        <% if (existingEncounters) { %>
            <div id="existing-encounters">
                <h3>${ ui.message("mirebalais.outpatientVitals.vitalsThisVisit") }</h3>
                <table>
                    <thead>
                        <tr>
                            <th>${ ui.message("mirebalais.outpatientVitals.when") }</th>
                            <th>${ ui.message("mirebalais.outpatientVitals.enteredBy") }</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% existingEncounters.each { enc ->
                            def minutesAgo = (long) ((System.currentTimeMillis() - enc.encounterDatetime.time) / 1000 / 60)
                        %>
                            <tr>
                                <td>${ ui.message("mirebalais.outpatientVitals.minutesAgo", minutesAgo) }</td>
                                <td>${ ui.format(enc.creator) }</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>
    </div>

<% } else { %>

    <h1>
        ${ ui.message("mirebalias.outpatientVitals.noVisit") }
    </h1>

    <div id="actions">
        <button class="cancel big">
            <i class="icon-arrow-left"></i>
            ${ ui.message("mirebalias.outpatientVitals.noVisit.findAnotherPatient") }
        </button>
    </div>

<% } %>