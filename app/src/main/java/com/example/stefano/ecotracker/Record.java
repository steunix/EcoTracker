package com.example.stefano.ecotracker;

import java.util.Date;

/**
 * Created by Stefano on 02/04/2015.
 */
public class Record {
    public Long    id;
    public String  date;
    public Account account;
    public Entity  entity;
    public Float   amount;
    public String  description;

    public void Record() {
        id = new Long(0);
        date = null;
        account = null;
        entity = null;
        amount = new Float(0);
        description = "";
    }

    public String getAmountString() {
        if ( this.account.type == this.account.type_income )
            return "+"+String.format("%.02f", this.amount);
        else
            return "-"+String.format("%.02f", this.amount);
    }
}
