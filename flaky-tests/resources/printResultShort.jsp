<jsp:useBean id="testAnalysisResult" type="jetbrains.buildServer.serverSide.flaky.data.TestAnalysisResult" scope="request"

/>
<div>
  Start time: ${testAnalysisResult.startDate},
  Finish time: ${testAnalysisResult.finishDate}.
</div>
