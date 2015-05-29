<div class="header" ng-include="'templates/defaultEncounterHeader.page'">
</div>

<div class="content">
    <p ng-repeat="obs in encounter.obs" class="aligned">
        <label>{{ obs.concept | omrs.display }}</label>
        <span class="value">{{ obs | obs:"value" }}</span>
    </p>
    <div ng-show="orders && orders.length">
        <h4>Orders</h4>
        <p ng-repeat="order in orders | orderBy:'dateActivated'" class="aligned">
            {{ order | orderInstructions }}
        </p>
    </div>

    <div class="book-keeping" ng-include="'templates/standardEncounterBookkeeping.page'"></div>
</div>