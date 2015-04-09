package com.example.stefano.ecotracker;

import android.app.LauncherActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AccountListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);

        updateList();
    }

    private void updateList() {
        RegisterDB db = new RegisterDB(this);
        ArrayList<Account> accounts = db.getAccountsList(RegisterDB.DB_SORT.SORT_DESCRIPTION);

        ListView list = (ListView) findViewById(R.id.lstAccounts);
        AccountListAdapter ad = new AccountListAdapter(getApplicationContext(), accounts);
        list.setAdapter(ad);

        // Listener for click
        list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Account editAccount = (Account)parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), AccountEditActivity.class );
                intent.putExtra("mode", 1);
                intent.putExtra("account", editAccount.description);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_newaccount) {
            Intent intent = new Intent(this, AccountEditActivity.class );
            intent.putExtra("mode", 0);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
