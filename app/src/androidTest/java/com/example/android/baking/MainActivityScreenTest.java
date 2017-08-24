/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: MainActivityScreenTest.java
 * -This test demos a user clicking on a recipe on the first screen.
 * -Clicks on a recipe GridView item in the MainActivity and checks it opens up the RecipeDetailActivity and displays
 *   the 'Ingredients' title
 *
 *
 */

package com.example.android.baking;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.baking.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {

    public static final String RECIPE_NAME = "Ingredients";

    /**
     * The ActivityTestRule is a rule provided by Android used for functional testing of a single
     * activity. The activity that will be tested will be launched before each test that's annotated
     * with @Test and before methods annotated with @Before. The activity will be terminated after
     * the test and methods annotated with @After are complete. This rule allows you to directly
     * access the activity during the test.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Clicks on a recipe GridView item in the MainActivity and checks it opens up the RecipeDetailActivity and displays the 'Ingredients' title
     */
    @Test
    public void clickGridViewItem_OpensOrderActivity() {

        onView(withId(R.id.recyclerview_recipes))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(0, click())
                );

        onView(withId(R.id.tv_ingredient_header)).check(matches(withText(RECIPE_NAME)));


    }

}