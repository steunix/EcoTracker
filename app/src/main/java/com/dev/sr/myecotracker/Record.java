package com.dev.sr.myecotracker;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import java.util.Currency;
import java.util.Date;
import java.util.Locale;

/**
 * Class for a record
 */
public class Record {
    public Long    id;
    public Date    date;
    public Account account;
    public Entity  entity;
    public Float   amount;
    public String  description;
    public Location location;

    public Record() {
        id = 0l;
        date = null;
        account = null;
        entity = null;
        amount = 0f;
        description = "";
        location = null;
    }

    public String getAmountString() {
        String cs = Currency.getInstance(Locale.getDefault()).getSymbol();
        if ( this.account.type.equals(this.account.type_income) )
            return "+"+String.format("%.02f%s", this.amount, cs );
        else
            return "-"+String.format("%.02f%s", this.amount, cs);
    }

    public String getAbsAmountString() {
        String cs = Currency.getInstance (Locale.getDefault()).getSymbol();
        return String.format("%.02f%s", this.amount, cs);
    }

    public String getRawAmountString() {
        return String.format("%.02f", this.amount);
    }

    public String getLocationString() {
        return Helper.locationString(location);
    }

}
