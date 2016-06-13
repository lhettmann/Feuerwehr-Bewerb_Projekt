package com.example.admin.feuerwehr_bewerb;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by lhettmann on 10.06.2016.
 */
public class PrefsActivity_draw extends PreferenceActivity {

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.draw_preferences);
    }

}
