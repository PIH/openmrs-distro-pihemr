<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("pih.app.labs.label") }"}
    ];
</script>

<div id="apps">

    <% if (context.hasPrivilege("App: labs")) { %>
        <a class="button app big" href="/owa/openmrs-owa-oncology/index.html">
            <i class="icon-beaker"></i>
            ${ ui.message("pih.app.labtracking") }
        </a>
    <% } %>
</div>