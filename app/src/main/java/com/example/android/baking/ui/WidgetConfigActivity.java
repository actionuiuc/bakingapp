/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: WidgetConfigActivity.java
 * -Activity dealing with the configuration of the widget (when it is first added).
 * -Displays list of recipes that can be displayed in the app widget.
 * -Writes to widget DB inserting added widget id and chosen recipe id.
 *
 */


package com.example.android.baking.ui;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.baking.BakingWidgetProvider;
import com.example.android.baking.R;
import com.example.android.baking.data.WidgetColumns;
import com.example.android.baking.data.WidgetProvider;
import com.example.android.baking.model.RecipeData;
import com.example.android.baking.model.WidgetEntryData;
import com.example.android.baking.utilities.NetworkUtils;
import com.example.android.baking.utilities.RecipeJsonUtils;

import java.net.URL;


public class WidgetConfigActivity extends AppCompatActivity {

    //Attributes
    private LinearLayout mRecipeLayout;
    private TextView mErrorMessageDisplay;

    private int widgetId;
    private WidgetEntryData widgetEntry;

    private RecipeData[] simpleJsonRecipeData;

    private static final String LOG_TAG = WidgetConfigActivity.class.getSimpleName();

    boolean foundWidget;

     //LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        setResult(RESULT_CANCELED);
        widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

        mRecipeLayout = (LinearLayout) findViewById(R.id.recipe_llist);
        mErrorMessageDisplay = (TextView) findViewById(R.id.widget_error_message_display);

        loadRecipeData(); //Display available recipes for the user to choose from using AsyncTask
                          //Each recipe is displayed as a button with an onclick listener set to add information to the widget DB

        foundWidget = false;

        Intent intent = getIntent();

        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

                //Determine if widget_id that will be added already exists in widget table in DB.
                Context context = mRecipeLayout.getContext();
                Cursor c = context.getContentResolver().query(WidgetProvider.Widgets.CONTENT_URI, null, null, null, null);

                for (int i = 0; i < c.getCount(); i++) {
                    c.moveToNext();

                    int widgetId_DB;
                    int widgetIdCol = c.getColumnIndex(WidgetColumns.WIDGET_ID);
                    widgetId_DB = c.getInt(widgetIdCol);

                    int recipeId;
                    int recipeIdCol = c.getColumnIndex(WidgetColumns.RECIPE_ID);
                    recipeId = c.getInt(recipeIdCol);

                    if(widgetId == widgetId_DB) {
                        foundWidget = true;
                        widgetEntry = new WidgetEntryData(widgetId_DB, recipeId);
                    }
                }
            }
        }

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

    }

    private void loadRecipeData() {
        new FetchRecipeTask().execute();
    }

    // Private Methods

    private void recClicked(View v) {
            insertUpdateWidgetEntry(v);
            broadcastWidgetUpdate();
            setResultAndFinish();
    }

    private void setResultAndFinish() {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(Activity.RESULT_OK, resultValue);
        finish();
    }

    private void broadcastWidgetUpdate() {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, BakingWidgetProvider.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{widgetId});
        sendBroadcast(intent);
    }

    // Method that will either insert the widget id and recipe id to the DB if an entry does not exist for the current widget id
    // OR update an existing entry matching the current widget id
    private void insertUpdateWidgetEntry(View v) {

        int clickedRecipeId = v.getId();

        if (foundWidget == true) {
            //do update of widget DB
            getContentResolver().update(WidgetProvider.Widgets.withId(widgetId), null, null, null);
        } else {
            //do add for widget DB
            ContentValues contentValues = new ContentValues();

            contentValues.put(WidgetColumns.RECIPE_ID, clickedRecipeId);
            contentValues.put(WidgetColumns.WIDGET_ID, widgetId);

            getContentResolver().insert(WidgetProvider.Widgets.CONTENT_URI, contentValues);
        }
    }

    //create list of recipes for the user to chose from
    public class FetchRecipeTask extends AsyncTask<String, Void, RecipeData[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected RecipeData[] doInBackground(String... params) {
            URL recipeRequestUrl = NetworkUtils.buildUrl(); //build Recipe request url

            try {
                String jsonRecipeResponse = NetworkUtils
                        .getResponseFromHttpUrl(recipeRequestUrl); //get JSON response with recipe list data

                simpleJsonRecipeData = RecipeJsonUtils
                        .getRecipeDataArrayFromJson(WidgetConfigActivity.this, jsonRecipeResponse); //parse RecipeData objects from JSON response

                return simpleJsonRecipeData; //return RecipeData array with recipe details

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(RecipeData[] recipeArray) {

            if (recipeArray != null) {
                for (int i = 0; i < recipeArray.length; i++) {
                    //Loop through each RecipeData object and inflate each recipe item inside a LinearLayout
                    View mRecipeItem = LayoutInflater.from(WidgetConfigActivity.this).inflate(
                            R.layout.recipe_list_item, null);

                    //create button for each recipe
                    Button mRecipeName = (Button) mRecipeItem.findViewById(R.id.recipe_button);
                    mRecipeName.setText(recipeArray[i].name);
                    mRecipeName.setId(recipeArray[i].id);

                    //set onclick listener to call recClicked when a recipe is chosen by the user
                    mRecipeName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recClicked(v);
                        }
                    });


                    mRecipeLayout.addView(mRecipeItem);
                }

            } else { //if RecipeData array is empty, display error message
                mErrorMessageDisplay.setVisibility(View.VISIBLE);
            }
        }
    }

}