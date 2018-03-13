package ericconnect.sitehr;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.AndroidTestRunner;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static org.junit.Assert.*;



/**
 * Created by Eric on 12/8/17.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>{
    private Person person;
    private String personName = "Eric";
    private String personStatus = "2";
    private String personPhone = "123456789";
    private String personImgUrl = "http://192.168.1.189:8080/apps/Samples/thumb/sample03.jpg?1513112208";
    private String personId = "public_id Test";
    private String personDpmt = "Test Department";
    private String md5 = "ebea99ea2fcd4ad6148a9af7f9dac541";

    private MainActivity mainActivity;


    public MainActivityTest(){
        super(MainActivity.class);
    }
    @Before
    public void setUp() throws Exception {
        person = new Person();
        person.setName(personName);
        person.setStatus(2);
        person.setPhone(personPhone);
        person.setImgUrl(personImgUrl);
        person.setId(personId);
        person.setDpmt(personDpmt);


        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mainActivity = getActivity();

    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void changePersonView() throws Exception {

        onView(withId(R.id.btn_test1)).perform(click());

        onView(withId(R.id.txt_name)).check(matches(withText(personName)));
        onView(withId(R.id.txt_status)).check(matches(withText("注销")));
        onView(withId(R.id.txt_dptmt)).check(matches(withText(personDpmt)));
    }

    @Test
    public void getPublicIdFromQRString() throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("public_id", person.getId());
        jsonObject.put("name", person.getName());
        jsonObject.put("phone", person.getPhone());
        jsonObject.put("department", person.getDpmt() );
        jsonObject.put("md5", md5);

        /**
         * { "public_id": "public_id Test","name": "Eric","department": "Test Department","md5": "ebea99ea2fcd4ad6148a9af7f9dac541"}
         */

        String public_id = mainActivity.getPublicIdFromQRString(jsonObject.toString());
        assertEquals(public_id, personId);
    }


}