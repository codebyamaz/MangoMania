package com.example.mangoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Order_Details extends AppCompatActivity {


    String price;
    int p = 0;
    TextView ItemCount, ItemPrice, ItemName,itemDescription;
    int itemCountValue = 1;
    int itemPriceValue = 0;
    CardView PlusCard, SubtractCard;


    ImageView OrderImage;
    CardView btn_AddToCart;
    LottieAnimationView lottiOnBtnClick, lottiOnBtnClick1, lottiOnBtnClick2, lottiOnBtnClick3;


    DatabaseReference reference,OrderTotalRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);




        lottiOnBtnClick = findViewById(R.id.add_to_cart_animation);
        lottiOnBtnClick1 = findViewById(R.id.add_to_cart_animation1);
        lottiOnBtnClick2 = findViewById(R.id.add_to_cart_animation2);
        lottiOnBtnClick3 = findViewById(R.id.add_to_cart_animation3);

        btn_AddToCart = findViewById(R.id.btn_add_to_cart);
        OrderImage = findViewById(R.id.item_image);
        ItemName = findViewById(R.id.item_name);
        ItemCount = findViewById(R.id.item_count);
        PlusCard = findViewById(R.id.plus_icon);
        ItemPrice = findViewById(R.id.item_price);
        SubtractCard = findViewById(R.id.subtract_icon);
        itemDescription=findViewById(R.id.item_description);


        Intent iin = getIntent();
        Bundle b = iin.getExtras();

        if (b != null) {
            String image = (String) b.get("OrderImage");
            price = (String) b.get("OrderPrice");
            String name = (String) b.get("OrderName");
            p = Integer.parseInt(price);

            String description=(String)b.get("OrderDescription");

            Glide.with(Order_Details.this).load(image).into(OrderImage);
            ItemName.setText(name);
            itemDescription.setText(description);
            ItemPrice.setText(price);
            itemPriceValue = p;
        }


        btn_AddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_AddToCart.setEnabled(false);


                SharedPreferences prefs = getSharedPreferences("LoginInfo", MODE_PRIVATE);
                String phone = prefs.getString("Phone", "");

                reference= FirebaseDatabase.getInstance().getReference("cart/"+phone+"/");

                reference=reference.push();

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (b != null) {

                            reference.child("single_price").setValue(b.get("OrderPrice"));
                            reference.child("image").setValue(b.get("OrderImage"));
                            reference.child("name").setValue(b.get("OrderName"));
                            reference.child("price").setValue(String.valueOf(itemPriceValue));
                            reference.child("quantity").setValue(String.valueOf(itemCountValue));
                            reference.child("key").setValue(snapshot.getKey());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                lottiOnBtnClick.setVisibility(View.VISIBLE);
                lottiOnBtnClick.playAnimation();
                lottiOnBtnClick1.setVisibility(View.VISIBLE);
                lottiOnBtnClick1.playAnimation();
                lottiOnBtnClick2.setVisibility(View.VISIBLE);
                lottiOnBtnClick2.playAnimation();
                lottiOnBtnClick3.setVisibility(View.VISIBLE);
                lottiOnBtnClick3.playAnimation();

                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(Order_Details.this,MainActivity.class));
                    }
                },700);

            }
        });


        PlusCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemCountValue++;
                itemPriceValue = itemPriceValue + p;
                ItemCount.setText(String.valueOf(itemCountValue));
                ItemPrice.setText(String.valueOf(itemPriceValue));
            }
        });

        SubtractCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCountValue == 1) {
                    Toast.makeText(Order_Details.this, "You Got the Minimum range", Toast.LENGTH_SHORT).show();
                } else {
                    itemPriceValue = itemPriceValue - p;
                    itemCountValue--;
                    ItemCount.setText(String.valueOf(itemCountValue));
                    ItemPrice.setText(String.valueOf(itemPriceValue));
                }
            }
        });

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();

        // here also we are excluding status bar,
        // action bar and navigation bar from animation.
//        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        // below methods are used for adding
        // enter and exit transition.
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
//

    }


}