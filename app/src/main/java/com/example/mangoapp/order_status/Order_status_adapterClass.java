package com.example.mangoapp.order_status;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mangoapp.R;
import com.example.mangoapp.iteslist.items_modelClass;

import java.util.ArrayList;

public class Order_status_adapterClass extends RecyclerView.Adapter<Order_status_adapterClass.ViewHolder> {

    Context OrderStatus_Context;
    ArrayList<Order_status_modelClass> status_items_list;

    public Order_status_adapterClass(Context orderStatus_Context, ArrayList<Order_status_modelClass> status_items_list) {
        OrderStatus_Context = orderStatus_Context;
        this.status_items_list = status_items_list;
    }

    @NonNull
    @Override
    public Order_status_adapterClass.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(OrderStatus_Context).inflate(R.layout.order_status_items_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Order_status_adapterClass.ViewHolder holder, int position) {
        Order_status_modelClass order_status_modelClass = status_items_list.get(position);

        holder.Name.setText(order_status_modelClass.getName());
        holder.Price.setText(order_status_modelClass.getPrice());
        Glide.with(OrderStatus_Context).load(order_status_modelClass.getImage()).into(holder.Image);

    }

    @Override
    public int getItemCount() {
        return status_items_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView Image;
        TextView Name, Price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.place_order_image);
            Name = itemView.findViewById(R.id.place_order_title);
            Price = itemView.findViewById(R.id.place_order_price);
        }
    }
}
