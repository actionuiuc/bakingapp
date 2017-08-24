package com.example.android.baking.utils;

/**
 * Modified from original by F1sherKK.
 * https://github.com/AzimoLabs/CustomScrollActions/blob/master/app/src/androidTest/java/
 * customscrollactions/azimolabs/com/customscrollactions/utils/CustomScrollActions.java
 *
 * Solves problem of scrolling in NestedScrollView in Espresso.
 * More information can be found at: https://medium.com/azimolabs/
 * guide-to-make-custom-viewaction-solving-problem-of-nestedscrollview-in-espresso-35b133850254
 */

        import android.support.test.espresso.PerformException;
        import android.support.test.espresso.UiController;
        import android.support.test.espresso.ViewAction;
        import android.support.test.espresso.matcher.ViewMatchers;
        import android.support.test.espresso.util.HumanReadables;
        import android.support.v4.widget.NestedScrollView;
        import android.view.View;
        import android.view.ViewParent;
        import android.widget.FrameLayout;

        import org.hamcrest.Matcher;
        import org.hamcrest.Matchers;

        import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
        import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
        import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;


public class CustomScrollActions {

    public static ViewAction nestedScrollTo() {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return Matchers.allOf(
                        isDescendantOfA(isAssignableFrom(NestedScrollView.class)),
                        withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE));
            }

            @Override
            public String getDescription() {
                return "Find parent with type " + NestedScrollView.class +
                        " of matched view and programmatically scroll to it.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                try {
                    NestedScrollView nestedScrollView = (NestedScrollView)
                            findFirstParentLayoutOfClass(view, NestedScrollView.class);
                    if (nestedScrollView != null) {
                        nestedScrollView.scrollTo(0, view.getTop());
                    } else {
                        throw new Exception("Unable to find NestedScrollView parent.");
                    }
                } catch (Exception e) {
                    throw new PerformException.Builder()
                            .withActionDescription(this.getDescription())
                            .withViewDescription(HumanReadables.describe(view))
                            .withCause(e)
                            .build();
                }
                uiController.loopMainThreadUntilIdle();
            }
        };
    }


    private static View findFirstParentLayoutOfClass(View view, Class<? extends View> parentClass) {
        ViewParent parent = new FrameLayout(view.getContext());
        ViewParent incrementView = null;
        int i = 0;
        while (parent != null && !(parent.getClass() == parentClass)) {
            if (i == 0) {
                parent = findParent(view);
            } else {
                parent = findParent(incrementView);
            }
            incrementView = parent;
            i++;
        }
        return (View) parent;
    }

    private static ViewParent findParent(View view) {
        return view.getParent();
    }

    private static ViewParent findParent(ViewParent view) {
        return view.getParent();
    }
}
