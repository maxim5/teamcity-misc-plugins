<%@ include file="/include.jsp"
%><jsp:useBean id="project" type="jetbrains.buildServer.serverSide.SProject" scope="request"
/><jsp:useBean id="bean" type="jetbrains.buildServer.serverSide.flaky.web.TestsAnalysisBean" scope="request"

/><c:set var="caption" value="${bean.testAnalysisEverStarted ? 'Start Again' : 'Start Now'}"/>

<div style="margin-bottom: 3em;">
  <span class="buttonWrapper">
    <button class="btn btn_mini btn_main" onclick="return BS.Flaky.start('${project.projectId}');">${caption}</button>
    <button class="btn btn_mini btn_append" onclick="return BS.Flaky.Dialog.show();" title="Run custom build">...</button>
  </span>
  <forms:progressRing id="startAnalysisProgress" className="progressRingInline" style="display:none"/>
</div>

<%--<input class="btn" type="button" value="Start" name="start" href="#" onclick="return BS.Flaky.start(this, '${project.projectId}');"/>--%>
<%--<a href="#" onclick="return BS.Flaky.start('${project.projectId}');">Start</a> with
<a href="#" onclick="return BS.Flaky.Dialog.show();">settings</a>--%>