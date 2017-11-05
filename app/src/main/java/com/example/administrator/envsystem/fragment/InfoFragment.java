package com.example.administrator.envsystem.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.envsystem.R;
import com.example.administrator.envsystem.activity.DetailActivity;
import com.example.administrator.envsystem.bean.PdaTaskList;

/**
 * Created by Administrator on 2017/4/14.
 */

public class InfoFragment extends Fragment {

    private PdaTaskList pdaTaskList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, null);
        TextView cp= (TextView) view.findViewById(R.id.tv_cp);
        TextView color= (TextView) view.findViewById(R.id.tv_cpys);
        TextView vin= (TextView) view.findViewById(R.id.tv_cpvin);
        TextView fuel= (TextView) view.findViewById(R.id.tv_fuel);
        TextView lsh= (TextView) view.findViewById(R.id.tv_lsh);
        TextView querition_title= (TextView) view.findViewById(R.id.querition_title);
        querition_title.setText(pdaTaskList.getCarcardnumber());
        lsh.setText("流水号："+pdaTaskList.getBusinessid());
        cp.setText("车牌号码："+pdaTaskList.getCarcardnumber());
        color.setText("车牌颜色："+pdaTaskList.getCarcardcolor());
        vin.setText("车辆VIN号："+pdaTaskList.getVin());
        fuel.setText("燃料类型："+pdaTaskList.getFuelname());
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        pdaTaskList = ((DetailActivity) activity).getPdaTaskList();
    }
}
