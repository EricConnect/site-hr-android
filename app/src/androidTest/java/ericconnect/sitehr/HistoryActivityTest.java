package ericconnect.sitehr;

import android.app.DatePickerDialog;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.internal.matchers.Matches;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


import okhttp3.Request;
import okhttp3.ResponseBody;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.when;

/**
 * Created by Eric on 3/14/18.
 */

@RunWith(MockitoJUnitRunner.class)
public class HistoryActivityTest extends ActivityInstrumentationTestCase2<HistoryActivity> {
    private final String TAG = getClass().getSimpleName();

    private  HistoryActivity historyActivity;
    private EditText mStartEditText;
    private EditText mEndEditText;
    private MockWebServer webServer;
    @Mock
    private ResponseBody mockResponse;
    @Mock
    private SignOperateInterface signOperateInterface;

    public HistoryActivityTest(){super(HistoryActivity.class);}
    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        historyActivity = getActivity();
        historyActivity.publicID = "fb351d8d-0c6b-4651-9b35-8aee9b59beff";
        webServer = new MockWebServer();
        webServer.start();

        historyActivity.ENDPOINT = webServer.url("/").toString();
    }



    @Test
    public void onCreate() throws Exception {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        onView(withId(R.id.Txt_EndDate)).check(matches(withText(sdf.format(calendar.getTime()))));
        calendar.add(Calendar.DAY_OF_MONTH, -7);

        onView(withId(R.id.Txt_StartDate)).check(matches(withText(sdf.format(calendar.getTime()))));
    }

    @Test
    public void onSearchBtnClick() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        mock(ServiceHelper.class);

        webServer.enqueue(
                new MockResponse()
                .setResponseCode(200)
                .setBody("{\"history\":[[\"Sun, 19 Nov 2017 23:02:05 GMT\",\"in\"],[\"Sun, 19 Nov 2017 23:02:15 GMT\",\"out\"],[\"Sun, 04 Mar 2018 05:17:46 GMT\",\"in\"],[\"Sun, 04 Mar 2018 17:44:31 GMT\",\"in\"],[\"Sun, 04 Mar 2018 18:25:33 GMT\",\"out\"]]}")
        );

        onView(withId(R.id.Btn_HistorySearch)).perform(click());

        latch.await(2, TimeUnit.SECONDS);
        assertEquals(5, historyActivity.gridView.getAdapter().getCount());

    }

    @Test
    public void onStartDateEditClick() throws Exception {
        onView(withId(R.id.Txt_StartDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(1999, 1, 1));
    }

    @Test
    public void onEndDateEditClick() throws Exception {
        onView(withId(R.id.Txt_EndDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(1999, 1, 1));
    }
    @After
    public void tearDown() throws Exception {
        webServer.shutdown();
        super.tearDown();

    }
}