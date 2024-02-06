package com.example.mangoapp;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mangoapp.cart.cart_model_class;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.mangoapp.cart.cart_Adapter;

import java.util.ArrayList;

public class Cart extends AppCompatActivity implements CartItemRemove {

    RecyclerView CartRecyclerView;
    cart_Adapter cart_adapter;
    ArrayList<cart_model_class> cart_items_list;
    DatabaseReference cartRef;
    RelativeLayout CartLayout;
    LottieAnimationView ProgressBar;
    TextView TotalPriceTV;
    DatabaseReference CalculateRef;

    CardView CartConfirmBtn;
    int CartPrice;


    String LocationCheck = "0";

    private LocationRequest locationRequest;
    private static final int REQUEST_CHECK_SETTINGS = 10001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        CartRecyclerView = findViewById(R.id.CartRecyclerView);
        CartLayout = findViewById(R.id.cart_layout);
        ProgressBar = findViewById(R.id.Cart_ProgressBar);
        TotalPriceTV = findViewById(R.id.CartTotalPrice);
        CartConfirmBtn = findViewById(R.id.cart_confirm);


        TotalPriceData();
        CartDataSeter();
        TransferCartValue();


    }


    private void TotalPriceData() {


        SharedPreferences prefs = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String phone = prefs.getString("Phone", "");
        CalculateRef = FirebaseDatabase.getInstance().getReference("CartPrice/" + phone);
        CalculateRef.keepSynced(true);

        CalculateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CartPrice = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        CartPrice = CartPrice + (Integer.parseInt(dataSnapshot.getValue().toString()));

                    }
                    TotalPriceTV.setText(String.valueOf(CartPrice));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void CartDataSeter() {
        CartLayout.setVisibility(View.GONE);


        SharedPreferences prefs = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String phone = prefs.getString("Phone", "");
        cartRef = FirebaseDatabase.getInstance().getReference("cart/" + phone);
        cartRef.keepSynced(true);


        CartRecyclerView.setHasFixedSize(true);
        GridLayoutManager cart_gridLayoutManager = new GridLayoutManager(this, 1);
        cart_gridLayoutManager.setOrientation(VERTICAL);
        CartRecyclerView.setLayoutManager(cart_gridLayoutManager);


        cart_items_list = new ArrayList<>();
        cart_adapter = new cart_Adapter(Cart.this, cart_items_list, this);
        CartRecyclerView.setAdapter(cart_adapter);
        cart_adapter.notifyDataSetChanged();

        CartRecyclerView.setNestedScrollingEnabled(false);

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        cart_model_class cart_model_class = dataSnapshot.getValue(cart_model_class.class);
                        cart_items_list.add(cart_model_class);
                    }
                    cart_adapter.notifyDataSetChanged();
                    CartLayout.setVisibility(View.VISIBLE);
                    ProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(String position) {
        cartRef.child(position).removeValue();
        ProgressBar.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CartDataSeter();
            }
        }, 1000);

    }

    private void TransferCartValue() {

        CartConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EnableLocation();

//                if (LocationCheck.equals("false")) {
//                    Toast.makeText(Cart.this, "Kindly Click Ok to Allow Location", Toast.LENGTH_SHORT).show();
//                }


//                if (LocationCheck.equals("true")) {
//
//                    Intent intent = new Intent(Cart.this, Address_detail.class);
//                    String TotalPrice = String.valueOf(CartPrice);
//
//                    intent.putExtra("CartPrice", TotalPrice);
//                    startActivity(intent);
//                }
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
//                    Toast.makeText(Cart.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();
                    LocationCheck = "true";

                    Intent intent = new Intent(Cart.this, Address_detail.class);
                    String TotalPrice = String.valueOf(CartPrice);

                    intent.putExtra("CartPrice", TotalPrice);
                    startActivity(intent);

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(Cart.this, REQUEST_CHECK_SETTINGS);
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
//                    Toast.makeText(this, "GPS is turned on", Toast.LENGTH_SHORT).show();
                    LocationCheck = "true";

                    if (LocationCheck.equals("false")) {
                        Toast.makeText(Cart.this, "Kindly Click Ok to Allow Location", Toast.LENGTH_SHORT).show();
                    }


                    if (LocationCheck.equals("true")) {

                        Intent intent = new Intent(Cart.this, Address_detail.class);
                        String TotalPrice = String.valueOf(CartPrice);

                        intent.putExtra("CartPrice", TotalPrice);
                        startActivity(intent);
                    }

                case Activity.RESULT_CANCELED:
//                    Toast.makeText(this, "GPS required to be turned on", Toast.LENGTH_SHORT).show();
                    LocationCheck = "false";


                    if (LocationCheck.equals("false")) {
                        Toast.makeText(Cart.this, "Kindly Click Ok to Allow Location", Toast.LENGTH_SHORT).show();
                    }


                    if (LocationCheck.equals("true")) {

                        Intent intent = new Intent(Cart.this, Address_detail.class);
                        String TotalPrice = String.valueOf(CartPrice);

                        intent.putExtra("CartPrice", TotalPrice);
                        startActivity(intent);
                    }
            }
        }
    }

    @Override
    public void onBtnAdd(String AddAction) {
        TotalPriceData();
    }

    @Override
    public void onBtnSub(String SubAction) {
        TotalPriceData();
    }
}