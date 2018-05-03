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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by scc pc-3 on 4/5/2018.
 */

public class PartiesFormActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private ProgressDialog progressDialog;
    private EditText text_party_id,text_party_name,text_party_address;
    private JSONArray natinality_json,party_type_json,helth_status_json;
    private Spinner spinner_party_nationality,spinner_birth_year,spinner_party_type,spinner_party_health;
    private String nationality="",birth_year="",gender="Female",party_type="",helth_status="";
    private RadioGroup rg_gender;
    private Button btn_party_add;
    private ImageView img_done,img_back;
    private Boolean is_submit=false;
    private RadioButton rb_female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_parties);

        progressDialog = new ProgressDialog(PartiesFormActivity.this);
        if(Share.isNetworkAvaliable(PartiesFormActivity.this)){
            RetrieveFeedTask("https://darbtest.000webhostapp.com/get_nationality.php");
        }
        else
        {
            Toast.makeText(PartiesFormActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        initview();
        initviewlistner();
    }
    private void initview() {
        text_party_id=findViewById(R.id.text_party_id);
        text_party_id.setText("");
        text_party_name=findViewById(R.id.text_party_name);
        text_party_name.setText("");
        text_party_address=findViewById(R.id.text_party_address);
        text_party_address.setText("");
        spinner_party_nationality=findViewById(R.id.spinner_party_nationality);
        spinner_birth_year=findViewById(R.id.spinner_birth_year);
        rg_gender=findViewById(R.id.rg_gender);
        rb_female=findViewById(R.id.rb_female);
        rb_female.setChecked(true);
        spinner_party_type=findViewById(R.id.spinner_party_type);
        spinner_party_health=findViewById(R.id.spinner_party_health);
        btn_party_add=findViewById(R.id.btn_party_add);
        img_back=findViewById(R.id.img_back);
        img_done=findViewById(R.id.img_done);

    }
    private void initviewlistner() {
        party_id();
        get_birth_year();
        rg_gender.setOnCheckedChangeListener(this);
        btn_party_add.setOnClickListener(this);
        img_done.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    private void get_birth_year() {
        final int pos = 0;
        try{
            final ArrayList<String> years = new ArrayList<String>();
            int thisYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = thisYear; i >= 1900; i--){
                years.add(Integer.toString(i));
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, years);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_birth_year.setAdapter(adpter);

            spinner_birth_year.setSelection(pos);

            spinner_birth_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_birth_year.setSelection(position);
                    birth_year = years.get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }

    private void party_id() {
        text_party_id.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(text_party_id.getText().toString().length()==1){
                    if(text_party_id.getText().toString().equals("0") || text_party_id.getText().toString().equals("1") || text_party_id.getText().toString().equals("2")){
                        Log.e("TAG","driver id --->"+editable);
                    }
                    else
                    {
                        Toast.makeText(PartiesFormActivity.this, "Please startstart with either 0, 1, or 2.", Toast.LENGTH_SHORT).show();
                        text_party_id.setText("");
                    }
                }

            }
        });

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
                    natinality_json=response.getJSONArray("Result");
                    set_natinality();
                    RetrieveFeedTask_party_type("https://darbtest.000webhostapp.com/get_partyType.php");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RetrieveFeedTask_party_type("https://darbtest.000webhostapp.com/get_partyType.php");
                Log.e("TAG","error is this-->"+error.getMessage().toString());
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
    private void RetrieveFeedTask_party_type(String url) {
        JSONObject object = new JSONObject();

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
//                    Log.e("TAG","responce is this-->"+response.toString());
                    party_type_json=response.getJSONArray("Result");
                    set_party_type();
                    RetrieveFeedTask_helth_status("https://darbtest.000webhostapp.com/get_healthStatus.php");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                RetrieveFeedTask_make("https://darbtest.000webhostapp.com/get_carList.php");
                Log.e("TAG","error is this-->"+error.getMessage().toString());

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

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
//                    Log.e("TAG","responce is this-->"+response.toString());
                    helth_status_json=response.getJSONArray("Result");
                    set_helth_status();
//                    RetrieveFeedTask_make("https://darbtest.000webhostapp.com/get_carList.php");
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
//                RetrieveFeedTask_make("https://darbtest.000webhostapp.com/get_carList.php");
                Log.e("TAG","error is this-->"+error.getMessage().toString());
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
            spinner_party_health.setAdapter(adpter);

            spinner_party_health.setSelection(pos);

            spinner_party_health.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_party_health.setSelection(position);
                    helth_status= accident_type_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }


    private void set_party_type() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_type_array=new ArrayList<>();

        try{
            final ArrayList<list> accident_type_array_main=new ArrayList<>();

//            Log.e("TAG","data is this-->"+accident_type_json);
            for (int i = 0; i < party_type_json.length(); i++) {
                JSONObject jsonObjectName = party_type_json.getJSONObject(i);
                accident_type_array.add(jsonObjectName.getString("party"));

                list model=new list();
                model.setId(jsonObjectName.getString("id"));
                model.setName(jsonObjectName.getString("party"));
                accident_type_array_main.add(model);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_type_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_party_type.setAdapter(adpter);

            spinner_party_type.setSelection(pos);

            spinner_party_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_party_type.setSelection(position);
                    party_type= accident_type_array.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }catch(Exception e){
            Log.e("TAG","error is this-->"+e.toString());
        }
    }


    private void set_natinality() {
        final int pos = 0;
        JSONArray data;
        final ArrayList<String> accident_type_array=new ArrayList<>();

        try{
            final ArrayList<list> accident_type_array_main=new ArrayList<>();

//            Log.e("TAG","data is this-->"+accident_type_json);
            for (int i = 0; i < natinality_json.length(); i++) {
                JSONObject jsonObjectName = natinality_json.getJSONObject(i);
                accident_type_array.add(jsonObjectName.getString("nationality"));

                list model=new list();
                model.setId(jsonObjectName.getString("id"));
                model.setName(jsonObjectName.getString("nationality"));
                accident_type_array_main.add(model);
            }

            ArrayAdapter<String> adpter=new ArrayAdapter<String>(this, R.layout.spinner_row_item, accident_type_array);

            adpter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_party_nationality.setAdapter(adpter);

            spinner_party_nationality.setSelection(pos);

            spinner_party_nationality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                    spinner_party_nationality.setSelection(position);
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

    @Override
    public void onClick(View view) {
        if(view==btn_party_add){
            if(text_party_id.getText().toString().equals("")){
                Toast.makeText(this, "Please add national id", Toast.LENGTH_SHORT).show();
            } else if(text_party_id.getText().toString().length()!=10){
                Toast.makeText(this, "Please add exactly 10 digits in national id", Toast.LENGTH_SHORT).show();
            }else if(text_party_name.getText().toString().equals("")){
                Toast.makeText(this, "Please add party name", Toast.LENGTH_SHORT).show();
            }else if(text_party_address.getText().toString().equals("")){
                Toast.makeText(this, "Please add address.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                submitdata("https://darbtest.000webhostapp.com/add_parties.php"+"?national_ID="+text_party_id.getText().toString()
                        +"&name="+text_party_name.getText().toString()+"&party_type="+party_type
                        +"&birth_year="+birth_year+"&gender="+gender
                        +"&address="+text_party_address.getText().toString()+"&health_status="+helth_status
                        +"&nationality="+nationality);
            }
        }
        else if(view==img_back){
            finish();
        }else if(view==img_done){
            if(is_submit){
                Intent i = new Intent(PartiesFormActivity.this,  Menu.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

                Log.e("TAG","responce is this-parties->"+response.toString());

                try {
                    if(response.getString("Status").equals("True")){
                        AlertDialog.Builder b = new AlertDialog.Builder(PartiesFormActivity.this);
                        b.setCancelable(false);
                        b.setTitle(response.getString("message"));
                        b.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                is_submit=true;

                                progressDialog = new ProgressDialog(PartiesFormActivity.this);
                                if(Share.isNetworkAvaliable(PartiesFormActivity.this)){
                                    RetrieveFeedTask("https://darbtest.000webhostapp.com/get_nationality.php");
                                }
                                else
                                {
                                    Toast.makeText(PartiesFormActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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

}
