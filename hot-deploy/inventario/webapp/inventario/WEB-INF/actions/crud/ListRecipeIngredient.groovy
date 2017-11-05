import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;

recipeIngredientList = delegator.findList("RecipeIngredient", null, null, null, null, false);

recipeIngredientListOrdered = EntityUtil.orderBy(recipeIngredientList, UtilMisc.toList("recipeId", "ingredientId"));

context.recipeIngredientList = recipeIngredientListOrdered;