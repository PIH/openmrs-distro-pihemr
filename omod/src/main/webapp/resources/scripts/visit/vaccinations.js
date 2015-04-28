angular.module("vaccinations", [ "constants", "ngDialog", "obsService", "encounterTransaction", "filters" ])

    .factory("VaccinationService", [ "Obs", "Concepts", "EncounterTypes", "EncounterTransaction", function(Obs, Concepts, EncounterTypes, EncounterTransaction) {
        return {
            getHistory: function(patient) {
                // raw REST query, will return { results: [...] }
                return Obs.query({ v:"default", patient: patient.uuid, concept: Concepts.vaccinationHistoryConstruct.uuid });
            },
            deleteDose: function(obsGroup) {
                return Obs.delete({uuid: obsGroup.uuid});
            },
            saveWithEncounter: function(visit, vaccination, sequence, date) {
                return EncounterTransaction.save({
                    patientUuid: visit.patient.uuid,
                    visitUuid: visit.uuid,
                    encounterTypeUuid: EncounterTypes.consultation.uuid,
                    encounterDateTime: date,
                    observations: [
                        {
                            concept: Concepts.vaccinationHistoryConstruct.uuid,
                            groupMembers: [
                                {
                                    concept: Concepts.vaccinationGiven.uuid,
                                    value: vaccination.concept
                                },
                                {
                                    concept: Concepts.vaccinationSequenceNumber.uuid,
                                    value: sequence.sequenceNumber
                                },
                                {
                                    concept: Concepts.vaccinationDate.uuid,
                                    value: date
                                }
                            ]
                        }
                    ]
                });
            },
            saveWithoutEncounter: function(patient, vaccination, sequence, date) {
                // this is for the scenario when they are retrospectively recording a vaccination that was not done during
                // any visit captured in this patient record (e.g. patient brings a paper record from another clinic)
                var obsDatetime = new Date().toISOString();
                return Obs.save({
                    person: patient.uuid,
                    obsDatetime: obsDatetime,
                    concept: Concepts.vaccinationHistoryConstruct.uuid,
                    groupMembers: [
                        {
                            person: patient.uuid,
                            obsDatetime: obsDatetime,
                            concept: Concepts.vaccinationGiven.uuid,
                            value: vaccination.concept.uuid
                        },
                        {
                            person: patient.uuid,
                            obsDatetime: obsDatetime,
                            concept: Concepts.vaccinationSequenceNumber.uuid,
                            value: sequence.sequenceNumber
                        },
                        {
                            person: patient.uuid,
                            obsDatetime: obsDatetime,
                            concept: Concepts.vaccinationDate.uuid,
                            value: date
                        }
                    ]
                });
            }
        }
    }])

    .directive("vaccinationTable", [ "Concepts", "VaccinationService", "ngDialog", "groupMemberFilter", "$timeout", function(Concepts, VaccinationService, ngDialog, groupMemberFilter, $timeout) {
        return {
            restrict: "E",
            scope: {
                visit: "=",
                visits: "="
            },
            controller: function($scope) {
                var sequences = [
                    {
                        label: "Dose 0",
                        sequenceNumber: 0
                    },
                    {
                        label: "Dose 1",
                        sequenceNumber: 1
                    },
                    {
                        label: "Dose 2",
                        sequenceNumber: 2
                    },
                    {
                        label: "Dose 3",
                        sequenceNumber: 3
                    },
                    {
                        label: "Rappel 1",
                        sequenceNumber: 11
                    },
                    {
                        label: "Rappel 2",
                        sequenceNumber: 12
                    }
                ]
                var vaccinations = [
                    {
                        label: "BCG",
                        concept: Concepts.bcgVaccination,
                        doses: [ 1 ]
                    },
                    {
                        label: "Polio",
                        concept: Concepts.polioVaccination,
                        doses: [ 0, 1, 2, 3, 11, 12 ]
                    },
                    {
                        label: "Pentavalent",
                        concept: Concepts.pentavalentVaccination,
                        doses: [ 1, 2, 3 ]
                    },
                    {
                        label: "Rotavirus",
                        concept: Concepts.rotavirusVaccination,
                        doses: [ 1, 2 ]
                    },
                    {
                        label: "Rougeole/Rubeole",
                        concept: Concepts.measlesRubellaVaccination,
                        doses: [ 1 ]
                    },
                    {
                        label: "DT",
                        concept: Concepts.diptheriaTetanusVaccination,
                        doses: [ 0, 1, 2, 3, 11, 12 ]
                    }
                ]

                function hasCodedMember(group, concept, codedValue) {
                    return _.find(group.groupMembers, function(member) {
                        return member.concept.uuid == concept.uuid
                            && member.value.uuid == codedValue.uuid;
                    });
                }
                function hasNumericMember(group, concept, numericValue) {
                    return _.find(group.groupMembers, function(member) {
                        return member.concept.uuid == concept.uuid
                            && member.value == numericValue;
                    });
                }
                $scope.existingDose = function(sequence, vaccination) {
                    return _.find($scope.history, function(it) {
                        return hasNumericMember(it, Concepts.vaccinationSequenceNumber, sequence.sequenceNumber)
                            && hasCodedMember(it, Concepts.vaccinationGiven, vaccination.concept);
                    });
                }

                $scope.isDoseValidForVaccination = function(sequence, vaccination) {
                    return _.contains(vaccination.doses, sequence.sequenceNumber);
                }

                $scope.Concepts = Concepts;
                $scope.history = [];
                $scope.sequences = sequences;
                $scope.vaccinations = vaccinations;

                function loadHistory() {
                    VaccinationService.getHistory($scope.visit.patient).$promise.then(function(response) {
                        $scope.history = response.results;
                    });
                }
                loadHistory();

                $scope.openDialog = function(sequence, vaccination) {
                    ngDialog.openConfirm({
                        template: "templates/vaccination/recordVaccination.page",
                        showClose: true,
                        closeByEscape: true,
                        closeByDocument: true,
                        controller: ["$scope", function($dialogScope) {
                            $dialogScope.now = new Date().toISOString();
                            $dialogScope.sequence = sequence;
                            $dialogScope.vaccination = vaccination;
                            $dialogScope.minDate = null;
                            $dialogScope.maxDate = new Date().toISOString();
                            $dialogScope.visits = $scope.visits;
                            $dialogScope.hasActiveVisit = _.find($scope.visits, function(it) {
                                return !it.stopDatetime;
                            });

                            // when they choose a visit, set the date and allowed date range based on it
                            $dialogScope.$watch("whenVisit", function(newVal) {
                                $dialogScope.date = newVal ? newVal.startDatetime : null;
                                $dialogScope.minDate = newVal ? newVal.startDatetime : null;
                                $dialogScope.maxDate = (newVal && newVal.stopDatetime) ? newVal.stopDatetime : $dialogScope.now;
                            });

                            $dialogScope.$watch("when", function(newVal) {
                                if (newVal == 'now') {
                                    $dialogScope.whenVisit = _.find($scope.visits, function(it) {
                                        return !it.stopDatetime;
                                    });
                                }
                                else if (newVal == 'visit') {
                                    //$dialogScope.date = null;
                                }
                                else if (newVal == 'no-visit') {
                                    $dialogScope.whenVisit = null; // also triggers setting date to null
                                }
                            });

                            // if the current visit is active, default to the "now" option, otherwise default to
                            // "visit" with the currently-selected visit chosen
                            if (!$scope.visit.stopDatetime) {
                                // active visit
                                $dialogScope.when = "now";
                            }
                            else {
                                $dialogScope.when = "visit";
                                $dialogScope.whenVisit = _.findWhere($scope.visits, {uuid: $scope.visit.uuid});
                                $dialogScope.date = $dialogScope.whenVisit.startDatetime;
                            }

                            $timeout(function() {
                                $(".dialog-content:visible button.confirm").focus();
                            }, 10)
                        }]
                    }).then(function(opts) {
                        if (opts.when == 'now') {
                            VaccinationService.saveWithEncounter($scope.visit, vaccination, sequence, opts.date)
                                .$promise.then(loadHistory);
                        } else if (opts.when == 'visit') {
                            VaccinationService.saveWithEncounter(opts.whenVisit, vaccination, sequence, opts.date)
                                .$promise.then(loadHistory);
                        } else if (opts.when == 'no-visit') {
                            VaccinationService.saveWithoutEncounter($scope.visit.patient, vaccination, sequence, opts.date)
                                .$promise.then(loadHistory);
                        }
                    });
                }

                $scope.confirmDelete = function(sequence, vaccination) {
                    var existingDose = $scope.existingDose(sequence, vaccination);
                    ngDialog.openConfirm({
                        showClose: true,
                        closeByEscape: true,
                        closeByDocument: true,
                        controller: ["$scope", function($scope) {
                            $scope.sequence = sequence;
                            $scope.vaccination = vaccination;
                            $scope.dateObs = groupMemberFilter(existingDose, Concepts.vaccinationDate);

                            $timeout(function() {
                                $(".dialog-content:visible button.confirm").focus();
                            }, 10)
                        }],
                        template: "templates/vaccination/confirmDeleteVaccination.page"
                    }).then(function(date) {
                        VaccinationService.deleteDose(existingDose)
                            .$promise.then(loadHistory);
                    });
                }
            },
            templateUrl: "templates/vaccination/vaccinationTable.page"
        }
    }]);