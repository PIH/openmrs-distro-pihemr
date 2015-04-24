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
            saveNew: function(visit, vaccination, sequence, date) {
                return EncounterTransaction.save({
                    patientUuid: visit.patient.uuid,
                    visitTypeUuid: visit.visitType.uuid,
                    encounterTypeUuid: EncounterTypes.consultation.uuid,
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
            }
        }
    }])

    .directive("vaccinationTable", [ "Concepts", "VaccinationService", "ngDialog", "groupMemberFilter", function(Concepts, VaccinationService, ngDialog, groupMemberFilter) {
        return {
            restrict: "E",
            scope: {
                visit: "="
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
                        showClose: true,
                        closeByEscape: true,
                        closeByDocument: true,
                        controller: ["$scope", function($scope) {
                            $scope.sequence = sequence;
                            $scope.vaccination = vaccination;
                            $scope.now = new Date().toISOString();
                            $scope.date = $scope.now;
                        }],
                        template: "templates/vaccination/recordVaccination.page"
                    }).then(function(date) {
                        VaccinationService.saveNew($scope.visit, vaccination, sequence, date)
                            .$promise.then(loadHistory);
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