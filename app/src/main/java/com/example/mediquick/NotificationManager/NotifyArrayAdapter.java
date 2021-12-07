package com.example.mediquick.NotificationManager;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.mediquick.Contract.MediContract;
import com.example.mediquick.R;

public class NotifyArrayAdapter extends CursorAdapter {
    private final String TAG=NotifyArrayAdapter.class.getSimpleName();
    private TextView nameView;
    private TextView timeView;
    private TextView tableidView;

    public NotifyArrayAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d(TAG,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+ " Notify newView");
        return LayoutInflater.from(context).inflate(R.layout.notify_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        nameView=view.findViewById(R.id.name_item);
        nameView.setText(cursor.getString(cursor.getColumnIndex(MediContract.NAME)));

        timeView=view.findViewById(R.id.time_item);
        timeView.setText(cursor.getString(cursor.getColumnIndex(MediContract.TIME)));

        tableidView=view.findViewById(R.id.table_id_item);
        tableidView.setText(cursor.getString(cursor.getColumnIndex(MediContract.TABLE_ID)));
        Log.d(TAG,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+ " bindView executed");
    }
}
