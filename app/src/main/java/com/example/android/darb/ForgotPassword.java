package com.example.android.darb;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.darb.other.GMailSender;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ForgotPassword extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Referencing
        final Button btnforgot = (Button) findViewById(R.id.btn_send_password);

        //On click listeners
        //Forgot password
        btnforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgot(view);
            }
        });
    }


    public void forgot(View v) {
        //Referencing
        final EditText txtid = (EditText) findViewById(R.id.txt_forgot);
        String userName = txtid.getText().toString();

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please type in your ID.", Toast.LENGTH_SHORT).show();
        } else {
            new ForgotPasswordServer(this).execute(userName);
        }
    }
    public class ForgotPasswordServer extends AsyncTask<String, Void, String> {

        private Context context;

        public ForgotPasswordServer(Context context) {
            this.context = context;
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {
            String id = arg0[0];
            String link;
            String data;
            BufferedReader bufferedReader;
            String result;

            try {
                data = "?id=" + URLEncoder.encode(id, "UTF-8");
                link = "https://darbtest.000webhostapp.com/Forgotpassword.php" + data;
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
        protected void onPostExecute(String result) {
            String jsonStr = result;
            GMailSender supportTeam = new GMailSender("darb.contact@gmail.com", "bobthebuilder");
            if (jsonStr != null) {
                try {
                    //Log.i("jsonStr = ", "[" + jsonStr + "]");
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String email = jsonObj.getString("email");

                    //Log.i("email = ", "[" + email + "]");
                    //Log.i("jsonObj = ", "[" + jsonObj + "]");
                    //Log.i("password = ", "[" + pass + "]");

                    if (email.contains("FAILURE")) {
                        Toast.makeText(context, "This ID does not exist.", Toast.LENGTH_SHORT).show();
                    } else if (jsonStr.contains("email")) {
                        try {
                            String pass = StringUtils.substringAfter(jsonStr, "password\":\"");
                            pass=pass.substring(0, pass.length() - 2);
                            supportTeam.sendMail("DARB: Retrieve Password Request", "Greetings,\nAs for your forgot password request, your password is \""+pass+"\".\nPlease don't forget to change your password when logged in.", "DARB", email);

                            Toast.makeText(context, "Email has been sent.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("SendMail = ", e.getMessage(), e);
                        }

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
