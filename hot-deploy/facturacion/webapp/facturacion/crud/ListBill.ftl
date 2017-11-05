<div class="screenlet-body">
  <#if billList?has_content>
    <table cellspacing=0 cellpadding=2 border=0 class="basic-table">
      <thead><tr>
        <th>${uiLabelMap.Id}</th>
        <th>${uiLabelMap.ClientId}</th>
        <th>${uiLabelMap.Total}</th>
        <th>${uiLabelMap.CommonDate}</th>
      </tr></thead>
      <tbody>
        <#list billList as bill>
          <tr>
            <td>${bill.billId}</td>
            <td>${bill.billClientId}</td>
            <td>${bill.billTotal?default(0)}</td>
            <td>${bill.createdStamp}</td>
          </tr>
        </#list>
       </tbody>
    </table>
  </#if>
</div>
