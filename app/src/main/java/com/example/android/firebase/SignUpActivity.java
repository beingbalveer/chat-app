package com.example.android.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    TextView email_text, passowrd_text,name_text;
    Button signup;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FrameLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        layout = (FrameLayout)findViewById(R.id.signup_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        name_text = (TextView) findViewById(R.id.name);
        email_text = (TextView) findViewById(R.id.email);
        passowrd_text = (TextView) findViewById(R.id.password);
        signup = (Button) findViewById(R.id.sign_up_button);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = email_text.getText().toString().trim();
                String password = passowrd_text.getText().toString().trim();
                final String name = name_text.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Snackbar.make(layout,"name cannot be empty",Snackbar.LENGTH_LONG).show();
                    return;
                }


                if (TextUtils.isEmpty(email)) {
                    Snackbar.make(layout,"email cannot be empty",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!email.contains("@")) {
                    Snackbar.make(layout,"invalid email",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Snackbar.make(layout,"password cannot be empty",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (password.length() < 6) {
                    Snackbar.make(layout,"password must be 6 character long",Snackbar.LENGTH_LONG).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                            Snackbar.make(layout,"Authentication failed." + task.getException(),Snackbar.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "account created", Toast.LENGTH_SHORT).show();
                            User user = new User(email,name);
                            String new_email = email;
                            new_email = new_email.replace('.','_');
                            reference.child(new_email).setValue(user);
                            Intent intent = new Intent(SignUpActivity.this,Home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                    }
                });

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
