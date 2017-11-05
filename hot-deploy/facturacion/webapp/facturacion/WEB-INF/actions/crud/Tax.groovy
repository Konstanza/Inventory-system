import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;

GenericValue taxValue = delegator.findOne("Tax", UtilMisc.toMap("taxId", "tax"), false);

if (taxValue == null) {
	context.taxPercentage = 0;
}
else {
	context.taxPercentage = Double.parseDouble(""+taxValue.get("taxPercentage"));
}
