package com.example.administrator.envsystem.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.administrator.envsystem.R;
import com.example.administrator.envsystem.db.SQLFuntion;
import com.example.administrator.envsystem.utils.TimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static com.example.administrator.envsystem.fragment.PhotoFragment.computeSampleSize;

public class CameraPicture extends AppCompatActivity {

    private ImageView imageView;
    private String lsh;
    private String type;
    private String[] photoPaths;
    private String jczbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        setContentView(R.layout.activity_camera_picture);
        imageView = (ImageView) findViewById(R.id.cp_img);
        lsh = getIntent().getStringExtra("lsh");
        type = getIntent().getStringExtra("type");
        jczbh = getIntent().getStringExtra("jczbh");
        Log.i("TAG", lsh);
        photoPaths = SQLFuntion.queryBySelection(lsh);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (photoPaths!=null){
        if (type.equals("01")) {
            File file1 = new File(createPath() + File.separator + lsh + "_ZMZP_01.jpg");
            if (file1.exists()) {
                imageView.setImageBitmap(GetImage(createPath() + File.separator + lsh + "_ZMZP_01.jpg"));
            }
        }
        if (type.equals("02")) {
            File file2 = new File(createPath() + File.separator + lsh + "_WBZP_02.jpg");
            if (file2.exists()) {
                imageView.setImageBitmap(GetImage(createPath() + File.separator + lsh + "_WBZP_02.jpg"));
            }
        }
        if (type.equals("03")) {
            File file1 = new File(createPath() + File.separator + lsh + "_PQGZP_03.jpg");
            if (file1.exists()) {
                imageView.setImageBitmap(GetImage(createPath() + File.separator + lsh + "_PQGZP_03.jpg"));
            }
        }
        if (type.equals("04")) {
            File file1 = new File(createPath() + File.separator + lsh + "_VINZP_04.jpg");
            if (file1.exists()) {
                imageView.setImageBitmap(GetImage(createPath() + File.separator + lsh + "_VINZP_04.jpg"));
            }
        }
//        }
    }

    public String createPath() {
        //preferences.getString("jczbh", "")
        String dir = jczbh + "/" + TimeUtils.getYear() + "/" + TimeUtils.getMonth() + "/" + TimeUtils.getDay();
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
            opts.inSampleSize = computeSampleSize(opts, -1, 1024 * 1024);
            opts.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, opts);
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
