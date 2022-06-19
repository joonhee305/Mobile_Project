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
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    //운동 리스트 선언
    private String[] youtubeURL = {"https://www.youtube.com/watch?v=lXUEMdde9hM","https://www.youtube.com/watch?v=I4ovzV-BLDU&t=529s",
            "https://www.youtube.com/watch?v=U_Tv31zKYkk","https://www.youtube.com/watch?v=WTVoxQUbcGo&t=41s",
            "https://www.youtube.com/watch?v=NABkYV6IWYA","https://www.youtube.com/watch?v=NABkYV6IWYA","https://www.youtube.com/watch?v=q-ySRwLEwY4"};
    private String[] exerName = {"걷기운동","근력강화운동","아령운동","타바타운동","하체강화운동","찔레꽃운동","손가락운동"};
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteOpenHelper myHelper;
    private HashMap<String,Integer> mapDate;
    private String month,day,d,nowDate,year;
    private LinearLayout layoutDoing, layoutExercise,layoutMedicine;
    //TextView testText;
    private Button btnExer;
    private int idx=300;
    private SharedPreferences prefs;
    //public ImageView iv;
    //int layoutIdx=1000;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_main, container, false);
        Button btn_addList = (Button) v.findViewById(R.id.btnAddList);

        layoutDoing=(LinearLayout) v.findViewById(R.id.layoutDoing);
        layoutExercise=(LinearLayout) v.findViewById(R.id.layoutExercise);
        layoutMedicine=(LinearLayout) v.findViewById(R.id.layoutMedicine);
        btnExer = (Button) v.findViewById(R.id.btnExer);

        mapDate=new HashMap<String,Integer>();
        setMapDate(mapDate);
        myHelper=new myDBHelper(getActivity());
        sqLiteDatabase=myHelper.getWritableDatabase();
        prefs= getActivity().getSharedPreferences("Pref", Context.MODE_PRIVATE);

        //최근 실행했을 때의 날짜 가져오기
        String lastDate=prefs.getString("lastDate"," ");

        //현재 날짜 가져오기
        long now=System.currentTimeMillis();
        Date date= new Date(now);
        SimpleDateFormat sdfMonth=new SimpleDateFormat("MM");
        SimpleDateFormat sdfDay=new SimpleDateFormat("dd");
        SimpleDateFormat sdfD=new SimpleDateFormat("EE");
        SimpleDateFormat sdfYear=new SimpleDateFormat("yyyy");
        month=sdfMonth.format(date);
        day=sdfDay.format(date);
        d=sdfD.format(date);
        year=sdfYear.format(date);
        nowDate=month+day+d;



        //오늘 일정의 데이터
        Cursor toDayCursor=sqLiteDatabase.rawQuery("select * from toDayTBL",null);
        if(!nowDate.equals(lastDate) || toDayCursor.getCount()==0){
            //일정 키값 및 이전 날짜 갱신
            idx=300;
            prefs.edit().putInt("toDayIdx",300).apply();
            prefs.edit().putString("lastDate",nowDate).apply();
            toDayCursor.close();
            //테이블 초기화
            sqLiteDatabase.execSQL("delete from toDayTBL");
            //루틴 가져오기
            Cursor routineCursor=sqLiteDatabase.rawQuery("select * from routineTBL",null);

            //루틴으로 toDayTBL 채우기
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
            //일정 키값 갱신
            prefs.edit().putInt("toDayIdx",idx).apply();
        }

        //일정 키값 가져오기
        idx=prefs.getInt("toDayIdx",300);
        //데이터 삽입

        try{
            setToDay("select * from toDayTBL where tType=='식사' or tType=='일정' order by tTime",sqLiteDatabase,layoutDoing);
            setToDay("select * from toDayTBL where tType=='운동' order by tTime",sqLiteDatabase,layoutExercise);
            setToDay("select * from toDayTBL where tType=='복약' order by tTime",sqLiteDatabase,layoutMedicine);
        }catch (FileNotFoundException e){Log.e("error","에러발생");};

        //운동 추천하기
        int index = Integer.parseInt(day)%7;
        btnExer.setText(exerName[index]);
        btnExer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL[index]));
                startActivity(intent);
            }
        });

        //일정 추가
        btn_addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddListActivity.class);
                intent.putExtra("idx",idx);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return v;
    }

    //월~일에 해당하는 비트값 선언
    public void setMapDate(HashMap<String,Integer> mapDate){
        mapDate.put("월",1);mapDate.put("Mon",1);
        mapDate.put("화",2);mapDate.put("Tue",2);
        mapDate.put("수",4);mapDate.put("Wed",4);
        mapDate.put("목",8);mapDate.put("Thu",8);
        mapDate.put("금",16);mapDate.put("Fri",16);
        mapDate.put("토",32);mapDate.put("Sat",32);
        mapDate.put("일",64);mapDate.put("Sun",64);
    }

    //시간값을 hh:mm 꼴로 변경
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
    //버튼 생성
    public void setToDay(String query,SQLiteDatabase sqLiteDatabase,LinearLayout layout) throws FileNotFoundException {
        Cursor toDayCursor=sqLiteDatabase.rawQuery(query,null);
        while(toDayCursor.moveToNext()){
            LinearLayout newLayout=new LinearLayout(getActivity());
            newLayout.setOrientation(LinearLayout.HORIZONTAL);

            //버튼과 레이아웃에 설정해줄 파라미터 선언
            LinearLayout.LayoutParams mLayoutParams=
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            mLayoutParams.rightMargin=5;
            mLayoutParams.leftMargin=5;
            mLayoutParams.bottomMargin=10;
            mLayoutParams.weight=1;

            //수정 및 삭제버튼에 적용할 파라미터
            LinearLayout.LayoutParams btnSubParams=
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            btnSubParams.weight=2;
            btnSubParams.bottomMargin=10;

            //데이터 버튼, 수정버튼, 삭제버튼
            Button btn=new Button(getActivity());
            Button btnUpdate=new Button(getActivity());
            Button btnDelete=new Button(getActivity());

            //쿼리로 가져온 데이터
            String tType=toDayCursor.getString(0);
            String tName=toDayCursor.getString(1);
            int tTime=toDayCursor.getInt(2);
            int tCheck=toDayCursor.getInt(3);
            int tId=toDayCursor.getInt(4);
            String value=tName+" "+timeToString(tTime);

            //버튼 셋팅
            btn.setText(value);
            btn.setId(tId);
            String path=year+month+day+btn.getId();
            String[] tData={tType,tName,year+month+day,tId+""};

            //수정 및 삭제 버튼
            btnUpdate.setText("수정");
            btnDelete.setText("삭제");

            //tCheck가 0 인증되지 않은 일정, 1이면 인증된 일정
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

                //수정 버튼
                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //수정 다이얼로그 선언
                        Dialog update;
                        update=new Dialog(getActivity());
                        update.setContentView(R.layout.dialog_update);
                        update.show();

                        Button updateSave=update.findViewById(R.id.updateSave);
                        Button updateCancel=update.findViewById(R.id.updateCancel);
                        EditText updateName=update.findViewById(R.id.updateName);
                        EditText updateHour=update.findViewById(R.id.updateHour);
                        EditText updateMinute=update.findViewById(R.id.updateMinute);

                        //수정하기 버튼
                        updateSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String name=updateName.getText().toString();
                                String hour=updateHour.getText().toString();
                                String minute=updateMinute.getText().toString();
                                int h,m,t;

                                if(name.length()==0) Toast.makeText(getActivity(),"이름을 입력해주세요",Toast.LENGTH_SHORT).show();
                                else{
                                    try{
                                        h=Integer.parseInt(hour);
                                        m=Integer.parseInt(minute);
                                        t=h*60+m;
                                        //동일한 id를 가진 데이터의 이름과 시간을 수정
                                        sqLiteDatabase.execSQL("update toDayTBL set tName='"+name+"', tTime="+t+" where tId = "+tId+"");

                                        Intent mainActivity=new Intent(getActivity(),MainActivity.class);
                                        startActivity(mainActivity);
                                        update.dismiss();
                                        getActivity().finish();

                                    }catch (Exception e){
                                        Toast.makeText(getActivity(),"시간을 다시 입력해주세요",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                        //취소버튼
                        updateCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                update.dismiss();
                            }
                        });
                    }
                });

                //삭제 버튼
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //삭제 다이얼로그
                        Dialog delete;
                        delete=new Dialog(getActivity());
                        delete.setContentView(R.layout.dialog_delete);
                        delete.show();

                        Button deleteYes=delete.findViewById(R.id.deleteYes);
                        Button deleteNo=delete.findViewById(R.id.deleteNo);

                        //삭제하기
                        deleteYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //해당 아이디를 가진 데이터를 삭제
                                sqLiteDatabase.execSQL("delete from toDayTBL where tId= ?;",new String[] {Integer.toString(tId)} );

                                Intent mainActivity=new Intent(getActivity(),MainActivity.class);
                                startActivity(mainActivity);
                                delete.dismiss();
                                getActivity().finish();
                            }
                        });

                        //취소하기
                        deleteNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                delete.dismiss();
                            }
                        });
                    }
                });

                //버튼 추가
                newLayout.addView(btn,mLayoutParams);
                newLayout.addView(btnUpdate,btnSubParams);
                newLayout.addView(btnDelete,btnSubParams);
            }
            else{
                //사진 파일 가져오기
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
                        //사진이 있다면 다이얼로그로 사진 보여주기
                        if(bitmap!=null) {
                            dlPicture.setImageBitmap(bitmap);
                            dlTitle.setText(tName);
                            myPicture.show();
                        }
                        //사진이 없다면
                        else Toast.makeText(getActivity(),"사진이 존재하지 않습니다.",Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getActivity(),"우효",Toast.LENGTH_SHORT).show();


                    }
                });
                //버튼들 모음 레이아웃 추가
                newLayout.addView(btn,mLayoutParams);
            }
            //버튼 모음 레이아웃을 기존 레이아웃에 추가
            layout.addView(newLayout,mLayoutParams);
        }
        //오늘의 일정 커서 종료
        toDayCursor.close();
    }
}