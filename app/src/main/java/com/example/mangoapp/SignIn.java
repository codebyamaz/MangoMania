package com.example.mangoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    private TextView GotoSignUp, BackArrow;
    private EditText Phone, Password;
    String StrPhone, StrPassword;
    private CardView BtnLogin;
    private String NumConfirm, PassConfirm;
    ProgressBar progressBar;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        GotoSignUp = findViewById(R.id.sign_up);
        BackArrow = findViewById(R.id.back_arrow);
        Phone = findViewById(R.id.sign_up_phone);
        Password = findViewById(R.id.sign_up_password);
        BtnLogin = findViewById(R.id.btn_sign_in);
        progressBar=findViewById(R.id.SignIn_progressBar);
        progressBar.setVisibility(View.GONE);

        LogInToAccount();
        GotoSignUp();
        OnBackArrowPress();
    }

    private void LogInToAccount() {
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                StrPhone = Phone.getText().toString();
                StrPassword = Password.getText().toString();

                if (StrPhone.isEmpty()) {
                    Toast.makeText(SignIn.this, "Enter Your Phone First", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                } else if (StrPassword.isEmpty()) {

                    Toast.makeText(SignIn.this, "Enter Your Password", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                } else if (StrPhone.length() != 11) {
                    Toast.makeText(SignIn.this, "Kindly Use Correct Number", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);

                } else if (StrPhone.length() == 11) {
                    progressBar.setVisibility(View.VISIBLE);
                    DatabaseReference UserLoginRef = FirebaseDatabase.getInstance().getReference("User");
                    UserLoginRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                NumConfirm = dataSnapshot.child("Phone").getValue().toString();
                                PassConfirm = dataSnapshot.child("Password").getValue().toString();

                                if (StrPhone.equals(NumConfirm) & StrPassword.equals(PassConfirm)) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(SignIn.this, "Successfully Login To You Account", Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor editor = getSharedPreferences("LoginInfo", MODE_PRIVATE).edit();
                                    editor.putString("login", "true");
                                    editor.putString("Phone", StrPhone);
                                    editor.apply();
                                    startActivity(new Intent(SignIn.this, MainActivity.class));
                                    break;
                                }


                            }
                            if (!StrPhone.equals(NumConfirm)) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignIn.this, "Don't have account", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignIn.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                        }
                    });


                }
            }
        });


    }

    private void OnBackArrowPress() {

        BackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(com.example.mangoapp.SignIn.this, Splash.class);
                startActivity(intent);
            }
        });
    }


    private void GotoSignUp() {
        GotoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(com.example.mangoapp.SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}