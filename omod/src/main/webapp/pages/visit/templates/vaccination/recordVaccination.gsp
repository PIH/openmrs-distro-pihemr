<div class="dialog-header">
    <h3>Record vaccination</h3>
</div>
<div class="dialog-content">
    <h4>
        {{ vaccination.label }} - {{ sequence.label }}
    </h4>
    <div>
        When?<br/>
        <date-with-popup ng-model="date"></date-with-popup>
        <br/>
        <br/>
    </div>
    <div>
        <button class="confirm right" ng-click="confirm(date)">${ ui.message("uicommons.save") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>