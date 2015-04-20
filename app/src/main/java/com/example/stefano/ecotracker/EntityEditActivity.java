package com.example.stefano.ecotracker;

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


public class EntityEditActivity extends ActionBarActivity {

    Entity currentEntity;
    String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_edit);

        Register db = new Register(this);

        Intent i = getIntent();
        mode = i.getExtras().getString("mode");
        if (mode.equals("edit") ) {
            // Edit existing account
            setTitle ( getString(R.string.title_activity_entity_edit));

            Long editEntity = i.getExtras().getLong("id");

            currentEntity = db.getEntity(editEntity);

            EditText descr = (EditText) findViewById(R.id.txtEntityDescr);
            descr.setText(currentEntity.description);
        } else {
            // New account
            setTitle(getString(R.string.title_activity_new_entity));
            currentEntity = new Entity();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entity_edit, menu);
        menu.findItem(R.id.action_deleteentity).setVisible(mode.equals("edit"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if ( id==R.id.action_saveentity ) {
            saveEntity(null);
            return true;
        }

        if ( id==R.id.action_deleteentity ) {
            deleteEntity(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteEntity(View v) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setMessage(R.string.entity_delete_warning)
                .setTitle(R.string.alert_warning);
        dlg.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Register db = new Register(getApplicationContext());
                        db.deleteEntity(currentEntity);
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

    public void saveEntity(View v) {
        Register db = new Register(this);
        Entity entity = new Entity();

        entity.description = ((EditText)findViewById(R.id.txtEntityDescr)).getText().toString();

        String mode = getIntent().getExtras().getString("mode");
        if ( mode.equals("edit") )
            entity.id = currentEntity.id;
        else
            entity.id = null;

        try {
            if (db.saveEntity(entity)) {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch ( Register.ETExists ex ) {
            Toast.makeText(this, R.string.exists_already, Toast.LENGTH_SHORT).show();
        }
    }

}
