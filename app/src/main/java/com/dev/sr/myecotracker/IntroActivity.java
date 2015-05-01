package com.dev.sr.myecotracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class IntroActivity extends ActionBarActivity {

    int step;
    Register register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        step = 1;
        register = new Register(this);
        updateText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_intro, menu);
        return true;
    }

    public void updateText() {
        TextView txt = (TextView)findViewById(R.id.txtIntro);

        if ( step==1 )
            txt.setText(getString(R.string.intro_accounts));
        if ( step==2 )
            txt.setText(getString(R.string.intro_categories));
        if ( step==3 )
            txt.setText(getString(R.string.intro_supplier));
        if ( step==4 )
            txt.setText(getString(R.string.intro_finished));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void goAhead(View v) {
        if ( step==1 ) {
            // Create accounts
            Account account = new Account();
            account.id = null;
            account.description = getString(R.string.intro_account_fuel);
            account.type = account.type_expense;
            account.usage = 0l;

            try {
                register.saveAccount(account);
            } catch(Exception e) { }

            account.description = getString(R.string.intro_account_maintenance);

            try {
                register.saveAccount(account);
            } catch(Exception e) { }

            account.description = getString(R.string.intro_account_insurance);

            try {
                register.saveAccount(account);
            } catch(Exception e) { }
        }

        if ( step==2 ) {
            // Create category
            Category category = new Category();
            category.id = null;
            category.description = getString(R.string.intro_category_car);

            try {
                register.saveCategory(category);
            } catch(Exception e) { }
        }

        if ( step==3 ) {
            // Create suppliers
            Entity entity = new Entity();
            entity.id = null;
            entity.description = getString(R.string.intro_entity_gas);

            try {
                register.saveEntity(entity);
            } catch(Exception e) { }
        }

        step++;
        if ( step==5 ) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putLong("first_run", 0l);
            editor.commit();

            finish();
            return;
        }
        updateText();
    }
}
