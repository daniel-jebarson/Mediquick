package com.example.mediquick.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;
public class AlarmReceiver extends BroadcastReceiver {
    MediaPlayer tone;

    public AlarmReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReciever","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+ " AlarmReceiver() Called");

        Toast.makeText(context,"MediQuick- someone needs your help",Toast.LENGTH_LONG).show();
        AlarmPlayerManager.StartMediaPlayer(context);


    }
}
