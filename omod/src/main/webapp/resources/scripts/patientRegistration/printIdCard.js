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

    // Directive which can be applied to an input element to ensure it gains focus when an expression evaluates to true
    .directive('focusMe', function($timeout, $parse) {
        return {
            link: function(scope, element, attrs) {
                var model = $parse(attrs.focusMe);
                scope.$watch(model, function(value) {
                    if(value === true) {
                        $timeout(function() {
                            element[0].focus();
                        });
                    }
                });
                element.bind('blur', function() {
                    scope.$apply(model.assign(scope, false));
                });
            }
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
            $scope.printStatus = {
                "alertType": data.success ? 'success' : 'error',
                "message": data.message
            };
         }

        $scope.printIdCard = function() {
            $scope.focusOnScanInput = false;
            $scope.printingInProgress = true;
            $http.get(emr.fragmentActionLink('mirebalais', 'patientRegistration/idCard', 'printIdCard', {"patientId": $scope.patientId, "locationId": $scope.locationId}))
                .then(function(result) {
                    $scope.displayStatus(result.data);
                    $scope.printingInProgress = false;
                    $scope.focusOnScanInput = true;
                });
        }

        $scope.recordSuccessfulPrintAttempt = function() {
            $http.get(emr.fragmentActionLink('mirebalais', 'patientRegistration/idCard', 'recordSuccessfulPrintAttempt', {"patientId": $scope.patientId, "identifier": $scope.scannedIdentifier}))
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
            $http.get(emr.fragmentActionLink('mirebalais', 'patientRegistration/idCard', 'recordFailedPrintAttempt', {"patientId": $scope.patientId}))
                .then(function(result) {
                    emr.navigateTo({"url": $scope.returnUrl});
                });
        }

    }])
