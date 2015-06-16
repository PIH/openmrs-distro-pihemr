<%
    if (emrContext.authenticated && !emrContext.currentProvider) {
        throw new IllegalStateException("Logged-in user is not a Provider")
    }

    ui.decorateWith("appui", "standardEmrPage")
%>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }

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
                            });
                    setTimeout(navigateBackToSearch, 3000);  // set a delay so that the toast message has time to display before the redirect
                },
                cancel: function() {
                    pullChartDialog.close();
                    navigateBackToSearch();
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
                            });
                   setTimeout(navigateBackToSearch, 3000);   // set a delay so that the toast message has time to display before the redirect
                },
                cancel: function() {
                    createPaperRecordDialog.close();
                    emr.getFragmentActionWithCallback('paperrecord', 'requestPaperRecord', 'requestPaperRecord'
                            , { patientId: patientId, locationId: sessionLocationModel.id() }
                            , function(data) {
                                emr.successMessage(data.message);
                            });
                    setTimeout(navigateBackToSearch, 3000);   // set a delay so that the toast message has time to display before the redirect
                }
            }
        });
    }

    <% if (!redirectToEmergency) { %>
        function navigateBackToSearch() {

            emr.navigateTo({
                provider: "coreapps",
                page: "findpatient/findPatient",
                query: {
                    app: "mirebalais.liveCheckin"
                }
            });

        }
    <% } else { // this redirects back the old patient registration app if we came here via the ed check-in flow%>
        function navigateBackToSearch() {

            emr.navigateTo({
                provider: "mirebalais",
                page: "patientRegistration/appRouter",
                query: {
                    task: "edCheckIn"
                }
            });
        }
    <% } %>

    jq(function() {
        jq(':input:enabled:visible:first').focus();
        <% if (!needToCreateRecord) { %>
            initPullChartDialog('${patient.id}');
            ko.applyBindings( sessionLocationModel, jq('#request-paper-record-dialog').get(0) );
            pullChartDialog.show();
        <% } else { %>
            initCreatePaperRecordDialog('${patient.id}');
            ko.applyBindings( sessionLocationModel, jq('#create-paper-record-dialog').get(0) );
            createPaperRecordDialog.show();
        <% } %>

    });
</script>


<% if(!needToCreateRecord) {%>
<div id="request-paper-record-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <i class="icon-folder-open"></i>
        <h3>${ ui.message("paperrecord.patientDashBoard.requestPaperRecord.title") }</h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">${ ui.message("paperrecord.patientDashBoard.requestPaperRecord.confirmTitle") }</p>
        <ul>
            <li class="info">
                <span>${ ui.message("uicommons.patient") }</span>
                <h5>${ ui.format(patient) }</h5>
            </li>
            <li class="info">
                <span>${ ui.message("uicommons.location") }</span>
                <h5 data-bind="text: text"></h5>
            </li>
        </ul>

        <button class="confirm right">${ ui.message("uicommons.yes") }</button>
        <button class="cancel">${ ui.message("uicommons.no") }</button>
    </div>
</div>
<%} else { // if not pull, then create %>
<div id="create-paper-record-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <i class="icon-folder-open"></i>
        <h3>${ ui.message("paperrecord.patientDashBoard.createPaperRecord.title") }</h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">${ ui.message("paperrecord.patientDashBoard.createPaperRecord.where") }</p>

        <button class="confirm right no-color">${ ui.format(sessionContext.sessionLocation) }</button>
        <button class="cancel no-color">${ ui.format(associatedArchivesLocation) }</button>
    </div>
</div>
<%} %>




