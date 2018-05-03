package com.example.android.darb;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.android.darb.other.GPSTracker;
import com.example.android.darb.other.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    String[] permissions = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSION = 786;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        //Referencing
        final FrameLayout framenewform = (FrameLayout) findViewById(R.id.layout_new_form);
        final FrameLayout framesubmittedforms = (FrameLayout) findViewById(R.id.layout_submitted_forms);
        final FrameLayout frameresetpassword = (FrameLayout) findViewById(R.id.layout_reset_password);
        final FrameLayout framesupport = (FrameLayout) findViewById(R.id.layout_support);
        final FrameLayout framesignout = (FrameLayout) findViewById(R.id.layout_signout);

        TextView name = (TextView) findViewById(R.id.txt_username);
        final String username = SharedPrefs.getString(Menu.this,SharedPrefs.USER_NAME);
        name.setText("Hello,\n" + username);

        // Intent for opening new form
        framenewform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkPermissionREAD_EXTERNAL_STORAGE(Menu.this)) {
                        gps = new GPSTracker(Menu.this);
                        Log.e("TAG","gpd.....__>"+gps.canGetLocation());

                        if(gps.canGetLocation()){
                            Intent i = new Intent(Menu.this, AccidentFormActivity.class);
                            startActivity(i);
                        }
                        else
                        {
                            gps.showSettingsAlert();
                        }

                    }
                    ActivityCompat.requestPermissions((Activity)Menu.this, new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_LOCATION_PERMISSION);
                } else {
                    gps = new GPSTracker(Menu.this);
                    Log.e("TAG","gpd.....__>"+gps.canGetLocation());

                    if(gps.canGetLocation()){
                        Intent i = new Intent(Menu.this, AccidentFormActivity.class);
                        startActivity(i);
                    }
                    else
                    {
                        gps.showSettingsAlert();
                    }
                }


            }
        });

        // Intent for opening submitted form
        framesubmittedforms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Menu.this, SubmittedFormActivity.class);
                startActivity(i);
            }
        });

        // Intent for opening reset password
        frameresetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Menu.this, ResetPassword.class);
                i.putExtra("USERNAME", username);
                startActivity(i);
            }
        });

        // Intent for support email
        framesupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse("mailto: darb.contact@gmail.com"));
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(i);
                }
            }
        });

        // Intent for opening reset password
        framesignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder (Menu.this);
                alertDialog.setMessage ("Are you Sure?");
                alertDialog.setPositiveButton ("Log Out", new DialogInterface.OnClickListener (){
                    @Override
                    public void onClick (DialogInterface dialogInterface, int i){
                        SharedPrefs.save(Menu.this,SharedPrefs.IS_LOGIN,false);
                        Intent intent = new Intent(Menu.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
                alertDialog.setNegativeButton ("Cancel", new DialogInterface.OnClickListener (){
                    @Override
                    public void onClick (DialogInterface dialogInterface, int i){
                        dialogInterface.dismiss ();
                    }
                });

                android.support.v7.app.AlertDialog alert = alertDialog.create ();
                alert.show ();
            }
        });
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }

        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
                gps = new GPSTracker(Menu.this);
                Log.e("TAG","gpd.....__>"+gps.canGetLocation());
                if(gps.canGetLocation()){
                    Intent i = new Intent(Menu.this, AccidentFormActivity.class);
                    startActivity(i);
                }
                else
                {
                    gps.showSettingsAlert();
                }
            }
            return;
        }
    }


}
