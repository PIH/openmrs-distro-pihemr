$( document ).ready(function() {

    $(".icon-arrow-down").siblings('div').hide();

    $(".icon-arrow-down").click(function () {
        var self = $(this);
        if(self.siblings('div').is(":visible")){
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