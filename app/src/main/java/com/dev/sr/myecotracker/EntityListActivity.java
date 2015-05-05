package com.dev.sr.myecotracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class EntityListActivity extends ActionBarActivity {

    Register register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_list);

        register = new Register(this);
        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entity_list, menu);
        return true;
    }

    public void updateList() {
        ArrayList<Entity> entities = register.getEntitiesList(Register.DB_SORT.SORT_DESCRIPTION, null);

        ListView list = (ListView) findViewById(R.id.lstEntities);
        EntityListAdapter ad = new EntityListAdapter(getApplicationContext(), entities);
        list.setAdapter(ad);

        // Listener for click
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entity editEntity = (Entity) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), EntityEditActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("id", editEntity.id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id == R.id.action_newentity ) {
            Intent intent = new Intent(this, EntityEditActivity.class );
            intent.putExtra("mode", "new");
            startActivity(intent);
        }

        if ( id == R.id.action_accountlist ) {
            Intent intent = new Intent(this, AccountListActivity.class);
            startActivity(intent);
        }

        if ( id == R.id.action_categorylist ) {
            Intent intent = new Intent(this, CategoryListActivity.class);
            startActivity(intent);
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
}
