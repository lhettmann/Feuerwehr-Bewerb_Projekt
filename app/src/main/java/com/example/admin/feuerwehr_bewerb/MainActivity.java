package com.example.admin.feuerwehr_bewerb;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    //Globale Variablen
    MediaPlayer mediaPlayer;
    int int_angriffsbefehl_modus;
    boolean reset_time;

    AdapterView.AdapterContextMenuInfo info;

    static final int PICK_CONTACT_REQUEST = 1;

    public static final long SLEEPTIME = 10;
    boolean runningThread;
    Thread refreshThread;
    double time;

    TextView textView_Zeit;
    Button button_zeitentnahme;
    Button button_zeitspeichern;
    RadioButton radioButton_angriffsbefehl_lang;
    RadioButton radioButton_angriffsbefehl_kurz;
    RadioButton radioButton_angriffsbefehl_keine;

    RadioButton radioButton_aufstellung_bronze;
    RadioButton radioButton_aufstellung_silber;

    Spinner spinner_taetigkeiten;
    ArrayAdapter<String> spinnerAdapter_dialog;
    ArrayList<String> taetigkeiten;

    OpenDBHelper openDBHelper;
    SQLiteDatabase zeitenDB = null;

    ArrayList<Lauf> zeitenliste;

    MyArrayAdapter zeitenliste_adapter;
    ListView listView_zeitenliste;

    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createTabs();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mediaPlayer = MediaPlayer.create(this, R.raw.angriffsbefehllang);
        int_angriffsbefehl_modus = 0;
        reset_time = false;

        textView_Zeit = (TextView) findViewById(R.id.textView_Zeit);
        button_zeitentnahme = (Button) findViewById(R.id.button_zeitentnahme);
        button_zeitspeichern = (Button) findViewById(R.id.button_zeitspeichern);

        listView_zeitenliste = (ListView) findViewById(R.id.listView_zeitenliste);

        radioButton_angriffsbefehl_lang = (RadioButton) findViewById(R.id.radioButton_langerA);
        radioButton_angriffsbefehl_kurz = (RadioButton) findViewById(R.id.radioButton_kurzerA);
        radioButton_angriffsbefehl_keine = (RadioButton) findViewById(R.id.radioButton_keinA);

        zeitenliste = new ArrayList<>();

        //TESTFÄLLE
        Lauf lauf_1 = new Lauf("1", "Kuppeln", "Bronze", "20-5-2015");
        Lauf lauf_7 = new Lauf("7", "Kuppeln", "Bronze", "26-5-2015");
        Lauf lauf_8 = new Lauf("8", "Kuppeln", "Silber", "27-5-2015");
        Lauf lauf_9 = new Lauf("9", "Kuppeln", "Bronze", "28-5-2015");
        Lauf lauf_2 = new Lauf("2", "Kuppeln", "Silber", "21-5-2015");
        Lauf lauf_3 = new Lauf("3", "Gesamt Trocken", "Bronze", "22-5-2015");
        Lauf lauf_4 = new Lauf("4", "Gesamt Trocken", "Silber", "23-5-2015");
        Lauf lauf_5 = new Lauf("5", "Gesamt Nass", "Bronze", "24-5-2015");
        Lauf lauf_6 = new Lauf("6", "Gesamt Nass", "Silber", "25-5-2015");
        Lauf lauf_10 = new Lauf("9", "Kuppeln", "Silber", "29-5-2015");
        zeitenliste.add(lauf_1);
        zeitenliste.add(lauf_2);
        zeitenliste.add(lauf_3);
        zeitenliste.add(lauf_4);
        zeitenliste.add(lauf_5);
        zeitenliste.add(lauf_6);
        zeitenliste.add(lauf_7);
        zeitenliste.add(lauf_8);
        zeitenliste.add(lauf_9);
        zeitenliste.add(lauf_10);


        runningThread = false;
        time = 0;

        openDBHelper = new OpenDBHelper(this);
        zeitenDB = openDBHelper.getWritableDatabase();

        loadData();

        zeitenliste_adapter = new MyArrayAdapter(this, R.layout.adapter_design, zeitenliste);
        listView_zeitenliste.setAdapter(zeitenliste_adapter);

        registerForContextMenu(findViewById(R.id.listView_zeitenliste));


        button_zeitentnahme.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                time = 0;
                textView_Zeit.setText("0.00");
                mediaPlayer.stop();
                Toast.makeText(MainActivity.this, "Zeit wurde zurückgesetzt", Toast.LENGTH_SHORT).show();
                reset_time = true;
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAllTimes:
                zeitenliste.clear();
                zeitenliste_adapter.notifyDataSetChanged();
                zeitenDB.delete(Schemaklasse.TABLE_NAME, null, null);
                break;
            case R.id.sortiereinstellungen:
                startActivity(new Intent(this, PrefsActivity_sort.class));
                break;
            case R.id.sort:
                boolean order_by_date;
                boolean kuppeln_sort = preferences.getBoolean("sort_kuppeln", true);
                boolean gesamtTrocken_sort = preferences.getBoolean("sort_gesamtTrocken", true);
                boolean gesamtNass_sort = preferences.getBoolean("sort_gesamtNass", true);
                boolean bronze_sort = preferences.getBoolean("sort_bronze", true);
                boolean silber_sort = preferences.getBoolean("sort_silber", true);
                String sort_order = preferences.getString("sort_order", "");
                if (sort_order.equals("Datum")) {
                    order_by_date = true;
                } else {
                    order_by_date = false;
                }
                sortList(kuppeln_sort, gesamtTrocken_sort, gesamtNass_sort, bronze_sort, silber_sort, order_by_date);
                break;
            case R.id.zeitauswertung:
                //Zeitenauswertung Intent starten
                Intent intent_evaluation = new Intent(this, Time_evaluation_intent.class);
                intent_evaluation.putExtra("zeitenliste",zeitenliste);
                startActivity(intent_evaluation);
                break;
            case R.id.ziehung:
                //Zieheinstellungen
                startActivity(new Intent(this, PrefsActivity_draw.class));
                break;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int id = v.getId();
        if (id == R.id.listView_zeitenliste) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu_main, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (id == R.id.deleteTime) {
            Lauf lauf_selected = zeitenliste.get(info.position);
            zeitenDB.delete(Schemaklasse.TABLE_NAME, "datum=? AND taetigkeit=? AND aufstellung=? AND zeit=?",
                    new String[]{lauf_selected.datum, lauf_selected.taetigkeit, lauf_selected.aufstellung, lauf_selected.zeit});
            zeitenliste.remove(info.position);
            zeitenliste_adapter.notifyDataSetChanged();
        }
        if (id == R.id.editTime) {
            Lauf lauf_selected = zeitenliste.get(info.position);

            Intent intent = new Intent(MainActivity.this, Time_edit_intent.class);
            intent.putExtra("LAUF", lauf_selected);
            intent.putExtra("POSITION", info.position);
            startActivityForResult(intent, PICK_CONTACT_REQUEST);

        }

        return true;
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radioButton_langerA:
                if (checked) {
                    if (textView_Zeit.getText().equals("0.00")) {
                        mediaPlayer = MediaPlayer.create(this, R.raw.angriffsbefehllang);
                    }
                    int_angriffsbefehl_modus = 0;
                }

                break;
            case R.id.radioButton_kurzerA:
                if (checked) {
                    if (textView_Zeit.getText().equals("0.00")) {
                        mediaPlayer = MediaPlayer.create(this, R.raw.angriffsbefehlkurz);
                    }
                    int_angriffsbefehl_modus = 1;
                }
                break;
            case R.id.radioButton_keinA:
                if (checked) {
                    int_angriffsbefehl_modus = 2;
                }
                break;
        }

    }


    public void pressButton(View view) {     //gehört noch überarbeitet

        if (reset_time) {
            if (int_angriffsbefehl_modus == 0) {
                mediaPlayer = MediaPlayer.create(this, R.raw.angriffsbefehllang);
                reset_time = false;
            }

            if (int_angriffsbefehl_modus == 1) {
                mediaPlayer = MediaPlayer.create(this, R.raw.angriffsbefehlkurz);
                reset_time = false;
            }

        }

        if (textView_Zeit.getText().equals("0.00")) {
            if (int_angriffsbefehl_modus < 2) {
                mediaPlayer.start();
            }
        }


        if (!runningThread) {

            radioButton_angriffsbefehl_lang.setEnabled(false);
            radioButton_angriffsbefehl_kurz.setEnabled(false);
            radioButton_angriffsbefehl_keine.setEnabled(false);
            button_zeitspeichern.setEnabled(false);

            runningThread = true;

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer player) {
                    initThread();
                }
            });
            if(!textView_Zeit.getText().equals("0.00")){
                initThread();
            }
            if(int_angriffsbefehl_modus == 2){
                initThread();
            }


            button_zeitentnahme.setText("Stopp");
            if (int_angriffsbefehl_modus < 2) {
                if (textView_Zeit.getText().equals("0.00")) {
                    mediaPlayer.start();
                }
            }


        } else {

            radioButton_angriffsbefehl_lang.setEnabled(true);
            radioButton_angriffsbefehl_kurz.setEnabled(true);
            radioButton_angriffsbefehl_keine.setEnabled(true);
            button_zeitspeichern.setEnabled(true);

            runningThread = false;
            button_zeitentnahme.setText("Start");
            mediaPlayer.pause();
        }

    }


    public void createTabs() {

        TabHost host = (TabHost) findViewById(R.id.tabHost_main);
        host.setup();

        //Tab1 Kuppeln
        TabHost.TabSpec spec = host.newTabSpec("Tab_Stoppen");
        spec.setContent(R.id.tab_stoppen);
        spec.setIndicator("Stoppen");
        host.addTab(spec);

        //Tab2 Gesamt
        spec = host.newTabSpec("Tab_Zeiten");
        spec.setContent(R.id.tab_zeiten);
        spec.setIndicator("Zeiten");
        host.addTab(spec);

        //Tab3 Zeiten
        spec = host.newTabSpec("Tab_PositionZiehen");
        spec.setContent(R.id.tab_positionZiehen);
        spec.setIndicator("Position ziehen");
        host.addTab(spec);

    }


    public void initThread() {

        refreshThread = new Thread(new Runnable() {
            public void run() {

                while (runningThread) {
                    time = time + 0.01;
                    try {
                        Thread.sleep(SLEEPTIME);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            textView_Zeit.setText(String.format("%.2f", time));
                        }
                    });

                }
            }
        });
        refreshThread.start();

    }


    public void ZeitSpeichern(View view) {

        final String zeit = (String) textView_Zeit.getText();
        if (zeit.equals("0.00")) {
            Toast.makeText(this, "Es wurde keine Zeit aufgenommen", Toast.LENGTH_LONG).show();
        } else {
            //Dialog
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Zeitaufnahme");
            final LinearLayout vDialog = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_zeitspeichern, null);

            TextView textView_zeiteintrag = (TextView) vDialog.findViewById(R.id.textView_zeiteintrag);
            textView_zeiteintrag.setText(zeit);

            taetigkeiten = new ArrayList<>();
            taetigkeiten.add("Kuppeln");
            taetigkeiten.add("Gesamt Trocken");
            taetigkeiten.add("Gesamt Nass");
            spinner_taetigkeiten = (Spinner) vDialog.findViewById(R.id.spinner_tätigkeit);
            spinnerAdapter_dialog = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, taetigkeiten);
            spinner_taetigkeiten.setAdapter(spinnerAdapter_dialog);

            radioButton_aufstellung_bronze = (RadioButton) vDialog.findViewById(R.id.radioButton_bronze);
            radioButton_aufstellung_silber = (RadioButton) vDialog.findViewById(R.id.radioButton_silber);

            alert.setView(vDialog);

            alert.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //zeit,tätigkeit,aufstellung,datum(aktuelles Datum holen) -> Objekt erstellen
                    String taetigkeit = spinner_taetigkeiten.getSelectedItem().toString();
                    String aufstellung;
                    boolean bronze = radioButton_aufstellung_bronze.isChecked();
                    if (bronze) {
                        aufstellung = "Bronze";
                    } else {
                        aufstellung = "Silber";
                    }
                    Calendar datum = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String datum_formatiert = df.format(datum.getTime());

                    Lauf lauf = new Lauf(zeit, taetigkeit, aufstellung, datum_formatiert);

                    zeitenliste.add(lauf);
                    insertIntoDB(lauf);
                    zeitenliste_adapter.notifyDataSetChanged();


                }
            });

            alert.setNegativeButton("Abbrechen", null);
            alert.show();

        }

    }


    public void insertIntoDB(Lauf lauf) {
        ContentValues neueZeit = new ContentValues();
        neueZeit.put("zeit", lauf.zeit);
        neueZeit.put("taetigkeit", lauf.taetigkeit);
        neueZeit.put("aufstellung", lauf.aufstellung);
        neueZeit.put("datum", lauf.datum);
        zeitenDB.insert(Schemaklasse.TABLE_NAME, null, neueZeit);
    }


    public void loadData() {
        Cursor rows = zeitenDB.rawQuery("select datum,taetigkeit,aufstellung,zeit from zeitenTBL", null);
        while (rows.moveToNext()) {
            String datum = rows.getString(0);
            String taetigkeit = rows.getString(1);
            String aufstellung = rows.getString(2);
            String zeit = rows.getString(3);

            Lauf lauf = new Lauf(datum, taetigkeit, aufstellung, zeit);
            lauf.datum = datum;
            lauf.taetigkeit = taetigkeit;
            lauf.aufstellung = aufstellung;
            lauf.zeit = zeit;

            zeitenliste.add(lauf);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {

                Bundle bundle = data.getExtras();
                Lauf lauf_edit = (Lauf) bundle.get("LAUF_EDIT");
                int position = (int) bundle.get("POSITION_BACK");
                Lauf lauf_alt = zeitenliste.get(position);

                //In Datenbank ändern
                ContentValues vals = new ContentValues();
                vals.put("zeit", lauf_edit.zeit);
                vals.put("taetigkeit", lauf_edit.taetigkeit);
                vals.put("aufstellung", lauf_edit.aufstellung);
                vals.put("datum", lauf_alt.datum);

                zeitenDB.update("ZeitenTBL", vals, "zeit=? AND taetigkeit=? AND aufstellung=? AND datum=?",
                        new String[]{lauf_alt.zeit, lauf_alt.taetigkeit, lauf_alt.aufstellung, lauf_alt.datum});

                zeitenliste.get(position).taetigkeit = lauf_edit.taetigkeit;
                zeitenliste.get(position).aufstellung = lauf_edit.aufstellung;
                zeitenliste.get(position).zeit = lauf_edit.zeit;

                zeitenliste_adapter.notifyDataSetChanged();

            }
        }
    }


    public void sortList(boolean sort_kuppeln, boolean sort_gesamtTrocken, boolean sort_gesamtNass,
                                boolean sort_bronze, boolean sort_silber, boolean order_by_date) {                   //Hier wird die Liste sortiert

        //Intent der angepassten Liste aufrufen
        ArrayList<Lauf> sort_list;
        sort_list = (ArrayList<Lauf>) zeitenliste.clone();
        int kontrollvariable = 0;
        for(int i = 0; i < sort_list.size(); i++){
            if(kontrollvariable == 0) {
                if (!sort_kuppeln) {
                    if(sort_list.get(i).taetigkeit.equals("Kuppeln")) {
                        sort_list.remove(i);
                        kontrollvariable = 1;
                        i--;
                    }
                }
            }
            if(kontrollvariable == 0) {
                if (!sort_gesamtTrocken) {
                    if(sort_list.get(i).taetigkeit.equals("Gesamt Trocken")) {
                        sort_list.remove(i);
                        kontrollvariable = 1;
                        i--;
                    }
                }
            }
            if(kontrollvariable == 0) {
                if (!sort_gesamtNass) {
                    if(sort_list.get(i).taetigkeit.equals("Gesamt Nass")) {
                        sort_list.remove(i);
                        kontrollvariable = 1;
                        i--;
                    }
                }
            }
            if(kontrollvariable == 0) {
                if (!sort_bronze) {
                    if(sort_list.get(i).aufstellung.equals("Bronze")) {
                        sort_list.remove(i);
                        kontrollvariable = 1;
                        i--;
                    }
                }
            }
            if(kontrollvariable == 0) {
                if (!sort_silber) {
                    if(sort_list.get(i).aufstellung.equals("Silber")) {
                        sort_list.remove(i);
                        i--;
                    }
                }
            }
            kontrollvariable = 0;
        }

        //Sortierung ??




        //Intent starten
        Intent intent = new Intent(this, Time_sort_intent.class);
        intent.putExtra("LIST_SORT", sort_list);
        for(int i = 0; i < sort_list.size(); i++){
            System.out.println(sort_list.get(i).toString());
        }

        startActivity(intent);


    }


}
