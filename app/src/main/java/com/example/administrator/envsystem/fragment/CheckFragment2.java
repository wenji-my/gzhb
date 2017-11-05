package com.example.administrator.envsystem.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.administrator.envsystem.R;
import com.example.administrator.envsystem.adapter.CheckFragAdapter;

/**
 * Created by Administrator on 2017/9/13.
 */

public class CheckFragment2 extends Fragment {

    private ExpandableListView mExLv;
    private CheckFragAdapter myAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check2, null, false);
        mExLv = (ExpandableListView) view.findViewById(R.id.ex_list);
        myAdapter = new CheckFragAdapter(getActivity());
        return view;
    }


}
