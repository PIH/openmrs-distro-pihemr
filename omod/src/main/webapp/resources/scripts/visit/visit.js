angular.module("visit", [ "filters", "constants", "visit-templates", "visitService", "encounterService" ])

    .directive("displayElement", [ "Concepts", function(Concepts) {
        return {
            restrict: 'E',
            scope: {
                visit: '=',
                element: '&'
            },
            controller: function($scope) {
                var element = $scope.element();

                if (element.type === 'encounter') {
                    $scope.canExpand = function() {
                        return element.state === 'short' && element.encounter.longTemplate;
                    }
                    $scope.canContract = function() {
                        return element.state === 'long' && element.encounter.shortTemplate;
                    }

                    $scope.expand = function() {
                        element.state = 'long';
                    }
                    $scope.contract = function() {
                        element.state = 'short';
                    }

                    $scope.action = element.action;
                    $scope.encounterStub = element.encounter.existingStub;
                    $scope.encounter = element.encounter.existing;

                    $scope.headerTemplate = function() {
                        return $scope.encounterStub ?
                            "templates/defaultEncounterHeader.page":
                            "templates/noEncounterYetHeader.page";
                    }

                    $scope.contentTemplate = function() {
                        if ($scope.encounterStub) {
                            var content = element.encounter[element.state + "Template"];
                            if (!content) {
                                content = element.encounter["defaultTemplate"] + "Template";
                            }
                            return content;
                        }
                        else {
                            return "templates/action.page"
                        }
                    }

                    $scope.eval = function(template) {
                        if (!template) {
                            return null;
                        }
                        var compiled = Handlebars.compile(template);
                        return compiled({
                            contextPath: OPENMRS_CONTEXT_PATH,
                            returnUrl: location.href,
                            visit: $scope.visit
                        });
                    }

                    $scope.Concepts = Concepts;
                    $scope.template = "templates/visitElementEncounter.page";

                }
                else if (element.type === 'include') {
                    $scope.include = element.include;
                    $scope.template = "templates/visitElementInclude.page";
                }
                else {
                    $scope.type = element.type;
                    $scope.template = "templates/visitElementNotYetImplemented.page";
                }
            },
            template: '<div ng-include="template"></div>'
        }
    }])

    .service("VisitTemplateService", [ "VisitTemplates", "Encounter",
        function(VisitTemplates, Encounter) {

            return {
                determineFor: function(visit) {
                    return angular.copy(VisitTemplates.standardOutpatient);
                },

                applyVisit: function(visitTemplate, visit) {
                    _.each(visitTemplate.elements, function(it) {
                        it.state = it.defaultState;
                        if (it.type == 'encounter') {
                            it.encounter.existingStub = _.find(visit.encounters, function(candidate) {
                                return candidate.encounterType.uuid === it.encounter.encounterType.uuid;
                            });
                            if (it.encounter.existingStub) {
                                it.encounter.existing = Encounter.get({ uuid: it.encounter.existingStub.uuid, v: "full" });
                            }
                        }
                    });
                }
            }
        }])

    .controller("VisitController", [ "$scope", "Visit", "VisitTemplateService",
        function($scope, Visit, VisitTemplateService) {

            function loadVisit(visitUuid) {
                $scope.visit = Visit.get({ uuid: visitUuid, v: "full" });

                $scope.visit.$promise.then(function(response) {
                    $scope.visitTemplate = VisitTemplateService.determineFor($scope.visit);
                    VisitTemplateService.applyVisit($scope.visitTemplate, $scope.visit);

                    Visit.get({patient: $scope.visit.patient.uuid, v: "default"}).$promise.then(function(response) {
                        $scope.visits = _.map(response.results, function(it) {
                            if (it.uuid === $scope.visit.uuid) {
                                it.display = it.display + " [this visit]";
                            }
                            return it;
                        });
                    });
                });
            }

            function getVisitParameter() {
                var index = location.href.indexOf("?");
                var temp = location.href.substring(index + 1);
                index = temp.indexOf("visit=");
                temp = temp.substring(index + 6);
                index = temp.indexOf("&");
                if (index > 0) {
                    temp = temp.substring(0, index);
                }
                return temp;
            }

            $scope.$watch('visitUuid', function(newVal, oldVal) {
                loadVisit(newVal);
            })
            $scope.visitUuid = getVisitParameter();
        }]);