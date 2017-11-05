package com.example.administrator.envsystem.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/4/13.
 */

public class ToastUtils {

    public static void showToast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    public static void showToastLong(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
}
