package com.lee.android.MDMP.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lee.android.MDMP.Activity.MainActivity;
import com.lee.android.MDMP.R;

public class ChangePwdFragment extends Fragment implements View.OnClickListener {

    private EditText ed_password;
    private EditText ed_newpassword;
    private EditText ed_confirmpassword;
    private Button bt_changepwd;
    private Button bt_canclechange;
    private Context mContext;

    private int id = -1;
    private SQLiteDatabase db = null;

    // TODO: Rename and change types and number of parameters
    public static ChangePwdFragment newInstance(SQLiteDatabase db, int id) {
        ChangePwdFragment fragment = new ChangePwdFragment();
        fragment.db = db;
        fragment.id = id;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_changepwd, container, false);
        ed_password = view.findViewById(R.id.ed_currentpassword);
        ed_newpassword = view.findViewById(R.id.ed_newpassword);
        ed_confirmpassword = view.findViewById(R.id.ed_confirmnewpassword);
        bt_changepwd = view.findViewById(R.id.bt_changepwd);
        bt_canclechange = view.findViewById(R.id.bt_canclechange);
        bt_changepwd.setOnClickListener(this);
        bt_canclechange.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_canclechange:
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.bt_changepwd:
                String currentpassword = ed_password.getText().toString().trim();
                String password = ed_newpassword.getText().toString().trim();
                String confirmpassword = ed_confirmpassword.getText().toString().trim();
                if ("".equals(password)) {
                    Toast.makeText(mContext, "新密码不能为空", Toast.LENGTH_LONG).show();
                } else if (password.length() < 6 || password.length() > 18) {
                    Toast.makeText(mContext, "密码长度不正确", Toast.LENGTH_LONG).show();
                } else if (!password.equals(confirmpassword)) {
                    Toast.makeText(mContext, "两次密码不一致", Toast.LENGTH_LONG).show();
                } else {
                    if (!checkPassword(currentpassword))
                        Toast.makeText(mContext, "原密码有误", Toast.LENGTH_LONG).show();
                    else
                        changePassword(password);
                }
                break;
        }
    }

    private boolean checkPassword(String password) {
        if (db != null) {
            Cursor cursor = db.query("users", new String[]{"password"}, "id=" + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                String currentpwd = cursor.getString(cursor.getColumnIndex("password")).trim();
                if (password.equals(currentpwd)) {
                    return true;
                }
            }
        }else {
            Toast.makeText(mContext, "未获取到数据库", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void changePassword(String password) {
        if (db != null) {
            ContentValues cv = new ContentValues();
            cv.put("password", password);
            db.update("users", cv, "id=" + id, null);
            Toast.makeText(mContext, "密码已修改", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
