package com.example.mangoapp;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.mangoapp.order_status.Order_status_adapterClass;
import com.example.mangoapp.order_status.Order_status_modelClass;

import java.util.ArrayList;

public class OrderStatus extends AppCompatActivity {

    RecyclerView Order_Status_RecyclerView;
    Order_status_adapterClass order_status_adapterClass;
    ArrayList<Order_status_modelClass> status_items_list;
    DatabaseReference Order_Status_Ref;
    TextView OrderPrice;
    DatabaseReference OrderPriceRef;
    ProgressBar ProgressBar;
    CardView btnOk;
    DatabaseReference OrderTimeRef;
    TextView RemainingTime,RemainingTime2;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        SharedPreferences prefs = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        phone = prefs.getString("Phone", "");


        Order_Status_RecyclerView = findViewById(R.id.Order_status_RecyclerView);
        ProgressBar = findViewById(R.id.Status_ProgressBar);
        OrderPrice = findViewById(R.id.OrderTotalPrice);
        btnOk=findViewById(R.id.order_status_btn);
        RemainingTime=findViewById(R.id.order_delivery_time);
        RemainingTime2=findViewById(R.id.OrderStatus_remainingTime);


        Order_Status_RecyclerView.setNestedScrollingEnabled(false);

        OrderPriceRef = FirebaseDatabase.getInstance().getReference("orders/" + phone + "/Price");
        OrderPriceRef.keepSynced(true);
        OrderPriceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    OrderPrice.setText(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        OrderTimeRef = FirebaseDatabase.getInstance().getReference("orders/" + phone + "/remianingTime");
        OrderTimeRef.keepSynced(true);
        OrderTimeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    RemainingTime.setText(snapshot.getValue().toString());
                    RemainingTime2.setText(snapshot.getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ProgressBar.setVisibility(View.VISIBLE);


        Order_Status_RecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        gridLayoutManager.setOrientation(VERTICAL);
        Order_Status_RecyclerView.setLayoutManager(gridLayoutManager);


        status_items_list = new ArrayList<>();
        order_status_adapterClass = new Order_status_adapterClass(OrderStatus.this, status_items_list);
        Order_Status_RecyclerView.setAdapter(order_status_adapterClass);




        Order_Status_Ref = FirebaseDatabase.getInstance().getReference("orders/" + phone + "/products");
        Order_Status_Ref.keepSynced(true);
        Order_Status_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Order_status_modelClass order_status_modelClass = dataSnapshot.getValue(Order_status_modelClass.class);
                        status_items_list.add(order_status_modelClass);
                    }
                }
                if (!snapshot.exists()){
                    startActivity(new Intent(OrderStatus.this,MainActivity.class));
                    Toast.makeText(OrderStatus.this, "Successfully Delivered!", Toast.LENGTH_SHORT).show();
                }

                order_status_adapterClass.notifyDataSetChanged();
                ProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear order status data
                DatabaseReference orderStatusRef = FirebaseDatabase.getInstance().getReference("orders/" + phone + "/products/");
                orderStatusRef.removeValue();

                // Clear cart data
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart/" + phone);
                cartRef.removeValue();

                // Clear CartPrice data
                DatabaseReference calculateRef = FirebaseDatabase.getInstance().getReference("CartPrice/" + phone);
                calculateRef.removeValue();

                // Start the MainActivity (or Cart activity if needed)
                startActivity(new Intent(OrderStatus.this, MainActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}