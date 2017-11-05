package com.example.administrator.envsystem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.administrator.envsystem.activity.DetailActivity;
import com.example.administrator.envsystem.adapter.PdaTaskAdapter;
import com.example.administrator.envsystem.bean.PdaTaskList;
import com.example.administrator.envsystem.conf.MessageWhat;
import com.example.administrator.envsystem.conf.StaticValues;
import com.example.administrator.envsystem.unknown.AutoCaseTransformationMethod;
import com.example.administrator.envsystem.utils.DialogTool;
import com.example.administrator.envsystem.utils.HandleUtils;
import com.example.administrator.envsystem.utils.WebServiceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

import static com.example.administrator.envsystem.conf.MessageWhat.MSG_DISMISS;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int TASKLIST = 100;
    private static final int TASKLISTBYCAR = 101;
    private ImageButton back;
    private EditText carEt;
    private ListView carLv;
    private String token;//令牌号
    private HandleUtils handleUtils = new HandleUtils(this);
    private PdaTaskAdapter listAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TASKLIST:
                    if (listAdapter == null) {
                        listAdapter = new PdaTaskAdapter(MainActivity.this);
                        listAdapter.setData((List<PdaTaskList>) msg.obj);
                        carLv.setAdapter(listAdapter);
                    } else {
                        listAdapter.setData((List<PdaTaskList>) msg.obj);
                        listAdapter.notifyDataSetChanged();
                    }
                    break;
                case TASKLISTBYCAR:
                    if (listAdapter == null) {
                        listAdapter = new PdaTaskAdapter(MainActivity.this);
                        listAdapter.setData((List<PdaTaskList>) msg.obj);
                        carLv.setAdapter(listAdapter);
                    } else {
                        listAdapter.setData((List<PdaTaskList>) msg.obj);
                        listAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };
    private List<PdaTaskList> pdaTaskLists;
    private Spinner wkxzxm;
    private String[] wkxzxmsm = "粤,浙,苏,沪,皖,闽,晋,豫,赣,川,陕,湘,云,贵,桂,京,津,渝,黑,吉,辽,蒙,冀,新,甘,青,宁,鲁,鄂,黔,滇,藏,台,琼,港,澳".split(",");
    private PdaTaskList pdaTaskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        carLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pdaTaskList = pdaTaskLists.get(position);
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("pdaTaskList", pdaTaskList);
                    startActivity(intent);
                }
            }
        });
        carEt.setTransformationMethod(new AutoCaseTransformationMethod());
        carEt.setSelection(1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("pdaTaskList", pdaTaskList);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //查询全部
        new Thread(new Runnable() {
            @Override
            public void run() {
                handleUtils.sendEmptyMessage(MessageWhat.MSG_SEARCH);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("arg0", token);
                String jsonStr = WebServiceUtils.connectWebService(StaticValues.ip, StaticValues.getPdaTaskList, map);
//                String jsonStr = Datas.main;
                if (TextUtils.isEmpty(jsonStr)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleUtils.sendEmptyMessage(MSG_DISMISS);
                            DialogTool.AlertDialogShow(MainActivity.this, "服务器出错了！");
                        }
                    });
                    return;
                }
                pdaTaskLists = new Gson().fromJson(jsonStr, new TypeToken<List<PdaTaskList>>() {
                }.getType());
                //更新UI
                handleUtils.sendEmptyMessage(MSG_DISMISS);
                refreshUI(pdaTaskLists);

            }
        }).start();
    }

    private void initData() {
//        SharedPreferences preferences = getSharedPreferences("set", 0);
//        String jczbh = preferences.getString("jczbh", "");
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        StaticValues.token = token;
    }

    private void initView() {
        back = (ImageButton) findViewById(R.id.querition_back);
        back.setOnClickListener(this);
        carEt = (EditText) findViewById(R.id.querition_lsh);
        carLv = (ListView) findViewById(R.id.vehicle_list);
        wkxzxm = (Spinner) findViewById(R.id.wkxzxm);
        wkxzxm.setAdapter(SetSpinner(wkxzxmsm));
        wkxzxm.setSelection(0);
        carEt.setText("A");
    }

    public ArrayAdapter<String> SetSpinner(String[] spidata) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item, spidata);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.querition_scanning:
                //调用接口
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handleUtils.sendEmptyMessage(MessageWhat.MSG_SEARCH);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("arg0", token);
                        String jsonStr = WebServiceUtils.connectWebService(StaticValues.ip, StaticValues.getPdaTaskList, map);
//                        String jsonStr="";
                        pdaTaskLists = new Gson().fromJson(jsonStr, new TypeToken<List<PdaTaskList>>() {
                        }.getType());
                        //更新UI
                        handleUtils.sendEmptyMessage(MSG_DISMISS);
                        refreshUI(pdaTaskLists);

                    }
                }).start();
                break;
            case R.id.querition_query://查询单个
                String s = wkxzxm.getSelectedItem().toString();
                final String carNumber = s + carEt.getText().toString();
                final String s1 = carNumber.toUpperCase();
                if (!TextUtils.isEmpty(carNumber)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            handleUtils.sendEmptyMessage(MessageWhat.MSG_SEARCH);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("arg0", token);
                            map.put("arg1", s1);
                            Log.i("TAG", "车牌：" + s1);
                            String jsonStr = WebServiceUtils.connectWebService(StaticValues.ip, StaticValues.getPdaTaskByCarNumber, map);
                            if (TextUtils.isEmpty(jsonStr)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        handleUtils.sendEmptyMessage(MSG_DISMISS);
                                        DialogTool.AlertDialogShow(MainActivity.this, "服务器出错了！");
                                    }
                                });
                                return;
                            }
                            pdaTaskLists = new Gson().fromJson(jsonStr, new TypeToken<List<PdaTaskList>>() {
                            }.getType());
                            //更新UI
                            handleUtils.sendEmptyMessage(MSG_DISMISS);
                            Message msg = Message.obtain();
                            msg.what = TASKLISTBYCAR;
                            msg.obj = pdaTaskLists;
                            mHandler.sendMessage(msg);
                        }
                    }).start();
                }
                break;
        }
    }

    private void refreshUI(List<PdaTaskList> pdaTaskLists) {
        Message msg = Message.obtain();
        msg.what = TASKLIST;
        msg.obj = pdaTaskLists;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.querition_back:
                finish();
                break;
        }
    }
}
