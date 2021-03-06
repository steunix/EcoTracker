package com.dev.sr.myecotracker;

import android.content.Context;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for reocrd list
 */
public class RecordListAdapter extends ArrayAdapter<Record> {

    private final Context context;
    private final ArrayList<Record> recordList;

    public RecordListAdapter(Context context, ArrayList<Record> items) {
        super(context, R.layout.layout_record, items);

        this.context = context;
        this.recordList = items;
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

        View rowView = inflater.inflate(R.layout.layout_record, parent, false);

        TextView ldate = (TextView) rowView.findViewById(R.id.lblRLDate);
        TextView laccount = (TextView) rowView.findViewById(R.id.lblRLAccount);
        TextView lamount = (TextView) rowView.findViewById(R.id.lblRLAmount);
        TextView lentity = (TextView) rowView.findViewById(R.id.lblRLEntity);
        TextView ldescr = (TextView) rowView.findViewById(R.id.lblRLDescription);

        Record r = recordList.get(position);

        if ( r.date!=null )
            ldate.setText(Helper.dateToString(r.date));
        else
            ldate.setText("");

        laccount.setText(r.account.description);
        lentity.setText(r.entity.description);
        lamount.setText(r.getAmountString());

        if ( r.description.length()>0 ) {
            ldescr.setText(r.description);
            ldescr.setVisibility(View.VISIBLE);
        } else {
            ldescr.setText("");
            ldescr.setVisibility(View.GONE);
        }

        if ( r.account.type.equals(r.account.type_income) )
            lamount.setTextAppearance(getContext(), R.style.income );
        if ( r.account.type.equals(r.account.type_expense) )
            lamount.setTextAppearance(getContext(), R.style.expense );
        return rowView;
    }

}
