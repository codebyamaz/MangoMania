package com.example.mangoapp.cart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.mangoapp.Address_detail;
import com.example.mangoapp.CartItemRemove;
import com.example.mangoapp.R;
import com.example.mangoapp.address.address_model_class;

import java.util.ArrayList;

public class cart_Adapter extends RecyclerView.Adapter<cart_Adapter.ViewHolder> {

    Context CartContext;
    ArrayList<cart_model_class> cart_items_list;
    DatabaseReference Addreference, AddPriceRef, SubPriceRef, OrderPriceRef;
    DatabaseReference Subreference;
    CartItemRemove cartItemRemove;


    int AddCount;
    String AddSCount;
    String AddValue;

    int SubCount;
    String SubSCount;
    String SubValue;


    public cart_Adapter(Context cartContext, ArrayList<cart_model_class> cart_items_list, CartItemRemove cartItemRemove) {
        CartContext = cartContext;
        this.cart_items_list = cart_items_list;
        this.cartItemRemove = cartItemRemove;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(CartContext).inflate(R.layout.cart_item_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        cart_model_class cart_model_class = cart_items_list.get(position);

        Glide.with(CartContext).load(cart_model_class.getImage()).into(holder.Item_Image);
        holder.Item_Name.setText(cart_model_class.getName());
        holder.Item_Price.setText(cart_model_class.getPrice());
        holder.Item_Quantity.setText(cart_model_class.getQuantity());


        SharedPreferences prefs = CartContext.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        String phone = prefs.getString("Phone", "");

        OrderPriceRef = FirebaseDatabase.getInstance().getReference("CartPrice/" + phone + "/" + cart_model_class.getKey());
        OrderPriceRef.setValue(cart_model_class.getPrice());


        holder.Remove_Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartItemRemove.onItemClick(cart_model_class.getKey());

                String key = cart_model_class.getKey();
                SharedPreferences prefs = CartContext.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
                String phone = prefs.getString("Phone", "");

                OrderPriceRef = FirebaseDatabase.getInstance().getReference("CartPrice/" + phone + "/" + key);

                OrderPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        OrderPriceRef.removeValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


        holder.BtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] AddCalculate = new String[1];
                holder.ProgressOnBtnClick.setVisibility(View.VISIBLE);
                String key = cart_model_class.getKey();
                SharedPreferences prefs = CartContext.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
                String phone = prefs.getString("Phone", "");


                Addreference = FirebaseDatabase.getInstance().getReference("cart/" + phone + "/" + key + "/quantity");
                AddPriceRef = FirebaseDatabase.getInstance().getReference("cart/" + phone + "/" + key + "/price");

                Addreference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        AddSCount = snapshot.getValue().toString();


                        AddCount = Integer.parseInt(AddSCount);
                        AddCount++;
                        AddValue = String.valueOf(AddCount);


                        Addreference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Addreference.setValue(AddValue);
                                holder.Item_Quantity.setText(AddValue);
                                holder.ProgressOnBtnClick.setVisibility(View.INVISIBLE);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                AddPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int Value = Integer.parseInt(AddValue);
                        int Price = Integer.parseInt(cart_model_class.getSingle_price());

                        AddCalculate[0] = String.valueOf(Value * Price);
                        AddPriceRef.setValue(AddCalculate[0]);
                        holder.Item_Price.setText(AddCalculate[0]);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                OrderPriceRef = FirebaseDatabase.getInstance().getReference("CartPrice/" + phone + "/" + key);

                OrderPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        OrderPriceRef.setValue(AddCalculate[0]);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });


        holder.BtnSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] SubCalculate = new String[1];

                cartItemRemove.onBtnSub("Hello i am Sub");
                holder.ProgressOnBtnClick.setVisibility(View.VISIBLE);
                String key = cart_model_class.getKey();
                SharedPreferences prefs = CartContext.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
                String phone = prefs.getString("Phone", "");
                Subreference = FirebaseDatabase.getInstance().getReference("cart/" + phone + "/" + key + "/quantity");
                SubPriceRef = FirebaseDatabase.getInstance().getReference("cart/" + phone + "/" + key + "/price");

                Subreference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        SubSCount = snapshot.getValue().toString();


                        SubCount = Integer.parseInt(SubSCount);
                        SubCount--;
                        SubValue = String.valueOf(SubCount);

                        Subreference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (SubValue.equals("0")) {
                                    holder.ProgressOnBtnClick.setVisibility(View.INVISIBLE);
                                    Toast.makeText(CartContext, "You got the minimum limit", Toast.LENGTH_SHORT).show();
                                } else {

                                    Subreference.setValue(SubValue);
                                    holder.Item_Quantity.setText(SubValue);
                                    holder.ProgressOnBtnClick.setVisibility(View.INVISIBLE);


                                    SubPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            int Value = Integer.parseInt(SubValue);
                                            int Price = Integer.parseInt(cart_model_class.getSingle_price());

                                            SubCalculate[0] = String.valueOf(Value * Price);
                                            SubPriceRef.setValue(SubCalculate[0]);
                                            holder.Item_Price.setText(SubCalculate[0]);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    OrderPriceRef = FirebaseDatabase.getInstance().getReference("CartPrice/" + phone + "/" + key);

                                    OrderPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            OrderPriceRef.setValue(SubCalculate[0]);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return cart_items_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView Item_Image;
        TextView Item_Name, Item_Price, Item_Quantity;
        CardView BtnAdd, BtnSubtract;
        ImageView Remove_Item;
        ProgressBar ProgressOnBtnClick;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Item_Image = itemView.findViewById(R.id.cart_item_image);
            Item_Name = itemView.findViewById(R.id.cart_item_name);
            Item_Price = itemView.findViewById(R.id.cart_item_price);
            Item_Quantity = itemView.findViewById(R.id.cart_item_count);
            BtnAdd = itemView.findViewById(R.id.cart_add_btn);
            BtnSubtract = itemView.findViewById(R.id.cart_subtract_icon);
            ProgressOnBtnClick = itemView.findViewById(R.id.progressBar);
            Remove_Item = itemView.findViewById(R.id.remove_icon);
        }
    }
}
