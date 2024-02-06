package com.example.mangoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    TextView SignIn, BackArrow;
    EditText Name, Phone, Password;
    CardView BtnCardSignUp;
    String NumConfirm, PassConfirm;
    ProgressBar Signup_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        SignIn = findViewById(R.id.sign_in);
        BackArrow = findViewById(R.id.back_arrow);
        Name = findViewById(R.id.sign_up_name);
        Phone = findViewById(R.id.sign_up_phone);
        Password = findViewById(R.id.sign_up_password);
        BtnCardSignUp = findViewById(R.id.btn_sign_up);
        Signup_progressbar = findViewById(R.id.SignUp_progressBar);

        Signup_progressbar.setVisibility(View.GONE);


        GoToSignInPage();
        OnBackArrowPress();
        SignUpData();

    }

    private void SignUpData() {

        BtnCardSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String StrName, StrPhone, StrPassword;
                StrName = Name.getText().toString();
                StrPhone = Phone.getText().toString();
                StrPassword = Password.getText().toString();

                if (StrName.isEmpty()) {
                    Toast.makeText(SignUp.this, "Enter Your Name First", Toast.LENGTH_LONG).show();
                } else if (StrPhone.isEmpty()) {
                    Toast.makeText(SignUp.this, "Enter Your Phone Number", Toast.LENGTH_LONG).show();
                } else if (StrPassword.isEmpty()) {
                    Toast.makeText(SignUp.this, "Enter Your Password", Toast.LENGTH_LONG).show();
                } else if (StrPhone.length() != 11) {
                    Toast.makeText(SignUp.this, "Kindly Use Correct Number", Toast.LENGTH_LONG).show();

                } else if (StrPhone.length() == 11) {
                    Signup_progressbar.setVisibility(View.VISIBLE);

                    DatabaseReference UserDataRef = FirebaseDatabase.getInstance().getReference("User");
                    UserDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                NumConfirm = dataSnapshot.child("Phone").getValue().toString();
                                PassConfirm = dataSnapshot.child("Password").getValue().toString();

                                if (StrPhone.equals(NumConfirm)) {
                                    Toast.makeText(SignUp.this, "Number is Already in use", Toast.LENGTH_LONG).show();
                                    Signup_progressbar.setVisibility(View.GONE);
                                    break;
                                }
                            }

                            if (!StrPhone.equals(NumConfirm)) {

                                UserDataRef.child(StrPhone).child("Phone").setValue(StrPhone);
                                UserDataRef.child(StrPhone).child("Password").setValue(StrPassword);
                                UserDataRef.child(StrPhone).child("Name").setValue(StrName);
                                Intent intent = new Intent(SignUp.this, SignIn.class);
                                Toast.makeText(SignUp.this, "Your Account Successfully Created.", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(SignUp.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                            Signup_progressbar.setVisibility(View.GONE);
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
                Intent intent = new Intent(SignUp.this, Splash.class);
                startActivity(intent);
            }
        });
    }

    private void GoToSignInPage() {

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}