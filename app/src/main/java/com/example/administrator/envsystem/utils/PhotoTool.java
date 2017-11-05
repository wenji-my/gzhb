package com.example.administrator.envsystem.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2017/4/19.
 */

public class PhotoTool {

    public static byte[] getCompressPhotoByPixel(Bitmap photoBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 将压缩后的图片保存到baos中
        int quality = 100;
        while (baos.toByteArray().length > 100 * 1024) {
            baos.reset();
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 3;
        }
        byte[] reducedPhoto = baos.toByteArray();
        return reducedPhoto;
    }

    public static Bitmap getPhotoByPixel(Bitmap photoBitmap, double newWidth) {
        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (photoBitmap.getWidth() <= newWidth)
            return photoBitmap;
        float width = photoBitmap.getWidth();
        float height = photoBitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = 0;
        float scaleHeight = 0;
        double newHeight = newWidth * height / width;
        if (width > height) {
            scaleWidth = (float) (newWidth / width);
            scaleHeight = (float) (newHeight / height);
        } else {
            scaleWidth = (float) (newWidth / height);
            scaleHeight = (float) (newHeight / width);
        }
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(photoBitmap, 0, 0, (int) width, (int) height, matrix, true);
        // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // byte[] reducedPhoto = baos.toByteArray();
        return bitmap;
    }
}
