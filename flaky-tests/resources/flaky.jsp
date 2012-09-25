<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
    taglib prefix="bs" tagdir="/WEB-INF/tags"
%><jsp:useBean id="bean" type="jetbrains.buildServer.serverSide.flaky.web.FlakyTestsBean" scope="request"

/><div id="flaky">
  <c:if test="${not bean.hasData}">
    No flaky tests
  </c:if>
  <c:if test="${bean.hasData}">
    <c:set var="flakyDetails" value="${bean.details}" scope="request"/>

    <c:if test="${bean.hasFlaky}">
      <c:set var="title">Flaky tests: ${bean.flakyTestsSize}</c:set>
      <bs:_collapsibleBlock title="${title}" id="flakyTestsBlock">
        <c:set var="groupBean" value="${bean.flakyTests}" scope="request"/>
        <c:set var="id" value="flaky" scope="request"/>
        <jsp:include page="flakyTestGroup.jsp"/>
      </bs:_collapsibleBlock>
    </c:if>

    <c:if test="${bean.hasAlwaysFailing}">
      <c:set var="title">Tests that always fail: ${bean.alwaysFailingTestsSize}</c:set>
      <bs:_collapsibleBlock title="${title}" id="alwaysFailingTestsBlock" collapsedByDefault="true">
        <div>
          The tests that have never been successful (typically <code>setUp</code> and <code>tearDown</code> methods).
        </div>
        <c:set var="groupBean" value="${bean.alwaysFailingTests}" scope="request"/>
        <c:set var="id" value="always" scope="request"/>
        <jsp:include page="flakyTestGroup.jsp"/>
      </bs:_collapsibleBlock>
    </c:if>
  </c:if>

  <div style="margin-top: 2em;">
    <a href="findFlaky.html?projectId=${bean.project.projectId}">Calculate now!</a> (make it a POST).
  </div>
  TODO: progress.
</div>
