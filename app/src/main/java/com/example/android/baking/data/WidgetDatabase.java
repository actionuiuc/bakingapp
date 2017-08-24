/*
 * Brian Jackson
 * bj1412@att.com
 * 8/7/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: WidgetDatabase.java
 * Schematic class defining database with 'widgets' table.
 *
 */

package com.example.android.baking.data;


import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = WidgetDatabase.VERSION)
public final class WidgetDatabase {
    private WidgetDatabase(){}

    public static final int VERSION = 1;


    @Table(WidgetColumns.class) public static final String WIDGETS = "widgets";



}
