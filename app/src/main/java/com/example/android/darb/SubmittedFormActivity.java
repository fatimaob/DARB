package com.example.android.darb;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.darb.model_list.model_form;
import com.example.android.darb.other.Darb;
import com.example.android.darb.other.Share;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by scc pc-3 on 4/5/2018.
 */

public class SubmittedFormActivity extends AppCompatActivity {
    private RecyclerView rv_list;
    private Submited_form submited_form;
    private ArrayList<model_form> array_form_list=new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submited_form_list);

        progressDialog = new ProgressDialog(SubmittedFormActivity.this);
        initview();
        inotviewlistner();

        if(Share.isNetworkAvaliable(SubmittedFormActivity.this)){
            RetrieveFeedTask("https://darbtest.000webhostapp.com/get_acc.php");
        }
        else
        {
            Toast.makeText(SubmittedFormActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void initview() {
        rv_list=findViewById(R.id.rv_list);
    }

    private void inotviewlistner() {

    }
    private void RetrieveFeedTask(String url) {
        JSONObject object = new JSONObject();

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("TAG","responce is this-->"+response.toString());
                try {
                    JSONArray result=response.getJSONArray("Result");
                    array_form_list.clear();
                    for (int i = 0; i < result.length(); i++) {

                        JSONObject jsonObjectName = result.getJSONObject(i);

                        model_form model=new model_form();

                        model.setCash_status(jsonObjectName.getString("cash_status"));
                        model.setUser_id(jsonObjectName.getString("user_id"));
                        model.setCollision_date(jsonObjectName.getString("collision_date"));
                        model.setCollision_time(jsonObjectName.getString("collision_time"));
                        model.setAccident_ID(jsonObjectName.getString("accident_ID"));


                        array_form_list.add(model);

                    }
                    setAdapeter();

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
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(SubmittedFormActivity.this, "error: "+error.toString(), Toast.LENGTH_SHORT).show();
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


    private void setAdapeter() {
        submited_form = new Submited_form(SubmittedFormActivity.this, array_form_list);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SubmittedFormActivity.this);
        rv_list.setLayoutManager(linearLayoutManager);
        rv_list.setItemAnimator(new DefaultItemAnimator());
        rv_list.setAdapter(submited_form);
    }



    private class Submited_form extends RecyclerView.Adapter<Submited_form.MyViewHolder> {
        ArrayList<model_form> array_form_list=new ArrayList<>();
        Context context;

        public Submited_form(Context context, ArrayList<model_form> array_form_list  ) {
            this.array_form_list = array_form_list;
            this.context=context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item_form, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            try {

                holder.txt_date.setText(""+array_form_list.get(position).getCollision_date());
                holder.txt_time.setText(""+array_form_list.get(position).getCollision_time());
                holder.txt_user_id.setText(""+array_form_list.get(position).getAccident_ID());

                if(array_form_list.get(position).getCash_status().equals("0")){
                    holder.img_status.setImageResource(R.drawable.round_g);
                }
                else
                {
                    holder.img_status.setImageResource(R.drawable.round);

                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(array_form_list.get(position).getCash_status().equals("0")){
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setMessage("Are you sure you want to close this case?");
                            alertDialog.setCancelable(true);
                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    casestatus("https://darbtest.000webhostapp.com/case_update.php"+"?cash_status="+1+"&accident_ID="+array_form_list.get(position).getAccident_ID());
                                }
                            });
                            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog alert = alertDialog.create();
                            alert.show();
                        }

                    }
                });


            } catch (Exception e) {
                Log.e("TAG","Exception is- "+e.getMessage());
                e.printStackTrace();
            }
        }
        private void casestatus(String url) {
            JSONObject object = new JSONObject();

            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.e("TAG","responce is this-parties->"+response.toString());
                    if(Share.isNetworkAvaliable(SubmittedFormActivity.this)){
                        RetrieveFeedTask("https://darbtest.000webhostapp.com/get_acc.php");
                    }
                    else
                    {
                        Toast.makeText(SubmittedFormActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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
        public int getItemCount() {
            return array_form_list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView img_status;
            TextView txt_date, txt_time,txt_user_id;


            public MyViewHolder(View itemView) {
                super(itemView);

                img_status = (ImageView) itemView.findViewById(R.id.img_status);
                txt_time = (TextView) itemView.findViewById(R.id.txt_time);
                txt_date = (TextView) itemView.findViewById(R.id.txt_date);
                txt_user_id = (TextView) itemView.findViewById(R.id.txt_user_id);

            }
        }
    }


}
