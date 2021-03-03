<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("mirebalais.outpatientVitals.title") }",
          link: "${ ui.pageLink("coreapps", "findpatient/findPatient", [ 'app': 'pih.uhm.app.vitals' ] ) }"
        },
        { label: "${ ui.escapeJs(ui.format(patient.patient)) }" ,
          link: '${ui.pageLink("coreapps", "patientdashboard/patientDashboard", [patientId: patient.id])}'}
    ];
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<script type="text/javascript">
    jq(function() {
        jq('#actions .cancel').click(function() {
            emr.navigateTo({
                provider: "coreapps",
                page: "findpatient/findPatient",
                query: { 'app': 'pih.uhm.app.vitals' }
            });
        });
        jq('#actions .confirm').click(function() {
            emr.navigateTo({
                provider: "htmlformentryui",
                page: "htmlform/enterHtmlFormWithSimpleUi",
                query: {
                    patientId: "${ patient.id }",
                    visitId: "${ visit?.id }",
                    definitionUiResource: "file:configuration/pih/htmlforms/vitals.xml",
                    returnUrl: "${ ui.escapeJs(ui.pageLink("coreapps", "findpatient/findPatient", [ 'app': appName ])) }",
                    breadcrumbOverride: "${ ui.escapeJs(breadcrumbOverride) }"
                }
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

<% if (emrContext.activeVisit) { %>

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

        <div id="existing-encounters">
            <h3>${ ui.message("mirebalais.outpatientVitals.vitalsThisVisit") }</h3>
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
                            <td colspan="3">${ ui.message("coreapps.none") }</td>
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
