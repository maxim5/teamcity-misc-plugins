BS.Flaky = {
  start: function(link, projectId) {
    var progressIcon = $j("#startAnalysisProgress");
    progressIcon.show();

    var parameters = {
      projectId: projectId,
      excludeBuildTypes: ($j("#excludeBuildTypes").val() || []).join(":"),
      analyseTimePeriodDays: $j("#analyseTimePeriodDays").val(),
      analyseFullHistory: $j("#analyseFullHistory").is(":checked"),
      speedUpAlwaysFailing: $j("#speedUpAlwaysFailing").is(":checked")
    };

    BS.ajaxRequest("analyzeTests.html", {
      method: "POST",
      parameters: parameters,
      onComplete: function() {
        progressIcon.hide();
        $("flaky").refresh();
      }
    });
    return false;
  },

  scheduleReload: function() {
    setTimeout(function() {
      $("flaky").refresh();
    }, 3000);
  }
};

BS.Flaky.Dialog = OO.extend(BS.AbstractModalDialog, {
  show: function() {
    this.showCentered();
    this.init();
    return false;
  },

  init: function() {
    $j("#analyseFullHistory").change(function() {
      var checked = $j(this).is(":checked");
      $j("#analyseTimePeriodDays").prop("disabled", checked);
    });
  },

  getContainer: function() {
    return $('settingsFormDialog');
  },

  formElement: function() {
    return $('settingsForm');
  },

  close: function() {
    this.validate();
    this.doClose();
    return false;
  },

  validate: function() {
    var period = $j("#analyseTimePeriodDays").val();
    if (!this.isInt(period)) {
      $j("#analyseTimePeriodDays").val(-1);    // TODO: alert
    }
  },

  isInt: function(value){
    return (parseFloat(value) == parseInt(value)) && !isNaN(value);
  }
});

BS.TestDetails._toggleDetails = BS.TestDetails.toggleDetails;
BS.TestDetails.toggleDetails = function(link, url) {
  var idx = url.indexOf("?");
  url = "/flakyTestDetails.html" + url.substr(idx);
  return this._toggleDetails(link, url);
};
