<%@ include file="/include.jsp" %>
<jsp:useBean id="testDetails" type="jetbrains.buildServer.serverSide.flaky.web.TestWebDetails" scope="request"

/><div class="test-details">
  <div>
    <c:choose>
      <c:when test="${not testDetails.hasReason}">
      </c:when>
      <c:when test="${testDetails.withoutChangesReason}">
        Test failed in <bs:buildLinkFull build="${testDetails.buildWithoutChanges}"/>
      </c:when>
      <c:when test="${testDetails.buildsOnSameModificationReason}">
        <div>Different results in two builds:</div>
        <div><bs:buildLinkFull build="${testDetails.firstBuild}"/></div>
        <div><bs:buildLinkFull build="${testDetails.secondBuild}"/></div>
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
                  <td><bs:agentDetailsLink agent="${agent}"/></td>
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
