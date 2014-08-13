angular.module('deathCertificate', ['encounterService']).
    controller('DeathCertificateCtrl', ['$scope', 'EncounterService', function ($scope, EncounterService) {

        $scope.patientUuid = null;
        $scope.existing = { loading: true };

        $scope.init = function (patientUuid) {
            $scope.patientUuid = patientUuid;
            EncounterService.getEncounters({ patient: patientUuid, encounterType: "1545d7ff-60f1-485e-9c95-5740b8e6634b" }).
                then(function (results) {
                    if (results.length > 0) {
                        $scope.existing = results[0];
                    } else {
                        $scope.existing = null;
                    }
                });
        }

        $scope.fillOutDeathCertificate = function() {
            emr.navigateTo({
                provider: "htmlformentryui",
                page: "htmlform/enterHtmlFormWithStandardUi",
                query: {
                    patientId: $scope.patientUuid,
                    definitionUiResource: "mirebalais:htmlforms/deathCertificate.xml",
                    returnUrl: emr.pageLink("coreapps", "patientdashboard/patientDashboard", { patientId: $scope.patientUuid })
                }
            });
        }

        $scope.viewDeathCertificate = function(encounterUuid) {
            emr.navigateTo({
                provider: "htmlformentryui",
                page: "htmlform/viewEncounterWithHtmlForm",
                query: {
                    encounter: encounterUuid,
                    returnUrl: emr.pageLink("coreapps", "patientdashboard/patientDashboard", { patientId: $scope.patientUuid })
                }
            });
        }
    }]);