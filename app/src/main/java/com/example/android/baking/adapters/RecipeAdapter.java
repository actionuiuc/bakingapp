/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: RecipeAdapter.java
 * -Adapter used by the MainActivity to retrieve recipe information from the Recipe DB server.
 * -Creates new ViewHolders and binds recipe data to them.
 * -Implements recycler view click handling.
 *
 */


package com.example.android.baking.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.baking.R;
import com.example.android.baking.model.RecipeData;
import com.squareup.picasso.Picasso;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {

    private RecipeData[] mRecipeData;

    /*
     * An on-click handler used for an Activity to interface with the RecyclerView
     */
    private final RecipeAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface RecipeAdapterOnClickHandler {
        void onClick(RecipeData clickedRecipe);
    }

    /**
     * Creates a RecipeAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public RecipeAdapter(RecipeAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the RecyclerView's children views each representing a recipe list item.
     */
    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public final ImageView mRecipeImageView;
        public final TextView mRecipeTitleTextView;

        public RecipeAdapterViewHolder(View view) {
            super(view);
            mRecipeImageView = (ImageView) view.findViewById(R.id.recipe_image);
            mRecipeTitleTextView = (TextView) view.findViewById(R.id.recipe_title);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mRecipeData[adapterPosition]);
        }
    }

    /**
     * This method is called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  View type of the new view.
     * @return A new RecipeAdapterViewHolder that holds the View for each list item
     */
    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recipecard_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RecipeAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the recipe
     * thumbnail for this particular position, using the "position" argument that is
     * passed in.
     *
     * @param recipeAdapterViewHolder    The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder recipeAdapterViewHolder, int position) {
        Context context = recipeAdapterViewHolder.mRecipeImageView.getContext();
        if (!mRecipeData[position].image.isEmpty() && (mRecipeData[position].image != null)) {
            Picasso.with(context)
                    .load(mRecipeData[position].image)
                    .placeholder(R.drawable.meal_icon)
                    .error(R.drawable.mv_placeholder_error)
                    .into(recipeAdapterViewHolder.mRecipeImageView);
            recipeAdapterViewHolder.mRecipeTitleTextView.setText(mRecipeData[position].name);
        } else {
            recipeAdapterViewHolder.mRecipeImageView.setImageResource(R.drawable.meal_icon);
            recipeAdapterViewHolder.mRecipeTitleTextView.setText(mRecipeData[position].name);
        }
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views.
     *
     * @return The number of items available in the recipe list array.
     */
    @Override
    public int getItemCount() {
        if (null == mRecipeData) return 0;
        return mRecipeData.length;
    }

    /**
     * This method is used to set the array list of recipe objects on a RecipeAdapter if we've already
     * created one.  Useful when we get new data from the web but don't want to create a
     * new RecipeAdapter to display it.
     *
     * @param recipeData The new recipe data to be displayed in the form of the RecipeData array list.
     */
    public void setRecipeData(RecipeData[] recipeData) {
        mRecipeData = recipeData;
        notifyDataSetChanged();
    }
}