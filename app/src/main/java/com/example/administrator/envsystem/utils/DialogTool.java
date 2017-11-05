package com.example.administrator.envsystem.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Administrator on 2017/4/10.
 */

public class DialogTool {

    private static ProgressDialog builder2;

    public static void AlertDialogShow(Context context, String data) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("提示框");
        builder1.setMessage(data);
        builder1.setPositiveButton("确定", null);
        builder1.show();
    }

    public static void AlertDialogShow2(final Context context, String data) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("提示框");
        builder1.setMessage(data);
        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder1.show();
    }

    public static void showLoading(Context context,String msg){
        builder2 = new ProgressDialog(context);
        builder2.setMessage(msg);
        builder2.setCancelable(false);
        builder2.show();
    }

    public static void disLoading(){
        builder2.dismiss();
    }
}
