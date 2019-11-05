var omrs = {
    toggleExposureDurationField: function (x, containerId) {
        if (x == 1) {
            $('#' + containerId).show();
        } else if (x == 0) {
            $('#' + containerId).hide();
        }
    }
}

$(document).ready(function () {

    $(".icon-arrow-down").click(function () {
        var self = $(this);
        if (self.siblings('div').is(":visible")) {
            self.siblings("div").hide("fast", function () {
                self.removeClass('icon-arrow-up').addClass('icon-arrow-down');
            });
        } else {
            self.siblings("div").show("fast", function () {
                self.removeClass('icon-arrow-down').addClass('icon-arrow-up');
            });
        }
    });
});