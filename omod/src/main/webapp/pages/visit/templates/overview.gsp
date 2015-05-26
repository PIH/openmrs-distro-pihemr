<div id="choose-another-visit">
    <a ui-sref="visitList">Go to another visit</a>
</div>

<div id="visit">
    <choose-visit-template></choose-visit-template>
    <visit-details visit="visit"></visit-details>
</div>

<div ng-repeat="element in visitTemplate.elements">
    <display-element></display-element>
</div>
