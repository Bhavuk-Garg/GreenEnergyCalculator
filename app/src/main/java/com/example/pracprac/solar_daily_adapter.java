package com.example.pracprac;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class solar_daily_adapter extends ArrayAdapter<data_class> {
    public solar_daily_adapter(Context context, List<data_class> energydata){
        super(context,0,energydata);

    }


    @Override
    public View getView(int position, View convertview, ViewGroup parent){
       View listitemview=convertview;
       if (listitemview==null){
           listitemview= LayoutInflater.from(getContext()).inflate(R.layout.listviewsolardaily,parent,false);
        }


        data_class currentsolardata= getItem(position);

        TextView dateview= (TextView)listitemview.findViewById(R.id.date);
        dateview.setText(currentsolardata.getDate());

        TextView energyview= (TextView)listitemview.findViewById(R.id.energyvalue);
        energyview.setText(currentsolardata.getValue());

        return listitemview;


    }
}
