package com.example.administrator.envsystem.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.administrator.envsystem.R;
import com.example.administrator.envsystem.bean.PdaTaskList;
import com.example.administrator.envsystem.bean.PdaTaskResult;
import com.example.administrator.envsystem.ui.MyRadioGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/18.
 */

public class MyExpandableListViewAdapter extends BaseExpandableListAdapter implements View.OnClickListener {

    private static final int TYPE_V01 = 0;//燃油类别
    private static final int TYPE_V02 = 1;//驱动方式
    private static final int TYPE_V04 = 2;//仪表
    private static final int TYPE_V05 = 4;//输入项目
    private static final int PHOTOIDCHECK = 5;//拍照审核项目
    private Context context;
    private PdaTaskResult.EnvBean envBean;
    private List<PdaTaskList.Env> env01, env04, envPhotoIDCheck;
    private List<String> titles, checkBoxKey, radioKey;
    private HashMap<String, Boolean> checkBox, radio, v04, photoCheck;
    private HashMap<String,String> sqlMap;
    private List<HashMap<String, Boolean>> ybRadio, pzRadio;
    TextWatcher fdjTW, lcbTW;
    String fdj, lcb;

    public MyExpandableListViewAdapter(Context context) {
        this.context = context;
        checkBox = new HashMap<>();
        radio = new HashMap<>();
        v04 = new HashMap<>();
        photoCheck = new HashMap<>();
        checkBoxKey = new ArrayList<>();
        radioKey = new ArrayList<>();
        ybRadio = new ArrayList<>();
        pzRadio = new ArrayList<>();
        fdj = "";
        lcb = "";
        sqlMap=new HashMap<>();
    }

    public void setData(List<PdaTaskList.Env> envs, HashMap<String, Boolean> exlvData, HashMap<String,String> etData) {
        titles = new ArrayList<>();
        env01 = new ArrayList<>();
        env04 = new ArrayList<>();//仪表list
        envPhotoIDCheck = new ArrayList<>();//拍照审核项目list
        String v0501 = etData.get("V0501");
        String v0502 = etData.get("V0502");
        if (!TextUtils.isEmpty(v0501)) fdj = v0501;
        if (!TextUtils.isEmpty(v0502)) lcb = v0502;
        for (int i = 0; i < envs.size(); i++) {
            PdaTaskList.Env env = envs.get(i);
            //检验项目标签
            if (env.getVisCode().equals("V01") || env.getVisCode().equals("V02") || env.getVisCode().equals("V04")
                    || env.getVisCode().equals("PhotoIDCheck") || env.getVisCode().equals("V05")) {
                titles.add(env.getVisName());//父分组的标题
            }
            if (env.getVisPCode().equals("V01")) {//设置CheckBox默认值
//                env01.add(env);
                checkBoxKey.add(env.getVisCode());//key : V0101
                checkBox.put(env.getVisCode(), exlvData.get(env.getVisCode()));//默认值都为false
            }
            if (env.getVisPCode().equals("V02")) {//设置RadioButton默认值
                radioKey.add(env.getVisCode());//key : V0201
                radio.put(env.getVisCode(), exlvData.get(env.getVisCode()));//默认值都为false
            }
            if (env.getVisPCode().equals("V04")) {
                env04.add(env);
                v04.put(env.getVisCode(), exlvData.get(env.getVisCode()));//默认值都为true
            }
            if (env.getVisPCode().equals("PhotoIDCheck")) {
                envPhotoIDCheck.add(env);
                photoCheck.put(env.getVisCode(), exlvData.get(env.getVisCode()));//默认值都为true
            }
        }
        for (int i = 0; i < env04.size(); i++) {//设置仪表默认值
            HashMap<String, Boolean> map = new HashMap<>();
            map.put(env04.get(i).getVisCode(), exlvData.get(env04.get(i).getVisCode()));
            ybRadio.add(map);//key : V0401
        }
        for (int i = 0; i < envPhotoIDCheck.size(); i++) {
            HashMap<String, Boolean> map = new HashMap<>();
            map.put(envPhotoIDCheck.get(i).getVisCode(), exlvData.get(envPhotoIDCheck.get(i).getVisCode()));
            pzRadio.add(map);//key : C01
        }
    }

    /**
     * 返回结果
     */
    public List<PdaTaskResult.EnvBean> getDatas() {
        List<PdaTaskResult.EnvBean> list = new ArrayList<>();
        List<PdaTaskResult.EnvBean> v02list = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : checkBox.entrySet()) {
            envBean = new PdaTaskResult.EnvBean();
            envBean.setVisCode(entry.getKey());
            envBean.setVisValue(entry.getValue() ? "1" : "0");
            list.add(envBean);
            sqlMap.put(entry.getKey(),entry.getValue() ? "1" : "0");
        }
        for (Map.Entry<String, Boolean> entry : radio.entrySet()) {
            if (entry.getValue()) {
                //如果key的值为true才需要
                envBean = new PdaTaskResult.EnvBean();
                envBean.setVisCode(entry.getKey());
                envBean.setVisValue("1");
                list.add(envBean);
                sqlMap.put(entry.getKey(),"1");
            }else {
                //把其他4项保存在另一个地方
//                PdaTaskResult.EnvBean v02=new PdaTaskResult.EnvBean();
//                v02.setVisCode(entry.getKey());
//                v02.setVisValue("0");
//                v02list.add(v02);
                sqlMap.put(entry.getKey(),"0");
            }
        }
        for (Map.Entry<String, Boolean> entry : v04.entrySet()) {
            envBean = new PdaTaskResult.EnvBean();
            envBean.setVisCode(entry.getKey());
            envBean.setVisValue(entry.getValue() ? "1" : "0");
            list.add(envBean);
            sqlMap.put(entry.getKey(),entry.getValue() ? "1" : "0");
        }
        for (Map.Entry<String, Boolean> entry : photoCheck.entrySet()) {
            envBean = new PdaTaskResult.EnvBean();
            envBean.setVisCode(entry.getKey());
            envBean.setVisValue(entry.getValue() ? "1" : "0");
            list.add(envBean);
            sqlMap.put(entry.getKey(),entry.getValue() ? "1" : "0");
        }
        envBean = new PdaTaskResult.EnvBean();
        envBean.setVisCode("V0501");
        envBean.setVisValue(fdj);
        list.add(envBean);
        sqlMap.put("V0501",fdj);
        envBean = new PdaTaskResult.EnvBean();
        envBean.setVisCode("V0502");
        envBean.setVisValue(lcb);
        list.add(envBean);
        sqlMap.put("V0502",lcb);
//        StaticValues.v02=v02list;//V02其他4项
        return list;
    }

    public HashMap<String,String> getSqlMap(){
        return sqlMap;
    }

    @Override
    public void onClick(View v) {
        //复选框点击事件
        switch (v.getId()) {
            case R.id.ry_cb1:
                checkBox.put(checkBoxKey.get(0), ((CheckBox) v).isChecked());
                break;
            case R.id.ry_cb2:
                checkBox.put(checkBoxKey.get(1), ((CheckBox) v).isChecked());
                break;
            case R.id.ry_cb3:
                checkBox.put(checkBoxKey.get(2), ((CheckBox) v).isChecked());
                break;
            case R.id.ry_cb4:
                checkBox.put(checkBoxKey.get(3), ((CheckBox) v).isChecked());
                break;
        }
    }

    static class ExpandableListHolder {
        TextView titleName;
    }

    public int getItemViewType(int groupPosition) {
        int p = groupPosition;
        if (p == 0) {
            return TYPE_V01;
        } else if (p == 1) {
            return TYPE_V02;
        } else if (p == 2) {
            return TYPE_V04;
        } else if (p == 3) {
            return TYPE_V05;
        } else {
            return PHOTOIDCHECK;
        }
    }


    @Override
    public int getGroupCount() {
        return 5;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == 2) {
            return env04.size();
        } else if (groupPosition == 4) {
            return envPhotoIDCheck.size();
        }
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//        PdaTaskList.Env env = envs.get(groupPosition);

        ExpandableListHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_group_view, null);
            holder = new ExpandableListHolder();
            holder.titleName = (TextView) convertView.findViewById(R.id.txt_title);
            convertView.setTag(holder);
        } else {
            holder = (ExpandableListHolder) convertView.getTag();
        }
        holder.titleName.setText(titles.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        int type = getItemViewType(groupPosition);
        switch (type) {
            case TYPE_V01:
                convertView = View.inflate(context, R.layout.item_child_rylb, null);
                CheckBox cb1 = (CheckBox) convertView.findViewById(R.id.ry_cb1);
                CheckBox cb2 = (CheckBox) convertView.findViewById(R.id.ry_cb2);
                CheckBox cb3 = (CheckBox) convertView.findViewById(R.id.ry_cb3);
                CheckBox cb4 = (CheckBox) convertView.findViewById(R.id.ry_cb4);
                cb1.setOnClickListener(this);
                cb2.setOnClickListener(this);
                cb3.setOnClickListener(this);
                cb4.setOnClickListener(this);
                //获取缓存的值
                cb1.setChecked(checkBox.get(checkBoxKey.get(0)));
                cb2.setChecked(checkBox.get(checkBoxKey.get(1)));
                cb3.setChecked(checkBox.get(checkBoxKey.get(2)));
                cb4.setChecked(checkBox.get(checkBoxKey.get(3)));
                break;
            case TYPE_V02:
                convertView = View.inflate(context, R.layout.item_child_qdfs, null);
                final MyRadioGroup qdRg = (MyRadioGroup) convertView.findViewById(R.id.qd_rg);
                final RadioButton rb1 = (RadioButton) convertView.findViewById(R.id.qd_rb1);
                final RadioButton rb2 = (RadioButton) convertView.findViewById(R.id.qd_rb2);
                final RadioButton rb3 = (RadioButton) convertView.findViewById(R.id.qd_rb3);
                final RadioButton rb4 = (RadioButton) convertView.findViewById(R.id.qd_rb4);
                final RadioButton rb5 = (RadioButton) convertView.findViewById(R.id.qd_rb5);
                //获取缓存的值
                rb1.setChecked(radio.get(radioKey.get(0)));
                rb2.setChecked(radio.get(radioKey.get(1)));
                rb3.setChecked(radio.get(radioKey.get(2)));
                rb4.setChecked(radio.get(radioKey.get(3)));
                rb5.setChecked(radio.get(radioKey.get(4)));
                qdRg.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.qd_rb1:
                                clearRadio();//清除之前的缓存
                                radio.put(radioKey.get(0), true);
//                                ToastUtils.showToast(context,rb1.getText().toString());
                                break;
                            case R.id.qd_rb2:
                                clearRadio();
                                radio.put(radioKey.get(1), true);
//                                ToastUtils.showToast(context,rb2.getText().toString());
                                break;
                            case R.id.qd_rb3:
                                clearRadio();
                                radio.put(radioKey.get(2), true);
//                                ToastUtils.showToast(context,rb3.getText().toString());
                                break;
                            case R.id.qd_rb4:
                                clearRadio();
                                radio.put(radioKey.get(3), true);
//                                ToastUtils.showToast(context,rb4.getText().toString());
                                break;
                            case R.id.qd_rb5:
                                clearRadio();
                                radio.put(radioKey.get(4), true);
//                                ToastUtils.showToast(context,rb5.getText().toString());
                                break;
                        }
                    }
                });
                break;
            case TYPE_V04:
                final PdaTaskList.Env env = env04.get(childPosition);
                final HashMap<String, Boolean> booleanHashMap = ybRadio.get(childPosition);
                convertView = View.inflate(context, R.layout.item_child_yb, null);
                TextView tv = (TextView) convertView.findViewById(R.id.tv_lcb);
                MyRadioGroup ybMrg = (MyRadioGroup) convertView.findViewById(R.id.myrg_lcb);
                final RadioButton ybPass = (RadioButton) convertView.findViewById(R.id.yb_pass);
                final RadioButton ybFail = (RadioButton) convertView.findViewById(R.id.yb_fail);
                ybPass.setChecked(booleanHashMap.get(env.getVisCode()));
                ybFail.setChecked(!booleanHashMap.get(env.getVisCode()));
//                v04.put(env.getVisCode(), "不合格");
                ybMrg.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.yb_pass://合格
                                booleanHashMap.put(env.getVisCode(), true);
                                v04.put(env.getVisCode(), true);
                                break;
                            case R.id.yb_fail://不合格
                                booleanHashMap.put(env.getVisCode(), false);
                                v04.put(env.getVisCode(), false);
                        }
                    }
                });
//                LinearLayout ybLl= (LinearLayout) convertView.findViewById(R.id.ll_yb);
//                addRadioGroup(ybLl);
                tv.setText(env.getVisName());
                break;
            case TYPE_V05://输入项目
                convertView = View.inflate(context, R.layout.item_child_sr, null);
                EditText fdjEt = (EditText) convertView.findViewById(R.id.et_fdjpl);
                EditText lcbEt = (EditText) convertView.findViewById(R.id.et_lcbs);
                fdjTW = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        fdj = s.toString();
                    }
                };
                lcbTW = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        lcb = s.toString();
                    }
                };
                fdjEt.addTextChangedListener(fdjTW);
                lcbEt.addTextChangedListener(lcbTW);
                fdjEt.setText(fdj);
                lcbEt.setText(lcb);
                break;
            case PHOTOIDCHECK://拍照审核项目
                convertView = View.inflate(context, R.layout.item_child_pz, null);
                final PdaTaskList.Env envPhoto = envPhotoIDCheck.get(childPosition);
                final HashMap<String, Boolean> booleanMap = pzRadio.get(childPosition);
                TextView pzTv = (TextView) convertView.findViewById(R.id.tv_photocheck);
                MyRadioGroup pzMrg = (MyRadioGroup) convertView.findViewById(R.id.mrg_photocheck);
                final RadioButton pzPass = (RadioButton) convertView.findViewById(R.id.pz_pass);
                final RadioButton pzFail = (RadioButton) convertView.findViewById(R.id.pz_fail);
                pzTv.setText(envPhoto.getVisName());
                pzPass.setChecked(booleanMap.get(envPhoto.getVisCode()));
                pzFail.setChecked(!booleanMap.get(envPhoto.getVisCode()));
//                photoCheck.put(envPhoto.getVisCode(), "不合格");
                pzMrg.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.pz_pass://合格
                                booleanMap.put(envPhoto.getVisCode(), true);
                                photoCheck.put(envPhoto.getVisCode(), true);
                                break;
                            case R.id.pz_fail://不合格
                                booleanMap.put(envPhoto.getVisCode(), false);
                                photoCheck.put(envPhoto.getVisCode(), false);
                        }
                    }
                });
                break;
        }
        return convertView;
    }

    private void addRadioGroup(LinearLayout ybLl) {
        TextView tv = new TextView(context);
        tv.setTextSize(18);
        tv.setText("test");
        RadioGroup rg = new RadioGroup(context);
        LinearLayout.LayoutParams rgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rg.setOrientation(LinearLayout.HORIZONTAL);
        rg.setLayoutParams(rgParams);
        RadioButton rb11 = new RadioButton(context);
        RadioButton rb12 = new RadioButton(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        rb11.setLayoutParams(params);
        rb12.setLayoutParams(params);
        rb11.setText("合格");
        rb12.setText("不合格");
        rg.addView(rb11);
        rg.addView(rb12);
        ybLl.addView(tv);
        ybLl.addView(rg);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void clearRadio() {
        for (int i = 0; i < radio.size(); i++) {
            radio.put(radioKey.get(i), false);
        }
    }

}
