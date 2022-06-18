package com.cookandroid.mobile_project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    // 송지민이 추가한 메인 화면의 툴바와 프레그먼트

    public static final int REQUEST_PERMISSION = 11;
    Toolbar toolbar;
    MainFragment mainFragment;
    HistoryFragment historyFragment;

    Button reset,btnResetRoutine;
    TextView titleDate;
    SQLiteDatabase sqLiteDatabase;
    SQLiteOpenHelper myHelper;
    // 여기까지
    public SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission(); //권한체크
        // 송지민이 추가한 프레그먼트 관련 코드

//        데베 수정후 첫실행 할 때 초기화
//        myHelper=new myDBHelper(this);
//        sqLiteDatabase=myHelper.getWritableDatabase();
//        myHelper.onUpgrade(sqLiteDatabase,1,2);
//        sqLiteDatabase.close();

        //재시작 확인
        checkFirstRun();

        //바 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //날짜
        long now=System.currentTimeMillis();
        Date date= new Date(now);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy년 MM월 dd일 EE");
        String nowDate=sdf.format(date);
        titleDate=findViewById(R.id.titleDate);
        titleDate.setText(nowDate.toString());

        //액션바 설정
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        //플래그먼트
        mainFragment = new MainFragment();
        historyFragment = new HistoryFragment();


        getSupportFragmentManager().beginTransaction().replace(R.id.container,mainFragment).commit();
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("메인 화면"));
        tabs.addTab(tabs.newTab().setText("지난 일정"));
//        tabs.addTab(tabs.newTab().setText("루틴 수정"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("MainActivity","선택된 탭 : "+position);
                Fragment selected = null;
                if(position == 0){
                    selected = mainFragment;
                } else if(position == 1){
                    selected = historyFragment;
                }
//                else if(position == 2){
//                    selected = mainFragment;
//                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container,selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        //리셋
//        reset = (Button) findViewById(R.id.btnReset);
//        reset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                prefs.edit().putString("lastDate"," ").apply();
//                prefs.edit().putBoolean("isFirstRun",true).apply();
//            }
//        });
//
        //루틴 수정 버튼
        btnResetRoutine = (Button) findViewById(R.id.btnResetRoutine);
        btnResetRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startActivity=new Intent(getApplicationContext(),StartActivity.class);
                startActivity(startActivity);
                finish();
            }
        });

    }

    public void checkFirstRun(){
        myHelper=new myDBHelper(this);
        sqLiteDatabase=myHelper.getWritableDatabase();
        Cursor checkcursor=sqLiteDatabase.rawQuery("select * from routineTBL",null);
        if(checkcursor.getCount()==0){
            Intent newIntent=new Intent(getApplicationContext(),StartActivity.class);
            startActivity(newIntent);
            finish();
        }
    }
    public void checkPermission() {
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //권한이 없으면 권한 요청
        if (permissionCamera != PackageManager.PERMISSION_GRANTED
                || permissionRead != PackageManager.PERMISSION_GRANTED
                || permissionWrite != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "이 앱을 실행하기 위해 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);

        }
    }
}