package com.example.mediquick.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.mediquick.Contract.MediContract;

public class DataBaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="medi.db";

    private String SQL_STATEMENT_FOR_CREATING_A_TABLE;

    private static final int DATABASE_VERSION=1;

    public DataBaseManager(@Nullable Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        SQL_STATEMENT_FOR_CREATING_A_TABLE="CREATE TABLE "+MediContract.TABLE_NAME+"("
                +MediContract.TABLE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +MediContract.NAME+" TEXT NOT NULL,"
                +MediContract.ADDRESS+" TEXT,"
                +MediContract.AGE+" TEXT,"
                +MediContract.GENDER+" TEXT,"
                +MediContract.CONTACT+" TEXT,"
                +MediContract.RCONTACT+" TEXT,"
                +MediContract.BLOODGROUP+" TEXT,"
                +MediContract.TIME+" TEXT,"
                +MediContract.DATE+" TEXT,"
                +MediContract.RESPONSE+" TEXT,"
                +MediContract.USER_TYPE+" TEXT,"
                +MediContract.ALERT_LIFE+" TEXT,"
                +MediContract.ADDITIONAL_INFO+" TEXT,"
                +MediContract.LATITUDE+" TEXT,"
                +MediContract.LONGITUDE+" TEXT,"
                +MediContract.PROBLEM+" TEXT);";

        db.execSQL(SQL_STATEMENT_FOR_CREATING_A_TABLE);

        SQL_STATEMENT_FOR_CREATING_A_TABLE="CREATE TABLE "+MediContract.PROB_TABLE_NAME+"("
                +MediContract.PROB_TABLE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +MediContract.PROB_NAME+" TEXT NOT NULL,"
                +MediContract.PROB_IMAGE+" BLOB);";

        db.execSQL(SQL_STATEMENT_FOR_CREATING_A_TABLE);

//        String SQL_PROB_INSERT_STATEMENT="INSERT INTO "+MediContract.PROB_TABLE_NAME+"("+MediContract.PROB_NAME+") VALUES "
//                +"(\'"+MediContract.HEART_ATTACK_STRING+"\'),"
//                +"(\'"+MediContract.SNAKE_BITE_STRING+"\'),"
//                +"(\'"+MediContract.BLEEDING_STRING+"\'),"
//                +"(\'"+MediContract.DROWNED_STRING+"\'),"
//                +"(\'"+MediContract.STROKE_STRING+"\'),"
//                +"(\'"+MediContract.CONVULSIONS_STRING+"\'),"
//                +"(\'"+MediContract.BURNS_STRING+"\'),"
//                +"(\'"+MediContract.TRAUMATIC_HEAD_INJURY_STRING+"\');";
//
//        db.execSQL(SQL_PROB_INSERT_STATEMENT);

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        return;
    }



}
