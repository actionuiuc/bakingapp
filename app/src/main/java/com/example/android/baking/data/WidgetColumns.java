/*
 * Brian Jackson
 * bj1412@att.com
 * 8/7/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: WidgetColumns.java
 * Schematic class defining column names and data types in the 'widgets' table.
 * Active widget information to be saved in database:
 *      entry id
 *      widget id
 *      recipe id
 */

package com.example.android.baking.data;


import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface WidgetColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID =
            "_id";
    @DataType(DataType.Type.INTEGER) @NotNull public static final String WIDGET_ID =
            "widget_id";
    @DataType(DataType.Type.INTEGER) @NotNull
    public static final String RECIPE_ID = "recipe_id";
}
