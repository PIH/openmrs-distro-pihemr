<div id="active-visit-template">
    <form ng-show="choosingTemplate || !selectedTemplate">
        <select ng-model="newVisitTemplate" ng-options="t.label for t in availableTemplates">
            <option value="">Which visit template?</option>
        </select>
        <span class="actions">
            <button ng-click="save()">Save</button>
            <button ng-show="selectedTemplate" ng-click="choosingTemplate = false">Cancel</button>
        </span>
    </form>

    <div ng-show="selectedTemplate && !choosingTemplate">
        <strong>{{ activeTemplate.label | omrs.display }}</strong>
        <span class="actions">
            <a ng-click="choosingTemplate = true"><i class="icon-pencil edit-action"></i></a>
        </span>
    </div>
</div>