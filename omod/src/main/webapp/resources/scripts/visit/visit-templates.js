angular.module("visit-templates", ["constants"])

    .factory("VisitTemplates", [ "EncounterTypes", function(EncounterTypes) {
        var checkIn = {
            type: "encounter",
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.checkIn.uuid
                },
                shortTemplate: "templates/checkInShort.page"
            },
            action: {
                label: "Check In",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/checkin.xml&returnUrl={{returnUrl}}"
            },
            defaultState: "short"
        };

        var vitals = {
            type: "encounter",
            encounter: {
                encounterType: {
                    uuid: EncounterTypes.vitals.uuid
                },
                shortTemplate: "templates/vitalsShort.page",
                longTemplate: "templates/vitalsLong.page"
            },
            action: {
                label: "Vitals",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/vitals.xml&returnUrl={{returnUrl}}"
            },
            defaultState: "short"
        };

        var firstTimeHistory = [

        ]

        var reviewAllergies = {
            type: "include",
            include: {
                label: "Review Allergies",
                template: "templates/reviewAllergies.page"
            }
        };
        var vaccinations = {
            type: "include",
            includeRaw: "templates/vaccinations.page"
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
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-adult-history.xml&returnUrl={{returnUrl}}"
            },
            defaultState: "long"
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
                label: "Exam and Diagnosis (Adult)",
                href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=pihcore:htmlforms/haiti/primary-care-adult-exam-dx.xml&returnUrl={{returnUrl}}"
            },
            defaultState: "long"
        };
        var outpatientPlan = {
            type: "include",
            include: {
                //label: "Conduite a tenir",
                template: "templates/outpatient-plan.page"
            }
        };

        return {
            standardOutpatient: {
                label: "Standard Outpatient Visit",
                elements: [
                    checkIn,
                    vitals,
                    primaryCareExam,
                    outpatientPlan
                ]
            },
            adultInitialOutpatient: {
                label: "Adult Initial Outpatient Visit",
                elements: [
                    checkIn,
                    vitals,
                    reviewAllergies,
                    primaryCareAdultHistory,
                    primaryCareExam,
                    outpatientPlan
                ]
            },
            pedsInitialOutpatient: {
                label: "Peds Initial Outpatient Visit",
                elements: [
                    checkIn,
                    vaccinations,
                    vitals,
                    reviewAllergies,
                    primaryCareAdultHistory,
                    primaryCareExam,
                    outpatientPlan
                ]
            }
        };
    }]);