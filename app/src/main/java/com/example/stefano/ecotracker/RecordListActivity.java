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


public class RecordListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        updateList();
    }

    public void updateList() {
        Register db = new Register(this);

        ArrayList<Record> rlist;
        rlist = db.getRecordList(Register.DB_SORT.SORT_DATE);

        RecordListAdapter ad = new RecordListAdapter(this, rlist);
        ListView list = (ListView) findViewById(R.id.lstRecordList);
        list.setAdapter(ad);

        list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record editRecord = (Record) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), RecordEditActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("id", editRecord.id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
