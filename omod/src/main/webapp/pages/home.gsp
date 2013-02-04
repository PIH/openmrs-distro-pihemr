<%
	ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("mirebalais", "home.css")

    def htmlSafeId = { app ->
        "${ app.id.replace(".", "-") }-app"
    }
%>
<script type="text/javascript">
    jq(function() {
        jq('#search-field-search').first().focus();

        jq('#search-form').submit( function() {
            navigateToPatient(parseInt(jq('#search-field-value').val()));
            return false;
        });
    });

    function labelFunction(item) {
        var id = item.patientId;
        if(id > 0){
            if (item.primaryIdentifiers[0]) {
                id = item.primaryIdentifiers[0].identifier;
            }
            return id + ' - ' + item.preferredName.fullName
                    + ' - ' + item.gender
                    + ' - ' + item.age;
        }
        return false;
    }

    function navigateFunction(item) {
        if(item !== null && item.patientId > 0){
            navigateToPatient(item.patientId);
        }
        return true;
    }

    function navigateToPatient(patientId) {
        if(patientId > 0) {
            emr.navigateTo({
                provider: 'emr',
                page: 'patient',
                query: { patientId: patientId }
            });
        }
    }
</script>

<div id="home-container">
    <form id="search-form">
        <label>
            <i class="icon-search small"></i>
            ${ui.message("emr.searchPatientHeading")}</label>
        <div class="search-input">
        ${ ui.includeFragment("emr", "field/autocomplete", [
                id: "search-field",
                label: "",
                placeholder: ui.message("emr.searchByNameOrIdOrScan"),
                formFieldName: "patient1",
                fragment: "findPatient",
                action: "search",
                maxResults: 10,
                itemValueProperty: "patientId",
                itemLabelFunction: "labelFunction",
                onExactMatchFunction:"navigateFunction"
        ])}
        </div>
    </form>

<div id="apps">
    <% apps.each { app -> %>

        <a id="${ htmlSafeId(app) }" href="/${ contextPath }/${ app.homepageUrl }" class="button big">
            <% if (app.iconUrl) { %>
                <i class="${ app.iconUrl }"></i>
            <% } %>
            ${ app.label }
        </a>

    <% } %>
</div>

</div>