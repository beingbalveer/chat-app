package com.example.android.firebase;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class Splash extends AppCompatActivity {


    FirebaseAuth auth;
    private static final int REQUEST_CODE = 100;

    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        database = FirebaseDatabase.getInstance();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null)
            actionBar.hide();

        Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        Firebase.setAndroidContext(this);

        auth = FirebaseAuth.getInstance();

        boolean b = ActivityCompat.checkSelfPermission(this,READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        if (!b)
            ActivityCompat.requestPermissions(this,new String[]{READ_EXTERNAL_STORAGE},REQUEST_CODE);
        else
        {
            doWork();
        }

    }

    private void doWork() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (auth.getCurrentUser()!=null)
                {
                    startActivity(new Intent(Splash.this,Home.class));
                    finish();
                }
                else
                {
                    startActivity(new Intent(Splash.this,MainActivity.class));
                    finish();
                }

            }
        },100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==REQUEST_CODE)
        {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                doWork();
            }
            else
                Toast.makeText(this,"permission denied",Toast.LENGTH_LONG).show();
                finish();
        }
    }

}
