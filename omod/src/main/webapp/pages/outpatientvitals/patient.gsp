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
        });
    });
</script>

<% if (emrContext.activeVisitSummary) { %>

    <div id="actions">
        <button class="confirm big">
            <i class="icon-tasks"></i>
            Record Vitals
        </button>

        <button class="cancel big">
            <i class="icon-repeat"></i>
            Find Another Patient
        </button>
    </div>

    <% if (existingEncounters) { %>
        Recently entered
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

<% } else { %>

    This patient is not checked in.

    <div id="actions">
        <button class="cancel big">
            <i class="icon-repeat"></i>
            Find Another Patient
        </button>
    </div>

<% } %>