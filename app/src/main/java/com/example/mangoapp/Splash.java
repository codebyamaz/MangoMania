package com.example.mangoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mangoapp.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.location.LocationRequest;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Splash extends AppCompatActivity {

    RelativeLayout btnNext;
    FusedLocationProviderClient fusedLocationProviderClient;
    String Latitude = "", Longitude = "0", Country = "0", Locality = "0", Address = "0";

    private LocationRequest locationRequest;
    private static final int REQUEST_CHECK_SETTINGS = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //button to change activity;
        btnNext = findViewById(R.id.btn_splash_to_next);

        GetUserLocation();
        GoToNextActivity();
    }


    private void GoToNextActivity() {

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EnableLocation();

                FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();

                SharedPreferences prefs = getSharedPreferences("LoginInfo", MODE_PRIVATE);
                String loginInfo = prefs.getString("login", "");//"No name defined" is the default value.
                if (loginInfo.equals("true")) {
                    UploadUserLocation();
                    startActivity(new Intent(Splash.this, MainActivity.class));
                } else {
                    Intent intent = new Intent(Splash.this, SignUp.class);
                    startActivity(intent);
                }

                Toast.makeText(Splash.this, "Hello Finalllly", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void UploadUserLocation() {

        SharedPreferences prefs = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String phone = prefs.getString("Phone", "");

        DatabaseReference LocationRef = FirebaseDatabase.getInstance().getReference("Users Location/" + phone);
        LocationRef.child("Latitude").setValue(Latitude);
        LocationRef.child("Longitude").setValue(Longitude);
        LocationRef.child("Country").setValue(Country);
        LocationRef.child("Locality").setValue(Locality);
        LocationRef.child("Address").setValue(Address);

        Toast.makeText(this, "upload data " + Latitude, Toast.LENGTH_SHORT).show();
    }

    private void GetUserLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Splash.this);
        if (ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When Permission Granted
            getLocation();
        } else {
            //When Permission Denied
            ActivityCompat.requestPermissions(Splash.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }

    }

    private void getLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                Location location = task.getResult();
                if (location != null) {

                    try {
                        Geocoder geocoder = new Geocoder(Splash.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );

                        Latitude = String.valueOf(addresses.get(0).getLatitude());
                        Longitude = String.valueOf(addresses.get(0).getLongitude());
                        Country = addresses.get(0).getCountryName();
                        Locality = addresses.get(0).getLocality();
                        Address = addresses.get(0).getAddressLine(0);
                        Toast.makeText(Splash.this, "location " + Latitude, Toast.LENGTH_SHORT).show();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void EnableLocation() {


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(Splash.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(Splash.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Toast.makeText(this, "GPS is tured on", Toast.LENGTH_SHORT).show();
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "GPS required to be tured on", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}