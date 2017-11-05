package com.example.administrator.envsystem.login;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.administrator.envsystem.R;

public class SetActivity extends AppCompatActivity {

    private EditText et_jgmc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameter_setting);
        et_jgmc = (EditText) this.findViewById(R.id.et_jgmc);
        SharedPreferences get_preferences = getSharedPreferences("set", 0);
        et_jgmc.setText(get_preferences.getString("jgmc", ""));
    }

    /**
     * 按钮点击事件
     */
    public void doClick(View v) {
        switch (v.getId()) {
            // 返回
            case R.id.setting_back:
                SetActivity.this.finish();
                break;
            // 初始化设置
            case R.id.but_setting:
                String ip_data = et_jgmc.getText().toString().trim();
                SharedPreferences set_preferences = getSharedPreferences("set", 0);
                SharedPreferences.Editor editor = set_preferences.edit();
                editor.putString("jgmc", ip_data);
                editor.commit();
                this.finish();
                break;
        }
    }
}
