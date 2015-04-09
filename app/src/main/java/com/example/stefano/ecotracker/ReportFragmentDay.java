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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Fragment for daily report
 */
public class ReportFragmentDay extends Fragment {

    int current_offset = 0;
    Calendar cal;
    RecordListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        cal = Calendar.getInstance();

        ImageButton b = (ImageButton) v.findViewById(R.id.btnNext);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextDay();
            }
        });

        b = (ImageButton) v.findViewById(R.id.btnPrevious);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousDay();
            }
        });

        adapter = new RecordListAdapter(v.getContext(), new ArrayList<Record>() );
        ListView list = (ListView) v.findViewById(R.id.lstRecords);
        list.setAdapter(adapter);

        updateTotals(v);
        return v;
    }

    private void nextDay() {
        cal.add(Calendar.DAY_OF_MONTH, 1);
        current_offset++;
        updateTotals(getView());
    }

    private void previousDay() {
        cal.add(Calendar.DAY_OF_MONTH, -1);
        current_offset--;
        updateTotals(getView());
    }

    public void updateTotals(View v) {
        String dt = Helper.dateToString(cal.getTime());
        TextView cur = (TextView) v.findViewById(R.id.txtCurrent);
        cur.setText(dt);

        TextView ti = (TextView) v.findViewById(R.id.txtEntrate);
        TextView te = (TextView) v.findViewById(R.id.txtUscite);
        TextView ts = (TextView) v.findViewById(R.id.txtSaldo);

        RegisterDB db = new RegisterDB(v.getContext());
        float e = db.dayExpense(cal.getTime());
        float i = db.dayIncome(cal.getTime());

        float s = i - e;

        ti.setText("+"+String.format("%.02f", i));
        te.setText("-" + String.format("%.02f", e));
        ts.setText((s > 0 ? "+" : "") + String.format("%.02f", s));

        adapter.clear();
        ArrayList<Record> rec = db.getRecordList(cal.getTime(), cal.getTime(), RegisterDB.DB_SORT.SORT_DATE_DESC);
        adapter.addAll(rec);
    }

    public static ReportFragmentDay newInstance(String message) {
        ReportFragmentDay f = new ReportFragmentDay();
        Bundle b = new Bundle();
        b.putString("message", message);

        f.setArguments(b);

        return f;
    }

}