package com.dev.sr.myecotracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class CategoryEditActivity extends ActionBarActivity {

    Register register;
    String   mode;
    Category currentCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        register = new Register(this);

        Intent i = getIntent();
        mode = i.getExtras().getString("mode");
        if (mode.equals("edit") ) {
            // Edit existing account
            setTitle ( getString(R.string.title_activity_category_edit));

            Long editCategory = i.getExtras().getLong("id");

            currentCategory = register.getCategory(editCategory);

            EditText descr = (EditText) findViewById(R.id.txtCEDescr);
            descr.setText(currentCategory.description);
        } else {
            // New account
            setTitle(getString(R.string.title_activity_new_category));
            currentCategory = new Category();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_edit, menu);
        if ( mode.equals("new") )
            menu.findItem(R.id.action_deletecategory).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id==R.id.action_savecategory ) {
            saveCategory();
        }

        if ( id==R.id.action_deletecategory ) {
            deleteCategory();
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

    public void deleteCategory() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setMessage(R.string.category_delete_warning)
                .setTitle(R.string.alert_warning);
        dlg.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        register.deleteCategory(currentCategory);
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

    public void saveCategory() {
        Category category = new Category();

        category.description = ((EditText) findViewById(R.id.txtCEDescr)).getText().toString().trim();

        if (category.description.length() == 0) {
            Toast.makeText(this, R.string.baddescription, Toast.LENGTH_SHORT).show();
            return;
        }

        String mode = getIntent().getExtras().getString("mode");
        if ( mode.equals("edit") )
            category.id = currentCategory.id;
        else
            category.id = null;

        try {
            if (register.saveCategory(category)) {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch ( Register.ETExists ex ) {
            Toast.makeText(this, R.string.exists_already, Toast.LENGTH_SHORT).show();
        }
    }

}
