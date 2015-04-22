package com.dev.sr.myecotracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Helper class
 */
public class Helper {

    static String sqlString(String string) {
        return string.replace("'","''");
    }

    static Date isoToDate(String date) {
        String y = date.substring(0,4);
        String m = date.substring(5,7);
        String d = date.substring(8,10);

        if ( d.equals("99") ) {
            Integer max = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
            d = max.toString();
        }

        return new GregorianCalendar(Integer.parseInt(y),Integer.parseInt(m)-1,Integer.parseInt(d)).getTime();
    }

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
        return date.substring(6,10)+"-"+date.substring(3,5)+"-"+date.substring(0,2);
    }

    static String toIso(Date date) {
        String s = new SimpleDateFormat("dd/MM/yyyy").format(date);
        return Helper.toIso(s);
    }

    static Date getWeekStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int i = cal.getFirstDayOfWeek();
        while ( cal.get(Calendar.DAY_OF_WEEK) != i )
            cal.add(Calendar.DAY_OF_MONTH,-1);

        return cal.getTime();
    }

    static Date getWeekEnd(Date date) {
        Date ws = Helper.getWeekStart(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(ws);
        cal.add(Calendar.DAY_OF_MONTH,6);

        return cal.getTime();
    }
}
