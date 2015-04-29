<style>
    .printing-successful {
        font-weight:bold;
        color: green;
    }
    .printing-failed {
        font-weight:bold;
        color: red;
    }
</style>

<div class="info-section">

    <div class="info-header">
        <i class="${ app.icon }"></i>
        <h3>${ ui.message(app.label).toUpperCase() }</h3>
    </div>

    <div id="idcard-status-section" class="info-body summary-section">

        <% boolean neverAttempted = (status.numSuccessful == 0 && status.numFailed == 0) %>

        <div>
            <h3><%= ui.message("zl.registration.patient.idcard.latestStatus") %></h3>
            <p class="left">
                <% if (neverAttempted) { %>
                    <span class="printing-failed"><%= ui.message("zl.registration.patient.idcard.latestStatus.neverAttempted") %></span>
                <% } else if (status.latestAttemptSuccessful) { %>
                    <span class="printing-successful"><%= ui.message("zl.registration.patient.idcard.latestStatus.successful") %></span>
                <% } else { %>
                    <span class="printing-failed"><%= ui.message("zl.registration.patient.idcard.latestStatus.failed") %></span>
                <% } %>
            </p>
        </div>

        <% if (!neverAttempted) { %>
            <div>
                <h3>
                    <% if (status.latestAttemptSuccessful) { %>
                        <%= ui.message("zl.registration.patient.idcard.latestStatus.datePrinted") %>
                    <% } else { %>
                        <%= ui.message("zl.registration.patient.idcard.latestStatus.dateFailed") %>
                    <% } %>
                </h3>
                <p class="left">${status.latestAttemptDate}</p>
            </div>

            <div>
                <h3><%= ui.message("zl.registration.patient.idcard.history") %></h3>
                <p class="left"><%= ui.message("zl.registration.patient.idcard.numCardsIssued", status.numSuccessful) %></p>
            </div>
        <% } %>
        
    </div>
</div>