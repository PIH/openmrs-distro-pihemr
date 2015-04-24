angular.module("orders", [ "constants", "orderService", "encounterService" ])

    .directive("orderSheet", [ "Order", "Encounter", function(Order, Encounter) {
        return {
            restrict: "E",
            scope: {
                visit: "="
            },
            controller: function($scope) {
                $scope.ordersByEncounters = {};
                angular.forEach($scope.visit.encounters, function(encounter) {
                    $scope.ordersByEncounters[encounter.uuid] = Encounter.query({ uuid: encounter.uuid, v: "custom:(uuid,orders:full)" });
                });

                $scope.orderList = function() {
                    var orders = _.flatten(_.values($scope.ordersByEncounters));
                    return _.sortBy(orders, "startDate");
                }

                $scope.anyOrders = function() {
                    return _.some(_.values($scope.ordersByEncounters));
                }
            },
            templateUrl: "templates/orderSheet.page"
        }
    }]);