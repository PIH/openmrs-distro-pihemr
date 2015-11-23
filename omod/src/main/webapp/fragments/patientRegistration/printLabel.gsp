<!-- we include this on the patient dashboard via the patientDashboard.includeFragments extension point, and this in turn includes
 the javascript to call the print id label fragment -->

 <!-- TODO this is only a demo for Sierra Leone ID labe printing--clean up or remove if not used -->

<%
    ui.includeJavascript("mirebalais", "patientRegistration/printLabel.js")
%>