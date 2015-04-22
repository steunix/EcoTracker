package com.dev.sr.myecotracker;

import android.content.Context;

/**
 * Created by Stefano on 02/04/2015.
 */
public class Account {

    public final String type_income = "INC";
    public final String type_expense= "EXP";

    public Long   id;
    public Long   parent;
    public String description;
    public String type;
    public Long   usage;

    public Account() {
        id = new Long(0);
        parent = new Long(0);
        description = "";
        type = "";
        usage = new Long(0);
    }

    public String getTypeDescription(Context context) {
        if ( type.equals(type_income) )
            return context.getString(R.string.type_income);
        if ( type.equals(type_expense) )
            return context.getString(R.string.type_expense);
        return "";
    }
}
