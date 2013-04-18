<%
    if (emrContext.authenticated && !emrContext.currentProvider) {
        throw new IllegalStateException("Logged-in user is not a Provider")
    }

    ui.decorateWith("emr", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("mirebalais.app.patientRegistration.checkin.label") }", link: "${ ui.pageLink("mirebalais", "checkin/findPatient") }" }
    ];
</script>

<script type="text/javascript">
    var pullChartDialog = null;

    function createPullChartDialog(patientId) {
        console.log("patientId=" + patientId);
        pullChartDialog = emr.setupConfirmationDialog({
            selector: '#request-paper-record-dialog',
            actions: {
                confirm: function() {
                    emr.getFragmentActionWithCallback('emr', 'paperrecord/requestPaperRecord', 'requestPaperRecord'
                            , { patientId: patientId, locationId: sessionLocationModel.id() }
                            , function(data) {
                                emr.successMessage(data.message);
                                pullChartDialog.close();
                                jq(':input:enabled:visible:first').focus();
                            });
                },
                cancel: function() {
                    pullChartDialog.close();
                    jq(':input:enabled:visible:first').focus();
                }
            }
        });
    }

    jq(function() {
     jq(':input:enabled:visible:first').focus();
     if('${ pullPaperRecord }' == 'true'){
         createPullChartDialog('${patient.id}');
         ko.applyBindings( sessionLocationModel, jq('#request-paper-record-dialog').get(0) );
         pullChartDialog.show();
     }

    });
</script>


<h1>
    ${ ui.message("mirebalais.checkin.title") }
</h1>

<% if(pullPaperRecord) {%>
    <div id="request-paper-record-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <i class="icon-folder-open"></i>
        <h3>${ ui.message("emr.patientDashBoard.requestPaperRecord.title") }</h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">${ ui.message("emr.patientDashBoard.requestPaperRecord.confirmTitle") }</p>
        <ul>
            <li class="info">
                <span>${ ui.message("emr.patient") }</span>
                <h5>${ ui.format(patient.patient) }</h5>
            </li>
            <li class="info">
                <span>${ ui.message("emr.location") }</span>
                <h5 data-bind="text: text"></h5>
            </li>
        </ul>

        <button class="confirm right">${ ui.message("emr.confirm") }</button>
        <button class="cancel">${ ui.message("emr.cancel") }</button>
    </div>
</div>
<%}%>

${ ui.includeFragment("emr", "widget/findPatient", [
        targetPageProvider: "mirebalais",
        targetPage: "checkin/patient"
]) }
