package com.example.mangoapp.categorylist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mangoapp.Category_onClick_Interface;
import com.example.mangoapp.R;

import java.util.ArrayList;

public class category_adapterClass extends RecyclerView.Adapter<category_adapterClass.ViewHolder> {

    Context CategoryContext;
    ArrayList<com.example.mangoapp.categorylist.category_modelClass> CategoryList;
    Category_onClick_Interface recyclerView_items_interface;
    int item_row_index;

    public category_adapterClass(Context categoryContext, ArrayList<category_modelClass> categoryList, Category_onClick_Interface recyclerView_items_interface) {
        CategoryContext = categoryContext;
        CategoryList = categoryList;
        this.recyclerView_items_interface = recyclerView_items_interface;
    }

    @NonNull
    @Override
    public category_adapterClass.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(CategoryContext).inflate(R.layout.item_category_design, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull category_adapterClass.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        category_modelClass category_modelClass = CategoryList.get(position);

        Glide.with(CategoryContext).load(category_modelClass.getImage()).into(holder.CategoryImage);

        holder.CategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_items_interface.onItemClick(category_modelClass.getName());
                item_row_index = position;
                notifyDataSetChanged();
            }
        });

        holder.CategoryImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                recyclerView_items_interface.onItemClick(category_modelClass.getName());
                item_row_index = position;
                notifyDataSetChanged();
                return true;
            }
        });

        holder.Category_Background.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                recyclerView_items_interface.onItemClick(category_modelClass.getName());
                item_row_index = position;
                notifyDataSetChanged();
                return true;
            }
        });
        holder.Category_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView_items_interface.onItemClick(category_modelClass.getName());
                item_row_index = position;
                notifyDataSetChanged();
            }
        });

        holder.Category_Layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                recyclerView_items_interface.onItemClick(category_modelClass.getName());
                item_row_index = position;
                notifyDataSetChanged();
                return true;
            }
        });



        if (item_row_index == position) {

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(147, 147);
            holder.Category_Background.setLayoutParams(layoutParams);

//            holder.Category_Background.setBackground(ContextCompat.getDrawable(CategoryContext, R.drawable.category_bg_select));
//            holder.CategoryImage.setColorFilter(Color.parseColor("#ffffff"));


        } else {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(137, 137);

            holder.Category_Background.setLayoutParams(layoutParams);
//            holder.Category_Background.setBackground(ContextCompat.getDrawable(CategoryContext, R.drawable.category_bg));
//            holder.CategoryImage.setColorFilter(Color.parseColor("#ffcc00"));

        }

        ContextCompat.getDrawable(CategoryContext, R.drawable.category_bg_select);

    }

    @Override
    public int getItemCount() {
        return CategoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView CategoryImage;
        LinearLayout Category_Background;
        LinearLayout Category_Layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CategoryImage = itemView.findViewById(R.id.item_category_imageview);
            Category_Background = itemView.findViewById(R.id.category_background);
            Category_Layout=itemView.findViewById(R.id.category_layout);

        }
    }
}
