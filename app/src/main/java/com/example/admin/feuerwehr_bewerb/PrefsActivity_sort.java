package com.example.admin.feuerwehr_bewerb;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class PrefsActivity_sort extends PreferenceActivity{

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.sort_preferences);
    }


}
