package com.lee.android.MDMP.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.badoualy.stepperindicator.StepperIndicator;
import com.lee.android.MDMP.Adapter.MyFragmentAdapter;
import com.lee.android.MDMP.Fragment.GuideFragment1;
import com.lee.android.MDMP.Fragment.GuideFragment2;
import com.lee.android.MDMP.R;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager viewPager;
    private StepperIndicator indicator;
    private Button bt_uselee;
    private Button bt_continue;
    private List<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        findView();
        initFragment();
        FragmentManager fm = getSupportFragmentManager();
        MyFragmentAdapter adapter = new MyFragmentAdapter(fm, fragments);
        bt_uselee.setOnClickListener(this);
        bt_continue.setOnClickListener(this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        indicator.setCurrentStep(0);
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentStep(position);
                if (position == fragments.size() - 1) {
                    bt_uselee.setVisibility(View.VISIBLE);
                    bt_continue.setVisibility(View.VISIBLE);
                }else {
                    bt_uselee.setVisibility(View.INVISIBLE);
                    bt_continue.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void findView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        indicator = (StepperIndicator) findViewById(R.id.indicator);
        bt_uselee = (Button) findViewById(R.id.bt_fxxkoff);
        bt_continue = (Button) findViewById(R.id.bt_continue);
    }

    private void initFragment() {
        GuideFragment1 guide1 = new GuideFragment1();
        GuideFragment1 guide2 = new GuideFragment1();
        GuideFragment1 guide3 = new GuideFragment1();
        GuideFragment1 guide4 = new GuideFragment1();
        GuideFragment2 guide5 = new GuideFragment2();
        guide1.setImageId(R.drawable.pic0);
        guide2.setImageId(R.drawable.pic1);
        guide3.setImageId(R.drawable.pic2);
        guide4.setImageId(R.drawable.pic3);
        fragments.add(guide1);
        fragments.add(guide2);
        fragments.add(guide3);
        fragments.add(guide4);
        fragments.add(guide5);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_fxxkoff:
                AlertDialog dlg = new AlertDialog.Builder(GuideActivity.this).setTitle("退出应用").setMessage("打扰了，告辞！").setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                }).create();
                dlg.show();
                break;
            case R.id.bt_continue:
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                boolean flag = preferences.getBoolean("flag", true);
                SharedPreferences.Editor editor = preferences.edit();
                if (flag) {
                    Intent intent = new Intent(this, HelpActivity.class);
                    editor.putBoolean("flag", false);
                    editor.commit();
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
        }
    }
}
