package com.example.admin.feuerwehr_bewerb;

import java.io.Serializable;

/**
 * Created by Admin on 06.05.2016.
 */
public class Lauf implements Serializable{

    String zeit;
    String taetigkeit;
    String aufstellung;
    String datum;

    public Lauf(String zeit, String taetigkeit, String aufstellung, String datum) {
        this.zeit = zeit;
        this.taetigkeit = taetigkeit;
        this.aufstellung = aufstellung;
        this.datum = datum;
    }

    @Override
    public String toString() {
        return "Zeit: " + zeit + ", T: " + taetigkeit + ", A: " + aufstellung + ", D: " + datum;
    }
}
