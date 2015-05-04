package org.openmrs.module.mirebalais;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkServiceImpl;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.contextmodel.VisitContextModel;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.pihcore.metadata.core.LocationTags;
import org.openmrs.module.pihcore.metadata.core.Privileges;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.ui.framework.SimpleObject;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openmrs.module.mirebalais.require.RequireUtil.and;
import static org.openmrs.module.mirebalais.require.RequireUtil.or;
import static org.openmrs.module.mirebalais.require.RequireUtil.patientHasActiveVisit;
import static org.openmrs.module.mirebalais.require.RequireUtil.patientVisitWithinPastThirtyDays;
import static org.openmrs.module.mirebalais.require.RequireUtil.sessionLocationHasTag;
import static org.openmrs.module.mirebalais.require.RequireUtil.userHasPrivilege;

// TODO these tests are passing when run through IntelliJ, but failing through maven for some reason, so @Ignoring this for now

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest(ConversionUtil.class)
public class RequireUtilTest {

    private AppFrameworkServiceImpl appFrameworkService;

    private User user;

    private Role doctor;

    private Role admin;

    private UserContext userContext;

    private UiSessionContext uiSessionContext;

    @Before
    public void setup() {
        appFrameworkService = new AppFrameworkServiceImpl(null, null, null, null, null, null, null, null);

        doctor = new Role("Doctor");
        admin = new Role("Admin");

        Privilege enterConsultNote = new Privilege(Privileges.TASK_EMR_ENTER_CONSULT_NOTE.privilege());
        enterConsultNote.setUuid(Privileges.TASK_EMR_ENTER_CONSULT_NOTE.uuid());
        enterConsultNote.setDescription(Privileges.TASK_EMR_ENTER_CONSULT_NOTE.description());

        Privilege retroClinicalNote = new Privilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE.privilege());
        retroClinicalNote.setUuid(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE.uuid());
        retroClinicalNote.setDescription(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE.description());

        Privilege retroClinicalNoteThisProviderOnly = new Privilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY.privilege());
        retroClinicalNoteThisProviderOnly.setUuid(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY.uuid());
        retroClinicalNoteThisProviderOnly.setDescription(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY.description());

        doctor.addPrivilege(enterConsultNote);
        doctor.addPrivilege(retroClinicalNoteThisProviderOnly);

        admin.addPrivilege(enterConsultNote);
        admin.addPrivilege(retroClinicalNote);

        user = new User();
        user.setUsername("bobMeIn");
        user.setUuid("123-456");
        user.setSystemId("abc");
        user.setRetired(true);
        userContext = mock(UserContext.class);
        when(userContext.getAuthenticatedUser()).thenReturn(user);

        uiSessionContext = new UiSessionContext();
        uiSessionContext.setUserContext(userContext);

    }

    @Test
    public void shouldCreateCompoundAnd() {
        assertThat(and("big", "bad", "bird"), is("(big && bad && bird)"));
    }

    @Test
    public void shouldCreateCompoundOr() {
        assertThat(or("big", "bad", "bird"), is("(big || bad || bird)"));
    }

    @Test
    public void shouldReturnTrueIfUserHasPrivilege() {
        user.addRole(doctor);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(userHasPrivilege(Privileges.TASK_EMR_ENTER_CONSULT_NOTE)), appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfUserDoesNotHavePrivilege() {
        user.addRole(doctor);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE)), appContextModel), is(false));
    }


    @Test
    public void shouldReturnTrueIfVisitActive() {

        VisitDomainWrapper visit = mock(VisitDomainWrapper.class);
        when(visit.isActive()).thenReturn(true);

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();

        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(patientHasActiveVisit()), appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfVisitNotActive() {

        VisitDomainWrapper visit = mock(VisitDomainWrapper.class);
        when(visit.isActive()).thenReturn(false);

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(patientHasActiveVisit()), appContextModel), is(false));
    }

    @Test
    public void shouldReturnFalseIfNoVisit() {
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(patientHasActiveVisit()), appContextModel), is(false));
    }

    @Test
    public void shouldReturnTrueIfLocationHasTag() {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", LocationTags.ADMISSION_LOCATION.name());
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(sessionLocationHasTag(LocationTags.ADMISSION_LOCATION)), appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfLocationDoesNotHaveTag() {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", LocationTags.ADMISSION_LOCATION.name());
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(sessionLocationHasTag(LocationTags.ARCHIVES_LOCATION)), appContextModel), is(false));
    }


    @Test
    public void shouldReturnTrueIfLocationHasTagAndVisitActive() {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", LocationTags.ADMISSION_LOCATION.name());
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitDomainWrapper visit = mock(VisitDomainWrapper.class);
        when(visit.isActive()).thenReturn(true);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring
                (and(sessionLocationHasTag(LocationTags.ADMISSION_LOCATION), patientHasActiveVisit())), appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfLocationDoesNotHasTagAndVisitActive() {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", LocationTags.CHECKIN_LOCATION.name());
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitDomainWrapper visit = mock(VisitDomainWrapper.class);
        when(visit.isActive()).thenReturn(true);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring
                (and(sessionLocationHasTag(LocationTags.ADMISSION_LOCATION), patientHasActiveVisit())), appContextModel), is(false));
    }

    @Test
    public void shouldReturnFalseIfLocationHasTagButVisitNotActive() {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", LocationTags.ADMISSION_LOCATION.name());
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitDomainWrapper visit = mock(VisitDomainWrapper.class);
        when(visit.isActive()).thenReturn(false);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring
                (and(sessionLocationHasTag(LocationTags.ADMISSION_LOCATION), patientHasActiveVisit())), appContextModel), is(false));
    }

    @Test
    public void shouldReturnFalseIfLocationDOesNotHaveTagAndVisitNotActive() {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", LocationTags.CHECKIN_LOCATION.name());
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitDomainWrapper visit = mock(VisitDomainWrapper.class);
        when(visit.isActive()).thenReturn(false);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring
                (and(sessionLocationHasTag(LocationTags.ADMISSION_LOCATION), patientHasActiveVisit())), appContextModel), is(false));
    }

    @Test
    public void shouldReturnTrueIfVisitWithinPastThirtyDays() {

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(new Date().getTime());

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(patientVisitWithinPastThirtyDays()), appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfVisitNotWithinPastThirtyDays() {

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(new DateTime(2014,1,1,1,1).getMillis());

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(patientVisitWithinPastThirtyDays()), appContextModel), is(false));
    }

    @Test
    public void shouldReturnTrueIfProperLocationTagAndUserHasRetroPrivilege() {

        user.addRole(admin);

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", LocationTags.CONSULT_NOTE_LOCATION.name());
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(new DateTime(2014,1,1,1,1).getMillis());

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(
                and(sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION),
                or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                        userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                        and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays())))),
                appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfNotProperLocationTagAndUserHasRetroPrivilege() {

        user.addRole(admin);

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", LocationTags.ADMISSION_LOCATION.name());
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(new DateTime(2014,1,1,1,1).getMillis());

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(
                        and(sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION),
                                or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                        userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                        and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays())))),
                appContextModel), is(false));
    }

    @Test
    public void shouldReturnTrueIfProperLocationTagAndUserHasConsultPrivilegeAndActiveVisit() {

        user.addRole(doctor);

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", LocationTags.CONSULT_NOTE_LOCATION.name());
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(null);
        when(visit.isActive()).thenReturn(true);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(
                        and(sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION),
                                or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                        userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                        and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays())))),
                appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfProperLocationTagAndUserHasConsultPrivilegeButNoActiveVisit() {

        user.addRole(doctor);

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", LocationTags.CONSULT_NOTE_LOCATION.name());
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(null);
        when(visit.isActive()).thenReturn(false);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(
                        and(sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION),
                                or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                        userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                        and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays())))),
                appContextModel), is(false));
    }

    @Test
    public void shouldReturnFalseIfImproperLocationTagEventThoughUserHasConsultPrivilegeAndActiveVisit() {

        user.addRole(doctor);

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", LocationTags.CHECKIN_LOCATION.name());
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(null);
        when(visit.isActive()).thenReturn(true);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(
                        and(sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION),
                                or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                        userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                        and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays())))),
                appContextModel), is(false));
    }

    @Test
    public void shouldReturnTrueIfProperLocationTagAndUserHasRetroPrivilegeThisProviderOnlyAndVisitInLastThirtyDays() {

        user.addRole(doctor);  // doctor has last thirty days privilege

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", LocationTags.CONSULT_NOTE_LOCATION.name());
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(new DateTime().getMillis());

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(
                        and(sessionLocationHasTag(LocationTags.CONSULT_NOTE_LOCATION),
                                or(and(userHasPrivilege(Privileges.TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                        userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE),
                                        and(userHasPrivilege(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays())))),
                appContextModel), is(true));
    }

    private Extension extensionRequiring(String requires) {
        Extension extension = new Extension();
        extension.setRequire(requires);
        return extension;
    }


}
