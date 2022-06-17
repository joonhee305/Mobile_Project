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
import android.graphics.Color;
import android.graphics.PorterDuff;
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
    String month,day,d,nowDate,year;
    LinearLayout layoutDoing, layoutExercise,layoutMedicine;
    TextView testText;
    int idx=300;
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
        SimpleDateFormat sdfYear=new SimpleDateFormat("yyyy");

        Cursor toDayCursor=sqLiteDatabase.rawQuery("select * from toDayTBL",null);
        idx+=toDayCursor.getCount();
        month=sdfMonth.format(date);
        day=sdfDay.format(date);
        d=sdfD.format(date);
        year=sdfYear.format(date);
        nowDate=month+day+d;

        //String asdf="lastDate : "+lastDate+"nowDate : "+nowDate;

        if(!nowDate.equals(lastDate) || toDayCursor.getCount()==0){
            prefs.edit().putString("lastDate",nowDate).apply();
            toDayCursor.close();
            sqLiteDatabase.execSQL("delete from toDayTBL");
            Cursor routineCursor=sqLiteDatabase.rawQuery("select * from routineTBL",null);


            while(routineCursor.moveToNext()){
                String tType,tName;
                int tTime;
                tType=routineCursor.getString(0);
                tName=routineCursor.getString(1);
                int rDate=routineCursor.getInt(2);
                tTime=routineCursor.getInt(3);

                if((rDate & mapDate.get(d))!=0){
                    sqLiteDatabase.execSQL("insert into toDayTBL values('"+tType+"','"+tName+"',"+tTime+",0,"+idx+");"); idx++;
                }
            }
            routineCursor.close();
        }
        //데이터 삽입

        setToDay("select * from toDayTBL where tType=='식사' or tType=='일정' order by tTime",sqLiteDatabase,layoutDoing);
        setToDay("select * from toDayTBL where tType=='운동' order by tTime",sqLiteDatabase,layoutExercise);
        setToDay("select * from toDayTBL where tType=='복약' order by tTime",sqLiteDatabase,layoutMedicine);

        btn_addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddListActivity.class);
                intent.putExtra("idx",idx);
                Toast.makeText(getActivity(),idx+"",Toast.LENGTH_SHORT).show();
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

    public void setToDay(String query,SQLiteDatabase sqLiteDatabase,LinearLayout layout){
        Cursor toDayCursor=sqLiteDatabase.rawQuery(query,null);
        while(toDayCursor.moveToNext()){
            Button btn=new Button(getActivity());
            String tType=toDayCursor.getString(0);
            String tName=toDayCursor.getString(1);
            int tTime=toDayCursor.getInt(2);
            int tCheck=toDayCursor.getInt(3);
            int tId=toDayCursor.getInt(4);
            String value=tName+" "+timeToString(tTime);
            btn.setText(value);
            btn.setId(tId);
            String path=year+month+day+btn.getId();
            if(tCheck==0){
                btn.setOnClickListener(new View.OnClickListener() {
                    Integer btnId=btn.getId();
                    @Override
                    public void onClick(View v) {
                        sqLiteDatabase.execSQL("update toDayTBL set tCheck = 1 where tId = "+btnId+"");
                       // sqLiteDatabase.execSQL("insert into historyTBL values('"+tType+"','"+tName+"',"+tTime+",0,"+idx+");");
                        Intent intent = new Intent(getActivity(), CameraActivity.class);
                        intent.putExtra("Path",path);
                        startActivity(intent);
                    }
                });
            }
            else{
                btn.setHeight(10);
                btn.setBackgroundColor(Color.parseColor("#00ff00"));
                btn.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(),"우효",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            layout.addView(btn);
            LinearLayout.LayoutParams mLayoutParams=(LinearLayout.LayoutParams) btn.getLayoutParams();
            mLayoutParams.bottomMargin=10;
            mLayoutParams.leftMargin=5;
            mLayoutParams.rightMargin=5;
            btn.setLayoutParams(mLayoutParams);
        }
        toDayCursor.close();
    }
}