package com.cookandroid.mobile_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class myDBHelper extends SQLiteOpenHelper {
    public myDBHelper(Context context){
        super(context, "groupDB",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //루틴 테이블 //타입, 이름, 요일, 시간
        db.execSQL("create  table if not exists routineTBL(rType Char(20), rName Char(20), rDate int, rTime int) ");
        //당일 테이블 //타입, 이름, 시간
        db.execSQL("create table if not exists toDayTBL(tType Char(20), tName Char(20), tTime int, tCheck int,tId int primary key)");
        //기록 테이블 //타입,이름,날짜,시간,값
        db.execSQL("create table if not exists historyTBL(hType Char(20) , hName Char(20), hDate Char(20), hTime Char(40) primary key, hData Char(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists routineTBL");
        db.execSQL("drop table if exists toDayTBL");
        db.execSQL("drop table if exists historyTBL");
        onCreate(db);
    }
}