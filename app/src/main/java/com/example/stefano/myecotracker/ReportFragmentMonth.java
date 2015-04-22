package com.example.stefano.myecotracker;

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
public class ReportFragmentMonth extends Fragment {

    int current_month;
    int current_year;
    View current_view;
    Register register;
    RecordListAdapter adapter;

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

        adapter = new RecordListAdapter(v.getContext(), new ArrayList<Record>() );
        ListView list = (ListView) v.findViewById(R.id.lstRecords);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record editRecord = (Record) parent.getItemAtPosition(position);
                Intent intent = new Intent(current_view.getContext(), RecordEditActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("id", editRecord.id);
                startActivity(intent);
            }
        });

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

        ti.setText("+"+String.format("%.02f", i));
        te.setText("-" + String.format("%.02f", e));
        ts.setText((s > 0 ? "+" : "") + String.format("%.02f", s));

        adapter.clear();
        Date d1 = Helper.isoToDate(String.format("%04d-%02d-01", current_year, current_month));
        Date d2 = Helper.isoToDate(String.format("%04d-%02d-99", current_year, current_month));
        ArrayList<Record> rec = register.getRecordList(d1, d2, Register.DB_SORT.SORT_DATE_DESC);
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
