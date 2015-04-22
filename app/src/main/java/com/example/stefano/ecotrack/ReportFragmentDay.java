package com.example.stefano.ecotrack;

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

/**
 * Fragment for daily report
 */
public class ReportFragmentDay extends Fragment {

    int current_offset = 0;
    Calendar cal;
    RecordListAdapter adapter;
    View current_view;
    Register register;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        register = new Register(v.getContext());

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

    private void nextDay() {
        cal.add(Calendar.DAY_OF_MONTH, 1);
        current_offset++;
        updateTotals();
    }

    private void previousDay() {
        cal.add(Calendar.DAY_OF_MONTH, -1);
        current_offset--;
        updateTotals();
    }

    public void updateTotals() {

        if ( current_view==null )
            return;

        String dt = Helper.dateToString(cal.getTime());
        TextView cur = (TextView) current_view.findViewById(R.id.txtCurrent);
        cur.setText(dt);

        TextView ti = (TextView) current_view.findViewById(R.id.txtEntrate);
        TextView te = (TextView) current_view.findViewById(R.id.txtUscite);
        TextView ts = (TextView) current_view.findViewById(R.id.txtSaldo);


        float e = register.dayExpense(cal.getTime());
        float i = register.dayIncome(cal.getTime());

        float s = i - e;

        ti.setText("+"+String.format("%.02f", i));
        te.setText("-" + String.format("%.02f", e));
        ts.setText((s > 0 ? "+" : "") + String.format("%.02f", s));

        adapter.clear();
        ArrayList<Record> rec = register.getRecordList(cal.getTime(), cal.getTime(), Register.DB_SORT.SORT_DATE_DESC);
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