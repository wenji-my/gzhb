package com.example.administrator.envsystem.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.envsystem.R;
import com.example.administrator.envsystem.activity.CameraActivity;
import com.example.administrator.envsystem.activity.CameraPicture;
import com.example.administrator.envsystem.activity.DetailActivity;
import com.example.administrator.envsystem.bean.PdaTaskList;
import com.example.administrator.envsystem.bean.PdaTaskResult;
import com.example.administrator.envsystem.conf.MessageWhat;
import com.example.administrator.envsystem.db.SQLFuntion;
import com.example.administrator.envsystem.utils.DialogTool;
import com.example.administrator.envsystem.utils.FtpUtil;
import com.example.administrator.envsystem.utils.TimeUtils;
import com.example.administrator.envsystem.utils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2017/4/14.
 */

public class PhotoFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private static int PHOTO_TYPE = -1;
    private TextView tv1, tv2, tv3, tv4, querition_title;
    private TextView sc1, sc2, sc3, sc4;
    private ImageView mImageView, mImageView2, mImageView3, mImageView4;
    private PdaTaskList pdaTaskList;
    private Button uploadBtn;
    private List<String> path;
    private boolean b;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    builder2 = new ProgressDialog(getActivity());
                    builder2.setMessage("正在上传中，请稍等。。。");
                    builder2.setCancelable(false);
                    builder2.show();
                    break;
                case 1:
                    builder2.dismiss();
                    break;
                case MessageWhat.MSG_SEARCH:
                    builder2 = new ProgressDialog(getActivity());
                    builder2.setMessage("正在查询中，请稍等。。。");
                    builder2.setCancelable(false);
                    builder2.show();
                    break;
            }
        }
    };
    private ProgressDialog builder2;
    private ImageButton backBtn;
    private SharedPreferences preferences;
    private DetailActivity mActivity;
    private String opt;
    private String ftpPath;
    private String[] photoPaths;
    private List<PdaTaskResult.PhotolistBean> photolistBeen;
    private List<HashMap<String,String>> photoPath;
    private HashMap<String,String> hashMap;
    PdaTaskResult.PhotolistBean bean1,bean2,bean3,bean4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, null);
        path = new ArrayList<>();
        photolistBeen = new ArrayList<>();
        hashMap=new HashMap<>();
        photoPath=new ArrayList<>();
        bean1=new PdaTaskResult.PhotolistBean();
        bean2=new PdaTaskResult.PhotolistBean();
        bean3=new PdaTaskResult.PhotolistBean();
        bean4=new PdaTaskResult.PhotolistBean();
        querition_title = (TextView) view.findViewById(R.id.querition_title);
        backBtn = (ImageButton) view.findViewById(R.id.photo_back);
        uploadBtn = (Button) view.findViewById(R.id.btn_upload);
        FrameLayout photo1 = (FrameLayout) view.findViewById(R.id.photo1);
        FrameLayout photo2 = (FrameLayout) view.findViewById(R.id.photo2);
        FrameLayout photo3 = (FrameLayout) view.findViewById(R.id.photo3);
        FrameLayout photo4 = (FrameLayout) view.findViewById(R.id.photo4);
//        GridView gridView= (GridView) view.findViewById(R.id.gridview);
        tv1 = (TextView) view.findViewById(R.id.tv1);
        tv2 = (TextView) view.findViewById(R.id.tv2);
        tv3 = (TextView) view.findViewById(R.id.tv3);
        tv4 = (TextView) view.findViewById(R.id.tv4);
        sc1 = (TextView) view.findViewById(R.id.sc1);
        sc2 = (TextView) view.findViewById(R.id.sc2);
        sc3 = (TextView) view.findViewById(R.id.sc3);
        sc4 = (TextView) view.findViewById(R.id.sc4);
        mImageView = (ImageView) view.findViewById(R.id.iv_photo1);
        mImageView2 = (ImageView) view.findViewById(R.id.iv_photo2);
        mImageView3 = (ImageView) view.findViewById(R.id.iv_photo3);
        mImageView4 = (ImageView) view.findViewById(R.id.iv_photo4);
        photo1.setOnClickListener(this);
        photo2.setOnClickListener(this);
        photo3.setOnClickListener(this);
        photo4.setOnClickListener(this);
        photo1.setOnLongClickListener(this);
        photo2.setOnLongClickListener(this);
        photo3.setOnLongClickListener(this);
        photo4.setOnLongClickListener(this);
        uploadBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (DetailActivity) activity;
        pdaTaskList = mActivity.getPdaTaskList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        querition_title.setText(pdaTaskList.getBusinessid());
        List<PdaTaskList.Phototype> phototype = pdaTaskList.getPhototype();
        tv1.setText(phototype.get(0).getTypename());
        tv2.setText(phototype.get(1).getTypename());
        tv3.setText(phototype.get(2).getTypename());
        tv4.setText(phototype.get(3).getTypename());
        preferences = getActivity().getSharedPreferences("setting", 0);
        ftpPath = preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/";
        photoPaths = SQLFuntion.queryBySelection(pdaTaskList.getBusinessid());
        if (photoPaths == null) {
            opt = "add";
        } else {
            opt = "update";
//            if (!TextUtils.isEmpty(photoPaths[1])){
//                mImageView.setImageBitmap(GetImage(photoPaths[1]));
//                hashMap.put("01",ftpPath + pdaTaskList.getBusinessid() + "_ZMZP_01.jpg");
//            }
//            if (!TextUtils.isEmpty(photoPaths[2])){
//                mImageView2.setImageBitmap(GetImage(photoPaths[2]));
//                hashMap.put("02",ftpPath + pdaTaskList.getBusinessid() + "_WBZP_02.jpg");
//            }
//            if (!TextUtils.isEmpty(photoPaths[3])){
//                mImageView3.setImageBitmap(GetImage(photoPaths[3]));
//                hashMap.put("03",ftpPath + pdaTaskList.getBusinessid() + "_PQGZP_03.jpg");
//            }
//            if (!TextUtils.isEmpty(photoPaths[4])){
//                mImageView4.setImageBitmap(GetImage(photoPaths[4]));
//                hashMap.put("04",ftpPath + pdaTaskList.getBusinessid() + "_VINZP_04.jpg");
//            }
//            mActivity.setPhotoMap(hashMap);
        }
        downloadPic();
    }

    private void downloadPic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    handler.sendEmptyMessage(MessageWhat.MSG_SEARCH);
                    List<String> files=new ArrayList<String>();
                    files.add(pdaTaskList.getBusinessid()+"_ZMZP_01.jpg");
                    files.add(pdaTaskList.getBusinessid()+"_WBZP_02.jpg");
                    files.add(pdaTaskList.getBusinessid()+"_PQGZP_03.jpg");
                    files.add(pdaTaskList.getBusinessid()+"_VINZP_04.jpg");
                    int code = FtpUtil.downloadFile("10.111.102.26", 21, "pda", "pda", files, null, preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/", createPath());
                    handler.sendEmptyMessage(1);
//                    if (code01 == 1 || code02 == 1 || code03 == 1 || code04 == 1) {
                    if (code == 1) {
                        //如果下载成功
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                File file1 = new File(createPath() + File.separator + pdaTaskList.getBusinessid() + "_ZMZP_01.jpg");
                                if (file1.exists()) {
                                    Bitmap bitmap = GetImage(createPath() + File.separator + pdaTaskList.getBusinessid() + "_ZMZP_01.jpg");
                                    mImageView.setImageBitmap(bitmap);
                                    sc1.setVisibility(View.VISIBLE);
                                    hashMap.put("01",ftpPath + pdaTaskList.getBusinessid() + "_ZMZP_01.jpg");
                                    mActivity.setPhotoMap(hashMap);
                                }
                                File file2 = new File(createPath() + File.separator + pdaTaskList.getBusinessid() + "_WBZP_02.jpg");
                                if (file2.exists()) {
                                    Bitmap bitmap = GetImage(createPath() + File.separator + pdaTaskList.getBusinessid() + "_WBZP_02.jpg");
                                    mImageView2.setImageBitmap(bitmap);
                                    sc2.setVisibility(View.VISIBLE);
                                    hashMap.put("02",ftpPath + pdaTaskList.getBusinessid() + "_WBZP_02.jpg");
                                    mActivity.setPhotoMap(hashMap);
                                }
                                File file3 = new File(createPath() + File.separator + pdaTaskList.getBusinessid() + "_PQGZP_03.jpg");
                                if (file3.exists()) {
                                    Bitmap bitmap = GetImage(createPath() + File.separator + pdaTaskList.getBusinessid() + "_PQGZP_03.jpg");
                                    mImageView3.setImageBitmap(bitmap);
                                    sc3.setVisibility(View.VISIBLE);
                                    hashMap.put("03",ftpPath + pdaTaskList.getBusinessid() + "_PQGZP_03.jpg");
                                    mActivity.setPhotoMap(hashMap);
                                }
                                File file4 = new File(createPath() + File.separator + pdaTaskList.getBusinessid() + "_VINZP_04.jpg");
                                if (file4.exists()) {
                                    Bitmap bitmap = GetImage(createPath() + File.separator + pdaTaskList.getBusinessid() + "_VINZP_04.jpg");
                                    mImageView4.setImageBitmap(bitmap);
                                    sc4.setVisibility(View.VISIBLE);
                                    hashMap.put("04",ftpPath + pdaTaskList.getBusinessid() + "_VINZP_04.jpg");
                                    mActivity.setPhotoMap(hashMap);
                                }
                            }
                        });
                    } else {
                        if (code == -1) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogTool.AlertDialogShow(getActivity(), "连接FTP服务器失败");
                                }
                            });
                        }
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(1);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogTool.AlertDialogShow(getActivity(),"连接FTP异常！"+e.toString());
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_back:
                getActivity().finish();
                break;
            case R.id.photo1://正面照片
                PHOTO_TYPE = 1;
                String[] s1 = SQLFuntion.queryBySelection(pdaTaskList.getBusinessid());
                String s = pdaTaskList.getBusinessid() + "_ZMZP_01";
                String imagePath = getImagePath(s);
                //第一次无法创建文件夹
                if (imagePath == null) return;
                path.add(imagePath);
                Intent intent1 = new Intent(getActivity(), CameraActivity.class);
                intent1.putExtra("businessid", pdaTaskList.getBusinessid());
                intent1.putExtra("phototype", imagePath);
                intent1.putExtra("photosql", "photo01");//数据库字段名
                intent1.putExtra("opt", s1 == null ? "add" : "update");
                startActivityForResult(intent1, 2);

                break;
            case R.id.photo2://尾部照片
                PHOTO_TYPE = 2;
                String[] ss2 = SQLFuntion.queryBySelection(pdaTaskList.getBusinessid());
                String s2 = pdaTaskList.getBusinessid() + "_WBZP_02";
                String wbImagePath = getImagePath(s2);
                if (wbImagePath == null) return;
                path.add(wbImagePath);
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                intent.putExtra("businessid", pdaTaskList.getBusinessid());
                intent.putExtra("phototype", wbImagePath);
                intent.putExtra("photosql", "photo02");
                intent.putExtra("opt", ss2 == null ? "add" : "update");
                startActivityForResult(intent, 2);
                break;
            case R.id.photo3://排气管装置
                PHOTO_TYPE = 3;
                String[] ss3 = SQLFuntion.queryBySelection(pdaTaskList.getBusinessid());
                String s3 = pdaTaskList.getBusinessid() + "_PQGZP_03";
                String pqgImagePath = getImagePath(s3);
                if (pqgImagePath == null) return;
                path.add(pqgImagePath);
                Intent intent3 = new Intent(getActivity(), CameraActivity.class);
                intent3.putExtra("businessid", pdaTaskList.getBusinessid());
                intent3.putExtra("phototype", pqgImagePath);
                intent3.putExtra("photosql", "photo03");
                intent3.putExtra("opt", ss3 == null ? "add" : "update");
                startActivityForResult(intent3, 2);
                break;
            case R.id.photo4://VIN拍照
                PHOTO_TYPE = 4;
                String[] ss4 = SQLFuntion.queryBySelection(pdaTaskList.getBusinessid());
                String s4 = pdaTaskList.getBusinessid() + "_VINZP_04";
                String vinImagePath = getImagePath(s4);
                if (vinImagePath == null) return;
                path.add(vinImagePath);
                Intent intent4 = new Intent(getActivity(), CameraActivity.class);
                intent4.putExtra("businessid", pdaTaskList.getBusinessid());
                intent4.putExtra("phototype", vinImagePath);
                intent4.putExtra("photosql", "photo04");
                intent4.putExtra("opt", ss4 == null ? "add" : "update");
                startActivityForResult(intent4, 2);
                break;
            case R.id.btn_upload://上传照片
                mActivity.setList(photolistBeen);
                handler.sendEmptyMessage(0);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < path.size(); i++) {
                            String s1 = path.get(i);
                            File file = new File(s1);
                            if (file.exists()) {
                                FileInputStream in = null;
                                try {
                                    in = new FileInputStream(file);
                                    //上传FTP
                                    b = FtpUtil.uploadFile("10.111.102.26", 21, "pda", "pda", preferences.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/", file.getName(), in);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (in != null) {
                                        try {
                                            in.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (b) {
                                    handler.sendEmptyMessage(1);
                                    DialogTool.AlertDialogShow(getActivity(), "上传成功！");
                                    ToastUtils.showToastLong(getActivity(), "上传成功");
                                } else {
                                    handler.sendEmptyMessage(1);
                                    DialogTool.AlertDialogShow(getActivity(), "上传失败！");
                                }
                            }
                        });
                    }
                }).start();
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (data != null) {
                if (resultCode == 3) {
                    String pathshow = data.getStringExtra("pathshow");
                    Bitmap bitmap = GetImage(pathshow);
                    switch (PHOTO_TYPE) {
                        case 1:
//                            preferenceEditor("01", ftpPath + pdaTaskList.getBusinessid() + "_01.jpg");
                            mImageView.setImageBitmap(bitmap);
                            sc1.setVisibility(View.VISIBLE);
                            hashMap.put("01",ftpPath + pdaTaskList.getBusinessid() + "_ZMZP_01.jpg");
                            mActivity.setPhotoMap(hashMap);
                            break;
                        case 2:
//                            preferenceEditor("02", ftpPath + pdaTaskList.getBusinessid() + "_02.jpg");
                            mImageView2.setImageBitmap(bitmap);
                            sc2.setVisibility(View.VISIBLE);
                            hashMap.put("02",ftpPath + pdaTaskList.getBusinessid() + "_WBZP_02.jpg");
                            mActivity.setPhotoMap(hashMap);
                            break;
                        case 3:
//                            preferenceEditor("03", ftpPath + pdaTaskList.getBusinessid() + "_03.jpg");
                            mImageView3.setImageBitmap(bitmap);
                            sc3.setVisibility(View.VISIBLE);
                            hashMap.put("03",ftpPath + pdaTaskList.getBusinessid() + "_PQGZP_03.jpg");
                            mActivity.setPhotoMap(hashMap);
                            break;
                        case 4:
//                            preferenceEditor("04", ftpPath + pdaTaskList.getBusinessid() + "_04.jpg");
                            mImageView4.setImageBitmap(bitmap);
                            sc4.setVisibility(View.VISIBLE);
                            hashMap.put("04",ftpPath + pdaTaskList.getBusinessid() + "_VINZP_04.jpg");
                            mActivity.setPhotoMap(hashMap);
                            break;
                    }
                }
                if (resultCode==4){
                    DialogTool.AlertDialogShow(getActivity(),"上传失败！");
                }
            }
        }
    }
    public String getImagePath(String name) {
        String dir = preferences.getString("jczbh", "") + "/" + TimeUtils.getYear() + "/" + TimeUtils.getMonth() + "/" + TimeUtils.getDay();
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), dir);
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String path = mediaStorageDir.getPath() + File.separator + name + ".jpg";
        return path;
    }

    public String createPath() {
        //preferences.getString("jczbh", "")
        String dir = preferences.getString("jczbh", "") + "/" + TimeUtils.getYear() + "/" + TimeUtils.getMonth() + "/" + TimeUtils.getDay();
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), dir);
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String path = mediaStorageDir.getPath();
        return path;
    }

    public Bitmap GetImage(String currentImagePath) {
        try {
            InputStream input = new FileInputStream(currentImagePath);
            byte[] imgData = new byte[input.available()];
            input.read(imgData);
            input.close();
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // BitmapFactory.decodeFile(currentImagePath, opts);
            BitmapFactory.decodeByteArray(imgData, 0, imgData.length, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, 256 * 256);
            opts.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, opts);
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double h = options.outWidth;
        double w = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.photo1:
                showPic(pdaTaskList.getBusinessid(), "01");
                break;
            case R.id.photo2:
                showPic(pdaTaskList.getBusinessid(), "02");
                break;
            case R.id.photo3:
                showPic(pdaTaskList.getBusinessid(), "03");
                break;
            case R.id.photo4:
                showPic(pdaTaskList.getBusinessid(), "04");
                break;
        }
        return false;
    }

    private void showPic(String lsh, String type) {
        Intent intent = new Intent(getActivity(), CameraPicture.class);
        intent.putExtra("lsh", lsh);
        intent.putExtra("type", type);
        intent.putExtra("jczbh",preferences.getString("jczbh",""));
        startActivity(intent);
    }

}
