package com.example.mangoapp;

import android.app.Application;

import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivityOffline extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        FirebaseApp.initializeApp(this);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
