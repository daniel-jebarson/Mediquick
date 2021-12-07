package com.example.mediquick.Database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mediquick.Contract.MediContract;

public class MediProvider extends ContentProvider {
    private DataBaseManager dataBaseManager;
    private static final UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);

    private static final int USERS_TABLE=100;
    private static final int PROB_TABLE=101;

    static {
        matcher.addURI(MediContract.AUTHORITY,MediContract.TABLE_NAME, USERS_TABLE);
        matcher.addURI(MediContract.AUTHORITY, MediContract.PROB_TABLE_NAME,PROB_TABLE);
    }

    @Override
    public boolean onCreate() {
        dataBaseManager=new DataBaseManager(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db=dataBaseManager.getReadableDatabase();
        Cursor cursor;
        int match=matcher.match(uri);
        switch (match){
            case USERS_TABLE:
                cursor=db.query(MediContract.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PROB_TABLE:
                cursor=db.query(MediContract.PROB_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                cursor=db.query(MediContract.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db=dataBaseManager.getWritableDatabase();
        long id=db.insert(MediContract.TABLE_NAME,null,values);

        getContext().getContentResolver().notifyChange(uri,null);

        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db=dataBaseManager.getWritableDatabase();
        int n=0;
        n=db.delete(MediContract.TABLE_NAME,selection,selectionArgs);
        if(n!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return n;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int n=0;
        SQLiteDatabase db=dataBaseManager.getWritableDatabase();

        n=db.update(MediContract.TABLE_NAME,values,selection,selectionArgs);

        if(n!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return n;
    }
}
