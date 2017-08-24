/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: StepData.java
 * -StepData class containing a step for the recipe:
 *      +Step ID
 *      +Recipe ID
 *      +Short description
 *      +Description
 *      +Video URL
 *      +Thumbnail URL
 *
 *
 */

package com.example.android.baking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StepData implements Parcelable {
    public int stepId;
    public int recipeId;
    public String shortDescr;
    public String descr;
    public String videoUrl;
    public String thumbUrl;


    public StepData(int vStepId, int vRecipeId, String vShortDescr, String vDescr, String vVideoUrl, String vThumbUrl)
    {
        this.stepId = vStepId;
        this.recipeId = vRecipeId;
        this.shortDescr = vShortDescr;
        this.descr = vDescr;
        this.videoUrl = vVideoUrl;
        this.thumbUrl = vThumbUrl;
    }

    private StepData(Parcel in){
        stepId = in.readInt();
        recipeId = in.readInt();
        shortDescr = in.readString();
        descr = in.readString();
        videoUrl = in.readString();
        thumbUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(stepId);
        parcel.writeInt(recipeId);
        parcel.writeString(shortDescr);
        parcel.writeString(descr);
        parcel.writeString(videoUrl);
        parcel.writeString(thumbUrl);
    }

    public static final Parcelable.Creator<StepData> CREATOR = new Parcelable.Creator<StepData>() {
        @Override
        public StepData createFromParcel(Parcel parcel) {
            return new StepData(parcel);
        }

        @Override
        public StepData[] newArray(int i) {
            return new StepData[i];
        }

    };



}