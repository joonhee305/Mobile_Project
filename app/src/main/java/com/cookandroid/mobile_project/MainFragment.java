package com.cookandroid.mobile_project;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainFragment extends Fragment {
    SQLiteDatabase sqLiteDatabase;
    SQLiteOpenHelper myHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_main, container, false);

        Button btn_addList = (Button) v.findViewById(R.id.btnAddList);
        LinearLayout layoutDoing=v.findViewById(R.id.layoutDoing);

        myHelper=new myDBHelper(getActivity());
        sqLiteDatabase=myHelper.getWritableDatabase();
        btn_addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), AddListActivity.class);
//                startActivity(intent);
                Toast.makeText(getActivity(),"이건가?",Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}