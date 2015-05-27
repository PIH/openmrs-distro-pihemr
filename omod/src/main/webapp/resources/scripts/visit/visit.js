angular.module("visit", [ "filters", "constants", "visit-templates", "visitService", "encounterService", "obsService", "allergies", "orders", "vaccinations", "ui.bootstrap", "ui.router", "session", "orderEntry", "ngDialog", "appFramework"])

    .config(function ($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise("overview");

        $stateProvider
            .state("overview", {
                url: "/overview",
                templateUrl: "templates/overview.page"
            })
            .state("visitList", {
                url: "/visitList",
                templateUrl: "templates/visitList.page"
            })
            .state("drugOrders", {
                url: "/drugOrders",
                templateUrl: "templates/orders/drugOrders.page"
            })
            .state("addLabOrders", {
                url: "/addLabOrders",
                templateUrl: "templates/orders/addLabOrdersState.page"
            });
    })

    .directive("dateWithPopup", [ function() {
        return {
            restrict: 'E',
            scope: {
                ngModel: '=',
                minDate: '=',
                maxDate: '='
            },
            controller: function($scope) {
                $scope.now = new Date();
                $scope.opened = false;
                $scope.open = function(event) {
                    event.preventDefault();
                    event.stopPropagation();
                    $scope.opened = true;
                }
                $scope.options = { // for some reason setting this via attribute doesn't work
                    showWeeks: false
                }
            },
            template: '<span class="angular-datepicker">' +
                        '<input type="text" is-open="opened" ng-model="ngModel" datepicker-popup="dd-MMM-yyyy" readonly ' +
                        'datepicker-options="options" min-date="minDate" max-date="maxDate" ng-click="open($event)"/>' +
                        '<i class="icon-calendar small add-on" ng-click="open($event)" ></i>' +
                        '</span>'
        }
    }])

    // This is not a reusable directive. It does not have an isolate scope, but rather inherits scope from VisitController
    .directive("displayElement", [ "Concepts", "EncounterTypes", "VisitDisplayModel", function(Concepts, EncounterTypes, VisitDisplayModel) {
        return {
            restrict: 'E',
            controller: function($scope) {
                $scope.Concepts = Concepts;
                $scope.EncounterTypes = EncounterTypes;

                var element = $scope.element;

                if (element.type === 'encounter') {
                    $scope.action = element.action;
                    $scope.encounterStubs = element.encounterStubs;
                    $scope.canAdd = element.encounterStubs.length == 0 || element.allowMultiple;

                    $scope.encounterTemplate = function() {
                        if ($scope.encounterStub) {
                            var state = VisitDisplayModel.encounterState($scope.encounterStub);
                            var content = element.encounter[state + "Template"];
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
                    if (element.includeAsVisitElement) {
                        $scope.include = element.includeAsVisitElement;
                        $scope.template = "templates/visitElementInclude.page";
                    } else {
                        $scope.template = element.include;
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

    .directive("encounter", [ "Encounter", "VisitDisplayModel", "VisitTemplateService", "OrderEntryService", "Concepts", "SessionInfo",
        function(Encounter, VisitDisplayModel, VisitTemplateService, OrderEntryService, Concepts, SessionInfo) {
            return {
                restrict: "E",
                scope: {
                    encounterStub: "=encounter",
                    encounterDateFormat: "="
                },
                controller: ["$scope", function($scope) {
                    function loadFullEncounter() {
                        Encounter.get({ uuid: $scope.encounterStub.uuid, v: "full" }).
                            $promise.then(function(encounter) {
                                $scope.encounter = encounter;
                            });
                        $scope.orders = OrderEntryService.getOrdersForEncounter($scope.encounterStub);
                    }

                    $scope.encounter = $scope.encounterStub;
                    loadFullEncounter();
                    var config = VisitTemplateService.getConfigFor($scope.encounterStub);
                    var currentUser = new OpenMRS.UserModel(SessionInfo.get().user);

                    $scope.session = SessionInfo.get();
                    $scope.Concepts = Concepts;
                    $scope.icon = config ? config.icon : null;

                    $scope.currentTemplate = function() {
                        return VisitDisplayModel.displayTemplateFor($scope.encounterStub);
                    }
                    $scope.canExpand = function() {
                        return VisitDisplayModel.canExpand($scope.encounterStub);
                    }
                    $scope.canContract = function() {
                        return VisitDisplayModel.canContract($scope.encounterStub);
                    }
                    $scope.canEdit = function() {
                        return config.editUrl &&
                            new OpenMRS.EncounterModel($scope.encounter).canBeEditedBy(currentUser);
                    }
                    $scope.canDelete = function() {
                        // also allow deleting if the current user participated in the encounter as a provider
                        return new OpenMRS.EncounterModel($scope.encounter).canBeDeletedBy(currentUser);
                    }
                    $scope.expand = function() {
                        // Get the latest representation when we expand, in case things have been edited
                        loadFullEncounter();
                        VisitDisplayModel.expand($scope.encounterStub);
                    }
                    $scope.contract = function() {
                        VisitDisplayModel.contract($scope.encounterStub);
                    }
                    $scope.edit = function() {
                        $scope.$emit("request-edit-encounter", $scope.encounter);
                    }
                    $scope.delete = function() {
                        $scope.$emit("request-delete-encounter", $scope.encounter);
                    }
                }],
                template: '<div class="visit-element"><div ng-include="currentTemplate()"></div></div>'
            }
    }])

    // this is not a reusable directive, and it does not have an isolate scope
    .directive("visitDetails", [ "Visit", function(Visit) {
        return {
            restrict: 'E',
            controller: function($scope) {
                $scope.editing = false;

                $scope.now = new Date();

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
                        $scope.reloadVisit();
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

    // inherits scope from visit overview controller
    .directive("chooseVisitTemplate", [ "VisitTemplateService", "VisitAttributeTypes", "VisitService", function(VisitTemplateService, VisitAttributeTypes, VisitService) {
        return {
            restrict: 'E',
            controller: function($scope) {
                $scope.availableTemplates = VisitTemplateService.getAllowedVisitTemplates($scope.visit);
                $scope.activeTemplate = VisitTemplateService.getCurrent();
                $scope.$watch("visit", function() {
                    $scope.selectedTemplate = $scope.visit.getAttributeValue(VisitAttributeTypes.visitTemplate);
                    $scope.newVisitTemplate = _.findWhere($scope.availableTemplates, {name: $scope.selectedTemplate});
                    $scope.activeTemplate = VisitTemplateService.getCurrent();
                });

                $scope.choosingTemplate = false;

                $scope.save = function() {
                    var existing = $scope.visit.getAttribute(VisitAttributeTypes.visitTemplate);
                    var VisitAttribute = VisitService.visitAttributeResourceFor($scope.visit);
                    if ($scope.newVisitTemplate) {
                        new VisitAttribute({
                            attributeType: VisitAttributeTypes.visitTemplate.uuid,
                            value: $scope.newVisitTemplate.name
                        }).$save().then(function() {
                            $scope.choosingTemplate = false;
                            $scope.reloadVisit();
                        });
                    }
                    else {
                        // they chose nothing
                        if (existing) {
                            new VisitAttribute({uuid: existing.uuid}).$delete().then(function() {
                                $scope.reloadVisit();
                            });
                        }
                    }
                };
            },
            templateUrl: 'templates/chooseVisitTemplate.page'
        }
    }])

    .service("VisitTemplateService", [ "VisitTemplates", "VisitAttributeTypes", "Encounter",
        function(VisitTemplates, VisitAttributeTypes, Encounter) {

            var currentTemplate = null;

            return {
                getAllowedVisitTemplates: function(visit) {
                    return _.filter(VisitTemplates, function(it) {
                        return it.allowedFor(visit);
                    });
                },

                setCurrent: function(visitTemplate) {
                    currentTemplate = visitTemplate;
                },

                getCurrent: function() {
                    return currentTemplate;
                },

                getConfigFor: function(encounter) {
                    if (currentTemplate && currentTemplate.encounterTypeConfig) {
                        var config = currentTemplate.encounterTypeConfig[encounter.encounterType.uuid];
                        return config ? config : currentTemplate.encounterTypeConfig.DEFAULT;
                    }
                    return null;
                },

                determineFor: function(visit) {
                    var specified = new OpenMRS.VisitModel(visit).getAttributeValue(VisitAttributeTypes.visitTemplate);
                    if (specified && VisitTemplates[specified]) {
                        return angular.copy(VisitTemplates[specified]);
                    }
                    else {
                        //var template = visit.patient.person.age < 15 ? "pedsInitialOutpatient" : "adultInitialOutpatient";
                        var template = "timeline";
                        return angular.copy(VisitTemplates[template]);
                    }
                },

                applyVisit: function(visitTemplate, visit, VisitDisplayModel) {
                    this.setCurrent(visitTemplate);
                    var encounters = _.reject(visit.encounters, function(it) { return it.voided; });
                    _.each(visitTemplate.elements, function(it) {
                        if (it.type == 'encounter') {
                            it.encounterStubs = _.filter(encounters, function(candidate) {
                                // TODO support specifying by form also
                                return candidate.encounterType.uuid === it.encounter.encounterType.uuid;
                            });
                        }
                    });
                    _.each(encounters, function(it) {
                        var config = visitTemplate.encounterTypeConfig[it.encounterType.uuid];
                        if (!config) {
                            config = visitTemplate.encounterTypeConfig.DEFAULT;
                        }
                        VisitDisplayModel.encounterStates[it.uuid] = config.defaultState;
                    });
                }
            }
        }])

    .factory("VisitDisplayModel", [ "VisitTemplateService", function(VisitTemplateService) {
        var model = {};
        model.reset = function() {
            model.encounterStates = {}; // maps from encounter.uuid => "short" or "long"
        };
        model.encounterState = function(encounter) {
            return model.encounterStates ? model.encounterStates[encounter.uuid] : null;
        }
        model.canExpand = function(encounter) {
            var current = model.encounterState(encounter);
            var config = VisitTemplateService.getConfigFor(encounter);
            return current === 'short' && config && config.longTemplate;
        };
        model.canContract = function(encounter) {
            var current = model.encounterState(encounter);
            var config = VisitTemplateService.getConfigFor(encounter);
            return current === 'long' && config && config.shortTemplate;
        };
        model.expand = function(encounter) {
            model.encounterStates[encounter.uuid] = 'long';
        };
        model.contract = function(encounter) {
            model.encounterStates[encounter.uuid] = 'short';
        };
        model.displayTemplateFor = function(encounter) {
            var config = VisitTemplateService.getConfigFor(encounter);
            if (config) {
                var state = model.encounterStates[encounter.uuid];
                if (state) {
                    return config[state + "Template"];
                }
            }
            return "templates/defaultEncounterShort.page"

        }
        model.reset();
        return model;
    }])

    .controller("VisitController", [ "$scope", "$rootScope", "Visit", "VisitTemplateService", "CareSetting", "$q", "$state", "$timeout", "OrderContext", "VisitDisplayModel", "ngDialog", "Encounter", "OrderEntryService", "AppFrameworkService",
        function($scope, $rootScope, Visit, VisitTemplateService, CareSetting, $q, $state, $timeout, OrderContext, VisitDisplayModel, ngDialog, Encounter, OrderEntryService, AppFrameworkService) {

            $scope.VisitDisplayModel = VisitDisplayModel;

            AppFrameworkService.getUserExtensionsFor("patientDashboard.visitActions").then(function(ext) {
                $scope.visitActions = ext;
            })

            function sameDate(d1, d2) {
                return d1 && d2 && d1.substring(0, 10) == d2.substring(0, 10);
            }

            function loadVisit(visitUuid) {
                Visit.get({ uuid: visitUuid, v: "custom:(uuid,startDatetime,stopDatetime,location:ref,encounters:default,patient:default,visitType:ref,attributes:default)" })
                    .$promise.then(function(visit) {
                        $scope.visit = new OpenMRS.VisitModel(visit);
                        $scope.encounterDateFormat = sameDate($scope.visit.startDatetime, $scope.visit.stopDatetime) ? "HH:mm" : "HH:mm (d-MMM)";

                        // get other visits
                        Visit.get({patient: $scope.visit.patient.uuid, v: "default"}).$promise.then(function(response) {
                            // TODO fetch more pages?
                            $scope.visits = response.results;
                            $scope.isLatestVisit = !$scope.visit.stopDatetime || _.max($scope.visits, function(it) { return new Date(it.startDatetime) }).startDatetime === $scope.visit.startDatetime;
                        });

                        $scope.visitTemplate = VisitTemplateService.determineFor($scope.visit);
                        VisitTemplateService.applyVisit($scope.visitTemplate, $scope.visit, $scope.VisitDisplayModel);

                        // TODO refactor so that OrderContext has better logic for knowing when it is configured/ready, so that we don't have to nest this
                        $scope.careSettings.$promise.then(function() {
                            OrderContext.setCareSetting(_.findWhere($scope.careSettings.results, { careSettingType: "OUTPATIENT" }));
                            OrderContext.setPatient($scope.visit.patient);
                        })
                    });
                VisitDisplayModel.reset();
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
                index = temp.indexOf("#");
                if (index > 0) {
                    temp = temp.substring(0, index);
                }
                return temp;
            }

            //$rootScope.$on('$stateChangeStart', function(event, toState) {
            //    if (toState.name === "newPrescription") {
            //        $scope.newDraftDrugOrder = OpenMRS.createEmptyDraftOrder(OrderContext.get().careSetting);
            //    }
            //});

            $rootScope.$on("request-edit-encounter", function(event, encounter) {
                var config = VisitTemplateService.getConfigFor(encounter);
                if (config.editUrl) {
                    var url = Handlebars.compile(config.editUrl)({
                        patient: encounter.patient,
                        visit: $scope.visit,
                        encounter: encounter,
                        returnUrl: "/" + OPENMRS_CONTEXT_PATH + "/mirebalais/visit/visit.page?visit=" + $scope.visit.uuid
                    });
                    emr.navigateTo({applicationUrl: url});
                }
            });

            $rootScope.$on("request-delete-encounter", function(event, encounter) {
                ngDialog.openConfirm({
                    showClose: true,
                    closeByEscape: true,
                    closeByDocument: true,
                    controller: function($scope) {
                        OrderEntryService.getOrdersForEncounter(encounter).$promise.then(function(orders) {
                            $scope.activeOrders = _.filter(orders, function(it) {
                                return it.isActive();
                            });
                        });
                        $timeout(function() {
                            $(".dialog-content:visible button.confirm").focus();
                        }, 10)
                    },
                    template: "templates/confirmDeleteEncounter.page"
                }).then(function() {
                    Encounter.delete({uuid: encounter.uuid})
                        .$promise.then(function() {
                            $scope.reloadVisit();
                        });
                });
            });

            $scope.$on('visit-changed', function(event, visit) {
                if ($scope.visitUuid == visit.uuid) {
                    $scope.reloadVisit();
                }
            });

            $scope.careSettings = CareSetting.query({v:"default"});

            $scope.reloadVisit = function() {
                loadVisit($scope.visitUuid);
            }

            $scope.goToVisit = function(visit) {
                $scope.visitUuid = visit.uuid;
                $state.go("overview");
            }

            // TODO figure out if we can get rid of this function
            $scope.$watch('visitUuid', function(newVal, oldVal) {
                loadVisit(newVal);
            })

            $scope.visitUuid = getVisitParameter();

            $scope.hasDraftOrders = function() {
                return OrderContext.get().draftOrders.length > 0;
            }

            $scope.visitAction = function(visitAction) {
                if (visitAction.type == 'script') {
                    // TODO
                } else {
                    var visitModel = angular.extend({}, $scope.visit);
                    visitModel.id = $scope.visit.uuid; // HACK! TODO: change our extensions to refer to visit.uuid
                    visitModel.active = !$scope.visit.stopDatetime;

                    var url = Handlebars.compile(visitAction.url)({
                        visit: visitModel,
                        patient: $scope.visit.patient
                    });
                    emr.navigateTo({ applicationUrl: "/" + url });
                }
            }

            //$scope.configFor = function(encounter) {
            //    if ($scope.visitTemplate && $scope.visitTemplate.encounterTypeConfig) {
            //        var templates = $scope.visitTemplate.encounterTypeConfig[encounter.encounterType.uuid];
            //        return templates ? templates : $scope.visitTemplate.encounterTypeConfig.DEFAULT;
            //    }
            //    return null;
            //}

            //$scope.displayTemplateFor = function(encounter) {
            //    var config = $scope.configFor(encounter);
            //    if (config) {
            //        var state = VisitDisplayModel.encounterStates[encounter.uuid];
            //        if (state) {
            //            return config[state + "Template"];
            //        }
            //    }
            //    return "templates/defaultEncounterShort.page"
            //}

        }]);