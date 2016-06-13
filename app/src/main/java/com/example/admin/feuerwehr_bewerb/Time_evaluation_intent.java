package com.example.admin.feuerwehr_bewerb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by lhettmann on 09.06.2016.
 */
public class Time_evaluation_intent extends Activity{

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.time_evaluation_intent_layout);

        ArrayList<Lauf> zeitenliste = null;

        Intent intent = getIntent();
        Bundle parms = intent.getExtras();
        if (parms != null) {
            zeitenliste = (ArrayList<Lauf>) parms.get("zeitenliste");
        }

        final Spinner spinner_evaluation_aufstellung = (Spinner)findViewById(R.id.spinner_evaluation_aufstellung);

        final TextView textView_evaluation_bronze_besteZeit = (TextView)findViewById(R.id.textView_evaluation_bronze_besteZeit);
        final TextView textView_evaluation_bronze_avgZeit = (TextView)findViewById(R.id.textView_evaluation_bronze_avgZeit);
        final TextView textView_evaluation_bronze_schlechtesteZeit = (TextView) findViewById(R.id.textView_evaluation_bronze_schlechtesteZeit);

        final TextView textView_evaluation_silber_besteZeit = (TextView)findViewById(R.id.textView_evaluation_silber_besteZeit);
        final TextView textView_evaluation_silber_avgZeit = (TextView)findViewById(R.id.textView_evaluation_silber_avgZeit);
        final TextView textView_evaluation_silber_schlechtesteZeit = (TextView) findViewById(R.id.textView_evaluation_silber_schlechtesteZeit);

        String[] spinnerArray_aufstellung = {"Kuppeln", "Gesamt Trocken", "Gesamt Nass"};

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, spinnerArray_aufstellung);
        spinner_evaluation_aufstellung.setAdapter(spinnerArrayAdapter);

        //TextViews befüllen

        final ArrayList<Lauf> finalZeitenliste = zeitenliste;
        spinner_evaluation_aufstellung.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                textView_evaluation_bronze_besteZeit.setText("-");
                textView_evaluation_bronze_schlechtesteZeit.setText("-");
                textView_evaluation_bronze_avgZeit.setText("-");
                textView_evaluation_silber_besteZeit.setText("-");
                textView_evaluation_silber_schlechtesteZeit.setText("-");
                textView_evaluation_silber_avgZeit.setText("-");

                double bronze_besteZeit = 1000;
                double silber_besteZeit = 1000;

                double bronze_schlechtesteZeit = 1;
                double silber_schlechtesteZeit = 1;

                double bronze_avg = 0;
                double silber_avg = 0;

                int zeitencounter_bronze = 0;
                int zeitencounter_silber = 0;

                for (int i = 0; i < finalZeitenliste.size(); i++) {

                    if(finalZeitenliste.get(i).taetigkeit.toString().equals(spinner_evaluation_aufstellung.getSelectedItem().toString())){

                        if(finalZeitenliste.get(i).aufstellung.equals("Bronze")){
                            double zeit_aktuell = Double.parseDouble(finalZeitenliste.get(i).zeit);
                            if(zeit_aktuell < bronze_besteZeit){
                                bronze_besteZeit = zeit_aktuell;
                            }
                            bronze_avg = bronze_avg + zeit_aktuell;
                            zeitencounter_bronze++;
                        }

                        if(finalZeitenliste.get(i).aufstellung.equals("Silber")){
                            double zeit_aktuell = Double.parseDouble(finalZeitenliste.get(i).zeit);
                            if(zeit_aktuell < silber_besteZeit){
                                silber_besteZeit = zeit_aktuell;
                            }
                            silber_avg = silber_avg + zeit_aktuell;
                            zeitencounter_silber++;
                        }

                        if(finalZeitenliste.get(i).aufstellung.equals("Bronze")){
                            double zeit_aktuell = Double.parseDouble(finalZeitenliste.get(i).zeit);
                            if(zeit_aktuell > bronze_schlechtesteZeit){
                                bronze_schlechtesteZeit = zeit_aktuell;
                            }
                        }

                        if(finalZeitenliste.get(i).aufstellung.equals("Silber")){
                            double zeit_aktuell = Double.parseDouble(finalZeitenliste.get(i).zeit);
                            if(zeit_aktuell > silber_schlechtesteZeit){
                                silber_schlechtesteZeit = zeit_aktuell;
                            }
                        }

                    }

                }

                bronze_avg = bronze_avg/zeitencounter_bronze;
                silber_avg = silber_avg/zeitencounter_silber;

                NumberFormat n = NumberFormat.getInstance();
                n.setMaximumFractionDigits(2);

                textView_evaluation_bronze_besteZeit.setText(bronze_besteZeit+"");
                textView_evaluation_bronze_schlechtesteZeit.setText(bronze_schlechtesteZeit+"");
                textView_evaluation_bronze_avgZeit.setText(n.format(bronze_avg));

                textView_evaluation_silber_besteZeit.setText(silber_besteZeit+"");
                textView_evaluation_silber_schlechtesteZeit.setText(silber_schlechtesteZeit+"");
                textView_evaluation_silber_avgZeit.setText(n.format(silber_avg));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("Wird nicht verwendet.");
            }
        });







    }

}
