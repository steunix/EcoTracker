package com.example.stefano.ecotracker;

import java.util.Date;

/**
 * Class for a record
 */
public class Record {
    public Long    id;
    public String  date;
    public Account account;
    public Entity  entity;
    public Float   amount;
    public String  description;

    public Record() {
        id = 0l;
        date = null;
        account = null;
        entity = null;
        amount = 0f;
        description = "";
    }

    public String getAmountString() {
        if ( this.account.type.equals(this.account.type_income) )
            return "+"+String.format("%.02f", this.amount);
        else
            return "-"+String.format("%.02f", this.amount);
    }
}
