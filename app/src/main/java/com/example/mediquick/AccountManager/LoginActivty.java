package com.example.mediquick.AccountManager;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mediquick.Contract.MediContract;
import com.example.mediquick.MainActivity;
import com.example.mediquick.R;
import com.example.mediquick.Utils.NetworkPermissionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class LoginActivty extends AppCompatActivity {

    private EditText user_name;
    private EditText otp;
    private TextView phno;

    private String prevstarted = "";

    private String phone_number;
    private String verificationCode;

    private LinearLayout otp_linearLayout;
    private LinearLayout name_linearLayout;
    private TextView please_wait_view;

    private Button next_button;
    private Button login_button;

    private String name;


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private FirebaseAuth auth;

    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private String age_String;
    private  String gender;
    private  String ad_phno_string;
    private  String bloodgrp;
    private  String address_String;
    private  String profession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activty);


        next_button=findViewById(R.id.next_button);
        login_button=findViewById(R.id.login_button);

        otp_linearLayout=findViewById(R.id.otp_linear_layout);
        name_linearLayout=findViewById(R.id.name_linear_layout);
        please_wait_view=findViewById(R.id.please_wait_view);

        user_name=findViewById(R.id.user_name);
        otp=findViewById(R.id.otp);
        phno=findViewById(R.id.ph_no);

        databaseReference = MediContract.firebaseDatabase.getReference().child(MediContract.USERS);

        StartFirebaseLogin();



        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkPermissionManager.checkInternetConnection(LoginActivty.this)) {
                    Toast.makeText(LoginActivty.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(user_name.getText().toString().trim().isEmpty()){
                    Toast.makeText(LoginActivty.this,"Enter something",Toast.LENGTH_SHORT).show();
                    return;
                }
                name=user_name.getText().toString();
                checkForExistingNames(name);


            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkPermissionManager.checkInternetConnection(LoginActivty.this)) {
                    Toast.makeText(LoginActivty.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                String otp_string=otp.getText().toString().trim();
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationCode,otp_string);
                SignInWith(credential);
            }
        });



    }

    public void StartFirebaseLogin() {
        auth= FirebaseAuth.getInstance();


        //auth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        //used to disable Recaptcha


        mCallback=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(LoginActivty.this,"verification completed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                name_linearLayout.setVisibility(View.VISIBLE);
                otp_linearLayout.setVisibility(View.GONE);
                please_wait_view.setVisibility(View.GONE);
                Toast.makeText(LoginActivty.this,"verification failed",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                verificationCode=s;
                name_linearLayout.setVisibility(View.GONE);
                otp_linearLayout.setVisibility(View.VISIBLE);
                please_wait_view.setVisibility(View.GONE);
                phno.setText("OTP will be sent to ********"+phone_number.substring(11));
                Toast.makeText(LoginActivty.this,"Code sent",Toast.LENGTH_SHORT).show();
                super.onCodeSent(s, forceResendingToken);
            }
        };
    }

    public void SignInWith(PhoneAuthCredential credential){

        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    fetchDetailsfromDatabase();

                }
                else{
                    Toast.makeText(LoginActivty.this,"Incorrect OTP",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void VerifyPhoneNumber(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_number,
                60,
                TimeUnit.SECONDS,
                LoginActivty.this,
                mCallback
        );
    }

    public void checkForExistingNames(String name) {


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(name)){
                    getPhonenum(name);
                }
                else{
                    Toast.makeText(LoginActivty.this,"Username not found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getPhonenum(String name){
       databaseReference.child(name).child(MediContract.DETAILS).child(MediContract.CONTACT).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               phone_number=snapshot.getValue().toString();
               name_linearLayout.setVisibility(View.GONE);
               otp_linearLayout.setVisibility(View.GONE);
               please_wait_view.setVisibility(View.VISIBLE);
               VerifyPhoneNumber();

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
    public void setSharedPreference(){

        sharedPreferences=getSharedPreferences(String.valueOf(R.string.userpreference),MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(prevstarted, true);
        editor.putString(String.valueOf(R.string.username),name);
        editor.putString(String.valueOf(R.string.age),age_String);
        editor.putString(String.valueOf(R.string.gender),gender);
        editor.putString(String.valueOf(R.string.contact),phone_number);
        editor.putString(String.valueOf(R.string.rcontact),ad_phno_string);
        editor.putString(String.valueOf(R.string.bloodgroup),bloodgrp);
        editor.putString(String.valueOf(R.string.address),address_String);
        editor.putString(String.valueOf(R.string.usertype),profession);
        editor.apply();

        Intent intent=new Intent(LoginActivty.this, MainActivity.class);
        startActivity(intent);
        OneTimeUserActivity.fa.finish();
        finish();


    }
    public void fetchDetailsfromDatabase(){

        databaseReference.child(name).child(MediContract.DETAILS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterator iterator=snapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    address_String=((DataSnapshot)iterator.next()).getValue().toString();
                    age_String=((DataSnapshot)iterator.next()).getValue().toString();
                    bloodgrp=((DataSnapshot)iterator.next()).getValue().toString();
                    phone_number=((DataSnapshot)iterator.next()).getValue().toString();
                    gender=((DataSnapshot)iterator.next()).getValue().toString();
                    ad_phno_string=((DataSnapshot)iterator.next()).getValue().toString();
                    profession=((DataSnapshot)iterator.next()).getValue().toString();
                }
                setSharedPreference();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}