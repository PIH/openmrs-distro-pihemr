<%
	ui.decorateWith("emr", "standardEmrPage")
%>
<style type="text/css">

    .app {
        float: left;
        border: 1px black solid;
        background-color: #d3d3d3;
        border-radius: 0.3em;
        margin: 10px;
        padding: 10px;
        cursor: pointer;
    }

    .app a {
        text-decoration: none;
    }

    .app-icon {
        float: left;
        width: 64px;
        height: 64px;
        margin-right: 5px;
    }

    .app-label {
        font-size: 1.4em;
    }
</style>

Welcome to the Mirebalais EMR.

<br/><br/>

Your Apps:
<div id="apps">
	<% apps.each { app -> %>
		<div class="app">
			<a href="/${ contextPath }/${ app.homepageUrl }?appId=${ app.id }">
                <span class="app-icon">
                    <% if (app.iconUrl) { %>
                        <img width="64" height="64" src="/${ contextPath }/${ app.iconUrl }"/>
                    <% } %>
                </span>
                <span class="app-label">
				    ${ app.label }
                </span>
			</a>
		</div>
	<% } %>
</div>