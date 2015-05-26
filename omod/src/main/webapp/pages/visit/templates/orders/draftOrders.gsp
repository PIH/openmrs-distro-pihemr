<div id="plan-heading">
    <p>
        <label>Write a prescription:</label>
        <select-drug ng-model="newOrderForDrug" placeholder="Drug" size="20"></select-drug>
    </p>
    <button ui-sref="overview">
        Back to Visit
    </button>
</div>

<div id="draft-plan">
    <h2>Plan</h2>
    <div class="editing-draft-order" ng-repeat="draftOrder in orderContext.draftOrders | filter:{editing:true}">
        <edit-draft-order draft-order="draftOrder"></edit-draft-order>
    </div>

    <h5>Orders</h5>
    <span ng-hide="orderContext.draftOrders.length">None</span>
    <ul ng-show="orderContext.draftOrders.length">
        <li class="draft-order" ng-repeat="order in orderContext.draftOrders | filter:{editing:false}">
            {{ order | orderDates }}
            {{ order | orderInstructions }}
            <span ng-show="order.action == 'DISCONTINUE'">
                <br/>
                For: <input ng-model="order.orderReasonNonCoded" class="dc-reason" type="text" placeholder="reason" size="40"/>
            </span>
            <span class="actions">
                <a ng-click="editDraftDrugOrder(order)" ng-hide="order.action == 'DISCONTINUE'"><i class="icon-pencil edit-action"></i></a>
                <a ng-click="cancelDraft(order)"><i class="icon-remove delete-action"></i></a>
            </span>
        </li>
    </table>

    <div class="form">
        <h5>Additional plan comments</h5>
        <textarea placeholder="This is not yet saved to the DB -- we're looking for the concept."></textarea>
    </div>

    <div class="actions">
        <button class="confirm right" ng-disabled="loading || !canSaveDrafts()" ng-click="signAndSaveDraftOrders()">Sign and Save</button>
        <div class="signature" ng-show="loading">
            Signing as ${ ui.format(sessionContext.currentProvider) }
            on (auto-generated timestamp)
            <img src="${ ui.resourceLink("uicommons", "images/spinner.gif") }"/>
        </div>

        <div style="clear:both"></div>
    </div>
</fieldset>