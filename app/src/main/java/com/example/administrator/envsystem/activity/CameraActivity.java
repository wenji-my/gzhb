package com.example.administrator.envsystem.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.administrator.envsystem.R;
import com.example.administrator.envsystem.db.SQLFuntion;
import com.example.administrator.envsystem.utils.DialogTool;
import com.example.administrator.envsystem.utils.FtpUtil;
import com.example.administrator.envsystem.utils.PhotoTool;
import com.example.administrator.envsystem.utils.TimeTool;
import com.example.administrator.envsystem.utils.TimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.administrator.envsystem.conf.MessageWhat.MSG_DISMISS;
import static com.example.administrator.envsystem.conf.MessageWhat.MSG_SHOW;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, SurfaceHolder.Callback, Camera.AutoFocusCallback {

    private LinearLayout pros_lin;
    private ImageView takePhotoImageView;
    private ImageView flashImageView;
    private ImageView cameraSwitchImageView;
    private ImageView focusImageView;
    private ImageView cancelPhotoImageView;
    private ImageView savePhotoImageView;
    private SeekBar seekbar_zoom;
    private ImageView increase_focal_length;
    private ImageView minus_focal_length;
    private SurfaceView mySurfaceView;
    private SurfaceHolder holder;
    private Camera myCamera;
    private int widthR, heightR;
    private String phototype,businessid,photosql,opt;
    private boolean isOpenFlash=false;
    private boolean isLongClicked=false;//长按标识
    private String jgmc="";
    private int cs=0;
    private Handler myHandler=new MyHandler(this);
    private boolean b=false;
    private SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        setContentView(R.layout.activity_camera2);

        preference=getSharedPreferences("setting", 0);
        phototype = getIntent().getStringExtra("phototype");
        photosql = getIntent().getStringExtra("photosql");
        businessid = getIntent().getStringExtra("businessid");
        opt = getIntent().getStringExtra("opt");
        SharedPreferences preferences = getSharedPreferences("set", 0);
        jgmc = preferences.getString("jgmc", "");
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        widthR = display.getWidth();
        heightR = display.getHeight();
        pros_lin = (LinearLayout) this.findViewById(R.id.pros_lin);
        pros_lin.setVisibility(View.VISIBLE);
        takePhotoImageView = (ImageView) this.findViewById(R.id.takePhotoImageView); //拍照按钮
        takePhotoImageView.setOnClickListener(this);
        flashImageView = (ImageView) this.findViewById(R.id.flashImageView);
        cameraSwitchImageView = (ImageView) this.findViewById(R.id.cameraSwitchImageView);
        focusImageView = (ImageView) this.findViewById(R.id.focusImageView);
        focusImageView.setVisibility(View.INVISIBLE);
        flashImageView.setOnClickListener(this);

        cancelPhotoImageView = (ImageView) this.findViewById(R.id.cancelPhotoImageView);// 取消
        savePhotoImageView = (ImageView) this.findViewById(R.id.savePhotoImageView); // 保存
        cancelPhotoImageView.setOnClickListener(this);
        savePhotoImageView.setOnClickListener(this);
        savePhotoImageView.setEnabled(false);
        seekbar_zoom = (SeekBar) this.findViewById(R.id.seekbar_zoom);// 进度条
        seekbar_zoom.setOnSeekBarChangeListener(seekBarChange);
        increase_focal_length = (ImageView) this.findViewById(R.id.increase_focal_length);// 放大
        minus_focal_length = (ImageView) this.findViewById(R.id.minus_focal_length);// 缩小
        increase_focal_length.setOnClickListener(this);
        minus_focal_length.setOnClickListener(this);
        // 获得控件
        mySurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        mySurfaceView.setOnClickListener(this);
        mySurfaceView.setOnLongClickListener(this);
        // 获得句柄
        holder = mySurfaceView.getHolder();
        // 设置类型
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 添加回调
        holder.addCallback(this);
    }


    SeekBar.OnSeekBarChangeListener seekBarChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Camera.Parameters par = myCamera.getParameters();
            par.setZoom(progress);
            myCamera.setParameters(par);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private File pictureFile;
    // 创建jpeg图片回调数据对象
    Camera.PictureCallback jpeg = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            pictureFile = new File(phototype);
//            Log.i("TAG",pictureFile.getAbsolutePath());
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                Bitmap photoBitmap = Bytes2Bimap(data);
                photoBitmap = PhotoTool.getPhotoByPixel(photoBitmap, 1280);
                // 加水印
                photoBitmap = createBitmap(photoBitmap, jgmc, "张三");
                // 图片压缩
                data = PhotoTool.getCompressPhotoByPixel(photoBitmap);
                fos.write(data, 0, data.length);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            System.gc();
            savePhotoImageView.setVisibility(View.VISIBLE);
            savePhotoImageView.setEnabled(true);
            flashImageView.setVisibility(View.INVISIBLE);
            // cameraSwitchImageView.setVisibility(View.INVISIBLE);
            takePhotoImageView.setVisibility(View.INVISIBLE);
            cancelPhotoImageView.setVisibility(View.VISIBLE);
            pros_lin.setVisibility(View.GONE);
        }
    };

    // 由Byte转为Bimap
    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    private Bitmap createBitmap(Bitmap photo, String strname, String newjyyxm) {
        String str = TimeTool.getTiem();
//        String sjimei = "imei:";
//        String sjjyyxm = "检验员姓名：" + newjyyxm;
        int width = photo.getWidth(), hight = photo.getHeight();
        Bitmap icon = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888); // 建立一个空的BItMap
        Canvas canvas = new Canvas(icon);// 初始化画布绘制的图像到icon上

        Paint photoPaint = new Paint(); // 建立画笔
        photoPaint.setDither(true); // 获取跟清晰的图像采样
        photoPaint.setFilterBitmap(true);// 过滤一些

        Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());// 创建一个指定的新矩形的坐标
        Rect dst = new Rect(0, 0, width, hight);// 创建一个指定的新矩形的坐标
        canvas.drawBitmap(photo, src, dst, photoPaint);// 将photo 缩放或则扩大到
        // dst使用的填充区photoPaint

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
        //textPaint.setTextSize(45.0f);// 字体大小
        textPaint.setTextSize(30.0f);// 字体大小
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
        textPaint.setColor(Color.RED);// 采用的颜色

        //textPaint.setAlpha(60);
        //canvas.drawText(str, canvas.getWidth() - gettextwidth(str, 45.0f) - 50, 60, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
        //canvas.drawText(strname, canvas.getWidth() - gettextwidth(strname, 45.0f) - 50, 130, textPaint);
        //canvas.drawText(sjimei, canvas.getWidth() - gettextwidth(sjimei, 45.0f) - 50, 200, textPaint);
        //canvas.drawText(sjjyyxm, canvas.getWidth() - gettextwidth(sjjyyxm, 45.0f) - 50, 270, textPaint);

        canvas.drawText(str, 50, 30, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
        canvas.drawText(strname, 50, 70, textPaint);
//        canvas.drawText(sjimei, 50, 110, textPaint);
        //canvas.drawText(str, 50, 60, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
        //canvas.drawText(strname, 50, 130, textPaint);
        //canvas.drawText(sjimei, 50, 200, textPaint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return icon;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (savePhotoImageView.getVisibility()==View.VISIBLE){
            return super.onKeyDown(keyCode,event);
        }
        if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
//            int i=0;
//            i--;
//            Log.i("TAG",i+"");
            myCamera.takePicture(null,null,jpeg);
            return true;
        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
//            int i=0;
//            i++;
//            Log.i("TAG",i+"");
            myCamera.takePicture(null,null,jpeg);
            return true;
        }else {
            return super.onKeyDown(keyCode,event);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhotoImageView://拍照
                myCamera.takePicture(null, null, jpeg);
                break;
            case R.id.cancelPhotoImageView://取消
                savePhotoImageView.setEnabled(false);
                String fileFullPath = pictureFile.getAbsolutePath();
                File file = new File(fileFullPath);
                if (file.exists() && file.canWrite()) {
                    file.delete();
                }
                myCamera.startPreview();// 开启预览
                takePhotoImageView.setEnabled(true);
                flashImageView.setVisibility(View.VISIBLE);
                // cameraSwitchImageView.setVisibility(View.VISIBLE);
                takePhotoImageView.setVisibility(View.VISIBLE);
                cancelPhotoImageView.setVisibility(View.INVISIBLE);
                savePhotoImageView.setVisibility(View.INVISIBLE);
                break;
            case R.id.savePhotoImageView://保存
                if (opt.equals("add")){
                    HashMap<String,String> map=new HashMap<>();
                    map.put("jylsh",businessid);
                    map.put(photosql,phototype);
                    boolean b = SQLFuntion.insertData(map);
                    Log.i("TAG","保存："+b);
                }else if (opt.equals("update")){
                    //更新数据库
                    HashMap<String,String> map=new HashMap<>();
                    map.put(photosql,phototype);
                    boolean b = SQLFuntion.updateData(businessid, map);
                    Log.i("TAG","更新："+b);
                }
                String photoPath = pictureFile.getAbsolutePath();
                File filetwo = new File(photoPath);
                if (filetwo.exists()) {
                    uploadPhotos(photoPath);
                }
                break;
            case R.id.surfaceView1://点击对焦
                try {
                    if (cancelPhotoImageView.getVisibility() == View.INVISIBLE) {
                        isLongClicked = false;
                        myCamera.autoFocus(this);// 自动对焦
                    }
                } catch (Exception e) {

                }
                break;
            case R.id.flashImageView://闪光灯
                try {
                    Camera.Parameters params = myCamera.getParameters();
                    if (isOpenFlash) {
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        flashImageView.setImageResource(R.drawable.flash_off);
                        isOpenFlash = false;
                    } else {
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);// FLASH_MODE_TORCH
                        flashImageView.setImageResource(R.drawable.flash_on);
                        isOpenFlash = true;
                    }
                    myCamera.setParameters(params);
                } catch (Exception e) {

                }
                break;
            case R.id.increase_focal_length://放大
                if (isSupportZoom()) {
                    setZoom(true);
                }
                break;
            case R.id.minus_focal_length://缩小
                if (isSupportZoom()) {
                    setZoom(false);
                }
                break;
        }
    }

    /**
     * 上传照片
     */
    private void uploadPhotos(final String photoPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(MSG_SHOW);
                File file = new File(photoPath);
                FileInputStream in = null;
                try {
                    in = new FileInputStream(file);
                    //上传FTP
                    b = FtpUtil.uploadFile("10.111.102.26", 21, "pda", "pda", preference.getString("jczbh", "") + "/PIC/" + TimeUtils.getYear() + TimeUtils.getMonth() + "/" + TimeUtils.getDay() + "/", file.getName(), in);
                } catch (IOException e) {
                    e.printStackTrace();
                    myHandler.sendEmptyMessage(0x00015);
//                    ToastUtils.showToastLong(CameraActivity.this, "FTP连接异常!!");
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //离线
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                b=true;
                if (b) {
                    //上传成功，返回显示图片
                    myHandler.sendEmptyMessage(MSG_DISMISS);
                    setRes(3, photoPath);
                } else {
                    //上传失败，返回提示失败，不显示图片
                    myHandler.sendEmptyMessage(MSG_DISMISS);
                    setRes(4, photoPath);
                }
            }
        }).start();
    }

    /**
     * 回调
     *
     * @param resCode
     * @param msg
     */
    public void setRes(int resCode, String msg) {
        Intent intent = new Intent();
        intent.putExtra("pathshow", msg);
        setResult(resCode, intent);
        pictureFile = null;
        finish();
    }

    public void setZoom(boolean isAdd) {
        try {
            Camera.Parameters mParameters = myCamera.getParameters();
            int Max = mParameters.getMaxZoom();
            Log.i("AAA", "Max:" + Max);
            int zoomValue = mParameters.getZoom();
            Log.i("AAA", "getZoom:" + zoomValue);
            if (isAdd) {
                // zoomValue = zoomValue + 1;
                zoomValue = zoomValue < Max ? zoomValue + 1 : zoomValue;
                Log.i("AAA", "ADD:" + zoomValue);
            } else {
                // zoomValue = zoomValue - 1;
                zoomValue = zoomValue > 0 ? zoomValue - 1 : zoomValue;
                Log.i("AAA", "MIN:" + zoomValue);
            }
            mParameters.setZoom(zoomValue);
            myCamera.setParameters(mParameters);
            seekbar_zoom.setProgress(zoomValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSupportZoom() {
        return myCamera.getParameters().isSmoothZoomSupported();
    }

    @Override
    public boolean onLongClick(View v) {
        try {
            isLongClicked = true;
            myCamera.autoFocus(this);// 自动对焦

        } catch (Exception e) {
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 开启相机
        if (myCamera == null) {
            myCamera = Camera.open();
            seekbar_zoom.setMax(myCamera.getParameters().getMaxZoom());
            try {
                myCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        initCamera();
        myCamera.startPreview();
        myCamera.autoFocus(this);// 自动对焦
    }

    private void initCamera() {
        Camera.Parameters params = myCamera.getParameters();
        // myCamera.setDisplayOrientation(90);
        params.setPictureFormat(PixelFormat.JPEG);
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size optimalPreviewSize = getOptimalPreviewSize(sizes, widthR, heightR);
        params.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        List<Camera.Size> psizes = params.getSupportedPictureSizes();
        Camera.Size optimalPictureSize = GetOptimalPictureSize(psizes);
//        Log.i("TAG", "optimalPictureSize:" + optimalPictureSize.height + "    " + optimalPictureSize.width + "");
        params.setPictureSize(optimalPictureSize.width, optimalPictureSize.height);
        // params.setPictureSize(960, 720);
        params.setJpegQuality(50);
        myCamera.setParameters(params);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
// 关闭预览并释放资源
        myCamera.stopPreview();
        myCamera.release();
        myCamera = null;
    }

    private Camera.Size GetOptimalPictureSize(List<Camera.Size> sizes) {
        List<Camera.Size> tempSizes = new ArrayList<Camera.Size>();
        for (int i = 0; i < sizes.size(); i++) {
            if (800 < sizes.get(i).width && sizes.get(i).width < 3200)
                tempSizes.add(sizes.get(i));
        }
        Camera.Size curSize = tempSizes.get(0);
        for (int i = 0; i < tempSizes.size(); i++) {
            if ((float) ((float) curSize.width / (float) curSize.height) <= (float) ((float) tempSizes.get(i).width / (float) tempSizes
                    .get(i).height)) {
                curSize = tempSizes.get(i);
            }
        }
        return curSize;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        try {
            if (success) {
                if (isLongClicked)
                    myCamera.takePicture(null, null, jpeg);
            } else
                takePhotoImageView.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "autoFocus:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        isLongClicked = false;
    }

    private static class MyHandler extends Handler {
        private WeakReference<CameraActivity> mActivity;

        private MyHandler(CameraActivity activity) {
            mActivity = new WeakReference<CameraActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            if (msg.what == MSG_SHOW) {
                DialogTool.showLoading(mActivity.get(), "正在上传。。。");
            }
            if (msg.what == MSG_DISMISS) {
                DialogTool.disLoading();
            }
            if (msg.what == 0x0015) {
                DialogTool.disLoading();
                DialogTool.AlertDialogShow(mActivity.get(),"FTP连接异常,请检查网络！");
            }
        }
    }
}
