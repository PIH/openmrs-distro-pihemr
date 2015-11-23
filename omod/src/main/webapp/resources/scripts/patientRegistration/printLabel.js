
// TODO this is only a demo for Sierra Leone ID labe printing--clean up or remove if not used

function printLabel() {
    emr.getFragmentActionWithCallback('mirebalais', 'patientRegistration/label', 'printLabel'
        , { patientId: patient.id }
        , function(data) {
            if(data.success) {
                emr.successMessage(data.message);
            } else {
                emr.errorMessage(data.message);
            }
        }
    );
}