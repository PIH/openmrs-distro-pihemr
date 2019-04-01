package org.openmrs.module.mirebalais.require;

import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.PrivilegeDescriptor;
import org.openmrs.module.pihcore.config.Config;

public class RequireUtil {


    public static String patientHasActiveVisit() {
        return new String("typeof visit !== 'undefined' && visit != null && (visit.stopDatetime == null)");
    }

    public static String patientDoesNotActiveVisit() {
        return new String("typeof visit == 'undefined' || !visit || (visit.stopDatetime !== null)");
    }

    public static String patientVisitWithinPastThirtyDays(Config config) {
        if (config.isComponentEnabled("visitNote")) {
            return new String("typeof visit !== 'undefined' && visit != null && ((((new Date()).getTime() - new Date(visit.stopDatetime).getTime()) / (1000*60*60*24)) < 30 )");
        } else {
            return new String("typeof visit !== 'undefined' && visit != null && (Date.now () - visit.stopDatetimeInMilliseconds)/(1000 * 60 * 60 * 24) < 30");
        }
    }

    public static String userHasPrivilege(PrivilegeDescriptor privilegeDescriptor) {
        return new String("typeof user !== 'undefined' && hasMemberWithProperty(user.privileges, 'display', '" + privilegeDescriptor.privilege() + "')");
    }

    public static String sessionLocationHasTag(LocationTagDescriptor descriptor) {
        return new String("typeof sessionLocation !== 'undefined' && hasMemberWithProperty(sessionLocation.tags, 'display','" + descriptor.name() + "')");
    }

    public static String patientNotDead() {
        return new String("!patient.person.dead");
    }

    public static String patientIsAdult() {  // expects patient or visit to be in the context
        return new String("patient.person.age >= 15");
    }

    public static String patientIsChild() {  // expects patient or visit to be in the context
        return new String("patient.person.age < 15");
    }

    public static String patientAgeUnknown() {
        return new String("patient.person.age == null");
    }

    public static String patientIsFemale() {
        return new String("patient.person.gender == 'F'");
    }

    public static String patientIsMale() {
        return new String("patient.person.gender == 'M'");
    }

    public static String and(String ... args) {

        StringBuilder str = new StringBuilder();
        str.append("(");

        int i = 1;
        for (String arg : args) {
            str.append(arg);
            if (i < args.length) {
                str.append(" && ");
            }
            i++;
        }

        str.append(")");
        return str.toString();
    }

    public static String or(String ... args) {

        StringBuilder str = new StringBuilder();
        str.append("(");

        int i = 1;
        for (String arg : args) {
            str.append(arg);
            if (i < args.length) {
                str.append(" || ");
            }
            i++;
        }

        str.append(")");
        return str.toString();
    }
}
