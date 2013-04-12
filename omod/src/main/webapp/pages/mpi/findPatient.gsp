<%
    if (emrContext.authenticated && !emrContext.currentProvider) {
        throw new IllegalStateException("Logged-in user is not a Provider")
    }

    ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("uicommons", "mirebalais/masterPatientIndex.css")

    def genderOptions = [ [label: ui.message("emr.gender.M"), value: 'M'],
            [label: ui.message("emr.gender.F"), value: 'F'] ]

%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html">
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

<form action="${ ui.pageLink("mirebalais", "mpi/findPatient") }" id="master-patient-index">

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

<div id="mpiSpinner" style="display: none; text-align: center; padding: 20px;">
    <img src="${ui.resourceLink("mirebalais", "images/biggerloader.gif")}">
</div>

<% if (results) { %>
    <h2>Your search returned {%} patients:</h2>
    <ul class="patient-search-results">
    <% results.each {
        def p = it.patient
    %>
        <li id="patient-${ p.id }">
            <div class="patient-info">
                <h3>
                    ${ ui.format(p) }
                    <div class="identifiers">
                        <span>${ ui.format(p.patientIdentifier) }</span>
                    </div>
                </h3>
                <div class="demographics">
                    <span >
                        ${ ui.message("emr.gender." + p.gender) }
                    </span>
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
                    <span>
                        <% addressHierarchyLevels.each { addressLevel -> %>
                            <% if(p.personAddress && p.personAddress[addressLevel]) { %>
                                ${p.personAddress[addressLevel]}<% if(addressLevel != addressHierarchyLevels.last()){%>,<%}%>
                            <% }%>
                        <% } %>
                    </span>
                </div>
            </div>
        <% if (!it.localPatient) { %>
        <div class="import-patient">
            <form action="${ ui.pageLink("mirebalais", "mpi/findPatient") }" method="post">
                <input type="hidden" name="remoteUuid" value="${ it.remoteUuid }"/>
                <input type="submit" value="${ ui.message("mirebalais.mpi.import") }" class="proceed" />
            </form>
        </div>
        <% } %>
        </li>
        <% } %>
    </ul>
<% } %>

