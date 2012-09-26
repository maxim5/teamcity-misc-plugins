<%@ include file="/include.jsp" %>
<jsp:useBean id="project" type="jetbrains.buildServer.serverSide.SProject" scope="request"/>
<a href="#" onclick="return BS.Flaky.start(this, '${project.projectId}');">Start</a>
<forms:progressRing id="startAnalysisProgress" className="progressRingInline" style="display:none"/>
