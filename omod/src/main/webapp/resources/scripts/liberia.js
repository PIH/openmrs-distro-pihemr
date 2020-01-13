var omrs = {
    toggleExposureDurationField: function (x, containerId) {
        if (x == 1) {
            $('#' + containerId).show();
        } else if (x == 0) {
            $('#' + containerId).hide();
        }
    },
    mentalHealthDiagnoses: ["Psychosis", "Bipolar disorder", "Schizophrenia", "Psychosomatic problems",
        "Psychosomatic disorder", "Hyperkinetic Behavior", "Conduct disorder", "Dementia", "Epilepsy",
        "Anxiety", "Anxiety disorder", "Post-traumatic stress disorder", "PTSD", "Psychological trauma", "Panic Attack",
        "Depression", "Manic episode", "Mood disorder"]
}

jQuery(document).ready(function ($) {

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

    $('#htmlformentry-condition').find('.autoCompleteText').on("autocompleteresponse", function (event, ui) {
        var content = Array.from(ui.content);
        var len = content.length;
        ui.content.splice(0, len);
        $(content).each(function (idx, el) {
            if (omrs.mentalHealthDiagnoses.includes(el.value)) {
                ui.content.push(el);
            }
        });
    }).on("autocompleteselect", function (event, ui) {
        if (ui.item.value == "Epilepsy") {
            $('#mh-epilepsy-section').show();
        } else {
            $('#mh-epilepsy-section').hide();
        }
    });
});