package com.dev.sr.myecotracker;

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

    public void updateResults(Account account, Entity entity, Date dateFrom, Date dateTo, Float amountFrom, Float amountTo) {
        adapter.clear();

        ArrayList<Record> list = register.getRecordList(account,entity,dateFrom,dateTo,amountFrom,amountTo, Register.DB_SORT.SORT_DATE_DESC);
        adapter.addAll(list);
    }

}
