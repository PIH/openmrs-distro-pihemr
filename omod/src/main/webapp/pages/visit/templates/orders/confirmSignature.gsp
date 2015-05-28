<div class="dialog-header">
    <h3>Sign this treatment plan</h3>
</div>
<div class="dialog-content">
    <h4>
        Sign treatment plan as:
        <em>${ ui.format(sessionContext.currentProvider) }</em>
    </h4>
    <h5>
        Confirm by re-entering your password:
        <input type="password" ng-model="passwordConfirmationNotUsed"/>
    </h5>
    <p>
        <em>This is a placeholder and password validation is not implemented yet.</em>
    </p>
    <br/>
    <div>
        <button class="confirm right" ng-click="confirm()" ng-disabled="!passwordConfirmationNotUsed">Sign and Save</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>