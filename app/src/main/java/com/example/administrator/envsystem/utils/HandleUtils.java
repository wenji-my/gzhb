package com.example.administrator.envsystem.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import static com.example.administrator.envsystem.conf.MessageWhat.MSG_CHAOSHI;
import static com.example.administrator.envsystem.conf.MessageWhat.MSG_CUOWU;
import static com.example.administrator.envsystem.conf.MessageWhat.MSG_DISMISS;
import static com.example.administrator.envsystem.conf.MessageWhat.MSG_SEARCH;
import static com.example.administrator.envsystem.conf.MessageWhat.MSG_SHOW;
import static com.example.administrator.envsystem.conf.MessageWhat.MSG_UPLOAD;

/**
 * Created by Administrator on 2017/4/13.
 */

public class HandleUtils extends Handler {

    private ProgressDialog builder2 = null;
    private Context context;
    public HandleUtils(Context context){
        this.context=context;
    }
    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MSG_SHOW) {
            builder2 = new ProgressDialog(context);
            builder2.setMessage("正在登陆中，请稍等。。。");
            builder2.setCancelable(false);
            builder2.show();
        }
        if (msg.what == MSG_SEARCH) {
            builder2 = new ProgressDialog(context);
            builder2.setMessage("正在查询中，请稍等。。。");
            builder2.setCancelable(false);
            builder2.show();
        }
        if (msg.what == MSG_UPLOAD) {
            builder2 = new ProgressDialog(context);
            builder2.setMessage("正在上传中，请稍等。。。");
            builder2.setCancelable(false);
            builder2.show();
        }
        if (msg.what == MSG_DISMISS) {
            builder2.dismiss();
        }
        if (msg.what == MSG_CHAOSHI) {
            DialogTool.AlertDialogShow(context, "请求超时，请重新登录!");
        }

        if (msg.what == MSG_CUOWU) {
            DialogTool.AlertDialogShow(context, msg.obj.toString());
        }
    }
}
