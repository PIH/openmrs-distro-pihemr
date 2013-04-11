<!-- This is the login page -->
<%
    ui.includeFragment("emr", "standardEmrIncludes")

    ui.setPageTitle(ui.message("mirebalais.login.welcomeHeading"))

    ui.includeCss("uicommons", "mirebalais/login.css")
%>

${ ui.includeFragment("emr", "header") }

<div id="body-wrapper" class="container">
    <div id="content">

        <h1>${ ui.message("mirebalais.login.welcomeHeading") }</h1>

        <form id="login-form" method="post" autocomplete="off">
            <fieldset>

                <legend>
                    <i class="icon-lock small"></i>
                    ${ ui.message("mirebalais.login.loginHeading") }
                </legend>

                ${ ui.includeFragment("emr", "infoAndErrorMessage") }

                <p class="left">
                    <label for="username">
                        ${ ui.message("mirebalais.login.username") }:
                    </label>
                    <input id="username" type="text" name="username" placeholder="${ ui.message("mirebalais.login.username.placeholder") }"/>
                </p>

                <p class="left">
                    <label for="password">
                        ${ ui.message("mirebalais.login.password") }:
                    </label>
                    <input id="password" type="password" name="password" placeholder="${ ui.message("mirebalais.login.password.placeholder") }"/>
                </p>

                <p class="clear">
                    <label for="sessionLocation">
                        ${ ui.message("mirebalais.login.sessionLocation") }:
                    </label>
                    <ul id="sessionLocation" class="select">
                        <% locations.sort { ui.format(it) }.each { %>
                            <li value="${it.id}">${ui.format(it)}</li>
                        <% } %>
                    </ul>
                </p>

                <input type="hidden" id="sessionLocationInput" name="sessionLocation"
                       <% if (lastSessionLocation != null) { %> value="${lastSessionLocation.id}" <% } %> />

                <p>
                    <input id="login-button" class="confirm" type="submit" value="${ ui.message("mirebalais.login.button") }"/>
                </p>
                <p>
                    <a id="cant-login" href="javascript:void(0)">
                        <i class="icon-question-sign small"></i>
                        ${ ui.message("mirebalais.login.cannotLogin") }
                    </a>
                </p>

            </fieldset>

        </form>

    </div>
</div>

<div id="cannot-login-popup" class="dialog" style="display: none">
    <div class="dialog-header">
        <i class="icon-info-sign"></i>
        <h3>${ ui.message("mirebalais.login.cannotLogin") }</h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">${ ui.message("mirebalais.login.cannotLoginInstructions") }</p>

        <button class="confirm">${ ui.message("emr.okay") }</button>
    </div>
</div>


<script type="text/javascript">
	document.getElementById('username').focus();
    updateSelectedOption = function() {
        \$('#sessionLocation li').removeClass('selected');
        \$('#sessionLocation li[value|=' + \$('#sessionLocationInput').val() + ']').addClass('selected');
    };

    \$(function() {
        updateSelectedOption();

        \$('#sessionLocation li').click( function() {
            \$('#sessionLocationInput').val(\$(this).attr("value"));
            updateSelectedOption();
        });

        var cannotLoginController = emr.setupConfirmationDialog({
            selector: '#cannot-login-popup',
            actions: {
                confirm: function() {
                    cannotLoginController.close();
                }
            }
        });
        \$('a#cant-login').click(function() {
            cannotLoginController.show();
        })
    });
</script>