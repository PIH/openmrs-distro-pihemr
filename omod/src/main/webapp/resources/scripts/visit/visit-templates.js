angular.module("visit-templates", ["constants"])

    .factory("VisitTemplates", [ "EncounterTypes", function(EncounterTypes) {
        var hfeSimpleEditUrl = "/htmlformentryui/htmlform/editHtmlFormWithSimpleUi.page?patientId={{encounter.patient.uuid}}&encounterId={{encounter.uuid}}&returnUrl={{returnUrl}}";
        var hfeStandardEditUrl = "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{encounter.patient.uuid}}&encounterId={{encounter.uuid}}&returnUrl={{returnUrl}}";

        var encounterTypeConfig = {
            DEFAULT: {
                defaultState: "short",
                shortTemplate: "templates/defaultEncounterShort.page",
                longTemplate: "templates/defaultEncounterLong.page"
            }
        };
        encounterTypeConfig[EncounterTypes.checkIn.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/checkInShort.page",
            longTemplate: "templates/defaultEncounterLong.page",
            icon: "icon-check-in",
            editUrl: hfeSimpleEditUrl
        };
        encounterTypeConfig[EncounterTypes.vitals.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/vitalsShort.page",
            longTemplate: "templates/vitalsLong.page",
            icon: "icon-vitals",
            editUrl: hfeSimpleEditUrl
        };
        encounterTypeConfig[EncounterTypes.consultation.uuid] = {
            defaultState: "short",
            shortTemplate: "templates/clinicConsultShort.page",
            longTemplate: "templates/clinicConsultLong.page",
            icon: "icon-stethoscope",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.primaryCareHistory.uuid] = {
            defaultState: "long",
            shortTemplate: "templates/defaultEncounterShort.page",
            longTemplate: "templates/primaryCareAdultHistoryLong.page",
            icon: "icon-file-alt",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.labResults.uuid] = {
            defaultState: "long",
            shortTemplate: "templates/defaultEncounterShort.page",
            longTemplate: "templates/defaultEncounterLong.page",
            icon: "icon-beaker",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.primaryCareExam.uuid] = {
            defaultState: "long",
            shortTemplate: "templates/defaultEncounterShort.page",
            longTemplate: "templates/defaultEncounterLong.page",
            icon: "icon-stethoscope",
            editUrl: hfeSimpleEditUrl
        };
        encounterTypeConfig[EncounterTypes.primaryCareDx.uuid] = {
            defaultState: "long",
            shortTemplate: "templates/defaultEncounterShort.page",
            longTemplate: "templates/defaultEncounterLong.page",
            icon: "icon-list-ul",
            editUrl: hfeStandardEditUrl
        };
        encounterTypeConfig[EncounterTypes.consultationPlan.uuid] = {
            defaultState: "long",
            shortTemplate: "templates/defaultEncounterShort.page",
            longTemplate: "templates/defaultEncounterLong.page",
            icon: "icon-list-ol",
            editUrl: null
        };

        var visitActions = {
            type: "include",
            include: "templates/visitActions.page"
        };

        var reverseChronologicalEncounters = {
            type: "include",
            include: "templates/reverseChronologicalEncounters.page"
        };

        var checkIn = {
            type: "encounter",
            addInline: true,
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.checkIn.uuid
                },
                shortTemplate: "templates/checkInShort.page"
            },
            action: {
                label: "Check In",
                icon: "icon-check-in",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/checkin.xml&returnUrl={{returnUrl}}"
            }
        };

        var vitals = {
            type: "encounter",
            addInline: true,
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.vitals.uuid
                },
                shortTemplate: "templates/vitalsShort.page",
                longTemplate: "templates/vitalsLong.page"
            },
            action: {
                label: "Vitals",
                icon: "icon-vitals",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/vitals.xml&returnUrl={{returnUrl}}"
            }
        };

        var reviewAllergies = {
            type: "include",
            include: "templates/reviewAllergies.page"
        };
        var vaccinations = {
            type: "include",
            include: "templates/vaccinations.page"
        };
        var primaryCareAdultHistory = {
            type: "encounter",
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.primaryCareHistory.uuid
                },
                longTemplate: "templates/primaryCareAdultHistoryLong.page"
            },
            action: {
                label: "History (Adult)",
                icon: "icon-file-alt",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-adult-history.xml&returnUrl={{returnUrl}}"
            }
        };
        var primaryCareExam = {
            type: "encounter",
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.primaryCareExam.uuid
                },
                longTemplate: "templates/defaultEncounterLong.page"
            },
            action: {
                label: "Exam (Adult)",
                icon: "icon-stethoscope",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-adult-exam.xml&returnUrl={{returnUrl}}"
            }
        };

        var primaryCareDx = {
            type: "encounter",
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.primaryCareDx.uuid
                },
                longTemplate: "templates/defaultEncounterLong.page"
            },
            action: {
                label: "Diagnosis",
                icon: "icon-list-ul",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-adult-dx.xml&returnUrl={{returnUrl}}"
            }
        };

        var labResults = {
            type: "encounter",
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.labResults.uuid
                },
                longTemplate: "templates/defaultEncounterLong.page"
            },
            action: {
                label: "Lab results",
                icon: "icon-beaker",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/lab-results.xml&returnUrl={{returnUrl}}"
            }
        };

        var outpatientPlan = {
            type: "encounter",
            allowMultiple: true,
            encounter: {
                encounterType: EncounterTypes.consultationPlan
            },
            action: {
                label: "Plan",
                icon: "icon-list-ol",
                sref: "editPlan"
            }
        };

        var addExpectedEncounters = {
            type: "include",
            include: "templates/add-expected-encounters.page"
        }

        var allowedForAll = function(visit) {
            return true;
        };

        var ret = {
            timeline: {
                label: "Visit (Generic)",
                encounterTypeConfig: encounterTypeConfig,
                allowedFor: allowedForAll,
                elements: [
                    visitActions,
                    reverseChronologicalEncounters
                ]
            },
            adultInitialOutpatient: {
                label: "Adult Initial Outpatient Visit",
                allowedFor: allowedForAll,
                encounterTypeConfig: encounterTypeConfig,
                elements: [
                    checkIn,
                    vitals,
                    reviewAllergies,
                    labResults,
                    primaryCareAdultHistory,
                    primaryCareExam,
                    primaryCareDx,
                    outpatientPlan,
                    addExpectedEncounters
                ]
            },
            adultFollowupOutpatient: {
                label: "Adult Followup Outpatient Visit",
                allowedFor: allowedForAll,
                encounterTypeConfig: encounterTypeConfig,
                elements: [
                    checkIn,
                    vitals,
                    reviewAllergies,
                    primaryCareExam,
                    primaryCareDx,
                    outpatientPlan,
                    addExpectedEncounters
                ]
            },
            pedsInitialOutpatient: {
                label: "Peds Initial Outpatient Visit",
                allowedFor: allowedForAll,
                encounterTypeConfig: encounterTypeConfig,
                elements: [
                    checkIn,
                    vaccinations,
                    vitals,
                    reviewAllergies,
                    labResults,
                    primaryCareAdultHistory,
                    primaryCareExam,
                    primaryCareDx,
                    outpatientPlan,
                    addExpectedEncounters
                ]
            }
        };
        _.each(ret, function(it, key) {
            it.name = key;
        });
        return ret;
    }]);