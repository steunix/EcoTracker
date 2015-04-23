package com.dev.sr.myecotracker;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Class for monthly record
 */
public class ReportFragmentMonthBreakdown extends Fragment {

    int current_month;
    int current_year;
    View current_view;
    Register register;
    AccountBreakdownAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        register = new Register(v.getContext());

        Calendar c = Calendar.getInstance();

        current_month = c.get(Calendar.MONTH)+1;
        current_year  = c.get(Calendar.YEAR);

        ImageButton b = (ImageButton) v.findViewById(R.id.btnNext);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth();
            }
        });

        b = (ImageButton) v.findViewById(R.id.btnPrevious);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonth();
            }
        });

        adapter = new AccountBreakdownAdapter(v.getContext(), new ArrayList<AccountBreakdown>() );
        ListView list = (ListView) v.findViewById(R.id.lstRecords);
        list.setAdapter(adapter);

        current_view = v;
        updateTotals();
        return v;
    }

    public void nextMonth() {
        current_month++;
        if ( current_month==13 ) {
            current_month = 1;
            current_year++;
        }
        updateTotals();
    }

    public void previousMonth() {
        current_month--;
        if ( current_month==0 ) {
            current_month = 12;
            current_year--;
        }
        updateTotals();
    }

    public void updateTotals() {
        if ( current_view==null )
            return;

        TextView cur = (TextView) current_view.findViewById(R.id.txtCurrent);
        cur.setText(String.format("%02d/%4d", current_month, current_year));

        TextView ti = (TextView) current_view.findViewById(R.id.txtEntrate);
        TextView te = (TextView) current_view.findViewById(R.id.txtUscite);
        TextView ts = (TextView) current_view.findViewById(R.id.txtSaldo);

        float e = register.monthExpense(current_year,current_month);
        float i = register.monthIncome(current_year,current_month);

        float s = i - e;

        ti.setText(Helper.formatAmount(i));
        te.setText(Helper.formatAmount(-e));
        ts.setText(Helper.formatAmount(s));

        adapter.clear();
        Date d1 = Helper.isoToDate(String.format("%04d-%02d-01", current_year, current_month));
        Date d2 = Helper.isoToDate(String.format("%04d-%02d-99", current_year, current_month));
        ArrayList<AccountBreakdown> rec = register.getAccountBreakdown(d1, d2);
        adapter.addAll(rec);
    }

    public static ReportFragmentMonthBreakdown newInstance(String message) {
        ReportFragmentMonthBreakdown f = new ReportFragmentMonthBreakdown();
        Bundle b = new Bundle();
        b.putString("message", message);

        f.setArguments(b);

        return f;
    }
}
