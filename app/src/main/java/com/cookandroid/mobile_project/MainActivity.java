package com.cookandroid.mobile_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    public SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs=getSharedPreferences("Pref",MODE_PRIVATE);
        checkFirstRun();
    }

    public void checkFirstRun(){
        boolean isFirstRun=prefs.getBoolean("isFirstRun",true);
        if(isFirstRun){
            Intent newIntent=new Intent(getApplicationContext(),StartActivity.class);
            startActivity(newIntent);

            //두번째 실행부터 실행하지 않으려면 밑에 주석 해제
            //prefs.edit().putBoolean("isFirstRun",false).apply();
        }
    }
}