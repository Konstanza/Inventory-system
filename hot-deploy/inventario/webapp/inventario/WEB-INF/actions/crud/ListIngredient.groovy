import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;

ingredientList = delegator.findList("Ingredient", null, null, null, null, false);

ingredientListOrdered = EntityUtil.orderBy(ingredientList, UtilMisc.toList("ingredientId"));

context.ingredientList = ingredientListOrdered;