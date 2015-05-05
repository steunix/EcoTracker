package com.dev.sr.myecotracker;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;


public class SettingsActivity extends ActionBarActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Switch sw = (Switch) findViewById(R.id.swSGPS);
        sw.setChecked(preferences.getBoolean("enable_gps",false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void saveSettings(View v) {
        SharedPreferences.Editor editor = preferences.edit();

        Switch sw = (Switch) findViewById(R.id.swSGPS);
        editor.putBoolean("enable_gps", sw.isChecked());

        editor.commit();
    }
}
