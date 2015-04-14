<div class="header" ng-include="'templates/defaultEncounterHeader.page'">
</div>

<div class="content">
    <p ng-repeat="obs in encounter.obs" class="aligned">
        <label>{{ obs.concept | omrs.display }}</label>
        <span class="value">{{ obs | obs:"value" }}</span>
    </p>
</div>