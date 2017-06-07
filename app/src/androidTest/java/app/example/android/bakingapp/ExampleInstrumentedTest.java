package app.example.android.bakingapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import app.example.android.bakingapp.ui.ActivityRecipeList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest{
    //@Rule
    //public ActivityTestRule<ActivityRecipeList> mActivityRule = new ActivityTestRule(ActivityRecipeList.class);
    @Rule
    public IntentsTestRule<ActivityRecipeList> mActivityRule = new IntentsTestRule<ActivityRecipeList>(ActivityRecipeList.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        Espresso.registerIdlingResources(mIdlingResource);
    }
    @Test
    public void checkOnClick() {
        onView(ViewMatchers.withId(R.id.recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        intended(allOf(
            hasExtra("name","Brownies"),
            hasExtra("_ID","2"),
            toPackage("app.example.android.bakingapp")));
    }
    @Test
    public void useAppContext() throws Exception{
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("app.example.android.bakingapp",appContext.getPackageName());
    }
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
