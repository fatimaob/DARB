package com.example.android.darb;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.darb.model_list.list;
import com.example.android.darb.other.Darb;
import com.example.android.darb.other.Share;
import com.example.android.darb.other.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by scc pc-3 on 4/4/2018.
 */

public class AddDriverFormActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private EditText text_driver_id,text_driver_name,text_driver_address,text_vehicle_model;
    private Spinner spinner_year,spinner_driver_licence,spinner_vehicle_make,spinner_vehicle_year,spinner_vehicle_color,
            spinner_registration_type,spinner_vehicle_country,spinner_vehicle_status,spinner_driver_nationality,spinner_driver_health;
    private String year="",gender="Female",license_type="",vihical_make="",vihical_make_year="",color="",
            reg_type="",country="",status,nationality="",health_status="";
    private RadioButton rb_female,rb_male;
    private RadioGroup rg_gender;
    private ProgressDialog progressDialog;
    private JSONArray license_type_json,make_car_json,color_json,reg_type_json,status_json,nationality_json,helth_status_json;
    private Button btn_driver_add;
    private ImageView img_done,img_back;
    private Boolean is_submit=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_drivers);


        progressDialog = new ProgressDialog(AddDriverFormActivity.this);
        if(Share.isNetworkAvaliable(AddDriverFormActivity.this)){
            RetrieveFeedTask("https://darbtest.000webhostapp.com/get_licenseType.php");
        }
        else
        {
            Toast.makeText(AddDriverFormActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        initview();
        initviewlistner();
    }

    private void initview() {
        img_back=findViewById(R.id.img_back);
        img_done=findViewById(R.id.img_done);

        text_driver_id=findViewById(R.id.text_driver_id);
        text_driver_id.setText("");
        spinner_year=findViewById(R.id.spinner_year);
        rb_female=findViewById(R.id.rb_female);
        rb_female.setChecked(true);
        rb_male=findViewById(R.id.rb_male);
        rg_gender=findViewById(R.id.rg_gender);
        spinner_driver_licence=findViewById(R.id.spinner_driver_licence);
        spinner_vehicle_make=findViewById(R.id.spinner_vehicle_make);
        spinner_vehicle_year=findViewById(R.id.spinner_vehicle_year);
        spinner_vehicle_color=findViewById(R.id.spinner_vehicle_color);
        spinner_registration_type=findViewById(R.id.spinner_registration_type);
        spinner_vehicle_country=findViewById(R.id.spinner_vehicle_country);
        spinner_vehicle_status=findViewById(R.id.spinner_vehicle_status);
        btn_driver_add=findViewById(R.id.btn_driver_add);
        spinner_driver_nationality=findViewById(R.id.spinner_driver_nationality);
        spinner_driver_health=findViewById(R.id.spinner_driver_health);
        text_driver_name=findViewById(R.id.text_driver_name);
        text_driver_name.setText("");
        text_driver_address=findViewById(R.id.text_driver_address);
        text_driver_address.setText("");
        text_vehicle_model=findViewById(R.id.text_vehicle_model);
        text_vehicle_model.setText("");


    }

    private void initviewlistner() {
        driver_id();
        set_year();
        rg_gender.setOnCheckedChangeListener(this);
        set_make_car_year();
        set_country();
        btn_driver_add.setOnClickListener(this);
        img_done.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("currency.json");
            int size = is.available();

            Log.e("TAG","size is this-->"+size);
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    private void set_country() {
        final int pos = 0;
        try {
            final ArrayList<String> country_array = new ArrayList<String>();
            JSONObject obj = new JSONObject(loadJSONFromAsset());

            JSONArray m_jArry = obj.getJSONArray("currency");
            Log.e("TAG"," m_jArry length is- "+ m_jArry.length());

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                country_array.add(jo_inside.getString("country_Name"));

            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, country_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_vehicle_country.setAdapter(adpter);

            spinner_vehicle_country.setSelection(pos);

            spinner_vehicle_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_vehicle_country.setSelection(position);
                    country = country_array.get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }



    private void set_year() {
        final int pos = 0;
        try{
            final ArrayList<String> years = new ArrayList<String>();
            int thisYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = thisYear; i >= 1900; i--){
                years.add(Integer.toString(i));
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, years);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_year.setAdapter(adpter);

            spinner_year.setSelection(pos);

            spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_year.setSelection(position);
                    year = years.get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }

    private void set_make_car_year() {
        final int pos = 0;
        try{
            final ArrayList<String> years = new ArrayList<String>();
            int thisYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = thisYear; i >= 1900; i--){
                years.add(Integer.toString(i));
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, years);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_vehicle_year.setAdapter(adpter);

            spinner_vehicle_year.setSelection(pos);

            spinner_vehicle_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_vehicle_year.setSelection(position);
                    vihical_make_year = years.get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }


    private void driver_id() {
        text_driver_id.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(text_driver_id.getText().toString().length()==1){
                    if(text_driver_id.getText().toString().equals("0") || text_driver_id.getText().toString().equals("1") || text_driver_id.getText().toString().equals("2")){
                        Log.e("TAG","driver id --->"+editable);
                    }
                    else
                    {
                        Toast.makeText(AddDriverFormActivity.this, "Please startstart with either 0, 1, or 2.", Toast.LENGTH_SHORT).show();
                        text_driver_id.setText("");
                    }
                }


            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view==btn_driver_add){
            if(text_driver_id.getText().toString().equals("")){
                Toast.makeText(this, "Please add national id.", Toast.LENGTH_SHORT).show();
            }
            else if(text_driver_id.getText().toString().length()!=10){
                Toast.makeText(this, "Please add exactly 10 digits in national id", Toast.LENGTH_SHORT).show();
            }
            else if(text_driver_name.getText().toString().equals("")){
                Toast.makeText(this, "Please add driver name.", Toast.LENGTH_SHORT).show();
            }
            else if(text_driver_address.getText().toString().equals("")){
                Toast.makeText(this, "Please add address.", Toast.LENGTH_SHORT).show();
            }
            else if(text_vehicle_model.getText().toString().equals("")){
                Toast.makeText(this, "Please add model.", Toast.LENGTH_SHORT).show();
            }

            else
            {

                submitdata("https://darbtest.000webhostapp.com/add_driver.php"+"?national_ID="+text_driver_id.getText().toString()
                        +"&name="+text_driver_name.getText().toString()+"&nationality="+nationality
                        +"&birth_year="+year+"&gender="+gender
                        +"&address="+text_driver_address.getText().toString()+"&licenese_type="+license_type
                        +"&health_status="+health_status+"&vehicle_model="+text_vehicle_model.getText().toString()+"&make="+vihical_make
                        +"&make_year="+vihical_make_year+"&color="+color+"&register_type="+reg_type+"&reg_country="+country
                        +"&status="+status+"&acc_id="+getIntent().getExtras().getString("acc_id"));

            }

        }else if(view==img_back){
            finish();
        }else if(view==img_done){
            if(is_submit){
                Intent i = new Intent(AddDriverFormActivity.this,  PartiesFormActivity.class);
                startActivity(i);
            }
            else
            {
                Toast.makeText(this, "Please submit data", Toast.LENGTH_SHORT).show();
            }
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

                Log.e("TAG","responce is this-->"+response.toString());
                try {
                    if(response.getString("Status").equals("True")){
                        AlertDialog.Builder b = new AlertDialog.Builder(AddDriverFormActivity.this);
                        b.setCancelable(false);
                        b.setTitle(response.getString("message"));
                        b.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                is_submit=true;

                                progressDialog = new ProgressDialog(AddDriverFormActivity.this);
                                if(Share.isNetworkAvaliable(AddDriverFormActivity.this)){
                                    RetrieveFeedTask("https://darbtest.000webhostapp.com/get_licenseType.php");
                                }
                                else
                                {
                                    Toast.makeText(AddDriverFormActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }

                                initview();
                                initviewlistner();
                            }
                        });
                        b.show();
                    }

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
                Log.e("TAG","error---->"+error.toString());
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

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.rb_female:
                gender="Female";
                break;
            case R.id.rb_male:
                gender="Male";
                break;
        }
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
                    license_type_json=response.getJSONArray("Result");
                    set_License_type();
                    RetrieveFeedTask_make("https://darbtest.000webhostapp.com/get_carList.php");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RetrieveFeedTask_make("https://darbtest.000webhostapp.com/get_carList.php");
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
    private void RetrieveFeedTask_make(String url) {
        JSONObject object = new JSONObject();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    make_car_json=response.getJSONArray("Result");
                    set_make_car();
                    RetrieveFeedTask_color("https://darbtest.000webhostapp.com/get_color.php");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RetrieveFeedTask_color("https://darbtest.000webhostapp.com/get_color.php");
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
    private void RetrieveFeedTask_color(String url) {
        JSONObject object = new JSONObject();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    color_json=response.getJSONArray("Result");
                    set_color();
                    RetrieveFeedTask_reg_type("https://darbtest.000webhostapp.com/get_regType.php");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                RetrieveFeedTask_reg_type("https://darbtest.000webhostapp.com/get_regType.php");
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
    private void RetrieveFeedTask_reg_type(String url) {
        JSONObject object = new JSONObject();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    reg_type_json=response.getJSONArray("Result");
                    set_reg_type();
                    RetrieveFeedTask_status("https://darbtest.000webhostapp.com/get_status.php");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RetrieveFeedTask_status("https://darbtest.000webhostapp.com/get_status.php");

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

    private void RetrieveFeedTask_status(String url) {
        JSONObject object = new JSONObject();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    status_json=response.getJSONArray("Result");
                    set_status_type();
                    RetrieveFeedTask_nationality("https://darbtest.000webhostapp.com/get_nationality.php");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                RetrieveFeedTask_nationality("https://darbtest.000webhostapp.com/get_nationality.php");
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
    private void RetrieveFeedTask_nationality(String url) {
        JSONObject object = new JSONObject();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    nationality_json=response.getJSONArray("Result");
                    set_nationality();
                    RetrieveFeedTask_helth_status("https://darbtest.000webhostapp.com/get_healthStatus.php");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RetrieveFeedTask_helth_status("https://darbtest.000webhostapp.com/get_healthStatus.php");
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
    private void RetrieveFeedTask_helth_status(String url) {
        JSONObject object = new JSONObject();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    helth_status_json=response.getJSONArray("Result");
                    set_helth_status();
//                    RetrieveFeedTask_reason("https://darbtest.000webhostapp.com/get_reason.php");
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
                Toast.makeText(AddDriverFormActivity.this, "error: "+error.toString(), Toast.LENGTH_SHORT).show();
//                RetrieveFeedTask_reason("https://darbtest.000webhostapp.com/get_reason.php");
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

    private void set_helth_status() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_type_array=new ArrayList<>();

        try{
            final ArrayList<list> accident_type_array_main=new ArrayList<>();

//            Log.e("TAG","data is this-->"+accident_type_json);
            for (int i = 0; i < helth_status_json.length(); i++) {
                JSONObject jsonObjectName = helth_status_json.getJSONObject(i);
                accident_type_array.add(jsonObjectName.getString("health"));

                list model=new list();
                model.setId(jsonObjectName.getString("id"));
                model.setName(jsonObjectName.getString("health"));
                accident_type_array_main.add(model);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_type_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_driver_health.setAdapter(adpter);

            spinner_driver_health.setSelection(pos);

            spinner_driver_health.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_driver_health.setSelection(position);
                    health_status= accident_type_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }


    private void set_nationality() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_type_array=new ArrayList<>();

        try{
            final ArrayList<list> accident_type_array_main=new ArrayList<>();

//            Log.e("TAG","data is this-->"+accident_type_json);
            for (int i = 0; i < nationality_json.length(); i++) {
                JSONObject jsonObjectName = nationality_json.getJSONObject(i);
                accident_type_array.add(jsonObjectName.getString("nationality"));

                list model=new list();
                model.setId(jsonObjectName.getString("id"));
                model.setName(jsonObjectName.getString("nationality"));
                accident_type_array_main.add(model);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_type_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_driver_nationality.setAdapter(adpter);

            spinner_driver_nationality.setSelection(pos);

            spinner_driver_nationality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_driver_nationality.setSelection(position);
                    nationality= accident_type_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }


    private void set_status_type() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_type_array=new ArrayList<>();

        try{
            final ArrayList<list> accident_type_array_main=new ArrayList<>();

//            Log.e("TAG","data is this-->"+accident_type_json);
            for (int i = 0; i < status_json.length(); i++) {
                JSONObject jsonObjectName = status_json.getJSONObject(i);
                accident_type_array.add(jsonObjectName.getString("status"));

                list model=new list();
                model.setId(jsonObjectName.getString("id"));
                model.setName(jsonObjectName.getString("status"));
                accident_type_array_main.add(model);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_type_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_vehicle_status.setAdapter(adpter);

            spinner_vehicle_status.setSelection(pos);

            spinner_vehicle_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_vehicle_status.setSelection(position);
                    status= accident_type_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }



    private void set_reg_type() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_type_array=new ArrayList<>();

        try{
            final ArrayList<list> accident_type_array_main=new ArrayList<>();

//            Log.e("TAG","data is this-->"+accident_type_json);
            for (int i = 0; i < reg_type_json.length(); i++) {
                JSONObject jsonObjectName = reg_type_json.getJSONObject(i);
                accident_type_array.add(jsonObjectName.getString("reg_type"));

                list model=new list();
                model.setId(jsonObjectName.getString("id"));
                model.setName(jsonObjectName.getString("reg_type"));
                accident_type_array_main.add(model);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_type_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_registration_type.setAdapter(adpter);

            spinner_registration_type.setSelection(pos);

            spinner_registration_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_registration_type.setSelection(position);
                    reg_type= accident_type_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }


    private void set_color() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_type_array=new ArrayList<>();

        try{
            final ArrayList<list> accident_type_array_main=new ArrayList<>();

//            Log.e("TAG","data is this-->"+accident_type_json);
            for (int i = 0; i < color_json.length(); i++) {
                JSONObject jsonObjectName = color_json.getJSONObject(i);
                accident_type_array.add(jsonObjectName.getString("color"));

                list model=new list();
                model.setId(jsonObjectName.getString("id"));
                model.setName(jsonObjectName.getString("color"));
                accident_type_array_main.add(model);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_type_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_vehicle_color.setAdapter(adpter);

            spinner_vehicle_color.setSelection(pos);

            spinner_vehicle_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_vehicle_color.setSelection(position);
                    color= accident_type_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }

    private void set_make_car() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_type_array=new ArrayList<>();

        try{
            final ArrayList<list> accident_type_array_main=new ArrayList<>();

//            Log.e("TAG","data is this-->"+accident_type_json);
            for (int i = 0; i < make_car_json.length(); i++) {
                JSONObject jsonObjectName = make_car_json.getJSONObject(i);
                accident_type_array.add(jsonObjectName.getString("car_name"));

                list model=new list();
                model.setId(jsonObjectName.getString("id"));
                model.setName(jsonObjectName.getString("car_name"));
                accident_type_array_main.add(model);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_type_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_vehicle_make.setAdapter(adpter);

            spinner_vehicle_make.setSelection(pos);

            spinner_vehicle_make.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_vehicle_make.setSelection(position);
                    vihical_make= accident_type_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }


    private void set_License_type() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_type_array=new ArrayList<>();

        try{
            final ArrayList<list> accident_type_array_main=new ArrayList<>();

//            Log.e("TAG","data is this-->"+accident_type_json);
            for (int i = 0; i < license_type_json.length(); i++) {
                JSONObject jsonObjectName = license_type_json.getJSONObject(i);
                accident_type_array.add(jsonObjectName.getString("license"));

                list model=new list();
                model.setId(jsonObjectName.getString("id"));
                model.setName(jsonObjectName.getString("license"));
                accident_type_array_main.add(model);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_type_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_driver_licence.setAdapter(adpter);

            spinner_driver_licence.setSelection(pos);

            spinner_driver_licence.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_driver_licence.setSelection(position);
                    license_type= accident_type_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }


}
