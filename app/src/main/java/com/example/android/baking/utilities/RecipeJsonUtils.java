/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: RecipeJsonUtils.java
 * Utility functions to handle Recipe JSON data.
 *
 */

package com.example.android.baking.utilities;

import android.content.Context;

import com.example.android.baking.model.IngredientData;
import com.example.android.baking.model.RecipeData;
import com.example.android.baking.model.StepData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class RecipeJsonUtils {

    private static final String TAG = RecipeJsonUtils.class.getSimpleName();

    /**
     * This method parses JSON from a web response and returns an array of RecipeData objects
     * describing recipes found.
     *
     * @param recipeJsonStr JSON response from server
     *
     * @return Array of RecipeData objects describing movies
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static RecipeData[] getRecipeDataArrayFromJson(Context context, String recipeJsonStr)
            throws JSONException {

        final String RP_RESULTS = "";

        final String RP_ID = "id";
        final String RP_NAME = "name";
        final String RP_SERVINGS = "servings";
        final String RP_IMAGE = "image";
        final String RP_STEPS = "steps";
        final String RP_INGR = "ingredients";

        final String RP_STEPS_ID = "id";
        final String RP_STEPS_SHORT_DESC = "shortDescription";
        final String RP_STEPS_DESC = "description";
        final String RP_STEPS_VIDEO_URL = "videoURL";
        final String RP_STEPS_THUMB_URL = "thumbnailURL";

        final String RP_INGR_QUANTITY = "quantity";
        final String RP_INGR_MEASURE = "measure";
        final String RP_INGR_NAME = "ingredient";

        /* RecipeData array to hold each recipe's details */
        RecipeData[] parsedRecipeData = null;

        JSONArray recipeArray = new JSONArray(recipeJsonStr); //json string starts with [

        parsedRecipeData = new RecipeData[recipeArray.length()];

        for (int i = 0; i < recipeArray.length(); i++) {

            /* These are the values that will be collected */
            int id;
            int servings;
            int stepCount;
            String name;
            String image;
            IngredientData[] parsedIngredientData = null;
            StepData[] parsedStepData = null;

            /* Get the JSON object representing a particular recipe */
            JSONObject recipeInfo = recipeArray.getJSONObject(i);

            id = recipeInfo.getInt(RP_ID);
            servings = recipeInfo.getInt(RP_SERVINGS);
            name = recipeInfo.getString(RP_NAME);
            image = recipeInfo.getString(RP_IMAGE);

            JSONArray ingredArray = recipeInfo.getJSONArray(RP_INGR);
            JSONArray stepArray = recipeInfo.getJSONArray(RP_STEPS);

            parsedIngredientData = new IngredientData[ingredArray.length()];
            parsedStepData = new StepData[stepArray.length()];

            for (int j = 0; j < ingredArray.length(); j++) {
                /* These are the values that will be collected */
                double quantity;
                String measure;
                String ingrName;

                /* Get the JSON object representing the particular ingredients for a recipe */
                JSONObject ingrInfo = ingredArray.getJSONObject(j);

                quantity = ingrInfo.getDouble(RP_INGR_QUANTITY);
                measure = ingrInfo.getString(RP_INGR_MEASURE);
                ingrName = ingrInfo.getString(RP_INGR_NAME);

                parsedIngredientData[j] = new IngredientData(quantity, ingrName, measure);

            }

            stepCount = stepArray.length();

            for (int k = 0; k < stepArray.length(); k++) {
                /* These are the values that will be collected */
                int stepId;
                int recipeId;
                String shortDesc;
                String description;
                String videoUrl;
                String thumbUrl;

                /* Get the JSON object representing the particular steps for a recipe */
                JSONObject stepsInfo = stepArray.getJSONObject(k);

                stepId = stepsInfo.getInt(RP_STEPS_ID);
                recipeId = id;
                shortDesc = stepsInfo.getString(RP_STEPS_SHORT_DESC);
                description = stepsInfo.getString(RP_STEPS_DESC);
                videoUrl = stepsInfo.getString(RP_STEPS_VIDEO_URL);
                thumbUrl = stepsInfo.getString(RP_STEPS_THUMB_URL);

                parsedStepData[k] = new StepData(stepId, recipeId, shortDesc, description, videoUrl, thumbUrl);

            }

            parsedRecipeData[i] = new RecipeData(id, servings, stepCount, name, image, parsedStepData, parsedIngredientData);

        }

        return parsedRecipeData;
    }
}