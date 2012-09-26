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
      <td class="env">Environment specifics</td>
      <td>&nbsp;</td>
    </jsp:attribute>
    <jsp:attribute name="testAfterName">
      <c:set var="test" value="${testBean.run.test}"/>
      <c:set var="details" value="${allDetails[test.testNameId]}"/>
      <%--@elvariable id="details" type="jetbrains.buildServer.serverSide.flaky.web.TestWebDetails"--%>

      </td>
      <td class="env">
        <c:if test="${details.failedOnlyInSingleBuildType or details.failedOnlyOnSingleAgent}">
          fails only
          <c:if test="${details.failedOnlyInSingleBuildType}">
            in <bs:buildTypeLink buildType="${details.failedInBuildTypes[0]}"/>
          </c:if>
          <c:if test="${details.failedOnlyOnSingleAgent}">
            on <bs:agentDetailsLink agent="${details.failedOnAgents[0]}"/>
          </c:if>
        </c:if>
      </td>
      <td>
    </jsp:attribute>
  </tt:testGroupWithActions
></bs:trimWhitespace>
