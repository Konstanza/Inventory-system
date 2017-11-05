<div class="screenlet-body">
  <#if recipeIngredientList?has_content>
    <table cellspacing=0 cellpadding=2 border=0 class="basic-table">
      <thead><tr>
        <th>${uiLabelMap.Id}</th>
        <th>${uiLabelMap.RecipeId}</th>
        <th>${uiLabelMap.IngredientId}</th>
        <th>${uiLabelMap.Weight}</th>
        <th>${uiLabelMap.Comment}</th>
      </tr></thead>
      <tbody>
        <#list recipeIngredientList as recipeIngredient>
          <tr>
            <td>${recipeIngredient.recipeIngredientId}</td>
            <td>${recipeIngredient.recipeId}</td>
            <td>${recipeIngredient.ingredientId}</td>
            <td>${recipeIngredient.recipeIngredientWeight?default(0)}</td>
            <td>${recipeIngredient.recipeIngredientComments!}</td>
          </tr>
        </#list>
       </tbody>
    </table>
  </#if>
</div>
