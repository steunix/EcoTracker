package com.example.stefano.ecotracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int i;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar c = Calendar.getInstance();

        // Year spinner
        ArrayList<Long> years = new ArrayList<Long>();
        for ( i=2015; i<=2020; i++ )
            years.add(new Long(i));

        Spinner spnYear = (Spinner) findViewById(R.id.spnYear);
        ArrayAdapter<Long> adYears = new ArrayAdapter<Long>(this, R.layout.support_simple_spinner_dropdown_item, years);
        spnYear.setAdapter(adYears);

        spnYear.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                updateTotals(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateTotals(null);
            }
        });
        spnYear.setSelection(
                adYears.getPosition(Long.valueOf(c.get(Calendar.YEAR)))
        );

        // Month spinner
        ArrayList<Long> months = new ArrayList<Long>();
        for ( i=1; i<=12; i++ )
            months.add(new Long(i));

        Spinner spnMonth = (Spinner) findViewById(R.id.spnMonth);
        ArrayAdapter<Long> adMonths = new ArrayAdapter<Long>(this, R.layout.support_simple_spinner_dropdown_item, months);
        spnMonth.setAdapter(adMonths);

        spnMonth.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                updateTotals(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateTotals(null);
            }
        });
        spnMonth.setSelection(
                adMonths.getPosition(Long.valueOf((c.get(Calendar.MONTH) + 1)))
        );

        updateTotals(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTotals(null);
    }

    public void updateTotals(View v) {
        TextView ti = (TextView) findViewById(R.id.txtEntrate);
        TextView te = (TextView) findViewById(R.id.txtUscite);
        TextView ts = (TextView) findViewById(R.id.txtSaldo);

        Spinner _month = (Spinner) findViewById(R.id.spnMonth);
        Spinner _year  = (Spinner) findViewById(R.id.spnYear);

        RegisterDB db = new RegisterDB(this);
        float e = db.monthExpense(Integer.valueOf(_year.getSelectedItem().toString()),Integer.valueOf(_month.getSelectedItem().toString()));
        float i = db.monthIncome(Integer.valueOf(_year.getSelectedItem().toString()),Integer.valueOf(_month.getSelectedItem().toString()));

        float s = i - e;

        ti.setText("+"+String.format("%.02f", i));
        te.setText("-" + String.format("%.02f", e));
        ts.setText((s > 0 ? "+" : "") + String.format("%.02f", s));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if ( id == R.id.action_newreg ) {
            Intent intent = new Intent(this, NewregActivity.class);
            startActivity(intent);
        }

        if ( id == R.id.action_accountlist ) {
            Intent intent = new Intent(this, AccountListActivity.class);
            startActivity(intent);
        }

        if ( id == R.id.action_entitylist ) {
            Intent intent = new Intent(this, EntityListActivity.class);
            startActivity(intent);
        }

        if ( id == R.id.action_recordlist ) {
            Intent intent = new Intent(this, RecordListActivity.class);
            startActivity(intent);
        }

        if ( id == R.id.action_report ) {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}

