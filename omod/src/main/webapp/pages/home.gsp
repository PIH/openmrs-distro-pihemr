<%
	ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("mirebalais", "home.css")

    def htmlSafeId = { app ->
        "${ app.id.replace(".", "-") }-app"
    }
%>

<div id="home-container">
    ${ ui.includeFragment("emr", "widget/findPatient") }

    <div id="apps">
        <% apps.each { app -> %>

            <a id="${ htmlSafeId(app) }" href="/${ contextPath }/${ app.homepageUrl }" class="button app big">
                <% if (app.iconUrl) { %>
                    <i class="${ app.iconUrl }"></i>
                <% } %>
                ${ app.label }
            </a>

        <% } %>
    </div>

</div>