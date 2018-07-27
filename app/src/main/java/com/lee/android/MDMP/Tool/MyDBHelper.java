package com.lee.android.MDMP.Tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {

    private static final String CREATE_USER_TABLE = "create table users(" +
            "id integer primary key autoincrement," +
            "username text not null," +
            "email text unique," +
            "password text not null," +
            "user_image blob)";

    private static final String CREATE_INFO_TABLE = "create table user_info(" +
            "id integer primary key," +
            "gender text check (gender in( '男','女','不公开'))," +
            "birthday text," +
            "introduce text)";

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_INFO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists users");
        db.execSQL("drop table if exists user_info");
        onCreate(db);
    }
}
