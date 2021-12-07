package com.example.mediquick.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.example.mediquick.Contract.MediContract;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.Iterator;

public class AlertInputManager{
    private String name;
    private String time;
    private String date;
    private String address;
    private String age;
    private String bloodgroup;
    private String contact;
    private String gender;
    private String rcontact;
    private String user_type;
    private String problem;
    private String lat;
    private String lon;

    private Context context;

    private static final String TAG=AlertInputManager.class.getSimpleName();


    public AlertInputManager(Context c) {
        context=c;
    }

    DatabaseReference databaseReference;

    public String parseInputAlert(String input){
        int len=input.length();
        String temp="";
        int n=0;
        for(int i=0;i<len;i++){

            if(input.charAt(i)=='|'){
                n++;
                switch(n){
                    case 1:
                        name=temp;
                        break;
                    case 2:
                        time=temp;
                        break;
                    case 3:
                        problem=temp;
                        break;
                    case 4:
                        lat=temp;
                        break;
                    default:
                        break;
                }
                temp="";
            }
            else{
                temp+=input.charAt(i);
                if(i==len-1){
                    lon=temp;
                    temp="";
                }
            }

        }

        collectUserData();
        return time;




    }

    public void collectUserData(){
        databaseReference= MediContract.firebaseDatabase.getReference().child(MediContract.USERS).child(name).child(MediContract.DETAILS);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getDetails(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void getDetails(DataSnapshot snapshot){

        Iterator iterator=snapshot.getChildren().iterator();
        while(iterator.hasNext()){
            address=((DataSnapshot)iterator.next()).getValue().toString();
            age=((DataSnapshot)iterator.next()).getValue().toString();
            bloodgroup=((DataSnapshot)iterator.next()).getValue().toString();
            contact=((DataSnapshot)iterator.next()).getValue().toString();
            gender=((DataSnapshot)iterator.next()).getValue().toString();
            rcontact=((DataSnapshot)iterator.next()).getValue().toString();
            user_type=((DataSnapshot)iterator.next()).getValue().toString();
        }
        storeIntoDatabase();

    }



    public void storeIntoDatabase(){
        date=MediContract.getDate(Long.valueOf(time));
        time=MediContract.getTime(Long.valueOf(time));

        ContentValues contentValues=new ContentValues();

        contentValues.put(MediContract.NAME,name);
        contentValues.put(MediContract.TIME,time);
        contentValues.put(MediContract.DATE,date);
        contentValues.put(MediContract.ADDRESS,address);
        contentValues.put(MediContract.AGE,age);
        contentValues.put(MediContract.BLOODGROUP,bloodgroup);
        contentValues.put(MediContract.CONTACT,contact);
        contentValues.put(MediContract.RCONTACT,rcontact);
        contentValues.put(MediContract.GENDER,gender);
        contentValues.put(MediContract.USER_TYPE,user_type);
        contentValues.put(MediContract.ALERT_LIFE,"yes");
        contentValues.put(MediContract.PROBLEM,problem);
        contentValues.put(MediContract.LATITUDE,lat);
        contentValues.put(MediContract.LONGITUDE,lon);


        try {
            context.getContentResolver().insert(MediContract.FINAL_URI,contentValues);
            Log.d(TAG,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+ " Stored Into the Database");



        } catch (Exception e) {
            Log.d(TAG,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+ " Error while storing Into the Database");
            e.printStackTrace();
        }

    }

}
