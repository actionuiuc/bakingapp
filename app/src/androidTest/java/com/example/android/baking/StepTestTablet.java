/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: StepTestTablet.java
 * -Works only on tablets using RecipeDetailActivity having two fragments in two panes.
 * -This test demos a user clicking a step from the list of steps on the recipe details screen (left pane).
 * -Clicks on the 'Recipe Introduction' step in RecipeDetailsFragment and verifies that the step details for the 'Recipe
 *  Introduction' are displayed in the right pane StepDetailsFragment.
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

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.baking.ui.MainActivity;
import com.example.android.baking.utils.CustomScrollActions;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class StepTestTablet {

    private static final int STEP_NUMBER = 0;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    public static final String STEP_TITLE = "Recipe Introduction";

    /**
     * Clicks on 'Recipe Introduction' step in RecipeDetailsFragment and verifies that the StepDetailsFragment loads the
     * details in the right pane.
     */
    @Test
    public void clickStepList_checkStepDetail() {

        onView(withId(R.id.recyclerview_recipes))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(0, click())
                );

        onView(allOf(withId(R.id.short_desc), withText("Recipe Introduction")))
                .perform(CustomScrollActions.nestedScrollTo())
                .check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.recyclerview_steps))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(0, click())
                );

        onView(withId(R.id.step_header_num)).check(matches(withText(STEP_TITLE)));

    }
}