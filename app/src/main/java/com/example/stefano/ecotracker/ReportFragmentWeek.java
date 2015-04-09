package com.example.stefano.ecotracker;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Class for weekly report
 */
public class ReportFragmentWeek extends Fragment {

    int current_offset = 0;
    Calendar cal;

    RecordListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);
        cal = Calendar.getInstance();

        ImageButton b = (ImageButton) v.findViewById(R.id.btnPrevious);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousWeek();
            }
        });

        b = (ImageButton) v.findViewById(R.id.btnNext);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWeek();
            }
        });

        adapter = new RecordListAdapter(v.getContext(), new ArrayList<Record>() );
        ListView list = (ListView) v.findViewById(R.id.lstRecords);
        list.setAdapter(adapter);

        updateTotals(v);
        return v;
    }

    private void nextWeek() {
        cal.add(Calendar.DAY_OF_MONTH, 7);
        current_offset+=7;
        updateTotals(getView());
    }

    private void previousWeek() {
        cal.add(Calendar.DAY_OF_MONTH, -7);
        current_offset-=7;
        updateTotals(getView());
    }

    public void updateTotals(View v) {
        Date d1 = Helper.getWeekStart(cal.getTime());
        Date d2 = Helper.getWeekEnd(cal.getTime());
        TextView cur = (TextView) v.findViewById(R.id.txtCurrent);
        cur.setText(Helper.dateToString(d1,d2));

        TextView ti = (TextView) v.findViewById(R.id.txtEntrate);
        TextView te = (TextView) v.findViewById(R.id.txtUscite);
        TextView ts = (TextView) v.findViewById(R.id.txtSaldo);

        RegisterDB db = new RegisterDB(v.getContext());
        float e = db.weekExpense(cal.getTime());
        float i = db.weekIncome(cal.getTime());

        float s = i - e;

        ti.setText("+" + String.format("%.02f", i));
        te.setText("-" + String.format("%.02f", e));
        ts.setText((s > 0 ? "+" : "") + String.format("%.02f", s));

        adapter.clear();
        ArrayList<Record> rec = db.getRecordList(d1, d2, RegisterDB.DB_SORT.SORT_DATE_DESC);
        adapter.addAll(rec);
    }

    public static ReportFragmentWeek newInstance(String message) {
        ReportFragmentWeek f = new ReportFragmentWeek();
        Bundle b = new Bundle();
        b.putString("message", message);

        f.setArguments(b);

        return f;
    }
}
