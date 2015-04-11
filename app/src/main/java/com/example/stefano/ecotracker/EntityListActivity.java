package com.example.stefano.ecotracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class EntityListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_list);

        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entity_list, menu);
        return true;
    }

    public void updateList() {
        Register db = new Register(this);
        ArrayList<Entity> entities = db.getEntitiesList(Register.DB_SORT.SORT_DESCRIPTION);

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

        if (id == R.id.action_settings) {
            return true;
        }

        if ( id == R.id.action_newentity ) {
            Intent intent = new Intent(this, EntityEditActivity.class );
            intent.putExtra("mode", "new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
