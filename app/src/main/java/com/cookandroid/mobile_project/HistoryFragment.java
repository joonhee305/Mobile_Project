package com.cookandroid.mobile_project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

public class HistoryFragment extends Fragment {

    SQLiteDatabase sqLiteDatabase;
    SQLiteOpenHelper myHelper;
    LinearLayout linearHistory;
    CalendarView calendarView;
    Button btnImgTest;
    TextView todo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_history, container, false);
        myHelper=new myDBHelper(getActivity());
        calendarView = (CalendarView) v.findViewById(R.id.calendarView);
        todo = (TextView) v.findViewById(R.id.tvTodo);
        sqLiteDatabase=myHelper.getWritableDatabase();
        linearHistory = v.findViewById(R.id.linearHistory);
        btnImgTest = v.findViewById(R.id.btnImgTest);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                Cursor historyCursor=sqLiteDatabase.rawQuery("select * from historyTBL where hDate = ('"+year+""+month+""+day+"')",null);
                while (historyCursor.moveToNext()) {
                    Button btn=new Button(getActivity());
                    ImageView myImage = new ImageView(getActivity());

                    String hName=historyCursor.getString(1);
                    int hTime=historyCursor.getInt(3);
                    String hPath=historyCursor.getString(4);

//                    String value=tName+" "+historyCursor(tTime);
//                    btn.setText(value);
//                    btn.setId(tId);
                    btn.setText(hName+" "+hTime);
                    btn.setOnClickListener(new View.OnClickListener() {
                        Integer btnId=btn.getId();
                        @Override
                        public void onClick(View v) {
                            File file = new File(hPath);
                            if(file.exists()){
                                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                myImage.setImageBitmap(myBitmap);


                            }
                        }

                    });
                    linearHistory.addView(btn);
                    linearHistory.addView(myImage);
                }

            }

        });
        btnImgTest.setOnClickListener(new View.OnClickListener() {
            int a = 0;
            @Override
            public void onClick(View view) {
//                sqLiteDatabase.execSQL("insert into historyTBL values('일정','test','20220617',a,' ');");
//                a++;
            }
        });


        return v;

    }
}