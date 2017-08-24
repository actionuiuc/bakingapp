/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: IngredientData.java
 * -IngredientData class containing the details for a particular ingredient:
 *      +Ingredient name
 *      +Quantity
 *      +Unit of Measure
 *
 *
 */

package com.example.android.baking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class IngredientData implements Parcelable {
    public double quantity;
    public String ingName;
    public String measure;


    public IngredientData(double vQuantity, String vIngName, String vMeasure)
    {
        this.quantity = vQuantity;
        this.ingName = vIngName;
        this.measure = vMeasure;
    }

    private IngredientData(Parcel in){
        quantity = in.readDouble();
        ingName = in.readString();
        measure = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(quantity);
        parcel.writeString(ingName);
        parcel.writeString(measure);
    }

    public static final Parcelable.Creator<IngredientData> CREATOR = new Parcelable.Creator<IngredientData>() {
        @Override
        public IngredientData createFromParcel(Parcel parcel) {
            return new IngredientData(parcel);
        }

        @Override
        public IngredientData[] newArray(int i) {
            return new IngredientData[i];
        }

    };



}