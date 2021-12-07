package com.example.mediquick.Utils;

import com.example.mediquick.Contract.MediContract;
import com.google.firebase.database.DatabaseReference;
import java.util.HashMap;
import java.util.Map;
public class ResponseManager {

    public static void InformMyAcceptance(String user_name,String name){
        DatabaseReference databaseReference= MediContract.firebaseDatabase.getReference().child(MediContract.USERS).child(name).child(MediContract.ACCEPTED_USERS);
        Map<String,Object> map=new HashMap<>();
        map.put(user_name,"0");
        databaseReference.updateChildren(map);
    }
    public static void InformMyRejectance(String user_name,String name){
        DatabaseReference databaseReference= MediContract.firebaseDatabase.getReference().child(MediContract.USERS).child(name).child(MediContract.ACCEPTED_USERS);
        databaseReference.child(user_name).removeValue();
    }


}
