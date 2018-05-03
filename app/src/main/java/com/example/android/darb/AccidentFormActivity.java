package com.example.android.darb;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.darb.R;
import com.example.android.darb.model_list.list;
import com.example.android.darb.other.Darb;
import com.example.android.darb.other.GPSTracker;
import com.example.android.darb.other.Share;
import com.example.android.darb.other.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Fatima on 12-Mar-18.
 */

public class AccidentFormActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private LinearLayout ll_collision_date,ll_collision_time,ll_reach_time;
    private TextView text_collision_date,text_collision_time,text_reach_time,text_lat,text_long;
    private TextView text_street,text_neighborhood,text_district;
    private int mYear, mMonth, mDay;
    private String date = "",collision_time="",accident_type="",accident_reason="",accident_severity="";
    private String accident_minor_injury="",accident_major_injury="",accident_death="",light="Dark",land_surface="Dry",weather="";
    GPSTracker gps;
    private RadioButton rb_light_clear,rb_light_dark,rb_land_dry,rb_land_wet;
    private Spinner spinner_type,spinner_reason,spinner_severity,spinner_minor_injury,spinner_major_injury,spinner_death,spinner_weather;
    private JSONArray accident_type_json,accident_reason_json,accident_severity_json;
    private ProgressDialog progressDialog;
    private Button btn_accident_add;
    private RadioGroup rg_land_surface,rg_light;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_accident);

        gps = new GPSTracker(AccidentFormActivity.this);
        progressDialog = new ProgressDialog(AccidentFormActivity.this);
        if(Share.isNetworkAvaliable(AccidentFormActivity.this)){
            RetrieveFeedTask("https://darbtest.000webhostapp.com/AccType.php");
        }
        else
        {
            Toast.makeText(AccidentFormActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


        initview();
        initviewlistner();

    }

    private void initviewlistner() {
        ll_collision_date.setOnClickListener(this);
        ll_collision_time.setOnClickListener(this);
        ll_reach_time.setOnClickListener(this);
        btn_accident_add.setOnClickListener(this);
        rg_light.setOnCheckedChangeListener(this);
        rg_land_surface.setOnCheckedChangeListener(this);
        img_back.setOnClickListener(this);
    }

    public void initview() {
        img_back=findViewById(R.id.img_back);

        ll_collision_date=findViewById(R.id.ll_collision_date);
        ll_collision_time=findViewById(R.id.ll_collision_time);
        ll_reach_time=findViewById(R.id.ll_reach_time);

        text_collision_date=findViewById(R.id.text_collision_date);
        text_collision_time=findViewById(R.id.text_collision_time);
        text_reach_time=findViewById(R.id.text_reach_time);
        text_lat=findViewById(R.id.text_lat);
        text_long=findViewById(R.id.text_long);
        text_street=findViewById(R.id.text_street);
        text_neighborhood=findViewById(R.id.text_neighborhood);
        text_district=findViewById(R.id.text_district);

        rb_light_clear=findViewById(R.id.rb_light_clear);
        rb_light_dark=findViewById(R.id.rb_light_dark);

        rb_land_dry=findViewById(R.id.rb_land_dry);
        rb_land_wet=findViewById(R.id.rb_land_wet);

        spinner_type=findViewById(R.id.spinner_type);
        spinner_reason=findViewById(R.id.spinner_reason);
        spinner_severity=findViewById(R.id.spinner_severity);
        spinner_minor_injury=findViewById(R.id.spinner_minor_injury);
        spinner_major_injury=findViewById(R.id.spinner_major_injury);
        spinner_death=findViewById(R.id.spinner_death);

        btn_accident_add=findViewById(R.id.btn_accident_add);
        rg_land_surface=findViewById(R.id.rg_land_surface);
        rg_light=findViewById(R.id.rg_light);
        spinner_weather=findViewById(R.id.spinner_weather);


        setdata();
        set_minor_injury();
        set_major_injury();
        set_death();
        set_weather();
    }

    private void set_weather() {
        final int pos = 0;
        try{
            final ArrayList<String> years = new ArrayList<String>();

            years.add("Clear");
            years.add("Cloudy");
            years.add("Foggy");
            years.add("Dusty");
            years.add("Snow");

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, years);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_weather.setAdapter(adpter);

            spinner_weather.setSelection(pos);

            spinner_weather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_weather.setSelection(position);
                    weather = years.get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }

    private void setdata() {
         Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        date = String.format("%02d/%02d/%4d", mDay, mMonth + 1, mYear);
        text_collision_date.setText(date);

        c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss a");
        String formattedDate = df.format(c.getTime());
        text_collision_time.setText(formattedDate);
        text_reach_time.setText(formattedDate);

        if(gps.canGetLocation()){
            text_lat.setText(""+gps.getLatitude());
            text_long.setText(""+gps.getLongitude());
            Log.e("TAG","gps location"+gps.getLocation());


            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                String neighborhood="";
                addresses = geocoder.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

//                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String district = addresses.get(0).getSubAdminArea();
                neighborhood = addresses.get(0).getThoroughfare();
                String street = addresses.get(0).getSubLocality();

                Log.e("TAG","address--->"+street);
                text_street.setText(street);
                text_district.setText(state);
                text_neighborhood.setText(city);

            } catch (IOException e) {
                Log.e("TAG","error--->"+e);
                e.printStackTrace();
            }
        }
        else
        {
            gps.showSettingsAlert();
        }

    }

    private void set_accident_type() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_type_array=new ArrayList<>();

        try{
            final ArrayList<list> accident_type_array_main=new ArrayList<>();

//            Log.e("TAG","data is this-->"+accident_type_json);
            for (int i = 0; i < accident_type_json.length(); i++) {
                JSONObject jsonObjectName = accident_type_json.getJSONObject(i);
                accident_type_array.add(jsonObjectName.getString("name"));

                list model=new list();
                model.setId(jsonObjectName.getString("id"));
                model.setName(jsonObjectName.getString("name"));
                accident_type_array_main.add(model);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_type_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_type.setAdapter(adpter);

            spinner_type.setSelection(pos);

            spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                        spinner_type.setSelection(position);
                        accident_type = accident_type_array.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }

    private void set_accident_reason() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_reason_array=new ArrayList<>();

        try{
            final ArrayList<list> accident_reason_array_main=new ArrayList<>();

//            Log.e("TAG","data is this--->"+accident_reason_json);
            for (int i = 0; i < accident_reason_json.length(); i++) {
                JSONObject jsonObjectName = accident_reason_json.getJSONObject(i);
                accident_reason_array.add(jsonObjectName.getString("reason"));

                list model=new list();
                model.setId(jsonObjectName.getString("id"));
                model.setName(jsonObjectName.getString("reason"));
                accident_reason_array_main.add(model);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_reason_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_reason.setAdapter(adpter);

            spinner_reason.setSelection(pos);

            spinner_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                        spinner_reason.setSelection(position);
                        accident_reason = accident_reason_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }

    private void set_accident_severity() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_severity_array=new ArrayList<>();

        try{
            final ArrayList<list> accident_severity_array_main=new ArrayList<>();

//            Log.e("TAG","data is this-->"+accident_type_json);
            for (int i = 0; i < accident_severity_json.length(); i++) {
                JSONObject jsonObjectName = accident_severity_json.getJSONObject(i);
                accident_severity_array.add(jsonObjectName.getString("severity"));

                list model=new list();
                model.setId(jsonObjectName.getString("id"));
                model.setName(jsonObjectName.getString("severity"));
                accident_severity_array_main.add(model);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_severity_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_severity.setAdapter(adpter);

            spinner_severity.setSelection(pos);

            spinner_severity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                        spinner_severity.setSelection(position);
                        accident_severity = accident_severity_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }

    private void set_minor_injury() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_minor_injury_array=new ArrayList<>();

        try{

            for (int i = 0; i <=100 ; i++) {
                accident_minor_injury_array.add(""+i);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_minor_injury_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_minor_injury.setAdapter(adpter);

            spinner_minor_injury.setSelection(pos);

            spinner_minor_injury.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                         spinner_minor_injury.setSelection(position);
                         accident_minor_injury = accident_minor_injury_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }

    private void set_major_injury() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_major_injury_array=new ArrayList<>();

        try{

            for (int i = 0; i <=100 ; i++) {
                accident_major_injury_array.add(""+i);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_major_injury_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_major_injury.setAdapter(adpter);

            spinner_major_injury.setSelection(pos);

            spinner_major_injury.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_major_injury.setSelection(position);
                    accident_major_injury = accident_major_injury_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }

    private void set_death() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_death_array=new ArrayList<>();

        try{

            for (int i = 0; i <=100 ; i++) {
                accident_death_array.add(""+i);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_death_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_death.setAdapter(adpter);

            spinner_death.setSelection(pos);

            spinner_death.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_death.setSelection(position);
                    accident_death = accident_death_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }



    @Override
    public void onClick(View view) {
        if(view==ll_collision_date){
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AccidentFormActivity.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            date = String.format("%02d/%02d/%4d", dayOfMonth, monthOfYear + 1, year);

                            text_collision_date.setText(date);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker();
            datePickerDialog.show();

        }
        else if(view==ll_collision_time){
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            final int secound = c.get(Calendar.SECOND);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute ) {

                    Calendar calNow = Calendar.getInstance();
                    Calendar calSet = Calendar.getInstance();
                    calSet.set(Calendar.HOUR_OF_DAY,selectedHour);
                    calSet.set(Calendar.MINUTE,selectedMinute);

                    if(calSet.compareTo(calNow) <= 0){
                        calSet.add(Calendar.DATE, 1);
                    }
                    String state ;
                    if(selectedHour > 12){
                        selectedHour -=12;
                        state = "PM";
                    }

                    if (selectedHour == 0) {

                        selectedHour += 12;

                        state = "AM";
                    }
                    else if (selectedHour == 12) {

                        state = "PM";

                    }
                    else if (selectedHour > 12) {
                        selectedHour -= 12;
                        state = "PM";

                    }
                    else {
                        state = "AM";
                    }


                   String time = selectedHour + ":" + selectedMinute + ":" + secound + " "+state;
                    text_collision_time.setText(time);
                }
            }, hour, minute,false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        }
        else if(view==ll_reach_time){
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            final int secound = c.get(Calendar.SECOND);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute ) {

                    Calendar calNow = Calendar.getInstance();
                    Calendar calSet = Calendar.getInstance();
                    calSet.set(Calendar.HOUR_OF_DAY,selectedHour);
                    calSet.set(Calendar.MINUTE,selectedMinute);

                    if(calSet.compareTo(calNow) <= 0){
                        calSet.add(Calendar.DATE, 1);
                    }
                    String state ;
                    if(selectedHour > 12){
                        selectedHour -=12;
                        state = "PM";
                    }

                    if (selectedHour == 0) {

                        selectedHour += 12;

                        state = "AM";
                    }
                    else if (selectedHour == 12) {

                        state = "PM";

                    }
                    else if (selectedHour > 12) {
                        selectedHour -= 12;
                        state = "PM";

                    }
                    else {
                        state = "AM";
                    }


                    String time = selectedHour + ":" + selectedMinute + ":" + secound + " "+state;
                    text_reach_time.setText(time);
                }
            }, hour, minute,false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        }
        else if(view==btn_accident_add){

            submitdata("https://darbtest.000webhostapp.com/add_accident.php"+"?collision_date="+text_collision_date.getText().toString()
                         +"&collision_time="+text_collision_time.getText().toString()+"&reach_time="+text_reach_time.getText().toString()
                         +"&x_coordinate="+text_lat.getText().toString()+"&y_coordinate="+text_long.getText().toString()
                         +"&street="+text_street.getText().toString()+"&neighborhood="+text_neighborhood.getText().toString()
                         +"&district="+text_district.getText().toString()+"&light="+light+"&weather="+weather
                         +"&land_surface="+land_surface+"&type="+accident_type+"&reasons="+accident_reason+"&severity="+accident_severity
                         +"&minor_injury="+accident_minor_injury+"&major_injury="+accident_major_injury+"&deaths="+accident_death
                         +"&user_id="+ SharedPrefs.getString(AccidentFormActivity.this,SharedPrefs.USER_ID));

        }
        else if(view==img_back){
            finish();
        }
    }
    private void submitdata(String url) {
        JSONObject object = new JSONObject();

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

//                Log.e("TAG","responce is this-->"+response.toString());
                try {
//                    Log.e("TAG","responce acc id is this-->"+response.getString("acc_id"));
                    Intent i = new Intent(AccidentFormActivity.this,  AddDriverFormActivity.class);
                    i.putExtra("acc_id",response.getString("acc_id"));
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","error---->"+error.getMessage().toString());
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }
        };
        Darb.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    private void RetrieveFeedTask(String url) {
        JSONObject object = new JSONObject();

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
//                    Log.e("TAG","responce is this-->"+response.toString());
                    accident_type_json=response.getJSONArray("Result");
                    set_accident_type();
                    RetrieveFeedTask_reason("https://darbtest.000webhostapp.com/get_reason.php");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RetrieveFeedTask_reason("https://darbtest.000webhostapp.com/get_reason.php");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }
        };
        Darb.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void RetrieveFeedTask_reason(String url) {
        JSONObject object = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    accident_reason_json=response.getJSONArray("Result");
//                    Log.e("TAG","accident_reason_json is this-->"+accident_reason_json.toString());
                    set_accident_reason();
                    RetrieveFeedTask_severity("https://darbtest.000webhostapp.com/get_severity.php");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RetrieveFeedTask_severity("https://darbtest.000webhostapp.com/get_severity.php");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }
        };
        Darb.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    private void RetrieveFeedTask_severity(String url) {
        JSONObject object = new JSONObject();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    accident_severity_json=response.getJSONArray("Result");
//                    Log.e("TAG","accident_severity_json is this-->"+accident_severity_json.toString());
                    set_accident_severity();
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(AccidentFormActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }
        };
        Darb.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if(radioGroup==rg_light){
            switch (i){
                case R.id.rb_light_clear:
                    light="Clear";
                    break;
                case R.id.rb_light_dark:
                    light="Dark";
                    break;
            }
        }else if(radioGroup==rg_land_surface){
            switch (i){
                case R.id.rb_land_dry:
                    land_surface="Dry";
                    break;
                case R.id.rb_land_wet:
                    land_surface="Wet";
                    break;
            }
        }

    }
}

