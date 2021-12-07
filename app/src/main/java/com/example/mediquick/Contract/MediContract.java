package com.example.mediquick.Contract;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.example.mediquick.BackgroundTasks.LocationUpdate;
import com.example.mediquick.NotificationManager.NotificationsActivity;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MediContract {
    public static final FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();

    public static final String USERS="users";
    public static final String LOCATION="location";
    public static final String LATITUDE="latitude";
    public static final String LONGITUDE="longitude";
    public static final String TRIGGER="trigger";

    public static final String DETAILS="details";
    public static final String NAME="name";
    public static final String AGE="age";
    public static final String GENDER="gender";
    public static final String BLOODGROUP="bloodgroup";
    public static final String CONTACT ="contact";
    public static final String RCONTACT="rcontact";
    public static final String ADDRESS="address";
    public static final String USER_TYPE="user_type";
    public static final String NOTHING="Not Mentioned";
    public static final String PROFESSION="profession";
    public static String PROB=NOTHING;




    public static final String TABLE_NAME="medi";
    public static final String TABLE_ID= BaseColumns._ID;
    public static final String TIME="time";
    public static final String DATE="date";
    public static final String RESPONSE="response";
    public static final String ACCEPTED="accepted";
    public static final String REJECTED="rejected";
    public static final String MISSED="missed";
    public static final String ADDITIONAL_INFO="additional_info";
    public static final String PROBLEM="problem";

    public static final String ALERT="alert";
    public static final String ACCEPTED_USERS="accepted_users";
    public static final String ALERT_LIFE="alert_life";

    public static final String PROB_TABLE_NAME="prob";
    public static final String PROB_TABLE_ID= BaseColumns._ID;
    public static final String PROB_NAME="name";
    public static final String PROB_IMAGE="image";

    public static final ArrayList<String> ACCEPTED_USERS_ARRAY=new ArrayList<String>();

    public static final String AUTHORITY="com.example.mediquick";
    public static final Uri BASE_URI=Uri.parse("content://"+AUTHORITY);
    public static final Uri FINAL_URI= Uri.withAppendedPath(BASE_URI,TABLE_NAME);

    public static final long EXCEEDING_TIME_LIMIT=10;




    public static double distance(double lat1, double lat2, double lon1, double lon2)
    {

        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2),2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371;

        return(c*r*1000);
    }

    public static void startBackgroundService(Context context) {
        if (isBackgroundServiceRunning(context)) {
            Log.d("SplashActivity", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX " + "Yes bg running");

        } else {
            Log.d("SplashActivity", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX " + "NO. bg is not running");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("SplashActivity", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + " Foreground Service called from MainActivity");
                context.startForegroundService(new Intent(context, LocationUpdate.class));

            } else {
                context.startService(new Intent(context, LocationUpdate.class));
            }
        }

    }

    public static boolean isBackgroundServiceRunning(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationUpdate.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void onBackPress(Context context) {
        Activity activity=(Activity) context;


        new AlertDialog.Builder(context)
                .setTitle("Exit")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.finish();
                        if(NotificationsActivity.fa!=null){
                            NotificationsActivity.fa.finish();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }

    public static String getTime(long time)
    {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        return sdf.format(d);
    }


    public static String getDate(long time)
    {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(d);
    }








}
