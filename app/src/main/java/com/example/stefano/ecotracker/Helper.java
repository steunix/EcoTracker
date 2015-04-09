package com.example.stefano.ecotracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Stefano on 09/04/2015.
 */
public class Helper {

    static String dateToString(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    static String dateToString(Date date1, Date date2) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date1)+" - "+
                new SimpleDateFormat("dd/MM/yyyy").format(date2);
    }

    static String isoToString(String iso) {
        return iso.substring(8,10)+"/"+iso.substring(5, 7)+"/"+iso.substring(0, 5);
    }

    static String toIso(String date) {
        String s = date.substring(6,10)+"-"+date.substring(3,5)+"-"+date.substring(0,2);
        return s;
    }

    static String toIso(Date date) {
        String s = new SimpleDateFormat("dd/MM/yyyy").format(date);
        return Helper.toIso(s);
    }

    static Date getWeekStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK,1);
        return cal.getTime();
    }

    static Date getWeekEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK,7);
        return cal.getTime();
    }
}
