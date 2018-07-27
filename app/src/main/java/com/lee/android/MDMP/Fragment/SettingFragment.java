package com.lee.android.MDMP.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lee.android.MDMP.Activity.LoginActivity;
import com.lee.android.MDMP.Activity.MainActivity;
import com.lee.android.MDMP.R;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private SQLiteDatabase db = null;
    private boolean login_status = false;
    private int id = -1;
    private Button bt_login;
    private Button bt_changepwd;
    private Button bt_logout;

    private Context mContext = null;

    public static final String LOGOUT = "com.lee.android.MDMP.LOGOUT";

    public static SettingFragment newInstance(SQLiteDatabase db, boolean login_status, int id) {
        SettingFragment fragment = new SettingFragment();
        fragment.db=db;
        fragment.login_status = login_status;
        fragment.id=id;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        bt_login = view.findViewById(R.id.bt_setting_login);
        bt_changepwd = view.findViewById(R.id.bt_setting_changepwd);
        bt_logout = view.findViewById(R.id.bt_setting_logout);
        bt_changepwd.setOnClickListener(this);
        bt_logout.setOnClickListener(this);
        bt_login.setOnClickListener(this);
        if (login_status) {
            bt_login.setVisibility(View.INVISIBLE);
        } else {
            bt_logout.setVisibility(View.INVISIBLE);
            bt_changepwd.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.bt_setting_login:
                intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.bt_setting_logout:
                intent = new Intent(LOGOUT);
                mContext.sendBroadcast(intent);
                intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.bt_setting_changepwd:
                FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fl_setting,ChangePwdFragment.newInstance(db,id));
                fragmentTransaction.commit();
                break;
        }
    }
}
