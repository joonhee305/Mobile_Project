package com.cookandroid.mobile_project;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class MainFragment extends Fragment {
    SQLiteDatabase sqLiteDatabase;
    SQLiteOpenHelper myHelper;
    HashMap<String,Integer> mapDate;
    String month,day,d,nowDate,year;
    LinearLayout layoutDoing, layoutExercise,layoutMedicine;
    TextView testText;
    Button btnExer;
    int idx=300;
    public SharedPreferences prefs;
    public ImageView iv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_main, container, false);
        Button btn_addList = (Button) v.findViewById(R.id.btnAddList);

        String[] youtubeURL = {"https://www.youtube.com/watch?v=lXUEMdde9hM","https://www.youtube.com/watch?v=I4ovzV-BLDU&t=529s",
                "https://www.youtube.com/watch?v=U_Tv31zKYkk","https://www.youtube.com/watch?v=WTVoxQUbcGo&t=41s",
                "https://www.youtube.com/watch?v=NABkYV6IWYA","https://www.youtube.com/watch?v=NABkYV6IWYA","https://www.youtube.com/watch?v=q-ySRwLEwY4"};
        String[] exerName = {"걷기운동","근력강화운동","아령운동","타바타운동","하체강화운동","찔레꽃운동","손가락운동"};




        testText=v.findViewById(R.id.testText);
        layoutDoing=v.findViewById(R.id.layoutDoing);
        layoutExercise=v.findViewById(R.id.layoutExercise);
        layoutMedicine=v.findViewById(R.id.layoutMedicine);
        btnExer = (Button) v.findViewById(R.id.btnExer);

        mapDate=new HashMap<String,Integer>();
        setMapDate(mapDate);
        myHelper=new myDBHelper(getActivity());
        sqLiteDatabase=myHelper.getWritableDatabase();
        prefs= getActivity().getSharedPreferences("Pref", Context.MODE_PRIVATE);
        iv=getActivity().findViewById(R.id.iv);
//        Intent intent = getActivity().getIntent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        Cursor test=sqLiteDatabase.rawQuery("select * from historyTBL",null);
//        String val="";
//        while(test.moveToNext()){
//            val+=test.getString(0)+" "+test.getString(1)+test.getString(2)
//                    +" "+test.getString(3)+" "+test.getString(4)+"\n";
//
//        }
//        testText.setText(val);
        String lastDate=prefs.getString("lastDate"," ");


        //날짜
        long now=System.currentTimeMillis();
        Date date= new Date(now);
        SimpleDateFormat sdfMonth=new SimpleDateFormat("MM");
        SimpleDateFormat sdfDay=new SimpleDateFormat("dd");
        SimpleDateFormat sdfD=new SimpleDateFormat("EE");
        SimpleDateFormat sdfYear=new SimpleDateFormat("yyyy");

        Cursor toDayCursor=sqLiteDatabase.rawQuery("select * from toDayTBL",null);
        idx+=toDayCursor.getCount();
        month=sdfMonth.format(date);
        day=sdfDay.format(date);
        d=sdfD.format(date);
        year=sdfYear.format(date);
        nowDate=month+day+d;

        int index = Integer.parseInt(day)%7;
        btnExer.setText(exerName[index]);
        //String asdf="lastDate : "+lastDate+"nowDate : "+nowDate;

        if(!nowDate.equals(lastDate) || toDayCursor.getCount()==0){
            prefs.edit().putString("lastDate",nowDate).apply();
            toDayCursor.close();
            sqLiteDatabase.execSQL("delete from toDayTBL");
            Cursor routineCursor=sqLiteDatabase.rawQuery("select * from routineTBL",null);


            while(routineCursor.moveToNext()){
                String tType,tName;
                int tTime;
                tType=routineCursor.getString(0);
                tName=routineCursor.getString(1);
                int rDate=routineCursor.getInt(2);
                tTime=routineCursor.getInt(3);

                if((rDate & mapDate.get(d))!=0){
                    sqLiteDatabase.execSQL("insert into toDayTBL values('"+tType+"','"+tName+"',"+tTime+",0,"+idx+");"); idx++;
                }
            }
            routineCursor.close();
        }
        //데이터 삽입
        try{
            setToDay("select * from toDayTBL where tType=='식사' or tType=='일정' order by tTime",sqLiteDatabase,layoutDoing);
            setToDay("select * from toDayTBL where tType=='운동' order by tTime",sqLiteDatabase,layoutExercise);
            setToDay("select * from toDayTBL where tType=='복약' order by tTime",sqLiteDatabase,layoutMedicine);
        }catch (FileNotFoundException e){Log.e("error","에러발생");};

//      운동
        btnExer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL[index]));
                startActivity(intent);
            }
        });

        btn_addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddListActivity.class);
                intent.putExtra("idx",idx);
                Toast.makeText(getActivity(),idx+"",Toast.LENGTH_SHORT).show();
                startActivity(intent);
                getActivity().finish();

//                Toast.makeText(getActivity(),"이건가?",Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
    public void setMapDate(HashMap<String,Integer> mapDate){
        mapDate.put("월",1);mapDate.put("Mon",1);
        mapDate.put("화",2);mapDate.put("Tue",2);
        mapDate.put("수",4);mapDate.put("Wed",4);
        mapDate.put("목",8);mapDate.put("Thu",8);
        mapDate.put("금",16);mapDate.put("Fri",16);
        mapDate.put("토",32);mapDate.put("Sat",32);
        mapDate.put("일",64);mapDate.put("Sun",64);
    }

    //정수를 시간으로 고쳐줌
    public String timeToString(int t){
        String res="";
        //한자리면 앞에 0을 추가
        if((t/60)%24<10) res+="0";
        res+=(t/60)%24+":";
        t%=60;
        //한자리면 앞에 0을 추가
        if(t<10) res+="0";
        res+=t+"";

        return res;
    }

    public void setToDay(String query,SQLiteDatabase sqLiteDatabase,LinearLayout layout) throws FileNotFoundException {
        Cursor toDayCursor=sqLiteDatabase.rawQuery(query,null);
        while(toDayCursor.moveToNext()){
            Button btn=new Button(getActivity());
            String tType=toDayCursor.getString(0);
            String tName=toDayCursor.getString(1);
            int tTime=toDayCursor.getInt(2);
            int tCheck=toDayCursor.getInt(3);
            int tId=toDayCursor.getInt(4);
            String value=tName+" "+timeToString(tTime);
            btn.setText(value);
            btn.setId(tId);
            String path=year+month+day+btn.getId();
            String[] tData={tType,tName,year+month+day,tId+""};
            if(tCheck==0){
                btn.setOnClickListener(new View.OnClickListener() {
                    Integer btnId=btn.getId();
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CameraActivity.class);
                        intent.putExtra("Path",path);
                        intent.putExtra("tData",tData);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
            }
            else{
                //파일
                File storageDir = new File(getActivity().getFilesDir() + "/capture");
                String filename = path + ".jpg";
                File file = new File(storageDir, filename);
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

                //다이얼로그
                Dialog myPicture;
                myPicture=new Dialog(getActivity());
                myPicture.setContentView(R.layout.dialog_mypicture);

                //다이얼 로그 내 버튼
                TextView dlTitle=myPicture.findViewById(R.id.dlMyPictureTitle);
                ImageView dlPicture=myPicture.findViewById(R.id.dlMyPictureImage);
                Button dlFinish=myPicture.findViewById(R.id.dlMyPictureFinish);
                dlFinish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myPicture.dismiss();
                    }
                });

                btn.setHeight(10);
                btn.setBackgroundColor(Color.parseColor("#B7FF9C"));
                btn.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(bitmap!=null) {
                            dlPicture.setImageBitmap(bitmap);
                            dlTitle.setText(tName);
                            myPicture.show();
                        }
                        else Toast.makeText(getActivity(),"사진이 존재하지 않습니다.",Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getActivity(),"우효",Toast.LENGTH_SHORT).show();


                    }
                });
            }
            layout.addView(btn);
            LinearLayout.LayoutParams mLayoutParams=(LinearLayout.LayoutParams) btn.getLayoutParams();
            mLayoutParams.bottomMargin=10;
            mLayoutParams.leftMargin=5;
            mLayoutParams.rightMargin=5;
            btn.setLayoutParams(mLayoutParams);
        }
        toDayCursor.close();
    }
}