package com.example.mangoapp;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.mangoapp.categorylist.category_adapterClass;
import com.example.mangoapp.categorylist.category_modelClass;
import com.example.mangoapp.iteslist.items_adapterClass;
import com.example.mangoapp.iteslist.items_modelClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Category_onClick_Interface {

    RecyclerView CategoryRecyclerView;
    category_adapterClass CategoryAdapter;
    ArrayList<category_modelClass> CategoryList;
    DatabaseReference CategoryReference;
    RecyclerView ItemsRecyclerView;
    items_adapterClass ItemsAdapter;
    ArrayList<items_modelClass> ItemsList;
    DatabaseReference ItemsReference;
    String ItemPosition = "burger";
    DatabaseReference UserNameRef;
    LottieAnimationView ProgressBar;
    DatabaseReference OrderStatusCheckRef;
    DatabaseReference CheckOrderRef;
    CardView AddToCart;
    TextView Username;
    TextView CartItemCount;
    String phone;
    DatabaseReference CartCountRef;
    DatabaseReference CartCheckRef;
    DatabaseReference ConfirmedStatusRef;
    String ConfirmValue="false";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CategoryRecyclerView = findViewById(R.id.category_recyclerView);
        ItemsRecyclerView = findViewById(R.id.items_recyclerView);
        ProgressBar = findViewById(R.id.progressBar);
        Username = findViewById(R.id.username);
        AddToCart = findViewById(R.id.addtocart);
        CartItemCount = findViewById(R.id.cart_count);

        GetAccountPhone();
        UserCartCount();

        AddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference CheckConfirmedStatus = FirebaseDatabase.getInstance().getReference("orders/" + phone + "/isConfirmed");
                CheckConfirmedStatus.keepSynced(true);
                CheckConfirmedStatus.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            ConfirmValue = snapshot.getValue().toString();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (ConfirmValue.equals("true")) {
                    startActivity(new Intent(MainActivity.this, OrderStatus.class));

                } else if (ConfirmValue.equals("false")) {
                    CheckOrderStatus();
                }


//                startActivity(new Intent(MainActivity.this,Cart.class));
            }
        });

//
//        SharedPreferences prefs = getSharedPreferences("LoginInfo", MODE_PRIVATE);
//        phone = prefs.getString("Phone", "");
//
//        CheckOrderRef = FirebaseDatabase.getInstance().getReference("orders");
//        CheckOrderRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    String OrderStatus = dataSnapshot.child("id").getValue().toString();
//                    if (OrderStatus.equals(phone)) {
//                        OrderConfirmed();
//                    } else {
//                        AddToCart.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                startActivity(new Intent(MainActivity.this, Cart.class));
//
//                            }
//                        });
//                    }
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        CategoryReference = FirebaseDatabase.getInstance().getReference("Category");

        CategoryRecyclerView.setHasFixedSize(true);

        GridLayoutManager CategoryLayout = new GridLayoutManager(this, 1);
        CategoryLayout.setOrientation(HORIZONTAL);
        CategoryRecyclerView.setLayoutManager(CategoryLayout);

        CategoryList = new ArrayList<>();

        CategoryAdapter = new category_adapterClass(this, CategoryList, this);
        CategoryRecyclerView.setAdapter(CategoryAdapter);


        CategoryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        category_modelClass category_modelClass = dataSnapshot.getValue(category_modelClass.class);
                        CategoryList.add(category_modelClass);
                    }
                    CategoryAdapter.notifyDataSetChanged();
                    ProgressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        GetRecyclerViewData();
        GetUsername();

    }


    private void GetAccountPhone() {
        SharedPreferences prefs = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        phone = prefs.getString("Phone", "");

    }

    private void UserCartCount() {
        CartCountRef = FirebaseDatabase.getInstance().getReference("cart/" + phone);
        CartCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CartitemsCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CheckOrderStatus() {

        CartCheckRef = FirebaseDatabase.getInstance().getReference("cart/" + phone);
        CartCheckRef.keepSynced(true);
        CartCheckRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CheckOrderRef = FirebaseDatabase.getInstance().getReference("orders/" + phone);
                    CheckOrderRef.keepSynced(true);
                    CheckOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                ConfirmedStatusRef = FirebaseDatabase.getInstance().getReference("orders/" + phone + "/isConfirmed");
                                ConfirmedStatusRef.keepSynced(true);
                                ConfirmedStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            if (snapshot.getValue().toString().equals("true")) {
                                                startActivity(new Intent(MainActivity.this, OrderStatus.class));
                                            } else if (snapshot.getValue().toString().equals("false")) {
                                                startActivity(new Intent(MainActivity.this, Cart.class));
                                            }
                                        } else if (!snapshot.exists()) {
                                            startActivity(new Intent(MainActivity.this, Cart.class));
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else if (!snapshot.exists()) {
                                startActivity(new Intent(MainActivity.this, Cart.class));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                } else if (!snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "Add any item in the cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void CartitemsCount() {
        SharedPreferences prefs = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        phone = prefs.getString("Phone", "");
        DatabaseReference CartItemsCountRef;
        CartItemsCountRef = FirebaseDatabase.getInstance().getReference("cart/" + phone);
        CartItemsCountRef.keepSynced(true);
        CartItemsCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String CartItemsCount = String.valueOf(snapshot.getChildrenCount());
                    CartItemCount.setText(CartItemsCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void OrderConfirmed() {

        SharedPreferences prefs = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        phone = prefs.getString("Phone", "");

        OrderStatusCheckRef = FirebaseDatabase.getInstance().getReference("orders/" + phone + "/isConfirmed");


        OrderStatusCheckRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String OrderStatus = snapshot.getValue().toString();
                if (OrderStatus.equals("true")) {
                    AddToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(MainActivity.this, OrderStatus.class));
                        }
                    });

                } else if (OrderStatus.equals("false")) {
                    AddToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(MainActivity.this, Cart.class));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetUsername() {

        SharedPreferences prefs = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String phone = prefs.getString("Phone", "");

        UserNameRef = FirebaseDatabase.getInstance().getReference("User/" + phone);
        UserNameRef.child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Username.setText(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void GetRecyclerViewData() {

        ItemsRecyclerView.setNestedScrollingEnabled(false);
        ProgressBar.setVisibility(View.VISIBLE);

        ItemsReference = FirebaseDatabase.getInstance().getReference("products/" + ItemPosition);
        ItemsReference.keepSynced(true);
        ItemsRecyclerView.setHasFixedSize(true);
        GridLayoutManager ItemsLayout = new GridLayoutManager(this, 1);
        ItemsLayout.setOrientation(VERTICAL);
        ItemsRecyclerView.setLayoutManager(ItemsLayout);


        ItemsList = new ArrayList<>();
        ItemsAdapter = new items_adapterClass(this, ItemsList);
        ItemsRecyclerView.setAdapter(ItemsAdapter);


        ItemsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        items_modelClass items_modelClass = dataSnapshot.getValue(items_modelClass.class);
                        ItemsList.add(items_modelClass);
                    }
                    ProgressBar.setVisibility(View.GONE);
                    ItemsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onItemClick(String position) {
        ItemPosition = position;
        Toast.makeText(this, "" + ItemPosition, Toast.LENGTH_SHORT).show();
        GetRecyclerViewData();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}