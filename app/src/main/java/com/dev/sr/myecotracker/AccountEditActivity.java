package com.dev.sr.myecotracker;

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
    Register register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        Account none = new Account();
        none.description = getString(R.string.none);
        none.type = "";

        // Fills parent account
        register = new Register(this);
        ArrayList<Account> accounts = register.getAccountsList(Register.DB_SORT.SORT_DESCRIPTION);
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

            currentAccount = register.getAccount(editAccount);
            Account p = register.getAccount(currentAccount.parent);

            // Type
            if ( currentAccount.type.equals(currentAccount.type_expense) )
                spnType.setSelection(0);
            else
                spnType.setSelection(1);

            // Description
            EditText descr = (EditText) findViewById(R.id.txtAccountDescr);
            descr.setText(currentAccount.description);

            // Parent
            if ( p!=null ) {
                int pos;
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

        if (id == R.id.action_info) {
            Intent i = new Intent(this, InfoActivity.class);
            startActivity(i);
        }

        if ( id==R.id.action_saveaccount ) {
            saveAccount(null);
        }

        if ( id==R.id.action_deleteaccount ) {
            deleteAccount(null);
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteAccount(View v) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setMessage(R.string.account_delete_warning)
                .setTitle(R.string.alert_warning);
        dlg.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        register.deleteAccount(currentAccount);
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
            if (register.saveAccount(account)) {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch ( Register.ETExists ex ) {
            Toast.makeText(this, R.string.exists_already, Toast.LENGTH_SHORT).show();
        }
    }
}