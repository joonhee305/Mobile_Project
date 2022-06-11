package com.cookandroid.mobile_project;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class StartActivity extends Activity {
    SetWidget setWidget;
    Button btnMedadd,btnMem,btnSave,btnCancle;
    EditText edtMedName;
    CheckBox breakfast,lunch,dinner;
    CheckBox[] exerDate,medDate,medTime;
    int[] exerId,medDateId,medTimeId;
    LinearLayout layoutMed;
    TimePicker breakTimePicker, lunchTimePicker,dinnerTimePicker,exerTimePicker;
    ArrayList<Medicine> medicines;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setWidget=new SetWidget();
        //기록하기
        btnMem=(Button) findViewById(R.id.btnMem);

        //식사 레이아웃
        //아침 점심 저녁
        breakfast=findViewById(R.id.breakfast);
        lunch=findViewById(R.id.lunch);
        dinner=findViewById(R.id.dinner);
        breakTimePicker=findViewById(R.id.breakTimePicker);
        lunchTimePicker=findViewById(R.id.lunchTimePicker);
        dinnerTimePicker=findViewById(R.id.dinnerTimePicker);

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

        //버튼 set
        setWidget.setDate(exerDate,exerId);

        btnMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
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
                medTimeId=new int[]{R.id.breakAf,R.id.breakBf,R.id.lunchAf,R.id.lunchBf,R.id.dinnerAf,R.id.dinnerBf};
                setWidget.matchCheckBox(medTime,medTimeId);

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
                            btnMed.setText(name);

                            int dates=32;
                            ArrayList<String> times=new ArrayList<>();
                            Medicine m=new Medicine(name, dates, times);
                            medicines.add(m);
                            layoutMed.addView(btnMed);

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
    class SetWidget{
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
    class Medicine{
        String name;
        int dates;
        ArrayList<String> times;
        public Medicine(String n, int d,ArrayList<String> t){
            name=n;
            dates=d;
            times=t;
        }
    }

}
