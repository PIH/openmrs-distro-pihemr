<%
	ui.decorateWith("emr", "standardEmrPage")
%>

<input id="search-field" type="text" placeholder="Search by name or ID or scan card">

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