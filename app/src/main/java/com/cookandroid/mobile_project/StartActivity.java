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
import android.os.Build;
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
import androidx.annotation.RequiresApi;
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
    Button btnMedadd,btnMem,btnSave,btnCancle;
    EditText edtMedName;
    CheckBox chkBreakfast,chkLunch,chkDinner;
    CheckBox[] exerDate,medDate,medTime;
    int[] exerId,medDateId,medTimeId;
    LinearLayout layoutMed;
    TimePicker breakTimePicker, lunchTimePicker,dinnerTimePicker,exerTimePicker;
    ArrayList<Medicine> medicines;
    Integer breakfastTime,lunchTime,dinnerTime,exerciseTime;
    SQLiteDatabase sqLiteDatabase;
    SQLiteOpenHelper myHelper;
    Intent mainActivity;
    @RequiresApi(api = Build.VERSION_CODES.M)
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
        breakfastTime=540;
        lunchTime=720;
        dinnerTime=1080;


        chkBreakfast=findViewById(R.id.chkBreakfast);
        chkLunch=findViewById(R.id.chkLunch);
        chkDinner=findViewById(R.id.chkDinner);
        breakTimePicker=findViewById(R.id.breakTimePicker);
        breakTimePicker.setHour(9); breakTimePicker.setMinute(0);
        lunchTimePicker=findViewById(R.id.lunchTimePicker);
        lunchTimePicker.setHour(12); lunchTimePicker.setMinute(0);
        dinnerTimePicker=findViewById(R.id.dinnerTimePicker);
        dinnerTimePicker.setHour(18); dinnerTimePicker.setMinute(0);

        breakTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(chkBreakfast.isChecked()){
                    breakfastTime=hourOfDay*60+minute;
                }
            }
        });
        lunchTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(chkLunch.isChecked()){
                    lunchTime = hourOfDay*60+minute;
                }
            }
        });
        dinnerTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(chkDinner.isChecked()){
                    dinnerTime=hourOfDay*60+minute;
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
        exerciseTime=960;
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
                    exerciseTime=hourOfDay*60+minute;
                }
            }
        });
        //버튼 set

        //기록하기
        btnMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteDatabase=myHelper.getWritableDatabase();
                sqLiteDatabase.execSQL("delete from routineTBL");

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
                        Integer mtime = 0;
                        if((t&1<<i)!=0){
                            switch (i){
                                case 0: mtime=breakfastTime-30; break;
                                case 1: mtime=breakfastTime+30; break;
                                case 2: mtime=lunchTime-30; break;
                                case 3: mtime=lunchTime+30; break;
                                case 4: mtime=dinnerTime-30; break;
                                case 5: mtime=dinnerTime+30; break;
                                default: mtime=0; break;
                            }
                            sqLiteDatabase.execSQL("insert into routineTBL values('복약','"+medicine.name+"',"+medicine.dates+","+mtime+");");
                        }
                    }
                }

                int exerDates=0;
                for(int i=0;i<7;i++) if(exerDate[i].isChecked()) exerDates+=(int)Math.pow(2,i);
                if(exerDates!=0){
                    sqLiteDatabase.execSQL("insert into routineTBL values('운동','운동',"+exerDates+","+exerciseTime+");");
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
                            //요일 값 //시간 배열
                            int dates=0, times=0;
                            //약 데이터
                            Medicine m;

                            //요일 처리
                            for(int i=0;i<7;i++){
                                if(medDate[i].isChecked()) dates+=(int)Math.pow(2,i);
                            }

                            //시간 처리
                            for(int i=0;i<6;i++){
                                if(medTime[i].isChecked()) {
                                    times+=(int)Math.pow(2,i);
                                }
                            }
                            m=new Medicine(name, dates, times);
                            medicines.add(m);
                            layoutMed.addView(btnMed);
                            value=name+"\n"+dates;

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


    }

    public class SetWidget{
        public void matchCheckBox(CheckBox[] checkBoxes,int[] id){
            int len=checkBoxes.length;
            for(int i=0;i<len;i++){
                checkBoxes[i]=findViewById(id[i]);
            }
        }
        public void matchCheckBox(CheckBox[] checkBoxes,int[] id,Dialog dialog){
            int len=checkBoxes.length;
            for(int i=0;i<len;i++){
                checkBoxes[i]=dialog.findViewById(id[i]);
            }
        }
        public void setCheckBox(CheckBox[] checkBoxes){
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
        public void setDate(CheckBox[] date, int[] id) {
            matchCheckBox(date,id);
            setCheckBox(date);
        }
        public void setDate(CheckBox[] date, int[] id, Dialog dialog ){
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
