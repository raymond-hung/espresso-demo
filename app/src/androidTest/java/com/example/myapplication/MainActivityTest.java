package com.example.myapplication;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.After;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumented test, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest{

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, true, false);

    private MockWebServer server;
    private CountingIdlingResource mainActivityIdlingResource;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        Constant.URL = server.url("/").toString();

        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        mainActivityIdlingResource = activityRule.getActivity().getIdlingResourceForTest();
        IdlingRegistry.getInstance().register(mainActivityIdlingResource);
    }

    @Test
    public void testHttp404Error() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404));
        onView(withId(R.id.btn)).perform(click());
        onView(withText(R.string.msg_http_404_error)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
        if(mainActivityIdlingResource!=null) {
            IdlingRegistry.getInstance().unregister(mainActivityIdlingResource);
        }

    }
}
