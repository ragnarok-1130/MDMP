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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lee.android.MDMP.R;
import com.lee.android.MDMP.Tool.MyDBHelper;
import com.lee.android.MDMP.Tool.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView civ_head;
    private EditText ed_email;
    private EditText ed_username;
    private EditText ed_passwrod;
    private EditText ed_confirmpassword;
    private Button bt_register;
    private Button bt_login;

    private MyDBHelper dbHelper = new MyDBHelper(this, "Userinfo.db", null, 1);
    private SQLiteDatabase db;
    private Uri outputUri = null;
    private Bitmap bitmap = null;

    public static final int CHOOSE_PICTURE = 0;
    public static final int CROP_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findView();
        setView();
        db = dbHelper.getWritableDatabase();
    }

    private void findView() {
        civ_head = findViewById(R.id.civ_head);
        ed_email = findViewById(R.id.ed_reg_email);
        ed_username = findViewById(R.id.ed_reg_username);
        ed_passwrod = findViewById(R.id.ed_reg_password);
        ed_confirmpassword = findViewById(R.id.ed_confirmpassword);
        bt_register = findViewById(R.id.bt_reg_register);
        bt_login = findViewById(R.id.bt_reg_login);
    }

    private void setView() {
        bt_login.setOnClickListener(this);
        bt_register.setOnClickListener(this);
        civ_head.setOnClickListener(this);
    }

    public boolean isExist(String email) {
        String sql = "select email from users where email='" + email + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }

    private void register() {
        String email = ed_email.getText().toString().trim();
        String username = ed_username.getText().toString().trim();
        String password = ed_passwrod.getText().toString().trim();
        String confirmpassword = ed_confirmpassword.getText().toString().trim();
        if ("".equals(email)) {
            Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_LONG).show();
        } else if (!Utility.isEmailAddress(email)) {
            Toast.makeText(this, "请输入正确的邮箱地址", Toast.LENGTH_LONG).show();
        } else if ("".equals(username)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_LONG).show();
        } else if ("".equals(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
        } else if (password.length() < 6 || password.length() > 18) {
            Toast.makeText(this, "密码长度为6-18为数字、字母或符号", Toast.LENGTH_LONG).show();
        } else if (!password.equals(confirmpassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_LONG).show();
        } else if (isExist(email)) {
            Toast.makeText(this, "此邮箱已注册", Toast.LENGTH_LONG).show();
        } else if (bitmap == null) {
            Toast.makeText(this, "请选择您的头像", Toast.LENGTH_LONG).show();
        } else {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] bytes = outputStream.toByteArray();
            ContentValues cv = new ContentValues();
            cv.put("username", username);
            cv.put("email", email);
            cv.put("password", password);
            cv.put("user_image", bytes);
            db.insert("users", null, cv);
            Toast.makeText(this, "注册成功！", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_reg_register:
                register();
                break;
            case R.id.bt_reg_login:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.civ_head:
                intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_PICTURE);    //调用相册选择图片
                break;
            default:
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
                        if (bitmap != null)
                            civ_head.setImageBitmap(bitmap);
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
