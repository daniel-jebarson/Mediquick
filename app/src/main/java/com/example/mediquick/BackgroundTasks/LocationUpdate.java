package com.example.mediquick.BackgroundTasks;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.mediquick.Contract.MediContract;
import com.example.mediquick.NotificationManager.DetailedNotifyActivity;
import com.example.mediquick.R;
import com.example.mediquick.Utils.AlarmPlayerManager;
import com.example.mediquick.Utils.AlarmReceiver;
import com.example.mediquick.Utils.AlertInputManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LocationUpdate extends Service {
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference databaseReference;
    private LocationRequest locationRequest;
    private  LocationCallback locationCallback;
    private  SharedPreferences sharedPreferences;
    private String USERNAME;


    private String NOTIFICATION_CHANNEL_ID = "com.example.mediquick";
    private  String channelName = "My Background Service";

    private static final String TAG = LocationUpdate.class.getSimpleName();

    private static final String CHANNEL_ID = "100";
    private static final String CHANNEL_ID_ALERT = "101";


    private double lat1;
    private  double lon1;

    private  final long secondsInMilli = 1000;
    private  final long minutesInMilli = secondsInMilli * 60;
    private  final long hoursInMilli = minutesInMilli * 60;
    private  final long daysInMilli = hoursInMilli * 24;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startMyOwnForeground();
                Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + " onStartCommand Oreo executed");
            }
        } catch (Exception e) {
            Log.d(" Error --->> ", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + e.getMessage());
        }

        databaseReference = MediContract.firebaseDatabase.getReference();
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.userpreference), MODE_PRIVATE);
        USERNAME = sharedPreferences.getString(String.valueOf(R.string.username), "NO NAME");

       UpdateLocation();
        CheckForTrigger();


        return START_STICKY;

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + " onTaskRemoved()");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            stopForeground(true);
//        }

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + " onDestory()");


        super.onDestroy();
    }

    public void UpdateLocation() {
        Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + " UpdateLocation called");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + "onLocationResult called");

                if (locationResult != null) {

                    sharedPreferences = getSharedPreferences(String.valueOf(R.string.userpreference), MODE_PRIVATE);
                    USERNAME = sharedPreferences.getString(String.valueOf(R.string.username), "NO NAME");

                    for (Location location : locationResult.getLocations()) {

                        lat1 = location.getLatitude();
                        lon1 = location.getLongitude();



                        Map<String, Object> locationmap = new HashMap<>();
                        locationmap.put(MediContract.LATITUDE, lat1);
                        locationmap.put(MediContract.LONGITUDE, lon1);

                        MediContract.firebaseDatabase.getReference().child(MediContract.USERS).child(USERNAME).child(MediContract.LOCATION).updateChildren(locationmap);

                    }
                }
            }

        };
        Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + " UpdateLocation executed");
        startLocationUpdates();

    }

    private void startLocationUpdates() {
        Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + " startLocationUpdates called");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            onDestroy();
            return;
        }

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000 * 300);
        locationRequest.setFastestInterval(1000 * 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + " startLocationUpdates executed");

    }




    private void startMyOwnForeground() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createNotificationChannel();

            NotificationChannel chan = null;

            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("MediQuick is updating location")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);

        }
    }

    private NotificationChannel createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            return channel;
        }
        return null;
    }

    public void CheckForTrigger() {

        databaseReference.child(MediContract.USERS).child(USERNAME).child(MediContract.TRIGGER).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + " Trigger Detected");
                String input = null;

                Iterator iterator=snapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    input=((DataSnapshot)iterator.next()).getValue().toString();

                    if (input != null && !input.isEmpty()) {

                        AlertInputManager alertInputManager = new AlertInputManager(getApplicationContext());

                        String s=alertInputManager.parseInputAlert(input);
                        long incTime=Long.valueOf(s);
                        long myTime=Calendar.getInstance().getTimeInMillis();

                        isTimeExceeded(incTime,myTime);
                        Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +incTime+" "+myTime);


                        if(!AlarmPlayerManager.CheckIsPlaying()&&!isTimeExceeded(incTime,myTime)){
                            StartAlarm();
                        }


                    }

                }

                databaseReference.child(MediContract.USERS).child(USERNAME).child(MediContract.TRIGGER).setValue("0");


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void StartAlarm() {

        Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + " StartAlarm() Called");

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
        wl.acquire();


        Intent intent = new Intent(LocationUpdate.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getBaseContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1 * 1000), pendingIntent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setAlarmNotification();
            }
        },1000);



    }

    public void setAlarmNotification(){
        Cursor cursor = getContentResolver().query(MediContract.FINAL_URI, new String[]{MediContract.TABLE_ID}, null, null, MediContract.TABLE_ID+" DESC");
        cursor.moveToFirst();
        String table_id = cursor.getString(cursor.getColumnIndex(MediContract.TABLE_ID));
        cursor.close();

        Intent fullScreenIntent = new Intent(this, DetailedNotifyActivity.class);
        fullScreenIntent.putExtra("table_id", table_id);
        fullScreenIntent.putExtra("alarm", 1);

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 1,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        createNotificationChannelForAlert();


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID_ALERT)
                        .setSmallIcon(R.drawable.bell_icon)
                        .setContentTitle("Incoming Alert")
                        .setContentText("Someone")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setFullScreenIntent(fullScreenPendingIntent, true)
                        .setOngoing(true);

        Notification incomingCallNotification = notificationBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(3,incomingCallNotification);

    }

    public void createNotificationChannelForAlert() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name_alert);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = null;

            channel = new NotificationChannel(CHANNEL_ID_ALERT, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


    }

    public Boolean isTimeExceeded(long incTime,long myTime){
        long different=myTime-incTime;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

//        long elapsedSeconds = different / secondsInMilli;

        Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + elapsedDays+" "+elapsedHours+" "+elapsedMinutes);

        if(elapsedDays!=0||elapsedHours!=0){
            return true;
        }
        if(elapsedMinutes>MediContract.EXCEEDING_TIME_LIMIT||elapsedMinutes<-MediContract.EXCEEDING_TIME_LIMIT){
            return true;
        }
        return false;

    }



}