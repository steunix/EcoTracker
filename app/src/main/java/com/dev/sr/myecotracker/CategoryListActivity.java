package com.dev.sr.myecotracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class CategoryListActivity extends ActionBarActivity {

    Register register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        register = new Register(getApplicationContext());
        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_list, menu);
        return true;
    }

    public void updateList() {
        ArrayList<Category> categories = register.getCategoriesList();

        ListView list = (ListView) findViewById(R.id.lstCategories);
        CategoryListAdapter ad = new CategoryListAdapter(getApplicationContext(), categories);
        list.setAdapter(ad);

        // Listener for click
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category editCategory = (Category) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), CategoryEditActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("id", editCategory.id);
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

        if (id == R.id.action_info) {
            Intent i = new Intent(this, InfoActivity.class);
            startActivity(i);
        }

        if (id == R.id.action_newcategory) {
            Intent intent = new Intent(this, CategoryEditActivity.class );
            intent.putExtra("mode", "new");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
