<%
    if (!emrContext.currentProvider) {
        throw new IllegalStateException("Logged-in user is not a Provider")
    }

    ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("mirebalais", "outpatientvitals/findPatient.css")
%>
<script type="text/javascript">

    jq(function(){
        ko.applyBindings( sessionLocationModel, jq('#task-details').get(0) );
    });

</script>

<h1>
    Outpatient Vitals Capture
</h1>
<h3 id="task-details">
    <i class="icon-tasks"></i>
    at location <span data-bind="text: text"></span>
    as provider ${ ui.format(emrContext.currentProvider.person) }
</h3>

${ ui.includeFragment("emr", "widget/findPatient", [
        targetPageProvider: "mirebalais",
        targetPage: "outpatientvitals/patient"
]) }