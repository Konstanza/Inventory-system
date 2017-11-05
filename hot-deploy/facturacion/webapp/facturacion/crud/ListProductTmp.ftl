<div class="screenlet-body">
  <#if billTmpList?has_content>
    <table cellspacing=0 cellpadding=2 border=0 class="basic-table">
      <thead><tr>
        <th>${uiLabelMap.Id}</th>
        <th>${uiLabelMap.ProductId}</th>
        <th>${uiLabelMap.ProductName}</th>
        <th>${uiLabelMap.ProductPrice}</th>
      </tr></thead>
      <tbody>
        <#list billTmpList as recipeTmp>
          <tr>
            <td>${recipeTmp.billTmpId}</td>
            <td>${recipeTmp.billTmpRecipeId}</td>
            <td>${recipeTmp.billTmpRecipeName?default("NA")}</td>
            <td>${recipeTmp.billTmpRecipePrice?default(0)}</td>
          </tr>
        </#list>
       </tbody>
    </table>
  </#if>
</div>
