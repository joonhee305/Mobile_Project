package com.cookandroid.mobile_project;

import android.app.Activity;
import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;

import com.cookandroid.mobile_project.R;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

public class AddListActivity extends Activity {

    CheckBox chk_illjung, chk_pill;
    LinearLayout container;
    Button btn_save;
    TextView test;
    Boolean boolToDo = false, boolPill = false;
    SQLiteDatabase sqLiteDatabase;
    SQLiteOpenHelper myHelper;
    String getTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlist);



        container = (LinearLayout) findViewById(R.id.container);
        chk_illjung = (CheckBox) findViewById(R.id.check_illjung);
        chk_pill = (CheckBox) findViewById(R.id.check_pill);
        btn_save = (Button) findViewById(R.id.btn_save);
        test = (TextView) findViewById(R.id.tv_test);

        //illjung
        LinearLayout linear_write_todo = new LinearLayout(this);
        linear_write_todo.setOrientation(LinearLayout.VERTICAL);
        LinearLayout linear_when_todo = new LinearLayout(this);
        linear_when_todo.setOrientation(LinearLayout.VERTICAL);

        TextView tv_write_todo = new TextView(this);
        tv_write_todo.setText("해야 할 일을 적어주세요");
        tv_write_todo.setTextSize(25);
        TextView tv_when_todo = new TextView(this);
        tv_when_todo.setText("언제 해야하는지 설정해주세요");
        tv_when_todo.setTextSize(25);

        EditText edt_write_todo = new EditText(this);
        edt_write_todo.setHint("해야 할 일을 적어주세요");



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

        LinearLayout linear_tableLay = new LinearLayout(this);
        linear_tableLay.setOrientation(LinearLayout.VERTICAL);
        LinearLayout linear_tableRow1 = new LinearLayout(this);
        linear_tableRow1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout linear_tableRow2 = new LinearLayout(this);
        linear_tableRow2.setOrientation(LinearLayout.HORIZONTAL);


        TextView tv_write_pill = new TextView(this);
        tv_write_pill.setText("복용하실 약을 적어주세요");
        tv_write_pill.setTextSize(25);
        TextView tv_eat_date = new TextView(this);
        tv_eat_date.setText("복용 요일을 선택해주세요");
        tv_eat_date.setTextSize(25);
        TextView tv_eat_time = new TextView(this);
        tv_eat_time.setText("복용 시간을 선택해주세요");
        tv_eat_time.setTextSize(25);

        EditText edt_piilName = new EditText(this);
        edt_piilName.setHint("복용중인 약을 적어주세요");

        CheckBox mon = new CheckBox(this);
        mon.setText("월");
        CheckBox tue = new CheckBox(this);
        tue.setText("화");
        CheckBox wed = new CheckBox(this);
        wed.setText("수");
        CheckBox thur = new CheckBox(this);
        thur.setText("목");
        CheckBox fri= new CheckBox(this);
        fri.setText("금");
        CheckBox sat = new CheckBox(this);
        sat.setText("토");
        CheckBox sun = new CheckBox(this);
        sun.setText("일");
        CheckBox eve = new CheckBox(this);
        eve.setText("매일");

        CheckBox bfm = new CheckBox(this);
        bfm.setText("아침 식전");
        CheckBox afm = new CheckBox(this);
        afm.setText("아침 식후");
        CheckBox bfl = new CheckBox(this);
        bfl.setText("점심 식전");
        CheckBox afl = new CheckBox(this);
        afl.setText("점심 식후");
        CheckBox bfd = new CheckBox(this);
        bfd.setText("저녁 식전");
        CheckBox afd = new CheckBox(this);
        afd.setText("저녁 식후");
        CheckBox bfs = new CheckBox(this);
        bfs.setText("기상 후");
        CheckBox afs = new CheckBox(this);
        afs.setText("자기 전");

        linear_pillName.addView(tv_write_pill);
        linear_pillName.addView(edt_piilName);

        linear_tableRow1.addView(mon);
        linear_tableRow1.addView(tue);
        linear_tableRow1.addView(wed);
        linear_tableRow1.addView(thur);

        linear_tableRow2.addView(fri);
        linear_tableRow2.addView(sat);
        linear_tableRow2.addView(sun);
        linear_tableRow2.addView(eve);

        linear_tableLay.addView(tv_eat_date);
        linear_tableLay.addView(linear_tableRow1);
        linear_tableLay.addView(linear_tableRow2);

        linear_pillTime.addView(tv_eat_time);
        linear_pillTime.addView(bfs);
        linear_pillTime.addView(bfm);
        linear_pillTime.addView(afm);
        linear_pillTime.addView(bfl);
        linear_pillTime.addView(afl);
        linear_pillTime.addView(bfd);
        linear_pillTime.addView(afd);
        linear_pillTime.addView(afs);




        chk_illjung.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(chk_illjung.isChecked()==true && chk_pill.isChecked() == false){
                    container .setVisibility(View.VISIBLE);
                    container.addView(linear_write_todo);
                    container.addView(linear_when_todo);
                    boolToDo = true;
                    boolPill = false;
                }
                else if(chk_illjung.isChecked()==false && chk_pill.isChecked() == true){
                    container .setVisibility(View.VISIBLE);
                    container.addView(linear_pillName);
                    container.addView(linear_tableLay);
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

                if(chk_illjung.isChecked()==false && chk_pill.isChecked() == true){
                    container .setVisibility(View.VISIBLE);
                    container.addView(linear_pillName);
                    container.addView(linear_tableLay);
                    container.addView(linear_pillTime);
                    boolToDo = true;
                    boolPill = false;
                }
                else if(chk_illjung.isChecked()==true && chk_pill.isChecked() == false){
                    container .setVisibility(View.VISIBLE);
                    container.addView(linear_write_todo);
                    container.addView(linear_when_todo);
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
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLiteDatabase=myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqLiteDatabase,1,2);
                if(boolToDo){
                    sqLiteDatabase.execSQL("insert into toDayTBL values('일정',"+edt_write_todo.getText()+",getTime);");
                    test.setText("");
                }


            }
        });

        set_time_todo.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                getTime = hourOfDay+":"+minute;
            }
        });
    }
}
