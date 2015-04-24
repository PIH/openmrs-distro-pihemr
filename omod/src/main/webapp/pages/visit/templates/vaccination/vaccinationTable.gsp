<table class="vaccination-table">
    <thead>
    <tr>
        <th>Vaccination</th>
        <th ng-repeat="sequence in sequences">{{ sequence.label }}</th>
    </tr>
    </thead>
    <tbody>
        <tr ng-repeat="vaccination in vaccinations">
            <th>{{ vaccination.label }}</th>
            <td ng-repeat="sequence in sequences" ng-class="{ impossible: !isDoseValidForVaccination(sequence, vaccination) }">
                <span ng-show="existingDose(sequence, vaccination)">
                    {{ existingDose(sequence, vaccination) | groupMember:Concepts.vaccinationDate | obs:"value" | date }}
                    <a class="delete-action" ng-click="confirmDelete(sequence, vaccination)"><i class="icon-remove"></i></a>
                </span>
                <span ng-show="!existingDose(sequence, vaccination) && isDoseValidForVaccination(sequence, vaccination)">
                    <a ng-click="openDialog(sequence, vaccination)" class="edit-action"><i class="icon-plus"></i></a>
                </span>
            </td>
        </tr>
    </tbody>
</table>