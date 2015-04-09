package com.example.stefano.ecotracker;

import android.content.Context;
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
        super(context, R.layout.layout_record_list, items);

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

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.layout_record_list, parent, false);

        // 3. Get the two text view from the rowView
        TextView ldate = (TextView) rowView.findViewById(R.id.lblRLDate);
        TextView laccount = (TextView) rowView.findViewById(R.id.lblRLAccount);
        TextView lamount = (TextView) rowView.findViewById(R.id.lblRLAmount);
        TextView lentity = (TextView) rowView.findViewById(R.id.lblRLEntity);

        // 4. Set the text for textView
        Record r = recordList.get(position);
        ldate.setText(r.date);
        laccount.setText(r.account.description);
        lentity.setText(r.entity.description);
        lamount.setText(r.getAmountString());

        // 5. retrn rowView
        return rowView;
    }

}
