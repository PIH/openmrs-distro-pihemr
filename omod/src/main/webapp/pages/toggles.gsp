<%
	ui.decorateWith("appui", "standardEmrPage")
    ui.includeFragment("appui", "standardEmrIncludes")
    ui.includeCss("pihcore", "toggles.css")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
	var breadcrumbs = [
		{ icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
		{ label: "${ ui.message("coreapps.app.system.administration.label") }", link: '/' + OPENMRS_CONTEXT_PATH + '/coreapps/systemadministration/systemAdministration.page' },
		{ label: "${ ui.message("pih.app.featureToggles.label") }", link: "${ ui.pageLink("mirebalais", "toggles.page") }" }
	];
</script>

<div id="toggles-container">
	<h1>List of Feature Toggles</h1>
	<table>
	<thead>
		<th>Property</th>
		<th>Toggle</th>
	</thead>
	<tbody>
    <% featureToggles.each { toggle -> %>
		<tr>
		<td>${ toggle.key }</td>
    	<td><strong>${ toggle.value }</strong></td>
    	</tr>
    <% } %>
    </tbody>
    </table>
</div>