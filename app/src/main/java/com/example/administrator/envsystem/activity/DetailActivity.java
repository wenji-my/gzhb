package com.example.administrator.envsystem.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.administrator.envsystem.R;
import com.example.administrator.envsystem.adapter.MyFragmentPagerAdapter;
import com.example.administrator.envsystem.bean.PdaTaskList;
import com.example.administrator.envsystem.bean.PdaTaskResult;

import java.util.HashMap;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private PdaTaskList pdaTaskList;
    private List<PdaTaskResult.PhotolistBean> photolistBeen;
    private HashMap<String,String> hashMap;

    public void setPhotoMap(HashMap<String,String> map){
        this.hashMap=map;
    }

    public HashMap<String,String> getPhotoMap(){
        return hashMap;
    }

    public void setList(List<PdaTaskResult.PhotolistBean> list){
        this.photolistBeen=list;
    }

    public List<PdaTaskResult.PhotolistBean> getList(){
        return photolistBeen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initData();
        initView();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("TAG","授权成功");
                }
                break;
            default:
                break;
        }
    }

    private void initView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                this);
        viewPager.setAdapter(adapter);

        //TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initData() {
        Intent intent = getIntent();
        pdaTaskList = (PdaTaskList) intent.getSerializableExtra("pdaTaskList");
    }

    public PdaTaskList getPdaTaskList(){
        return pdaTaskList;
    }

}
