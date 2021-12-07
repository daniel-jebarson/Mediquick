package com.example.mediquick;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import com.example.mediquick.AccountManager.OneTimeUserActivity;

public class SplashActivity extends AppCompatActivity {
    private String prev_started="";
    private SharedPreferences sharedPreferences;
    private Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(getSharedPreferences(String.valueOf(R.string.userpreference),MODE_PRIVATE).getBoolean(prev_started,false)){
                    i=new Intent(SplashActivity.this,MainActivity.class);
                }
                else{
                    i=new Intent(SplashActivity.this, OneTimeUserActivity.class);
                }
                startActivity(i);
                finish();
            }
        },0);


    }
}