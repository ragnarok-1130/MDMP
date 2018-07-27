package com.lee.android.MDMP.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lee.android.MDMP.Fragment.ChangePwdFragment;
import com.lee.android.MDMP.Fragment.SettingFragment;
import com.lee.android.MDMP.R;
import com.lee.android.MDMP.Tool.MyDBHelper;

public class SettingActivity extends AppCompatActivity {
    private int id = -1;
    private boolean login_status = false;
    private MyDBHelper dbHelper;
    private SQLiteDatabase db;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        dbHelper = new MyDBHelper(this, "Userinfo.db", null, 1);
        db = dbHelper.getWritableDatabase();
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        login_status = intent.getBooleanExtra("login_status", false);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_setting, SettingFragment.newInstance(db, login_status, id));
        fragmentTransaction.commit();
    }
}
