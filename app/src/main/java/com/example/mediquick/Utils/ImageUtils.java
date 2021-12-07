package com.example.mediquick.Utils;

import com.example.mediquick.R;

import java.util.ArrayList;

public class ImageUtils {

    public static final ArrayList<Integer> prob_name = new ArrayList<Integer>() {{
        add(R.string.HEART_ATTACK_STRING);
        add(R.string.SNAKE_BITE_STRING);
        add(R.string.CONVULSIONS_STRING);
        add(R.string.DROWNED_STRING);
        add(R.string.BLEEDING_STRING);
        add(R.string.BURNS_STRING);
        add(R.string.TRAUMATIC_HEAD_INJURY_STRING);
        add(R.string.STROKE_STRING);
    }};

    public static final ArrayList<Integer> prob_image = new ArrayList<Integer>() {{
        add(R.drawable.heartattack_image);
        add(R.drawable.snakebite_image);
        add(R.drawable.convulsions_image);
        add(R.drawable.drowned_image);
        add(R.drawable.bleeing_image);
        add(R.drawable.burn_image);
        add(R.drawable.traumaticheadinjury_image);
        add(R.drawable.stroke_image);
    }};

    public static ArrayList<Integer> prob_color = new ArrayList<Integer>() {{
        add(R.color.white);
        add(R.color.white);
        add(R.color.white);
        add(R.color.white);
        add(R.color.white);
        add(R.color.white);
        add(R.color.white);
        add(R.color.white);
    }};

    public static Integer getProb_name(int i){
        return prob_name.get(i);
    }

    public static Integer getProb_image(int i){
        return prob_image.get(i);
    }

    public static Integer getProb_color(int i){
        return prob_color.get(i);
    }


}