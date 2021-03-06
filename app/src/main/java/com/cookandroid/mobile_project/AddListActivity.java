package com.cookandroid.mobile_project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.cookandroid.mobile_project.R;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AddListActivity extends Activity {
    SetWidget setWidget;
    CheckBox chk_illjung, chk_pill;
    LinearLayout container, layoutTest;
    Button btn_save,btn_cancel;
    Boolean boolToDo = false, boolPill = false;
    SQLiteDatabase sqLiteDatabase;
    SQLiteOpenHelper myHelper;
    Integer getTime;
    Integer breakfastTime,lunchTime,dinnerTime;
    CheckBox[] medDate, medTime;
    ArrayList<Medicine> medicines;
    Intent mainActivity,mainFragment;
    int idx;
    public SharedPreferences prefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlist);
        prefs= getSharedPreferences("Pref", Context.MODE_PRIVATE);
        myHelper=new myDBHelper(this);
        medicines=new ArrayList<>();

        container = (LinearLayout) findViewById(R.id.container);
        chk_illjung = (CheckBox) findViewById(R.id.check_illjung);
        chk_pill = (CheckBox) findViewById(R.id.check_pill);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_cancel=(Button) findViewById(R.id.btn_cancel);

        breakfastTime=540;
        lunchTime=720;
        dinnerTime=1080;

        mainActivity = new Intent(this, MainActivity.class);
        mainFragment=getIntent();
        idx=mainFragment.getIntExtra("idx",0);

        //
        // Toast.makeText(getApplicationContext(),idx+"",Toast.LENGTH_SHORT).show();
        //illjung
        LinearLayout linear_write_todo = new LinearLayout(this);
        linear_write_todo.setOrientation(LinearLayout.VERTICAL);
        LinearLayout linear_when_todo = new LinearLayout(this);
        linear_when_todo.setOrientation(LinearLayout.VERTICAL);

        TextView tv_write_todo = new TextView(this);
        tv_write_todo.setText("?????? ??? ?????? ???????????????");
        tv_write_todo.setTextSize(25);
        TextView tv_when_todo = new TextView(this);
        tv_when_todo.setText("?????? ??????????????? ??????????????????");
        tv_when_todo.setTextSize(25);

        EditText edt_write_todo = new EditText(this);
        edt_write_todo.setHint("?????? ??? ?????? ???????????????");



        TimePicker set_time_todo = new TimePicker(this);
        set_time_todo.setMinimumHeight(80);

        linear_write_todo.addView(tv_write_todo);
        linear_write_todo.addView(edt_write_todo);

        linear_when_todo.addView(tv_when_todo);
        linear_when_todo.addView(set_time_todo);

        //pill
        LinearLayout linear_pill = new LinearLayout(this);
        linear_pill.setOrientation(LinearLayout.VERTICAL);
        LinearLayout linear_pillName = new LinearLayout(this);
        linear_pillName.setOrientation(LinearLayout.VERTICAL);
        LinearLayout linear_pillTime = new LinearLayout(this);
        linear_pillTime.setOrientation(LinearLayout.VERTICAL);

//        LinearLayout linear_tableLay = new LinearLayout(this);
//        linear_tableLay.setOrientation(LinearLayout.VERTICAL);
//        LinearLayout linear_tableRow1 = new LinearLayout(this);
//        linear_tableRow1.setOrientation(LinearLayout.HORIZONTAL);
//        LinearLayout linear_tableRow2 = new LinearLayout(this);
//        linear_tableRow2.setOrientation(LinearLayout.HORIZONTAL);


        TextView tv_write_pill = new TextView(this);
        tv_write_pill.setText("???????????? ?????? ???????????????");
        tv_write_pill.setTextSize(25);
        TextView tv_eat_date = new TextView(this);
        tv_eat_date.setText("?????? ????????? ??????????????????");
        tv_eat_date.setTextSize(25);
        TextView tv_eat_time = new TextView(this);
        tv_eat_time.setText("?????? ????????? ??????????????????");
        tv_eat_time.setTextSize(25);

        EditText edt_piilName = new EditText(this);
        edt_piilName.setHint("???????????? ?????? ???????????????");

        CheckBox mon = new CheckBox(this);
        mon.setText("???");
        CheckBox tue = new CheckBox(this);
        tue.setText("???");
        CheckBox wed = new CheckBox(this);
        wed.setText("???");
        CheckBox thur = new CheckBox(this);
        thur.setText("???");
        CheckBox fri= new CheckBox(this);
        fri.setText("???");
        CheckBox sat = new CheckBox(this);
        sat.setText("???");
        CheckBox sun = new CheckBox(this);
        sun.setText("???");
        CheckBox eve = new CheckBox(this);
        eve.setText("??????");

        CheckBox bfm = new CheckBox(this);
        bfm.setText("?????? ??????");
        CheckBox afm = new CheckBox(this);
        afm.setText("?????? ??????");
        CheckBox bfl = new CheckBox(this);
        bfl.setText("?????? ??????");
        CheckBox afl = new CheckBox(this);
        afl.setText("?????? ??????");
        CheckBox bfd = new CheckBox(this);
        bfd.setText("?????? ??????");
        CheckBox afd = new CheckBox(this);
        afd.setText("?????? ??????");


        setWidget=new SetWidget();
        medDate= new CheckBox[] {mon,tue,wed,thur,fri,sat,sun,eve};
        medTime=new CheckBox[] {bfm,afm,bfl,afl,bfd,afd};
        setWidget.setDate(medDate);


        linear_pillName.addView(tv_write_pill);
        linear_pillName.addView(edt_piilName);

//        linear_tableRow1.addView(mon);
//        linear_tableRow1.addView(tue);
//        linear_tableRow1.addView(wed);
//        linear_tableRow1.addView(thur);
//
//        linear_tableRow2.addView(fri);
//        linear_tableRow2.addView(sat);
//        linear_tableRow2.addView(sun);
//        linear_tableRow2.addView(eve);
//
//        linear_tableLay.addView(tv_eat_date);
//        linear_tableLay.addView(linear_tableRow1);
//        linear_tableLay.addView(linear_tableRow2);

        linear_pillTime.addView(tv_eat_time);
        linear_pillTime.addView(bfm);
        linear_pillTime.addView(afm);
        linear_pillTime.addView(bfl);
        linear_pillTime.addView(afl);
        linear_pillTime.addView(bfd);
        linear_pillTime.addView(afd);




        chk_illjung.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(chk_illjung.isChecked()==true){
                    chk_pill.setChecked(false);
                    container .setVisibility(View.VISIBLE);
                    container.removeAllViews();
                    container.addView(linear_write_todo);
                    container.addView(linear_when_todo);
                    boolToDo = true;
                    boolPill = false;
                }
                else if(chk_illjung.isChecked()==false && chk_pill.isChecked() == true){
                    container .setVisibility(View.VISIBLE);
                    container.removeAllViews();
                    container.addView(linear_pillName);
//                    container.addView(linear_tableLay);
                    container.addView(linear_pillTime);
                    boolToDo = false;
                    boolPill = true;
                }
                else{
                    container.setVisibility(View.INVISIBLE);
                    container.removeAllViews();
                    boolToDo = false;
                    boolPill = false;
                }
            }

        });
        chk_pill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(chk_pill.isChecked() == true){
                    chk_illjung.setChecked(false);
                    container .setVisibility(View.VISIBLE);
                    container.removeAllViews();
                    container.addView(linear_pillName);
//                    container.addView(linear_tableLay);
                    container.addView(linear_pillTime);
                    boolToDo = false;
                    boolPill = true;
                }
                else if(chk_illjung.isChecked()==true && chk_pill.isChecked() == false){
                    container .setVisibility(View.VISIBLE);
                    container.removeAllViews();
                    container.addView(linear_write_todo);
                    container.addView(linear_when_todo);
                    boolToDo = true;
                    boolPill = false;
                }
                else{
                    container.setVisibility(View.INVISIBLE);
                    container.removeAllViews();
                    boolToDo = false;
                    boolPill = false;
                }
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLiteDatabase=myHelper.getWritableDatabase();
                if(boolToDo){
                    sqLiteDatabase.execSQL("insert into toDayTBL values('??????','"+edt_write_todo.getText()+"','"+getTime+"',0,"+idx+");"); idx++;
                }
                else if(boolPill){
                    //?????????
                    String value;
                    //?????? ???
                    int dates=0;
                    //?????? ??????
                    int times=0;
                    //??? ?????????
                    Medicine m;

                    //?????? ??????
                    for(int i=0;i<7;i++){
                        if(medDate[i].isChecked()) dates+=(int)Math.pow(2,i);
                    }
                    //?????? ??????
                    for(int i=0;i<6;i++){
                        if(medTime[i].isChecked()) times+=(int)Math.pow(2,i);
                    }
                    m=new Medicine(edt_piilName.getText().toString(), dates, times);
                    medicines.add(m);
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
                                sqLiteDatabase.execSQL("insert into toDayTBL values('??????','"+edt_piilName.getText()+"','"+mtime+"',0,"+idx+");"); idx++;
                            }
                        }
                    }
                }
                prefs.edit().putInt("toDayIdx",idx).apply();
                sqLiteDatabase.close();
                startActivity(mainActivity);
                finish();
            }


        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mainActivity);
                finish();
            }
        });

        set_time_todo.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                getTime = hourOfDay*60+minute;
            }
        });

    }

    public class SetWidget{
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
        void setDate(CheckBox[] date) {
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