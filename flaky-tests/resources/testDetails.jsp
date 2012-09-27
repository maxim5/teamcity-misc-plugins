<%@ include file="/include.jsp" %>
<jsp:useBean id="testDetails" type="jetbrains.buildServer.serverSide.flaky.web.TestWebDetails" scope="request"

/><div class="test-details">
  <div>
    [${testDetails.buildId}]
  </div>

  <div>
    <c:choose>
      <c:when test="${not testDetails.hasReason}">
      </c:when>
      <c:when test="${testDetails.withoutChangesReason}">
        Test failed in <bs:buildLinkFull build="${testDetails.buildWithoutChanges}"/>
      </c:when>
      <c:when test="${testDetails.buildsOnSameModificationReason}">
        <div>Different test results in builds with same sources:</div>

        <table class="modificationBuilds">
          <c:set var="build" value="${testDetails.failedInBuild}"/>
          <tr class="buildTypeProblem">
            <td class="fail">Failed in:</td>
            <td class="bt"><bs:buildTypeLink buildType="${build.buildType}"/></td>
            <td class="build"><%@ include file="/changeBuild.jspf" %></td>
          </tr>

          <tr class="buildTypeProblem">
            <c:set var="build" value="${testDetails.successfulInBuild}"/>
            <td class="success">Successful in:</td>
            <td class="bt"><bs:buildTypeLink buildType="${build.buildType}"/></td>
            <td class="build"><%@ include file="/changeBuild.jspf" %></td>
          </tr>
        </table>
      </c:when>
    </c:choose>
  </div>

  <table>
    <tr>
      <td class="block">
        <div class="block">
          <div class="title">Build Types</div>
          <div class="content">
            <table>
              <c:forEach items="${testDetails.allBuildTypes}" var="bt">
                <c:set var="failureRate" value="${testDetails.testData.buildTypeFailureRates[bt.buildTypeId]}"/>
                <tr ${failureRate.failures == 0 ? "class='zero'" : ""}>
                  <td><bs:buildTypeLink buildType="${bt}"/></td>
                  <bs:changeRequest key="failureRate" value="${failureRate}">
                    <jsp:include page="failureRate.jsp"/>
                  </bs:changeRequest>
                </tr>
              </c:forEach>
            </table>
          </div>
        </div>
      </td>
      <td class="block">
        <div class="block">
          <div class="title">Agents</div>
          <div class="content">
            <table>
              <c:forEach items="${testDetails.allAgents}" var="agent">
                <c:set var="failureRate" value="${testDetails.testData.agentFailureRates[agent.name]}"/>
                <tr ${failureRate.failures == 0 ? "class='zero'" : ""}>
                  <%--<td><bs:agentDetailsLink agent="${agent}"/></td>--%>
                  <td><bs:agentDetailsFullLink agent="${agent}"
                                               doNotShowOutdated="true"
                                               doNotShowOSIcon="false"
                                               doNotShowPoolInfo="true"
                                               showRunningStatus="false"
                                               doNotShowUnavailableStatus="true"/></td>
                  <bs:changeRequest key="failureRate" value="${failureRate}">
                    <jsp:include page="failureRate.jsp"/>
                  </bs:changeRequest>
                </tr>
              </c:forEach>
            </table>
          </div>
        </div>
      </td>
      <td></td>
    </tr>
  </table>
</div>
