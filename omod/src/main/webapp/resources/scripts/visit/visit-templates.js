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
                        label: "Review Allergies...",
                        template: "templates/reviewAllergies.page"
                    }
                },
                {
                    type: "encounter",
                    encounter: {
                        encounterType: {
                            uuid: EncounterTypes.consultation.uuid
                        },
                        shortTemplate: "templates/clinicConsultShort.page",
                        longTemplate: "templates/clinicConsultLong.page"
                    },
                    action: {
                        label: "Consult Note",
                        href: "/{{contextPath}}/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{visit.patient.uuid}}&visitId={{visit.uuid}}&definitionUiResource=mirebalais:htmlforms/outpatientConsult.xml&returnUrl={{returnUrl}}"
                    },
                    defaultState: "long"
                }
            ]
        };

        return {
            standardOutpatient: standardOutpatientVisitTemplate
        };
    }]);