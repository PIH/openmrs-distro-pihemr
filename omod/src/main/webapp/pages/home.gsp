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
    });

    function labelFunction(item) {
        var id = item.patientId;
        if (item.primaryIdentifiers[0]) {
            id = item.primaryIdentifiers[0].identifier;
        }
        return id + ' - ' + item.preferredName.fullName;
    }
</script>

    ${ ui.includeFragment("emr", "field/autocomplete", [
            id: "search-field",
            label: "",
            placeholder: ui.message("emr.searchByNameOrIdOrScan"),
            formFieldName: "patient1",
            fragment: "findPatient",
            action: "search",
            itemValueProperty: "patientId",
            itemLabelFunction: "labelFunction"
    ])}

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