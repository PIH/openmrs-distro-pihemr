<div class="header" ng-include="'templates/defaultEncounterHeader.page'">
</div>

<div class="content">
    <p ng-repeat="obs in encounter.obs" class="aligned">
        <label>{{ obs.concept.display }}</label>
        <span class="value">{{ obs.value | omrs.display }}</span>
    </p>
</div>