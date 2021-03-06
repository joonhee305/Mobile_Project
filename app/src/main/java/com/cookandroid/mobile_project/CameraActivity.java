package com.cookandroid.mobile_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";

    public static final int REQUEST_TAKE_PHOTO = 10;
    public static final int REQUEST_PERMISSION = 11;


    private Button btnCamera, btnSave,btnCameraCancel;
    private ImageView ivCapture;
    private String mCurrentPhotoPath;
    private String picturePath;
    private Intent getPath, mainActivity;
    private myDBHelper myDBHelper;
    private SQLiteDatabase sqLiteDatabase;
    private String[] hData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mainActivity = new Intent(this, MainActivity.class);
        checkPermission(); //권한체크

        myDBHelper=new myDBHelper(this);
        sqLiteDatabase=myDBHelper.getWritableDatabase();

        ivCapture = findViewById(R.id.ivCapture); //ImageView 선언
        btnCamera = findViewById(R.id.btnCapture); //Button 선언
        btnSave = findViewById(R.id.btnSave); //Button 선언
        btnCameraCancel = findViewById(R.id.btnCameraCancel);

        getPath=getIntent();
        picturePath=getPath.getStringExtra("Path");
        hData=getPath.getStringArrayExtra("tData");

        //촬영
        btnCamera.setOnClickListener(v -> captrueCamera());

        //저장
        btnSave.setOnClickListener(v -> {
            try {

                BitmapDrawable drawable = (BitmapDrawable) ivCapture.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                //찍은 사진이 없으면
                if (bitmap == null) {
                    Toast.makeText(this, "저장할 사진이 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    //저장
                    long now=System.currentTimeMillis();
                    Date date= new Date(now);
                    SimpleDateFormat sdfHour=new SimpleDateFormat("hh");
                    SimpleDateFormat sdfMinute=new SimpleDateFormat("mm");
                    int hTime=Integer.parseInt(sdfHour.format(date))*60+Integer.parseInt(sdfMinute.format(date));
                    saveImg(picturePath);
                    mCurrentPhotoPath = ""; //initialize
                    sqLiteDatabase.execSQL("update toDayTBL set tCheck = 1 where tId = "+hData[3]+"");
                    sqLiteDatabase.execSQL("insert into historyTBL values('"+hData[0]+"','"+hData[1]+"','"+hData[2]+"','"+hTime+"','"+picturePath+"');");
                    //Toast.makeText(this,"사진 저장!",Toast.LENGTH_SHORT).show();
                    Intent mainActivity=new Intent(this,MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                }

            } catch (Exception e) {
                Log.w(TAG, "SAVE ERROR!", e);
            }
        });

        btnCameraCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(mainActivity);
                finish();
            }
        });
    }

    //사진촬영
    private void captrueCamera(){
        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            File photoFile=null;
            try{
                File tempDir=getCacheDir();
                File tempImage=File.createTempFile(picturePath,".jpg",tempDir);
                mCurrentPhotoPath=tempImage.getAbsolutePath();
                photoFile=tempImage;
                //Toast.makeText(getApplicationContext(),photoFile.getPath(),Toast.LENGTH_SHORT).show();
            }catch (IOException e) {
                Log.w(TAG,"파일 생성 에러!",e);
            }

            if(photoFile!=null){
                Uri photoURI=FileProvider.getUriForFile(this,getPackageName()+".fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
            }

        }

    }

    //사진 저장
    private void saveImg(String path){
        try{
            File storageDir=new File(getFilesDir()+"/capture");
            if(!storageDir.exists()) storageDir.mkdirs();

            String filename=path+".jpg";

            File file=new File(storageDir,filename);
            boolean deleted=file.delete();
            Log.w(TAG,"Delete Dup Check"+deleted);

            FileOutputStream output=null;
            try{
                output=new FileOutputStream(file);
                BitmapDrawable drawable=(BitmapDrawable) ivCapture.getDrawable();
                Bitmap bitmap=drawable.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);
            }catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert output != null;
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.e(TAG, "Captured Saved");
            Toast.makeText(this, "인증 완료!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.w(TAG, "Capture Saving Error!", e);
            Toast.makeText(this, "인증 실패", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            //after capture
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {
                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));

                        if (bitmap != null) {
                            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);

                            //사진해상도가 너무 높으면 비트맵으로 로딩
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 8; //8분의 1크기로 비트맵 객체 생성
                            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

                            Bitmap rotatedBitmap = null;
                            switch (orientation) {

                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotatedBitmap = rotateImage(bitmap, 90);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotatedBitmap = rotateImage(bitmap, 180);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotatedBitmap = rotateImage(bitmap, 270);
                                    break;

                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotatedBitmap = bitmap;
                            }

                            //Rotate한 bitmap을 ImageView에 저장
                            ivCapture.setImageBitmap(rotatedBitmap);

                        }
                    }
                    break;
                }
            }

        } catch (Exception e) {
            Log.w(TAG, "onActivityResult Error !", e);
        }
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermission(); //권한체크
    }

    //권한 확인
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // 권한이 취소되면 result 배열은 비어있다.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "권한 확인", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();
                    finish(); //권한이 없으면 앱 종료
                }
            }
        }
    }
}