<%
	ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("mirebalais", "home.css")

    def htmlSafeId = { extension ->
        "${ extension.id.replace(".", "-") }-app"
    }
%>

<div id="home-container">



    <% if (featureToggles.isFeatureEnabled("newPatientSearchWidget")) { %>

        <% if (sessionContext.currentUser.hasPrivilege(privilegeSearchForPatients)) { %>

            ${ ui.message("mirebalais.searchPatientHeading") }
            ${ ui.includeFragment("coreapps", "patientsearch/patientSearchWidget",
                    [ afterSelectedUrl: '/coreapps/patientdashboard/patientDashboard.page?patientId={{patientId}}',
                      showLastViewedPatients: 'false' ])}
        <% } %>

    <% } else {%>
        ${ ui.includeFragment("emr", "widget/findPatient") }
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