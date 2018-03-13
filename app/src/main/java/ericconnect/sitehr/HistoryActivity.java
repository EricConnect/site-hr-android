package ericconnect.sitehr;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.session.MediaSession;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = HistoryActivity.class.getName();
    String ENDPOINT = "http://192.168.1.8:5000";
    String TOKEN;

    GridView gridView;
    EditText startEditText;
    EditText endEditText;
    String strFormat;
    ProgressBar historyProgress;
    SimpleDateFormat simpleDateFormat;
    String publicID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridView = findViewById(R.id.gv_history);
        startEditText = findViewById(R.id.Txt_StartDate);
        endEditText = findViewById(R.id.Txt_EndDate);
        historyProgress = findViewById(R.id.progress_history);

        publicID = getIntent().getStringExtra("public_id");

        initStartEndDate(); // set default START DATE EditText and END DATE EditText.

        //set service token from default preference.
        SharedPreferences sharedPer = PreferenceManager.getDefaultSharedPreferences(this);
        TOKEN = sharedPer.getString("token","");







    }

    /**
     * set start date is before 7 days on end date.
     */
    private void initStartEndDate() {
        // Initial EditText set end date is current date and start date is  7days before.
        Calendar mCalendar;
        mCalendar = Calendar.getInstance();
        strFormat = "yyyy-MM-dd";
        simpleDateFormat = new SimpleDateFormat(strFormat, Locale.CHINA);

        startEditText = findViewById(R.id.Txt_StartDate);
        endEditText = findViewById(R.id.Txt_EndDate);

        endEditText.setText(simpleDateFormat.format(mCalendar.getTime())); //set current date as END DATE first.
        mCalendar.add(Calendar.DAY_OF_MONTH,-7);
        startEditText.setText(simpleDateFormat.format(mCalendar.getTime()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void dataBind(GridView gridView, ArrayList<HashMap<String, String>> data){

        SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext()
                , data
                , R.layout.view_item
                , new String[]{"date","status"}
                , new int[]{R.id.tv_title
                , R.id.tv_status}){


            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                View view = super.getView(position,convertView,parent);

                if(position %2 == 1)
                {
                    // Set a background color for regular row/item
                    view.setBackgroundColor(getColor(R.color.colorGridViewBackgroundFirst));
                }
                else
                {
                    // Set the background color for alternate row/item
                    view.setBackgroundColor(getColor(R.color.colorGridViewBackgroundSecond));
                }

                return view;


            }
        };
        //set history view adapter
        gridView.setAdapter(simpleAdapter);
    }


    private void getData(){
        final ArrayList<HashMap<String, String>> list = new ArrayList<>();

        ServiceHelper.getInstance(ENDPOINT, TOKEN)
                .getHistory(publicID,startEditText.getText().toString(),endEditText.getText().toString())
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code()==200 && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray array = jsonObject.getJSONArray("history");

                        for(int i=0; i<array.length(); i++){
                            HashMap<String,String> map = new HashMap<>();
                            map.put("date",array.getJSONArray(i).getString(0).toString());
                            map.put("status", array.getJSONArray(i).getString(1).toString());

                            list.add(map);

                        }


                    }catch (Exception e){
                        Toast.makeText(HistoryActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: " +e.getMessage());

                    }

                    dataBind(gridView, list);

                }else{
                    Toast.makeText(HistoryActivity.this, "Error on Response code:" + response.code(), Toast.LENGTH_SHORT).show();
                }
                historyProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                historyProgress.setVisibility(View.INVISIBLE);


            }
        });


    }


    public void onSearchBtnClick(View view){

        String startDate = startEditText.getText().toString();
        String endDate = endEditText.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date sDate = sdf.parse(startDate);
            Date eDate = sdf.parse(endDate);

            if(eDate.after(sDate)) {
                getData();
                historyProgress.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(HistoryActivity.this, R.string.warn_end_after_start, Toast.LENGTH_SHORT).show();
            }

        }catch(Exception e){
            Toast.makeText(HistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }




    }

    /**
     * start date select
     * @param view start date EditText
     */
    public void onStartDateEditClick(View view){
        Calendar mCalendar = Calendar.getInstance();
        final EditText startEdit = (EditText)view;

        DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                startEdit.setText(simpleDateFormat.format(mCalendar.getTime()));

            }
        },mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    /**
     * end date select
     * @param view end date EditText
     */
    public void onEndDateEditClick(View view){
        Calendar mCalendar = Calendar.getInstance();
        final EditText endEdit = (EditText)view;

        DatePickerDialog endDatePickerDialog = new DatePickerDialog(HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                endEdit.setText(simpleDateFormat.format(mCalendar.getTime()));

            }
        },mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH));

        endDatePickerDialog.show();
    }
}
