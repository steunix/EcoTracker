package com.dev.sr.myecotracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RecordEditActivity extends ActionBarActivity {

    Record editRecord;
    Register register;
    AccountListAdapter adAccounts;
    EntityListAdapter adEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_edit);

        register = new Register(this);

        adAccounts = new AccountListAdapter(this, new ArrayList<Account>());
        adEntities = new EntityListAdapter(this, new ArrayList<Entity>());

        Spinner spnAccounts = (Spinner) findViewById(R.id.spnREAccount);
        Spinner spnEntities = (Spinner) findViewById(R.id.spnREEntity);

        updateAccounts();
        updateEntities();

        EditText txtDate = (EditText) findViewById(R.id.txtREDate);
        String strDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        txtDate.setText(strDate);

        Intent i = getIntent();
        String mode = i.getExtras().getString("mode");
        if (mode.equals("edit") ) {

            editRecord = register.getRecord(i.getExtras().getLong("id"));

            int pos = adAccounts.getPosition(editRecord.account.description);
            spnAccounts.setSelection(pos);

            pos = adEntities.getPosition(editRecord.entity.description);
            spnEntities.setSelection(pos);

            txtDate.setText(Helper.dateToString(editRecord.date));

            EditText txtAmount = (EditText) findViewById(R.id.txtREAmount);
            txtAmount.setText(editRecord.getRawAmountString());

            EditText txtDescription = (EditText) findViewById(R.id.txtREDescription);
            txtDescription.setText(editRecord.description);
        }

        // Add events for click
        spnAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateEntities();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateAccounts() {
        ArrayList<Account> accounts = register.getAccountsList(Register.DB_SORT.SORT_USAGE);

        adAccounts.clear();
        adAccounts.addAll(accounts);

        Spinner spnAccounts = (Spinner) findViewById(R.id.spnREAccount);
        spnAccounts.setAdapter(adAccounts);
    }

    private void updateEntities() {
        Account account = (Account)((Spinner)findViewById(R.id.spnREAccount)).getSelectedItem();
        ArrayList<Entity> entities = register.getEntitiesList(Register.DB_SORT.SORT_USAGE_COMBINED, account);

        adEntities.clear();
        adEntities.addAll(entities);

        Spinner spnEntities = (Spinner) findViewById(R.id.spnREEntity);
        spnEntities.setAdapter(adEntities);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_record_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_info) {
            Intent i = new Intent(this, InfoActivity.class);
            startActivity(i);
        }

        if ( id==R.id.action_saverecord ) {
            saveRecord(null);
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveRecord(View view) {
        // Check date
        EditText _date = (EditText) findViewById(R.id.txtREDate);
        String sdate = _date.getText().toString();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        try {
            date = df.parse(sdate);
            if (sdate.length() != 10 ) {
                Toast.makeText(this,R.string.baddate,Toast.LENGTH_SHORT).show();
                return;
            }
        }
        catch(ParseException e) {
            Toast.makeText(this,R.string.baddate,Toast.LENGTH_SHORT).show();
            return;
        }

        // Check amount
        EditText _amount = (EditText) findViewById(R.id.txtREAmount);
        String samount = _amount.getText().toString();
        float amt = 0;

        try {
            amt = Float.parseFloat(samount);
        } catch ( NumberFormatException e ) {
            Toast.makeText(this,R.string.badamount,Toast.LENGTH_SHORT).show();
            return;
        }
        if ( amt==0 ) {
            Toast.makeText(this,R.string.amountzero,Toast.LENGTH_SHORT).show();
            return;
        }

        // Save record
        Record r = new Record();

        Intent i = getIntent();
        if ( i.getExtras().getString("mode").equals("edit") )
            r.id = editRecord.id;
        else
            r.id = null;

        r.account = (Account)((Spinner)findViewById(R.id.spnREAccount)).getSelectedItem();
        r.entity = (Entity)((Spinner)findViewById(R.id.spnREEntity)).getSelectedItem();
        r.description = ((EditText)findViewById(R.id.txtREDescription)).getText().toString().trim();
        r.date = date;
        r.amount = amt;

        if ( register.saveRecord(r) ) {
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void newEntity(View v) {
        Intent i = new Intent(this, EntityEditActivity.class);
        i.putExtra("mode","new");
        startActivity(i);
    }

    public void newAccount(View v) {
        Intent i = new Intent(this, AccountEditActivity.class);
        i.putExtra("mode","new");
        startActivity(i);
    }
}
