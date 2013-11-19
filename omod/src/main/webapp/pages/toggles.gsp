<%
	ui.decorateWith("appui", "standardEmrPage")
    ui.includeFragment("appui", "standardEmrIncludes")
    ui.includeCss("mirebalais", "toggles.css")
%>

<div id="toggles-container">
	<h1>List of Feature Toggles</h1>
	<ul>
    <% featureToggles.each { toggle -> %>
		<li>
		${ toggle.key }
    	<strong>${ toggle.value }</strong>
    	</li>
    <% } %>
    </ul>
</div>