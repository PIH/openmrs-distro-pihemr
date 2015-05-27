<h2>Request Tests</h2>

<div ng-repeat="group in labs">
    <h3>{{ group.label }}</h3>
    <ul>
        <li ng-repeat="lab in group.labs">
            <label>
                <input type="checkbox" ng-model="orderLabs[lab.concept.uuid].selected"/>
                {{ lab.label }}
            </label>
        </li>
    </ul>
    <h3>Etc</h3>
    <ul>
        <li>More types...</li>
    </ul>
</div>

<br/>

<div class="actions">
    <button class="confirm right" ng-click="apply()" ng-disabled="!anySelected()">Add</button>
    <button class="cancel" ui-sref="drugOrders">Cancel</button>
</div>

