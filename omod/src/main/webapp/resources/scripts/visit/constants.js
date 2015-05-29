angular.module('constants', [])

    .value('Concepts', {
        yes: {
            uuid: "3cd6f600-26fe-102b-80cb-0017a47871b2"
        },
        no: {
            uuid: "3cd6f86c-26fe-102b-80cb-0017a47871b2"
        },
        unknown: {
            uuid: "3cd6fac4-26fe-102b-80cb-0017a47871b2"
        },
        diagnosisConstruct: {
            uuid: "159947AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        codedDiagnosis: {
            uuid: "226ed7ad-b776-4b99-966d-fd818d3302c2"
        },
        nonCodedDiagnosis: {
            uuid: "970d41ce-5098-47a4-8872-4dd843c0df3f"
        },
        diagnosisOrder: {
            uuid: "159946AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        primaryOrder: {
            uuid: "159943AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        dispositionConstruct: {
            uuid: "164e9e1b-d26c-4a93-9bdf-af1ce4ae8fce"
        },
        disposition: {
            uuid: "c8b22b09-e2f2-4606-af7d-e52579996de3"
        },
        typeOfVisit: {
            uuid: "e2964359-790a-419d-be53-602e828dcdb9"
        },
        paymentInformation: {
            uuid: "7a6330f1-9503-465c-8d63-82e1ad914b47"
        },
        systolicBloodPressure: {
            uuid: "3ce934fa-26fe-102b-80cb-0017a47871b2"
        },
        diastolicBloodPressure: {
            uuid: "3ce93694-26fe-102b-80cb-0017a47871b2"
        },
        temperature: {
            uuid: "3ce939d2-26fe-102b-80cb-0017a47871b2"
        },
        height: {
            uuid: "3ce93cf2-26fe-102b-80cb-0017a47871b2"
        },
        weight: {
            uuid: "3ce93b62-26fe-102b-80cb-0017a47871b2"
        },
        heartRate: {
            uuid: "3ce93824-26fe-102b-80cb-0017a47871b2"
        },
        respiratoryRate: {
            uuid: "3ceb11f8-26fe-102b-80cb-0017a47871b2"
        },
        oxygenSaturation: {
            uuid: "3ce9401c-26fe-102b-80cb-0017a47871b2"
        },
        pastMedicalHistoryConstruct: {
            uuid: "1633AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        pastMedicalHistoryFinding: {
            uuid: "1628AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        pastMedicalHistoryPresence: {
            uuid: "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        pastMedicalHistoryComment: {
            uuid: "160221AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        familyHistoryConstruct: {
            uuid: "160593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        familyHistoryRelative: {
            uuid: "3ce18156-26fe-102b-80cb-0017a47871b2"
        },
        familyHistoryDiagnosis: {
            uuid: "160592AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        familyHistoryPresence: {
            uuid: "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        familyHistoryComments: {
            uuid: "160618AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        vaccinationHistoryConstruct: {
            uuid: "74260088-9c83-41d5-b92b-03a41654daaf"
        },
        vaccinationGiven: {
            uuid: "2dc6c690-a5fe-4cc4-97cc-32c70200a2eb"
        },
        vaccinationSequenceNumber: {
            uuid: "ef6b45b4-525e-4d74-bf81-a65a41f3feb9"
        },
        vaccinationDate: {
            uuid: "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        bcgVaccination: {
            uuid: "3cd4e004-26fe-102b-80cb-0017a47871b2"
        },
        polioVaccination: {
            uuid: "3cd42c36-26fe-102b-80cb-0017a47871b2"
        },
        pentavalentVaccination: {
            uuid: "1423AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        rotavirusVaccination: {
            uuid: "83531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        measlesRubellaVaccination: {
            uuid: "162586AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        diptheriaTetanusVaccination: {
            uuid: "3ccc6b7c-26fe-102b-80cb-0017a47871b2"
        },
        hemoglobin: {
            uuid: "3ccc7158-26fe-102b-80cb-0017a47871b2"
        },
        hematocrit: {
            uuid: "3cd69a98-26fe-102b-80cb-0017a47871b2"
        },
        clinicalManagementPlanComment: {
            uuid: "162749AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        returnVisitDate: {
            uuid: "3ce94df0-26fe-102b-80cb-0017a47871b2"
        }
    })

    .value('EncounterTypes', {
        checkIn: {
            uuid: "55a0d3ea-a4d7-4e88-8f01-5aceb2d3c61b"
        },
        vitals: {
            uuid: "4fb47712-34a6-40d2-8ed3-e153abbd25b7"
        },
        primaryCareHistory: {
            uuid: "ffa148de-2c88-4828-833e-f3788991543d"
        },
        labResults: {
            uuid: "4d77916a-0620-11e5-a6c0-1697f925ec7b"
        },
        primaryCareExam: {
            uuid: "0a9facff-fdc4-4aa9-aae0-8d7feaf5b3ef"
        },
        primaryCareDx: {
            uuid: "09febbd8-03f1-11e5-8418-1697f925ec7b"
        },
        consultation: {
            uuid: "92fd09b4-5335-4f7e-9f63-b2a663fd09a6"
        },
        consultationPlan: {
            uuid: "e0aaa214-1d4b-442a-b527-144adf025299"
        }
    })

    .value('VisitAttributeTypes', {
        visitTemplate: {
            uuid: "f7b07c80-27c3-49de-8830-cb9e3e805eeb"
        }
    })