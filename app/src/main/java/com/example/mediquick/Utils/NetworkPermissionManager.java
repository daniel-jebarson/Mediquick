package com.example.mediquick.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import com.example.mediquick.R;


public class NetworkPermissionManager {

    private static final String TAG = NetworkPermissionManager.class.getSimpleName();
    private static AlertDialog builder;


    public static boolean checkInternetConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        NetworkCapabilities networkCapabilities=null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            networkCapabilities=connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        }

        int downspeed=0;
        int upspeed=0;
        if(networkCapabilities!=null){
            downspeed=networkCapabilities.getLinkDownstreamBandwidthKbps();
            upspeed=networkCapabilities.getLinkUpstreamBandwidthKbps();
        }

        if ( (networkInfo != null) && (networkInfo.isConnected()) && (downspeed>0 && upspeed>0)){
            Log.v(TAG,"internet XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            return true;
        } else {
            Log.v(TAG,"no internet XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            return  false;
        }

    }
    public static boolean checkLocationPermission(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return checkBackgroundLocationPermission(context);
        }
        else{
            return checkForegroundLocationPermission(context);
        }
    }

    public static boolean checkForegroundLocationPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        View view= LayoutInflater.from(context).inflate(R.layout.alert_layout,null);
        ImageView imageView2;
        ImageView imageView3;
        Button buttonView;
        imageView2 = view.findViewById(R.id.denied_image);
        imageView2.setImageResource(R.drawable.denied_image);
        imageView2.setVisibility(View.GONE);
        imageView3 = view.findViewById(R.id.allow_image);
        imageView3.setImageResource(R.drawable.location_permission_android10below);

        buttonView=view.findViewById(R.id.button_view);
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.getPackageName(), null));
                ((Activity) context).startActivityForResult(intent, 0);
                builder.dismiss();
            }
        });


        builder=new AlertDialog.Builder(context)
                .setTitle("Location Permission Needed")
                .setMessage("Please select 'Allow' to continue")
                .setCancelable(false)
                .setView(view)
                .create();
        builder.show();
        return false;

    }
    public static boolean checkBackgroundLocationPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }


        View view= LayoutInflater.from(context).inflate(R.layout.alert_layout,null);
        ImageView imageView2;
        ImageView imageView3;
        Button buttonView;

        imageView2 = view.findViewById(R.id.denied_image);
        imageView2.setImageResource(R.drawable.denied_image);
        imageView2.setVisibility(View.VISIBLE);
        imageView3 = view.findViewById(R.id.allow_image);
        imageView3.setImageResource(R.drawable.location_permission_android10above);
        buttonView=view.findViewById(R.id.button_view);
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.getPackageName(), null));
                ((Activity) context).startActivityForResult(intent, 0);
                builder.dismiss();
            }
        });


        builder=new AlertDialog.Builder(context,R.style.MyDialogTheme)
                .setTitle("Location Permission Needed")
                .setMessage("Please select 'Allow all time' to continue")
                .setView(view)
                .setCancelable(false)
                .create();

        builder.show();


        return false;
    }

    public static boolean checklocationAccess(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(context);
            return false;

        }
        return true;
    }

    private static void buildAlertMessageNoGps(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.alert_gps_layout,null);
        builder.setMessage("Enable GPS to continue")
                .setCancelable(false)
                .setView(view)
                .setPositiveButton("ENABLE", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
