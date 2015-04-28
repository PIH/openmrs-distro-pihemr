<div class="dialog-header">
    <h3>Record vaccination</h3>
</div>
<div class="dialog-content">
    <h4>
        {{ vaccination.label }}, {{ sequence.label }}
    </h4>
    <div class="spaced-paragraphs">
        <h5>When was it given?</h5>

        <p ng-show="hasActiveVisit">
            <label>
                <input type="radio" ng-model="when" value="now"/> Today: {{ now | date }}
            </label>
        </p>

        <p>
            <label>
                <input type="radio" ng-model="when" value="visit"/> During a visit in the patient record
            </label>
        </p>

        <p>
            <label>
                <input type="radio" ng-model="when" value="no-visit"/> Not during a visit in the patient record
            </label>
        </p>

        <p ng-show="when == 'visit'">
            <br/>
            Which visit?
            <select ng-model="whenVisit"
                    ng-options="visit as visit.display for visit in visits">
            </select>
        </p>

        <p ng-show="when != 'now'">
            <br/>
            Date: <date-with-popup ng-model="date" min-date="minDate" max-date="maxDate"></date-with-popup>
        </p>
        <br/>
    </div>
    <div>
        <button class="confirm right" ng-click="confirm({when: when, whenVisit: whenVisit, date: date})">${ ui.message("uicommons.save") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>