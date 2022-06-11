package com.cookandroid.mobile_project;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class StartActivity extends Activity {
    public static Context context;
    SetWidget setWidget;
    Button btnMedadd,btnMem,btnSave,btnCancle,btnReset,btnGet;
    EditText edtMedName;
    CheckBox chkBreakfast,chkLunch,chkDinner;
    CheckBox[] exerDate,medDate,medTime;
    int[] exerId,medDateId,medTimeId;
    LinearLayout layoutMed;
    TimePicker breakTimePicker, lunchTimePicker,dinnerTimePicker,exerTimePicker;
    ArrayList<Medicine> medicines;
    String breakfastTime,lunchTime,dinnerTime,exerciseTime;
    SQLiteDatabase sqLiteDatabase;
    SQLiteOpenHelper myHelper;
    Intent mainActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mainActivity = new Intent(this, MainActivity.class);
        myHelper=new myDBHelper(this);
        setWidget=new SetWidget();
        //기록하기
        btnMem=(Button) findViewById(R.id.btnMem);

        //식사 레이아웃
        //아침 점심 저녁, defalut
        breakfastTime="09:00";
        lunchTime="12:00";
        dinnerTime="18:00";


        chkBreakfast=findViewById(R.id.chkBreakfast);
        chkLunch=findViewById(R.id.chkLunch);
        chkDinner=findViewById(R.id.chkDinner);
        breakTimePicker=findViewById(R.id.breakTimePicker);
        lunchTimePicker=findViewById(R.id.lunchTimePicker);
        dinnerTimePicker=findViewById(R.id.dinnerTimePicker);

        breakTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(chkBreakfast.isChecked()){
                    breakfastTime=hourOfDay+":"+minute;
                }
            }
        });
        lunchTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(chkLunch.isChecked()){
                    lunchTime = hourOfDay+":"+minute;
                }
            }
        });
        dinnerTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(chkDinner.isChecked()){
                    dinnerTime=hourOfDay+":"+minute;
                }
            }
        });

        //복약 레이아웃
        //약 추가
        layoutMed=findViewById(R.id.layoutMed);
        btnMedadd=(Button) findViewById(R.id.btnMedAdd);
        medicines=new ArrayList<>();

        //운동 레이아웃
        //운동
        exerId= new int[]{R.id.exerMon,R.id.exerTue,R.id.exerWed,R.id.exerThu,R.id.exerFri,R.id.exerSat,R.id.exerSun,R.id.exerEvery};
        exerDate=new CheckBox[8];
        exerTimePicker=findViewById(R.id.exerTimePicker);
        setWidget.setDate(exerDate,exerId);
        exerciseTime="16:00";
        exerTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                boolean flag=false;
                for(int i=0;i<7;i++){
                    if(exerDate[i].isChecked()){
                        flag=true; break;
                    }
                }
                if(flag){
                    exerciseTime=hourOfDay+":"+minute;
                }
            }
        });
        //버튼 set

        //기록하기
        btnMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now=System.currentTimeMillis();
                Date key=new Date(now);
                sqLiteDatabase=myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqLiteDatabase,1,2);
                if(chkBreakfast.isChecked()){
                    sqLiteDatabase.execSQL("insert into routineTBL values('식사','아침',127,'"+breakfastTime+"');");
                }
                if(chkLunch.isChecked()){
                    sqLiteDatabase.execSQL("insert into routineTBL values('식사','점심',127,'"+lunchTime+"');");
                }
                if(chkDinner.isChecked()){
                    sqLiteDatabase.execSQL("insert into routineTBL values('식사','저녁',127,'"+dinnerTime+"');");
                }
                //"+key.toString()+"','

                for(Medicine medicine : medicines){
                    int t=medicine.times;
                    for(int i=0;i<7;i++){
                        String mtime;
                        if((t&1<<i)!=0){
                            switch (i){
                                case 0:
                                    mtime=addTime(breakfastTime,-30);
                                    break;
                                case 1:
                                    mtime=addTime(breakfastTime,30);
                                    break;
                                case 2:
                                    mtime=addTime(lunchTime,-30);
                                    break;
                                case 3:
                                    mtime=addTime(lunchTime,30);
                                    break;
                                case 4:
                                    mtime=addTime(dinnerTime,-30);
                                    break;
                                case 5:
                                    mtime=addTime(dinnerTime,30);
                                    break;
                                default:
                                    mtime="";
                                    break;
                            }
                            sqLiteDatabase.execSQL("insert into routineTBL values('복약','"+medicine.name+"',"+medicine.dates+",'"+mtime+"');");
                        }
                    }
                    //sqLiteDatabase.execSQL("insert into routineTBL values('식사','"+medicine.name+"',"+medecine.times+",'"+breakfastTime+"'')");
                }

                int exerDates=0;
                for(int i=0;i<7;i++) if(exerDate[i].isChecked()) exerDates+=(int)Math.pow(2,i);
                if(exerDates!=0){
                    sqLiteDatabase.execSQL("insert into routineTBL values('운동','운동',"+exerDates+",'"+exerciseTime+"');");
                }
                sqLiteDatabase.close();
                startActivity(mainActivity);
                finish();
            }
        });
        //약 추가
        btnMedadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog addMed;
                addMed=new Dialog(StartActivity.this);
                addMed.setContentView(R.layout.activity_medicine);
                addMed.show();
                //약 이름
                edtMedName=addMed.findViewById(R.id.edtMedName);

                //요일
                medDate=new CheckBox[8];
                medDateId= new int[]{R.id.medMon,R.id.medTue,R.id.medWed,R.id.medThu,R.id.medFri,R.id.medSat,R.id.medSun,R.id.medEvery};
                setWidget.setDate(medDate,medDateId,addMed);

                //시간
                medTime=new CheckBox[6];
                medTimeId=new int[]{R.id.breakBf,R.id.breakAf,R.id.lunchBf,R.id.lunchAf,R.id.dinnerBf,R.id.dinnerAf};
                setWidget.matchCheckBox(medTime,medTimeId,addMed);


                //다이얼로그 버튼
                btnSave=addMed.findViewById(R.id.btnSave);
                btnCancle=addMed.findViewById(R.id.btnCancle);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button btnMed=new Button(StartActivity.this);
                        String name=edtMedName.getText().toString();
                        if(name.equals("")){
                            Toast.makeText(getApplicationContext(), "약이름을 입력해주세요",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //출력값
                            String value;
                            //요일 값
                            int dates=0;
                            //시간 배열
                            int times=0;
                            //약 데이터
                            Medicine m;

                            //요일 처리
                            for(int i=0;i<7;i++){
                                if(medDate[i].isChecked()) dates+=(int)Math.pow(2,i);
                            }
                            //시간 처리
                            for(int i=0;i<6;i++){
                                if(medTime[i].isChecked()) times+=(int)Math.pow(2,i);
                            }
                            m=new Medicine(name, dates, times);
                            medicines.add(m);
                            layoutMed.addView(btnMed);

                            value=name+"\n"+dates+"\n"+times;
                            value+="\n"+addTime(breakfastTime,30)+"\n"+addTime(lunchTime,30)+"\n"+addTime(dinnerTime,30);
                            btnMed.setText(value);
                            addMed.dismiss();
                        }

                    }
                });
                btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addMed.dismiss();
                    }
                });
            }
        });
        //불러오기

        btnGet=findViewById(R.id.btnGet);
        btnReset=findViewById(R.id.btnReset);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layoutTest=findViewById(R.id.layoutTest);
                Cursor cursor;
                sqLiteDatabase=myHelper.getWritableDatabase();
                cursor=sqLiteDatabase.rawQuery("select * from routineTBL;",null);
                while(cursor.moveToNext()){
                    TextView ntextView=new TextView(StartActivity.this);
                    String val="";
                    for(int i=0;i<4;i++) val+=cursor.getString(i)+" ";

                    ntextView.setText(val);
                    layoutTest.addView(ntextView);
                }
                sqLiteDatabase.close();
            }
        });
        //새로고침
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteDatabase=myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqLiteDatabase,1,2);
                sqLiteDatabase.close();

            }
        });
    }
    String addTime(String s,int t){
        String result;
        String[] res=s.split(":");
        int val=Integer.parseInt(res[0])*60+Integer.parseInt(res[1]);
        val+=t;
        res[0]=(val/60)%24+"";
        res[1]=(val%60)+"";

        result=res[0]+":"+res[1];

        return result;
    }
    public class SetWidget{
        void matchCheckBox(CheckBox[] checkBoxes,int[] id){
            int len=checkBoxes.length;
            for(int i=0;i<len;i++){
                checkBoxes[i]=findViewById(id[i]);
            }
        }
        void matchCheckBox(CheckBox[] checkBoxes,int[] id,Dialog dialog){
            int len=checkBoxes.length;
            for(int i=0;i<len;i++){
                checkBoxes[i]=dialog.findViewById(id[i]);
            }
        }
        void setCheckBox(CheckBox[] checkBoxes){
            int len=checkBoxes.length;
            for(int i=0;i<len;i++){
                checkBoxes[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox buttonView=(CheckBox) v;
                        if(buttonView.isChecked()){
                            boolean flag=true;
                            for(int i=0;i<len-1;i++){
                                if(!checkBoxes[i].isChecked()){ flag=false; break; }
                            }
                            if(flag) checkBoxes[len-1].setChecked(true);
                            else checkBoxes[len-1].setChecked(false);
                        }
                        else checkBoxes[len-1].setChecked(false);
                    }
                });
            }
            checkBoxes[len-1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBoxes[len-1].isChecked()){
                        for(CheckBox value : checkBoxes){ value.setChecked(true); }
                    }
                    else {
                        for(CheckBox value : checkBoxes){ value.setChecked(false); }
                    }
                }
            });
        }
        void setDate(CheckBox[] date, int[] id) {
            matchCheckBox(date,id);
            setCheckBox(date);
        }
        void setDate(CheckBox[] date, int[] id, Dialog dialog ){
            matchCheckBox(date,id,dialog);
            setCheckBox(date);
        }
    }
    public class Medicine{
        String name;
        int dates;
        int times;
        public Medicine(String n, int d,int t){
            name=n;
            dates=d;
            times=t;
        }
    }
}
