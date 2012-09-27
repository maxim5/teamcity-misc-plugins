BS.Flaky = {
  start: function(link, projectId) {
    var progressIcon = $j("#startAnalysisProgress");
    progressIcon.show();

    var parameters = {
      projectId: projectId,
      excludeBuildTypes: ($j("#buildTypes").val() || []).join(":"),
      period: $j("#period").val(),
      speedUpAlwaysFailing: $j("#speedUpAlwaysFailing").is(":checked")
    };

    BS.ajaxRequest("analyzeTests.html", {
      method: "POST",
      parameters: parameters,
      onComplete: function() {
        progressIcon.hide();
        BS.reload();
      }
    });
    return false;
  },

  scheduleReload: function() {
    setTimeout(function() {
      BS.reload();
    }, 3000);
  }
};

BS.Flaky.Dialog = OO.extend(BS.AbstractModalDialog, {
  show: function() {
    this.showCentered();
    return false;
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
    var period = $j("#period").val();
    if (!this.isInt(period)) {
      $j("#period").val(-1);
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
