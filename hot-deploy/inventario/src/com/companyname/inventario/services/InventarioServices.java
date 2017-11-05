package com.companyname.inventario.services;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.lang.Math;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilMisc;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
 

public class InventarioServices {
 
    public static final String module = InventarioServices.class.getName();
    
    public static Map<String, Object> test(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Debug.log("\n---------SERVICE START-----------\n");
        Debug.log("\nDATOS:\n"+context);
        Debug.log("\n---------SERVICE END-----------\n");
        return result;
    }

    public static Map<String, Object> createIngredient(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);
        try { 
            GenericValue ingredient = delegator.makeValue("Ingredient");
            // Auto generating next sequence of IngredientId primary key
            if (context.get("ingredientId") == null) ingredient.setNextSeqId();
            else ingredient.set("ingredientId", context.get("ingredientId"));
            // Setting up all non primary key field values from context map
            ingredient.setNonPKFields(context);
            // Creating record in database for Ingredient entity for prepared value
            ingredient = delegator.createOrStore(ingredient);
            result.put("ingredientId", ingredient.getString("ingredientId"));

            Debug.log("Ingredient record created successfully with ingredientId: "+ingredient.getString("ingredientId"));
            updateAvailableProducts(delegator, locale);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("InventarioUiLabels", "CreatingRecordError", locale);
            return ServiceUtil.returnError(error+module);
        }
        return result;
    }

    public static Map<String, Object> removeIngredient(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);
        try {
            GenericValue ingredient = delegator.makeValue("Ingredient");
            ingredient.set("ingredientId", context.get("ingredientId"));
            ingredient.remove();
            result.put("ingredientId", ingredient.getString("ingredientId"));

            Debug.log("Ingredient record removed successfully with ingredientId: "+ingredient.getString("ingredientId"));
            updateAvailableProducts(delegator, locale);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("InventarioUiLabels", "RemovingRecordError", locale);
            return ServiceUtil.returnError(error+module);
        }
        return result;
    }

    public static Map<String, Object> updateIngredient(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);
        try {
            GenericValue ingredient = delegator.makeValue("Ingredient");
            ingredient.set("ingredientId", context.get("ingredientId"));
            ingredient.setNonPKFields(context);
            delegator.store(ingredient);
            result.put("ingredientId", ingredient.getString("ingredientId"));

            Debug.log("Ingredient record stored successfully with ingredientId: "+ingredient.getString("ingredientId"));
            updateAvailableProducts(delegator, locale);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("InventarioUiLabels", "StoringRecordError", locale);
            return ServiceUtil.returnError(error+module);
        }
        return result;
    }

    public static Map<String, Object> createRecipe(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);
        try { 
            GenericValue recipe = delegator.makeValue("Recipe");

            if (context.get("recipeId") == null) recipe.setNextSeqId();
            else recipe.set("recipeId", context.get("recipeId"));
            // Setting up all non primary key field values from context map
            recipe.setNonPKFields(context);
            // Creating record in database for recipe entity for prepared value
            recipe = delegator.createOrStore(recipe);
            result.put("recipeId", recipe.getString("recipeId"));

            Debug.log("Recipe record created successfully with recipeId: "+recipe.getString("recipeId"));
            updateAvailableProducts(delegator, locale);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("InventarioUiLabels", "CreatingRecordError", locale);
            return ServiceUtil.returnError(error+module);
        }
        return result;
    }

     public static Map<String, Object> removeRecipe(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);
        try {
            GenericValue recipe = delegator.makeValue("Recipe");
            recipe.set("recipeId", context.get("recipeId"));
            recipe.remove();
            result.put("recipeId", recipe.getString("recipeId"));

            Debug.log("Recipe record removed successfully with recipeId: "+recipe.getString("recipeId"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("InventarioUiLabels", "RemovingRecordError", locale);
            return ServiceUtil.returnError(error+module);
        }
        return result;
    }

    public static Map<String, Object> addIngredientToRecipe(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        List<GenericValue> recipes = null;
        List<GenericValue> ingredients = null;
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);
        try {
            Debug.log("\nDATOS: "+context+"\n");

            recipes = delegator.findList("Recipe", EntityCondition.makeCondition("recipeId", EntityOperator.EQUALS, context.get("recipeId")), null, null, null, false);
            Debug.log("\nRECETAS: "+recipes+"\n");
            ingredients = delegator.findList("Ingredient", EntityCondition.makeCondition("ingredientId", EntityOperator.EQUALS, context.get("ingredientId")), null, null, null, false);
            Debug.log("\nINGREDIENTES: "+ingredients+"\n");
            
            GenericValue recipeIngredient = delegator.makeValue("RecipeIngredient");
            
            if (context.get("recipeIngredientId") == null) recipeIngredient.setNextSeqId();
            else recipeIngredient.set("recipeIngredientId", context.get("recipeIngredientId"));

            recipeIngredient.setNonPKFields(context);
            delegator.create(recipeIngredient);
            result.put("recipeIngredientId", recipeIngredient.getString("recipeIngredientId"));
            
            Debug.log("RecipeIngredient record created successfully with recipeIngredientId: "+recipeIngredient.getString("recipeIngredientId"));
            updateAvailableProducts(delegator, locale);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("InventarioUiLabels", "CreatingRecordError", locale);
            return ServiceUtil.returnError(error+module);
        }
        return result;
    }

    public static Map<String, Object> removeRecipeIngredient(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);
        try {
            GenericValue recipeIngredient = delegator.makeValue("RecipeIngredient");
            recipeIngredient.set("recipeIngredientId", context.get("recipeIngredientId"));
            recipeIngredient.remove();
            result.put("recipeIngredientId", recipeIngredient.getString("recipeIngredientId"));

            Debug.log("RecipeIngredient record removed successfully with recipeIngredientId: "+recipeIngredient.getString("recipeIngredientId"));
            updateAvailableProducts(delegator, locale);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("InventarioUiLabels", "RemovingRecordError", locale);
            return ServiceUtil.returnError(error+module);
        }
        return result;
    }

    public static Map<String, Object> updateAvailableProducts(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String localeString = "" + context.get("locale");
        Locale locale = new Locale(localeString);

        try {
            updateAvailableProducts(delegator, locale);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String error = UtilProperties.getMessage("InventarioUiLabels", "ProductsListError", locale);
            return ServiceUtil.returnError(error+module);
        }

        return result;
    }

    public static void updateAvailableProducts(Delegator delegator, Locale locale) throws GenericEntityException{

        List<GenericValue> recipeList = delegator.findList("Recipe", null, null, null, null, false);

        for (GenericValue recipe : recipeList) {
            System.out.println("\n---------Recipe: "+recipe);

            String recId = ""+recipe.get("recipeId");
            List<GenericValue> recipeIngredients = delegator.findList("RecipeIngredient", EntityCondition.makeCondition("recipeId", EntityOperator.EQUALS, recId), null, null, null, false);

            long available = 0;
            boolean first = true;

            System.out.println("\n---------------RecipeIngredients: "+recipeIngredients+"-----------\n");

            for (GenericValue recipeIngredient : recipeIngredients) {
                System.out.println("\n-------------------RecipeIngredient: "+recipeIngredient);

                String ingId = ""+recipeIngredient.get("ingredientId");
                GenericValue ingredient = delegator.findOne("Ingredient", UtilMisc.toMap("ingredientId", ingId), false);

                if (ingredient != null) {
                    long recIngWeight = Long.parseLong(""+recipeIngredient.get("recipeIngredientWeight"));
                    long ingWeight = Long.parseLong(""+ingredient.get("ingredientWeight"));

                    long recIngAvailable = (long) Math.floor(ingWeight/recIngWeight);

                    if (!first) available = Math.min(available, recIngAvailable);
                    else {
                        available = recIngAvailable;
                        first = false;
                    }
                    System.out.println("\n-------------------RecipeIngredientAvailable: "+recIngAvailable);
                    System.out.println("\n-------------------Available: "+available);
                }
                else {
                    available = 0;
                    break;
                }
            }

            Long availableLong = new Long((long)available);
            System.out.println("\n--------------AvailableLong: "+availableLong);
            recipe.set("recipeAvailable", availableLong);

            System.out.println("\n--------------RecipeUpd: "+recipe);
            delegator.store(recipe);
        }
    }

} 