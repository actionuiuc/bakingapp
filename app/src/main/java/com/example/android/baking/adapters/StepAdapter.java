/*
 * Brian Jackson
 * bj1412@att.com
 * 8/6/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: StepAdapter.java
 * -Adapter used by the RecipeDetailsFragment to perform
 * -Creates new ViewHolders and binds step data to them.
 * -Implements recycler view click handling.
 *
 */


package com.example.android.baking.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.baking.R;
import com.example.android.baking.data.StepColumns;
import com.example.android.baking.data.StepProvider;
import com.example.android.baking.model.RecipeData;
import com.example.android.baking.model.StepData;
import com.squareup.picasso.Picasso;


public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepAdapterViewHolder> {

    private StepData[] mStepData;
    private RecipeData mRecipeData;
    private static final String LOG_TAG = StepAdapter.class.getSimpleName();

    /*
     * An on-click handler used for an Activity to interface with the RecyclerView
     */
    private final StepAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     * Two onClick scenarios are overriden.
     *    1.  When a user clicks on a step from the step list in order to display the step details in the right-pane
     *    2.  When a user checks the checkbox next to a step in the step list to record that they completed the step (write to DB).
     */
    public interface StepAdapterOnClickHandler {
        void onClick(StepData clickedStep, RecipeData clickedRecipe);  //step click
        void onClick(View view); //checkbox click
    }

    /**
     * Creates a RecipeAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public StepAdapter(StepAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }


    /**
     * Cache of the RecyclerView's children views each representing a specific step in the recipe.
     */
    public class StepAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public final ImageView mThumbImageView; //step thumbnail
        public final TextView mStepShortDescTextView;  //step short description
        public final CheckBox mCheckboxDone;  //step checkbox
        public final TextView mStepTitle;  //step title



        public StepAdapterViewHolder(View view) {
            super(view);
            mThumbImageView = (ImageView) view.findViewById(R.id.thumb_image);
            mStepShortDescTextView = (TextView) view.findViewById(R.id.short_desc);
            mCheckboxDone = (CheckBox) view.findViewById(R.id.checkbox_done);
            mStepTitle = (TextView) view.findViewById(R.id.step_num);

            mCheckboxDone.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.   Called
         * for any click.  Method will determine which overriden onClick will be called based on the view id passed in
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {

            if(v.getId() != mCheckboxDone.getId() ) { //step clicked
                int adapterPosition = getAdapterPosition();
                mClickHandler.onClick(mStepData[adapterPosition], mRecipeData);
            } else { //checkbox clicked
                mClickHandler.onClick(v);
            }

        }
    }

    /**
     * This method is called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  View type of the new view.
     * @return A new StepAdapterViewHolder that holds the View for each list item
     */
    @Override
    public StepAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.step_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new StepAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the step data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the step
     * for this particular position, using the "position" argument that is
     * passed in.
     *
     * @param stepAdapterViewHolder    The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(StepAdapterViewHolder stepAdapterViewHolder, int position) {
        String stepTitle;

        Context context = stepAdapterViewHolder.mThumbImageView.getContext();

        if(position == 0) {
            stepTitle = "Recipe Introduction";
        } else {
            stepTitle = context.getString(R.string.step_title) + " " + String.valueOf(mStepData[position].stepId);
        }

        //Determine if checkbox should be checked by querying the database of completed steps
        Cursor c = context.getContentResolver().query(StepProvider.Steps.CONTENT_URI, null, null, null, null);

        boolean foundStep = false;

        for (int i = 0; i < c.getCount(); i++) {
            c.moveToNext();

            int stepId;
            int stepIdCol = c.getColumnIndex(StepColumns.STEP_ID);
            stepId = c.getInt(stepIdCol);

            int recipeId;
            int recipeIdCol = c.getColumnIndex(StepColumns.RECIPE_ID);
            recipeId = c.getInt(recipeIdCol);

            //check if entry exists with chosen recipe id and step id
            if((mStepData[position].stepId == stepId) && (mStepData[position].recipeId == recipeId)) {
                stepAdapterViewHolder.mCheckboxDone.setChecked(true);
                foundStep = true;
            }

        }

        if(foundStep == false)
            stepAdapterViewHolder.mCheckboxDone.setChecked(false);

        c.close();

        //set views to display step information
        if (!mStepData[position].thumbUrl.isEmpty() && (mStepData[position].thumbUrl != null)) {
            Picasso.with(context)
                    .load(mStepData[position].thumbUrl)
                    .placeholder(R.drawable.measure_cup)
                    .error(R.drawable.mv_placeholder_error)
                    .into(stepAdapterViewHolder.mThumbImageView);
            stepAdapterViewHolder.mStepShortDescTextView.setText(mStepData[position].shortDescr);
            stepAdapterViewHolder.mCheckboxDone.setId(mStepData[position].stepId);
            stepAdapterViewHolder.mStepTitle.setText(stepTitle);
        } else {
            stepAdapterViewHolder.mThumbImageView.setImageResource(R.drawable.measure_cup);
            stepAdapterViewHolder.mStepShortDescTextView.setText(mStepData[position].shortDescr);
            stepAdapterViewHolder.mCheckboxDone.setId(mStepData[position].stepId);
            stepAdapterViewHolder.mStepTitle.setText(stepTitle);
        }
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views.
     *
     * @return The number of items available in the step list array.
     */
    @Override
    public int getItemCount() {
        if (null == mStepData) return 0;
        return mStepData.length;
    }

    /**
     * This method is used to set the recipe object and step array objects on a RecipeAdapter if we've already
     * created one.
     *
     * @param stepData The new step data to be displayed in the form of the step array list.
     *
     * @param rData The recipe object to which the step data belongs.
     */
    public void setRecipeData(StepData[] stepData, RecipeData rData) {
        mStepData = stepData;
        mRecipeData = rData;
        notifyDataSetChanged();
    }
}