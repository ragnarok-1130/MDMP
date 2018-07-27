package com.lee.android.MDMP.Activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.lee.android.MDMP.R;
import com.lee.android.MDMP.Tool.MyDBHelper;

import java.util.Calendar;

public class EditInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar_edit;
    private EditText ed_edit_username;
    private RadioButton rb_male;
    private RadioButton rb_female;
    private RadioButton rb_pirvate;
    private Button bt_edit_birthday;
    private Button bt_cancle;
    private Button bt_submit;
    private EditText ed_edit_introduce;

    private MyDBHelper dbHelper = new MyDBHelper(this, "Userinfo.db", null, 1);
    private SQLiteDatabase db;
    private int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        findView();
        setView();
        db = dbHelper.getWritableDatabase();
        init();
    }

    private void findView() {
        toolbar_edit = findViewById(R.id.toolbar_edit);
        ed_edit_username = findViewById(R.id.ed_edit_username);
        rb_male = findViewById(R.id.rb_male);
        rb_female = findViewById(R.id.rb_female);
        rb_pirvate = findViewById(R.id.rb_pirvate);
        bt_edit_birthday = findViewById(R.id.bt_edit_birthday);
        ed_edit_introduce = findViewById(R.id.ed_edit_introduce);
        bt_cancle = findViewById(R.id.bt_cancle);
        bt_submit = findViewById(R.id.bt_submit);
    }

    private void setView() {
        setSupportActionBar(toolbar_edit);
        bt_edit_birthday.setOnClickListener(this);
        bt_cancle.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
    }

    private void init() {
        if (id != -1) {
            String username = getUsername(id);
            String gender = getGender(id);
            String birthday = getBirthday(id);
            String introduce = getIntroduce(id);
            ed_edit_username.setText(username);
            if (gender != null) {
                if ("男".equals(gender.trim())) {
                    rb_male.setSelected(true);
                } else if ("女".equals(gender.trim())) {
                    rb_female.setSelected(true);
                } else if ("不公开".equals(gender.trim())) {
                    rb_pirvate.setSelected(true);
                }
            } else
                rb_pirvate.setSelected(true);
            if (birthday != null)
                bt_edit_birthday.setText(birthday);
            else
                bt_edit_birthday.setText("未知");
            if (introduce != null)
                ed_edit_introduce.setText(introduce);
            else
                ed_edit_introduce.setText("这家伙很懒，什么都没有留下。");
        } else {
            Toast.makeText(this, "未获取到用户id信息", Toast.LENGTH_LONG).show();
        }
    }

    private void showDatePickDlg() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(EditInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                EditInfoActivity.this.bt_edit_birthday.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.bt_cancle:
                intent = new Intent(this, PersonalInfoActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.bt_submit:
                updateInfo();
                break;
            case R.id.bt_edit_birthday:
                showDatePickDlg();
                break;
        }
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

    public void updateInfo() {
        String username = ed_edit_username.getText().toString().trim();
        String gender = null;
        if (rb_male.isChecked())
            gender = "男";
        else if (rb_female.isChecked())
            gender = "女";
        else if (rb_pirvate.isChecked())
            gender = "不公开";
        String birthday = bt_edit_birthday.getText().toString().trim();
        String introduce = ed_edit_introduce.getText().toString();
        if ("".equals(introduce.trim())) {
            introduce = null;
        }
        if ("".equals(username)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_LONG).show();
        } else {
            ContentValues cv = new ContentValues();
            cv.put("username", username);
            db.update("users", cv, "id=" + id, null);
            if (getBirthday(id) == null) {    //判断info表是否有此id的数据
                cv = new ContentValues();
                cv.put("id", id);
                cv.put("gender", gender);
                cv.put("birthday", birthday);
                cv.put("introduce", introduce);
                db.insert("user_info", null, cv);
            } else {
                cv = new ContentValues();
                cv.put("gender", gender);
                cv.put("birthday", birthday);
                cv.put("introduce", introduce);
                db.update("user_info", cv, "id=" + id, null);
            }
            Intent intent = new Intent(PersonalInfoActivity.UPDATE);
            sendBroadcast(intent);
            Toast.makeText(this, "个人信息已更新", Toast.LENGTH_LONG).show();
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
