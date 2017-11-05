package com.example.administrator.envsystem.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/5/26.
 */

public class ShareUtils {

    private static final String NAME = "set";

    public static  String getString(String title, Context context){
        SharedPreferences sharedPreferences =  context.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        String content = sharedPreferences.getString(title,"");
        return content;
    }
}
