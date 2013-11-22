<%
	ui.decorateWith("appui", "standardEmrPage")
    ui.includeFragment("appui", "standardEmrIncludes")
    ui.includeCss("mirebalais", "toggles.css")
%>

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