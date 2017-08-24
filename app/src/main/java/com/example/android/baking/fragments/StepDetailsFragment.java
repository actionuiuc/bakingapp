/*
 * Brian Jackson
 * bj1412@att.com
 * 8/5/2017
 * Android Developer Nanodegree
 * Project 3: Baking App
 *
 * Filename: StepDetailsFragment.java
 * -Fragment to display the details of a specific recipe step.
 * -Used in either two-pane mode (right pane) or single pane mode.
 *
 */

package com.example.android.baking.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.baking.R;
import com.example.android.baking.model.RecipeData;
import com.example.android.baking.model.StepData;
import com.example.android.baking.ui.StepDetailActivity;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class StepDetailsFragment extends Fragment {

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;

    private TextView mRecipeName;
    private TextView mStepTitle;
    private TextView mShortDesc;
    private TextView mDescription;
    private TextView mPrevTitle;
    private TextView mNextTitle;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;

    StepData currStep;
    RecipeData dRecipe;
    long videoCurrentPosition = 0; //used to store video position Exoplayer (initially set to 0)

    String stepTitle;
    String nextStepTitle;
    String prevStepTitle;
    int totalSteps;

    private boolean sTwoPane;
    RecipeData initialRecipe;
    Boolean initialStatus;
    StepData clickedStep;
    Uri builtUri;

    View rootView;

    private static final String LOG_TAG = StepDetailsFragment.class.getSimpleName();
    private static final String SAVED_POSITION = "exoplayer-position";

    // Mandatory empty constructor
    public StepDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //set saved video position after screen rotation
        if (savedInstanceState != null) {
            videoCurrentPosition = savedInstanceState.getLong(SAVED_POSITION, 0);
            //Log.i(LOG_TAG, "Exoplayer Position Passed: " + videoCurrentPosition);
        }

        rootView = inflater.inflate(R.layout.activity_step, container, false);


        mRecipeName = (TextView) rootView.findViewById(R.id.step_recipe_title);
        mStepTitle = (TextView) rootView.findViewById(R.id.step_header_num);
        mShortDesc = (TextView) rootView.findViewById(R.id.step_header_short_desc);
        mDescription = (TextView) rootView.findViewById(R.id.step_description);
        mPrevTitle = (TextView) rootView.findViewById(R.id.prev_step_title);
        mNextTitle = (TextView) rootView.findViewById(R.id.next_step_title);
        mNextButton = (ImageButton) rootView.findViewById(R.id.next_arrow_button);
        mPrevButton = (ImageButton) rootView.findViewById(R.id.prev_arrow_button);

        Intent intentThatStartedThisActivity = getActivity().getIntent();

        //Two-pane mode: intent is sent from MainActivity
        //Single pane mode: intent is sent from RecipeDetailActivity
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("recipe.details")) { //both intents will contain the recipe.details extra
                dRecipe = getActivity().getIntent().getParcelableExtra("recipe.details");
                mRecipeName.setText(dRecipe.name);
                totalSteps = dRecipe.stepCount;

            }

            if ((intentThatStartedThisActivity.hasExtra("step.details")) && (sTwoPane == false)) {  //intent used in single pane mode

                currStep = getActivity().getIntent().getParcelableExtra("step.details");

                if (currStep.stepId == 0) {
                    stepTitle = "Recipe Introduction";
                    mShortDesc.setText("");
                } else {
                    stepTitle = getString(R.string.step_title) + " " + String.valueOf(currStep.stepId);
                    mShortDesc.setText(currStep.shortDescr);
                }

                mStepTitle.setText(stepTitle);
                mDescription.setText(currStep.descr);

                //Display appropriate bottom navigation when current step is first, last, or somewhere inbetween
                //Bottom navigation is ONLY displayed in single pane mode
                if(currStep.stepId == 0) {
                    mPrevTitle.setVisibility(View.GONE);
                    showNextNav();
                } else if(dRecipe.stepCount == (currStep.stepId + 1) ) {
                    mNextTitle.setVisibility(View.GONE);
                    showPrevNav();
                } else {
                    showPrevNav();
                    showNextNav();
                }


            } else if(sTwoPane == true) { //load information to display in the views in two-pane mode
                if(initialStatus == true) { //if recipe details view is being displayed for the first time display details for the first step
                    currStep = initialRecipe.steps[0];
                } else { //load recipe details for the step clicked by the user
                    currStep = clickedStep;
                }

                mRecipeName.setVisibility(View.GONE);

                if (currStep.stepId == 0) {
                    stepTitle = "Recipe Introduction";
                    mShortDesc.setText("");
                } else {
                    stepTitle = getString(R.string.step_title) + " " + String.valueOf(currStep.stepId);
                    mShortDesc.setText(currStep.shortDescr);
                }

                mStepTitle.setText(stepTitle);

                mDescription.setText(currStep.descr);

                //Don't show bottom naviation arrows when in two-pane mode
                mPrevTitle.setVisibility(View.GONE);
                mNextTitle.setVisibility(View.GONE);
                mNextButton.setVisibility(View.GONE);
                mPrevButton.setVisibility(View.GONE);
            }

        }

        // Return the root view
        return rootView;
    }

    //display bottom navigation to go to next step in recipe
    private void showNextNav() {
        nextStepTitle = getString(R.string.step_title) + " " + String.valueOf(currStep.stepId + 1);
        mNextTitle.setText(nextStepTitle);

        //create intent to pass when navigation text is clicked
        mNextTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Class destinationClass = StepDetailActivity.class;
                Intent intentToStartDetailActivity = new Intent(context, destinationClass);
                intentToStartDetailActivity.putExtra("step.details", dRecipe.steps[currStep.stepId + 1]);  //pass the next step details
                intentToStartDetailActivity.putExtra("recipe.details", dRecipe);
                startActivity(intentToStartDetailActivity);
            }
        });

        //create intent to pass when navigation button is clicked
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Class destinationClass = StepDetailActivity.class;
                Intent intentToStartDetailActivity = new Intent(context, destinationClass);
                intentToStartDetailActivity.putExtra("step.details", dRecipe.steps[currStep.stepId + 1]);  //pass the next step details
                intentToStartDetailActivity.putExtra("recipe.details", dRecipe);
                startActivity(intentToStartDetailActivity);
            }
        });

        mNextTitle.setVisibility(View.VISIBLE);
        mNextButton.setVisibility(View.VISIBLE);
    }

    //display bottom navigation to go to previous step in recipe
    private void showPrevNav() {

        if(currStep.stepId == 1) {
            prevStepTitle = "Intro";
        } else {
            prevStepTitle = getString(R.string.step_title) + " " + String.valueOf(currStep.stepId - 1);
        }

        mPrevTitle.setText(prevStepTitle);

        //create intent to pass when navigation text is clicked
        mPrevTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Class destinationClass = StepDetailActivity.class;
                Intent intentToStartDetailActivity = new Intent(context, destinationClass);
                intentToStartDetailActivity.putExtra("step.details", dRecipe.steps[currStep.stepId - 1]);  //pass the previous step details
                intentToStartDetailActivity.putExtra("recipe.details", dRecipe);
                startActivity(intentToStartDetailActivity);
            }
        });

        //create intent to pass when navigation button is clicked
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Class destinationClass = StepDetailActivity.class;
                Intent intentToStartDetailActivity = new Intent(context, destinationClass);
                intentToStartDetailActivity.putExtra("step.details", dRecipe.steps[currStep.stepId - 1]);  //pass the previous step details
                intentToStartDetailActivity.putExtra("recipe.details", dRecipe);
                startActivity(intentToStartDetailActivity);
            }
        });

        mPrevTitle.setVisibility(View.VISIBLE);
        mPrevButton.setVisibility(View.VISIBLE);
    }

    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri, Long videoCurrentPosition) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingAppStepDetails");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.seekTo(videoCurrentPosition);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }


    @Override
    public void onPause() {
        videoCurrentPosition = mExoPlayer.getCurrentPosition(); //record current video position incase onPause is run before onSaveInstanceState
        super.onPause();
        releasePlayer();
        //Log.i(LOG_TAG, "On Pause");
    }

    @Override
    public void onStop() {
        if (mExoPlayer != null) {
            videoCurrentPosition = mExoPlayer.getCurrentPosition();
        }
        super.onStop();
        releasePlayer();
        //Log.i(LOG_TAG, "On Stop");
    }

    @Override
    public void onResume() {
        super.onResume();
        setupPlayer(rootView, currStep.videoUrl, videoCurrentPosition); //setup Exoplayer
        //Log.i(LOG_TAG, "On Resume");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    //Method used to identify if user is using a tablet or phone
    //Called when programmatically adding or replacing the StepDetailsFragment.
    public void setTwoPane(boolean twoPaneStatus) {
        sTwoPane = twoPaneStatus;
    }

    //Method used to capture current Recipe chosen by the user.
    //Called when programmatically adding or replacing the StepDetailsFragment.
    public void setInitialRecipe(RecipeData recipeDetails) {
        initialRecipe = recipeDetails;
    }

    //Method used to identify if this is the first time the StepDetailsFragment has been loaded for a chosen recipe.
    //Called when programmatically adding or replacing the StepDetailsFragment.
    public void setInitialStatus(Boolean initStatus) {
        initialStatus = initStatus;
    }

    //Method used to capture recipe step object chosen by the user to display details for.
    //Called when programmatically adding or replacing the StepDetailsFragment.
    public void setClickedStep(StepData clkStep) {
        clickedStep = clkStep;
    }

    //Method used to identify the current video position that will be displayed in the fragment.
    //Called when programmatically adding or replacing the StepDetailsFragment.
    public void setVideoPosFrag(Long vidPosPassed) {
        videoCurrentPosition = vidPosPassed;
    }

    //setup Exoplayer
    public void setupPlayer(View rtView, String vdURL, Long vdCurrPos) {
        // Initialize the player view.
        mPlayerView = (SimpleExoPlayerView) rtView.findViewById(R.id.playerView);

        mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                (getResources(), R.drawable.mv_placeholder));

        builtUri = Uri.parse(vdURL).buildUpon()
                .build();

        // Initialize the player.
        initializePlayer(builtUri, vdCurrPos);
    }

    //save video position and restore on screen rotation
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mExoPlayer != null) {
            videoCurrentPosition = mExoPlayer.getCurrentPosition();
            outState.putLong(SAVED_POSITION, videoCurrentPosition);
        } else { //used in case onPause is called before onSaveInstanceState
            outState.putLong(SAVED_POSITION, videoCurrentPosition);
        }

        //used to share video position on tablet between Portrait Step view and Dual Pane view.
        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putLong("vid_pos", videoCurrentPosition);
        edt.commit();

    }

}
