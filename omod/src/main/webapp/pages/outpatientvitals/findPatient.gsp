<%
    if (emrContext.authenticated && !emrContext.currentProvider) {
        throw new IllegalStateException("Logged-in user is not a Provider")
    }

    ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("mirebalais", "outpatientvitals/findPatient.css")
%>

<h1>
    ${ ui.message("mirebalais.app.mirebalais.outpatientVitals.label") }
</h1>

${ ui.includeFragment("emr", "widget/findPatient", [
        targetPageProvider: "mirebalais",
        targetPage: "outpatientvitals/patient"
]) }