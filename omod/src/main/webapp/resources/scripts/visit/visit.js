angular.module("visit", [ "filters", "constants", "visit-templates", "visitService", "encounterService", "obsService", "allergies", "orders", "vaccinations", "ui.bootstrap" ])

    .directive("dateWithPopup", [ function() {
        return {
            restrict: 'E',
            scope: {
                ngModel: '='
            },
            controller: function($scope) {
                $scope.now = new Date();
                $scope.opened = false;
                $scope.open = function(event) {
                    event.preventDefault();
                    event.stopPropagation();
                    $scope.opened = true;
                }
            },
            template: '<span class="angular-datepicker">' +
                        '<input type="text" is-open="opened" ng-model="ngModel" show-weeks="false" datepicker-popup="dd-MMMM-yyyy" max-date="now" readonly ng-click="open($event)"/>' +
                        '<i class="icon-calendar small add-on" ng-click="open($event)" ></i>' +
                        '</span>'
        }
    }])

    .directive("displayElement", [ "Concepts", "EncounterTypes", function(Concepts, EncounterTypes) {
        return {
            restrict: 'E',
            scope: {
                visit: '=',
                element: '&',
                dateFormat: '@'
            },
            controller: function($scope) {
                $scope.Concepts = Concepts;
                $scope.EncounterTypes = EncounterTypes;

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

                    $scope.encounterTemplate = function() {
                        if ($scope.encounterStub) {
                            var content = element.encounter[element.state + "Template"];
                            if (!content) {
                                content = element.encounter["defaultTemplate"] + "Template";
                            }
                            return content;
                        }
                        else {
                            return "templates/action.page";
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

                    $scope.template = "templates/visitElementEncounter.page";

                }
                else if (element.type === 'include') {
                    if (element.includeRaw) {
                        $scope.template = element.includeRaw;
                    } else {
                        $scope.include = element.include;
                        $scope.template = "templates/visitElementInclude.page";
                    }
                }
                else {
                    $scope.type = element.type;
                    $scope.template = "templates/visitElementNotYetImplemented.page";
                }

                $scope.goToPage = function(provider, page, opts) {
                    location.href = emr.pageLink(provider, page, opts);
                }
            },
            template: '<div ng-include="template"></div>'
        }
    }])

    // this is not a reusable directive, and it does not have an isolate scope
    .directive("visitDetails", [ "Visit", function(Visit) {
        return {
            restrict: 'E',
            controller: function($scope) {
                $scope.editing = false;

                $scope.startEditing = function() {
                    $scope.newStartDatetime = $scope.visit.startDatetime;
                    $scope.newStopDatetime = $scope.visit.stopDatetime;
                    $scope.editing = true;
                }

                $scope.apply = function() {
                    // we only want to edit a few properties
                    var props = {
                        uuid: $scope.visit.uuid,
                        startDatetime: $scope.newStartDatetime,
                        stopDatetime: $scope.newStopDatetime == '' ? null : $scope.newStopDatetime
                    };
                    new Visit(props).$save(function(v) {
                        $scope.visit.startDatetime = v.startDatetime;
                        $scope.visit.stopDatetime = v.stopDatetime;
                    });
                    $scope.editing = false;
                }

                $scope.cancel = function() {
                    $scope.editing = false;
                }
            },
            templateUrl: 'templates/visitDetails.page'
        }
    }])

    .service("VisitTemplateService", [ "VisitTemplates", "Encounter",
        function(VisitTemplates, Encounter) {

            return {
                determineFor: function(visit) {
                    var template = visit.patient.person.age < 15 ? "pedsInitialOutpatient" : "adultInitialOutpatient";
                    return angular.copy(VisitTemplates[template]);
                },

                applyVisit: function(visitTemplate, visit) {
                    var encounters = _.reject(visit.encounters, function(it) { return it.voided; });
                    _.each(visitTemplate.elements, function(it) {
                        it.state = it.defaultState;
                        if (it.type == 'encounter') {
                            it.encounter.existingStub = _.find(encounters, function(candidate) {
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

            function sameDate(d1, d2) {
                return d1 && d2 && d1.substring(0, 10) == d2.substring(0, 10);
            }

            function loadVisit(visitUuid) {
                $scope.visit = Visit.get({ uuid: visitUuid, v: "custom:(uuid,startDatetime,stopDatetime,encounters:default,patient:default,visitType:ref)" });

                $scope.visit.$promise.then(function(response) {
                    $scope.visitTemplate = VisitTemplateService.determineFor($scope.visit);
                    VisitTemplateService.applyVisit($scope.visitTemplate, $scope.visit);

                    Visit.get({patient: $scope.visit.patient.uuid, v: "default"}).$promise.then(function(response) {
                        $scope.visits = _.map(response.results, function(it) {
                            if (!it.stopDatetime) {
                                it.display += " [active visit]";
                            }
                            if (it.uuid === $scope.visit.uuid) {
                                it.display += " [selected visit]";
                            }
                            return it;
                        });
                    });

                    $scope.encounterDateFormat = sameDate(visit.startDatetime, visit.stopDatetime) ? "HH:mm" : "HH:mm (d-MMM)";
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