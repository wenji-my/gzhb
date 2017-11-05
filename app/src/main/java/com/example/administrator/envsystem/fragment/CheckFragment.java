package com.example.administrator.envsystem.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.envsystem.R;
import com.example.administrator.envsystem.activity.DetailActivity;
import com.example.administrator.envsystem.adapter.MyExpandableListViewAdapter;
import com.example.administrator.envsystem.bean.PdaTaskList;
import com.example.administrator.envsystem.bean.PdaTaskResult;
import com.example.administrator.envsystem.bean.PdaTaskResultResponse;
import com.example.administrator.envsystem.conf.StaticValues;
import com.example.administrator.envsystem.db.SQLFuntion;
import com.example.administrator.envsystem.utils.DialogTool;
import com.example.administrator.envsystem.utils.FtpUtil;
import com.example.administrator.envsystem.utils.TimeUtils;
import com.example.administrator.envsystem.utils.WebServiceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/4/14.
 */

public class CheckFragment extends Fragment implements View.OnClickListener {

    private PdaTaskList pdaTaskList;
    private TextView titleTv;
    private ImageButton backBtn;
    private Button subBtn;
    private Button optBtn;
    private Button reqBtn;
    private LinearLayout reqLl;
    private ExpandableListView mExLv;
    private MyExpandableListViewAdapter myAdapter;
    private DetailActivity mActivity;
    private ProgressDialog builder2 = null;
    private HashMap<String, Boolean> exlvData;
    private HashMap<String, String> etData;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0032:
                    builder2 = new ProgressDialog(getActivity());
                    builder2.setMessage("正在提交中，请稍等。。。");
                    builder2.setCancelable(false);
                    builder2.show();
                    break;
                case 0x0033:
                    builder2.dismiss();
                    break;
                case 0x0034:
                    builder2 = new ProgressDialog(getActivity());
                    builder2.setMessage("正在查询中，请稍等。。。");
                    builder2.setCancelable(false);
                    builder2.show();
                    break;
                case 0x0035:
                    builder2.dismiss();
                    myAdapter.setData(pdaTaskList.getEnv(), exlvData, etData);
                    mExLv.setAdapter(myAdapter);
                    //展开所有项
                    for (int i = 0; i < myAdapter.getGroupCount(); i++) {
                        mExLv.expandGroup(i);
                    }
                    break;
                case 0x0036:
                    DialogTool.AlertDialogShow(getActivity(), "保存数据库失败！");
                    break;
                case 0x0037:
                    DialogTool.AlertDialogShow(getActivity(), "网络异常，提交失败！");
                    break;
                case 0x0038:
                    DialogTool.AlertDialogShow(getActivity(), "提交数据成功！");
                    break;
                case 0x0039:
                    DialogTool.AlertDialogShow(getActivity(), "提交数据失败！");
                    break;
                case 0x0040:
                    DialogTool.AlertDialogShow(getActivity(), "正面照片上传失败");
                    break;
                case 0x0041:
                    DialogTool.AlertDialogShow(getActivity(), "尾部照片上传失败");
                    break;
                case 0x0042:
                    DialogTool.AlertDialogShow(getActivity(), "排气管照片上传失败");
                    break;
                case 0x0043:
                    DialogTool.AlertDialogShow(getActivity(), "VIN码照片上传失败");
                    break;
            }
        }
    };
    private SharedPreferences preferences;
    private String ftpPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check, null, false);
        titleTv = (TextView) view.findViewById(R.id.querition_title);
        backBtn = (ImageButton) view.findViewById(R.id.photo_back);
        subBtn = (Button) view.findViewById(R.id.submit_data);
        optBtn = (Button) view.findViewById(R.id.optional_parameters);
        reqBtn = (Button) view.findViewById(R.id.required_parameters);
        reqLl = (LinearLayout) view.findViewById(R.id.lin_btcs);
        mExLv = (ExpandableListView) view.findViewById(R.id.ex_list);
        mExLv.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        myAdapter = new MyExpandableListViewAdapter(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preferences = getActivity().getSharedPreferences("setting", 0);
        ftpPath = preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/";
        initData();
    }

    private void initData() {
        exlvData = new HashMap<>();
        etData = new HashMap<>();
        setDefault();
        titleTv.setText(pdaTaskList.getBusinessid());
        backBtn.setOnClickListener(this);
        subBtn.setOnClickListener(this);
        optBtn.setOnClickListener(this);
        reqBtn.setOnClickListener(this);
    }

    private void setDefault() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x0034);
                List<PdaTaskList.Env> envList = pdaTaskList.getEnv();
                //查询数据库有没有这个流水号
                boolean hasData = SQLFuntion.queryAll2(pdaTaskList.getBusinessid());

                for (int i = 0; i < envList.size(); i++) {
                    PdaTaskList.Env env = envList.get(i);
                    if (env.getVisPCode().equals("V01") || env.getVisPCode().equals("V02")) {
                        if (!hasData) {
                            //没有数据，设置默认值
                            exlvData.put(env.getVisCode(), false);
                        } else {
                            //有数据，获取数据库的数据
                            String s = SQLFuntion.queryBySelection2(new String[]{env.getVisCode()}, pdaTaskList.getBusinessid());
                            if (TextUtils.isEmpty(s) || s.equals("0")) {
                                exlvData.put(env.getVisCode(), false);
                            } else if (s.equals("1")) {
                                exlvData.put(env.getVisCode(), true);
                            }
                        }
                    }
                    if (env.getVisPCode().equals("V04") || env.getVisPCode().equals("PhotoIDCheck")) {
                        if (!hasData) {
                            //没有数据，设置默认值
                            exlvData.put(env.getVisCode(), true);
                        } else {
                            //有数据，获取数据库的数据
                            String s = SQLFuntion.queryBySelection2(new String[]{env.getVisCode()}, pdaTaskList.getBusinessid());
                            if (TextUtils.isEmpty(s) || s.equals("0")) {
                                exlvData.put(env.getVisCode(), false);
                            } else if (s.equals("1")) {
                                exlvData.put(env.getVisCode(), true);
                            }
                        }
                    }
                    if (env.getVisPCode().equals("V05")) {
                        String s = SQLFuntion.queryBySelection2(new String[]{env.getVisCode()}, pdaTaskList.getBusinessid());
                        etData.put(env.getVisCode(), s);
                    }
                }
                handler.sendEmptyMessage(0x0035);
            }
        }).start();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (DetailActivity) activity;
        pdaTaskList = ((DetailActivity) activity).getPdaTaskList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.optional_parameters:
//                reqLl.setVisibility(View.GONE);
//                mExLv.setVisibility(View.VISIBLE);
//                break;
//            case R.id.required_parameters:
//                mExLv.setVisibility(View.GONE);
//                reqLl.setVisibility(View.VISIBLE);
//                break;
            case R.id.submit_data://提交数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0x0032);
                        //检查FTP服务器有没有照片
                        checkFTP();
                        List<PdaTaskResult> pdaTaskResultList = new ArrayList<>();
                        PdaTaskResult pdaTaskResult = new PdaTaskResult();
                        pdaTaskResult.setBusinessid(pdaTaskList.getBusinessid());//编号
                        pdaTaskResult.setCheckresult(true);
                        //获取外检数据
                        List<PdaTaskResult.EnvBean> envBeanList = myAdapter.getDatas();
                        List<PdaTaskResult.PhotolistBean> photolistBeanList = new ArrayList<>();
                        HashMap<String, String> photoMap = mActivity.getPhotoMap();
                        if (photoMap != null) {
                            String path01 = photoMap.get("01");
                            String path02 = photoMap.get("02");
                            String path03 = photoMap.get("03");
                            String path04 = photoMap.get("04");
                            if (!TextUtils.isEmpty(path01)) {
                                PdaTaskResult.PhotolistBean bean01 = new PdaTaskResult.PhotolistBean();
                                bean01.setPhototype("01");
                                bean01.setPath(path01);
                                photolistBeanList.add(bean01);
                            }
                            if (!TextUtils.isEmpty(path02)) {
                                PdaTaskResult.PhotolistBean bean02 = new PdaTaskResult.PhotolistBean();
                                bean02.setPhototype("02");
                                bean02.setPath(path02);
                                photolistBeanList.add(bean02);
                            }
                            if (!TextUtils.isEmpty(path03)) {
                                PdaTaskResult.PhotolistBean bean03 = new PdaTaskResult.PhotolistBean();
                                bean03.setPhototype("03");
                                bean03.setPath(path03);
                                photolistBeanList.add(bean03);
                            }
                            if (!TextUtils.isEmpty(path04)) {
                                PdaTaskResult.PhotolistBean bean04 = new PdaTaskResult.PhotolistBean();
                                bean04.setPhototype("04");
                                bean04.setPath(path04);
                                photolistBeanList.add(bean04);
                            }
                        }else {
                            try {
                                boolean b = FtpUtil.searchFile("10.111.102.26", 21, "pda", "pda", pdaTaskList.getBusinessid() + "_ZMZP_01.jpg", preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/");
                                if (b){
                                    PdaTaskResult.PhotolistBean bean01 = new PdaTaskResult.PhotolistBean();
                                    bean01.setPhototype("01");
                                    bean01.setPath(ftpPath + pdaTaskList.getBusinessid() + "_ZMZP_01.jpg");
                                    photolistBeanList.add(bean01);
                                }
                                boolean b2 = FtpUtil.searchFile("10.111.102.26", 21, "pda", "pda", pdaTaskList.getBusinessid() + "_WBZP_02.jpg", preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/");
                                if (b2){
                                    PdaTaskResult.PhotolistBean bean02 = new PdaTaskResult.PhotolistBean();
                                    bean02.setPhototype("02");
                                    bean02.setPath(ftpPath + pdaTaskList.getBusinessid() + "_WBZP_02.jpg");
                                    photolistBeanList.add(bean02);
                                }
                                boolean b3 = FtpUtil.searchFile("10.111.102.26", 21, "pda", "pda", pdaTaskList.getBusinessid() + "_PQGZP_03.jpg", preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/");
                                if (b3){
                                    PdaTaskResult.PhotolistBean bean03 = new PdaTaskResult.PhotolistBean();
                                    bean03.setPhototype("03");
                                    bean03.setPath(ftpPath + pdaTaskList.getBusinessid() + "_PQGZP_03.jpg");
                                    photolistBeanList.add(bean03);
                                }
                                boolean b4 = FtpUtil.searchFile("10.111.102.26", 21, "pda", "pda", pdaTaskList.getBusinessid() + "_VINZP_04.jpg", preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/");
                                if (b4){
                                    PdaTaskResult.PhotolistBean bean04 = new PdaTaskResult.PhotolistBean();
                                    bean04.setPhototype("04");
                                    bean04.setPath(ftpPath + pdaTaskList.getBusinessid() + "_VINZP_04.jpg");
                                    photolistBeanList.add(bean04);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //先保存到数据库
                        boolean hasData = SQLFuntion.queryAll2(pdaTaskList.getBusinessid());
                        HashMap<String, String> sqlMap = myAdapter.getSqlMap();
                        if (!hasData) {//数据库不存在流水号，插入新数据
                            sqlMap.put("jylsh", pdaTaskList.getBusinessid());
                            boolean b = SQLFuntion.insertData(sqlMap);
                            if (!b) {
                                //插入数据失败
                                handler.sendEmptyMessage(0x0036);
                            }
                        } else {
                            //已存在流水号，更新数据
                            SQLFuntion.updateData(pdaTaskList.getBusinessid(), sqlMap);
                        }
                        pdaTaskResult.setEnv(envBeanList);
                        pdaTaskResult.setPhotolist(photolistBeanList);
                        pdaTaskResultList.add(pdaTaskResult);
                        String json = new Gson().toJson(pdaTaskResultList);
                        Log.i("TAG", "提交:" + json);
                        //添加参数
                        final HashMap<String, String> map = new HashMap<>();
                        map.put("arg0", StaticValues.token);
                        map.put("arg1", json);
                        String jsonStr = WebServiceUtils.connectWebService(StaticValues.ip, StaticValues.setPdaTaskResult, map);
                        handler.sendEmptyMessage(0x0033);
                        if (TextUtils.isEmpty(jsonStr)) {
                            handler.sendEmptyMessage(0x0037);
                        } else {
                            List<PdaTaskResultResponse> list = new Gson().fromJson(jsonStr, new TypeToken<List<PdaTaskResultResponse>>() {
                            }.getType());
                            PdaTaskResultResponse pdaTaskResultResponse = list.get(0);
                            if (pdaTaskResultResponse.isResult()) {
                                handler.sendEmptyMessage(0x0038);
                            } else {
                                handler.sendEmptyMessage(0x0039);
                            }
                        }

                    }
                }).start();
                break;
            case R.id.photo_back:
                getActivity().finish();
                break;
        }
    }

    private void checkFTP() {
        File file1 = new File(createPath() + File.separator + pdaTaskList.getBusinessid() + "_ZMZP_01.jpg");
        File file2 = new File(createPath() + File.separator + pdaTaskList.getBusinessid() + "_WBZP_02.jpg");
        File file3 = new File(createPath() + File.separator + pdaTaskList.getBusinessid() + "_PQGZP_03.jpg");
        File file4 = new File(createPath() + File.separator + pdaTaskList.getBusinessid() + "_VINZP_04.jpg");

        if (file1.exists()) {
            try {
                boolean b = FtpUtil.searchFile("10.111.102.26", 21, "pda", "pda", pdaTaskList.getBusinessid() + "_ZMZP_01.jpg", preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/");
                if (!b) {
                    File file = file1;
                    FileInputStream in = new FileInputStream(file);
                    boolean b1 = FtpUtil.uploadFile("10.111.102.26", 21, "pda", "pda", preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/", file.getName(), in);
                    in.close();
                    if (!b1) {
                        handler.sendEmptyMessage(0x0040);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file2.exists()) {
            try {
                boolean b = FtpUtil.searchFile("10.111.102.26", 21, "pda", "pda", pdaTaskList.getBusinessid() + "_WBZP_02.jpg", preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/");
                if (!b) {
                    File file = file2;
                    FileInputStream in = new FileInputStream(file);
                    boolean b1 = FtpUtil.uploadFile("10.111.102.26", 21, "pda", "pda", preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/", file.getName(), in);
                    in.close();
                    if (!b1) {
                        handler.sendEmptyMessage(0x0041);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file3.exists()) {
            try {
                boolean b = FtpUtil.searchFile("10.111.102.26", 21, "pda", "pda", pdaTaskList.getBusinessid() + "_PQGZP_03.jpg", preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/");
                if (!b) {
                    File file = file3;
                    FileInputStream in = new FileInputStream(file);
                    boolean b1 = FtpUtil.uploadFile("10.111.102.26", 21, "pda", "pda", preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/", file.getName(), in);
                    in.close();
                    if (!b1) {
                        handler.sendEmptyMessage(0x0042);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file4.exists()) {
            try {
                boolean b = FtpUtil.searchFile("10.111.102.26", 21, "pda", "pda", pdaTaskList.getBusinessid() + "_VINZP_04.jpg", preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/");
                if (!b) {
                    File file = file4;
                    FileInputStream in = new FileInputStream(file);
                    boolean b1 = FtpUtil.uploadFile("10.111.102.26", 21, "pda", "pda", preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/", file.getName(), in);
                    in.close();
                    if (!b1) {
                        handler.sendEmptyMessage(0x0043);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String createPath() {
        //preferences.getString("jczbh", "")
        String dir = preferences.getString("jczbh", "") + "/" + TimeUtils.getYear() + "/" + TimeUtils.getMonth() + "/" + TimeUtils.getDay();
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), dir);
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String path = mediaStorageDir.getPath();
        return path;
    }

}
