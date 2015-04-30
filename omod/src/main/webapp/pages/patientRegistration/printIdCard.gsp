<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeFragment("appui", "standardEmrIncludes")
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("mirebalais", "patientRegistration/printIdCard.js")
%>

<style>
    #print-id-card-app {
        width: 100%;
        text-align: center;
        padding-top: 50px;
        font-weight:bold;
    }
    #scan-card-image-section {
        padding-top: 10px;
        padding-bottom: 10px;
    }
    #scan-patient-identifier {
        height:30px;
        width:350px;
        font-size: 25px;
    }
    #scan-action-instruction-section {
        padding-top:50px;
        padding-bottom:50px;
    }
</style>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }

<div id="print-id-card-app" ng-controller="PrintIdCardCtrl" ng-init="init(${patient.patientId}, ${locationId}, '${returnUrl}')">

    <div id="printing-id-card-section" ng-show="printingInProgress">

        ${ ui.message("zl.registration.patient.idcard.printing") }...

    </div>

    <div id="confirm-id-card-section" ng-hide="printingInProgress">

        ${ ui.message("zl.registration.patient.idcard.scanToProceed") }...

        <div id="scan-card-image-section">
            <img src="${ui.resourceLink("mirebalais", "images/scanCard.png")}">
        </div>

        <input id="scan-patient-identifier" autocomplete="off" value="" autofocus="true" ng-model="scannedIdentifier" ng-enter="recordSuccessfulPrintAttempt()"/>

        <div>

            <div id="scan-action-instruction-section">
                ${ui.message("zl.registration.patient.idcard.notPrintedInstructions")}
            </div>

            <div>
                <button id="broken-printer-button" ng-click="recordFailedPrintAttempt()">
                    ${ui.message("zl.registration.patient.idcard.recordFailedPrinting")}
                </button>

                <button id="reprint-card-button" ng-click="printIdCard()">
                    ${ui.message("zl.registration.patient.idcard.tryPrintingAgain")}
                </button>
            </div>

        </div>

    </div>

</div>

<script type="text/javascript">
    angular.bootstrap('#print-id-card-app', [ 'printIdCard' ]);
</script>