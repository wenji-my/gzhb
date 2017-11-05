package com.example.administrator.envsystem;

import android.app.Application;

import com.example.administrator.envsystem.db.SQLFuntion;

/**
 * Created by Administrator on 2017/8/13.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SQLFuntion.initTable(this);
    }
}
