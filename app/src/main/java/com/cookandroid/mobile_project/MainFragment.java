package com.cookandroid.mobile_project;

import android.app.Activity;
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

import android.util.Log;
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
    TextView testText;
    public SharedPreferences prefs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_main, container, false);
        Button btn_addList = (Button) v.findViewById(R.id.btnAddList);
        testText=v.findViewById(R.id.testText);
        layoutDoing=v.findViewById(R.id.layoutDoing);
        layoutExercise=v.findViewById(R.id.layoutExercise);
        layoutMedicine=v.findViewById(R.id.layoutMedicine);
        mapDate=new HashMap<String,Integer>();
        setMapDate(mapDate);
        myHelper=new myDBHelper(getActivity());
        sqLiteDatabase=myHelper.getWritableDatabase();
        prefs= getActivity().getSharedPreferences("Pref", Context.MODE_PRIVATE);

//        Intent intent = getActivity().getIntent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);


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

        String asdf="lastDate : "+lastDate+"nowDate : "+nowDate;

        if(!nowDate.equals(lastDate) || toDayCursor.getCount()==0){
            prefs.edit().putString("lastDate",nowDate).apply();
            toDayCursor.close();
            sqLiteDatabase.execSQL("delete from toDayTBL");
            Cursor routineCursor=sqLiteDatabase.rawQuery("select * from routineTBL",null);

            asdf+="\n루틴count : "+routineCursor.getCount();
            while(routineCursor.moveToNext()){
                String tType,tName;
                int tTime;
                tType=routineCursor.getString(0);
                tName=routineCursor.getString(1);
                int rDate=routineCursor.getInt(2);
                tTime=routineCursor.getInt(3);
                if((rDate & mapDate.get(d))!=0){
                    sqLiteDatabase.execSQL("insert into toDayTBL values('"+tType+"','"+tName+"',"+tTime+");");
                }
            }
            routineCursor.close();
        }
        //데이터 삽입
        toDayCursor=sqLiteDatabase.rawQuery("select * from toDayTBL where tType=='식사' or tType=='일정' order by tTime",null);
        while(toDayCursor.moveToNext()){
            Button btn=new Button(getActivity());
            String tName=toDayCursor.getString(1);
            int tTime=toDayCursor.getInt(2);

            String value=tName+" "+timeToString(tTime);
            btn.setText(value);
            btn.setOnClickListener(new View.OnClickListener() {
                Integer i=btn.getId();
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),i.toString(),Toast.LENGTH_SHORT).show();
                }
            });
            layoutDoing.addView(btn);
        }


        toDayCursor=sqLiteDatabase.rawQuery("select * from toDayTBL where tType=='운동' order by tTime",null);
        while(toDayCursor.moveToNext()){
            Button btn=new Button(getActivity());
            String tName=toDayCursor.getString(1);
            int tTime=toDayCursor.getInt(2);

            String value=tName+" "+timeToString(tTime);
            btn.setText(value);
            layoutExercise.addView(btn);
        }


        toDayCursor=sqLiteDatabase.rawQuery("select * from toDayTBL where tType=='복약' order by tTime",null);
        while(toDayCursor.moveToNext()){
            Button btn=new Button(getActivity());
            String tName=toDayCursor.getString(1);
            int tTime=toDayCursor.getInt(2);

            String value=tName+" "+timeToString(tTime);
            btn.setText(value);
            layoutMedicine.addView(btn);
        }
        toDayCursor.close();

        btn_addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddListActivity.class);
                startActivity(intent);
                getActivity().finish();

//                Toast.makeText(getActivity(),"이건가?",Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
    public void setMapDate(HashMap<String,Integer> mapDate){
        mapDate.put("월",1);mapDate.put("Mon",1);
        mapDate.put("화",2);mapDate.put("Tue",2);
        mapDate.put("수",4);mapDate.put("Wed",4);
        mapDate.put("목",8);mapDate.put("Thu",8);
        mapDate.put("금",16);mapDate.put("Fri",16);
        mapDate.put("토",32);mapDate.put("Sat",32);
        mapDate.put("일",64);mapDate.put("Sun",64);
    }

    //정수를 시간으로 고쳐줌
    public String timeToString(int t){
        String res="";
        //한자리면 앞에 0을 추가
        if((t/60)%24<10) res+="0";
        res+=(t/60)%24+":";
        t%=60;
        //한자리면 앞에 0을 추가
        if(t<10) res+="0";
        res+=t+"";

        return res;
    }
}