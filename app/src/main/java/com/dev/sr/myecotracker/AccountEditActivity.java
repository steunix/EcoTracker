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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AccountEditActivity extends ActionBarActivity {

    Account currentAccount;
    String  mode;
    Register register;
    CategorySelectAdapter adCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        Account none = new Account();
        none.description = getString(R.string.none);
        none.type = "";

        register = Register.getInstance(this);

        List<String> types = new ArrayList<>();
        types.add(getString(R.string.type_expense));
        types.add(getString(R.string.type_income));

        // Fills type
        Spinner spnType = (Spinner) findViewById(R.id.spnAEAccountType);
        ArrayAdapter<String> adType = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,types);
        spnType.setAdapter(adType);

        // Fills categories
        ArrayList<Category> cat = register.getCategoriesList();
        ListView lstCat = (ListView) findViewById(R.id.lstAECategories);

        Intent i = getIntent();
        mode = i.getExtras().getString("mode");
        if (mode.equals("edit") ) {
            // Edit existing account
            setTitle ( getString(R.string.title_activity_edit_account));

            long editAccount = i.getExtras().getLong("id");

            currentAccount = register.getAccount(editAccount);

            // Type
            if ( currentAccount.type.equals(currentAccount.type_expense) )
                spnType.setSelection(0);
            else
                spnType.setSelection(1);

            // Description
            EditText descr = (EditText) findViewById(R.id.txtAEDescr);
            descr.setText(currentAccount.description);

            // Categories
            ArrayList<CategorySelect> catlist = register.getAccountCategoriesSelect(currentAccount);
            adCat = new CategorySelectAdapter(getApplicationContext(), catlist);
            lstCat.setAdapter(adCat);

        } else {
            // New account
            setTitle(getString(R.string.title_activity_new_account));
            currentAccount = new Account();

            // Categories
            ArrayList<CategorySelect> catlist = register.getAccountCategoriesSelect(currentAccount);
            adCat = new CategorySelectAdapter(getApplicationContext(), catlist);
            lstCat.setAdapter(adCat);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_edit, menu);
        if ( mode.equals("new") || currentAccount.id==0 )
            menu.findItem(R.id.action_deleteaccount).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id==R.id.action_saveaccount ) {
            saveAccount(null);
        }

        if ( id==R.id.action_deleteaccount ) {
            deleteAccount(null);
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

        account.description = ((EditText)findViewById(R.id.txtAEDescr)).getText().toString().trim();

        if (account.description.length() == 0) {
            Toast.makeText(this, R.string.baddescription, Toast.LENGTH_SHORT).show();
            return;
        }

        String t = ((Spinner)findViewById(R.id.spnAEAccountType)).getSelectedItem().toString();
        if ( t.equals(getString(R.string.type_expense)) )
            account.type = account.type_expense;
        else
            account.type = account.type_income;

        String mode = getIntent().getExtras().getString("mode");
        if ( mode.equals("edit") )
            account.id = currentAccount.id;
        else
            account.id = null;

        // Categories
        account.categories = adCat.getSelectedCategories();

        try {
            if (register.saveAccount(account)) {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                intent.putExtra("newid", account.id);
                setResult(RESULT_OK, intent);
                finish();
                finish();
            }
        } catch ( Register.ETExists ex ) {
            Toast.makeText(this, R.string.exists_already, Toast.LENGTH_SHORT).show();
        }
    }
}
