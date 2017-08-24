/*
 * Brian Jackson
 * bj1412@att.com
 * 8/7/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: StepColumns.java
 * Schematic class defining column names and data types in the 'steps' table.
 * Step completion information to be saved in database:
 *      entry id
 *      recipe id
 *      step id
 */

package com.example.android.baking.data;


import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface StepColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID =
            "_id";
    @DataType(DataType.Type.INTEGER) @NotNull
    public static final String RECIPE_ID = "recipe_id";
    @DataType(DataType.Type.INTEGER) @NotNull public static final String STEP_ID =
            "step_id";

}
