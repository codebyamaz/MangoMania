package com.example.mangoapp.address;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.mangoapp.cart.cart_model_class;

import java.util.ArrayList;

public class address_adapter extends RecyclerView.Adapter<address_adapter.ViewHolder> {
    Context AddressContext;
    ArrayList<com.example.mangoapp.address.address_model_class> address_list;


    public address_adapter(Context addressContext, ArrayList<address_model_class> address_list) {
        AddressContext = addressContext;
        this.address_list = address_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        address_model_class address_model_class = address_list.get(position);



        Toast.makeText(AddressContext, "kahsldfkhas;dklfjhalskdjfhalskdjf", Toast.LENGTH_SHORT).show();


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
