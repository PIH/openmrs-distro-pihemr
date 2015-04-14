angular.module("visit-templates", ["constants"])

    .factory("VisitTemplates", [ "EncounterTypes", function(EncounterTypes) {
        var standardOutpatientVisitTemplate = {
            label: "Standard Outpatient Visit",
            elements: [
                {
                    type: "encounter",
                    encounter: {
                        encounterType: {
                            uuid: EncounterTypes.checkIn.uuid
                        },
                        shortTemplate: "templates/checkInShort.page"
                    },
                    action: {
                        label: "Check In",
                        href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=mirebalais:htmlforms/checkin.xml&returnUrl={{returnUrl}}"
                    },
                    defaultState: "short"
                },
                {
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
                        href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=mirebalais:htmlforms/vitals.xml&returnUrl={{returnUrl}}"
                    },
                    defaultState: "short"
                },
                {
                    type: "include",
                    include: {
                        label: "Review Allergies",
                        template: "templates/reviewAllergies.page"
                    }
                },
                {
                    type: "encounter",
                    encounter: {
                        encounterType: {
                            uuid: EncounterTypes.primaryCareHistory.uuid
                        },
                        longTemplate: "templates/primaryCareAdultHistoryLong.page"
                    },
                    action: {
                        label: "History (Adult)",
                        href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=mirebalais:htmlforms/zl/primary-care-adult-history.xml&returnUrl={{returnUrl}}"
                    },
                    defaultState: "long"
                },
                {
                    type: "encounter",
                    encounter: {
                        encounterType: {
                            uuid: EncounterTypes.primaryCareExam.uuid
                        },
                        longTemplate: "templates/defaultEncounterLong.page"
                    },
                    action: {
                        label: "Exam and Diagnosis (Adult)",
                        href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=mirebalais:htmlforms/zl/primary-care-adult-exam-dx.xml&returnUrl={{returnUrl}}"
                    },
                    defaultState: "long"
                },
                {
                    type: "include",
                    include: {
                        label: "Conduite a tenir",
                        template: "templates/outpatient-plan.page"
                    }
                }
            ]
        };

        return {
            standardOutpatient: standardOutpatientVisitTemplate
        };
    }]);