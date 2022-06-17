package com.cookandroid.mobile_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Calendar;

public class HistoryFragment extends Fragment {
    CalendarView calendarView;
    TextView todo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_history, container, false);

        calendarView = (CalendarView) v.findViewById(R.id.calendarView);
        todo = (TextView) v.findViewById(R.id.tvTodo);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                    todo.setText("year : "+year+" month : "+month+" day : "+day);
            }
        });


        return v;

    }
}