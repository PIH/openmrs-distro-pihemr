angular.module("encounterTransaction", [ "ngResource", "uicommons.common" ])

    .factory('EncounterTransaction', [ "$resource", function($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH  + "/ws/rest/emrapi/encounter/:uuid", {
            uuid: '@uuid'
        }, {
            active: { method:'GET', isArray:false, url: "/" + OPENMRS_CONTEXT_PATH  + "/ws/rest/emrapi/encounter/active" }
        });
    }])

    .factory("EncounterTransactionService", [ "EncounterTransaction", function(EncounterTransaction) {

        return {
            getActiveEncounterTransaction: function(params) {
                params = _.mapObject(params, function(val) {
                    return (val && val.uuid) ? val.uuid : val;
                });
                return EncounterTransaction.$active(params);
            }
        }
    }]);