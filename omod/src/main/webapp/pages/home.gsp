<%
	ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("mirebalais", "home.css")

    def htmlSafeId = { extension ->
        "${ extension.id.replace(".", "-") }-app"
    }
%>


<script type="text/javascript">
    jq(function() {
        jq('#patient-search').first().focus();
    });
</script>



<div id="home-container">

    <% if (sessionContext.currentUser.hasPrivilege(privilegeSearchForPatients)) { %>

        ${ ui.message("mirebalais.searchPatientHeading") }
        ${ ui.includeFragment("coreapps", "patientsearch/patientSearchWidget",
                [ afterSelectedUrl: '/coreapps/patientdashboard/patientDashboard.page?patientId={{patientId}}',
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