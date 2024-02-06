package com.example.mangoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.mangoapp.address.address_adapter;
import com.example.mangoapp.address.address_model_class;
import com.example.mangoapp.cart.cart_Adapter;
import com.example.mangoapp.cart.cart_model_class;
import com.example.mangoapp.categorylist.category_modelClass;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Address_detail extends AppCompatActivity {

    EditText Name, Phone, Address;
    CardView BtnOrder;
    ProgressBar ProgressBar;
    DatabaseReference cartRef;
    DatabaseReference OrdersRef;


    address_adapter address_adapter;
    ArrayList<address_model_class> address_list;

    //Location;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;
    String Latitude, Longitude;

    String CreatedDate, CurrentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_detail);

        Name = findViewById(R.id.person_name);
        Phone = findViewById(R.id.person_phone);
        Address = findViewById(R.id.person_address);
        BtnOrder = findViewById(R.id.btn_upload_order);
        ProgressBar = findViewById(R.id.address_progressBar);
        ProgressBar.setVisibility(View.INVISIBLE);


        GetDateAndTime();
        GetUserLocation();
        CartData();
        BtnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressBar.setVisibility(View.VISIBLE);
                UploadOrdersData();
            }
        });


    }

    private void GetUserLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Address_detail.this);
        if (ActivityCompat.checkSelfPermission(Address_detail.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When Permission Granted
            getLocation();
        } else {
            //When Permission Denied
            ActivityCompat.requestPermissions(Address_detail.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

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
                        Geocoder geocoder = new Geocoder(Address_detail.this, Locale.getDefault());
                        List<android.location.Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        Latitude = String.valueOf(addresses.get(0).getLatitude());

                        Longitude = String.valueOf(addresses.get(0).getLongitude());


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void CartData() {
        getLocation();

        SharedPreferences prefs = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String phone = prefs.getString("Phone", "");
        cartRef = FirebaseDatabase.getInstance().getReference("cart/" + phone);

        address_list = new ArrayList<>();
        address_adapter = new address_adapter(Address_detail.this, address_list);

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                SharedPreferences prefs = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
                String phone = prefs.getString("Phone", "");

                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OrdersRef = FirebaseDatabase.getInstance().getReference("orders/" + phone + "/products/" + i);
                    i++;
                    address_model_class address_model_class = dataSnapshot.getValue(address_model_class.class);
                    OrdersRef.child("image").setValue(address_model_class.getImage());
                    OrdersRef.child("name").setValue(address_model_class.getName());
                    OrdersRef.child("price").setValue(address_model_class.getPrice());
                    OrdersRef.child("quantity").setValue(address_model_class.getQuantity());
                    OrdersRef.child("key").setValue(address_model_class.getKey());
                    OrdersRef.child("single_price").setValue(address_model_class.getSingle_price());
                    OrdersRef.child("createdAt").setValue(CreatedDate);
                    OrdersRef.child("Date").setValue(CurrentDate);


                }
                ProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void UploadOrdersData() {

        getLocation();


        String name = Name.getText().toString();
        String UserPhone = Phone.getText().toString();
        String address = Address.getText().toString();


        SharedPreferences prefs = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String accountPhone = prefs.getString("Phone", "");

        Intent intent = getIntent();
        String CartPrice = intent.getStringExtra("CartPrice");

        DatabaseReference OrdersRef = FirebaseDatabase.getInstance().getReference("orders/" + accountPhone);

        OrdersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                OrdersRef.child("id").setValue(accountPhone);
                OrdersRef.child("name").setValue(name);
                OrdersRef.child("phone").setValue(UserPhone);
                OrdersRef.child("address").setValue(address);
                OrdersRef.child("createdAt").setValue(CreatedDate);
                OrdersRef.child("isConfirmed").setValue(false);
                OrdersRef.child("isDelivered").setValue(false);
                OrdersRef.child("Price").setValue(CartPrice);
                OrdersRef.child("Latitude").setValue(Latitude);
                OrdersRef.child("Longitude").setValue(Longitude);
                OrdersRef.child("remianingTime").setValue("40");
                OrdersRef.child("Date").setValue(CurrentDate);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(Address_detail.this, "Something went wrong", Toast.LENGTH_LONG).show();

            }
        });

        ProgressBar.setVisibility(View.GONE);
        startActivity(new Intent(Address_detail.this, OrderStatus.class));
    }

    private void GetDateAndTime() {
        CreatedDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        CurrentDate = df.format(c);

    }
}