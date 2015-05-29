<div id="plan-heading">
    <button ui-sref="overview">
        <i class="icon-arrow-left"></i>
        Back to Visit
    </button>
</div>

<div id="draft-plan">
    <h5 class="orders-category">
        Prescriptions
    </h5>
    <ul>
        <li class="draft-order" ng-repeat="order in getDraftOrdersByType('drugorder') | filter:{editing:false}">
            {{ order | orderDates }}
            {{ order | orderInstructions }}
            <span ng-show="order.action == 'DISCONTINUE'">
                <br/>
                For: <input ng-model="order.orderReasonNonCoded" class="dc-reason" type="text" placeholder="reason" size="40"/>
            </span>
            <span class="actions">
                <a ng-click="editDraftDrugOrder(order)" ng-show="order.action == 'drugorder' && order.action != 'DISCONTINUE'"><i class="icon-pencil edit-action"></i></a>
                <a ng-click="cancelDraft(order)"><i class="icon-remove delete-action"></i></a>
            </span>
        </li>
    </ul>

    <div class="editing-draft-order" ng-repeat="draftOrder in orderContext.draftOrders | filter:{editing:true}">
        <edit-draft-order draft-order="draftOrder"></edit-draft-order>
    </div>
    <div>
        <label><i class="icon-plus"></i> Add:</label>
        <select-drug ng-model="newOrderForDrug" placeholder="Drug" size="20" on-select-callback="focusEdit()"></select-drug>
    </div>


    <h5 class="orders-category">
        Test Orders
    </h5>
    <ul>
        <li class="draft-order" ng-repeat="order in getDraftOrdersByType('testorder') | filter:{editing:false}">
            {{ order | orderDates }}
            {{ order | orderInstructions }}
            <span ng-show="order.action == 'DISCONTINUE'">
                <br/>
                For: <input ng-model="order.orderReasonNonCoded" class="dc-reason" type="text" placeholder="reason" size="40"/>
            </span>
            <span class="actions">
                <a ng-click="editDraftDrugOrder(order)" ng-show="order.action == 'drugorder' && order.action != 'DISCONTINUE'"><i class="icon-pencil edit-action"></i></a>
                <a ng-click="cancelDraft(order)"><i class="icon-remove delete-action"></i></a>
            </span>
        </li>
        <li>
            <a class="add-orders" ui-sref="addLabOrders">
                <i class="icon-plus"></i> Add
            </a>
        </li>
    </ul>


    <div class="form">
        <h5>Clinical management plan</h5>
        <textarea ng-model="orderContext.draftData.clinicalManagementPlanComment" placeholder="optional"></textarea>

        <label>Return Visit Date</label>
        <date-with-popup ng-model="orderContext.draftData.returnVisitDate" min-date="tomorrow"></date-with-popup>
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
</div>