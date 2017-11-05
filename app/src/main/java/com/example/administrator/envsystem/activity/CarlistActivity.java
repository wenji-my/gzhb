package com.example.administrator.envsystem.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.administrator.envsystem.R;
import com.example.administrator.envsystem.adapter.PdaTaskAdapter;
import com.example.administrator.envsystem.bean.PdaTaskList;
import com.example.administrator.envsystem.conf.MessageWhat;
import com.example.administrator.envsystem.conf.StaticValues;
import com.example.administrator.envsystem.utils.DialogTool;
import com.example.administrator.envsystem.utils.HandleUtils;
import com.example.administrator.envsystem.utils.WebServiceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

import static com.example.administrator.envsystem.conf.MessageWhat.MSG_DISMISS;

public class CarlistActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int TASKLIST = 100;
    private static final int TASKLISTBYCAR = 101;
    private ImageButton back;
    private EditText carEt;
    private ListView carLv;
    private String token;//令牌号
    private HandleUtils handleUtils = new HandleUtils(this);
    private PdaTaskAdapter listAdapter;
    private List<PdaTaskList> pdaTaskLists;
    private Spinner wkxzxm;
    private String[] wkxzxmsm = "粤,浙,苏,沪,皖,闽,晋,豫,赣,川,陕,湘,云,贵,桂,京,津,渝,黑,吉,辽,蒙,冀,新,甘,青,宁,鲁,鄂,黔,滇,藏,台,琼,港,澳".split(",");
    private Button sxBtn;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TASKLIST:
                    if (listAdapter == null) {
                        listAdapter = new PdaTaskAdapter(CarlistActivity.this);
                        listAdapter.setData((List<PdaTaskList>) msg.obj);
                        carLv.setAdapter(listAdapter);
                    } else {
                        listAdapter.setData((List<PdaTaskList>) msg.obj);
                        listAdapter.notifyDataSetChanged();
                    }
                    break;
                case TASKLISTBYCAR:
                    if (listAdapter == null) {
                        listAdapter = new PdaTaskAdapter(CarlistActivity.this);
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
    private PdaTaskList pdaTaskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carlist);
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        carLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pdaTaskList = pdaTaskLists.get(position);
                if (ContextCompat.checkSelfPermission(CarlistActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CarlistActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else {
                    Intent intent = new Intent(CarlistActivity.this, DetailActivity.class);
                    intent.putExtra("pdaTaskList", pdaTaskList);
                    intent.putExtra("businessid",pdaTaskLists.get(position).getBusinessid());
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(CarlistActivity.this, DetailActivity.class);
                    intent.putExtra("pdaTaskList", pdaTaskList);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    private void initData() {
//        SharedPreferences preferences = getSharedPreferences("set", 0);
//        String jczbh = preferences.getString("jczbh", "");
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        StaticValues.token=token;
    }

    private void initView() {
        back= (ImageButton) findViewById(R.id.ib_back);
        sxBtn=(Button)findViewById(R.id.sx);
        back.setOnClickListener(this);
        sxBtn.setOnClickListener(this);
        carEt = (EditText) findViewById(R.id.et_number);
        carLv = (ListView) findViewById(R.id.lv_car);
        wkxzxm=(Spinner)findViewById(R.id.sp_yue);
        wkxzxm.setAdapter(SetSpinner(wkxzxmsm));
        wkxzxm.setSelection(0);
        carEt.setSelection(1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sx:
                if (carEt.getText().length()>1){
                    //根据车牌号码查询
                    String s = wkxzxm.getSelectedItem().toString();
                    final String carNumber = s+carEt.getText().toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            handleUtils.sendEmptyMessage(MessageWhat.MSG_SEARCH);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("arg0", token);
                            map.put("arg1", carNumber);
                            Log.i("TAG","车牌"+carNumber);
                            String jsonStr = WebServiceUtils.connectWebService(StaticValues.ip, StaticValues.getPdaTaskByCarNumber, map);
                            if (TextUtils.isEmpty(jsonStr)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        handleUtils.sendEmptyMessage(MSG_DISMISS);
                                        DialogTool.AlertDialogShow(CarlistActivity.this,"服务器出错了！");
                                    }
                                });
                                return;
                            }
                            pdaTaskLists = new Gson().fromJson(jsonStr, new TypeToken<List<PdaTaskList>>() {
                            }.getType());
//                            pda = pdaTaskLists.get(0);
                            //更新UI
                            handleUtils.sendEmptyMessage(MSG_DISMISS);
                            Message msg = Message.obtain();
                            msg.what = TASKLISTBYCAR;
                            msg.obj = pdaTaskLists;
                            mHandler.sendMessage(msg);
                        }
                    }).start();
                }else {
                    //查询全部
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            handleUtils.sendEmptyMessage(MessageWhat.MSG_SEARCH);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("arg0", token);
                        String jsonStr = WebServiceUtils.connectWebService(StaticValues.ip, StaticValues.getPdaTaskList, map);
//                            String jsonStr="[ { \"businessid\": \"1492411867940_ZXY\", \"carcardnumber\": \"粤Y651G6\", \"carcardcolor\": \"蓝色\", \"vin\": \"LZWACAGA8FC042498\", \"opt\": \"add\", \"fueltype\": \"A\", \"fuelname\": \"汽油\", \"phototype\": [ { \"typecode\": \"01\", \"typename\": \"正面照片\" }, { \"typecode\": \"02\", \"typename\": \"尾部照片\" }, { \"typecode\": \"03\", \"typename\": \"排气管装置\" }, { \"typecode\": \"04\", \"typename\": \"VIN拍照\" } ], \"env\": [ { \"visCode\": \"V01\", \"visName\": \"燃油类别(汽油)\", \"visPCode\": \"00\", \"visType\": \"Lable\" }, { \"visCode\": \"V0101\", \"visName\": \"1:闭环控制\", \"visPCode\": \"V01\", \"visType\": \"CheckBox\" }, { \"visCode\": \"V0102\", \"visName\": \"2:开环控制\", \"visPCode\": \"V01\", \"visType\": \"CheckBox\" }, { \"visCode\": \"V0103\", \"visName\": \"3:三元催化剂\", \"visPCode\": \"V01\", \"visType\": \"CheckBox\" }, { \"visCode\": \"V0104\", \"visName\": \"4:化油器\", \"visPCode\": \"V01\", \"visType\": \"CheckBox\" }, { \"visCode\": \"V02\", \"visName\": \"驱动方式\", \"visPCode\": \"00\", \"visType\": \"Lable\" }, { \"visCode\": \"V0201\", \"visName\": \"1:前轮驱动\", \"visPCode\": \"V02\", \"visType\": \"Radio\" }, { \"visCode\": \"V0202\", \"visName\": \"2:后轮驱动\", \"visPCode\": \"V02\", \"visType\": \"Radio\" }, { \"visCode\": \"V0203\", \"visName\": \"3:全时四驱\", \"visPCode\": \"V02\", \"visType\": \"Radio\" }, { \"visCode\": \"V0204\", \"visName\": \"4:分时四驱\", \"visPCode\": \"V02\", \"visType\": \"Radio\" }, { \"visCode\": \"V0205\", \"visName\": \"5:适时四驱\", \"visPCode\": \"V02\", \"visType\": \"Radio\" }, { \"visCode\": \"V04\", \"visName\": \"仪表\", \"visPCode\": \"00\", \"visType\": \"Lable\" }, { \"visCode\": \"V0401\", \"visName\": \"1.里程表是否正常\", \"visPCode\": \"V04\", \"visType\": \"RadioPassAndFail\" }, { \"visCode\": \"V0402\", \"visName\": \"2.机油压力无偏低\", \"visPCode\": \"V04\", \"visType\": \"RadioPassAndFail\" }, { \"visCode\": \"V0403\", \"visName\": \"3.冷却液温度表正常\", \"visPCode\": \"V04\", \"visType\": \"RadioPassAndFail\" }, { \"visCode\": \"V05\", \"visName\": \"输入项目\", \"visPCode\": \"00\", \"visType\": \"Lable\" }, { \"visCode\": \"V0501\", \"visName\": \"1.发动机排量\", \"visPCode\": \"V05\", \"visType\": \"TextBox\" }, { \"visCode\": \"V0502\", \"visName\": \"2.里程表\", \"visPCode\": \"V05\", \"visType\": \"TextBox\" }, { \"visCode\": \"PhotoIDCheck\", \"visName\": \"拍照审核项目\", \"visPCode\": \"00\", \"visType\": \"Lable\" }, { \"visCode\": \"C01\", \"visName\": \"车头,车尾,车底照片\", \"visPCode\": \"PhotoIDCheck\", \"visType\": \"RadioPassAndFail\" }, { \"visCode\": \"C02\", \"visName\": \"VIN照片\", \"visPCode\": \"PhotoIDCheck\", \"visType\": \"RadioPassAndFail\" } ] } ]";
                            pdaTaskLists = new Gson().fromJson(jsonStr, new TypeToken<List<PdaTaskList>>() {
                            }.getType());
                            //更新UI
                            handleUtils.sendEmptyMessage(MSG_DISMISS);
                            refreshUI(pdaTaskLists);

                        }
                    }).start();
                }
                break;
            case R.id.ib_back:
                finish();
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
//                            String jsonStr="[ { \"businessid\": \"1492411867940_ZXY\", \"carcardnumber\": \"粤Y651G6\", \"carcardcolor\": \"蓝色\", \"vin\": \"LZWACAGA8FC042498\", \"opt\": \"add\", \"fueltype\": \"A\", \"fuelname\": \"汽油\", \"phototype\": [ { \"typecode\": \"01\", \"typename\": \"正面照片\" }, { \"typecode\": \"02\", \"typename\": \"尾部照片\" }, { \"typecode\": \"03\", \"typename\": \"排气管装置\" }, { \"typecode\": \"04\", \"typename\": \"VIN拍照\" } ], \"env\": [ { \"visCode\": \"V01\", \"visName\": \"燃油类别(汽油)\", \"visPCode\": \"00\", \"visType\": \"Lable\" }, { \"visCode\": \"V0101\", \"visName\": \"1:闭环控制\", \"visPCode\": \"V01\", \"visType\": \"CheckBox\" }, { \"visCode\": \"V0102\", \"visName\": \"2:开环控制\", \"visPCode\": \"V01\", \"visType\": \"CheckBox\" }, { \"visCode\": \"V0103\", \"visName\": \"3:三元催化剂\", \"visPCode\": \"V01\", \"visType\": \"CheckBox\" }, { \"visCode\": \"V0104\", \"visName\": \"4:化油器\", \"visPCode\": \"V01\", \"visType\": \"CheckBox\" }, { \"visCode\": \"V02\", \"visName\": \"驱动方式\", \"visPCode\": \"00\", \"visType\": \"Lable\" }, { \"visCode\": \"V0201\", \"visName\": \"1:前轮驱动\", \"visPCode\": \"V02\", \"visType\": \"Radio\" }, { \"visCode\": \"V0202\", \"visName\": \"2:后轮驱动\", \"visPCode\": \"V02\", \"visType\": \"Radio\" }, { \"visCode\": \"V0203\", \"visName\": \"3:全时四驱\", \"visPCode\": \"V02\", \"visType\": \"Radio\" }, { \"visCode\": \"V0204\", \"visName\": \"4:分时四驱\", \"visPCode\": \"V02\", \"visType\": \"Radio\" }, { \"visCode\": \"V0205\", \"visName\": \"5:适时四驱\", \"visPCode\": \"V02\", \"visType\": \"Radio\" }, { \"visCode\": \"V04\", \"visName\": \"仪表\", \"visPCode\": \"00\", \"visType\": \"Lable\" }, { \"visCode\": \"V0401\", \"visName\": \"1.里程表是否正常\", \"visPCode\": \"V04\", \"visType\": \"RadioPassAndFail\" }, { \"visCode\": \"V0402\", \"visName\": \"2.机油压力无偏低\", \"visPCode\": \"V04\", \"visType\": \"RadioPassAndFail\" }, { \"visCode\": \"V0403\", \"visName\": \"3.冷却液温度表正常\", \"visPCode\": \"V04\", \"visType\": \"RadioPassAndFail\" }, { \"visCode\": \"V05\", \"visName\": \"输入项目\", \"visPCode\": \"00\", \"visType\": \"Lable\" }, { \"visCode\": \"V0501\", \"visName\": \"1.发动机排量\", \"visPCode\": \"V05\", \"visType\": \"TextBox\" }, { \"visCode\": \"V0502\", \"visName\": \"2.里程表\", \"visPCode\": \"V05\", \"visType\": \"TextBox\" }, { \"visCode\": \"PhotoIDCheck\", \"visName\": \"拍照审核项目\", \"visPCode\": \"00\", \"visType\": \"Lable\" }, { \"visCode\": \"C01\", \"visName\": \"车头,车尾,车底照片\", \"visPCode\": \"PhotoIDCheck\", \"visType\": \"RadioPassAndFail\" }, { \"visCode\": \"C02\", \"visName\": \"VIN照片\", \"visPCode\": \"PhotoIDCheck\", \"visType\": \"RadioPassAndFail\" } ] } ]";
                pdaTaskLists = new Gson().fromJson(jsonStr, new TypeToken<List<PdaTaskList>>() {
                }.getType());
                //更新UI
                handleUtils.sendEmptyMessage(MSG_DISMISS);
                refreshUI(pdaTaskLists);

            }
        }).start();
    }

    private void refreshUI(List<PdaTaskList> pdaTaskLists) {
        Message msg = Message.obtain();
        msg.what = TASKLIST;
        msg.obj = pdaTaskLists;
        mHandler.sendMessage(msg);
    }

    public ArrayAdapter<String> SetSpinner(String[] spidata) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item, spidata);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}
