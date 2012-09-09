<%
	ui.decorateWith("mirebalais", "standardEmrPage")
%>

Welcome to the Mirebalais EMR.

<br/><br/>

Your Apps:
<ul>
	<% apps.each { app -> %>
		<li>
			<a href="/${ contextPath }/${ app.homepageUrl }">
				<% if (app.iconUrl) { %>
					<img src="/${ contextPath }/${ app.iconUrl }"/>
				<% } %>
				${ app.label }
			</a>
		</li>
	<% } %>
</ul>