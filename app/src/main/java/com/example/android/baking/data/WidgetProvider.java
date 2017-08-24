/*
 * Brian Jackson
 * bj1412@att.com
 * 8/7/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: WidgetProvider.java
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



@ContentProvider(authority = WidgetProvider.AUTHORITY, database = WidgetDatabase.class)
public final class WidgetProvider {
    public static final String AUTHORITY =
            "com.example.android.baking.data.WidgetProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String WIDGETS = "widgets";
        String WIDGET = "widget";

    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }
    @TableEndpoint(table = WidgetDatabase.WIDGETS) public static class Widgets{
        @ContentUri(
                path = Path.WIDGETS,
                type = "vnd.android.cursor.dir/widget",
                defaultSort = WidgetColumns.WIDGET_ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.WIDGETS);

        //used to query for existing widget OR updating existing widget recipe information
        @InexactContentUri(
                name = "WIDGET_ID",
                path = Path.WIDGETS + "/#",
                type = "vnd.android.cursor.item/widget",
                whereColumn = WidgetColumns.WIDGET_ID,
                pathSegment = 1)
        public static Uri withId(long id){
            return buildUri(Path.WIDGETS, String.valueOf(id));
        }


    }

}