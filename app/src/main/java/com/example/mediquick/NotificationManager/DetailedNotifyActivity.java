package com.example.mediquick.NotificationManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mediquick.Contract.MediContract;
import com.example.mediquick.R;
import com.example.mediquick.Utils.AlarmPlayerManager;
import com.example.mediquick.Utils.NetworkPermissionManager;
import com.example.mediquick.Utils.ResponseManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DetailedNotifyActivity extends AppCompatActivity {

    private static final String TAG = DetailedNotifyActivity.class.getSimpleName();
    private TextView name;
    private TextView time;
    private TextView date;
    private  TextView address;
    private TextView age;
    private  TextView bloodgroup;
    private TextView contact;
    private TextView rcontact;
    private TextView gender;
    private TextView user_type;
    private TextView problem;
    private TextView distance;
    private String table_id;


    private  String latitude;
    private String longitude;

    private double lat2, lon2;

    private  FusedLocationProviderClient fusedLocationClient;


    private  Button response_button;
    private  TextView alert_state;
    private  Button show_location_button;
    private  ContentValues contentValues;
    private  SharedPreferences sharedPreferences;
    private  String USERNAME;

    private  DatabaseReference databaseReference;
    private  ValueEventListener valueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_notify);

        sharedPreferences = getSharedPreferences(String.valueOf(R.string.userpreference), MODE_PRIVATE);
        USERNAME = sharedPreferences.getString(String.valueOf(R.string.username), "NO NAME");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setLocation();

        table_id = getIntent().getStringExtra("table_id");
        int alarm = getIntent().getIntExtra("alarm", 0);

        if (alarm == 1) {
            NotificationManager notifyManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notifyManager.cancelAll();
            AlarmPlayerManager.StopMediaPlayer();
        }

        name = findViewById(R.id.name);
        time = findViewById(R.id.time);
        date = findViewById(R.id.date);
        address = findViewById(R.id.address);
        age = findViewById(R.id.age);
        bloodgroup = findViewById(R.id.bloodgroup);
        contact = findViewById(R.id.contact);
        rcontact = findViewById(R.id.rcontact);
        gender = findViewById(R.id.gender);
        user_type = findViewById(R.id.usertype);
        problem = findViewById(R.id.problem);
        distance=findViewById(R.id.distance);
        show_location_button = findViewById(R.id.show_location);

        response_button = findViewById(R.id.response_button);
        alert_state=findViewById(R.id.alert_cancelled_view);

        UpdateUI();

        Cursor cursor = getContentResolver().query(MediContract.FINAL_URI, new String[]{MediContract.RESPONSE, MediContract.ALERT_LIFE}, MediContract.TABLE_ID + "=?", new String[]{table_id}, null);
        if (cursor != null && cursor.moveToFirst()) {
            String res = cursor.getString(cursor.getColumnIndex(MediContract.RESPONSE));
            if (res == null || res.equals("") || res.equals(MediContract.REJECTED)) {
                response_button.setText("ACCEPT");
            } else {
                response_button.setText("REJECT");
            }
        }

        contentValues = new ContentValues();
        response_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkPermissionManager.checkInternetConnection(DetailedNotifyActivity.this)) {
                    Toast.makeText(DetailedNotifyActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response_button.getText().equals("ACCEPT")) {
                    response_button.setText("REJECT");
                    ResponseManager.InformMyAcceptance(USERNAME, name.getText().toString());
                    contentValues.put(MediContract.RESPONSE, MediContract.ACCEPTED);

                } else {
                    response_button.setText("ACCEPT");
                    contentValues.put(MediContract.RESPONSE, MediContract.REJECTED);
                    ResponseManager.InformMyRejectance(USERNAME, name.getText().toString());
                }
                getContentResolver().update(MediContract.FINAL_URI, contentValues, MediContract.TABLE_ID + "=?", new String[]{table_id});


                return;


            }
        });

        databaseReference = MediContract.firebaseDatabase.getReference().child(MediContract.USERS).child(name.getText().toString()).child(MediContract.ALERT);

        if (cursor != null && cursor.moveToFirst()) {
            String life = cursor.getString(cursor.getColumnIndex(MediContract.ALERT_LIFE));
            if (life.equals("yes")) {
                response_button.setVisibility(View.VISIBLE);
                alert_state.setVisibility(View.GONE);

                IsAlertLive();
            } else {
                response_button.setVisibility(View.GONE);
                alert_state.setVisibility(View.VISIBLE);
            }
        }

        show_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkPermissionManager.checkInternetConnection(DetailedNotifyActivity.this)) {
                    Toast.makeText(DetailedNotifyActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!NetworkPermissionManager.checklocationAccess(DetailedNotifyActivity.this)) {
                    return;
                }


                showMap();

            }
        });

    }

    public void UpdateUI() {
        Cursor cursor = getContentResolver().query(MediContract.FINAL_URI, null, MediContract.TABLE_ID + "=?", new String[]{table_id}, null);

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

            name.setText(cursor.getString(cursor.getColumnIndex(MediContract.NAME)));
            time.setText(cursor.getString(cursor.getColumnIndex(MediContract.TIME)));
            date.setText(cursor.getString(cursor.getColumnIndex(MediContract.DATE)));
            address.setText(cursor.getString(cursor.getColumnIndex(MediContract.ADDRESS)));
            age.setText(cursor.getString(cursor.getColumnIndex(MediContract.AGE)));
            gender.setText(cursor.getString(cursor.getColumnIndex(MediContract.GENDER)));
            bloodgroup.setText(cursor.getString(cursor.getColumnIndex(MediContract.BLOODGROUP)));
            contact.setText(cursor.getString(cursor.getColumnIndex(MediContract.CONTACT)));
            rcontact.setText(cursor.getString(cursor.getColumnIndex(MediContract.RCONTACT)));
            user_type.setText(cursor.getString(cursor.getColumnIndex(MediContract.USER_TYPE)));
            problem.setText(cursor.getString(cursor.getColumnIndex(MediContract.PROBLEM)));
            latitude = cursor.getString(cursor.getColumnIndex(MediContract.LATITUDE));
            longitude = cursor.getString(cursor.getColumnIndex(MediContract.LONGITUDE));




        }
        cursor.close();
    }

    public void IsAlertLive() {

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    String v = snapshot.getValue().toString();
                    if (!v.equals("yes")) {
                        contentValues.put(MediContract.ALERT_LIFE, "no");
                        getContentResolver().update(MediContract.FINAL_URI, contentValues, MediContract.TABLE_ID + "=?", new String[]{table_id});
                        response_button.setVisibility(View.GONE);
                        alert_state.setVisibility(View.VISIBLE);
                        Log.d("DetailedNotifyActivity", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX " + "setVisibiliy Gone");
                        databaseReference.removeEventListener(valueEventListener);
                    }
                } catch (Exception e) {
                    finish();
                    Toast.makeText(DetailedNotifyActivity.this,"Problem with finding this account",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void showMap() {
        String origin=lat2+","+lon2;
        String destination = latitude + "," + longitude;


        //String Uri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "Someone" + ")";
        String geoUri="https://www.google.com/maps/dir/?api=1&origin="+origin+"&destination="+destination;
        //String geoUri="https://maps.googleapis.com/maps/api/directions/json?origin=heading=90:"+origin+"&destination=37.773245,-122.469502 &"+"key="+MediContract.GOOGLE_API_KEY;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        startActivity(intent);


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailedNotifyActivity.this, NotificationsActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        NetworkPermissionManager.checkLocationPermission(DetailedNotifyActivity.this);
        super.onResume();
    }

    private void setLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        NetworkPermissionManager.checklocationAccess(this);



        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lat2 = location.getLatitude();
                    lon2 = location.getLongitude();
                    Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + "Lat:"+lat2+" Lon:"+lon2);
                    double d=(MediContract.distance(Double.parseDouble(latitude),lat2,Double.parseDouble(longitude),lon2));
                    int dis=(int)d;
                    String s;
                    if(dis>999){
                        s=String.valueOf(dis/1000)+" Kilo metre";
                    }
                    else{
                        s=String.valueOf(dis)+" metre";
                    }
                    distance.setText(s+"\naway");
                }

            }
        });

    }

}