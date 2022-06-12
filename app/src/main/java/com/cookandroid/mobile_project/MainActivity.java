package com.cookandroid.mobile_project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    // 송지민이 추가한 메인 화면의 툴바와 프레그먼트
    Toolbar toolbar;
    MainFragment mainFragment;
    Button reset;
    TextView titleDate;
    // 여기까지
    public SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 송지민이 추가한 프레그먼트 관련 코드

        //재시작 확인
        prefs=getSharedPreferences("Pref",MODE_PRIVATE);
        checkFirstRun();
        //바 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //날짜
        long now=System.currentTimeMillis();
        Date date= new Date(now);
        SimpleDateFormat sdf=new SimpleDateFormat("MM월 dd일 EE");
        String nowDate=sdf.format(date);
        titleDate=findViewById(R.id.titleDate);
        titleDate.setText(nowDate.toString());

        //액션바 설정
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        //플래그먼트
        mainFragment = new MainFragment();



        getSupportFragmentManager().beginTransaction().replace(R.id.container,mainFragment).commit();
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("메인 화면"));
        tabs.addTab(tabs.newTab().setText("지난 일정"));
        tabs.addTab(tabs.newTab().setText("루틴 수정"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("MainActivity","선택된 탭 : "+position);
                Fragment selected = null;
                if(position == 0){
                    selected = mainFragment;
                } else if(position == 1){
                    selected = mainFragment;
                }else if(position == 2){
                    selected = mainFragment;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container,selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //리셋
        reset = (Button) findViewById(R.id.btnReset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.edit().putBoolean("isFirstRun",true).apply();
            }
        });


    }

    public void checkFirstRun(){
        boolean isFirstRun=prefs.getBoolean("isFirstRun",true);
        if(isFirstRun){
            Intent newIntent=new Intent(getApplicationContext(),StartActivity.class);
            startActivity(newIntent);
            finish();

            //두번째 실행부터 실행하지 않으려면 밑에 주석 해제
            prefs.edit().putBoolean("isFirstRun",false).apply();
        }
    }

    public void openDB(){
        SQLiteDatabase rootinDatabase;
    }
}