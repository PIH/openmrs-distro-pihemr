<%
	ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("home.css")

    def htmlSafeId = { app ->
        "${ app.id.replace(".", "-") }-app"
    }
%>
<script type="text/javascript">
    jq(function() {
        jq('#search-field-search').first().focus();

        jq('#search-form').submit( function() {
            navigateToPatient(jq('#search-field-value').val());
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
    }

    function navigateFunction(item) {
        if(item !== null && item.patientId > 0){
            navigateToPatient(item.patientId);
        }
        return true;
    }

    function navigateToPatient(patientId) {
        if(patientId.length > 0 && (parseInt(patientId, 10) > 0)) {
            emr.navigateTo({
                provider: 'emr',
                page: 'patient',
                query: { patientId: patientId }
            });
        }
    }
</script>


<form id="search-form">
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
</form>

<hr class="separator"/>

<div id="apps">
    <% apps.each { app -> %>
    <div class="app" id="${ htmlSafeId(app) }">
        <a href="/${ contextPath }/${ app.homepageUrl }">
            <% if (app.iconUrl) { %>
                <img src="/${ contextPath }/${ app.iconUrl }"/>
            <% } %>
            ${ app.label }
        </a>
    </div>
    <% } %>
</div>