package com.dev.sr.myecotracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Stefano on 30/04/2015.
 */
public class CategorySelectAdapter extends ArrayAdapter<CategorySelect> {
    private final Context context;
    private final ArrayList<CategorySelect> categoryList;

    public CategorySelectAdapter(Context context, ArrayList<CategorySelect> itemsArrayList) {

        super(context, R.layout.layout_category_choose, itemsArrayList);

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

        View rowView = inflater.inflate(R.layout.layout_category_choose, parent, false);

        CategorySelect cs = categoryList.get(position);

        TextView lblDescr = (TextView) rowView.findViewById(R.id.txtCCLDescription);
        lblDescr.setText(cs.category.description);

        CheckBox chkSel = (CheckBox) rowView.findViewById(R.id.chkCCLSelected);
        chkSel.setChecked(cs.selected);
        chkSel.setTag(cs);

        chkSel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((CategorySelect)buttonView.getTag()).selected = isChecked;
            }
        });
        return rowView;
    }

    public int getPosition(String description) {
        int i;
        for ( i=0; i<categoryList.size(); i++ ) {
            if ( categoryList.get(i).category.description.equals(description) )
                return i;
        }
        return -1;
    }

    public ArrayList<Category> getSelectedCategories() {
        ArrayList<Category> cat = new ArrayList<>();
        int i;
        for ( i=0; i<categoryList.size(); i++ ) {
            if ( categoryList.get(i).selected ) {
                cat.add(categoryList.get(i).category);
            }
        }
        return cat;
    }
}
