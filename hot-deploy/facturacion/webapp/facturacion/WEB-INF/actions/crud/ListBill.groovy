import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;

billList = delegator.findList("Bill", null, null, null, null, false);

billListOrdered = EntityUtil.orderBy(billList, UtilMisc.toList("billId"));

context.billList = billListOrdered;