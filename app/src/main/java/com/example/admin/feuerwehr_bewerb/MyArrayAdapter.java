package com.example.admin.feuerwehr_bewerb;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Admin on 06.05.2016.
 */
public class MyArrayAdapter extends ArrayAdapter<Lauf>{

    Context context;
    int layoutResourceId;
    ArrayList zeitenliste;

    public MyArrayAdapter(Context context, int layoutResourceId, ArrayList zeitenliste) {
        super(context, layoutResourceId, zeitenliste);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.zeitenliste = zeitenliste;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        TimeHolder holder = null;

        if(row == null){

            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TimeHolder();
            holder.datum = (TextView)row.findViewById(R.id.textView_adapter_datum);
            holder.taetigkeit = (TextView)row.findViewById(R.id.textView_adapter_taetigkeit);
            holder.aufstellung = (TextView)row.findViewById(R.id.textView_adapter_aufstellung);
            holder.zeit = (TextView)row.findViewById(R.id.textView_adapter_zeit);

            row.setTag(holder);
        }else{
            holder = (TimeHolder)row.getTag();
        }


        System.out.println("Position: " + position);
        System.out.println("Zeitenliste: " + zeitenliste.size());

        Lauf lauf = (Lauf) zeitenliste.get(position);
        holder.datum.setText(lauf.datum);
        holder.taetigkeit.setText(lauf.taetigkeit);
        holder.aufstellung.setText(lauf.aufstellung);
        holder.zeit.setText(lauf.zeit);

        return row;
    }

    static class TimeHolder
    {
        TextView datum;
        TextView taetigkeit;
        TextView aufstellung;
        TextView zeit;
    }



}

