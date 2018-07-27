package com.lee.android.MDMP.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.android.MDMP.R;
import com.lee.android.MDMP.Tool.MyDBHelper;
import com.lee.android.MDMP.Tool.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ib_info_back;
    private ImageButton ib_info_edit;
    private CircleImageView civ_info_head;
    private TextView tv_info_username;
    private TextView tv_info_gender;
    private TextView tv_info_birthday;
    private TextView tv_info_introduce;

    private int id = -1;
    private MyDBHelper dbHelper = new MyDBHelper(this, "Userinfo.db", null, 1);
    private SQLiteDatabase db;
    private Uri outputUri = null;
    private Bitmap bitmap = null;

    public static final int CHOOSE_PICTURE = 0;
    public static final int CROP_PICTURE = 1;

    public static final String UPDATE="com.lee.android.MDMP.UPDATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        findView();
        setView();
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        db = dbHelper.getReadableDatabase();
        init();
    }

    private void findView() {
        ib_info_back = (ImageButton) findViewById(R.id.ib_info_back);
        ib_info_edit = (ImageButton) findViewById(R.id.ib_info_edit);
        civ_info_head = (CircleImageView) findViewById(R.id.civ_info_head);
        tv_info_username = (TextView) findViewById(R.id.tv_info_username);
        tv_info_gender = (TextView) findViewById(R.id.tv_info_gender);
        tv_info_birthday = (TextView) findViewById(R.id.tv_info_birthday);
        tv_info_introduce = (TextView) findViewById(R.id.tv_info_introduce);
    }

    private void setView() {
        ib_info_back.setOnClickListener(this);
        ib_info_edit.setOnClickListener(this);
        civ_info_head.setOnClickListener(this);
    }

    private void init() {
        Bitmap bitmap = Utility.getPictuBitmap(db, id);
        String username = getUsername(id);
        String gender = getGender(id);
        String birthday = getBirthday(id);
        String introduce = getIntroduce(id);
        if (bitmap != null)
            civ_info_head.setImageBitmap(bitmap);
        tv_info_username.setText(username);
        if (gender != null)
            tv_info_gender.setText(gender);
        else
            tv_info_gender.setText("未知");
        if (birthday != null)
            tv_info_birthday.setText(birthday);
        else
            tv_info_birthday.setText("未知");
        if (introduce != null)
            tv_info_introduce.setText(introduce);
        else
            tv_info_introduce.setText("这家伙很懒，什么都没有留下。");
    }

    public String getUsername(int id) {
        Cursor cursor = db.query("users", new String[]{"username"}, "id=" + id, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("username"));
            }
        }
        return null;
    }

    public String getBirthday(int id) {
        Cursor cursor = db.query("user_info", new String[]{"birthday"}, "id=" + id, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("birthday"));
            }
        }
        return null;
    }

    public String getGender(int id) {
        Cursor cursor = db.query("user_info", new String[]{"gender"}, "id=" + id, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("gender"));
            }
        }
        return null;
    }

    public String getIntroduce(int id) {
        Cursor cursor = db.query("user_info", new String[]{"introduce"}, "id=" + id, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("introduce"));
            }
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.ib_info_back:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.ib_info_edit:
                intent = new Intent(this, EditInfoActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
                finish();
                break;
            case R.id.civ_info_head:
                intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_PICTURE);    //调用相册选择图片
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {   //判断返回码是否可用
            switch (requestCode) {
                case CHOOSE_PICTURE:
                    cropPicture(data.getData());
                    break;
                case CROP_PICTURE:
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputUri));
                        if (bitmap != null) {
                            civ_info_head.setImageBitmap(bitmap);
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            byte[] bytes = outputStream.toByteArray();
                            ContentValues cv = new ContentValues();
                            cv.put("user_image", bytes);
                            db.update("users",cv,"id="+id,null);
                            Intent intent=new Intent(UPDATE);
                            sendBroadcast(intent);
                            Toast.makeText(this,"头像已更新",Toast.LENGTH_LONG).show();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    protected void cropPicture(Uri uri) {
        // 创建File对象，用于存储裁剪后的图片，避免更改原图
        File file = new File(getExternalCacheDir(), "crop_image.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputUri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        //裁剪图片的宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("crop", "true");//可裁剪
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);//支持缩放
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());//输出图片格式
        intent.putExtra("noFaceDetection", true);//取消人脸识别
        startActivityForResult(intent, CROP_PICTURE);
    }
}
