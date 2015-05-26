angular.module("filters", [ "uicommons.filters", "constants" ])

    .filter("with", [function() {
        return function(list, property, value, justOne) {
            var f = justOne ? _.find : _.filter;
            return f(list, function(candidate) {
                return candidate[property] && (candidate[property].uuid === value.uuid);
            });
        }
    }])

    .filter("byConcept", [function() {
        return function(listOfObs, concept, justOne) {
            var f = justOne ? _.find : _.filter;
            return f(listOfObs, function(candidate) {
                if (!candidate.concept) {
                    return false; // too small a representation to determine
                }
                return candidate.concept.uuid === concept.uuid;
            });
        }
    }])

    .filter("groupMember", [function() {
        return function(obs, concept) {
            if (!obs) {
                return null;
            }
            return _.find(obs.groupMembers, function(candidate) {
                return candidate.concept.uuid === concept.uuid;
            });
        }
    }])

    .filter("withCodedMember", [function() {
        return function(listOfObs, concept, codedValue, justOne) {
            var f = justOne ? _.find : _.filter;
            return f(listOfObs, function(candidate) {
                return _.find(candidate.groupMembers, function(member) {
                    return member.concept.uuid == concept.uuid
                        && member.value.uuid == codedValue.uuid;
                });
            });
        }
    }])

    .filter("obs", [ "omrs.displayFilter", function(displayFilter) {
        return function(obs, mode) {
            if (!obs) {
                return "";
            }
            var includeConcept = true;
            if ("value" === mode) {
                includeConcept = false;
            }
            var ret = includeConcept ?
                (displayFilter(obs.concept) + ": ") :
                "";
            if (obs.groupMembers) {
                ret += _.map(obs.groupMembers, function (member) {
                    // return displayFilter(member.valueCodedName ? member.valueCodedName : member.value);
                    return displayFilter(member.value);
                }).join(", ");
            } else {
                ret += displayFilter(obs.value);
            }
            return ret;
        }
    }])

    .filter("diagnosis", [ "omrs.displayFilter", "Concepts", function(displayFilter, Concepts) {
        return function(group) {
            if (!group) {
                return "";
            }
            var coded = _.find(group.groupMembers, function(it) {
                return it.concept.uuid == Concepts.codedDiagnosis.uuid;
            });
            if (coded) {
                // return displayFilter(coded.valueCodedName ? coded.valueCodedName : coded.value);
                return displayFilter(coded.value);
            }

            var nonCoded = _.find(group.groupMembers, function(it) {
                return it.concept.uuid == Concepts.nonCodedDiagnosis.uuid;
            });
            return nonCoded ? nonCoded.value : null;
        }
    }])

    // if val is not empty, we return before + val + after
    .filter("wrapWith", [ function() {
        return function(val, before, after) {
            if (val) {
                return before + val + after;
            }
            else {
                return val;
            }
        }
    }])

    .filter('allowedWithContext', [ "SessionInfo", function(SessionInfo) {
        return function(extensionList, context) {

            var util = {
                hasMemberWithProperty: function(list, prop, val) {
                    return _.any(list, function(it) {
                        return it[prop] == val;
                    });
                }
            };

            return _.filter(extensionList, function(it) {
                if (it.requiredPrivilege) {
                    if (!SessionInfo.hasPrivilege(it.requiredPrivilege)) {
                        return false;
                    }
                }
                if (it.require) {
                    // find a cleaner way to avoid polluting scope
                    var result = (function(expr) {
                        // also, clean up the server-side eval code so we can have normal js objects here without this hackiness
                        var sessionLocation = SessionInfo.get().sessionLocation;
                        sessionLocation.get = function(key) { return sessionLocation[key]; }

                        var user = new OpenMRS.UserModel(SessionInfo.get().user);
                        user.get = function(key) {
                            return user[key];
                        }
                        user.fn = {
                            hasPrivilege: function(priv) {
                                return user.hasPrivilege(priv);
                            }
                        };

                        return eval(expr);
                    })(it.require);
                    if (!result) {
                        return false;
                    }
                }
                return true;
            });
        }
    }]);