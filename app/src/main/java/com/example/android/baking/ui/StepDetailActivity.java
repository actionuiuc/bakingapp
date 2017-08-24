/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: StepDetailActivity.java
 * -Activity called by RecipeDetailsActivity to display Step details when application is used on a phone in single pane mode.
 * -Fills the views in the child activity_step_detail layout with the data provided in the intent sent from the parent RecipeDetailsActivity.
 *
 */

package com.example.android.baking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.android.baking.R;
import com.example.android.baking.model.RecipeData;
import com.example.android.baking.model.StepData;
import com.example.android.baking.fragments.StepDetailsFragment;

public class StepDetailActivity extends AppCompatActivity {

    RecipeData iRecipeDetails;
    StepData iStepDetails;

    private static final String LOG_TAG = StepDetailActivity.class.getSimpleName();
    private static final String STEP_FRAGMENT = "step-fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        Intent startingIntent = getIntent();

        //if orientation is changed from portrait to landscape when viewing the StepDetailActivity, call the RecipeDetailActivity
        //passing intent
        if(getResources().getBoolean(R.bool.tablet_land)) {
            if (startingIntent != null) { //will always contain all required extras
                iRecipeDetails = getIntent().getParcelableExtra("recipe.details");
                iStepDetails = getIntent().getParcelableExtra("step.details");
                Class destinationClass = RecipeDetailActivity.class;
                Intent intentToStartDetailActivity = new Intent(this, destinationClass);
                //Pass the recipe and step details in the form of the RecipeData object to the StepDetailActivity
                intentToStartDetailActivity.putExtra("step.details", iStepDetails);
                intentToStartDetailActivity.putExtra("recipe.details", iRecipeDetails);
                intentToStartDetailActivity.putExtra("step.number", iStepDetails.stepId);
                startActivity(intentToStartDetailActivity);
                finish();
            }

        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        StepDetailsFragment stepFrag;


        if(savedInstanceState == null) {
            //Create step details fragment (single pane) programmatically if first time Activity has been called.
            stepFrag = new StepDetailsFragment();
        }
        else {  //find existing fragment when screen is rotated (will be replaced)
            stepFrag = (StepDetailsFragment) fragmentManager.findFragmentByTag(STEP_FRAGMENT);
        }


        if (startingIntent != null) { //will always contain all required extras
                iRecipeDetails = getIntent().getParcelableExtra("recipe.details");
                iStepDetails = getIntent().getParcelableExtra("step.details");
                stepFrag.setInitialRecipe(iRecipeDetails);
                stepFrag.setClickedStep(iStepDetails);
                stepFrag.setInitialStatus(false);
                stepFrag.setTwoPane(false);
        }

        if(savedInstanceState == null) { //first call to Activity
            fragmentManager.beginTransaction()
                    .add(R.id.step_details_container, stepFrag, STEP_FRAGMENT)
                    .commit();
        } else { //replace existing fragment in situations of screen rotation
            fragmentManager.beginTransaction()
                    .replace(R.id.step_details_container, stepFrag)
                    .commit();
        }

    }
}