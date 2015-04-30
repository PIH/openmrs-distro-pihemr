package org.openmrs.module.mirebalais.require;

import org.openmrs.module.mirebalaismetadata.descriptor.LocationTagDescriptor;
import org.openmrs.module.mirebalaismetadata.descriptor.PrivilegeDescriptor;

public class RequireUtil {


    public static String patientHasActiveVisit() {
        return new String("typeof visit !== 'undefined' && visit != null && visit.active");
    }

    public static String patientVisitWithinPastThirtyDays() {
        return new String("typeof visit !== 'undefined' && visit != null && (Date.now () - visit.stopDatetimeInMilliseconds)/(1000 * 60 * 60 * 24) < 30");
    }

    public static String userHasPrivilege(PrivilegeDescriptor privilegeDescriptor) {
        return new String("user.get('fn').hasPrivilege('" + privilegeDescriptor.privilege() + "')");
    }

    public static String sessionLocationHasTag(LocationTagDescriptor descriptor) {
        return new String("util.hasMemberWithProperty(sessionLocation.get('tags'),'display','" + descriptor.name() + "')");
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
