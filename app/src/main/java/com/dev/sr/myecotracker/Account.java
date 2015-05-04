package com.dev.sr.myecotracker;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Stefano on 02/04/2015.
 */
public class Account {

    public final String type_income = "INC";
    public final String type_expense= "EXP";
    public final String type_balance= "BAL";

    public Long   id;
    public String description;
    public String type;
    public Long   usage;
    public ArrayList<Category> categories;

    public Account() {
        id = new Long(0);
        description = "";
        type = "";
        usage = new Long(0);
        categories = null;
    }

    public String getTypeDescription(Context context) {
        if ( type.equals(type_income) )
            return context.getString(R.string.type_income);
        if ( type.equals(type_expense) )
            return context.getString(R.string.type_expense);
        return "";
    }

    public void addCategory(Category category) {
        if ( categories == null )
            categories = new ArrayList<>();
        categories.add(category);
    }
}
