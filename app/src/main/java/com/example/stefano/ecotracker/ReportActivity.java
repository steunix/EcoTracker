package com.example.stefano.ecotracker;

import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ReportActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_report);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        // If no accounts or entities exists, then direct to the proper page
        Register db = new Register(this);
        ArrayList<Account> accounts = db.getAccountsList(Register.DB_SORT.SORT_DESCRIPTION);

        if ( accounts.size()==0 ) {
            AlertDialog.Builder bld = new AlertDialog.Builder(this);
            bld.setTitle(R.string.alert_warning)
                    .setMessage(R.string.no_accounts_initial);
            bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            bld.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Intent intent = new Intent(getApplicationContext(), AccountEditActivity.class );
                    intent.putExtra("mode", "new");
                    startActivity(intent);
                }
            });
            AlertDialog dlg = bld.create();
            dlg.show();
            return;
        }

        ArrayList<Entity> entities = db.getEntitiesList(Register.DB_SORT.SORT_DESCRIPTION);
        if ( entities.size()==0 ) {
            AlertDialog.Builder bld = new AlertDialog.Builder(this);
            bld.setTitle(R.string.alert_warning)
                    .setMessage(R.string.no_entities_initial);
            bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            bld.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Intent intent = new Intent(getApplicationContext(), EntityEditActivity.class );
                    intent.putExtra("mode", "new");
                    startActivity(intent);
                }
            });
            AlertDialog dlg = bld.create();
            dlg.show();
            return;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if ( id == R.id.action_newreg ) {
            Intent intent = new Intent(this, RecordEditActivity.class);
            intent.putExtra("mode","new");
            startActivity(intent);
        }

        if ( id == R.id.action_accountlist ) {
            Intent intent = new Intent(this, AccountListActivity.class);
            startActivity(intent);
        }

        if ( id == R.id.action_entitylist ) {
            Intent intent = new Intent(this, EntityListActivity.class);
            startActivity(intent);
        }

        if ( id == R.id.action_recordlist ) {
            Intent intent = new Intent(this, RecordListActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        FragmentManager fm = getSupportFragmentManager();

        ReportFragmentDay d = (ReportFragmentDay)fm.findFragmentByTag("android:switcher:"+mViewPager.getId()+":0");
        if ( d!=null )
            d.updateTotals();

        ReportFragmentWeek w = (ReportFragmentWeek)fm.findFragmentByTag("android:switcher:" + mViewPager.getId() + ":1");
        if ( w!=null )
            w.updateTotals();

        ReportFragmentMonth m = (ReportFragmentMonth) fm.findFragmentByTag("android:switcher:" + mViewPager.getId() + ":2");
        if ( m!=null )
            m.updateTotals();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch ( position ) {
                case 0:
                    return ReportFragmentDay.newInstance("1");
                case 1:
                    return ReportFragmentWeek.newInstance("2");
                case 2:
                    return ReportFragmentMonth.newInstance("3");
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.report_section_day);
                case 1:
                    return getString(R.string.report_section_week);
                case 2:
                    return getString(R.string.report_section_month);
            }
            return null;
        }
    }
}
