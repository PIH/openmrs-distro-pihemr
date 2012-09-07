<%
	ui.includeCss("uilibrary", "jquery-ui.css")
	ui.includeCss("mirebalais", "emr.css");
	
	ui.includeJavascript("uilibrary", "jquery.js")
	ui.includeJavascript("uilibrary", "jquery-ui.js")
	
	ui.includeJavascript("uiframework.js")
	ui.includeJavascript("mirebalais", "emr.js");	
%>

${ ui.includeFragment("standardIncludes") }

<div id="application-header">PIH EMR</div>

${ ui.includeFragment("flashMessage") }

<div id="content">
	<%= config.content %>
</div>