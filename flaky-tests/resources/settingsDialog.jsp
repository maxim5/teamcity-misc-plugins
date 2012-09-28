<%@ include file="/include.jsp"
%><jsp:useBean id="bean" type="jetbrains.buildServer.serverSide.flaky.web.TestsAnalysisBean" scope="request"/>

<bs:modalDialog formId="settingsForm"
                title="Test Analysis Settings"
                action=""
                closeCommand="BS.Flaky.Dialog.close()"
                saveCommand="BS.Flaky.Dialog.close()">
  <c:set var="settings" value="${bean.testAnalysisResult.settings}"/>

  <table class="runnerFormTable">
    <tr class="groupingTitle">
      <td>
        <label for="buildTypes">Exclude build configurations</label>
      </td>
    </tr>
    <tr>
      <td>
        <div class="grayNote">Selected build configurations will not be used in analysis:</div>

        <select id="excludeBuildTypes" multiple="multiple">
          <c:forEach items="${bean.buildTypeSettings}" var="entry">
            <forms:option value="${entry.key.buildTypeId}" selected="${entry.value}">
              <c:out value="${entry.key.name}"/>
            </forms:option>
          </c:forEach>
        </select>
      </td>
    </tr>

    <tr class="groupingTitle">
      <td>Time period to process</td>
    </tr>
    <tr>
      <td>
        <div class="grayNote">Only builds started in a specified time slot will be processed</div>

        <table>
          <tr>
            <td class="head">
              <label for="analyseTimePeriodDays">Time period in days:</label>
            </td>
            <td>
              <forms:textField name="analyseTimePeriodDays" value="${settings.analyseTimePeriodDays}"
                               disabled="${settings.analyseFullHistory}"/>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <forms:checkbox name="analyseFullHistory" checked="${settings.analyseFullHistory}"/>
              <label for="analyseFullHistory">Analyse full history</label>
            </td>
          </tr>
        </table>
      </td>
    </tr>

    <tr class="groupingTitle">
      <td>Euristics</td>
    </tr>
    <tr>
      <td>
        <forms:checkbox name="speedUpAlwaysFailing" checked="${settings.speedUpAlwaysFailing}"/>
        <label for="speedUpAlwaysFailing">Speed up processing tests with 100% failure rate</label>

        <div class="grayNote">May produce inaccurate results</div>
      </td>
    </tr>
  </table>

  <div class="popupSaveButtonsBlock">
    <forms:submit label="Close" onclick="return BS.Flaky.Dialog.close();"/>
  </div>
</bs:modalDialog>
