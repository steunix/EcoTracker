package com.dev.sr.myecotracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class RecordEditActivity extends ActionBarActivity {

    Record editRecord;
    Register register;
    AccountListAdapter adAccounts;
    EntityListAdapter adEntities;
    LocationManager locManager;
    String mode;
    boolean GPSEnabled = false;
    LocationListener listener;
    Location current_location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_edit);

        register = Register.getInstance(this);

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
        mode = i.getExtras().getString("mode");
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

            TextView txt = (TextView) findViewById(R.id.txtRELocation);
            if ( editRecord.location==null )
                txt.setText(getString(R.string.notavailable));
            else
                txt.setText(editRecord.getLocationString());

        } else {
            editRecord = new Record();
            editRecord.location = null;

            TextView txt = (TextView) findViewById(R.id.txtRELocation);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if ( preferences.getBoolean("enable_gps",false) ) {
                trackGPS();
                txt.setText(getString(R.string.notavailable));
            } else
                txt.setText(getString(R.string.disabled));
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

    private void trackGPS() {
        try {
            locManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        } catch ( Exception E ) {
            return;
        }
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                TextView txt = (TextView) findViewById(R.id.txtRELocation);
                if ( txt==null )
                    return;

                if ( location==null )
                    return;

                if ( current_location==null || (current_location.getAccuracy() > location.getAccuracy()) )
                    current_location = location;

                if ( current_location==null )
                    txt.setText(getString(R.string.notavailable));
                else
                    txt.setText(Helper.locationString(current_location));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        try {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            GPSEnabled = true;
        } catch ( Exception e ) {}

        try {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
            GPSEnabled = true;
        } catch ( Exception e ) {};
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
        if ( mode.equals("new") )
            menu.findItem(R.id.action_deleterecord).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id==R.id.action_saverecord ) {
            saveRecord();
        }

        if ( id==R.id.action_deleterecord ) {
            deleteRecord();
        }

        if ( id==R.id.action_settings ) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        if ( id==R.id.action_info ) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( GPSEnabled ) {
            locManager.removeUpdates(listener);
        }
    }

    public void deleteRecord() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setMessage(R.string.record_delete_warning)
                .setTitle(R.string.alert_warning);
        dlg.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        register.deleteRecord(editRecord);
                        Toast.makeText(getApplicationContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        );
        dlg.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }
        );
        AlertDialog d = dlg.create();
        d.show();
    }

    public void saveRecord() {
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
        samount = samount.replace(",",".");

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

        if ( mode.equals("edit") )
            r.id = editRecord.id;
        else {
            r.id = null;
            r.location = current_location;
        }

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
        startActivityForResult(i, 1);
    }

    public void newAccount(View v) {
        Intent i = new Intent(this, AccountEditActivity.class);
        i.putExtra("mode","new");
        startActivityForResult(i, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode==1 && resultCode==RESULT_OK ) {
            Long id = data.getExtras().getLong("newid");
            updateEntities();
            Entity e = register.getEntity(id);

            int pos = adEntities.getPosition(e.description);
            Spinner spnEntities = (Spinner) findViewById(R.id.spnREEntity);
            spnEntities.setSelection(pos);

        }
        if ( requestCode==2 && resultCode==RESULT_OK ) {
            Long id = data.getExtras().getLong("newid");
            updateAccounts();
            Account a = register.getAccount(id);

            int pos = adAccounts.getPosition(a.description);
            Spinner spnAccounts = (Spinner) findViewById(R.id.spnREAccount);
            spnAccounts.setSelection(pos);
        }
    }

    public void openCalendar(View v) {
        DialogFragment f = new DatePickerFragment();
        f.show(getSupportFragmentManager(), "datePicker");
    }

    public void openPosition(View v) {
        if ( editRecord.location==null )
            return;

        String uri = String.format(Locale.getDefault(), "geo:%s", editRecord.getLocationString());
        Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(searchAddress);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year, month, day;

            EditText txtDate = (EditText) getActivity().findViewById(R.id.txtREDate);
            Date date = Helper.stringToDate(txtDate.getText().toString());

            Calendar c = Calendar.getInstance();
            if ( date!=null )
                c.setTime(date);

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            EditText txtDate = (EditText) getActivity().findViewById(R.id.txtREDate);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            txtDate.setText(Helper.dateToString(c.getTime()));
        }
    }
}
