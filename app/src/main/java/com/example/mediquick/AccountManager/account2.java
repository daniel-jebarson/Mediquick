package com.example.mediquick.AccountManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mediquick.Contract.MediContract;
import com.example.mediquick.MainActivity;
import com.example.mediquick.R;
import com.example.mediquick.Utils.NetworkPermissionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class account2 extends AppCompatActivity {

    private EditText edit_user_name;
    private Button finish_button;
    private Spinner prof_spinner;
    private Spinner gender_spinner;
    private Spinner bloodgrp_spinner;
    private String profession;
    private String gender;
    private String bloodgrp;
    private String phone_number;
    private String  prevstarted="";
    private EditText age;
    private EditText ad_phno;
    private EditText address;

    private CheckBox heart_attack_check_box;
    private  CheckBox snake_bite_check_box;
    private CheckBox bleeding_check_box;

    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;

    private String user_name;
    private  String age_String;
    private  String ad_phno_string;
    private  String address_String;

    private  Map<String,Object> first_aid_map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account2);

        if(getIntent().getExtras()!=null){
            phone_number=getIntent().getExtras().getString("phone_number");
        }

        edit_user_name=findViewById(R.id.user_name);
        finish_button=findViewById(R.id.finish_button);
        prof_spinner=findViewById(R.id.prof_spinner);
        gender_spinner=findViewById(R.id.gender_spinner);
        bloodgrp_spinner=findViewById(R.id.bloodgrp_spinner);
        age=findViewById(R.id.age);
        ad_phno=findViewById(R.id.rcontact);
        address=findViewById(R.id.address);

        sharedPreferences=getSharedPreferences(String.valueOf(R.string.userpreference),MODE_PRIVATE);

        heart_attack_check_box=findViewById(R.id.heart_attack_check_box);
        bleeding_check_box=findViewById(R.id.bleeding_check_box);
        snake_bite_check_box=findViewById(R.id.snake_Bite_check_box);


        setUpProfSpinner();
        setUpGenderSpinner();
        setUpBloodgrpSpinner();


        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkPermissionManager.checkInternetConnection(account2.this)) {
                    Toast.makeText(account2.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                    String user_name=edit_user_name.getText().toString().trim();
                    checkForExistingNames(user_name);

            }
        });
    }



    public void setUpProfSpinner(){

        ArrayAdapter spinnerAdapter=ArrayAdapter.createFromResource(this,R.array.prof_spinner_array,android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        prof_spinner.setAdapter(spinnerAdapter);

        prof_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if(!selection.isEmpty()){
                    profession=selection;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void setUpGenderSpinner(){

        ArrayAdapter spinnerAdapter=ArrayAdapter.createFromResource(this,R.array.gender_spinner_array,android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        gender_spinner.setAdapter(spinnerAdapter);

        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if(!selection.isEmpty()){
                    gender=selection;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void setUpBloodgrpSpinner(){

        ArrayAdapter spinnerAdapter=ArrayAdapter.createFromResource(this,R.array.bloodgroup_spinner_array,android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        bloodgrp_spinner.setAdapter(spinnerAdapter);

        bloodgrp_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if(!selection.isEmpty()){
                    bloodgrp=selection;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


    public void updateDatabase(){
        user_name=edit_user_name.getText().toString().trim();
        age_String=age.getText().toString().trim();
        ad_phno_string=ad_phno.getText().toString().trim();
        address_String=address.getText().toString().trim();

        if(user_name.isEmpty()||age_String.isEmpty()||ad_phno_string.isEmpty()||address_String.isEmpty()||profession.isEmpty()||gender.isEmpty()||bloodgrp.isEmpty()){
            Toast.makeText(account2.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!profession.equals("Normal People")){
            Intent nextIntent=new Intent(account2.this, account3.class);

            Bundle bundle=new Bundle();

            bundle.putString(MediContract.NAME,user_name);
            bundle.putString(MediContract.CONTACT,phone_number);
            bundle.putString(MediContract.USER_TYPE,profession);
            bundle.putString(MediContract.AGE,age_String);
            bundle.putString(MediContract.GENDER,gender);
            bundle.putString(MediContract.BLOODGROUP,bloodgrp);
            bundle.putString(MediContract.RCONTACT,ad_phno_string);
            bundle.putString(MediContract.ADDRESS,address_String);

            nextIntent.putExtras(bundle);
            startActivity(nextIntent);
        }
        else{
            ifNormalPeople();
        }

    }

    public void ifNormalPeople(){
        databaseReference=MediContract.firebaseDatabase.getReference().child(MediContract.USERS);

        Map<String,Object> map=new HashMap<>();
        map.put(user_name,"0");
        databaseReference.updateChildren(map);

        databaseReference.child(user_name).child(MediContract.PROFESSION).setValue("0");
        
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


    public void checkForExistingNames(String name) {

        DatabaseReference databaseReference = MediContract.firebaseDatabase.getReference().child(MediContract.USERS);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(name)){
                    edit_user_name.setText("");
                    Toast.makeText(account2.this, "This username is already taken", Toast.LENGTH_LONG).show();
                }
                else {
                    updateDatabase();
                }
//                Iterator i = snapshot.getChildren().iterator();
//                String s;
//                Boolean b = false;
//                while (i.hasNext()) {
//
//                    s = ((DataSnapshot) i.next()).getKey().toString();
//                    if (name.equals(s)) {
//                        edit_user_name.setText("");
//                        Toast.makeText(account2.this, "This username is already taken", Toast.LENGTH_LONG).show();
//                        b = true;
//                        break;
//                    }
//                }
//
//                if (!b) {
//                    updateDatabase();
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    public void MoveToMainActivity(){
        Intent i=new Intent(account2.this, MainActivity.class);
        startActivity(i);

    }

}