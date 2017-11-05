package com.example.administrator.envsystem.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.administrator.envsystem.R;

/**
 * Created by Administrator on 2017/8/12.
 */

public class PhotoShot extends Fragment {

    private LinearLayout photo_imageview;
    private String[] zpData={"正面照片","尾部照片","排气管装置","车辆VIN码"};
    private String[] isData={"","","",""};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.photo_shot, null);
        photo_imageview = (LinearLayout) view.findViewById(R.id.photo_imageview);
        return view;
    }
}
