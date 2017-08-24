/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: RecipeData.java
 * -RecipeData class containing the details for a particular recipe:
 *      +ID
 *      +name
 *      +servings
 *      +image filename
 *      +step count
 *      +step list
 *      +ingredient list
 *
 *
 */

package com.example.android.baking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeData implements Parcelable {
    public int id;
    public int servings;
    public int stepCount;
    public String name;
    public String image;
    public StepData[] steps;
    public IngredientData[] ingredients;

    public RecipeData(int vID, int vServings, int vStepCount, String vName, String vImage, StepData[] vSteps, IngredientData[] vIngredients)
    {
        this.id = vID;
        this.servings = vServings;
        this.stepCount = vStepCount;
        this.name = vName;
        this.image = vImage;
        this.steps = vSteps;
        this.ingredients = vIngredients;
    }

    private RecipeData(Parcel in){
        id = in.readInt();
        servings = in.readInt();
        stepCount = in.readInt();
        name = in.readString();
        image = in.readString();
        steps = in.createTypedArray(StepData.CREATOR);
        ingredients = in.createTypedArray(IngredientData.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(servings);
        parcel.writeInt(stepCount);
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeTypedArray(steps, 0);
        parcel.writeTypedArray(ingredients, 0);
    }

    public static final Parcelable.Creator<RecipeData> CREATOR = new Parcelable.Creator<RecipeData>() {
        @Override
        public RecipeData createFromParcel(Parcel parcel) {
            return new RecipeData(parcel);
        }

        @Override
        public RecipeData[] newArray(int i) {
            return new RecipeData[i];
        }

    };



}