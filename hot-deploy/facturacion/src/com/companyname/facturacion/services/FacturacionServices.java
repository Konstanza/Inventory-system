package com.companyname.facturacion.services;

import java.util.Map;
import java.util.List;
import java.util.Locale;

import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.GenericServiceException;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
 
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilMisc;


public class FacturacionServices {
 
    public static final String module = FacturacionServices.class.getName();
    public static double subTotal = 0, tax = 0, total = 0, taxPercentage = 0;

    public static Map<String, Object> test(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Debug.log("\n---------SERVICE INIT-----------\n");
        Debug.log("\nCONTEXT:\n"+context);
        Debug.log("\n---------SERVICE END-----------\n");
        return result;
    }

    public static Map<String, Object> addProductToBillTmp(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);
        try { 
            GenericValue billTmp = delegator.makeValue("BillTmp");
            billTmp.setNextSeqId();
            
            billTmp.set("billTmpRecipeId", context.get("billTmpRecipeId"));

            billTmp = delegator.create(billTmp);
            result.put("billTmpId", billTmp.getString("billTmpId"));
            
            updateBillTmp(delegator);

            result.put("subTotal", subTotal);
            result.put("tax", tax);
            result.put("total", total);

            Debug.log("BillTmp record created successfully with billTmpId: "+billTmp.getString("billTmpId"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("InventarioUiLabels", "CreatingRecordError", locale);
            return ServiceUtil.returnError(error+module);
        }
        return result;
    }

    public static Map<String, Object> removeProductFromBillTmp(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);
        try {
            GenericValue billTmp = delegator.makeValue("BillTmp");
            billTmp.set("billTmpId", context.get("billTmpId"));
            billTmp.remove();
            result.put("billTmpId", billTmp.getString("billTmpId"));

            updateBillTmp(delegator);

            result.put("subTotal", subTotal);
            result.put("tax", tax);
            result.put("total", total);

            Debug.log("BillTmp record removed successfully with billTmpId: "+billTmp.getString("billTmpId"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("InventarioUiLabels", "RemovingRecordError", locale);
            return ServiceUtil.returnError(error+module);
        }
        return result;
    }

    public static void updateBillTmp(Delegator delegator) {

        double newSubTotal = 0;

        try {
            List<GenericValue> billTmpList = delegator.findList("BillTmp", null, null, null, null, false);
        
            for (GenericValue billTmp : billTmpList) {

                String recipeId = ""+billTmp.get("billTmpRecipeId");
                GenericValue recipe = delegator.findOne("Recipe", UtilMisc.toMap("recipeId", recipeId), false);
                String recipeName = ""+recipe.get("recipeName");
                double recipePrice = Double.parseDouble(""+recipe.get("recipePrice"));

                billTmp.set("billTmpRecipeName", recipeName);
                billTmp.set("billTmpRecipePrice", recipePrice);
                delegator.store(billTmp);

                newSubTotal += (double)recipePrice;
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        subTotal = newSubTotal;
        tax = subTotal*taxPercentage/100;
        total = tax + subTotal;
    }

    public static void updateIngredients(Delegator delegator) {

        try {
            List<GenericValue> billTmpList = delegator.findList("BillTmp", null, null, null, null, false);

            for (GenericValue billTmp : billTmpList) {
                String recipeId = ""+billTmp.get("billTmpRecipeId");

                List<GenericValue> recipeIngredientList = delegator.findList("RecipeIngredient", EntityCondition.makeCondition("recipeId", EntityOperator.EQUALS, recipeId), null, null, null, false);
                
                for (GenericValue recipeIngredient : recipeIngredientList) {
                    String ingredientId = recipeIngredient.getString("ingredientId");
                    long recipeIngredientWeight = Long.parseLong(recipeIngredient.getString("recipeIngredientWeight"));
                    
                    GenericValue ingredient = delegator.findOne("Ingredient", UtilMisc.toMap("ingredientId", ingredientId), false);
                    
                    if (ingredient != null) {
                        long ingredientWeight = Long.parseLong(ingredient.getString("ingredientWeight"));

                        ingredient.set("ingredientWeight", (ingredientWeight-recipeIngredientWeight));
                        delegator.store(ingredient);
                    }
                }
            } 

            delegator.removeAll(billTmpList);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        
    }

    public static Map<String, Object> createBill(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);
        GenericValue userLogin = (GenericValue)context.get("userLogin");

        try { 
            GenericValue bill = delegator.makeValue("Bill");
            bill.setNextSeqId();
                
            bill.setNonPKFields(context);
            bill.set("billTotal", total);

            String billId =  bill.getString("billId");

            addProductsToBill(delegator,  bill.getString("billId"), bill.getString("billClientId"));

            try {
                LocalDispatcher dispatcher = dctx.getDispatcher();
                Map ctx = UtilMisc.toMap("userLogin", userLogin, "locale", locale);
                dispatcher.runSync("updateAvailableProducts", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
            }

            bill = delegator.create(bill);
            Debug.log("Bill record created successfully with billId: "+billId);

            String message = UtilProperties.getMessage("FacturacionUiLabels", "CreatingBillSuccess", locale);
            Map<String, Object> result = ServiceUtil.returnSuccess(message+billId);
            result.put("billId", billId);

            return result; 

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("FacturacionUiLabels", "CreatingBillError", locale);
            return ServiceUtil.returnError(error+module);
        } catch (Exception e){
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("FacturacionUiLabels", "EmptyBillError", locale);
            return ServiceUtil.returnError(error);
        }
    }

    public static void addProductsToBill(Delegator delegator, String billId, String clientId) throws Exception{

        try {
            List<GenericValue> billTmpList = delegator.findList("BillTmp", null, null, null, null, false);
            
            if (billTmpList.size() == 0) throw new Exception("The bill is empty");

            for (GenericValue billTmp : billTmpList) {
                String recipeId = ""+billTmp.get("billTmpRecipeId");

                GenericValue billRecipe = delegator.makeValue("BillRecipe");
                billRecipe.setNextSeqId();
                billRecipe.set("billId", billId);
                billRecipe.set("billClientId", clientId);
                billRecipe.set("recipeId", recipeId);
                delegator.create(billRecipe);
            }

            updateIngredients(delegator);

            subTotal = 0;
            tax = 0;
            total = 0;
        } catch (GenericEntityException e){
            Debug.logError(e, module);
        }
    }

    public static Map<String, Object> newBill(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);

        try {
            List<GenericValue> billTmpList = delegator.findList("BillTmp", null, null, null, null, false);
            delegator.removeAll(billTmpList);
            
            subTotal = 0;
            tax = 0;
            total = 0;

            GenericValue taxValue = delegator.findOne("Tax", UtilMisc.toMap("taxId", "tax"), false);

            if (taxValue == null) {
                taxPercentage = 0;
            }
            else {
                taxPercentage = Double.parseDouble(""+taxValue.get("taxPercentage"));
            }
            
            result.put("subTotal", subTotal);
            result.put("tax", tax);
            result.put("total", total);

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("InventarioUiLabels", "RemovingRecordError", locale);
            return ServiceUtil.returnError(error+module);
        }
        
        return result;
    }

    public static Map<String, Object> changeTax(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);

        try {
            GenericValue taxValue = delegator.makeValue("Tax");
            taxValue.set("taxId", "tax");
            taxValue.set("taxPercentage", context.get("taxPercentage"));
            delegator.createOrStore(taxValue);

            taxPercentage = Double.parseDouble(""+taxValue.get("taxPercentage"));
    
            result.put("taxPercentage", taxPercentage);

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("InventarioUiLabels", "StoringRecordError", locale);
            return ServiceUtil.returnError(error+module);
        }
        
        return result;
    }

    public static Map<String, Object> getTax(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);

        try {
            GenericValue taxValue = delegator.findOne("Tax", UtilMisc.toMap("taxId", "tax"), false);

            if (taxValue == null) {
                taxPercentage = 0;
            }
            else {
                taxPercentage = Double.parseDouble(""+taxValue.get("taxPercentage"));
            }
    
            result.put("taxPercentage", taxPercentage);

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("InventarioUiLabels", "GettingRecordError", locale);
            return ServiceUtil.returnError(error+module);
        }
        
        return result;
    }
} 