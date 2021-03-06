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
 * Class for weekly report
 */
public class ReportFragmentWeek extends Fragment {

    int current_offset = 0;
    Calendar cal;
    View current_view;
    Register register;
    RecordListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        cal = Calendar.getInstance();

        register = Register.getInstance(v.getContext());

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

    private void nextWeek() {
        cal.add(Calendar.DAY_OF_MONTH, 7);
        current_offset+=7;
        updateTotals();
    }

    private void previousWeek() {
        cal.add(Calendar.DAY_OF_MONTH, -7);
        current_offset-=7;
        updateTotals();
    }

    public void updateTotals() {
        if ( current_view==null )
            return;

        Date d1 = Helper.getWeekStart(cal.getTime());
        Date d2 = Helper.getWeekEnd(cal.getTime());
        TextView cur = (TextView) current_view.findViewById(R.id.txtCurrent);
        cur.setText(Helper.dateToString(d1,d2));

        TextView ti = (TextView) current_view.findViewById(R.id.txtEntrate);
        TextView te = (TextView) current_view.findViewById(R.id.txtSRExpenses);
        TextView ts = (TextView) current_view.findViewById(R.id.txtSaldo);

        float e = register.weekExpense(cal.getTime());
        float i = register.weekIncome(cal.getTime());

        float s = i - e;

        ti.setText(Helper.formatAmount(i));
        te.setText(Helper.formatAmount(-e));
        ts.setText(Helper.formatAmount(s));

        adapter.clear();
        ArrayList<Record> rec = register.getRecordList(d1, d2, Register.DB_SORT.SORT_DATE_DESC);
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
