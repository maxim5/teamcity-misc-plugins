<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
    taglib prefix="bs" tagdir="/WEB-INF/tags" %><%@
    taglib prefix="tt" tagdir="/WEB-INF/tags/tests"
%><jsp:useBean id="bean" type="jetbrains.buildServer.serverSide.flaky.web.FlakyTestsBean" scope="request"


/><div id="flaky">
  <c:if test="${not bean.hasData}">
    No flaky tests
  </c:if>
  <c:if test="${bean.hasData}">
    <c:if test="${bean.hasFlaky}">
      <c:set var="title">Flaky tests</c:set>
      <bs:_collapsibleBlock title="${title}" id="flakyTestsBlock">
        <tt:testGroupWithActions groupedTestsBean="${bean.flakyTests}" defaultOption="package"
                                 groupSelector="true"
                                 id="flakyTests">
        </tt:testGroupWithActions>
      </bs:_collapsibleBlock>
    </c:if>

    <c:if test="${bean.hasAlwaysFailing}">
      <c:set var="title">Always failing tests</c:set>
      <bs:_collapsibleBlock title="${title}" id="alwaysFailingTestsBlock">
        <tt:testGroupWithActions groupedTestsBean="${bean.alwaysFailingTests}" defaultOption="package"
                                 groupSelector="true"
                                 id="alwaysFailingTests">
        </tt:testGroupWithActions>
      </bs:_collapsibleBlock>
    </c:if>
  </c:if>

  <div style="margin-top: 2em;">
    <a href="findFlaky.html?projectId=${bean.project.projectId}">Calculate now!</a> (make it a POST).
  </div>
  TODO: javascript (collapse, mute/investigate); progress.
</div>
