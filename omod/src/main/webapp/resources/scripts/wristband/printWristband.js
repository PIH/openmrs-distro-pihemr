
function printWristband() {
    emr.getFragmentActionWithCallback('mirebalais', 'wristband', 'printWristband'
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