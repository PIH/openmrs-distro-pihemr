<%
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "services/encounterService.js")
    ui.includeJavascript("mirebalais", "deathcertificate/headerLink.js")
%>
<span id="death-certificate-info"
      ng-controller="DeathCertificateCtrl"
      ng-init="init('${ config.contextModel.patient.uuid }', '${ ui.escapeJs(ui.format(patient.patient)) }')">
    <span ng-show="existing.loading">
        ${ ui.message("uicommons.loading.placeholder") }
    </span>
    <span ng-show="existing.uuid">
        <a ng-click="viewDeathCertificate(existing.uuid)" style="cursor:pointer">
            <i class="icon-file small"></i> ${ ui.message("mirebalais.deathCertificate.view.label") }
        </a>
    </span>
    <span ng-show="existing === null && !${ ui.thisUrl().contains("deathCertificate.xml") }">
        <a ng-click="fillOutDeathCertificate()" style="cursor:pointer">
            <i class="icon-plus small"></i> ${ ui.message("mirebalais.deathCertificate.enter.label") }
        </a>
    </span>
</span>

<script type="text/javascript">
    // manually bootstrap angular app, in case there are multiple angular apps on a page
    angular.bootstrap('#death-certificate-info', ['deathCertificate']);
</script>
