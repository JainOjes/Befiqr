package com.ojes.womensafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText email_text_login, password_text_login;
    Button login_btn;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we login...");
        dialog.setCancelable(false);

        email_text_login = (TextInputEditText) findViewById(R.id.email_text_login);
        password_text_login = (TextInputEditText) findViewById(R.id.password_text_login);
        login_btn = (Button) findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_text_login.getText().toString();
                String password = password_text_login.getText().toString();

                if (TextUtils.isEmpty(email)){
                    email_text_login.setError("Fill this field");
                    email_text_login.requestFocus();
                }

                else if (TextUtils.isEmpty(password)){
                    password_text_login.setError("Fill this field");
                    password_text_login.requestFocus();
                }

                else {
                    dialog.show();
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                dialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            } else {
                                dialog.hide();
                                Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}