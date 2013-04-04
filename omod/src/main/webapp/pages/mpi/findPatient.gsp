<%
    if (emrContext.authenticated && !emrContext.currentProvider) {
        throw new IllegalStateException("Logged-in user is not a Provider")
    }

    ui.decorateWith("emr", "standardEmrPage")

    def genderOptions = [ [label: ui.message("emr.gender.M"), value: 'M'],
            [label: ui.message("emr.gender.F"), value: 'F'] ]
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("mirebalais.mpi.title") }", link: "${ ui.pageLink("mirebalais", "mpi/findPatient") }" }
    ];

    jq(function() {

        jq(document).on('focus', ':input', function() {
            jq(this).attr('autocomplete', 'off');
        });
        jq(':input:enabled:visible:first').focus();

        jq("form").submit(function(){
            jq("#mpiSpinner").css("visibility", "visible");
            jq("#mpiSpinner").show();
        });

    });

</script>

<h1>
    ${ ui.message("mirebalais.mpi.title") }
</h1>

<form action="${ ui.pageLink("mirebalais", "mpi/findPatient") }">

    <fieldset>
        <legend>${ ui.message("mirebalais.mpi.searchById") }</legend>

        ${ ui.includeFragment("emr", "field/text", [
                label: (''),
                formFieldName: "id",
                initialValue: ('')
        ])}

        <div>
            <input type="submit" class="confirm" id="search-button" value="${ ui.message("emr.findPatient.search") }"  />
        </div>
    </fieldset>
</form>

<form action="${ ui.pageLink("mirebalais", "mpi/findPatient") }">
    <fieldset>
            <legend>${ ui.message("mirebalais.mpi.searchByName") }</legend>

            ${ ui.includeFragment("emr", "field/text", [
                    label: (''),
                    formFieldName: "name",
                    initialValue: ('')
            ])}

            ${ ui.includeFragment("emr", "field/radioButtons", [
                    label: ui.message("emr.gender"),
                    formFieldName: "gender",
                    options: genderOptions
            ])}

        <div>
            <input type="submit" class="confirm" id="search-button" value="${ ui.message("emr.findPatient.search") }"  />
        </div>

    </fieldset>
</form>

<div id="mpiSpinner" style="visibility: hidden;">
    <img src="${ui.resourceLink("mirebalais", "images/biggerloader.gif")}">
</div>

<% if (results) { %>
<table id="active-visits" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
    <tr>
        <th>${ ui.message("emr.patient.identifier") }</th>
        <th>${ ui.message("emr.person.name") }</th>
        <th>${ ui.message("emr.gender") }</th>
        <th>${ ui.message("emr.age") }</th>
        <th>${ ui.message("emr.person.address") }</th>
        <th>${ ui.message("mirebalais.mpi.import") }</th>
        <th></th>
    </tr>
    </thead>
    <tbody>

    <% results.each {
        def p = it.patient
    %>
    <tr id="patient-${ p.id }">
        <td>${ ui.format(p.patientIdentifier) }</td>
        <td>
            ${ ui.format(p) }
        </td>
        <td>
            ${ ui.message("emr.gender." + p.gender) }
        </td>
        <td>
            <% if (p.birthdate) { %>
            <% if (p.age > 0) { %>
            <span>${ ui.message("emr.ageYears", p.age) }</span>
            <% } else if (it.ageInMonths > 0) { %>
            <span>${ ui.message("emr.ageMonths", it.ageInMonths) }</span>
            <% } else { %>
            <span>${ ui.message("emr.ageDays", it.ageInDays) }</span>
            <% } %>
            <% } else { %>
            <span>${ ui.message("emr.unknownAge") }</span>
            <% } %>
        </td>
        <td>
            <% addressHierarchyLevels.each { addressLevel -> %>
            <% if(p.personAddress && p.personAddress[addressLevel]) { %>
            ${p.personAddress[addressLevel]}<% if(addressLevel != addressHierarchyLevels.last()){%>,<%}%>
            <% }%>
            <% } %>
        </td>
        <td>
            <% if (!it.localPatient) { %>
            <form action="${ ui.pageLink("mirebalais", "mpi/findPatient") }" method="post">
                <input type="hidden" name="remoteUuid" value="${ it.remoteUuid }"/>
                <input class="icon-download-alt" type="submit" value="${ ui.message("mirebalais.mpi.import") }"/>
            </form>
            <% } %>
        </td>
    </tr>
    <% } %>
    </tbody>
</table>
<% } %>

