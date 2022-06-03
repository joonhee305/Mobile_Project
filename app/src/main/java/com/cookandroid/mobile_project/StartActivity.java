package com.cookandroid.mobile_project;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

public class StartActivity extends Activity {
    Button btn_Medadd;
    Button btn_Mem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        btn_Mem=(Button) findViewById(R.id.btn_Mem);
        btn_Medadd=(Button) findViewById(R.id.btn_MedAdd);
        btn_Mem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_Medadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog addMed;
                addMed=new Dialog(StartActivity.this);
                addMed.setContentView(R.layout.activity_medicine);
                addMed.show();
            }
        });
    }
}
