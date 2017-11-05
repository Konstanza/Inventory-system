<div class="screenlet-body">
  <#if recipeList?has_content>
    <table cellspacing=0 cellpadding=2 border=0 class="basic-table">
      <thead><tr>
        <th>${uiLabelMap.Id}</th>
        <th>${uiLabelMap.Name}</th>
        <th>${uiLabelMap.Price}</th>
        <th>${uiLabelMap.Available}</th>
        <th>${uiLabelMap.Comment}</th>
      </tr></thead>
      <tbody>
        <#list recipeList as recipe>
          <tr>
            <td>${recipe.recipeId}</td>
            <td>${recipe.recipeName?default("NA")}</td>
            <td>${recipe.recipePrice?default(0)}</td>
            <td>${recipe.recipeAvailable?default(0)}</td>
            <td>${recipe.recipeComments!}</td>
          </tr>
        </#list>
       </tbody>
    </table>
  </#if>
</div>
