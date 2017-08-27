package com.example.android.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class LoginWithPhone extends AppCompatActivity {

    EditText phone_text;
    Button confirm, back;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_phone);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        phone_text = (EditText) findViewById(R.id.phone);
        confirm = (Button) findViewById(R.id.bt_confirm);
        back = (Button) findViewById(R.id.back_button);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = phone_text.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginWithPhone.this, "enter phone number", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
