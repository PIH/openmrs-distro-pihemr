<!-- we include this on the patient dashboard via the patientDashboard.includeFragments extension point, and this in turn includes
 the javascript to call the print wristband fragment -->

<%
    ui.includeJavascript("mirebalais", "wristband/printWristband.js")
%>