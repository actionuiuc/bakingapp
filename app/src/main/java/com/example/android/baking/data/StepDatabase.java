/*
 * Brian Jackson
 * bj1412@att.com
 * 8/7/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: StepDatabase.java
 * Schematic class defining database with 'steps' table.
 *
 */

package com.example.android.baking.data;


import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = StepDatabase.VERSION)
public final class StepDatabase {
    private StepDatabase(){}

    public static final int VERSION = 1;


    @Table(StepColumns.class) public static final String STEPS = "steps";



}
