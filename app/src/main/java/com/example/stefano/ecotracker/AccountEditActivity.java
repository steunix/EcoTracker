package com.example.stefano.ecotracker;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class AccountEditActivity extends ActionBarActivity {

    Account currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        Account none = new Account();
        none.description = getString(R.string.none);
        none.type = "";

        // Fills parent account
        RegisterDB db = new RegisterDB(this);
        ArrayList<Account> accounts = db.getAccountsList(RegisterDB.DB_SORT.SORT_DESCRIPTION);
        accounts.add(0, none);

        Spinner spnParent = (Spinner) findViewById(R.id.spnParentAccount);
        AccountListAdapter adAccounts = new AccountListAdapter(this, accounts);
        spnParent.setAdapter(adAccounts);

        List<String> types = new ArrayList<String>();
        types.add(getString(R.string.type_expense));
        types.add(getString(R.string.type_income));

        // Fills type
        Spinner spnType = (Spinner) findViewById(R.id.spnAccountType);
        ArrayAdapter<String> adType = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,types);
        spnType.setAdapter(adType);

        Intent i = getIntent();
        int mode = i.getExtras().getInt("mode");
        if (mode == 1 ) {
            // Edit existing account
            setTitle ( getString(R.string.title_activity_edit_account));

            String editAccount = i.getExtras().getString("account");

            currentAccount = db.getAccount(editAccount);
            Account p = db.getAccount(currentAccount.parent);

            // Type
            int pos = adType.getPosition(currentAccount.type);
            spnType.setSelection(pos);

            // Description
            EditText descr = (EditText) findViewById(R.id.txtAccountDescr);
            descr.setText(currentAccount.description);

            // Parent
            if ( p!=null ) {
                pos = adAccounts.getPosition(p.description);
                spnParent.setSelection(pos);
            } else
                spnParent.setSelection(0);
        } else {
            // New account
            setTitle(getString(R.string.title_activity_new_account));
            currentAccount = new Account();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if ( id==R.id.action_saveaccount ) {
            saveAccount(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveAccount(View v) {
        RegisterDB db = new RegisterDB(this);
        Account account = new Account();
        Long parentId;

        account.description = ((EditText)findViewById(R.id.txtAccountDescr)).getText().toString();
        String t = ((Spinner)findViewById(R.id.spnAccountType)).getSelectedItem().toString();
        if ( t == getString(R.string.type_expense) )
            account.type = account.type_expense;
        else
            account.type = account.type_income;

        Account parent = (Account)((Spinner)findViewById(R.id.spnParentAccount)).getSelectedItem();

        if ( parent.description == getString(R.string.none) )
            parentId = new Long(0);
        else
            parentId = parent.id;

        int mode = getIntent().getExtras().getInt("mode");
        if ( mode==1 )
            account.id = currentAccount.id;
        else
            account.id = null;

        if ( db.saveAccount(account) ) {
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
