<%
	ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("mirebalais", "home.css")
%>

<input id="search-field" type="text" placeholder=" ${ ui.message("emr.searchByNameOrIdOrScan") }">

<hr class="separator"/>

<div id="apps">
    <% apps.each { app -> %>
    <div class="app">
        <a href="/${ contextPath }/${ app.homepageUrl }">
            <% if (app.iconUrl) { %>
                <img src="/${ contextPath }/${ app.iconUrl }"/>
            <% } %>
            ${ app.label }
        </a>
    </div>
    <% } %>
</div>