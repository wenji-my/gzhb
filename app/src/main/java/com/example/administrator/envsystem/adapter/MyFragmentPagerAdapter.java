package com.example.administrator.envsystem.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.administrator.envsystem.fragment.CheckFragment;
import com.example.administrator.envsystem.fragment.PhotoFragment;

/**
 * Created by Administrator on 2017/4/14.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"检验项", "拍摄项"};
    private Fragment[] fragments=new Fragment[]{new CheckFragment(),new PhotoFragment()};

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
