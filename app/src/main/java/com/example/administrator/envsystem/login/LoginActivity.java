package com.example.administrator.envsystem.login;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.envsystem.MainActivity;
import com.example.administrator.envsystem.R;
import com.example.administrator.envsystem.bean.Token;
import com.example.administrator.envsystem.conf.StaticValues;
import com.example.administrator.envsystem.utils.DialogTool;
import com.example.administrator.envsystem.utils.HandleUtils;
import com.example.administrator.envsystem.utils.WebServiceUtils;
import com.google.gson.Gson;

import java.util.HashMap;

import static com.example.administrator.envsystem.conf.MessageWhat.MSG_CUOWU;
import static com.example.administrator.envsystem.conf.MessageWhat.MSG_DISMISS;
import static com.example.administrator.envsystem.conf.MessageWhat.MSG_SHOW;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nameEt;
    private EditText pwdEt;
    private Button loginBtn;
    private HandleUtils handleUtils=new HandleUtils(this);
    private SharedPreferences preferences;
    private Button setBtn;
    private Button exitBtn;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
        initEvent();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("TAG","授权成功");
                }
                break;
            default:
                break;
        }
    }

    private void initData() {
        preferences = getSharedPreferences("setting", 0);
        if (nameEt!=null&&pwdEt!=null){
            nameEt.setText(preferences.getString("jczbh",""));
            pwdEt.setText(preferences.getString("pwd",""));
        }
    }

    private void initEvent() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected()) {
                    String prompt = "WIFI未连接，请重新设置WIFI。。";
                    DialogTool.AlertDialogShow(LoginActivity.this, prompt);
                } else {
//                    String[] str={"0","1","1","1","1","5","1","1","1","123","345","1","1","1/2017/08","1/2017/08","1/2017/08","1/2017/08","2017-8-13"};
//                    SQLFuntion.insert(str);
                    final String name = nameEt.getText().toString();
                    final String pwd = pwdEt.getText().toString();
                    if (name.equals("")) {
                        Toast.makeText(LoginActivity.this, "编号不能为空", Toast.LENGTH_SHORT).show();
                    } else if (pwd.equals("")) {
                        Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("jczbh",name);
                        editor.putString("pwd",pwd);
                        editor.commit();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handleUtils.sendEmptyMessage(MSG_SHOW);
                                //调用接口
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("arg0", name);
                                map.put("arg1", pwd);
                                String jsonStr = WebServiceUtils.connectWebService(StaticValues.ip, StaticValues.getToken, map);
                                //离线测试
//                                String jsonStr="{\"msg\":\"校验成功\",\"isSuc\":\"1\",\"token\":\"9dcec63c-af08-40d5-ba9f-af0edce374e2\"}";
//                                Log.i("TAG",jsonStr);
                                if (TextUtils.isEmpty(jsonStr)){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            handleUtils.sendEmptyMessage(MSG_DISMISS);
                                            DialogTool.AlertDialogShow(LoginActivity.this,"网络异常，请检查网络！");
                                        }
                                    });
                                    return;
                                }
                                //解析json，获取令牌号
                                Token token = new Gson().fromJson(jsonStr, Token.class);
                                if (token.getIsSuc().equals("1")) {
                                    //是否生成成功： 1 是， 0 否
                                    handleUtils.sendEmptyMessage(MSG_DISMISS);
                                    Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("token",token.getToken());
                                    startActivity(intent);
                                    LoginActivity.this.finish();
                                } else if (token.getIsSuc().equals("0")) {
                                    handleUtils.sendEmptyMessage(MSG_DISMISS);
                                    //弹出错误信息
                                    Message msg = Message.obtain();
                                    msg.what = MSG_CUOWU;
                                    msg.obj = token.getMsg();
                                    handleUtils.sendMessage(msg);
                                }
                            }
                        }).start();

                    }
                }
            }
        });
    }

    /**
     * 判断是否连接WiFi
     * @return
     */
    public boolean isConnected() {
        try {
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            boolean isconnect = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
            return isconnect;
        } catch (Exception e) {
            return false;
        }
    }

    private void initView() {
        nameEt = (EditText) findViewById(R.id.login_name);
        pwdEt = (EditText) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.login_login);
        exitBtn = (Button) findViewById(R.id.login_exit);
        exitBtn.setOnClickListener(this);
        setBtn = (Button) findViewById(R.id.login_forget);
        setBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_exit:
                finish();
                break;
            case R.id.login_forget:
                Intent intent=new Intent(this,SetActivity.class);
                startActivity(intent);
                break;
        }
    }
}
