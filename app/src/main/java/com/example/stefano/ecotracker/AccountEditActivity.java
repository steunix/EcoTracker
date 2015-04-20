package com.example.stefano.ecotracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.List;

public class AccountEditActivity extends ActionBarActivity {

    Account currentAccount;
    String  mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        Account none = new Account();
        none.description = getString(R.string.none);
        none.type = "";

        // Fills parent account
        Register db = new Register(this);
        ArrayList<Account> accounts = db.getAccountsList(Register.DB_SORT.SORT_DESCRIPTION);
        accounts.add(0, none);

        Spinner spnParent = (Spinner) findViewById(R.id.spnParentAccount);
        AccountListAdapter adAccounts = new AccountListAdapter(this, accounts);
        spnParent.setAdapter(adAccounts);

        List<String> types = new ArrayList<>();
        types.add(getString(R.string.type_expense));
        types.add(getString(R.string.type_income));

        // Fills type
        Spinner spnType = (Spinner) findViewById(R.id.spnAccountType);
        ArrayAdapter<String> adType = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,types);
        spnType.setAdapter(adType);

        Intent i = getIntent();
        mode = i.getExtras().getString("mode");
        if (mode.equals("edit") ) {
            // Edit existing account
            setTitle ( getString(R.string.title_activity_edit_account));

            long editAccount = i.getExtras().getLong("id");

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
        menu.findItem(R.id.action_deleteaccount).setVisible(mode.equals("edit"));
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

        if ( id==R.id.action_deleteaccount ) {
            deleteAccount(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteAccount(View v) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setMessage(R.string.account_delete_warning)
                .setTitle(R.string.alert_warning);
        dlg.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Register db = new Register(getApplicationContext());
                        db.deleteAccount(currentAccount);
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

    public void saveAccount(View v) {
        Register db = new Register(this);
        Account account = new Account();

        account.description = ((EditText)findViewById(R.id.txtAccountDescr)).getText().toString();
        String t = ((Spinner)findViewById(R.id.spnAccountType)).getSelectedItem().toString();
        if ( t.equals(getString(R.string.type_expense)) )
            account.type = account.type_expense;
        else
            account.type = account.type_income;

        Account parent = (Account)((Spinner)findViewById(R.id.spnParentAccount)).getSelectedItem();

        if ( parent.description.equals(getString(R.string.none)) )
            account.parent = 0l;
        else
            account.parent = parent.id;

        String mode = getIntent().getExtras().getString("mode");
        if ( mode.equals("edit") )
            account.id = currentAccount.id;
        else
            account.id = null;

        try {
            if (db.saveAccount(account)) {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch ( Register.ETExists ex ) {
            Toast.makeText(this, R.string.exists_already, Toast.LENGTH_SHORT).show();
        }
    }
}
