package com.example.mediquick.AccountManager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.mediquick.Contract.MediContract;
import com.example.mediquick.MainActivity;
import com.example.mediquick.R;
import com.example.mediquick.Utils.NetworkPermissionManager;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class account3 extends AppCompatActivity {

    private Button first_aid_finish_button;

    private  CheckBox heart_attack_check_box;
    private CheckBox snake_bite_check_box;
    private CheckBox bleeding_check_box;
    private CheckBox drowned_check_box;
    private CheckBox stroke_check_box;
    private CheckBox convulsions_check_box;
    private CheckBox burns_check_box;
    private  CheckBox traumaticheadinjury_check_box;


    private String user_name;
    private String phone_number;
    private String profession;
    private  String age_String;
    private  String gender;
    private  String bloodgrp;
    private String ad_phno_string;
    private String address_String;

    private SharedPreferences sharedPreferences;
    private String prevstarted="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account3);

        sharedPreferences=getSharedPreferences(String.valueOf(R.string.userpreference),MODE_PRIVATE);

        heart_attack_check_box=findViewById(R.id.heart_attack_check_box);
        bleeding_check_box=findViewById(R.id.bleeding_check_box);
        snake_bite_check_box=findViewById(R.id.snake_Bite_check_box);
        drowned_check_box=findViewById(R.id.drowned_check_box);
        stroke_check_box=findViewById(R.id.stroke_check_box);
        convulsions_check_box=findViewById(R.id.convulsions_check_box);
        burns_check_box=findViewById(R.id.burns_check_box);
        traumaticheadinjury_check_box=findViewById(R.id.traumaticheadinjury_check_box);

        first_aid_finish_button=findViewById(R.id.first_aid_finish_button);
        first_aid_finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkPermissionManager.checkInternetConnection(account3.this)) {
                    Toast.makeText(account3.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map first_aid_map=new HashMap<>();
                int nfirst_Aid=0;

                if(heart_attack_check_box.isChecked()){
                    first_aid_map.put(getResources().getString(R.string.HEART_ATTACK_STRING),0);
                    nfirst_Aid++;
                }
                if(snake_bite_check_box.isChecked()){
                    first_aid_map.put(getResources().getString(R.string.SNAKE_BITE_STRING),0);
                    nfirst_Aid++;
                }
                if(bleeding_check_box.isChecked()){
                    first_aid_map.put(getResources().getString(R.string.BLEEDING_STRING),0);
                    nfirst_Aid++;
                }
                if(drowned_check_box.isChecked()){
                    first_aid_map.put(getResources().getString(R.string.DROWNED_STRING),0);
                    nfirst_Aid++;
                }
                if(stroke_check_box.isChecked()){
                    first_aid_map.put(getResources().getString(R.string.STROKE_STRING),0);
                    nfirst_Aid++;
                }
                if(convulsions_check_box.isChecked()){
                    first_aid_map.put(getResources().getString(R.string.CONVULSIONS_STRING),0);
                    nfirst_Aid++;
                }
                if(burns_check_box.isChecked()){
                    first_aid_map.put(getResources().getString(R.string.BURNS_STRING),0);
                    nfirst_Aid++;
                }
                if(traumaticheadinjury_check_box.isChecked()){
                    first_aid_map.put(getResources().getString(R.string.TRAUMATIC_HEAD_INJURY_STRING),0);
                    nfirst_Aid++;
                }

                if(nfirst_Aid==0){
                    Toast.makeText(account3.this,"Select any First Aid Methods",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    updateDatabase(first_aid_map);

                }
            }

        });
    }

    public void updateDatabase(Map<String,Object> first_aid_map){
        DatabaseReference databaseReference=MediContract.firebaseDatabase.getReference().child(MediContract.USERS);


        Bundle bundle=getIntent().getExtras();
        user_name=bundle.getString(MediContract.NAME,user_name);
        phone_number=bundle.getString(MediContract.CONTACT,phone_number);
        profession=bundle.getString(MediContract.USER_TYPE,profession);
        age_String=bundle.getString(MediContract.AGE,age_String);
        gender=bundle.getString(MediContract.GENDER,gender);
        bloodgrp=bundle.getString(MediContract.BLOODGROUP,bloodgrp);
        ad_phno_string=bundle.getString(MediContract.RCONTACT,ad_phno_string);
        address_String=bundle.getString(MediContract.ADDRESS,address_String);


        Map<String,Object> map=new HashMap<>();
        map.put(user_name,"0");
        databaseReference.updateChildren(map);


        databaseReference=databaseReference.child(user_name).child(MediContract.DETAILS);
        Map<String,Object> map2=new HashMap<>();
        map2.put(MediContract.CONTACT,phone_number);
        map2.put(MediContract.USER_TYPE,profession);
        map2.put(MediContract.AGE,age_String);
        map2.put(MediContract.GENDER,gender);
        map2.put(MediContract.BLOODGROUP,bloodgrp);
        map2.put(MediContract.RCONTACT,ad_phno_string);
        map2.put(MediContract.ADDRESS,address_String);

        databaseReference.updateChildren(map2);

        databaseReference=MediContract.firebaseDatabase.getReference().child(MediContract.USERS).child(user_name).child(MediContract.PROFESSION);
        databaseReference.updateChildren(first_aid_map);


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(prevstarted, true);
        editor.putString(String.valueOf(R.string.username),user_name);
        editor.putString(String.valueOf(R.string.age),age_String);
        editor.putString(String.valueOf(R.string.gender),gender);
        editor.putString(String.valueOf(R.string.contact),phone_number);
        editor.putString(String.valueOf(R.string.rcontact),ad_phno_string);
        editor.putString(String.valueOf(R.string.bloodgroup),bloodgrp);
        editor.putString(String.valueOf(R.string.address),address_String);
        editor.putString(String.valueOf(R.string.usertype),profession);
        editor.apply();

        MoveToMainActivity();
        finish();

    }

    public void MoveToMainActivity(){
        Intent i=new Intent(account3.this, MainActivity.class);
        startActivity(i);
    }
}