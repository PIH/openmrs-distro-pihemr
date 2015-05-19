angular.module("allergies", [ "constants", "ngResource", "uicommons.common" ])
    .factory('Allergies', [ '$resource', function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH  + "/ws/rest/v1/allergies/:uuid", {
            uuid: '@uuid'
        });
    }])

    .directive("currentAllergies", [ 'Allergies', function(Allergies) {
        return {
            scope: {
                patient: "="
            },
            controller: ["$scope", function($scope) {
                if ($scope.patient) {
                    $scope.allergies = Allergies.get({uuid: $scope.patient.uuid});
                }
            }],
            templateUrl: "templates/allergiesList.page"
        }
    }]);