package com.dev.sr.myecotracker;

/**
 * Created by Stefano on 30/04/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for category list
 */
public class CategoryListAdapter extends ArrayAdapter<Category> {
    private final Context context;
    private final ArrayList<Category> categoryList;

    public CategoryListAdapter(Context context, ArrayList<Category> itemsArrayList) {

        super(context, R.layout.layout_category, itemsArrayList);

        this.context = context;
        this.categoryList = itemsArrayList;
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

        View rowView = inflater.inflate(R.layout.layout_category, parent, false);

        TextView lblDescr = (TextView) rowView.findViewById(R.id.txtCLDescription);

        Category a = categoryList.get(position);
        lblDescr.setText(a.description);

        return rowView;
    }

    public int getPosition(String description) {
        int i;
        for ( i=0; i<categoryList.size(); i++ ) {
            if ( categoryList.get(i).description.equals(description) )
                return i;
        }
        return -1;
    }
}
