package com.example.administrator.envsystem.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.envsystem.R;
import com.example.administrator.envsystem.ui.CameraGrid;
import com.example.administrator.envsystem.ui.GenericProgressDialog;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class CameraActivity extends AppCompatActivity implements View.OnTouchListener {

    public static final String CAMERA_PATH_VALUE1 = "PHOTO_PATH";
    public static final String CAMERA_PATH_VALUE2 = "PATH";
    public static final String CAMERA_TYPE = "CAMERA_TYPE";
    public static final String CAMERA_RETURN_PATH = "return_path";

    private int PHOTO_SIZE_W = 2000;
    private int PHOTO_SIZE_H = 2000;
    public static final int CAMERA_TYPE_1 = 1;
    public static final int CAMERA_TYPE_2 = 2;
    private final int PROCESS = 1;
    private CameraPreview preview;
    private Camera camera;
    private Context mContext;
    private View focusIndex;
    private ImageView flashBtn;
    private int mCurrentCameraId = 0; // 1是前置 0是后置
    private SurfaceView mSurfaceView;
    private CameraGrid mCameraGrid;

    private int type = 1;   //引用的矩形框

    private Button mBtnSearch;
    private Button mBtnTakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//拍照过程屏幕一直处于高亮
        setContentView(R.layout.activity_camera);
//        type = getIntent().getIntExtra(CAMERA_TYPE, CAMERA_TYPE_2);
//        Log.i("TAG",type+"");
        initView();
        initData();
    }

    private void initData() {
        preview = new CameraPreview(this, mSurfaceView);
        preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);
        mSurfaceView.setOnTouchListener(this);
//        mCameraGrid.setType(type);
    }

    private void initView() {
        focusIndex = (View) findViewById(R.id.focus_index);
        flashBtn = (ImageView) findViewById(R.id.flash_view);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
//        mCameraGrid = (CameraGrid) findViewById(R.id.camera_grid);
        mBtnSearch = (Button) findViewById(R.id.search);
        mBtnTakePhoto = (Button) findViewById(R.id.takephoto);
    }

    private Handler handler = new Handler();

    private void takePhoto() {
        try {

            camera.takePicture(shutterCallback, rawCallback, jpegCallback);

        } catch (Throwable t) {
            t.printStackTrace();
            Toast.makeText(getApplication(), "拍照失败，请重试！", Toast.LENGTH_LONG)
                    .show();
            try {
                camera.startPreview();
            } catch (Throwable e) {

            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                mCurrentCameraId = 0;
                camera = Camera.open(mCurrentCameraId);
                camera.startPreview();
                preview.setCamera(camera);
                preview.reAutoFocus();
            } catch (RuntimeException ex) {
                Toast.makeText(mContext, "未发现相机", Toast.LENGTH_LONG).show();
            }
        }

    }
    @Override
    protected void onPause() {
        if (camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
            preview.setNull();
        }
        super.onPause();

    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };


    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };


    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            new SaveImageTask(data).execute();
            resetCam();
        }
    };

    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }

    private static String getCameraPath() {
        Calendar calendar = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("IMG");
        sb.append(calendar.get(Calendar.YEAR));
        int month = calendar.get(Calendar.MONTH) + 1; // 0~11
        sb.append(month < 10 ? "0" + month : month);
        int day = calendar.get(Calendar.DATE);
        sb.append(day < 10 ? "0" + day : day);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        sb.append(hour < 10 ? "0" + hour : hour);
        int minute = calendar.get(Calendar.MINUTE);
        sb.append(minute < 10 ? "0" + minute : minute);
        int second = calendar.get(Calendar.SECOND);
        sb.append(second < 10 ? "0" + second : second);
        if (!new File(sb.toString() + ".jpg").exists()) {
            return sb.toString() + ".jpg";
        }

        StringBuilder tmpSb = new StringBuilder(sb);
        int indexStart = sb.length();
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            tmpSb.append('(');
            tmpSb.append(i);
            tmpSb.append(')');
            tmpSb.append(".jpg");
            if (!new File(tmpSb.toString()).exists()) {
                break;
            }

            tmpSb.delete(indexStart, tmpSb.length());
        }

        return tmpSb.toString();
    }

    public void onClick(View view) {
        switch (view.getId()) {

        /*case R.id.camera_back:
            setResult(0);
            finish();
            break;*/

            case R.id.camera_flip_view:
//                switchCamera();
                break;

            case R.id.flash_view:
//                turnLight(camera);
                break;

            case R.id.action_button:
                takePhoto();
                break;

            case R.id.search:   //处理选中状态
                mBtnSearch.setSelected(true);
                mBtnTakePhoto.setSelected(false);
                break;

            case R.id.takephoto:    //处理选中状态
                mBtnTakePhoto.setSelected(true);
                mBtnSearch.setSelected(false);
                break;
        }
    }

    //处理拍摄的照片
    private class SaveImageTask extends AsyncTask<Void, Void, String> {
        private byte[] data;

        SaveImageTask(byte[] data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(Void... params) {
            // Write to SD Card
            String path = "";
            try {

                showProgressDialog("处理中");
                path = saveToSDCard(data);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return path;
        }


        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);

            if (!TextUtils.isEmpty(path)) {

                Log.d("DemoLog", "path=" + path);

                dismissProgressDialog();
                Intent intent = new Intent();
                intent.setClass(CameraActivity.this, PhotoProcessActivity.class);
                intent.putExtra(CAMERA_PATH_VALUE1, path);
                startActivityForResult(intent, PROCESS);
            } else {
                Toast.makeText(getApplication(), "拍照失败，请稍后重试！",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 将拍下来的照片存放在SD卡中
     */
    public String saveToSDCard(byte[] data) throws IOException {
        Bitmap croppedImage;
        // 获得图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        // PHOTO_SIZE = options.outHeight > options.outWidth ? options.outWidth
        // : options.outHeight;
        PHOTO_SIZE_W = options.outWidth;
        PHOTO_SIZE_H = options.outHeight;
        options.inJustDecodeBounds = false;
        Rect r = new Rect(0, 0, PHOTO_SIZE_W, PHOTO_SIZE_H);
        try {
            croppedImage = decodeRegionCrop(data, r);
        } catch (Exception e) {
            return null;
        }
        String imagePath = "";
        try {
            imagePath = saveToFile(croppedImage);
        } catch (Exception e) {

        }
        croppedImage.recycle();
        return imagePath;
    }

    private Bitmap decodeRegionCrop(byte[] data, Rect rect) {
        InputStream is = null;
        System.gc();
        Bitmap croppedImage = null;
        try {
            is = new ByteArrayInputStream(data);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            try {
                croppedImage = decoder.decodeRegion(rect,
                        new BitmapFactory.Options());
            } catch (IllegalArgumentException e) {
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
        Matrix m = new Matrix();
        m.setRotate(90, PHOTO_SIZE_W / 2, PHOTO_SIZE_H / 2);
        if (mCurrentCameraId == 1) {
            m.postScale(1, -1);
        }
        Bitmap rotatedImage = Bitmap.createBitmap(croppedImage, 0, 0,
                PHOTO_SIZE_W, PHOTO_SIZE_H, m, true);
        if (rotatedImage != croppedImage)
            croppedImage.recycle();
        return rotatedImage;
    }

    // 保存图片文件
    public static String saveToFile(Bitmap croppedImage)
            throws FileNotFoundException, IOException {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/DCIM/Camera/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = getCameraPath();
        File outFile = new File(dir, fileName);
        FileOutputStream outputStream = new FileOutputStream(outFile); // 文件输出流
        croppedImage.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        outputStream.flush();
        outputStream.close();
        return outFile.getAbsolutePath();
    }

    private AlertDialog mAlertDialog;

    private void dismissProgressDialog() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAlertDialog != null && mAlertDialog.isShowing()
                        && !CameraActivity.this.isFinishing()) {
                    mAlertDialog.dismiss();
                    mAlertDialog = null;
                }
            }
        });
    }

    private void showProgressDialog(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAlertDialog == null) {
                    mAlertDialog = new GenericProgressDialog(
                            CameraActivity.this);
                }
                mAlertDialog.setMessage(msg);
                ((GenericProgressDialog) mAlertDialog)
                        .setProgressVisiable(true);
                mAlertDialog.setCancelable(false);
                mAlertDialog.setOnCancelListener(null);
                mAlertDialog.show();
                mAlertDialog.setCanceledOnTouchOutside(false);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                preview.pointFocus(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(
                focusIndex.getLayoutParams());
        layout.setMargins((int) event.getX() - 60, (int) event.getY() - 60, 0,0);

        focusIndex.setLayoutParams(layout);
        focusIndex.setVisibility(View.VISIBLE);

        ScaleAnimation sa = new ScaleAnimation(3f, 1f, 3f, 1f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(800);
        focusIndex.startAnimation(sa);
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                focusIndex.setVisibility(View.INVISIBLE);
            }
        }, 800);
        return false;
    }
}
