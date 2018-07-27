package com.lee.android.MDMP.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lee.android.MDMP.R;

public class welcomeActivity extends AppCompatActivity implements View.OnClickListener, Runnable {

    private Button bt_skip;
    boolean flag;
    boolean permission;
    boolean isclicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        bt_skip = (Button) findViewById(R.id.bt_skip);
        bt_skip.setOnClickListener(this);
        flag = isFirstOpen();
        permission = checkPermission();
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
            if (!isclicked)
                bt_skip.callOnClick();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            finish();
        }
    }

    public boolean isFirstOpen() {
        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        boolean flag = preferences.getBoolean("flag", true);
        if (flag) {
            bt_skip.setVisibility(View.INVISIBLE);
            return flag;
        } else {
            bt_skip.setVisibility(View.VISIBLE);
            return flag;
        }
    }

    public boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_skip:
                Intent intent = null;
                isclicked = true;
                if (flag) {
                    intent = new Intent(this, GuideActivity.class);
                    startActivity(intent);
                    finish();
                } else if (!permission) {
                    intent = new Intent(this, HelpActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
