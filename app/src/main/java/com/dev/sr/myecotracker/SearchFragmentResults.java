package com.dev.sr.myecotracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Stefano on 29/04/2015.
 */
public class SearchFragmentResults extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    RecordListAdapter adapter;
    Register register;

    public static SearchFragmentResults newInstance(int sectionNumber) {
        SearchFragmentResults fragment = new SearchFragmentResults();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragmentResults() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);

        register = new Register(rootView.getContext());

        adapter = new RecordListAdapter(rootView.getContext(),new ArrayList<Record>());
        ListView lst = (ListView) rootView.findViewById(R.id.lstResults);
        lst.setAdapter(adapter);

        return rootView;
    }

    public void updateResults() {
        adapter.clear();

        String s;
        Account account = new Account();
        Entity entity = new Entity();

        SharedPreferences sharedPref = getActivity().getPreferences(getActivity().getApplicationContext().MODE_PRIVATE);

        account.id = sharedPref.getLong("search_account_id",-1);
        entity.id = sharedPref.getLong("search_entity_id",-1);

        if ( account.id==-1 )
            account.id = null;
        if ( entity.id==-1 )
            entity.id = null;

        Date dateFrom = null;
        s = sharedPref.getString("search_date_from", "");
        if ( !s.equals("") )
            dateFrom = Helper.stringToDate(s);

        Date dateTo = null;
        s = sharedPref.getString("search_date_to", "");
        if ( !s.equals("") )
            dateTo = Helper.stringToDate(s);

        Float amountFrom = null;
        s = sharedPref.getString("search_amount_from", "");
        if ( !s.equals("") )
            amountFrom = Float.parseFloat(s);

        Float amountTo = null;
        s = sharedPref.getString("search_amount_to", "");
        if ( !s.equals("") )
            amountTo = Float.parseFloat(s);

        ArrayList<Record> list = register.getRecordList(account,entity,dateFrom,dateTo,amountFrom,amountTo, Register.DB_SORT.SORT_DATE_DESC);
        adapter.addAll(list);

        int i;
        Record r;

        Float exp = 0f, inc = 0f, bal = 0f;
        for ( i=0; i<list.size(); i++ ) {
            r = list.get(i);
            if ( r.account.type.equals(r.account.type_expense) )
                exp += r.amount;
            else
                inc += r.amount;
        }
        bal = inc-exp;

        r = new Record();

        r.account = new Account();
        r.entity = new Entity();
        r.account.type = r.account.type_income;
        r.account.description = getString(R.string.entrate);
        r.amount = inc;
        r.entity.description = "";
        r.date = null;

        adapter.addAll(r);

        r = new Record();
        r.account = new Account();
        r.entity = new Entity();
        r.account.type = r.account.type_expense;
        r.account.description = getString(R.string.uscite);
        r.amount = exp;
        r.entity.description = "";
        r.date = null;

        adapter.addAll(r);

        r = new Record();
        r.account = new Account();
        r.entity = new Entity();
        r.account.type = r.account.type_expense;
        r.account.description = getString(R.string.saldo);
        r.amount = bal;
        r.entity.description = "";
        r.date = null;

        adapter.addAll(r);
    }

}