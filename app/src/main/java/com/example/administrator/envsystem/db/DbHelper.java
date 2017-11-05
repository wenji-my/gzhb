package com.example.administrator.envsystem.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/8/12.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "abc.db";
    public static final int VERSION = 1;
    public DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建表语句执行
        String sql="create table "+DBconf.TABLE_NAME+"(pid integer primary key autoincrement,jylsh varchar(64),photo01 varchar(1000),photo02 varchar(1000),photo03 varchar(1000),photo04 varchar(1000), V0101 varchar(5),V0102 varchar(5),V0103 varchar(5),V0104 varchar(5),V0201 varchar(5),V0202 varchar(5),V0203 varchar(5),V0204 varchar(5),V0205 varchar(5),V0401 varchar(5),V0402 varchar(5),V0403 varchar(5),V0501 varchar(64),V0502 varchar(64),C01 varchar(5),C02 varchar(5),time varchar(64))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
