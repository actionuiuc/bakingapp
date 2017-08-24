/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: RecipeDetailsFragment.java
 * -Adapter used by the MainActivity to retrieve recipe information from the Recipe DB server.
 * -Creates new ViewHolders and binds recipe data to them.
 * -Implements recycler view click handling.
 * -Scroll position is captured and restored on rotation.
 *
 *
 */


package com.example.android.baking.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.baking.R;
import com.example.android.baking.adapters.StepAdapter;
import com.example.android.baking.data.StepColumns;
import com.example.android.baking.data.StepProvider;
import com.example.android.baking.model.RecipeData;
import com.example.android.baking.model.StepData;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class RecipeDetailsFragment extends Fragment implements StepAdapter.StepAdapterOnClickHandler {

    private TextView mRecipeTitleDisplay;
    private ImageView mThumb;
    private LinearLayout mIngredientLayout;
    private TextView mIngredientHeader;
    private TextView mServingSize;
    private TextView mErrorMessageIngredient;

    private RecyclerView mRecyclerViewDetail;
    private StepAdapter mStepAdapter;

    RecipeData dRecipe;

    NestedScrollView scroller;
    int scrollPosY;

    GridLayoutManager layoutManager;

    private static final String LOG_TAG = RecipeDetailsFragment.class.getSimpleName();
    private static final String SAVED_POSITION = "nestedscrollview-position";

    // Define a new interface OnStepClickListener that triggers a callback in the host activity
    OnStepClickListener mCallback;

    // OnStepClickListener interface, calls a method in the host activity named onStepSelected
    public interface OnStepClickListener {
        void onStepSelected(StepData clickedStep, RecipeData clickedRecipe);
    }


    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepClickListener");
        }
    }


    // Mandatory empty constructor
    public RecipeDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //super.onCreateView(inflater, container, savedInstanceState);
        Log.i(LOG_TAG, "Running onCreateView");
        final View rootView = inflater.inflate(R.layout.activity_detail, container, false);

        mRecipeTitleDisplay = (TextView) rootView.findViewById(R.id.detail_recipe_title);
        mThumb = (ImageView) rootView.findViewById(R.id.recipe_thumb);
        mIngredientLayout = (LinearLayout) rootView.findViewById(R.id.ingredient_list);
        mIngredientHeader = (TextView) rootView.findViewById(R.id.tv_ingredient_header);
        mServingSize = (TextView) rootView.findViewById(R.id.serving_size);
        mErrorMessageIngredient = (TextView) rootView.findViewById(R.id.tv_error_message_ingredient);
        scroller = (NestedScrollView) rootView.findViewById(R.id.nest_view);

        String srvSize;

        Intent intentThatStartedThisActivity = getActivity().getIntent();

        //setup recyclerview for the list of steps
        mRecyclerViewDetail = (RecyclerView) rootView.findViewById(R.id.recyclerview_steps);


        /* GridLayoutManager is used with 1 column */
            layoutManager = new GridLayoutManager(getContext(), 1);
            mRecyclerViewDetail.setLayoutManager(layoutManager);

            mRecyclerViewDetail.setHasFixedSize(true);

        mStepAdapter = new StepAdapter(this);
        mRecyclerViewDetail.setAdapter(mStepAdapter);

        //set saved scroll position after screen rotation
        if (savedInstanceState != null) {
            scroller.setScrollY(savedInstanceState.getInt(SAVED_POSITION));
        }

        //capture scroll position (Y-coordinate) of NestedScrollView
        if (scroller != null) {
            scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    //Log.i(LOG_TAG, "Scroll Y: " + scrollY);
                    scrollPosY = scrollY;
                }
            });
        }

        //get information from the intent sent from the main activity with the recipe chosen by the user
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("recipe.details")) {

                dRecipe = getActivity().getIntent().getParcelableExtra("recipe.details");

                mRecipeTitleDisplay.setText(dRecipe.name);

                srvSize = "Serving Size: " + dRecipe.servings;

                mServingSize.setText(srvSize);

                //use available recipe thumbnail image
                if (!dRecipe.image.isEmpty() && (dRecipe.image != null)) {
                    Picasso.with(mThumb.getContext())
                            .load(dRecipe.image)
                            .placeholder(R.drawable.meal_icon)
                            .error(R.drawable.mv_placeholder_error)
                            .into(mThumb);
                } else {
                    mThumb.setImageResource(R.drawable.meal_icon);
                }


                //setup ingredient list to display for the recipe
                if (dRecipe.ingredients != null && dRecipe.ingredients.length > 0) {  //check if populated array of ingredients objects returned
                    showIngredientDataView(); //display Ingredient section title

                    for (int j = 0; j < dRecipe.ingredients.length; j++) {

                        //Loop through each IngredientData object and inflate each ingredient item inside a LinearLayout
                        //in the activity_detail view to display with the ingredient name, quantity and unit of measure

                        View mRecipeIngredientItem = inflater.inflate(R.layout.ingredient_list_item, container, false);

                        TextView mRecipeIngredientQuant = (TextView) mRecipeIngredientItem.findViewById(R.id.ingredient_quantity);
                        DecimalFormat df = new DecimalFormat("###.##"); //format quantity to use decimal style if applicable
                        mRecipeIngredientQuant.setText(String.valueOf(df.format(dRecipe.ingredients[j].quantity)));

                        TextView mRecipeIngredientMeasure = (TextView) mRecipeIngredientItem.findViewById(R.id.ingredient_measure);
                        mRecipeIngredientMeasure.setText(dRecipe.ingredients[j].measure);

                        TextView mRecipeIngredientName = (TextView) mRecipeIngredientItem.findViewById(R.id.ingredient_name);
                        mRecipeIngredientName.setText(dRecipe.ingredients[j].ingName);

                        mIngredientLayout.addView(mRecipeIngredientItem);
                    }
                }
                else {
                    showIngredientErrorView();
                }

            }

            mStepAdapter.setRecipeData(dRecipe.steps, dRecipe);
            mRecyclerViewDetail.setVisibility(View.VISIBLE);

        }

        // Return the root view
        return rootView;
    }

    //hide error message and display list of ingredients
    private void showIngredientDataView() {
        mIngredientHeader.setVisibility(View.VISIBLE);
        mServingSize.setVisibility(View.VISIBLE);
        mErrorMessageIngredient.setVisibility(View.GONE);

    }

    //display error and hide list of ingredients
    private void showIngredientErrorView() {
        mIngredientHeader.setVisibility(View.GONE);
        mServingSize.setVisibility(View.GONE);
        mErrorMessageIngredient.setVisibility(View.VISIBLE);

    }

    //Two onClick scenarios are overriden
    //  1.  When a user clicks on a step from the step list in order to display the step details in the right-pane
    //  2.  When a user checks the checkbox next to the step in the step list to signify that they completed the step (write to DB).

    //when a user clicks a Step from the Step list in the left pane, call onClick
    @Override
    public void onClick(StepData clickedStep, RecipeData clickedRecipe) {
        mCallback.onStepSelected(clickedStep, clickedRecipe);
    }

    @Override
    //Insert the step completion details into the database when a use clicks the step checkbox.
    public void onClick(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        if (checked) {
            Log.i(LOG_TAG, "Added Step checkbox information to the db!");

            ContentValues contentValues = new ContentValues();

            contentValues.put(StepColumns.RECIPE_ID, dRecipe.id);
            contentValues.put(StepColumns.STEP_ID, view.getId());

            getContext().getContentResolver().insert(StepProvider.Steps.CONTENT_URI, contentValues);
        } else {
            Log.i(LOG_TAG, "Delete!"); //delete step completion information when checkbox in unchecked
            getContext().getContentResolver().delete(StepProvider.Steps.withIds(dRecipe.id, view.getId()), null, null);
        }
    }

    //save state with scroll position in NestedScrollView for config/orientation changes.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.i(LOG_TAG, "ScrollPosition: " + scrollPosY);
        outState.putInt(SAVED_POSITION, scrollPosY);
    }
}