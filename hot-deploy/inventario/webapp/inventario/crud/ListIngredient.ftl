<div class="screenlet-body">
  <#if ingredientList?has_content>
    <table cellspacing=0 cellpadding=2 border=0 class="basic-table">
      <thead><tr>
        <th>${uiLabelMap.Id}</th>
        <th>${uiLabelMap.Name}</th>
        <th>${uiLabelMap.Weight}</th>
        <th>${uiLabelMap.Comment}</th>
      </tr></thead>
      <tbody>
        <#list ingredientList as ingredient>
          <tr>
            <td>${ingredient.ingredientId}</td>
            <td>${ingredient.ingredientName?default("NA")}</td>
            <td>${ingredient.ingredientWeight?default(0)}</td>
            <td>${ingredient.ingredientComments!}</td>
          </tr>
        </#list>
       </tbody>
    </table>
  </#if>
</div>
