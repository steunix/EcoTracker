package com.example.stefano.ecotrack;

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
        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.layout_account, parent, false);

        // 3. Get the two text view from the rowView
        TextView lblDescr = (TextView) rowView.findViewById(R.id.description);
        TextView lblType = (TextView) rowView.findViewById(R.id.type);

        // 4. Set the text for textView
        Account a = accountList.get(position);
        lblDescr.setText(a.description);
        lblType.setText(a.getTypeDescription(context));

        // 5. retrn rowView
        return rowView;
    }

    public int getPosition(String description) {
        int i;
        for ( i=0; i<accountList.size(); i++ ) {
            if ( accountList.get(i).description == description )
                return i;
        }
        return -1;
    }
}
