/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: BakingWidgetProvider.java
 * -Implementation of App Widget functionality.
 * -Receives only the event broadcasts that are relevant to the App Widget.
 * -onUpdate is called when the widget gets added to the homescreen.
 * -To update the appwidget, an intent service (UpdateWidgetService) is started from onUpdate.
 *
 */


package com.example.android.baking;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;


public class BakingWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = BakingWidgetProvider.class.getSimpleName();


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        int callNum = 0;
        for (int appWidgetId : appWidgetIds) {
            callNum = callNum + 1;
            Log.i(LOG_TAG, "WDGT Id " + appWidgetId + " call num " + callNum);
            Intent intent = new Intent(context, UpdateWidgetService.class);
            intent.putExtra(EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setAction("Do NOTHING Action");
            context.startService(intent); //start the intent service
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

