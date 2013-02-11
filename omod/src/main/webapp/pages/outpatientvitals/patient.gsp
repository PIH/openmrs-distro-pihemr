<%
    ui.decorateWith("emr", "standardEmrPage")
%>

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

<% if (emrContext.activeVisitSummary) { %>

    <div class="container half-width">

        <h1>Is this the right patient?</h1>

        <div id="actions">
            <button class="confirm big right">
                <i class="icon-arrow-right"></i>
                Yes, Record Vitals
            </button>

            <button class="cancel big">
                <i class="icon-arrow-left"></i>
                No, Find Another Patient
            </button>
        </div>

        <% if (existingEncounters) { %>
            <h3>Vitals recorded this visit</h3>
            <table>
                <thead>
                    <tr>
                        <th>When</th>
                        <th>Entered by</th>
                    </tr>
                </thead>
                <tbody>
                    <% existingEncounters.each { enc ->
                        def minutesAgo = (long) ((System.currentTimeMillis() - enc.encounterDatetime.time) / 1000 / 60)
                    %>
                        <tr>
                            <td>${ minutesAgo } minute(s) ago</td>
                            <td>${ ui.format(enc.creator) }</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } %>
    </div>

<% } else { %>

    <h1>
        This patient is not checked in.
    </h1>

    <div id="actions">
        <button class="cancel big">
            <i class="icon-arrow-left"></i>
            Find Another Patient
        </button>
    </div>

<% } %>