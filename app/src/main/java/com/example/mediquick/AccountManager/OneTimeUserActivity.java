package com.example.mediquick.AccountManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.example.mediquick.R;
import com.example.mediquick.Utils.NetworkPermissionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.util.concurrent.TimeUnit;

public class OneTimeUserActivity extends AppCompatActivity {

    private LinearLayout phno_linear_layout;
    private LinearLayout otp_linear_layout;
    private TextView please_wait_view;

    private EditText phno;
    private EditText otp;

    private TextView haveAccount_button;


    private String prevstarted = "";
    private String phone_number;
    private String verificationCode;

    private Button send_otp;
    private Button next_button;

    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private FirebaseAuth auth;

    public static Activity fa;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_time_user);

        fa=OneTimeUserActivity.this;

        phno_linear_layout=findViewById(R.id.phno_linear_layout);
        otp_linear_layout=findViewById(R.id.otp_linear_layout);
        please_wait_view=findViewById(R.id.please_wait_view);

        next_button=findViewById(R.id.next_button);

        haveAccount_button=findViewById(R.id.haveAccount_button);

        StartFirebaseLogin();

        phno=findViewById(R.id.ph_no);
        phno.setFocusableInTouchMode(true);
        phno.requestFocus();
        otp=findViewById(R.id.otp);

        send_otp=findViewById(R.id.send_otp);

        sharedPreferences = getSharedPreferences(String.valueOf(R.string.userpreference), MODE_PRIVATE);

        send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkPermissionManager.checkInternetConnection(OneTimeUserActivity.this)) {
                    Toast.makeText(OneTimeUserActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(phno.getText().toString().trim().length()!=10){
                    Toast.makeText(OneTimeUserActivity.this,"Invalid Phone number",Toast.LENGTH_SHORT).show();
                    return;
                }

                phno_linear_layout.setVisibility(View.GONE);
                otp_linear_layout.setVisibility(View.GONE);
                please_wait_view.setVisibility(View.VISIBLE);
                phone_number="+91"+phno.getText().toString().trim();
                VerifyPhoneNumber();

            }

        });



        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkPermissionManager.checkInternetConnection(OneTimeUserActivity.this)) {
                    Toast.makeText(OneTimeUserActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }



                String otp_string=otp.getText().toString().trim();
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationCode,otp_string);
                SignInWith(credential);


            }
        });

        haveAccount_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OneTimeUserActivity.this,LoginActivty.class);
                startActivity(intent);
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
                Toast.makeText(OneTimeUserActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                phno_linear_layout.setVisibility(View.VISIBLE);
                otp_linear_layout.setVisibility(View.GONE);
                please_wait_view.setVisibility(View.GONE);
                Toast.makeText(OneTimeUserActivity.this,"verification failed",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                verificationCode=s;

                phno_linear_layout.setVisibility(View.GONE);
                otp_linear_layout.setVisibility(View.VISIBLE);
                please_wait_view.setVisibility(View.GONE);

                otp.setFocusableInTouchMode(true);
                otp.requestFocus();

                Toast.makeText(OneTimeUserActivity.this,"Code sent",Toast.LENGTH_SHORT).show();
                super.onCodeSent(s, forceResendingToken);
            }
        };
    }

    public void SignInWith(PhoneAuthCredential credential){

        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(OneTimeUserActivity.this, account2.class);
                    intent.putExtra("phone_number",phone_number);

                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(OneTimeUserActivity.this,"Incorrect OTP",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void VerifyPhoneNumber(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_number,
                60,
                TimeUnit.SECONDS,
                OneTimeUserActivity.this,
                mCallback
        );
    }

    @Override
    public void onBackPressed() {
        MediContract.onBackPress(OneTimeUserActivity.this);
    }


}