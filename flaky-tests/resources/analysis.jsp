<%@ include file="/include.jsp" %><%@
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
    taglib prefix="bs" tagdir="/WEB-INF/tags"
%><jsp:useBean id="bean" type="jetbrains.buildServer.serverSide.flaky.web.TestsAnalysisBean" scope="request"

/><div id="flaky">
  <c:choose>
    <c:when test="${bean.inProgress}">
      Tests analysis in progress. ${bean.progress.currentStep}
      <c:if test="${bean.progress.totalSize > 0}">
        (${bean.progress.doneSize} of ${bean.progress.totalSize})
      </c:if>
    </c:when>
    <c:when test="${not bean.hasData}">
      <c:choose>
        <c:when test="${bean.testAnalysisEverStarted}">
          <jsp:include page="printResultShort.jsp"/>
        </c:when>
        <c:otherwise>
          No tests were analyzed yet.
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:when test="${bean.hasData}">
      <jsp:include page="printResultShort.jsp"/>

      <c:set var="allDetails" value="${bean.details}" scope="request"/>

      <c:if test="${bean.hasFlaky}">
        <c:set var="title">Flaky tests: ${bean.flakyTestsSize}</c:set>
        <bs:_collapsibleBlock title="${title}" id="flakyTestsBlock">
          <c:set var="groupBean" value="${bean.flakyTests}" scope="request"/>
          <c:set var="id" value="flaky" scope="request"/>
          <jsp:include page="analysedTestsGroup.jsp"/>
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
          <jsp:include page="analysedTestsGroup.jsp"/>
        </bs:_collapsibleBlock>
      </c:if>
    </c:when>
  </c:choose>

  <div style="margin-top: 2em;">
    <a href="analyzeTests.html?projectId=${bean.project.projectId}">Calculate now!</a> (make it a POST).
  </div>
</div>
