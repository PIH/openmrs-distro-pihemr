<div class="dialog-header">
    <h3>Delete vaccination?</h3>
</div>
<div class="dialog-content">
    <h4>
        Are you sure you want to delete this vaccination record?
    </h4>
    <div>
        {{ vaccination.label }} - {{ sequence.label }} - {{ dateObs | obs:"value" | date }}
        <br/>
        <br/>
    </div>
    <div>
        <button class="confirm right" ng-click="confirm()">${ ui.message("uicommons.delete") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>