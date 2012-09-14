<%
	ui.includeCss("mirebalais", "emr.css");
%>

<div id="application-header">
	Mirebalais EMR
	<% if (context.authenticated) { %>
		<span style="float: right">
			${ context.authenticatedUser.personName }
			|
			<a href="/${ contextPath }/logout">Log Out</a>
		</span>
	<% } %>
</div>

<div id="content">
	<%= config.content %>
</div>