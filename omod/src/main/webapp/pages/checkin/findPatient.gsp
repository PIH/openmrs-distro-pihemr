<%
    if (emrContext.authenticated && !emrContext.currentProvider) {
        throw new IllegalStateException("Logged-in user is not a Provider")
    }

    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("mirebalais.app.patientRegistration.checkin.label") }", link: "${ ui.pageLink("mirebalais", "checkin/findPatient") }" }
    ];
</script>

<script type="text/javascript">
    var pullChartDialog = null;
    var createPaperRecordDialog = null;

    function initPullChartDialog(patientId) {
        pullChartDialog = emr.setupConfirmationDialog({
            selector: '#request-paper-record-dialog',
            actions: {
                confirm: function() {
                    pullChartDialog.close();
                    emr.getFragmentActionWithCallback('paperrecord', 'requestPaperRecord', 'requestPaperRecord'
                            , { patientId: patientId, locationId: sessionLocationModel.id() }
                            , function(data) {
                                emr.successMessage(data.message);
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

    function initCreatePaperRecordDialog(patientId) {
        createPaperRecordDialog = emr.setupConfirmationDialog({
            selector: '#create-paper-record-dialog',
            actions: {
                confirm: function() {
                    createPaperRecordDialog.close();
                    emr.getFragmentActionWithCallback('paperrecord', 'requestPaperRecord', 'createPaperRecord'
                            , { patientId: patientId, locationId: sessionLocationModel.id() }
                            , function(data) {
                                emr.successMessage(data.message);
                                jq(':input:enabled:visible:first').focus();
                            });
                },
                cancel: function() {
                    createPaperRecordDialog.close();
                    emr.getFragmentActionWithCallback('paperrecord', 'requestPaperRecord', 'requestPaperRecord'
                            , { patientId: patientId, locationId: sessionLocationModel.id() }
                            , function(data) {
                                emr.successMessage(data.message);
                                jq(':input:enabled:visible:first').focus();
                            });
                }
            }
        });
    }

    jq(function() {
     jq(':input:enabled:visible:first').focus();
     if('${ pullPaperRecord }' == 'true'){
         initPullChartDialog('${patient.id}');
         ko.applyBindings( sessionLocationModel, jq('#request-paper-record-dialog').get(0) );
         pullChartDialog.show();
     } else if('${ createPaperRecord }' == 'true'){
         initCreatePaperRecordDialog('${patient.id}');
         ko.applyBindings( sessionLocationModel, jq('#create-paper-record-dialog').get(0) );
         createPaperRecordDialog.show();
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

        <button class="confirm right">${ ui.message("emr.yes") }</button>
        <button class="cancel">${ ui.message("emr.no") }</button>
    </div>
</div>
<%} else if(createPaperRecord) { %>
    <div id="create-paper-record-dialog" class="dialog" style="display: none">
        <div class="dialog-header">
            <i class="icon-folder-open"></i>
            <h3>${ ui.message("emr.patientDashBoard.createPaperRecord.title") }</h3>
        </div>
        <div class="dialog-content">
            <p class="dialog-instructions">${ ui.message("emr.patientDashBoard.createPaperRecord.where") }</p>

            <button class="confirm right no-color">${ ui.format(sessionContext.sessionLocation) }</button>
            <button class="cancel no-color">${ ui.message("ui.i18n.Location.name.be50d584-26b2-4371-8768-2b9565742b3b") }</button>
        </div>
    </div>
<%} %>


${ ui.message("mirebalais.searchPatientHeading") }
${ ui.includeFragment("coreapps", "patientsearch/patientSearchWidget",
        [ afterSelectedUrl: '/mirebalais/checkin/patient.page?patientId={{patientId}}',
                showLastViewedPatients: 'false' ])}







