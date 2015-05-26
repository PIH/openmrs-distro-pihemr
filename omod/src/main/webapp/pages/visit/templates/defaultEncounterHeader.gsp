<span class="one-third" ng-include="'templates/standardEncounterHeading.page'"></span>
<span class="encounter-location">{{ encounterStub.location | omrs.display }}</span>
<span class="encounter-provider">{{ encounterStub.provider | omrs.display }}</span>
<span class="overall-actions" ng-include="'templates/standardEncounterActions.page'"></span>