<%@ include file="/include.jsp"
%><jsp:useBean id="project" type="jetbrains.buildServer.serverSide.SProject" scope="request"
/><jsp:useBean id="bean" type="jetbrains.buildServer.serverSide.flaky.web.TestsAnalysisBean" scope="request"
/>

<%--<input class="btn" type="button" value="Start" name="start" href="#" onclick="return BS.Flaky.start(this, '${project.projectId}');"/>--%>
<a href="#" onclick="return BS.Flaky.start(this, '${project.projectId}');">Start</a> with
<a href="#" onclick="return BS.Flaky.Dialog.show();">settings</a>
<forms:progressRing id="startAnalysisProgress" className="progressRingInline" style="display:none"/>
