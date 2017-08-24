/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: WidgetEntryData.java
 * -WidgetEntryData class containing the details for a particular entry in the widget db table:
 *      +Widget ID
 *      +Recipe ID
 *
 */

package com.example.android.baking.model;

import android.os.Parcel;
import android.os.Parcelable;


public class WidgetEntryData implements Parcelable {

    public int widgetId;
    public int recipeId;

    public WidgetEntryData(int vWidgetId, int vRecipeId)
    {
        this.widgetId = vWidgetId;
        this.recipeId = vRecipeId;
    }

    private WidgetEntryData(Parcel in){
        widgetId = in.readInt();
        recipeId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(widgetId);
        parcel.writeInt(recipeId);
    }

    public static final Parcelable.Creator<WidgetEntryData> CREATOR = new Parcelable.Creator<WidgetEntryData>() {
        @Override
        public WidgetEntryData createFromParcel(Parcel parcel) {
            return new WidgetEntryData(parcel);
        }

        @Override
        public WidgetEntryData[] newArray(int i) {
            return new WidgetEntryData[i];
        }

    };




}
