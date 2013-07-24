<%
    config.require("label")
    config.require("formFieldName")


    def options = []

    options[0]=  [ label: ui.message("disposition.emrapi.transferOutOfHospital.sites.zlSite"), value: "PIH:ZL-supported site", selected: false ]
    options[1]=  [ label: ui.message("disposition.emrapi.transferOutOfHospital.sites.nonZlSite"), value: "PIH:Non-ZL supported site", selected: false ]

%>
<input type="hidden" name="concept" value="PIH:Transfer out location">

${ ui.includeFragment("uicommons", "field/dropDown", [ options: options ] << config) }
