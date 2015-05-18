<fieldset>
    <legend>Active Prescriptions ({{ orderContext.careSetting | omrs.display }})</legend>

    <span ng-hide="activeDrugOrders.\$resolved">${ ui.message("uicommons.loading.placeholder") }</span>

    <span ng-show="activeDrugOrders.\$resolved && activeDrugOrders.length == 0">None</span>

    <table ng-show="activeDrugOrders.\$resolved">
        <tr ng-repeat="order in activeDrugOrders">
            <td ng-class="{ 'will-replace': replacementFor(order) }">
                {{ order | orderDates }}
            </td>
            <td ng-class="{ 'will-replace': replacementFor(order) }">
                {{ order | orderInstructions }}
            </td>
            <td ng-if="showActions" class="actions">
                <a ng-show="!replacementFor(order)" ng-click="reviseOrder(order)">
                    <i class="icon-pencil edit-action"></i>
                </a>
                <a ng-show="!replacementFor(order)" ng-click="discontinueOrder(order)">
                    <i class="icon-remove delete-action"></i>
                </a>
                <span ng-show="replacementFor(order)">
                    will {{ replacementFor(order).action }}
                </span>
            </td>
        </tr>
    </table>

</fieldset>