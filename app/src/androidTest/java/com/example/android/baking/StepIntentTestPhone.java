/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: StepIntentTestPhone.java
 * -Works only on phones using RecipeDetailActivity having a single pane.
 * -This test demos a user clicking a step from the list of steps on the recipe details screen.
 * -Clicks on the 'Recipe Introduction' step in the RecipeDetailsFragment and verifies that the correct intent was created to
 *   pass to the StepDetailActivity.
 * -Verifies that the intent created when clicking on the 'Recipe Introduction' step has the extra 'step.number' equal to the
 *   STEP_NUMBER value of 0.
 *
 *
 */

package com.example.android.baking;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.baking.ui.MainActivity;
import com.example.android.baking.ui.RecipeDetailActivity;
import com.example.android.baking.utils.CustomScrollActions;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class StepIntentTestPhone {

    private static final int STEP_NUMBER = 0;

    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<>(MainActivity.class);

    /**
     * Clicks on 'Recipe Introduction' step in RecipeDetailsFragment and verifies that intent passed to StepDetailActivity
     * contains the extra 'step.number' equal to the STEP_NUMBER value of 0
     */
    @Test
    public void clickStepDetail_checkIntent() {

        onView(withId(R.id.recyclerview_recipes))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(0, click())
                );

        //scroll to the first step

        onView(allOf(withId(R.id.short_desc), withText("Recipe Introduction")))
                .perform(CustomScrollActions.nestedScrollTo())
                .check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.recyclerview_steps))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(0, click())
                );


        intended(hasComponent(RecipeDetailActivity.class.getName()));
        intended(hasExtras(hasEntry(equalTo("step.number"), equalTo(STEP_NUMBER))));
    }

}