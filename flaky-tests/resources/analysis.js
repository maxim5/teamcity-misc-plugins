BS.Flaky = {
  start: function(link, projectId) {
    var progressIcon = $j("#startAnalysisProgress");
    progressIcon.show();

    BS.ajaxRequest("analyzeTests.html", {
      method: "POST",
      parameters: {
        projectId: projectId
      },
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

BS.TestDetails._toggleDetails = BS.TestDetails.toggleDetails;
BS.TestDetails.toggleDetails = function(link, url) {
  var idx = url.indexOf("?");
  url = "/flakyTestDetails.html" + url.substr(idx);
  return this._toggleDetails(link, url);
};
