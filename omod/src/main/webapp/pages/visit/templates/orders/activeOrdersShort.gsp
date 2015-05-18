<fieldset>
    <legend>Active Prescriptions ({{ orderContext.careSetting | omrs.display }})</legend>

    <span ng-hide="activeDrugOrders.\$resolved">${ ui.message("uicommons.loading.placeholder") }</span>

    <span ng-show="activeDrugOrders.\$resolved && activeDrugOrders.length == 0">None</span>

    <span ng-show="activeDrugOrders.\$resolved && activeDrugOrders.length > 0">
        {{ activeOrderConcepts() | omrs.display }}
    </span>

    <a ng-hide="state == 'long'" ng-click="expand()">Expand</a>

</fieldset>