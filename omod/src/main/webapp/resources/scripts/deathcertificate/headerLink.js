angular.module('deathCertificate', ['encounterService']).
    controller('DeathCertificateCtrl', ['$scope', 'EncounterService', function ($scope, EncounterService) {

        $scope.patientUuid = null;
        $scope.returnLabel = '';
        $scope.existing = { loading: true };

        $scope.init = function (patientUuid, returnLabel) {
            $scope.patientUuid = patientUuid;
            $scope.returnLabel = returnLabel;
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
                page: "htmlform/enterHtmlFormWithSimpleUi",
                query: {
                    patientId: $scope.patientUuid,
                    definitionUiResource: "file:configuration/pih/htmlforms/deathCertificate.xml",
                    returnUrl: emr.pageLink("coreapps", "clinicianfacing/patient", { patientId: $scope.patientUuid, app: "pih.app.clinicianDashboard" })
                }
            });
        }

        $scope.viewDeathCertificate = function(encounterUuid) {
            emr.navigateTo({
                provider: "htmlformentryui",
                page: "htmlform/viewEncounterWithHtmlForm",
                query: {
                    encounter: encounterUuid,
                    editStyle: "simple",
                    returnLabel: $scope.returnLabel,
                    returnUrl: emr.pageLink("coreapps", "clinicianfacing/patient", { patientId: $scope.patientUuid, app: "pih.app.clinicianDashboard" })
                }
            });
        }
    }]);
