package com.example.mediquick.NotificationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mediquick.Contract.MediContract;
import com.example.mediquick.MainActivity;
import com.example.mediquick.R;

public class NotificationsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView listView;
    private NotifyArrayAdapter notifyArrayAdapter;
    private Cursor cursor;
    private TextView emptyView;
    private final String TAG=NotificationsActivity.class.getSimpleName();



    private  Intent detail_intent;
    public static Activity fa=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        fa=this;

        emptyView=findViewById(R.id.emptyView);
        listView=findViewById(R.id.notify_list);
        listView.setEmptyView(emptyView);
        notifyArrayAdapter=new NotifyArrayAdapter(this,cursor);
        listView.setAdapter(notifyArrayAdapter);

        LoaderManager.getInstance(this).initLoader(1,null,this);

        detail_intent=new Intent(NotificationsActivity.this,DetailedNotifyActivity.class);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                RelativeLayout relativeLayout=view.findViewById(R.id.item_relative_layout);
                TextView textView=relativeLayout.findViewById(R.id.table_id_item);
                String table_id=textView.getText().toString();

                detail_intent.putExtra("table_id",table_id);
                detail_intent.putExtra("alarm",0);
                startActivity(detail_intent);


            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+ " onCreateLoader called");
        return new CursorLoader(this, MediContract.FINAL_URI,null,null,null,MediContract.TABLE_ID+" DESC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+ " onLoadFinished called");
        notifyArrayAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+ " onLoaderReset called");
        notifyArrayAdapter.changeCursor(null);
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(NotificationsActivity.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notifications_activity_tab,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_button:
                if(notifyArrayAdapter==null||notifyArrayAdapter.getCount()==0){
                    break;
                }
                deleteBuildAlert();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteBuildAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(NotificationsActivity.this);
        builder.setMessage("Delete All?")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        getContentResolver().delete(MediContract.FINAL_URI,null,null);
                    }
                })
                .setNegativeButton("Don't", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

}