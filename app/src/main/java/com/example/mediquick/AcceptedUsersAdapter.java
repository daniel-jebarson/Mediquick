package com.example.mediquick;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.mediquick.Contract.MediContract;
import java.util.ArrayList;

public class AcceptedUsersAdapter extends ArrayAdapter<String> {
    public AcceptedUsersAdapter(@NonNull Context context, int resource, ArrayList<String> arrayList) {
        super(context, resource,arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.accepted_users_list_item,parent,false);
        }
        TextView nameView=convertView.findViewById(R.id.name_item);
        nameView.setText(MediContract.ACCEPTED_USERS_ARRAY.get(position));
        return convertView;
    }
}
