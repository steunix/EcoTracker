package com.dev.sr.myecotracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Stefano on 03/04/2015.
 */
public class AccountListAdapter extends ArrayAdapter<Account> {
    private final Context context;
    private final ArrayList<Account> accountList;

    public AccountListAdapter(Context context, ArrayList<Account> itemsArrayList) {

        super(context, R.layout.layout_account, itemsArrayList);

        this.context = context;
        this.accountList = itemsArrayList;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.layout_account, parent, false);

        TextView lblDescr = (TextView) rowView.findViewById(R.id.txtALDescription);
        TextView lblType = (TextView) rowView.findViewById(R.id.txtALType);

        Account a = accountList.get(position);
        lblDescr.setText(a.description);
        lblType.setText(a.getFullDescription(context));

        return rowView;
    }

    public int getPosition(String description) {
        int i;
        for ( i=0; i<accountList.size(); i++ ) {
            if ( accountList.get(i).description.equals(description) )
                return i;
        }
        return -1;
    }
}
