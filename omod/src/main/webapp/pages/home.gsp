<%
	ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("uicommons", "mirebalais/home.css")

    def htmlSafeId = { app ->
        "${ app.id.replace(".", "-") }-app"
    }
%>

<div id="home-container">
    ${ ui.includeFragment("emr", "widget/findPatient") }

    <div id="apps">
        <% apps.each { app -> %>

            <a id="${ htmlSafeId(app) }" href="/${ contextPath }/${ app.url }" class="button app big">
                <% if (app.iconUrl) { %>
                    <i class="${ app.iconUrl }"></i>
                <% } %>
                ${ ui.message(app.label) }
            </a>

        <% } %>
    </div>

</div>