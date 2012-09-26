<%@ include file="/include.jsp"
%><jsp:useBean id="bean" type="jetbrains.buildServer.serverSide.flaky.web.TestsAnalysisBean" scope="request"
/><c:set var="testAnalysisResult" value="${bean.testAnalysisResult}" />
<div>
  Last test analysis performed <b><bs:elapsedTime time="${testAnalysisResult.startDate}"/></b> (completed in ${bean.testAnalysisDuration}).
  Total tests analysed: <b>${testAnalysisResult.totalTests}</b>
</div>
