<!-- This is the login page -->
<%
    ui.includeFragment("emr", "standardEmrIncludes")

    ui.setPageTitle(ui.message("mirebalais.login.welcomeHeading"))

    ui.includeCss("mirebalais", "login.css")
%>

<div id="content" class="container">

    <img id="pih-logo" src="${ ui.resourceLink("mirebalais", "images/PIH_ZL_plum_940.jpg") }"/>

    <h1>${ ui.message("mirebalais.login.welcomeHeading") }</h1>

    <p id="welcome-description">${ ui.message("mirebalais.login.welcomeDescription") }</p>

    <form id="login-form" class="standard-vertical-form" method="post" autocomplete="off">
        <h2>${ ui.message("mirebalais.login.loginHeading") }</h2>

        ${ ui.includeFragment("emr", "infoAndErrorMessage") }

        <label for="username">
            ${ ui.message("mirebalais.login.username") }
        </label>
        <input id="username" type="text" name="username"/>

        <label for="password">
            ${ ui.message("mirebalais.login.password") }
        </label>
        <input id="password" type="password" name="password"/>

        <label for="sessionLocation">
            ${ ui.message("mirebalais.login.sessionLocation") }
        </label>
        <select id="sessionLocation" name="sessionLocation" size="${ locations.size() < 8 ? locations.size() : 8 }">
            <% locations.each { %>
                <option <% if (it == lastSessionLocation) { %> selected="true" <% } %> value="${ it.id }">${ ui.format(it) }</option>
            <% } %>
        </select>

        <input id="login-button" type="submit" value="${ ui.message("mirebalais.login.button") }"/>

        <div id="cannot-login">
            <a href="javascript:void(0)">${ ui.message("mirebalais.login.cannotLogin") }</a>
            <div id="cannot-login-popup">
                ${ ui.message("mirebalais.login.cannotLoginInstructions") }
            </div>
        </div>

    </form>

</div>

<script type="text/javascript">
	document.getElementById('username').focus();

    \$(function() {
        \$('#cannot-login-popup').dialog({
            autoOpen: false,
            modal: true,
            title: "${ ui.message("mirebalais.login.cannotLogin") }",
            width: 750,
            height: 200
        });
        \$('#cannot-login > a').click(function() {
            \$('#cannot-login-popup').dialog('open');
        })
    });
</script>