package com.example.android.darb;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.android.darb.other.SharedPrefs;

public class Splash_screen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (SharedPrefs.getBoolean(Splash_screen.this,SharedPrefs.IS_LOGIN)) {
                    Intent intent=new Intent(Splash_screen.this, Menu.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent loginIntent = new Intent(Splash_screen.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }

            }
        }, 3000);
    }
}
