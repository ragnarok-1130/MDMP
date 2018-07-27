package com.lee.android.MDMP.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lee.android.MDMP.R;

public class HelpActivity extends AppCompatActivity {

    private Button bt_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);
        bt_next = (Button) findViewById(R.id.bt_next);
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestPower()){
                    Intent intent=new Intent(HelpActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{

                }
            }
        });
    }

    public boolean requestPower() {  //判断是否已经赋予权限
        boolean flag=false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {  //判断用户是否彻底禁止弹出权限请求
                Toast.makeText(this,"您已禁止本app读取您的存储，请在设置-应用管理中找到本app并授权",Toast.LENGTH_LONG).show();
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
            }
        }else {
            flag=true;
        }
        return flag;
    }
}
