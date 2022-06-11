package com.cookandroid.mobile_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class MySqlite {
    public SharedPreferences datePrefs;
    public class myDBHelper extends SQLiteOpenHelper{
        public myDBHelper(Context context){
            super(context,"mainDB",null,1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            //루틴 테이블 //타입, 이름, 요일, 시간
            db.execSQL("create table routineTBL(rkey char(50) primary key, rType Char(20) , rName Char(20), rDate int, rTime Char(20)) ");
            //당일 테이블 //타입, 이름, 시간
            db.execSQL("create table toDayTBL(tType Char(20), tName Char(20), tTime Char(20))");
            //기록 테이블 //타입,이름,날짜,시간,값
            db.execSQL("create table historyTBL(hType Char(20) , hName Char(20), hDate Char(20), hTime Char(40) primary key, hData Char(20))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists routineTBL");
            db.execSQL("drop table if exists historyTBL");
            onCreate(db);
        }

    }
    public void initHistoryData(SQLiteDatabase db){
        Cursor cursor;
        String data="select * from historyTBL";
        cursor=db.rawQuery(data,null);

        //기록추가
        while(cursor.moveToNext()){

        }
    }
    public void initData(SQLiteDatabase db){
        Cursor cursor;
        String toDay="select * from toDayTBL";

        //날짜 비교, 수정필요함
        long now=System.currentTimeMillis();
        Date date=new Date(now);
        String pastDate=datePrefs.getString("Date",date.toString());

        //초기화 한번 하고 다시 값 대입
        if(!date.equals(pastDate)){
            String diet="select * from routineTBL where rType=='식사'";
            String medicine="select * from routineTBL where rType=='복약'";
            db.execSQL("drop table if exists toDayTBL");

            cursor=db.rawQuery(diet,null);
            while(cursor.moveToNext()){

            }
            cursor=db.rawQuery(medicine,null);
            while(cursor.moveToNext()){

            }
        }

        //출력
        cursor=db.rawQuery(toDay,null);
        while(cursor.moveToNext()){

        }

    }
    public void getData(SQLiteDatabase db,String query){

    }
    public void deleteData(SQLiteDatabase db,String query){

    }
    public void addData(SQLiteDatabase db,String query){

    }
}
