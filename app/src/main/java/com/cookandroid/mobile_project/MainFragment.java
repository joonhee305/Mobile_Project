package com.cookandroid.mobile_project;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class MainFragment extends Fragment {
    SQLiteDatabase sqLiteDatabase;
    SQLiteOpenHelper myHelper;
    HashMap<String,Integer> mapDate;
    String month,day,d,nowDate;
    LinearLayout layoutDoing, layoutExercise,layoutMedicine;
    public SharedPreferences prefs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_main, container, false);
        Button btn_addList = (Button) v.findViewById(R.id.btnAddList);
        layoutDoing=v.findViewById(R.id.layoutDoing);
        layoutExercise=v.findViewById(R.id.layoutExercise);
        layoutMedicine=v.findViewById(R.id.layoutMedicine);
        mapDate=new HashMap<String,Integer>();
        setMapDate(mapDate);
        myHelper=new myDBHelper(getActivity());
        sqLiteDatabase=myHelper.getWritableDatabase();
        prefs= getActivity().getSharedPreferences("Pref", Context.MODE_PRIVATE);
        //임시

        String lastDate=prefs.getString("lastDate"," ");


        //날짜
        long now=System.currentTimeMillis();
        Date date= new Date(now);
        SimpleDateFormat sdfMonth=new SimpleDateFormat("MM");
        SimpleDateFormat sdfDay=new SimpleDateFormat("dd");
        SimpleDateFormat sdfD=new SimpleDateFormat("EE");

        Cursor toDayCursor=sqLiteDatabase.rawQuery("select * from toDayTBL",null);
        month=sdfMonth.format(date);
        day=sdfDay.format(date);
        d=sdfD.format(date);
        nowDate=month+day+d;
        if(!nowDate.equals(lastDate) || toDayCursor.getCount()==0){
            lastDate=nowDate;
            toDayCursor.close();
            sqLiteDatabase.execSQL("delete from toDayTBL");
            Cursor routineCursor=sqLiteDatabase.rawQuery("select * from routineTBL",null);
            while(routineCursor.moveToNext()){
                String tType,tName,tTime;
                tType=routineCursor.getString(0);
                tName=routineCursor.getString(1);
                int rDate=routineCursor.getInt(2);
                tTime=routineCursor.getString(3);
                if((rDate & mapDate.get(d))!=0){
                    sqLiteDatabase.execSQL("insert into toDayTBL values('"+tType+"','"+tName+"','"+tTime+"');");
                }
            }
            routineCursor.close();
        }
        //데이터 삽입
        toDayCursor=sqLiteDatabase.rawQuery("select * from toDayTBL where tType=='식사' or tType=='일정'",null);
        while(toDayCursor.moveToNext()){
            Button btn=new Button(getActivity());
            String tName=toDayCursor.getString(1);
            String tTime=toDayCursor.getString(2);
            String value=tName+" "+tTime;
            btn.setText(value);
            layoutDoing.addView(btn);
        }


        toDayCursor=sqLiteDatabase.rawQuery("select * from toDayTBL where tType=='운동'",null);
        while(toDayCursor.moveToNext()){
            Button btn=new Button(getActivity());
            String tName=toDayCursor.getString(1);
            String tTime=toDayCursor.getString(2);
            String value=tName+" "+tTime;
            btn.setText(value);
            layoutExercise.addView(btn);
        }


        toDayCursor=sqLiteDatabase.rawQuery("select * from toDayTBL where tType=='복약'",null);
        while(toDayCursor.moveToNext()){
            Button btn=new Button(getActivity());
            String tName=toDayCursor.getString(1);
            String tTime=toDayCursor.getString(2);
            String value=tName+" "+tTime;
            btn.setText(value);
            layoutMedicine.addView(btn);
        }


        btn_addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), AddListActivity.class);
//                startActivity(intent);
                Toast.makeText(getActivity(),"이건가?",Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
    void setMapDate(HashMap<String,Integer> mapDate){
        mapDate.put("월",0);mapDate.put("Mon",0);
        mapDate.put("화",1);mapDate.put("Tue",1);
        mapDate.put("수",2);mapDate.put("Wed",2);
        mapDate.put("목",3);mapDate.put("Thu",3);
        mapDate.put("금",4);mapDate.put("Fri",4);
        mapDate.put("토",5);mapDate.put("Sat",5);
        mapDate.put("일",6);mapDate.put("Sun",6);
    }
}