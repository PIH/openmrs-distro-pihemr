<div id="draft-orders">
    <h3>Drug Orders</h3>

    <p>
        <label>Add a new order:</label>
        <select-drug ng-model="newOrderForDrug" placeholder="Drug" size="40"></select-drug>
    </p>


    <div class="editing-draft-order" ng-repeat="draftOrder in orderContext.draftOrders | filter:{editing:true}">
        <edit-draft-order draft-order="draftOrder"></edit-draft-order>
    </div>

    <table>
        <tr class="draft-order" ng-repeat="order in orderContext.draftOrders | filter:{editing:false}">
            <td>
                {{ order.action }}
                {{ order | orderDates }}
            </td>
            <td>
                {{ order | orderInstructions }}
                <span ng-show="order.action == 'DISCONTINUE'">
                    <br/>
                    For: <input ng-model="order.orderReasonNonCoded" class="dc-reason" type="text" placeholder="reason" size="40"/>
                </span>
            </td>
            <td class="actions">
                <a ng-click="editDraftDrugOrder(order)" ng-hide="order.action == 'DISCONTINUE'"><i class="icon-pencil edit-action"></i></a>
                <a ng-click="cancelDraft(order)"><i class="icon-remove delete-action"></i></a>
            </td>
        </tr>
    </table>

    <div class="actions">
        <div class="signature">
            Signing as ${ ui.format(sessionContext.currentProvider) } on (auto-generated timestamp)
            <img ng-show="loading" src="${ ui.resourceLink("uicommons", "images/spinner.gif") }"/>
        </div>
        <button class="confirm right" ng-disabled="loading || !canSaveDrafts()" ng-click="signAndSaveDraftOrders()">Sign and Save</button>
        <button ui-sref="overview">
            Back to Visit
        </button>
    </div>
</div>