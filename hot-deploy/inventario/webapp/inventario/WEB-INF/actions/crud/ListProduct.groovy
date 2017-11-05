import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;

recipeList = delegator.findList("Recipe", null, null, null, null, false);

recipeListOrdered = EntityUtil.orderBy(recipeList, UtilMisc.toList("recipeId"));

context.recipeList = recipeListOrdered;