package com.example.stefano.ecotracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for entity list
 */
public class EntityListAdapter extends ArrayAdapter<Entity> {
    private final Context context;
    private final ArrayList<Entity> entityList;

    public EntityListAdapter(Context context, ArrayList<Entity> itemsArrayList) {

        super(context, R.layout.layout_entity, itemsArrayList);

        this.context = context;
        this.entityList = itemsArrayList;
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

        View rowView = inflater.inflate(R.layout.layout_entity, parent, false);

        TextView lblDescr = (TextView) rowView.findViewById(R.id.description);
        TextView lblType = (TextView) rowView.findViewById(R.id.type);

        Entity a = entityList.get(position);
        lblDescr.setText(a.description);

        return rowView;
    }

    public int getPosition(String description) {
        int i;
        for ( i=0; i<entityList.size(); i++ ) {
            if ( entityList.get(i).description.equals(description) )
                return i;
        }
        return -1;
    }
}
