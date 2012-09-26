<%@ include file="/include.jsp" %>
<jsp:useBean id="testDetails" type="jetbrains.buildServer.serverSide.flaky.web.TestWebDetails" scope="request"

/><div class="test-details">
  <table>
    <tr>
      <td class="block">
        <div class="block">
          <div class="title">Build Types</div>
          <div class="content">
            <table>
              <c:forEach items="${testDetails.allBuildTypes}" var="bt">
                <tr>
                  <td><bs:buildTypeLink buildType="${bt}"/></td>
                  <bs:changeRequest key="failureRate" value="${testDetails.testData.buildTypeFailureRates[bt.buildTypeId]}">
                    <jsp:include page="failureRate.jsp"/>
                  </bs:changeRequest>
                </tr>
              </c:forEach>
              <tr>
                <td>Total</td>
              </tr>
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
                <tr>
                  <td><bs:agentDetailsLink agent="${agent}"/></td>
                  <bs:changeRequest key="failureRate" value="${testDetails.testData.agentFailureRates[agent.name]}">
                    <jsp:include page="failureRate.jsp"/>
                  </bs:changeRequest>
                </tr>
              </c:forEach>
              <tr>
                <td>Total</td>
              </tr>
            </table>
          </div>
        </div>
      </td>
      <td></td>
    </tr>
  </table>
</div>
