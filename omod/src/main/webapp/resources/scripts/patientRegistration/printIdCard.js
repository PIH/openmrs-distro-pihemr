angular.module('printIdCard', [ 'ui.bootstrap' ])

    // Directive which can be applied to an input element to indicate that something should happen when enter is pressed
    .directive('ngEnter', function () {
        return function (scope, element, attrs) {
            element.bind("keydown keypress", function (event) {
                if(event.which === 13) {
                    scope.$apply(function (){
                        scope.$eval(attrs.ngEnter);
                    });
                    event.preventDefault();
                }
            });
        };
    })

    .controller('PrintIdCardCtrl', [ '$scope', '$http', function($scope, $http) {

        $scope.patientId = null;
        $scope.locationId = null;
        $scope.returnUrl = null;
        $scope.printingInProgress = false;

        $scope.init = function(patientId, locationId, returnUrl) {
            $scope.patientId = patientId;
            $scope.locationId = locationId;
            $scope.returnUrl = returnUrl;
            $scope.printIdCard();
        }

        $scope.displayStatus = function(data) {
            var alertType = data.success ? 'success' : 'error';
            $().toastmessage( 'showToast', { type: alertType, position: 'top-right', text: data.message, close: function() { $("#scan-patient-identifier").focus(); }} );
        }

        $scope.printIdCard = function() {
            $scope.printingInProgress = true;
            $http.get(emr.fragmentActionLink('mirebalais', 'idCard', 'printIdCard', {"patientId": $scope.patientId, "locationId": $scope.locationId}))
                .then(function(result) {
                    $scope.displayStatus(result.data);
                    $scope.printingInProgress = false;
                });
        }

        $scope.recordSuccessfulPrintAttempt = function() {
            $http.get(emr.fragmentActionLink('mirebalais', 'idCard', 'recordSuccessfulPrintAttempt', {"patientId": $scope.patientId, "identifier": $scope.scannedIdentifier}))
                .then(function(result) {
                    if (result.data.success) {
                        emr.navigateTo({"url": $scope.returnUrl});
                    }
                    else {
                        $scope.displayStatus(result.data);
                        $scope.scannedIdentifier = '';
                    }
                });
        }

        $scope.recordFailedPrintAttempt = function() {
            $http.get(emr.fragmentActionLink('mirebalais', 'idCard', 'recordFailedPrintAttempt', {"patientId": $scope.patientId}));
            emr.navigateTo({"url": $scope.returnUrl});
        }

    }])
