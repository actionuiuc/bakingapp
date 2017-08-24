/*
 * Brian Jackson
 * bj1412@att.com
 * 8/7/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: StepProvider.java
 * Schematic class defining the content provider
 *      -authority
 *      -base content uri
 *      -uri endpoints
 *
 */

package com.example.android.baking.data;


import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;



@ContentProvider(authority = StepProvider.AUTHORITY, database = StepDatabase.class)
public final class StepProvider {
    public static final String AUTHORITY =
            "com.example.android.baking.data.StepProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String STEPS = "steps";
        String STEP = "step";
        String RECIPE = "recipe";
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }
    @TableEndpoint(table = StepDatabase.STEPS) public static class Steps{
        @ContentUri(
                path = Path.STEP,
                type = "vnd.android.cursor.dir/step",
                defaultSort = StepColumns.RECIPE_ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.STEP);

        //used to delete step entry when a user unchecks a step checkbox
        @InexactContentUri(
                name = "STEP_RECIPE",
                path = Path.RECIPE + "/#/" + Path.STEP + "/#",
                type = "vnd.android.cursor.item/step",
                whereColumn = {StepColumns.RECIPE_ID, StepColumns.STEP_ID},
                pathSegment = {1,3})
        public static Uri withIds(long recipeId, long stepId){
            return buildUri(Path.RECIPE, String.valueOf(recipeId), Path.STEP, String.valueOf(stepId));
        }
    }

}