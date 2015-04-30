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
public class AccountBreakdownAdapter extends ArrayAdapter<AccountBreakdown> {
    private final Context context;
    private final ArrayList<AccountBreakdown> accountList;

    public AccountBreakdownAdapter(Context context, ArrayList<AccountBreakdown> itemsArrayList) {

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
        View rowView = inflater.inflate(R.layout.layout_account_total, parent, false);

        // 3. Get the two text view from the rowView
        TextView lblDescr = (TextView) rowView.findViewById(R.id.lblATLAccount);
        TextView lblAmount = (TextView) rowView.findViewById(R.id.lblATLAmount);

        // 4. Set the text for textView
        AccountBreakdown a = accountList.get(position);
        lblDescr.setText(a.account.description);
        Float amt = ( a.account.type.equals(a.account.type_expense) ? -a.amount : a.amount);
        lblAmount.setText(Helper.formatAmount(amt));

        if ( a.account.type.equals(a.account.type_income) )
            lblAmount.setTextAppearance(getContext(), R.style.income );
        else
            lblAmount.setTextAppearance(getContext(), R.style.expense );

        // 5. retrn rowView
        return rowView;
    }

    /*
    public int getPosition(String description) {
        int i;
        for ( i=0; i<accountList.size(); i++ ) {
            if ( accountList.get(i).description == description )
                return i;
        }
        return -1;
    }
    */
}
