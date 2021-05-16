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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText email_text, password_text, name_text, number_1_text, number_2_text, number_3_text, name_1_text, name_2_text, name_3_text;
    Button register_activity_btn;
    FirebaseAuth auth;
    DatabaseReference database;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("Users");

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we create a new account for you...");
        dialog.setCancelable(false);

        email_text = (TextInputEditText) findViewById(R.id.email_text);
        password_text = (TextInputEditText) findViewById(R.id.password_text);
        name_text = (TextInputEditText) findViewById(R.id.name_text);
        number_1_text = (TextInputEditText) findViewById(R.id.number_1_text);
        number_2_text = (TextInputEditText) findViewById(R.id.number_2_text);
        number_3_text = (TextInputEditText) findViewById(R.id.number_3_text);
        name_1_text = (TextInputEditText) findViewById(R.id.name_1_text);
        name_2_text = (TextInputEditText) findViewById(R.id.name_2_text);
        name_3_text = (TextInputEditText) findViewById(R.id.name_3_text);
        register_activity_btn = (Button) findViewById(R.id.register_activity_btn);

        register_activity_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = name_text.getText().toString();
                String number_1 = number_1_text.getText().toString();
                String number_2 = number_2_text.getText().toString();
                String number_3 = number_3_text.getText().toString();
                String name_1 = name_1_text.getText().toString();
                String name_2 = name_2_text.getText().toString();
                String name_3 = name_3_text.getText().toString();
                String email = email_text.getText().toString();
                String password = password_text.getText().toString();

                if (TextUtils.isEmpty(email)){
                    email_text.setError("Fill this field");
                    email_text.requestFocus();
                }

                else if (TextUtils.isEmpty(password)){
                    password_text.setError("Fill this field");
                    password_text.requestFocus();
                }

                else if (TextUtils.isEmpty(name)){
                    name_text.setError("Fill this field");
                    name_text.requestFocus();
                }

                else if (TextUtils.isEmpty(number_1)){
                    number_1_text.setError("Fill this field");
                    number_1_text.requestFocus();
                }

                else if (TextUtils.isEmpty(name_1)){
                    name_1_text.setError("Fill this field");
                    name_1_text.requestFocus();
                }

                else if (TextUtils.isEmpty(number_2)){
                    number_2_text.setError("Fill this field");
                    number_2_text.requestFocus();
                }

                else if (TextUtils.isEmpty(name_2)){
                    name_2_text.setError("Fill this field");
                    name_2_text.requestFocus();
                }

                else if (TextUtils.isEmpty(number_3)){
                    number_3_text.setError("Fill this field");
                    number_3_text.requestFocus();
                }

                else if (TextUtils.isEmpty(name_3)){
                    name_3_text.setError("Fill this field");
                    name_3_text.requestFocus();
                }

                else if (!number_1.startsWith("+") || !number_2.startsWith("+") || !number_3.startsWith("+")){
                    Toast.makeText(RegisterActivity.this, "Mention the country code in the phone numbers", Toast.LENGTH_SHORT).show();
                }
                
                else if (number_1.length() != 13 || number_2.length() != 13 || number_3.length() != 13){
                    Toast.makeText(RegisterActivity.this, "The phone numbers should contain the number with the country code", Toast.LENGTH_SHORT).show();
                }

                else {
                    User user = new User();
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setName(name);
                    user.setNumber_1(number_1);
                    user.setNumber_2(number_2);
                    user.setNumber_3(number_3);
                    user.setName_1(name_1);
                    user.setName_2(name_2);
                    user.setName_3(name_3);

                    dialog.show();

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                database.child(auth.getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();
                                        startActivity(new Intent(RegisterActivity.this, StartActivity.class));
                                        finishAffinity();
                                    }
                                });
                            } else {
                                dialog.hide();
                                Toast.makeText(RegisterActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}