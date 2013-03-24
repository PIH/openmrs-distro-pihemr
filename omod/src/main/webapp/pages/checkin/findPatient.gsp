<%
    if (emrContext.authenticated && !emrContext.currentProvider) {
        throw new IllegalStateException("Logged-in user is not a Provider")
    }

    ui.decorateWith("emr", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("mirebalais.app.patientRegistration.checkin.label") }", link: "${ ui.pageLink("mirebalais", "checkin/findPatient") }" }
    ];
</script>

<h1>
    ${ ui.message("mirebalais.checkin.title") }
</h1>

${ ui.includeFragment("emr", "widget/findPatient", [
        targetPageProvider: "mirebalais",
        targetPage: "checkin/patient"
]) }