package com.example.mediquick.Utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.mediquick.R;

public class AlarmPlayerManager {

    static MediaPlayer tone;


    public static void StartMediaPlayer(Context context){
        tone=MediaPlayer.create(context, R.raw.emergency_tone);
        tone.start();
    }

    public static Boolean CheckIsPlaying(){
        if(tone==null){
            return false;
        }
        return tone.isPlaying();
    }

    public static void StopMediaPlayer(){
        tone.stop();
    }
}
