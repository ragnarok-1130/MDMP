package com.lee.android.MDMP.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.lee.android.MDMP.R;
import com.lee.android.MDMP.Tool.MyDBHelper;
import com.lee.android.MDMP.Tool.Utility;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText ed_email;
    private EditText ed_password;
    private Button bt_login;
    private Button bt_register;
    private CheckBox ck_remember;

    private MyDBHelper dbHelper = new MyDBHelper(this, "Userinfo.db", null, 1);
    private SQLiteDatabase db;

    public static final String LOGIN_SUCCESS = "com.lee.android.MDMP.LOGIN_SUCCESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findView();
        setView();
        db = dbHelper.getReadableDatabase();
        getAccount();
    }

    private void findView() {
        ed_email = (EditText) findViewById(R.id.ed_email);
        ed_password = (EditText) findViewById(R.id.ed_password);
        ck_remember = (CheckBox) findViewById(R.id.ck_remember);
        bt_login = (Button) findViewById(R.id.bt_login);
        bt_register = (Button) findViewById(R.id.bt_register);
    }

    private void setView() {
        bt_login.setOnClickListener(this);
        bt_register.setOnClickListener(this);
    }

    public void getAccount() {
        SharedPreferences sp = getSharedPreferences("account", MODE_PRIVATE);
        boolean remember = sp.getBoolean("remember", false);
        String email = sp.getString("email", "");
        String password = sp.getString("password", "");
        ed_email.setText(email);
        if (remember)
            ed_password.setText(password);
        ck_remember.setChecked(remember);
    }

    public String getPassword(String email) {
        String sql = "select password from users";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("password")).trim();
        } else
            return "";
    }

    public int getID(String email) {
        String sql = "select id from users";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex("id"));
        } else
            return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                String email = ed_email.getText().toString().trim();
                String password = ed_password.getText().toString().trim();
                if ("".equals(email)) {
                    Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_LONG).show();
                } else if (!Utility.isEmailAddress(email)) {
                    Toast.makeText(this, "请输入正确的邮箱地址", Toast.LENGTH_LONG).show();
                } else if ("".equals(password)) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
                } else if (password.length() < 6 || password.length() > 18) {
                    Toast.makeText(this, "密码长度为6-18为数字、字母或符号", Toast.LENGTH_LONG).show();
                } else if (!getPassword(email).equals(password)) {
                    Toast.makeText(this, "账户或密码不正确", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "登陆成功", Toast.LENGTH_LONG).show();
                    int id = getID(email);
                    SharedPreferences sp = getSharedPreferences("account", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    if (ck_remember.isChecked()) {
                        editor.putBoolean("remember", true);
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.commit();
                    } else {
                        editor.putBoolean("remember", false);
                        editor.putString("email", email);
                        editor.commit();
                    }
                    if (id != -1) {
                        Intent intent = new Intent(LOGIN_SUCCESS);
                        intent.putExtra("id", id);
                        sendBroadcast(intent);
                    }
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.bt_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
