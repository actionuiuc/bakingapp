/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: RecipeDetailActivity.java
 * -Adapter used by the MainActivity to retrieve recipe information from the Recipe DB server.
 * -Creates new ViewHolders and binds recipe data to them.
 * -Implements recycler view click handling.
 * -ISSUE: Loading delay when onCreate ran for two-pane mode with Step having video to load.  Black screen appears breifly
 *  between StepDetailActivity and RecipeDetailActivity.  Only seen on a tablet in portrait mode viewing the StepDetailActivity
 *  and rotating the device to landscape mode causing the RecipeDetailActivity to load in two-pane mode.
 *
 */

package com.example.android.baking.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.baking.R;
import com.example.android.baking.model.RecipeData;
import com.example.android.baking.fragments.RecipeDetailsFragment;
import com.example.android.baking.model.StepData;
import com.example.android.baking.fragments.StepDetailsFragment;


public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailsFragment.OnStepClickListener {

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens or tablet in portrait mode and two-pane to larger tablet screens in
    // landscape mode.
    private boolean mTwoPane;
    RecipeData iRecipeDetails;
    StepData iClickedStep;
    StepDetailsFragment newFragment;
    StepDetailsFragment stepFrag;
    FragmentManager fragmentManager;
    Long vidPos;

    private static final String LOG_TAG = RecipeDetailActivity.class.getSimpleName();
    private static final String CLK_STEP_FRAGMENT = "clicked-step-fragment";
    private static final String NEW_STEP_FRAGMENT = "new-step-fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_detail);

        //used to retrieve current video position when transitioning between portrait and landscape mode on a tablet
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        vidPos = pref.getLong("vid_pos", 0);

        fragmentManager = getSupportFragmentManager();

        // Determine if you're creating a two-pane or single-pane display
        if(findViewById(R.id.step_linear_layout) != null) {
            // This LinearLayout will only initially exist in the two-pane tablet case
            mTwoPane = true;

            //Create step details fragment (right hand pane) programmatically
            stepFrag = new StepDetailsFragment();

            Intent startingIntent = getIntent(); //get intent passed from MainActivity that contains the user selected recipe

            if (startingIntent != null) { //setup step detail fragment when loading for first time (display Step 0 details)
                if (startingIntent.hasExtra("recipe.details")) {
                    iRecipeDetails = getIntent().getParcelableExtra("recipe.details");
                    stepFrag.setInitialRecipe(iRecipeDetails);

                    if (startingIntent.hasExtra("step.details")) {//setup fragment using the current clicked step that will be displayed
                        iClickedStep = getIntent().getParcelableExtra("step.details");
                        stepFrag.setClickedStep(iClickedStep);
                        stepFrag.setInitialStatus(false); //fragment added
                        stepFrag.setVideoPosFrag(vidPos); //add video position passed using SharedPreference for tablet Port->Land
                    } else {
                        stepFrag.setClickedStep(iRecipeDetails.steps[0]);
                        stepFrag.setInitialStatus(true); //fragment added
                    }
                }
            }

            stepFrag.setTwoPane(mTwoPane);

                fragmentManager.beginTransaction()
                        .add(R.id.step_details_container, stepFrag, NEW_STEP_FRAGMENT)
                        .commit();


        } else {
            // We're in single-pane mode and displaying fragments on a phone in separate activities
            mTwoPane = false;

            //check if going from tablet landscape to tablet portrait mode.  if so, find and remove existing step fragment
            newFragment = (StepDetailsFragment) fragmentManager.findFragmentByTag(NEW_STEP_FRAGMENT);
            if (newFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(newFragment)
                        .commit();
            }

            stepFrag = (StepDetailsFragment) fragmentManager.findFragmentByTag(CLK_STEP_FRAGMENT);
            if (stepFrag != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(stepFrag)
                        .commit();
            }
        }
    }

    // Define the behavior for onStepSelected
    public void onStepSelected(StepData clickedStep, RecipeData clickedRecipe) {

        // Handle the two-pane case and replace existing step fragment details(right pane) immediately when a new Step is selected
        // from the Recipe Details Fragment(left pane).
        if (mTwoPane) {
            // Create two-pane interaction
            newFragment = new StepDetailsFragment();

            newFragment.setTwoPane(mTwoPane);
            newFragment.setInitialRecipe(clickedRecipe);
            newFragment.setInitialStatus(false);
            newFragment.setClickedStep(clickedStep);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_details_container, newFragment, CLK_STEP_FRAGMENT)
                    .commit();

        } else {
            // Handle the single-pane phone case by passing information in a Bundle attached to an Intent
            Class destinationClass = StepDetailActivity.class;
            Intent intentToStartDetailActivity = new Intent(this, destinationClass);
            //Pass the recipe and step details in the form of the RecipeData object to the StepDetailActivity
            intentToStartDetailActivity.putExtra("step.details", clickedStep);
            intentToStartDetailActivity.putExtra("recipe.details", clickedRecipe);
            intentToStartDetailActivity.putExtra("step.number", clickedStep.stepId);
            startActivity(intentToStartDetailActivity);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state)
    {
        super.onSaveInstanceState(state);
    }

}
