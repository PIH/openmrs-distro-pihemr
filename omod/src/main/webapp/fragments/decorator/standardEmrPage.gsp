<%
	ui.includeCss("uilibrary", "jquery-ui.css")
	ui.includeCss("mirebalais", "emr.css");
	
	ui.includeJavascript("uilibrary", "jquery.js")
	ui.includeJavascript("uilibrary", "jquery-ui.js")
	
	ui.includeJavascript("uiframework.js")
	ui.includeJavascript("mirebalais", "emr.js");	
%>

${ ui.includeFragment("uilibrary", "standardIncludes") }

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

${ ui.includeFragment("uilibrary", "flashMessage") }

<div id="content">
	<%= config.content %>
</div>