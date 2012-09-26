<%@ include file="/include.jsp" %><%@
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
    taglib prefix="bs" tagdir="/WEB-INF/tags" %><%@
    taglib prefix="tt" tagdir="/WEB-INF/tags/tests"

%><jsp:useBean id="groupBean" type="jetbrains.buildServer.web.problems.GroupedTestsBean" scope="request"
/><jsp:useBean id="id" type="java.lang.String" scope="request"
/><jsp:useBean id="allDetails" type="java.util.Map" scope="request"

/><bs:trimWhitespace
  ><tt:testGroupWithActions groupedTestsBean="${groupBean}"
                            defaultOption="package"
                            groupSelector="true"
                            id="${id}">
    <jsp:attribute name="afterToolbar">
      <td class="flaky-details">Details</td>
      <td>&nbsp;</td>
    </jsp:attribute>
    <jsp:attribute name="testAfterName">
      <c:set var="test" value="${testBean.run.test}"/>
      <c:set var="details" value="${allDetails[test.testNameId]}"/>
      <%--@elvariable id="details" type="jetbrains.buildServer.serverSide.flaky.web.TestWebDetails"--%>

      </td>
      <td class="flaky-details">
        <c:choose>
          <c:when test="${details.testData.alwaysFailing}"></c:when>
          <c:when test="${details.runInSingleBuildType}">
            run only in <bs:buildTypeLink buildType="${details.failedInBuildTypes[0]}"/>
          </c:when>
          <c:otherwise>
            failed in
            <bs:changeRequest key="buildTypes" value="${details.failedInBuildTypes}">
              <jsp:include page="buildTypes.jsp"/>
            </bs:changeRequest>

            <c:if test="${fn:length(details.failedInBuildTypes) != fn:length(details.allBuildTypes)}">
              <bs:changeRequest key="buildTypes" value="${details.allBuildTypes}">
                (run in <jsp:include page="buildTypes.jsp"/>)
              </bs:changeRequest>
            </c:if>
          </c:otherwise>
        </c:choose>
      </td>
      <td>
    </jsp:attribute>
  </tt:testGroupWithActions
></bs:trimWhitespace>
