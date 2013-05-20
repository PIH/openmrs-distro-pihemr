<%
    if (emrContext.authenticated && !emrContext.currentProvider) {
        throw new IllegalStateException("Logged-in user is not a Provider")
    }

    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("mirebalais.outpatientVitals.title") }", link: "${ ui.pageLink("mirebalais", "outpatientvitals/findPatient") }" }
    ];
</script>

<h1>
    ${ ui.message("mirebalais.outpatientVitals.title") }
</h1>

${ ui.includeFragment("emr", "widget/findPatient", [
        targetPageProvider: "mirebalais",
        targetPage: "outpatientvitals/patient"
]) }