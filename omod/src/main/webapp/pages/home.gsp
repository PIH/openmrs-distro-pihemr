<%
	ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("mirebalais", "home.css")

    def htmlSafeId = { app ->
        "${ app.id.replace(".", "-") }-app"
    }
%>

<input id="search-field" type="text" placeholder=" ${ ui.message("emr.searchByNameOrIdOrScan") }">

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