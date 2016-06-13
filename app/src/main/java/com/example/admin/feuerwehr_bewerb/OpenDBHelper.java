package com.example.admin.feuerwehr_bewerb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 06.05.2016.
 */
public class OpenDBHelper extends SQLiteOpenHelper {

    public OpenDBHelper(Context context){
        super(context, Schemaklasse.DB_NAME, null, Schemaklasse.DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Schemaklasse.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Schemaklasse.SQL_DROP);
        onCreate(db);
    }
}

