package com.example.android.darb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.darb.other.SharedPrefs;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {
    private EditText txtid,txtpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Referencing
        final Button btnsignin = (Button) findViewById(R.id.btn_signin);
        TextView btnforgotpass = (TextView) findViewById(R.id.btn_forgot_password);

        // Intent for opening ForgotPassword
        btnforgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(i);
            }
        });

         txtid = (EditText) findViewById(R.id.txt_id);
         txtpass = (EditText) findViewById(R.id.txt_password);
         txtid.setText("");
         txtpass.setText("");
        // Intent for opening Menu
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });

    }

    public void login(View v) {
        //Referencing

        String userName = txtid.getText().toString();
        String passWord = txtpass.getText().toString();

        if (userName.isEmpty() || passWord.isEmpty()) {
            Toast.makeText(this, "Make sure to fill all fields.", Toast.LENGTH_SHORT).show();
        } else {
            new LoginServer(this).execute(userName, passWord);
        }
    }


    public class LoginServer extends AsyncTask<String, Void, String> {
        String name;
        private Activity mActivity;
        private Context context;

        public LoginServer(Context context) {
            this.context = context;
        }

        public LoginServer(final Activity mActivity) {
            this.mActivity = mActivity;
        }

        private  String toHexString(byte[] data) {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < data.length; ++i) {
                String s = Integer.toHexString(data[i] & 0XFF);
                buf.append((s.length() == 1) ? ("0" + s) : s);
            }
            return buf.toString();
        }

        public  String encrypt(String input) {
            byte[] crypted = null;
            String key = "smFrejclvntgHdyu";
            String encryptedString = "";
            try {
                SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, skey);
                crypted = cipher.doFinal(input.getBytes());
                encryptedString = toHexString(Base64.encodeBase64(crypted));

            } catch (Exception e) {
                System.out.println(e.toString());
            }
            return encryptedString;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            String userName = arg0[0];
            String passWord = arg0[1];

            String link;
            String data;
            BufferedReader bufferedReader;
            String result;

            try {
                data = "?username=" + URLEncoder.encode(userName, "UTF-8");
                //data += "&password=" + URLEncoder.encode(encrypt(passWord), "UTF-8");
                data += "&password=" + URLEncoder.encode(passWord, "UTF-8");
                link = "https://darbtest.000webhostapp.com/login.php" + data;
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
                    String query_result1 = jsonObj.getString("username");

                    if (query_result1.contains("FAILURE")) {
                        Toast.makeText(getBaseContext(), "Make sure you entered correct information.", Toast.LENGTH_SHORT).show();
                    } else {
                        name = jsonStr.substring(13, jsonStr.length() - 2);
                        Log.i("Name = ", "[" + name + "]");
                        Intent intent = new Intent(this.mActivity.getBaseContext(), Menu.class);
                        SharedPrefs.save(LoginActivity.this,SharedPrefs.USER_NAME,name);
                        SharedPrefs.save(LoginActivity.this,SharedPrefs.USER_ID,txtid.getText().toString());
                        SharedPrefs.save(LoginActivity.this,SharedPrefs.IS_LOGIN,true);
                        startActivity(intent);
                        finish();
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