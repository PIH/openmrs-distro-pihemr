<%
    if (emrContext.authenticated && !emrContext.currentProvider) {
        throw new IllegalStateException("Logged-in user is not a Provider")
    }

    ui.decorateWith("emr", "standardEmrPage")
%>

<h1>
    ${ ui.message("mirebalais.outpatientVitals.title") }
</h1>

${ ui.includeFragment("emr", "widget/findPatient", [
        targetPageProvider: "mirebalais",
        targetPage: "outpatientvitals/patient"
]) }