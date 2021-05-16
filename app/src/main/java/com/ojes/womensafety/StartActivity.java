package com.ojes.womensafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.seismic.ShakeDetector;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class StartActivity extends AppCompatActivity implements ShakeDetector.Listener {
    FirebaseAuth auth;
    Button help_btn, extra_help_btn;
    FusedLocationProviderClient fusedLocationProviderClient;
    SmsManager smsManager;
    TextView user_message, phone_number;
    DatabaseReference databaseReference;
    private static final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        help_btn = (Button) findViewById(R.id.help_btn);
        extra_help_btn = (Button) findViewById(R.id.extra_help_btn);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector shakeDetector = new ShakeDetector(this);
        shakeDetector.start(sensorManager);
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        PowerManager.WakeLock lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SensorRead:");
        lock.acquire();
        user_message = (TextView) findViewById(R.id.message);
        phone_number = (TextView) findViewById(R.id.phone);
        smsManager = SmsManager.getDefault();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        help_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        extra_help_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action, keycode;

        action = event.getAction();
        keycode = event.getKeyCode();

        switch (keycode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (KeyEvent.ACTION_UP == action){
                    getLocation();
                }
                break;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (KeyEvent.ACTION_DOWN == action){
                    getLocation();
                }
        }
        return super.dispatchKeyEvent(event);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        String message = null;
                        try {
                            Geocoder geocoder = new Geocoder(StartActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            String user_address = "Area: " + addresses.get(0).getAddressLine(0);
                            double latitude = addresses.get(0).getLatitude();
                            double longitude = addresses.get(0).getLongitude();
                            message = "Help me! I am in danger. My location is: " + "\n\n" + user_address + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            user_message.setText(message);
                            sendSMS(message);
                        }
                    } else {
                        sendSMS("Help me! I am in danger");
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void sendSMS(String message) {
        int permissionCheck = ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.SEND_SMS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            databaseReference.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String number_1 = snapshot.child("number_1").getValue().toString();
                    String number_2 = snapshot.child("number_2").getValue().toString();
                    String number_3 = snapshot.child("number_3").getValue().toString();
                    smsManager.sendTextMessage(number_1, null, message, null, null);
                    smsManager.sendTextMessage(number_2, null, message, null, null);
                    smsManager.sendTextMessage(number_3, null, message, null, null);
                    Toast.makeText(StartActivity.this, "An SMS has been sent to the phone numbers", Toast.LENGTH_SHORT).show();
                    phone_number.setText(number_1);
                    makePhoneCall(number_1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }
    }

    private void makePhoneCall(String number) {
        if (ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall(phone_number.getText().toString());
            }
        }

        if (requestCode == 0){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sendSMS(user_message.getText().toString());
            }
        }

        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logout){
            auth.signOut();
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return true;
    }

    @Override
    public void hearShake() {
        if (auth.getCurrentUser() != null){
            getLocation();
        }
    }
}