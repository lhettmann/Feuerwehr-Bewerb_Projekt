package com.example.admin.feuerwehr_bewerb;

/**
 * Created by Admin on 06.05.2016.
 */
public class Schemaklasse {

        public final static String DB_NAME = "zeiten.db";
        public final static int DB_VERSION = 1;

        public static final String TABLE_NAME = "zeitenTBL";

        public final static String zeitenID = "zeitenID";
        public final static String zeit = "zeit";
        public final static String taetigkeit = "taetigkeit";
        public final static String aufstellung = "aufstellung";
        public final static String datum = "datum";

        public final static String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public final static String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME +
                        "(" +
                        zeitenID + " INTEGER PRIMARY KEY," +
                        zeit + " TEXT NOT NULL," +
                        taetigkeit + " TEXT NOT NULL," +
                        aufstellung + " TEXT NOT NULL," +
                        datum + " TEXT NOT NULL" +
                        ")";



}
