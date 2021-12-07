package com.example.mediquick;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mediquick.Contract.MediContract;
import com.example.mediquick.Utils.NetworkPermissionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class AfterAlertActivity extends AppCompatActivity {
    private static final String TAG =AfterAlertActivity.class.getSimpleName();
    private ListView listView;
    private TextView emptyView;
    private AcceptedUsersAdapter acceptedUsersAdapter;
    private SharedPreferences sharedPreferences;
    private String USERNAME;
    private Button cancel_alert_button;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_alert);

        sharedPreferences=getSharedPreferences(String.valueOf(R.string.userpreference),MODE_PRIVATE);
        USERNAME=sharedPreferences.getString(String.valueOf(R.string.username),"NO NAME");

        databaseReference=MediContract.firebaseDatabase.getReference().child(MediContract.USERS).child(USERNAME);


        emptyView=findViewById(R.id.emptyView);
        cancel_alert_button=findViewById(R.id.cancel_alert_button);

        listView=findViewById(R.id.accepted_users_listview);
        listView.setEmptyView(emptyView);

        acceptedUsersAdapter=new AcceptedUsersAdapter(this,0,MediContract.ACCEPTED_USERS_ARRAY);
        listView.setAdapter(acceptedUsersAdapter);


        cancel_alert_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkPermissionManager.checkInternetConnection(AfterAlertActivity.this)) {
                    Toast.makeText(AfterAlertActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                databaseReference.child(MediContract.ALERT).setValue("no");
                databaseReference.child(MediContract.ACCEPTED_USERS).setValue(0);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(String.valueOf(R.string.alert_clicked),"no");
                editor.apply();

                Intent intent=new Intent(AfterAlertActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void checkForAcceptedUsers(){
        valueEventListener=databaseReference.child(MediContract.ACCEPTED_USERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX "+"addValueEventListener");
                updateList(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void updateList(DataSnapshot snapshot){
        MediContract.ACCEPTED_USERS_ARRAY.clear();
        Iterator iterator=snapshot.getChildren().iterator();
        if(!iterator.hasNext()){
            acceptedUsersAdapter.notifyDataSetChanged();
            return;
        }
        while(iterator.hasNext()){

            String name=((DataSnapshot)iterator.next()).getKey();

            MediContract.ACCEPTED_USERS_ARRAY.add(name);
            acceptedUsersAdapter.notifyDataSetChanged();

        }
    }

    @Override
    protected void onResume() {
        checkForAcceptedUsers();
        super.onResume();
    }

    @Override
    protected void onPause() {
        databaseReference.removeEventListener(valueEventListener);
        MediContract.ACCEPTED_USERS_ARRAY.clear();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(AfterAlertActivity.this,"Click 'CANCEL ALERT' button to cancel your alert",Toast.LENGTH_SHORT).show();
    }
}