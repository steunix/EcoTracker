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


public class EntityEditActivity extends ActionBarActivity {

    Entity currentEntity;
    String mode;
    Register register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_edit);

        register = new Register(this);

        Intent i = getIntent();
        mode = i.getExtras().getString("mode");
        if (mode.equals("edit") ) {
            // Edit existing account
            setTitle ( getString(R.string.title_activity_entity_edit));

            Long editEntity = i.getExtras().getLong("id");

            currentEntity = register.getEntity(editEntity);

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
        if ( mode.equals("new") || currentEntity.id==0 )
            menu.findItem(R.id.action_deleteentity).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_info) {
            Intent i = new Intent(this, InfoActivity.class);
            startActivity(i);
        }

        if ( id==R.id.action_saveentity ) {
            saveEntity();
        }

        if ( id==R.id.action_deleteentity ) {
            deleteEntity();
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteEntity() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setMessage(R.string.entity_delete_warning)
                .setTitle(R.string.alert_warning);
        dlg.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        register.deleteEntity(currentEntity);
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

    public void saveEntity() {
        Entity entity = new Entity();

        entity.description = ((EditText) findViewById(R.id.txtEntityDescr)).getText().toString().trim();

        if (entity.description.length() == 0) {
            Toast.makeText(this, R.string.baddescription, Toast.LENGTH_SHORT).show();
            return;
        }

        String mode = getIntent().getExtras().getString("mode");
        if ( mode.equals("edit") )
            entity.id = currentEntity.id;
        else
            entity.id = null;

        try {
            if (register.saveEntity(entity)) {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch ( Register.ETExists ex ) {
            Toast.makeText(this, R.string.exists_already, Toast.LENGTH_SHORT).show();
        }
    }

}
