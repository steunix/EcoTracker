package com.example.stefano.myecotracker;

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

    Register register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        register = new Register(this);
        updateList();
    }

    public void updateList() {
        ArrayList<Record> rlist;
        rlist = register.getRecordList(Register.DB_SORT.SORT_DATE_DESC);

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

        if (id == R.id.action_info) {
            Intent i = new Intent(this, InfoActivity.class);
            startActivity(i);
        }

        if ( id == R.id.action_newreg ) {
            Intent intent = new Intent(this, RecordEditActivity.class);
            intent.putExtra("mode","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
