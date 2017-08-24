/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: MainActivity.java
 *  I implemented the following required features:
 *      -Recipes are displayed in the main layout via a recylerview grid of their corresponding thumbnails and title.
 *      -Clicking on a recipe will navigate to the recipe details including ingredients and steps.
 *      -Exoplayer is used to display video in the step detail page.
 *      -Espresso can be used to run two available tests:
 *          +Clicks on the 'Recipe Introduction' step in the RecipeDetailsFragment and verifies that the correct intent was
 *           created to pass to the StepDetailActivity. (Phone Only)
 *          +Clicks on the 'Recipe Introduction' step in RecipeDetailsFragment and verifies that the step details for the 'Recipe
 *           Introduction' are displayed in the right pane StepDetailsFragment. (Tablet Only)
 *          +Demos a user clicking on a recipe on the first screen.
 *      -Schematic is used for the DB and ContentProviders to record completed steps and widget information.
 *      -Companion widget displays selected recipe ingredients.
 *      -In portrait mode (both phone and tablet) a single fragment is displayed on each screen.  For a tablet with a screen
  *      width greater than 600dp, dual pane mode is used to display both recipe details and step details.  Functionality
  *      exists to transition between the two modes on the fly.
 */

package com.example.android.baking.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.baking.R;
import com.example.android.baking.adapters.RecipeAdapter;
import com.example.android.baking.adapters.RecipeAdapter.RecipeAdapterOnClickHandler;
import com.example.android.baking.model.RecipeData;
import com.example.android.baking.utilities.NetworkUtils;
import com.example.android.baking.utilities.RecipeJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements RecipeAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private RecipeAdapter mRecipeAdapter;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //associated with activity_main layout


        /* This TextView is used to display an error and will be hidden if there are no errors to display.  */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        /*
         * Create a reference to our RecyclerView from the layout xml activity_main. This allows us to
         * set the adapter of the RecyclerView, set a layout manager, and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_recipes);


        /* GridLayoutManager is used with 1 column if screen width is less than 600 (greater than uses 2 cols) */
        int gridCols = getResources().getInteger(R.integer.grid_cols);
        GridLayoutManager layoutManager = new GridLayoutManager(this, gridCols);
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Recipes should rarely change once the adapter loads them.
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The RecipeAdapter is responsible for linking our recipe data with the views that
         * will be displaying our recipe data.
         */
        mRecipeAdapter = new RecipeAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mRecipeAdapter);

        /*
         * Attach the ProgressBar variable to the appropriate layout.
         * This will indicate to the user that we are loading data.
         * It will be hidden when no data is loading.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Once all of our views are setup, we can load the recipe data. */
        loadRecipeData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    /**
     * This method will call FetchRecipeTask to retrieve
     * the array of recipe objects using the async background thread.
     */
    private void loadRecipeData() {
        new FetchRecipeTask().execute();
    }


    /**
     * This method is overridden by the MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param clickedRecipe The RecipeData object for the recipe that was clicked by the user.
     */
    @Override
    public void onClick(RecipeData clickedRecipe) {
        Context context = this;
        Class destinationClass = RecipeDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        // Pass the recipe details in the form of the RecipeData object to the RecipeDetailActivity
        intentToStartDetailActivity.putExtra("recipe.details", clickedRecipe);
        startActivity(intentToStartDetailActivity);
    }

    /**
     * This method will make the View for the recipe list visible and
     * hide the error message.
     */
    private void showRecipeDataView() {
        /* Make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Make sure the recipe list is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the
     * View for the recipe list.
     */
    private void showErrorMessage() {
        /* Hide the currently visible view */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchRecipeTask extends AsyncTask<String, Void, RecipeData[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE); //Display the ProgressBar view
        }

        @Override
        protected RecipeData[] doInBackground(String... params) {

                URL recipeRequestUrl = NetworkUtils.buildUrl(); //build Recipe DB request url

                try {
                     String jsonRecipeResponse = NetworkUtils
                        .getResponseFromHttpUrl(recipeRequestUrl); //get JSON response with recipe list data

                     RecipeData[] simpleJsonRecipeData = RecipeJsonUtils
                        .getRecipeDataArrayFromJson(MainActivity.this, jsonRecipeResponse); //parse RecipeData objects from JSON response

                     return simpleJsonRecipeData; //return RecipeData array with recipe details

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }


        @Override
        protected void onPostExecute(RecipeData[] recipeArray) {
            mLoadingIndicator.setVisibility(View.INVISIBLE); //Stop displaying ProgressBar view
            if (recipeArray != null) {
                showRecipeDataView(); //set ReycyclerView to visible
                mRecipeAdapter.setRecipeData(recipeArray); //pass in RecipeData array to adapter
            } else { //if recipe array is empty
                showErrorMessage();
            }
        }
    }
}