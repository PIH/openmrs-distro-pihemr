<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("mirebalais.checkin.title") }", link: "${ ui.pageLink("mirebalais", "checkin/findPatient", [formUrl: formUrl] )}" },
        { label: "${ ui.format(patient.patient.familyName) }, ${ ui.format(patient.patient.givenName) }" , link: '${ui.pageLink("coreapps", "patientdashboard/patientDashboard", [patientId: patient.id])}'},
    ];
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<script type="text/javascript">
    jq(function() {

        jq('#actions .cancel').click(function() {
            emr.navigateTo({
                provider: "mirebalais",
                page: "checkin/findPatient"
            });
        });
        jq('#actions .confirm').click(function() {
            var id = jq(this).attr('id');
            if(id == 'addCheckIn'){
                //add check-in encounter to the active visit
                emr.navigateTo({
                    applicationUrl: '${ enterFormUrl }'
                });
            }else{
                // create new visit and add check-in encounter to the new visit
                emr.navigateTo({
                    applicationUrl: '${ enterFormUrl }' + "&createVisit=true"
                });
            }

        });
        jq('#actions button').first().focus();
    });
</script>

<% if (!emrContext.activeVisit) { %>

<div class="dialog no-overlay">
    <div class="dialog-header">
        <h3>
            <i class="icon-question-sign"></i>
            ${ ui.message("mirebalais.outpatientVitals.confirmPatientQuestion") }
        </h3>
    </div>
    <div class="dialog-content">
        <div id="actions">
            <button class="confirm medium right">
                <i class="icon-arrow-right"></i>
                ${ ui.message("mirebalais.checkin.confirm.yes") }
            </button>

            <button class="cancel medium">
                <i class="icon-arrow-left"></i>
                ${ ui.message("mirebalais.checkin.confirm.no") }
            </button>
        </div>
    </div>
</div>

<% } else { %>

    <%= ui.includeFragment("emr", "widget/note", [
            noteType: "warning",
            message: ui.message("mirebalais.checkin.newVisit"),
            additionalContent: """
                <div id="actions">
                    <button id="addCheckIn" class="confirm medium right">
                        <i class="icon-arrow-right"></i>
                        ${ ui.message("emr.yesContinue") }
                    </button>

                    <button class="cancel medium">
                        <i class="icon-arrow-left"></i>
                        ${ ui.message("mirebalais.checkin.confirm.no") }
                    </button>
                </div>
            """
    ]) %>

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