package com.example.android.darb;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ResetPassword extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //Referencing
        final Button btnreset = (Button) findViewById(R.id.btn_send_password);

        btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword(view);
            }
        });

    }

    public void resetPassword(View v) {

        //Referencing
        final EditText txtcurrentpass = (EditText) findViewById(R.id.txt_current_pass);
        final EditText txtnewpass = (EditText) findViewById(R.id.txt_forgot);
        final EditText txtconfirmpass = (EditText) findViewById(R.id.txt_new_pass_con);
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";

        String currentPass = txtcurrentpass.getText().toString();
        String newPass = txtnewpass.getText().toString();
        String newPassCon = txtconfirmpass.getText().toString();
        final String id = getIntent().getStringExtra("USERNAME");

        if (currentPass.isEmpty() || newPass.isEmpty() || newPassCon.isEmpty()) {
            Toast.makeText(this, "Make sure to fill all fields.", Toast.LENGTH_SHORT).show();
        } else if (!newPass.matches(pattern)) {
            Toast.makeText(this, "Make sure the new password satisfies password requirements.", Toast.LENGTH_SHORT).show();
        } else if (newPass.matches(newPassCon)) {
            new ResetPasswordServer(this).execute(currentPass, newPass, id);
        } else {
            Toast.makeText(this, "Make sure new password and its confirmation match.", Toast.LENGTH_SHORT).show();
        }
    }

    public class ResetPasswordServer extends AsyncTask<String, Void, String> {
        private Context context;

        public ResetPasswordServer(Context context) {
            this.context = context;
        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            String currentPass = arg0[0];
            String newPass = arg0[1];
            String id = arg0[2];

            String link;
            String data;
            BufferedReader bufferedReader;
            String result;

            try {
                data = "?currentpass=" + URLEncoder.encode(currentPass, "UTF-8");
                data += "&newpass=" + URLEncoder.encode(newPass, "UTF-8");
                data += "&id=" + URLEncoder.encode(id, "UTF-8");

                link = "https://darbtest.000webhostapp.com/resetpassword.php" + data;
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                result = bufferedReader.readLine();
                return result;
            } catch (Exception e) {
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            if (jsonStr != null) {
                try {
                    Log.i("jsonStr = ", "[" + jsonStr + "]");

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String query_result = jsonObj.getString("query_result");

                    if (query_result.equals("SUCCESS")) {
                        Toast.makeText(context, "Password changed successfully.", Toast.LENGTH_SHORT).show();
                    } else if (query_result.equals("FAILURE")) {
                        Toast.makeText(context, "Your current password is incorrect.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Couldn't connect to remote database.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Couldn't get any JSON data.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}