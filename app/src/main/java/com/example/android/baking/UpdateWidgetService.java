/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: UpdateWidgetService.java
 * -Intent Service used to update the appwidget
 * -Uses a db to record the app widget id and recipe id for all widgets in use.
 * -Performs action and shuts itself down.
 *
 */

package com.example.android.baking;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.baking.data.WidgetColumns;
import com.example.android.baking.data.WidgetProvider;
import com.example.android.baking.model.RecipeData;
import com.example.android.baking.model.WidgetEntryData;
import com.example.android.baking.ui.RecipeDetailActivity;
import com.example.android.baking.utilities.NetworkUtils;
import com.example.android.baking.utilities.RecipeJsonUtils;

import java.net.URL;
import java.text.DecimalFormat;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

public class UpdateWidgetService extends IntentService {

    private WidgetEntryData widgetEntry;

    private RecipeData[] simpleJsonRecipeData;

    RecipeData widgetRecipe;

    private static final String LOG_TAG = UpdateWidgetService.class.getSimpleName();

    public UpdateWidgetService() {
        super("UpdateWidgetService");
        setIntentRedelivery(true);
    }

    //called when IntentService starts
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int incomingAppWidgetId = intent.getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);

        if(incomingAppWidgetId != INVALID_APPWIDGET_ID) {
            updateWidget(this, appWidgetManager, incomingAppWidgetId, intent.getAction());
        }
    }

    public void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String whichButton) {

        //find passed widget_id in widget table if it exists and create WidgetEntry object
        Cursor c = context.getContentResolver().query(WidgetProvider.Widgets.withId(appWidgetId), null, null, null, null);

        //Log.i(LOG_TAG, "WidgetEntry cursor count: " + c.getCount());

        if (c.getCount() == 1) {
            c.moveToNext();
            int widgetId_DB;
            int widgetIdCol = c.getColumnIndex(WidgetColumns.WIDGET_ID);
            widgetId_DB = c.getInt(widgetIdCol);

            int recipeId;
            int recipeIdCol = c.getColumnIndex(WidgetColumns.RECIPE_ID);
            recipeId = c.getInt(recipeIdCol);

            widgetEntry = new WidgetEntryData(widgetId_DB, recipeId);

            //retrieve recipe data for recipe ingredients to be displayed in widget
            FetchRecipeTask task = new FetchRecipeTask(context, appWidgetId, appWidgetManager);
            task.execute();
        }
    }


    // AsyncTask was moved to the Intent Service because onUpdate in the BakingWidgetProvider can finish before the AsyncTask
    // completes if it is located in BakingWidgetProvider.  The Intent Service will call onUpdate once it is complete.
    public class FetchRecipeTask extends AsyncTask<String, Void, RecipeData[]> {

        private Context mContext;
        private int appWidgetId;
        private AppWidgetManager pAppWidgetManager;

        public FetchRecipeTask(Context context, int passAddWidgetId, AppWidgetManager passAppWidgetManager) {
            mContext = context;
            appWidgetId = passAddWidgetId;
            pAppWidgetManager = passAppWidgetManager;
        }

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
                        .getRecipeDataArrayFromJson(mContext, jsonRecipeResponse); //parse RecipeData objects from JSON response
                return simpleJsonRecipeData; //return RecipeData array with recipe details

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(RecipeData[] recipeArray) {
            super.onPostExecute(recipeArray);

            for (int j = 0; j < recipeArray.length; j++) {

                //if the recipeID of the WidgetEntry object exists in the recipe array get RecipeData object for the
                // corresponding recipe ID
                if (recipeArray[j].id == widgetEntry.recipeId) {
                    widgetRecipe = recipeArray[j];
                }
            }

            // creating the intent with the ingredients to be displayed
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra("recipe.details", widgetRecipe);

            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.baking_widget_provider);
            views.removeAllViews(R.id.ingredient_list);  //clear list of ingredients to reload during onUpdate
            //fill in layout for recipe ingredient widget
            views.setTextViewText(R.id.widget_ingredient_header, widgetRecipe.name);

            if (widgetRecipe.ingredients != null && widgetRecipe.ingredients.length > 0) { //if recipe has ingredients returned from server

                views.setViewVisibility(R.id.widget_ingredient_header, 0); //0 visible
                views.setViewVisibility(R.id.wdgt_error_message_ingredient, 8); //8 gone

                //Create intent to pass when the app widget is clicked by the user.
                //RecipeDetailActivity will be called on click.
                Intent intentDetail = new Intent(mContext, RecipeDetailActivity.class);
                intentDetail.putExtra("recipe.details", widgetRecipe);
                PendingIntent pendingIntentDetail = PendingIntent.getActivity(mContext, widgetRecipe.id, intentDetail, 0);  //set requestCode unique
                views.setOnClickPendingIntent(R.id.widget_ingredient_header, pendingIntentDetail);

                //populate views with ingredient data to display in the app widget
                for (int j = 0; j < widgetRecipe.ingredients.length; j++) {

                    RemoteViews llViews = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_list_item);

                    DecimalFormat df = new DecimalFormat("###.##");
                    llViews.setTextViewText(R.id.ingredient_quantity, String.valueOf(df.format(widgetRecipe.ingredients[j].quantity)));

                    llViews.setTextViewText(R.id.ingredient_measure, widgetRecipe.ingredients[j].measure);

                    llViews.setTextViewText(R.id.ingredient_name, widgetRecipe.ingredients[j].ingName);

                    views.addView(R.id.ingredient_list, llViews);

                    views.setOnClickPendingIntent(R.id.ingredient_list, pendingIntentDetail);

                    // Instruct the widget manager to update the widget
                    pAppWidgetManager.updateAppWidget(appWidgetId, views);
                }
            }
            else { //display error message as ingredients do not exist for the recipe
                views.setViewVisibility(R.id.widget_ingredient_header, 8); //8 gone
                views.setViewVisibility(R.id.wdgt_error_message_ingredient, 0); //0 visible
            }
        }
    }
}