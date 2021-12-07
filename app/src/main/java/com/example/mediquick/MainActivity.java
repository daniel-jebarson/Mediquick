package com.example.mediquick;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import com.example.mediquick.Contract.MediContract;
import com.example.mediquick.HelpManager.HelpActivity;
import com.example.mediquick.NotificationManager.NotificationsActivity;
import com.example.mediquick.Utils.ImageUtils;
import com.example.mediquick.Utils.NetworkPermissionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    public static ProbAdapter probAdapter;
    private Button alert_button;
    private Intent noti_intent;

    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private String USERNAME;
    double lat1,lon1;

    long time;

    private String alert_clicked;


    private final String TAG=MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediContract.PROB=MediContract.NOTHING;
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this);
        noti_intent=new Intent(MainActivity.this, NotificationsActivity.class);

        alert_button = findViewById(R.id.alert_button);
        gridView=findViewById(R.id.prob_grid_view);
        probAdapter=new ProbAdapter(this,0, ImageUtils.prob_name);
        gridView.setAdapter(probAdapter);

        sharedPreferences=getSharedPreferences(String.valueOf(R.string.userpreference),MODE_PRIVATE);
        USERNAME=sharedPreferences.getString(String.valueOf(R.string.username),"NO NAME");
        alert_clicked=sharedPreferences.getString(String.valueOf(R.string.alert_clicked),"no");


        databaseReference=MediContract.firebaseDatabase.getReference();

        if(alert_clicked.equals("yes")){
            Intent intent=new Intent(MainActivity.this,AfterAlertActivity.class);
            startActivity(intent);
            finish();
        }


        alert_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Long press to Alert",Toast.LENGTH_SHORT).show();
            }
        });

        alert_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!NetworkPermissionManager.checkInternetConnection(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (!NetworkPermissionManager.checklocationAccess(MainActivity.this)) {
                    return false;
                }
                setLocation();

                return false;
            }
        });

    }

    public void TriggerReady(){

        databaseReference.child(MediContract.USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TriggerUsers(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void TriggerUsers(DataSnapshot snapshot){
        Iterator iterator=snapshot.getChildren().iterator();

        while(iterator.hasNext()){
            String name=((DataSnapshot)iterator.next()).getKey();

            if(name.equals(USERNAME)){
                continue;
            }


            databaseReference.child(MediContract.USERS).child(name).child(MediContract.LOCATION).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String,Object> map=new HashMap<>();
                    String t=USERNAME+"|"+time+"|"+MediContract.PROB+"|"+lat1+"|"+lon1;
                    map.put("0",t);

                    if(CheckForDistance(snapshot)) {
                        databaseReference.child(MediContract.USERS).child(name).child(MediContract.TRIGGER).child(databaseReference.push().getKey()).updateChildren(map);
                    }

                    //databaseReference.child(MediContract.USERS).child(name).child(MediContract.TRIGGER).child(databaseReference.push().getKey()).updateChildren(map);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    public boolean CheckForDistance(DataSnapshot snapshot){

        double lat2=0,lon2=0;
        Iterator iterator=snapshot.getChildren().iterator();

        while (iterator.hasNext()){
            lat2= (double) ((DataSnapshot)iterator.next()).getValue();
            lon2= (double) ((DataSnapshot)iterator.next()).getValue();
        }

        if(MediContract.distance(lat1,lat2,lon1,lon2)<=2000) {
            return true;
        }
        else {
            return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_tab,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.bell_button:
                startActivity(noti_intent);
                break;
            case R.id.help_button:
                Intent help_intent=new Intent(MainActivity.this, HelpActivity.class);
                startActivity(help_intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        MediContract.onBackPress(MainActivity.this);
    }



    public void setLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lat1 = location.getLatitude();
                    lon1 = location.getLongitude();
                    time=location.getTime();



                    Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + "Lat:"+lat1+" Lon:"+lon1);

                    databaseReference.child(MediContract.USERS).child(USERNAME).child(MediContract.ALERT).setValue("yes");
                    databaseReference.child(MediContract.USERS).child(USERNAME).child(MediContract.ACCEPTED_USERS).setValue(0);
                    Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + "Trigger ready");
                    TriggerReady();
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString(String.valueOf(R.string.alert_clicked),"yes");
                    editor.apply();


                    Intent intent=new Intent(MainActivity.this,AfterAlertActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        });

    }


    @Override
    protected void onResume() {
        boolean r=false;
        boolean s=false;
        r=NetworkPermissionManager.checkLocationPermission(MainActivity.this);
        if(r){
            s=NetworkPermissionManager.checklocationAccess(MainActivity.this);

        }
        if(r&&s){
            MediContract.startBackgroundService(MainActivity.this);
        }

        super.onResume();
    }

}