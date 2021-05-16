package com.ojes.womensafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HelpActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    ProgressDialog dialog;
    TextView number_1, number_2, number_3;
    Button call_1, call_2, call_3;
    private static final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we load the data...");
        dialog.setCancelable(false);
        dialog.show();
        auth = FirebaseAuth.getInstance();
        number_1 = (TextView) findViewById(R.id.number_1);
        number_2 = (TextView) findViewById(R.id.number_2);
        number_3 = (TextView) findViewById(R.id.number_3);
        call_1 = (Button) findViewById(R.id.call_1);
        call_2 = (Button) findViewById(R.id.call_2);
        call_3 = (Button) findViewById(R.id.call_3);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String number_1_text = snapshot.child("number_1").getValue().toString();
                String number_2_text = snapshot.child("number_2").getValue().toString();
                String number_3_text = snapshot.child("number_3").getValue().toString();
                String name_1 = snapshot.child("name_1").getValue().toString();
                String name_2 = snapshot.child("name_2").getValue().toString();
                String name_3 = snapshot.child("name_3").getValue().toString();
                number_1.setText(name_1);
                number_2.setText(name_2);
                number_3.setText(name_3);
                dialog.dismiss();

                call_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number = number_1_text;
                        if (ContextCompat.checkSelfPermission(HelpActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(HelpActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                        } else {
                            String dial = "tel:" + number;
                            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                        }
                    }
                });

                call_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number = number_2_text;
                        if (ContextCompat.checkSelfPermission(HelpActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(HelpActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                        } else {
                            String dial = "tel:" + number;
                            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                        }
                    }
                });
                call_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number = number_3_text;
                        if (ContextCompat.checkSelfPermission(HelpActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(HelpActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                        } else {
                            String dial = "tel:" + number;
                            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}