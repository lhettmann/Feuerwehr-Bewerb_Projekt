package com.example.admin.feuerwehr_bewerb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Admin on 06.05.2016.
 */
public class Time_edit_intent extends Activity{

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.time_edit_intent_layout);

        final Intent intent = getIntent();
        Bundle parms = intent.getExtras();
        final Lauf lauf = (Lauf)parms.get("LAUF");
        final int position = (int) parms.get("POSITION");


        ArrayList<String> taetigkeiten = new ArrayList<>();
        taetigkeiten.add("Kuppeln");
        taetigkeiten.add("Gesamt Trocken");
        taetigkeiten.add("Gesamt Nass");
        int spinner_position = -1;
        switch(lauf.taetigkeit){
            case "Kuppeln":
                spinner_position = 0;
                break;
            case "Gesamt Trocken":
                spinner_position = 1;
                break;
            case "Gesamt Nass":
                spinner_position = 2;
                break;
        }



        final Spinner spinner_timeEdit_taetigkeit = (Spinner) findViewById(R.id.spinner_editTime_taetigkeit);
        ArrayAdapter<String> spinnerAdapter_dialog = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, taetigkeiten);
        spinner_timeEdit_taetigkeit.setAdapter(spinnerAdapter_dialog);

        final RadioButton radioButton_aufstellung_bronze = (RadioButton)findViewById(R.id.radioButton_editTime_bronze);
        RadioButton radioButton_aufstellung_silber = (RadioButton)findViewById(R.id.radioButton_editTime_Silber);

        final EditText editText_zeit = (EditText)findViewById(R.id.editText_zeit);

        Button button_speichern = (Button) findViewById(R.id.button_editTime_speichern);


        spinner_timeEdit_taetigkeit.setSelection(spinner_position);

        if(lauf.aufstellung.equals("Bronze")){
            radioButton_aufstellung_bronze.setChecked(true);
        }else{
            radioButton_aufstellung_silber.setChecked(true);
        }

        editText_zeit.setText(lauf.zeit);



        button_speichern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String taetigkeit = spinner_timeEdit_taetigkeit.getSelectedItem().toString();
                boolean bronze = radioButton_aufstellung_bronze.isChecked();
                String aufstellung;
                if(bronze){
                    aufstellung = "Bronze";
                }
                else{
                    aufstellung = "Silber";
                }
                String zeit = editText_zeit.getText().toString();

                Lauf lauf_edit = new Lauf(zeit, taetigkeit, aufstellung, lauf.datum);

                Intent intent_back = new Intent();
                intent.putExtra("LAUF_EDIT", lauf_edit);
                intent.putExtra("POSITION_BACK", position);
                setResult(RESULT_OK, intent);
                finish();


            }
        });



    }

}
