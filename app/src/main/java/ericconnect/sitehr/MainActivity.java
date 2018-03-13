package ericconnect.sitehr;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.common.base.Charsets;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.google.common.hash.*;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.yanzhenjie.permission.Permission;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static String TOKEN;

    public static String operator_id = "admin";

    // types : search, clockin, clockout,
    public static String operation_type = "Search"; // default is search and fetch workers information;
    public static final  String md5Salt = "SITEMANAGEMENT";
    private static String service_url = "";
    private DecoratedBarcodeView mBarcodeView;


    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {

            if(result.getText() != null){
                String str = result.getText();
                String public_id = "";

                try{
                    public_id = getPublicIdFromQRString(str);
                }catch (JSONException e){
                    return;
                }

                //check public_id, if null then exit;
                if(public_id.isEmpty()){
                    return;
                }

                if(operation_type.equals("Search")){
                    operateGetPersonInfo(public_id);
                }else if(operation_type.equals("ClockIn")){
                    operateClockIn(public_id, operator_id);
                }else if(operation_type.equals("ClockOut")){
                    operateClockout(public_id, operator_id);
                }

                //Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };
    /**
    * change activity view when fetch worker's info as correct operation;
    *
     */
    public void changePersonView(Person person){
        if(person == null) return;
        TextView tv_name = findViewById(R.id.txt_name);
        TextView tv_dpmt = findViewById(R.id.txt_dptmt);
        TextView tv_birth = findViewById(R.id.txt_birth);
        TextView tv_status = findViewById(R.id.txt_status);

        tv_name.setText(TextUtils.isEmpty(person.getName())?"":person.getName());
        tv_dpmt.setText(TextUtils.isEmpty(person.getDpmt())?"":person.getDpmt());
        String birth = android.text.format.DateFormat.format("yyyy-MM-dd", person.getBirth()).toString();
        tv_birth.setText(birth);

        String personStatus = "未知";
        switch(person.getStatus()){
            case 1: personStatus = "正常"; break;
            case 2: personStatus = "注销"; break;
            default: break;
        }

        tv_status.setText(personStatus); //show status

        loadImageFromUrl(person.getImgUrl()); //load image from url


    }


    /**
     * load img from url then set image view show the img.
     * @param url
     */
    public void loadImageFromUrl(String url){
        if(isNullOrEmpty(url)) return;
        ImageView imgView = findViewById(R.id.imv_person);
        Glide.with(this)
                .load(url)
                .override(120, 120)
                .into(imgView);

    }


    /**
    * decode from QR code input;
    * also need validate this string;
    * input string is a json , signature is md5( public id, name, department, and MD5 salt )
    * currently define md5 salt : SITEMANAGEMENT
    *
    * {
    * "public_id": "wekjewhtwklh53",
    * "name": "",
    * "department": "",
    * "md5": "sdfsdfsdf"
    * }
     */
    public String getPublicIdFromQRString(String str) throws JSONException{
        String public_id = "";
        String name = "";
        String department = "";
        String signature = "";
        JSONObject cardPerson = new JSONObject(str);
        public_id = cardPerson.getString("public_id");
        name = cardPerson.getString("name");
        department = cardPerson.getString("department");
        signature = cardPerson.getString("md5");

        String validStr = public_id + name + department + md5Salt;

        com.google.common.hash.HashFunction hf = Hashing.md5();
        Hasher hasher = hf.newHasher().putString(validStr, Charsets.UTF_8);

        String hash = hasher.hash().toString();

        if(hash.equalsIgnoreCase(signature)){
            return public_id;

        }



        return "";
    }

    /**
    * fetch information of the worker;
     */
    public void operateGetPersonInfo(String public_id){

        ServiceHelper.getInstance(service_url, TOKEN).getPersonInfo(public_id).enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                if(response.isSuccessful()){
                    Person p = response.body();
                    changePersonView(p);
                }
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {

                Toast.makeText(getApplicationContext(),"出现错误",Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.toString());

            }
        });

    }

    /**
    * set workers Clock in as current datetime;
    *
    */
    public void operateClockIn(String public_id, String operator_id) {
        ServiceHelper.getInstance(service_url, TOKEN).signIn(public_id, operator_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String str = response.body().string();
                        if (str != null && !str.isEmpty()) {
                            int responseCode = new JSONObject(str).getInt("code");

                            if(responseCode == 200) {
                                Toast.makeText(getApplicationContext(), "Success Clock in.", Toast.LENGTH_SHORT).show();
                                operatingSuccess();
                            }else
                            {
                                Toast.makeText(getApplicationContext(),"Error at Clock in. CODE:" + responseCode, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }catch (Exception e){
                    Log.d(TAG, e.toString());
                    Toast.makeText(getApplicationContext(), "Error at" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(MainActivity.this, "Clock in Error!"+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.toString());


            }
        });
    }

    /**
    * set workers clock out as current datetime;
    *
    */
    public void operateClockout(String public_id, String operator_id){
        ServiceHelper.getInstance(service_url, TOKEN).signOut(public_id, operator_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String str = response.body().string();
                        if (str != null && !str.isEmpty()) {
                            int responseCode = new JSONObject(str).getInt("code");

                            if(responseCode == 200) {
                                Toast.makeText(getApplicationContext(), "Success Clock out.", Toast.LENGTH_SHORT).show();
                                operatingSuccess();
                            }else
                            {
                                Toast.makeText(getApplicationContext(),"Error at Clock out. CODE:" + responseCode, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }catch (Exception e){
                    Log.d(TAG, e.toString());
                    Toast.makeText(getApplicationContext(), "Error at" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(MainActivity.this, "Clock out Error!"+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.toString());


            }
        });
    }

    private void operatingSuccess() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onTestBtnClick(View v){
        Person pt = new Person();
        pt.setDpmt("Test Department");
        pt.setName("Eric");
        pt.setBirth(new Date(System.currentTimeMillis()));
        pt.setPhone("123456789");
        pt.setStatus(2);
        pt.setImgUrl("http://192.168.1.189:8080/apps/Samples/thumb/sample03.jpg?1513112208");
        pt.setId("fb351d8d-0c6b-4651-9b35-8aee9b59beff");
        changePersonView(pt);

        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        intent.putExtra("public_id", pt.getId().toString());

        startActivity(intent);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        validateLogin();

        SharedPreferences sharedPer = PreferenceManager.getDefaultSharedPreferences(this);
                //getApplicationContext()
                //.getSharedPreferences(getString(R.string.pref_default_name), Context.MODE_PRIVATE);

        //setting service endpoint address, get value from shared preference.
        service_url = sharedPer.getString(getString(R.string.pref_services_url), getString(R.string.pref_services_url_default));
        TOKEN = sharedPer.getString("token","");
        this.operator_id = getIntent().getStringExtra("com.ericconnect.sitehr.intent_operatorId");

        mBarcodeView = findViewById(R.id.zxing_barcode_holder);


        startDecode(mBarcodeView, barcodeCallback);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.navigation);
        toolbar.setTitle(R.string.title_checkin);
        toolbar.setSubtitle(R.string.app_name);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);


    }

    /**
     * check login and token in shared preference, if not or null goto login activity.
     */
    private void validateLogin() {
        SharedPreferences sharedPer = PreferenceManager.getDefaultSharedPreferences(this);
        String Token = sharedPer.getString("token","");
        Boolean isLogin = sharedPer.getBoolean("isLogin", false);


        if(isNullOrEmpty(Token) || !isLogin){
            Toast.makeText(this, "Login Please.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

        }


    }

    private void startDecode(DecoratedBarcodeView view, BarcodeCallback callback) {

        //get permissions.

        AndPermission.with(this)
                .permission(Permission.CAMERA)
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Toast.makeText(getApplicationContext(), R.string.warn_need_permissions, Toast.LENGTH_SHORT).show();
                    }
                })
                .start();

        // start decode qr continuous.
        view.decodeContinuous(callback);



    }

    @Override
    protected void onResume() {
        super.onResume();

        mBarcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBarcodeView.pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    /**
     *  top right toolbar menu selected function
     * @param menuItem
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem menuItem){

        Toolbar toolbar = findViewById(R.id.toolbar);

        switch(menuItem.getItemId()){

            //when select setting, open setting activity.
            case R.id.navigation_setting:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;

            //change to clock in mode.
            case R.id.navigation_sign_in:

                toolbar.setTitle(R.string.title_checkin);
                operation_type = "ClockIn"; //change operation type to clock in;
                break;

            //change to clock out mode.
            case R.id.navigation_sign_out:

                toolbar.setTitle(R.string.title_checkout);
                operation_type = "ClockOut"; //change operation type to clock out;
                break;

            case R.id.navigation_about:
                Intent intentAbout = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intentAbout);
                //show about
                break;
            case R.id.navigation_logout:

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.pref_isLogin), false);
                editor.commit();
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;

            // default. do nothing.
            default:
                return false;
        }
        return false;

    }



}

