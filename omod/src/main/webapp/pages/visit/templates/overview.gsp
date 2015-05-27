<div id="choose-another-visit">
    <a ui-sref="visitList">Go to another visit</a>
</div>

<div id="visit-type">
    <choose-visit-template></choose-visit-template>
</div>

<div id="visit-details">
    <visit-details visit="visit"></visit-details>
</div>

<div ng-repeat="element in visitTemplate.elements">
    <display-element></display-element>
</div>
