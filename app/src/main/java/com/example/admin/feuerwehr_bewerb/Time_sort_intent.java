package com.example.admin.feuerwehr_bewerb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by lhettmann on 19.05.2016.
 */
public class Time_sort_intent extends Activity{

    public ArrayList<Lauf> finalArrayList_sortTimes;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.time_sort_intent_layout);

        final ListView listView_sortList = (ListView)findViewById(R.id.listView_sort_time);
        Button button_turnAroundSorting  = (Button)findViewById(R.id.button_turnAround);

        Intent intent = getIntent();
        Bundle params = intent.getExtras();

        ArrayList<Lauf> arrayList_sortTimes;
        arrayList_sortTimes = (ArrayList<Lauf>) params.get("LIST_SORT");

        MyArrayAdapter sortTimes_adapter = new MyArrayAdapter(this, R.layout.adapter_design, arrayList_sortTimes);
        listView_sortList.setAdapter(sortTimes_adapter);

        finalArrayList_sortTimes = arrayList_sortTimes;

        button_turnAroundSorting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Lauf> result = new ArrayList<Lauf>();
                for(int i = finalArrayList_sortTimes.size()-1; i >= 0; i--){
                    result.add(finalArrayList_sortTimes.get(i));
                }

                MyArrayAdapter sortTimes_adapter = new MyArrayAdapter(Time_sort_intent.this, R.layout.adapter_design, result);
                listView_sortList.setAdapter(sortTimes_adapter);
                finalArrayList_sortTimes = (ArrayList<Lauf>) result.clone();

            }
        });
    }





}
