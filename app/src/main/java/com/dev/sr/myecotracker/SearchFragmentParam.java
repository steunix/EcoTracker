package com.dev.sr.myecotracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Stefano on 29/04/2015.
 */
public class SearchFragmentParam extends Fragment {

    private View current_view;
    AccountListAdapter adAccount;
    EntityListAdapter adEntitiy;
    Spinner spnEntity;
    Spinner spnAccount;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static SearchFragmentParam newInstance(int sectionNumber) {
        SearchFragmentParam fragment = new SearchFragmentParam();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragmentParam() {
    }

    private void updateResults() {
        Account account = (Account) spnAccount.getSelectedItem();
        Entity  entity  = (Entity)  spnEntity.getSelectedItem();

        String s;
        s = ((TextView) (current_view.findViewById(R.id.txtDateFrom))).getText().toString();
        Date dtFrom = Helper.stringToDate(s);
        if ( s.length()>0 && dtFrom==null ) {
            Toast.makeText(current_view.getContext(), R.string.baddate, Toast.LENGTH_SHORT).show();
            return;
        }

        s = ((TextView) (current_view.findViewById(R.id.txtDateTo))).getText().toString();
        Date dtTo = Helper.stringToDate(s);
        if ( s.length()>0 && dtFrom==null ) {
            Toast.makeText(current_view.getContext(), R.string.baddate, Toast.LENGTH_SHORT).show();
            return;
        }

        Float amtFrom;
        try {
            s = ((TextView) (current_view.findViewById(R.id.txtAmountFrom))).getText().toString();
            amtFrom = Float.parseFloat(s);
        } catch ( Exception e ) {
            if ( s.length()>0 ) {
                Toast.makeText(current_view.getContext(), R.string.badamount, Toast.LENGTH_SHORT).show();
                return;
            }

            amtFrom = null;
        }

        Float amtTo;
        try {
            s = ((TextView) (current_view.findViewById(R.id.txtAmountTo))).getText().toString();
            amtTo = Float.parseFloat(s);
        } catch ( Exception e ) {
            if ( s.length()>0 ) {
                Toast.makeText(current_view.getContext(), R.string.badamount, Toast.LENGTH_SHORT).show();
                return;
            }

            amtTo = null;
        }

        ((SearchActivity)getActivity()).updateResults(
                account,
                entity,
                dtFrom,
                dtTo,
                amtFrom,
                amtTo
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_param, container, false);

        current_view = rootView;

        Activity activity = getActivity();
        Context context = activity.getApplicationContext();
        Register register = new Register(context);

        Account anyAccount = new Account();
        anyAccount.id = null;
        anyAccount.description = activity.getString(R.string.any);
        ArrayList<Account> accountList = register.getAccountsList(Register.DB_SORT.SORT_DESCRIPTION);
        accountList.add(0, anyAccount);

        Entity anyEntity = new Entity();
        anyEntity.id = null;
        anyEntity.description = activity.getString(R.string.any);
        ArrayList<Entity> entityList = register.getEntitiesList(Register.DB_SORT.SORT_DESCRIPTION, null);
        entityList.add(0, anyEntity);

        adAccount = new AccountListAdapter(context, accountList );
        adEntitiy = new EntityListAdapter(context, entityList);

        spnEntity = (Spinner) current_view.findViewById(R.id.spnEntity);
        spnAccount = (Spinner) current_view.findViewById(R.id.spnAccount);

        spnAccount.setAdapter(adAccount);
        spnEntity.setAdapter(adEntitiy);

        Button btn = (Button) current_view.findViewById(R.id.btnSearch);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateResults();
            }
        });
        return rootView;
    }

}
