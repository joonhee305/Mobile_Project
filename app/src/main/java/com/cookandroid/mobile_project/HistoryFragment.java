package com.cookandroid.mobile_project;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;

public class HistoryFragment extends Fragment {

    SQLiteDatabase sqLiteDatabase;
    SQLiteOpenHelper myHelper;
    LinearLayout linearHistory;
    CalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_history, container, false);
        myHelper=new myDBHelper(getActivity());
        calendarView = (CalendarView) v.findViewById(R.id.calendarView);
        sqLiteDatabase=myHelper.getWritableDatabase();
        linearHistory = v.findViewById(R.id.linearHistory);

        long cur = calendarView.getDate();

        calendarView.setDate(cur-86400*1000L);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String sMonth = Integer.toString(month+1);
                String sDay = Integer.toString(day);
                if(month<10) sMonth = "0"+Integer.toString(month+1);
                if(day<10) sDay = "0"+Integer.toString(day);
                linearHistory.removeAllViews();
                String date = year+sMonth+sDay;
                Cursor historyCursor=sqLiteDatabase.rawQuery("select * from historyTBL where hDate = ('"+date+"')", null);
                while (historyCursor.moveToNext()) {
                    Button btn=new Button(getActivity());
                    ImageView myImage = new ImageView(getActivity());

                    String hName=historyCursor.getString(1);
                    int hTime=historyCursor.getInt(3);
                    String hPath=historyCursor.getString(4);

                    btn.setText(hName+" "+hTime);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File storageDir = new File(getActivity().getFilesDir() + "/capture");
                            String filename = hPath + ".jpg";
                            File file = new File(storageDir, filename);
                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

                                Dialog myPicture;
                                myPicture=new Dialog(getActivity());
                                myPicture.setContentView(R.layout.dialog_mypicture);

                                //다이얼 로그 내 버튼
                                TextView dlTitle=myPicture.findViewById(R.id.dlMyPictureTitle);
                                ImageView dlPicture=myPicture.findViewById(R.id.dlMyPictureImage);
                                Button dlFinish=myPicture.findViewById(R.id.dlMyPictureFinish);
                                dlFinish.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myPicture.dismiss();
                                    }
                                });
                                dlPicture.setImageBitmap(bitmap);
                                dlTitle.setText(hName);
                                myPicture.show();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        }

                    });
                    linearHistory.addView(btn);
                }

            }

        });

        return v;

    }
}