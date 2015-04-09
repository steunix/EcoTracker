package com.example.stefano.ecotracker;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Stefano on 09/04/2015.
 */
public class ReportFragmentMonth extends Fragment {

    int current_month;
    int current_year;

    RecordListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        Calendar c = Calendar.getInstance();

        current_month = c.get(Calendar.MONTH)+1;
        current_year  = c.get(Calendar.YEAR);

        Button b = (Button) v.findViewById(R.id.btnNext);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth();
            }
        });

        b = (Button) v.findViewById(R.id.btnPrevious);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonth();
            }
        });

        adapter = new RecordListAdapter(v.getContext(), new ArrayList<Record>() );
        ListView list = (ListView) v.findViewById(R.id.lstRecords);
        list.setAdapter(adapter);

        updateTotals(v);
        return v;
    }

    public void nextMonth() {
        current_month++;
        if ( current_month==13 ) {
            current_month = 1;
            current_year++;
        }
        updateTotals(getView());
    }

    public void previousMonth() {
        current_month--;
        if ( current_month==0 ) {
            current_month = 12;
            current_year--;
        }
        updateTotals(getView());
    }

    public void updateTotals(View v) {
        TextView cur = (TextView) v.findViewById(R.id.txtCurrent);
        cur.setText(String.format("%02d/%4d", current_month, current_year));

        TextView ti = (TextView) v.findViewById(R.id.txtEntrate);
        TextView te = (TextView) v.findViewById(R.id.txtUscite);
        TextView ts = (TextView) v.findViewById(R.id.txtSaldo);

        RegisterDB db = new RegisterDB(v.getContext());
        float e = db.monthExpense(current_year,current_month);
        float i = db.monthIncome(current_year,current_month);

        float s = i - e;

        ti.setText("+"+String.format("%.02f", i));
        te.setText("-" + String.format("%.02f", e));
        ts.setText((s > 0 ? "+" : "") + String.format("%.02f", s));

        adapter.clear();
        Date d1 = Helper.isoToDate(String.format("%04d-%02d-01", current_year, current_month));
        Date d2 = Helper.isoToDate(String.format("%04d-%02d-99", current_year, current_month));
        ArrayList<Record> rec = db.getRecordList(d1, d2, RegisterDB.DB_SORT.SORT_DATE_DESC);
        adapter.addAll(rec);
    }

    public static ReportFragmentMonth newInstance(String message) {
        ReportFragmentMonth f = new ReportFragmentMonth();
        Bundle b = new Bundle();
        b.putString("message", message);

        f.setArguments(b);

        return f;
    }
}
