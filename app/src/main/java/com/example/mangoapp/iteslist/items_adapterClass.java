package com.example.mangoapp.iteslist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mangoapp.Order_Details;
import com.example.mangoapp.R;

import java.util.ArrayList;

public class items_adapterClass extends RecyclerView.Adapter<items_adapterClass.ViewHolder> {

    Context ItemsContext;
    ArrayList<items_modelClass> ItemsList;

    public items_adapterClass(Context itemsContext, ArrayList<items_modelClass> itemsList) {
        ItemsContext = itemsContext;
        ItemsList = itemsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ItemsContext).inflate(R.layout.items_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        items_modelClass items_modelClass = ItemsList.get(position);

        holder.Title.setText(items_modelClass.getTitle());
        holder.Price.setText(items_modelClass.getPrice());
        Glide.with(ItemsContext).load(items_modelClass.getImage()).into(holder.Image);


        holder.Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemsContext, Order_Details.class);

                String strName=items_modelClass.getTitle();
                String strPrice=items_modelClass.getPrice();
                String strImage = items_modelClass.getImage();
                String strDescription= items_modelClass.getDescription();
                intent.putExtra("OrderImage", strImage);
                intent.putExtra("OrderName", strName);
                intent.putExtra("OrderPrice", strPrice);
                intent.putExtra("OrderDescription",strDescription);

                // below method is used to make scene transition
                // and adding fade animation in it.
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) ItemsContext, holder.Image, ViewCompat.getTransitionName(holder.Image));
                // starting our activity with below method.
                ItemsContext.startActivity(intent, options.toBundle());
            }
        });




    }

    @Override
    public int getItemCount() {
        return ItemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Title, Price;
        ImageView Image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Title=itemView.findViewById(R.id.item_name);
            Price=itemView.findViewById(R.id.item_price);
            Image=itemView.findViewById(R.id.item_image);
        }
    }
}
