package com.example.mediquick;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import com.example.mediquick.Contract.MediContract;
import com.example.mediquick.Utils.ImageUtils;
import java.util.ArrayList;


public class ProbAdapter extends ArrayAdapter<Integer> {
    private final String TAG= ProbAdapter.class.getSimpleName();


    public ProbAdapter(@NonNull Context context, int resource, ArrayList<Integer> arrayList) {
        super(context, resource,arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView=LayoutInflater.from(getContext()).inflate(R.layout.prob_list_item,parent,false);
        }

        Log.d(TAG,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX get View running");
        TextView textView=convertView.findViewById(R.id.prob_name);
        textView.setText(ImageUtils.getProb_name(position));

        ImageView imageView=convertView.findViewById(R.id.prob_image);
        imageView.setImageResource(ImageUtils.getProb_image(position));

        LinearLayout linearLayout=convertView.findViewById(R.id.prob_linearlayout);


        CardView cardView=linearLayout.findViewById(R.id.card_view);

        LinearLayout linearLayout1=cardView.findViewById(R.id.inside_card);
        linearLayout1.setBackgroundResource(ImageUtils.getProb_color(position));

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView2=linearLayout.findViewById(R.id.prob_name);
                String string=textView2.getText().toString();

                if(!string.equals(MediContract.NOTHING)){
                    for(int i=0;i<ImageUtils.prob_color.size();i++){
                        ImageUtils.prob_color.set(i,R.color.white);
                    }
                }

                if(string.equals(MediContract.PROB)){
                    MediContract.PROB=MediContract.NOTHING;
                }
                else{
                    MediContract.PROB=string;
                    ImageUtils.prob_color.set(position,R.color.light_red);
                }
                MainActivity.probAdapter.notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
