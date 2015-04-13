<%
	ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("mirebalais", "home.css")

    def htmlSafeId = { extension ->
        "${ extension.id.replace(".", "-") }-app"
    }
%>


<script type="text/javascript">
    jq(function() {
        jq('#patient-search').focus();

        // make sure we reload the page if the location is changes; this custom event is emitted by by the location selector in the header
        jq(document).on('sessionLocationChanged', function() {
            window.location.reload();
        });
    });
</script>



<div id="home-container">

    <% if (sessionContext.currentUser.hasPrivilege(privilegeSearchForPatients)) { %>

        ${ ui.message("mirebalais.searchPatientHeading") }
        ${ ui.includeFragment("coreapps", "patientsearch/patientSearchWidget",
                [ afterSelectedUrl: dashboardUrl,
                  showLastViewedPatients: 'false' ])}
    <% } %>


    <div id="apps">
        <% extensions.each { extension -> %>

            <a id="${ htmlSafeId(extension) }" href="/${ contextPath }/${ extension.url }" class="button app big">
                <% if (extension.icon) { %>
                    <i class="${ extension.icon }"></i>
                <% } %>
                ${ ui.message(extension.label) }
            </a>

        <% } %>
    </div>

</div>