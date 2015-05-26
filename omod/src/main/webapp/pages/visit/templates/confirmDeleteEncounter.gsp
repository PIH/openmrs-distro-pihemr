<div class="dialog-header">
    <h3>Delete Encounter</h3>
</div>
<div class="dialog-content">
    <h4>
        Are you sure you want to delete this encounter from the visit?
    </h4>
    <div ng-show="activeOrders && activeOrders.length" class="note-container">
        <div class="note warning">
            <i class="icon-warning-sign medium"></i>
            This encounter has active orders!
            <br/>
            <br/>
            Confirm you also want to delete these active orders:
            <ul class="small">
                <li ng-repeat="order in activeOrders">
                    {{ order | orderInstructions }}
                </li>
            </ul>

        </div>
    </div>
    <div>
        <button class="confirm right" ng-click="confirm()">${ ui.message("uicommons.delete") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>
