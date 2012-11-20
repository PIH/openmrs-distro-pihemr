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
        <div id="sessionLocation">
            <% locations.each { %>
                <span class="locationOption" value="${it.id}">${ui.format(it)}</span>
            <% } %>
        </div>
        <input type="hidden" id="sessionLocationInput" name="sessionLocation"
               <% if (lastSessionLocation != null) { %> value="${lastSessionLocation.id}" <% } %> />

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
    updateSelectedOption = function() {
        \$('#sessionLocation .locationOption').removeClass('selected');
        \$('#sessionLocation .locationOption[value|=' + \$('#sessionLocationInput').val() + ']').addClass('selected');
    };

    \$(function() {
        updateSelectedOption();

        \$('#sessionLocation .locationOption').click( function() {
            \$('#sessionLocationInput').val(\$(this).attr("value"));
            updateSelectedOption();
        });

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